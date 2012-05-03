/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.io;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.stage.StageListener;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

public class ProjectScreenshotLoader {

	private class ScreenshotData {
		public String projectName;
		public ImageView imageView;

		public ScreenshotData(String projectName, ImageView imageView) {
			this.projectName = projectName;
			this.imageView = imageView;
		}
	}

	private static final int POOL_SIZE = 5;
	private static final int CACHE_MAX_SIZE = 25;
	private static final float LOAD_FACTOR = .75f;
	private static final int INITIAL_VALUE = 13; // (N / LOAD_FACTOR) + 1

	private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService;
	private Context context;

	private Map<String, Bitmap> imageCache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(
			INITIAL_VALUE, LOAD_FACTOR, true) {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
			return size() > CACHE_MAX_SIZE;
		}
	});

	public ProjectScreenshotLoader(Context context) {
		executorService = Executors.newFixedThreadPool(POOL_SIZE);
		this.context = context;
	}

	public void loadAndShowScreenshot(String projectName, ImageView imageView) {
		imageViews.put(imageView, projectName);
		Bitmap bitmap = imageCache.get(projectName);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			//set a dummy or null in the meantime
			imageView.setImageBitmap(null);
			//queue the loading and showing process
			ScreenshotData screenshotData = new ScreenshotData(projectName, imageView);
			executorService.submit(new ScreenshotLoader(screenshotData));
		}
	}

	class ScreenshotLoader implements Runnable {
		ScreenshotData projectScreenshotData;

		ScreenshotLoader(ScreenshotData screenshotData) {
			this.projectScreenshotData = screenshotData;
		}

		public void run() {
			if (imageViewReused(projectScreenshotData)) {
				return;
			}
			Activity uiActivity = (Activity) projectScreenshotData.imageView.getContext();

			String pathOfScreenshot = Utils.buildPath(Utils.buildProjectPath(projectScreenshotData.projectName),
					StageListener.SCREENSHOT_FILE_NAME);
			File projectImageFile = new File(pathOfScreenshot);
			final Bitmap projectImage;
			if (!projectImageFile.exists() || ImageEditing.getImageDimensions(pathOfScreenshot)[0] < 0) {
				projectImage = null;
			} else {
				int width = context.getResources().getDimensionPixelSize(R.dimen.project_thumbnail_width);
				int height = context.getResources().getDimensionPixelSize(R.dimen.project_thumbnail_height);
				projectImage = ImageEditing.getScaledBitmapFromPath(pathOfScreenshot, width, height, true);
			}

			imageCache.put(projectScreenshotData.projectName, projectImage);
			if (imageViewReused(projectScreenshotData)) {
				return;
			}

			uiActivity.runOnUiThread(new Runnable() {
				public void run() {
					if (imageViewReused(projectScreenshotData)) {
						return;
					}
					if (projectImage != null) {
						projectScreenshotData.imageView.setImageBitmap(projectImage);
					} else {
						projectScreenshotData.imageView.setImageBitmap(null);
					}
				}
			});
		}
	}

	boolean imageViewReused(ScreenshotData projectScreenshotData) {
		String tag = imageViews.get(projectScreenshotData.imageView);
		if (tag == null || !tag.equals(projectScreenshotData.projectName)) {
			return true;
		}
		return false;
	}
}

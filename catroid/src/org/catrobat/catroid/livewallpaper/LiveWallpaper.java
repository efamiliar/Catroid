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

package org.catrobat.catroid.livewallpaper;

import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.utils.Utils;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.Display;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class LiveWallpaper extends WallpaperService {

	@Override
	public Engine onCreateEngine() {

		//TODO: this code goes into the dummy app after the stuff is implemented
		ProjectManager projectManager = ProjectManager.getInstance();

		projectManager.setProject(null);
		Utils.loadProjectIfNeeded(getApplicationContext(), null);

		Project project = projectManager.getCurrentProject();
		WallpaperHelper.getInstance().setProject(project);

		ProjectInformation.projectName = project.getName();
		ProjectInformation.projectDescription = project.getDescription();

		return new CatWallEngine();

	}

	@TargetApi(9)
	public class CatWallEngine extends Engine {

		private boolean mVisible = false;

		private Display display;

		private Paint paint;
		private List<Sprite> sprites;

		private WallpaperHelper wallpaperHelper = WallpaperHelper.getInstance();

		private final Handler mHandler = new Handler();

		private final Runnable mUpdateDisplay = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);

			display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				wallpaperHelper.setLiveWallpaper(true);
				wallpaperHelper.setDrawingThread(mUpdateDisplay);
				wallpaperHelper.setDrawingThreadHandler(mHandler);

				sprites = wallpaperHelper.getProject().getSpriteList();

				for (Sprite sprite : sprites) {
					sprite.resetScripts();
					sprite.startStartScripts();
					draw();
				}

			} else {
				wallpaperHelper.setLiveWallpaper(false);
				mHandler.removeCallbacks(mUpdateDisplay);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {

			int rotation = display.getRotation();
			if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
				wallpaperHelper.setLandscape(false);

			} else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
				wallpaperHelper.setLandscape(true);
			}

		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {

			super.onSurfaceDestroyed(holder);
			wallpaperHelper.destroy();
			mVisible = false;
			mHandler.removeCallbacks(mUpdateDisplay);
		}

		public void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
			paint = new Paint();
			try {
				c = holder.lockCanvas();
				if (c != null && sprites != null) {

					WallpaperCostume wallpaperCostume;
					for (int position = 0; position < sprites.size(); position++) {
						Sprites: for (Sprite sprite : sprites) {
							wallpaperCostume = sprite.getWallpaperCostume();
							if (wallpaperCostume != null && wallpaperCostume.getzPosition() == position) {
								if (wallpaperCostume.getCostume() != null && !wallpaperCostume.isCostumeHidden()) {
									c.drawBitmap(wallpaperCostume.getCostume(), wallpaperCostume.getTop(),
											wallpaperCostume.getLeft(), paint);

								}
								break Sprites;
							}
						}
					}
				}

			} finally {
				if (c != null) {
					holder.unlockCanvasAndPost(c);
				}
			}
			mHandler.removeCallbacks(mUpdateDisplay);
			if (mVisible) {
				mHandler.postDelayed(mUpdateDisplay, 50);
			}
		}

		@Override
		public void onTouchEvent(MotionEvent event) {

			PointerCoords coords = new PointerCoords();

			int action = event.getActionMasked();
			if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
				for (int pointerIndex = 0; pointerIndex < event.getPointerCount(); pointerIndex++) {

					event.getPointerCoords(pointerIndex, coords);
					WallpaperCostume wallpaperCostume;

					Positions: for (int position = sprites.size() - 1; position >= 0; position--) {
						Sprites: for (Sprite sprite : sprites) {
							wallpaperCostume = sprite.getWallpaperCostume();
							if (wallpaperCostume != null && wallpaperCostume.getzPosition() == position) {
								if (wallpaperCostume.touchedInsideTheCostume(coords.x, coords.y)) {
									sprite.startWhenScripts("Tapped");
									draw();
									break Positions;
								}

								break Sprites;
							}
						}
					}

				}
			}
		}
	}

}

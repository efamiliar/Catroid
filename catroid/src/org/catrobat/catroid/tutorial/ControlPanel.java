/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.tutorial;

import org.catrobat.catroid.R;
import org.catrobat.catroid.tutorial.Tutor.ACTIONS;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Pinki, Herb
 * 
 */
public class ControlPanel implements SurfaceObject {
	private Resources resources;
	private Context context;

	private NinePatchDrawable menuBarPlaying;
	private NinePatchDrawable menuBarPaused;
	private NinePatchDrawable menuButton;

	private Rect menuButtonBounds;
	private Rect menuBarBounds;

	private boolean open = false;
	private boolean paused = false;

	private long waitTimeToDispatch = 250;
	private long timeOfLastChange = 0;

	private double scaleDifference = 0;
	private int originalDisplayWidthForPNGs = 300;

	private double[] stopPosition = { 241, 273 };
	private double[] pausePosition = { 79, 111 };
	private double[] backwardPosition = { 129, 167 };
	private double[] forwardPosition = { 185, 223 };

	private ACTIONS lastPressAction = null;
	private long lastPressTime = 0;

	private int marginLeft = 2;

	private int pauseTimeForToastMessage = 2000;

	Paint pauseBlock = new Paint();
	BitmapDrawable bitmap = new BitmapDrawable();

	public ControlPanel(Context context, TutorialOverlay tutorialOverlay) {
		this.resources = ((Activity) context).getResources();
		this.context = context;

		tutorialOverlay.addSurfaceObject(this);

		menuBarPlaying = (NinePatchDrawable) resources.getDrawable(R.drawable.tutorial_menu_bar_playing);
		menuBarPaused = (NinePatchDrawable) resources.getDrawable(R.drawable.tutorial_menu_bar_paused);
		menuButton = (NinePatchDrawable) resources.getDrawable(R.drawable.tutorial_menu_button);

		menuBarBounds = new Rect();
		menuBarBounds.bottom = Tutorial.getInstance(null).getScreenHeight();
		menuBarBounds.top = (Tutorial.getInstance(null).getScreenHeight() - menuBarPlaying.getIntrinsicHeight());
		menuBarBounds.right = Tutorial.getInstance(null).getScreenWidth();
		menuBarBounds.left = marginLeft;

		menuBarPlaying.setBounds(menuBarBounds);
		menuBarPaused.setBounds(menuBarBounds);

		menuButtonBounds = new Rect();
		menuButtonBounds.bottom = Tutorial.getInstance(null).getScreenHeight();
		menuButtonBounds.top = (Tutorial.getInstance(null).getScreenHeight() - menuButton.getIntrinsicHeight());
		menuButtonBounds.right = 64;
		menuButtonBounds.left = marginLeft;

		menuButton.setBounds(menuButtonBounds);

		scaleDifference = ((float) Tutorial.getInstance(null).getScreenWidth() - originalDisplayWidthForPNGs) / 4.0f;

		pausePosition[0] = pausePosition[0] + scaleDifference + marginLeft;
		pausePosition[1] = pausePosition[1] + scaleDifference + marginLeft;

		backwardPosition[0] = backwardPosition[0] + scaleDifference * 2.0f + marginLeft;
		backwardPosition[1] = backwardPosition[1] + scaleDifference * 2.0f + marginLeft;

		forwardPosition[0] = forwardPosition[0] + scaleDifference * 3.0f + marginLeft;
		forwardPosition[1] = forwardPosition[1] + scaleDifference * 3.0f + marginLeft;

		stopPosition[0] = stopPosition[0] + scaleDifference * 4.0f + marginLeft;
		stopPosition[1] = stopPosition[1] + scaleDifference * 4.0f + marginLeft;

		Log.i("buttons", "STOP BUTTON RO=" + stopPosition[0] + " LU=" + stopPosition[1]);
		Log.i("buttons", "Width before rezising=" + menuBarPlaying.getIntrinsicWidth());
	}

	@Override
	public void draw(Canvas canvas) {
		if (open) {
			if (paused) {
				menuBarPaused.draw(canvas);
			} else {
				menuBarPlaying.draw(canvas);
			}
		} else {
			menuButton.draw(canvas);
		}
	}

	public boolean isOpen() {
		return open;
	}

	public void open() {
		open = true;
	}

	public void close() {
		open = false;
	}

	public boolean isReadyToDispatch() {
		long actTime = System.currentTimeMillis();
		Log.i("tutorial", Thread.currentThread().getName() + ": (actTime - waitTimeToDispatch) > timeOfLastChange = "
				+ (actTime - waitTimeToDispatch) + " and " + timeOfLastChange);
		if ((actTime - waitTimeToDispatch) > timeOfLastChange) {
			timeOfLastChange = actTime;
			return true;
		}
		return false;
	}

	public void pressPlay() {
		paused = false;
		long actTime = System.currentTimeMillis();
		if (lastPressAction != ACTIONS.PLAY || actTime > (lastPressTime + pauseTimeForToastMessage)) {
			lastPressAction = ACTIONS.PLAY;
			Toast.makeText(context, "Play", Toast.LENGTH_SHORT).show();
			lastPressTime = actTime;
		}
	}

	public void pressPause() throws InterruptedException {
		paused = true;
		long actTime = System.currentTimeMillis();
		if (lastPressAction != ACTIONS.PAUSE || actTime > (lastPressTime + pauseTimeForToastMessage)) {
			lastPressAction = ACTIONS.PAUSE;
			Toast.makeText(context, "Pause", Toast.LENGTH_SHORT).show();
			lastPressTime = actTime;
		}
	}

	public void pressForward() {
		long actTime = System.currentTimeMillis();
		if (lastPressAction != ACTIONS.FORWARD || actTime > (lastPressTime + pauseTimeForToastMessage)) {
			lastPressAction = ACTIONS.FORWARD;
			Toast.makeText(context, "Schritt vor", Toast.LENGTH_SHORT).show();
			lastPressTime = actTime;
		}
	}

	public void pressBackward() {
		long actTime = System.currentTimeMillis();
		if (lastPressAction != ACTIONS.REWIND || actTime > (lastPressTime + pauseTimeForToastMessage)) {
			lastPressAction = ACTIONS.REWIND;
			Toast.makeText(context, "Schritt zurück", Toast.LENGTH_SHORT).show();
			lastPressTime = actTime;
		}
	}

	public boolean isPaused() {
		return this.paused;
	}

	public NinePatchDrawable getMenuBar() {
		return menuBarPlaying;
	}

	public NinePatchDrawable getMenuButton() {
		return menuButton;
	}

	public double[] getPausePosition() {
		return pausePosition;
	}

	public double[] getStopPosition() {
		return stopPosition;
	}

	public double[] getBackwardPosition() {
		return backwardPosition;
	}

	public double[] getForwardPosition() {
		return forwardPosition;
	}

	@Override
	public void update(long gameTime) {
	}
}

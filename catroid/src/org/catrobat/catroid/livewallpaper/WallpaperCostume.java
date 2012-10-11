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

import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.utils.ImageEditing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

public class WallpaperCostume {

	private CostumeData costumeData;
	private Sprite sprite;
	private Bitmap costume = null;
	private Bitmap costumeRotated = null;
	private Bitmap originalCostume = null;
	private Matrix landscapeRotationMatrix = null;

	private int x;
	private int y;
	private int top;
	private int left;

	int xDestination;
	int yDestination;

	float rotation = 0f;

	private int zPosition;

	private float alphaValue = 1f;
	private float brightness = 1f;

	private double size = 1;

	private boolean originalSaved = false;
	private boolean hidden = false;
	private boolean isBackground = false;
	private boolean topNeedsAdjustment = false;
	private boolean leftNeedsAdjustment = false;
	private boolean sizeChanged = false;
	private boolean coordsSwapped = false;

	private WallpaperHelper wallpaperHelper;

	public WallpaperCostume(Sprite sprite, CostumeData costumeData) {

		this.wallpaperHelper = WallpaperHelper.getInstance();
		this.sprite = sprite;
		this.zPosition = wallpaperHelper.getProject().getSpriteList().indexOf(sprite);

		this.landscapeRotationMatrix = new Matrix();
		landscapeRotationMatrix.postRotate(90.0f);

		//TODO: refactor the hard-coded value
		if (sprite.getName().equals("Background")) {
			this.isBackground = true;
			this.top = 0;
			this.left = 0;
		} else {
			setY(0);
			setX(0);
		}

		if (costumeData != null) {
			setCostume(costumeData);
		}

		sprite.setWallpaperCostume(this);

	}

	public float getTop() {
		if (topNeedsAdjustment) {
			this.topNeedsAdjustment = false;
			this.top = wallpaperHelper.getCenterXCoord() + x - (this.costume.getWidth() / 2);
		}
		return top;
	}

	public float getLeft() {
		if (leftNeedsAdjustment) {
			this.leftNeedsAdjustment = false;
			this.left = wallpaperHelper.getCenterYCoord() - y - (this.costume.getHeight() / 2);
		}
		return left;
	}

	public void setX(int x) {
		this.topNeedsAdjustment = true;
		this.x = x;
	}

	public void setY(int y) {
		this.leftNeedsAdjustment = true;
		this.y = y;
	}

	public void changeXBy(int x) {
		this.topNeedsAdjustment = true;
		this.x += x;
	}

	public void changeYby(int y) {
		this.leftNeedsAdjustment = true;
		this.y += y;
	}

	public boolean touchedInsideTheCostume(float x, float y) {
		if (isBackground) {
			return false;
		}

		float right = top;
		float bottom = left;

		if (wallpaperHelper.isLandscape()) {
			right += costumeRotated.getWidth();
			bottom += costumeRotated.getHeight();

		} else {
			right += costume.getWidth();
			bottom += costume.getHeight();
		}

		if (x > top && x < right && y > left && y < bottom) {

			return true;
		}

		return false;

	}

	public Bitmap getCostume() {
		if (!wallpaperHelper.isLandscape()) {
			return costume;
		} else {
			if (costumeRotated == null) {
				costumeRotated = Bitmap.createBitmap(costume, 0, 0, costume.getWidth(), costume.getHeight(),
						landscapeRotationMatrix, false);
			}

			if (!coordsSwapped) {
				swapCoords();
				this.coordsSwapped = true;
			}

			return costumeRotated;
		}
	}

	private void swapCoords() {
		int temp = this.top;
		this.top = this.left;
		this.left = temp;
	}

	public void setCostume(CostumeData costumeData) {
		this.costumeData = costumeData;
		Bitmap costumeImage = costumeData.getImageBitmap();

		if (isBackground && Values.SCREEN_WIDTH != costumeImage.getWidth()
				&& Values.SCREEN_HEIGHT != costumeImage.getHeight()) {
			this.costume = ImageEditing.scaleBitmap(costumeImage, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);

		} else {
			this.costume = costumeImage;
		}

		if (sizeChanged) {
			resizeCostume();
		}

		costumeRotated = null;

	}

	public void setCostumeSize(double size) {
		this.sizeChanged = true;
		this.size = size * 0.01;
		if (costumeData != null) {
			this.costume = costumeData.getImageBitmap();
			resizeCostume();
		}

	}

	public void changeCostumeSizeBy(double changeValue) {
		this.sizeChanged = true;
		this.size += (changeValue * 0.01);
		resizeCostume();
	}

	private void resizeCostume() {

		int newWidth = (int) (costume.getWidth() * size);
		int newHeight = (int) (costume.getHeight() * size);
		this.costume = ImageEditing.scaleBitmap(this.costume, newWidth, newHeight);

		this.topNeedsAdjustment = true;
		this.leftNeedsAdjustment = true;

	}

	public void clear() {
		setX(0);
		setY(0);
		this.hidden = false;
	}

	public CostumeData getCostumeData() {
		return costumeData;
	}

	public boolean isCostumeHidden() {
		return hidden;
	}

	public void setCostumeHidden(boolean hideCostume) {
		this.hidden = hideCostume;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public boolean isBackground() {
		return isBackground;
	}

	public void setBackground(boolean isBackground) {
		this.isBackground = isBackground;
	}

	public float getAlphaValue() {
		return alphaValue;
	}

	public void setAlphaValue(float alphaValue) {
		this.alphaValue = alphaValue;
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float percentage) {

		if (percentage < 0f) {
			percentage = 0f;
		}

		this.brightness = percentage;

		adjustBrightness();
	}

	public void changeBrightness(float percentage) {

		this.brightness += percentage;
		if (this.brightness < 0f) {
			this.brightness = 0f;
		}

		adjustBrightness();
	}

	private void adjustBrightness() {
		setCostume(this.costumeData);
		Bitmap resultBitmap = Bitmap.createBitmap(this.costume.getWidth(), this.costume.getHeight(),
				this.costume.getConfig());

		for (int width = 0; width < this.costume.getWidth(); width++) {
			for (int height = 0; height < this.costume.getHeight(); height++) {
				int oldPixelColor = this.costume.getPixel(width, height);

				int red = Color.red(oldPixelColor) + (int) (255 * (this.brightness - 1));
				int green = Color.green(oldPixelColor) + (int) (255 * (this.brightness - 1));
				int blue = Color.blue(oldPixelColor) + (int) (255 * (this.brightness - 1));
				int alpha = Color.alpha(oldPixelColor);

				if (red > 255) {
					red = 255;
				} else if (red < 0) {
					red = 0;
				}
				if (green > 255) {
					green = 255;
				} else if (green < 0) {
					green = 0;
				}
				if (blue > 255) {
					blue = 255;
				} else if (blue < 0) {
					blue = 0;
				}

				int newPixel = Color.argb(alpha, red, green, blue);
				resultBitmap.setPixel(width, height, newPixel);
			}
		}
		this.costume = resultBitmap;
	}

	public void setGhostEffect(float alpha) {
		if (alpha < 0f) {
			this.alphaValue = 0f;
		} else if (alpha > 1f) {
			this.alphaValue = 1f;
		}
		this.alphaValue = alpha;

		adjustGhostEffect();
	}

	public void changeGhostEffect(float alpha) {
		this.alphaValue += alpha;

		if (this.alphaValue < 0f) {
			this.alphaValue = 0f;
		} else if (this.alphaValue > 1f) {
			this.alphaValue = 1f;
		}

		adjustGhostEffect();

	}

	private void adjustGhostEffect() {
		setCostume(this.costumeData);
		Bitmap resultBitmap = Bitmap.createBitmap(this.costume.getWidth(), this.costume.getHeight(),
				this.costume.getConfig());

		for (int width = 0; width < this.costume.getWidth(); width++) {
			for (int height = 0; height < this.costume.getHeight(); height++) {

				int oldPixelColor = this.costume.getPixel(width, height);

				int red = Color.red(oldPixelColor);
				int green = Color.green(oldPixelColor);
				int blue = Color.blue(oldPixelColor);
				int alpha = Color.alpha(oldPixelColor) + (int) (255 * (this.alphaValue - 1));

				if (alpha > 255) {
					alpha = 255;
				} else if (alpha < 0) {
					alpha = 0;
				}

				int newPixel = Color.argb(alpha, red, green, blue);
				resultBitmap.setPixel(width, height, newPixel);
			}
		}
		this.costume = resultBitmap;
	}

	public void clearGraphicEffect() {
		this.alphaValue = 1f;
		this.brightness = 1f;
		adjustBrightness();
		adjustGhostEffect();
	}

	public void glideTo(int xDest, int yDest, int durationInMilliSeconds) {
		this.xDestination = xDest;
		this.yDestination = yDest;

		long startTime = System.currentTimeMillis();
		int duration = durationInMilliSeconds;
		while (duration > 0) {
			if (!sprite.isAlive(Thread.currentThread())) {
				break;
			}
			long timeBeforeSleep = System.currentTimeMillis();
			int sleep = 100;
			while (System.currentTimeMillis() <= (timeBeforeSleep + sleep)) {

				if (sprite.isPaused) {
					sleep = (int) ((timeBeforeSleep + sleep) - System.currentTimeMillis());
					long milliSecondsBeforePause = System.currentTimeMillis();
					while (sprite.isPaused) {
						if (sprite.isFinished) {
							return;
						}
						Thread.yield();
					}
					timeBeforeSleep = System.currentTimeMillis();
					startTime += System.currentTimeMillis() - milliSecondsBeforePause;
				}

				Thread.yield();
			}
			long currentTime = System.currentTimeMillis();
			duration -= (int) (currentTime - startTime);
			long timePassed = currentTime - startTime;

			float xPosition = this.x;
			float yPosition = this.y;

			this.changeXBy((int) (((float) timePassed / duration) * (xDestination - xPosition)));
			this.changeYby((int) (((float) timePassed / duration) * (yDestination - yPosition)));

			startTime = currentTime;
		}
		if (!sprite.isAlive(Thread.currentThread())) {
			// -stay at last position
		} else {
			setXYPosition(xDestination, yDestination);
		}
	}

	public void ifOnEdgeBounce() {

		float size = (float) this.size;

		float width = costume.getWidth() * size;
		float height = costume.getHeight() * size;
		int xPosition = this.x;
		int yPosition = this.y;

		int virtualScreenWidth = Values.SCREEN_WIDTH / 2;
		int virtualScreenHeight = Values.SCREEN_HEIGHT / 2;

		if (xPosition < -virtualScreenWidth + width / 2) {
			xPosition = -virtualScreenWidth + (int) (width / 2);
		} else if (xPosition > virtualScreenWidth - width / 2) {
			xPosition = virtualScreenWidth - (int) (width / 2);
		}
		if (yPosition > virtualScreenHeight - height / 2) {
			yPosition = virtualScreenHeight - (int) (height / 2);
		} else if (yPosition < -virtualScreenHeight + height / 2) {
			yPosition = -virtualScreenHeight + (int) (height / 2);
		}

		setXYPosition(xPosition, yPosition);
	}

	public void setXYPosition(int xPosition, int yPosition) {
		this.x = xPosition;
		this.y = yPosition;
	}

	public void rotate() {

		if (!originalSaved) {
			originalCostume = Bitmap.createBitmap(costume);
			originalSaved = true;
		}

		this.costume = ImageEditing.rotateBitmap(originalCostume, (int) this.rotation);
		this.topNeedsAdjustment = true;
		this.leftNeedsAdjustment = true;
	}

	public int getzPosition() {
		return zPosition;
	}

	public void setzPosition(int zPosition) {
		this.zPosition = zPosition;
	}

	public void setRotation(float r) {
		this.rotation += r;
		rotate();
	}

}

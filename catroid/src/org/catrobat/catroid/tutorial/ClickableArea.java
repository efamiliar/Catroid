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

/**
 * @author faxxe
 * 
 */
public class ClickableArea {
	public final int x;
	public final int y;
	public final float width;
	public final float height;
	public final float centerX;
	public final float centerY;

	public ClickableArea(float x, float y, float width, float height) {
		this.x = (int) x;
		this.y = (int) y;
		this.width = width;
		this.height = height;
		this.centerX = x + width / 2;
		this.centerY = y + height / 2;
	}
}
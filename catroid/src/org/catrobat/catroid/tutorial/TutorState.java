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

import android.util.Log;

/**
 * @author drab
 * 
 */
public class TutorState {
	private int x;
	private int y;
	private boolean flip;
	private int state;

	public TutorState(int x, int y, boolean flip, int state) {
		this.x = x;
		this.y = y;
		this.flip = flip;
		this.state = state;
		Log.i("state", "x= " + x + " y= " + y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isFlip() {
		return flip;
	}

	public int getState() {
		return state;
	}
}

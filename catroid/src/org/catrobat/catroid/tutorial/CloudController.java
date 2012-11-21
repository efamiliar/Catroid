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
public class CloudController {
	private Cloud cloud;

	public CloudController() {
		this.cloud = Cloud.getInstance(null);
	}

	public void fadeTo(ClickableArea ca) {
		cloud.fadeTo(ca);
	}

	public void jumpTo(ClickableArea ca) {
		cloud.jumpTo(ca);
	}

	public void disapear() {
		cloud.disappear();
	}

	public void show() {
		cloud.show();
	}

	public void fadeIn() {
		cloud.fadeIn();
	}

	public void fadeOut() {
		cloud.fadeOut();
	}

	public void clear() {
		cloud.clear();
	}

}

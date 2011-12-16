/**
 *  Catroid: An on-device graphical programming language for Android devices
    Copyright (C) 2010  Catroid development team
    (<http://code.google.com/p/catroid/wiki/Credits>)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.plugin.Drone.bricks;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.plugin.Drone.DroneConsts;
import at.tugraz.ist.catroid.plugin.Drone.DroneHandler;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class DroneLandBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	public DroneLandBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public void execute() {
		try {
			DroneHandler.getInstance().getDrone().land();
		} catch (Exception e) {
			Log.e(DroneConsts.DroneLogTag, "Exception DroneLandBrick -> execute()", e);
		}
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_drone_land, null);
		}
		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_drone_land, null);
	}

	@Override
	public Brick clone() {
		return new DroneLandBrick(getSprite());
	}

	public int getRequiredResources() {
		return WIFI_DRONE;
	}

}

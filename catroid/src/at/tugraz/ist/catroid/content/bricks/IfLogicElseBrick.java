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
package at.tugraz.ist.catroid.content.bricks;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;

public class IfLogicElseBrick extends NestingBrick implements AllowedAfterDeadEndBrick {
	private Sprite sprite;
	private static final String TAG = IfLogicElseBrick.class.getSimpleName();
	private IfLogicBeginBrick ifBeginBrick;
	private IfLogicEndBrick ifEndBrick;
	private boolean skipToEndBrickPosition = false;

	public IfLogicElseBrick(Sprite sprite, IfLogicBeginBrick ifBeginBrick) {
		this.sprite = sprite;
		this.ifBeginBrick = ifBeginBrick;
		ifBeginBrick.setElseBrick(this);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		if (skipToEndBrickPosition) {
			Script script = ifBeginBrick.getScript();
			script.setExecutingBrickIndex(script.getBrickList().indexOf(ifEndBrick));
		}
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	public void skipToEndIfPositionOnElse(boolean skip) {
		skipToEndBrickPosition = skip;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.brick_if_else, null);
	}

	@Override
	public Brick clone() {
		return new IfLogicElseBrick(getSprite(), ifBeginBrick);
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_if_else, null);
	}

	public IfLogicEndBrick getIfEndBrick() {
		return this.ifEndBrick;
	}

	public void setIfEndBrick(IfLogicEndBrick ifEndBrick) {
		this.ifEndBrick = ifEndBrick;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		if (brick == ifBeginBrick || brick == ifEndBrick) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isInitialized() {
		if (ifBeginBrick == null || ifEndBrick == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void initialize() {
		//ifBeginBrick = new IfLogicBeginBrick(sprite, 0);
		//ifEndBrick = new IfLogicEndBrick(sprite, this);
		Log.w(TAG, "Cannot create the IfLogic Bricks from here!");
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts() {
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		nestingBrickList.add(ifBeginBrick);
		nestingBrickList.add(this);
		nestingBrickList.add(ifEndBrick);

		return nestingBrickList;
	}

	@Override
	public View getNoPuzzleView(Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.brick_if_else, null);
	}

	@Override
	public void onClick(View view) {

	}

}

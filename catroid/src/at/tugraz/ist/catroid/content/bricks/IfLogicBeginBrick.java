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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.ui.fragment.FormulaEditorFragment;

public class IfLogicBeginBrick extends NestingBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private static final String TAG = IfLogicBeginBrick.class.getSimpleName();
	public static final int EXECUTE_ELSE_PART = -1;
	private Formula ifCondition;
	protected Sprite sprite;
	protected IfLogicElseBrick ifElseBrick;
	protected IfLogicEndBrick ifEndBrick;

	public IfLogicBeginBrick(Sprite sprite, int condition) {
		this.sprite = sprite;
		ifCondition = new Formula(Integer.toString(condition));
	}

	public IfLogicBeginBrick(Sprite sprite, Formula condition) {
		this.sprite = sprite;
		ifCondition = condition;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		int condition = ifCondition.interpretInteger();
		if (condition != 0) {
			ifElseBrick.skipToEndIfPositionOnElse(true);
		} else {
			Script script = getScript();
			ifElseBrick.skipToEndIfPositionOnElse(false);
			script.setExecutingBrickIndex(script.getBrickList().indexOf(ifElseBrick));
		}
	}

	public Script getScript() {
		for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
			Script script = sprite.getScript(i);
			if (script.getBrickList().contains(this)) {
				return script;
			}
		}
		return null;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public IfLogicElseBrick getElseBrick() {
		return this.ifElseBrick;
	}

	public void setElseBrick(IfLogicElseBrick elseBrick) {
		this.ifElseBrick = elseBrick;
	}

	public boolean checkCondition() {
		double evaluated = ifCondition.interpretInteger();
		if (evaluated == 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Brick clone() {
		return new IfLogicBeginBrick(getSprite(), ifCondition);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View view = View.inflate(context, R.layout.brick_if_begin_if, null);

		TextView text1 = (TextView) view.findViewById(R.id.brick_if_text_view1);
		EditText edit1 = (EditText) view.findViewById(R.id.brick_if_edit_text1);

		ifCondition.setTextFieldId(R.id.brick_if_edit_text1);
		ifCondition.refreshTextField(view);

		text1.setVisibility(View.GONE);
		edit1.setVisibility(View.VISIBLE);

		edit1.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_if_begin_if, null);
	}

	@Override
	public void onClick(View view) {
		FormulaEditorFragment.showFragment(view, this, ifCondition);
	}

	@Override
	public boolean isInitialized() {
		if (ifElseBrick == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void initialize() {
		ifElseBrick = new IfLogicElseBrick(sprite, this);
		ifEndBrick = new IfLogicEndBrick(sprite, ifElseBrick, this);
		Log.w(TAG, "Creating if logic stuff");
	}

	@Override
	public List<NestingBrick> getAllNestingBrickParts() {
		List<NestingBrick> nestingBrickList = new ArrayList<NestingBrick>();
		nestingBrickList.add(this);
		nestingBrickList.add(ifElseBrick);
		nestingBrickList.add(ifEndBrick);

		return nestingBrickList;
	}

	@Override
	public boolean isDraggableOver(Brick brick) {
		if (brick == ifElseBrick) {
			return false;
		} else {
			return true;
		}
	}

	//	@Override
	//	public View getNoPuzzleView(Context context, int brickId, BaseAdapter adapter) {
	//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	//		return inflater.inflate(R.layout.brick_if_else, null);
	//	}

}

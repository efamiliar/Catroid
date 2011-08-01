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
package at.tugraz.ist.catroid.ui.adapter;

/**
 * @author DANIEL
 *
 */

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.TapScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastReceiverBrick;
import at.tugraz.ist.catroid.content.bricks.IfStartedBrick;
import at.tugraz.ist.catroid.content.bricks.IfTouchedBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListener;
import at.tugraz.ist.catroid.ui.dragndrop.DragNDropListView;

public class BrickAdapter extends BaseExpandableListAdapter implements DragAndDropListener, OnGroupClickListener {

	private Context context;
	private Sprite sprite;
	private BrickListAnimation brickListAnimation;
	private boolean animateChildren;
	private int dragTargetPosition;
	private boolean dragging;
	private OnLongClickListener longClickListener;

	public BrickAdapter(Context context, Sprite sprite, DragNDropListView listView) {
		this.context = context;
		this.sprite = sprite;
		this.dragging = false;
		brickListAnimation = new BrickListAnimation(this, listView);
		longClickListener = listView;
	}

	public Brick getChild(int groupPosition, int childPosition) {
		return sprite.getScript(groupPosition).getBrickList().get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		Brick brick = getChild(groupPosition, childPosition);

		if (dragging && (dragTargetPosition == childPosition)) {

			View childView = View.inflate(context, R.layout.brick_insert, null);
			return childView;

		}
		View currentBrickView = brick.getView(context, childPosition, this);
		currentBrickView.setOnLongClickListener(longClickListener);

		if (!animateChildren) {
			return currentBrickView;
		}
		brickListAnimation.doExpandAnimation(currentBrickView, childPosition);

		return currentBrickView;
	}

	public int getChildrenCount(int groupPosition) {
		return sprite.getScript(groupPosition).getBrickList().size();
	}

	public Script getGroup(int groupPosition) {
		return sprite.getScript(groupPosition);
	}

	public int getGroupCount() {
		return sprite.getNumberOfScripts();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		View view;
		if (getGroup(groupPosition) instanceof TapScript) {
			view = new IfTouchedBrick(sprite, getGroup(groupPosition)).getPrototypeView(context);
		} else if (getGroup(groupPosition) instanceof BroadcastScript) {
			view = new BroadcastReceiverBrick(sprite, (BroadcastScript) getGroup(groupPosition)).getView(context, 0,
					null);
		} else {
			view = new IfStartedBrick(sprite, getGroup(groupPosition)).getPrototypeView(context);
		}
		return view;
	}

	public boolean hasStableIds() {
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public void drag(int from, int to) {

		int childFrom = Math.max(0, from - getGroupCount());
		int childTo = Math.max(0, to - getGroupCount());
		childFrom = Math.min(childFrom, getChildCountFromLastGroup() - 1);
		childTo = Math.min(childTo, getChildCountFromLastGroup() - 1);

		dragTargetPosition = childTo;

		if (from != to) {
			Brick removedBrick = sprite.getScript(getGroupCount() - 1).getBrickList().remove(childFrom);
			sprite.getScript(getGroupCount() - 1).addBrick(childTo, removedBrick);
			notifyDataSetChanged();
		}

		if (!dragging) {
			dragging = true;
			notifyDataSetChanged();
		}

	}

	public void drop(int from, int to) {
		dragging = false;

		Brick droppedBrick = getChild(getGroupCount() - 1, to - getGroupCount());

		if (droppedBrick instanceof LoopBeginBrick) {
			sprite.getScript(getGroupCount() - 1).removeBrick(((LoopBeginBrick) droppedBrick).getLoopEndBrick());
			sprite.getScript(getGroupCount() - 1).addBrick(to - getGroupCount() + 1,
					((LoopBeginBrick) droppedBrick).getLoopEndBrick());
		} else if (droppedBrick instanceof LoopEndBrick) {
			sprite.getScript(getGroupCount() - 1).removeBrick(((LoopEndBrick) droppedBrick).getLoopBeginBrick());
			sprite.getScript(getGroupCount() - 1).addBrick(to - getGroupCount() + 1,
					((LoopEndBrick) droppedBrick).getLoopBeginBrick());
		}

		notifyDataSetChanged();
	}

	public void remove(int index) {
		dragging = false;
		ArrayList<Brick> brickList = sprite.getScript(getGroupCount() - 1).getBrickList();

		Brick brickToRemove = brickList.get(index - getGroupCount());
		if (brickToRemove instanceof PlaySoundBrick) {
			PlaySoundBrick toDelete = (PlaySoundBrick) brickToRemove;
			String pathToSoundFile = toDelete.getPathToSoundFile();
			if (pathToSoundFile != null) {
				StorageHandler.getInstance().deleteFile(pathToSoundFile);
			}
		} else if (brickToRemove instanceof SetCostumeBrick) {
			SetCostumeBrick toDelete = (SetCostumeBrick) brickToRemove;
			String imagePath = toDelete.getImagePath();
			if (imagePath != null) {
				StorageHandler.getInstance().deleteFile(imagePath);
			}
		} else if (brickToRemove instanceof LoopBeginBrick) {
			LoopBeginBrick loopBeginBrick = (LoopBeginBrick) brickToRemove;
			brickList.remove(loopBeginBrick.getLoopEndBrick());
		} else if (brickToRemove instanceof LoopEndBrick) {
			LoopEndBrick loopEndBrick = (LoopEndBrick) brickToRemove;
			brickList.remove(loopEndBrick.getLoopBeginBrick());
		}

		brickList.remove(brickToRemove);
		notifyDataSetChanged();
	}

	public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id) {

		if (groupPosition == getGroupCount() - 1) {
			return false;
		}

		animateChildren = true;
		brickListAnimation.doClickOnGroupAnimate(getGroupCount(), groupPosition);
		return true;
	}

	public void doReordering(ExpandableListView parent, int groupPosition) {
		for (int i = 0; i < getGroupCount(); ++i) {
			parent.collapseGroup(i);
		}
		Script currentScript = sprite.getScript(groupPosition);
		int lastScriptIndex = sprite.getNumberOfScripts() - 1;
		Script lastScript = sprite.getScript(lastScriptIndex);
		boolean scriptDeleted = sprite.removeScript(currentScript);
		if (scriptDeleted) {
			sprite.addScript(currentScript);
			sprite.removeScript(lastScript);
			sprite.addScript(groupPosition, lastScript);
		}

		ProjectManager.getInstance().setCurrentScript(currentScript);

		notifyDataSetChanged();
		parent.expandGroup(getGroupCount() - 1);
	}

	public void setAnimateChildren(boolean animateChildren) {
		this.animateChildren = animateChildren;
	}

	public int getChildCountFromLastGroup() {
		return getChildrenCount(getGroupCount() - 1);
	}

}

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
package at.tugraz.ist.catroid.tutorial;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.tutorial.tasks.Task;

/**
 * @author gnu
 * 
 */
public class ClickDispatcher {

	Context context;
	ControlPanel panel;
	Task.Notification currentNotification;

	ClickDispatcher(Context context, ControlPanel panel) {
		this.panel = panel;
		this.context = context;
	}

	public void setCurrentNotification(Task.Notification currentNotification) {
		this.currentNotification = currentNotification;
	}

	public void dispatchEvent(MotionEvent ev) {

		int x = (int) ev.getX();
		int y = (int) ev.getY();

		if (panel != null) {
			Rect bounds = panel.getPanelBounds();
			//check if event coordinates are the coordinates of the control panel buttons
			if (x >= bounds.left && x <= bounds.right) {
				if (y >= bounds.top && y <= bounds.bottom) {
					dispatchPanel(ev);
					return;
				}
			}
		}

		if (currentNotification == null) {
			return;
		}

		if (currentNotification == Task.Notification.CURRENT_PROJECT_BUTTON) {
			if (context.getClass().getName().compareTo("at.tugraz.ist.catroid.ui.MainMenuActivity") != 0) {
				// Das darf nicht sein, wie so ein Fehler ueberhaupt auftreten und kann
				// und wie man damit umgeht ist noch rauszufinden
				return;
			}

			dispatchMainMenu(ev);
			return;
		}

		if (currentNotification == Task.Notification.ADD_SPRITE_BUTTON) {
			if (context.getClass().getName().compareTo("at.tugraz.ist.catroid.ui.ProjectActivity") != 0) {
				//nogo
				return;
			}
			dispatchProject(ev);
			return;
		}

		//		Log.i("faxxe", "hallo " + context.getClass().getName());
		//		if (context.getClass().getName().compareTo("at.tugraz.ist.catroid.ui.MainMenuActivity") == 0) {
		//			return dispatchMainMenu(ev);
		//		} else if (context.getClass().getName().compareTo("at.tugraz.ist.catroid.ui.ProjectActivity") == 0) {
		//			return dispatchProject(ev);
		//		} else if (context.getClass().getName().compareTo("at.tugraz.ist.catroid.ui.ScriptActivity") == 0) {
		//			return dispatchSkript(ev);
		//		}
		return;
	}

	public void dispatchPanel(MotionEvent ev) {
		//unterscheide buttons play, pause, forward, backward
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		Rect bounds = panel.getPanelBounds();

		//		check if event coordinates are the coordinates of the control panel buttons
		if (x >= bounds.left && x <= bounds.left + 50) {
			if (y >= bounds.top && y <= bounds.bottom) {
				Tutorial.getInstance(null).resumeTutorial();
				//panel.pressPlay();
			}
		}
		bounds.left = bounds.left + 70;

		if (x >= bounds.left && x <= bounds.left + 50) {
			if (y >= bounds.top && y <= bounds.bottom) {
				Tutorial.getInstance(null).pauseTutorial();
				//Toast toast = Toast.makeText(context, "PAUSE", Toast.LENGTH_SHORT);
				//toast.show();
			}
		}

		bounds.left = bounds.left + 70;

		if (x >= bounds.left && x <= bounds.left + 50) {
			if (y >= bounds.top && y <= bounds.bottom) {
				Toast toast = Toast.makeText(context, "FORWARD", Toast.LENGTH_SHORT);
				toast.show();
			}
		}

		bounds.left = bounds.left + 70;

		if (x >= bounds.left && x <= bounds.left + 50) {
			if (y >= bounds.top && y <= bounds.bottom) {
				Toast toast = Toast.makeText(context, "BACKWARD", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	public void dispatchSkript(MotionEvent ev) {
		Activity currentActivity = (Activity) context;
		Activity parentActivity = currentActivity.getParent();
		ImageButton lila = (ImageButton) parentActivity.findViewById(R.id.btn_action_add_sprite);

		int x = lila.getLeft();
		int y = lila.getTop();
		int maxx = x + lila.getHeight();
		int maxy = y + lila.getWidth();
		Log.i("faxxe", x + " " + y + " " + maxx + " " + maxy);
		Log.i("faxxe", ev.getX() + " " + ev.getY());
		Dialog currentDialog = Tutorial.getInstance(null).getDialog();
		if (currentDialog == null) {
			parentActivity.dispatchTouchEvent(ev);
		} else {
			currentDialog.dispatchTouchEvent(ev);
		}

		//		if (ev.getX() > x && ev.getY() > y && ev.getX() < maxx && ev.getY() < maxy && currentDialog == null) {
		//			dongs.dispatchTouchEvent(ev);
		//		}
		//		Log.i("faxxe", "schauma ob a einegeht!");
		//		if (Tutorial.getInstance(null).getDialog() != null) {
		//			Log.i("faxxe", "geht eh eine!");
		//			Tutorial.getInstance(null).getDialog().dispatchTouchEvent(ev);
		//			//Tutorial.getInstance(null).setDialog(null);
		//		}
	}

	public void dispatchProject(MotionEvent ev) {
		ListActivity listActivity = (ListActivity) context;
		ListView livi = listActivity.getListView();
		int y = livi.getChildAt(0).getTop();
		ev.setLocation(ev.getX(), ev.getY() - 100); // please anyone find out the real height of the titlebar!
		int x = livi.getChildAt(0).getLeft();
		int maxx = livi.getChildAt(0).getRight();
		int maxy = livi.getChildAt(0).getBottom();
		Log.i("faxxe", "touched!" + x + " " + y + " " + maxx + " " + maxy + " " + ev.getX() + " " + ev.getY());

		if (ev.getX() < maxx && ev.getX() > x && ev.getY() < maxy && ev.getY() > y) {
			Log.i("faxxe", "irgendwos" + x + " " + y + " " + maxx + " " + maxy + " " + ev.getX() + " " + ev.getY());
			livi.dispatchTouchEvent(ev);
		}
	}

	public boolean isButtonClicked(MotionEvent event, Button button) {
		int location[] = new int[2];
		button.getLocationOnScreen(location);
		int width = button.getWidth();
		int height = button.getHeight();

		if (event.getX() > location[0] && event.getX() < location[0] + width && event.getY() > location[1]
				&& event.getY() < location[1] + height) {
			return (true);
		} else {
			return (false);
		}
	}

	public void dispatchMainMenu(MotionEvent ev) {
		Log.i("faxxe", "mainmenudispatcher");
		Activity currentActivity = (Activity) context;
		Button currentProjectButton = (Button) currentActivity.findViewById(R.id.current_project_button);
		Button aboutButton = (Button) currentActivity.findViewById(R.id.about_catroid_button);
		Button webButton = (Button) currentActivity.findViewById(R.id.web_button);
		Button newProjectButton = (Button) currentActivity.findViewById(R.id.new_project_button);

		if (currentNotification == Task.Notification.CURRENT_PROJECT_BUTTON) {
			if (isButtonClicked(ev, currentProjectButton)) {
				currentActivity.dispatchTouchEvent(ev);
			}
		}

		//		if (isButtonClicked(ev, tutorialButton)) {
		//			currentActivity.dispatchTouchEvent(ev);
		//		}
	}

}

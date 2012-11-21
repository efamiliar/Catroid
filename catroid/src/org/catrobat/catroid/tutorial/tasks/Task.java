package org.catrobat.catroid.tutorial.tasks;

import java.util.HashMap;

import org.catrobat.catroid.tutorial.SurfaceObjectTutor;

public interface Task {

	public static enum Type {
		JUMP, APPEAR, DISAPPEAR, SAY, FLIP, POINT, NOTIFICATION, SLEEP, WALK, FADEIN, FADEOUT
	}

	public enum Tutor {
		CATRO, MIAUS
	}

	public enum Notification {
		CURRENT_PROJECT_BUTTON, NEW_PROJECT_BUTTON, ABOUT_BUTTON, MY_PROJECTS_BUTTON, UPLOAD_PROJECT_BUTTON, WEB_RESOURCES_BUTTON, SETTINGS_BUTTON, TUTORIAL_BUTTON,
		/**/
		PROJECT_ADD_SPRITE, PROJECT_LIST_ITEM, PROJECT_HOME_BUTTON, PROJECT_STAGE_BUTTON,
		/**/
		TAB_SCRIPTS, TAB_COSTUMES, TAB_SOUNDS,
		/**/
		SCRIPTS_ADD_BRICK,
		/**/
		COSTUMES_PAINTROID, COSTUMES_RENAME, COSTUMES_COPY, COSTUMES_DELETE, COSTUMES_ADD_COSTUME,
		/**/
		SOUNDS_ADD_SOUND, SOUNDS_PLAY, SOUNDS_RENAME, SOUNDS_DELETE,
		/**/
		BRICK_CATEGORY_DIALOG, BRICK_ADD_DIALOG, BRICK_CATEGORY_CONTROL, BRICK_CATEGORY_LOOKS, BRICK_CATEGORY_SOUND, BRICK_CATEGORY_MOTION, BRICK_DIALOG_DONE,
		/**/
		IF_PROJECT_STARTED, WAIT_SECONDS, IF, REPEAT, REPEAT_TIMES, SET_COSTUME
	}

	public boolean execute(HashMap<Task.Tutor, SurfaceObjectTutor> tutors);

	public Type getType();

	public Tutor getTutorType();

	public void setEndPositionOfTaskForTutor(HashMap<Tutor, SurfaceObjectTutor> tutors);

}

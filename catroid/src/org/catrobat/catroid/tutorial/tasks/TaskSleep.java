package org.catrobat.catroid.tutorial.tasks;

import java.util.HashMap;

import org.catrobat.catroid.tutorial.SurfaceObjectTutor;

public class TaskSleep implements Task {
	private Tutor tutorType;
	private int sleepTime;

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	@Override
	public Tutor getTutorType() {
		return tutorType;
	}

	public void setTutorType(Tutor tutorType) {
		this.tutorType = tutorType;
	}

	@Override
	public Type getType() {
		return (Type.SLEEP);
	}

	@Override
	public boolean execute(HashMap<Task.Tutor, SurfaceObjectTutor> tutors) {
		try {
			SurfaceObjectTutor tutor = tutors.get(tutorType);
			tutor.sleep();
			Thread.sleep(sleepTime);
		} catch (Exception e) {

		}
		return false;
	}

	@Override
	public void setEndPositionOfTaskForTutor(HashMap<Tutor, SurfaceObjectTutor> tutors) {

	}
}
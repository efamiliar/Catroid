package org.catrobat.catroid.physics;

import com.badlogic.gdx.math.Vector2;

public final class PhysicWorldConverter {

	public static float angleBox2dToCat(float angle) {
		return (float) Math.toDegrees(angle);
	}

	public static float angleCatToBox2d(float angle) {
		return (float) Math.toRadians(angle);
	}

	public static float lengthCatToBox2d(float length) {
		return length / PhysicWorld.RATIO;
	}

	public static float lengthBox2dToCat(float length) {
		return length * PhysicWorld.RATIO;
	}

	public static Vector2 vecCatToBox2d(Vector2 vector) {
		return new Vector2(lengthCatToBox2d(vector.x), lengthCatToBox2d(vector.y));
	}

	public static Vector2 vecBox2dToCat(Vector2 vector) {
		return new Vector2(lengthBox2dToCat(vector.x), lengthBox2dToCat(vector.y));
	}
}

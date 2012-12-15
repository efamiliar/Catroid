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
package at.tugraz.ist.catroid.formulaeditor;

import java.util.LinkedList;
import java.util.List;

import android.view.KeyEvent;

/**
 * @author obusher
 * 
 */
public class CatKeyEvent extends KeyEvent {

	/* FUNCTIONS */
	public static final int KEYCODE_SIN = 1000;
	public static final int KEYCODE_COS = 1001;
	public static final int KEYCODE_TAN = 1002;
	public static final int KEYCODE_LN = 1003;
	public static final int KEYCODE_LOG = 1004;
	public static final int KEYCODE_PI = 1005;
	public static final int KEYCODE_SQUAREROOT = 1006;
	public static final int KEYCODE_EULER = 1007;
	public static final int KEYCODE_RANDOM = 1008;
	public static final int KEYCODE_ABS = 1009;
	public static final int KEYCODE_ROUND = 1010;

	/* SENSOR */
	public static final int KEYCODE_SENSOR1 = 1100;
	public static final int KEYCODE_SENSOR2 = 1101;
	public static final int KEYCODE_SENSOR3 = 1102;
	public static final int KEYCODE_SENSOR4 = 1103;
	public static final int KEYCODE_SENSOR5 = 1104;
	public static final int KEYCODE_SENSOR6 = 1105;
	public static final int KEYCODE_SENSOR7 = 1106;

	/* OTHER STUFF */
	public static final int KEYCODE_BRACKET = 1200;
	public static final int KEYCODE_LOOK_BUTTON = 1201;
	public static final int KEYCODE_LOOK_X = 1202;
	public static final int KEYCODE_LOOK_Y = 1203;
	public static final int KEYCODE_LOOK_GHOSTEFFECT = 1204;
	public static final int KEYCODE_LOOK_BRIGHTNESS = 1205;
	public static final int KEYCODE_LOOK_SIZE = 1206;
	public static final int KEYCODE_LOOK_ROTATION = 1207;
	public static final int KEYCODE_LOOK_LAYER = 1208;

	/* OPERATORS */
	public static final int KEYCODE_NOT_EQUAL = 1300;
	public static final int KEYCODE_SMALLER_THAN = 1301;
	public static final int KEYCODE_GREATER_THAN = 1302;
	public static final int KEYCODE_LOGICAL_AND = 1303;
	public static final int KEYCODE_LOGICAL_OR = 1304;

	// Please update the functions of this class if you add new KEY_CODE constants ^_^

	/**
	 * @param origEvent
	 */
	public CatKeyEvent(KeyEvent origEvent) {
		super(origEvent);
	}

	public List<InternToken> createInternTokensByCatKeyEvent() {

		if (this.getKeyCode() >= KeyEvent.KEYCODE_0 && this.getKeyCode() <= KeyEvent.KEYCODE_9) {
			return buildNumber("" + super.getDisplayLabel());
		}

		switch (getKeyCode()) {

		//FUNCTIONS:
			case CatKeyEvent.KEYCODE_SIN:
				return buildSingleParameterFunction(Functions.SIN, "0");
			case CatKeyEvent.KEYCODE_COS:
				return buildSingleParameterFunction(Functions.COS, "0");
			case CatKeyEvent.KEYCODE_TAN:
				return buildSingleParameterFunction(Functions.TAN, "0");
			case CatKeyEvent.KEYCODE_LN:
				return buildSingleParameterFunction(Functions.LN, "0");
			case CatKeyEvent.KEYCODE_LOG:
				return buildSingleParameterFunction(Functions.LOG, "0");
			case CatKeyEvent.KEYCODE_PI:
				return buildFunctionWithoutParametersAndBrackets(Functions.PI);
			case CatKeyEvent.KEYCODE_SQUAREROOT:
				return buildSingleParameterFunction(Functions.SQRT, "0");
			case CatKeyEvent.KEYCODE_EULER:
				return buildFunctionWithoutParametersAndBrackets(Functions.EULER);
			case CatKeyEvent.KEYCODE_RANDOM:
				return buildDoubleParameterFunction(Functions.RAND, "0", "1");
			case CatKeyEvent.KEYCODE_ABS:
				return buildSingleParameterFunction(Functions.ABS, "0");
			case CatKeyEvent.KEYCODE_ROUND:
				return buildSingleParameterFunction(Functions.ROUND, "0");

				//SENSOR

			case CatKeyEvent.KEYCODE_SENSOR1:
				return buildSensor(Sensors.X_ACCELERATION_);
			case CatKeyEvent.KEYCODE_SENSOR2:
				return buildSensor(Sensors.Y_ACCELERATION_);
			case CatKeyEvent.KEYCODE_SENSOR3:
				return buildSensor(Sensors.Z_ACCELERATION_);
			case CatKeyEvent.KEYCODE_SENSOR4:
				return buildSensor(Sensors.AZIMUTH_ORIENTATION_);
			case CatKeyEvent.KEYCODE_SENSOR5:
				return buildSensor(Sensors.PITCH_ORIENTATION_);
			case CatKeyEvent.KEYCODE_SENSOR6:
				return buildSensor(Sensors.ROLL_ORIENTATION_);

				//PERIOD
			case CatKeyEvent.KEYCODE_PERIOD:
				return buildPeriod();

				//OPERATOR

			case CatKeyEvent.KEYCODE_PLUS:
				return buildOperator(Operators.PLUS);
			case CatKeyEvent.KEYCODE_MINUS:
				return buildOperator(Operators.MINUS);
			case CatKeyEvent.KEYCODE_STAR:
				return buildOperator(Operators.MULT);
			case CatKeyEvent.KEYCODE_SLASH:
				return buildOperator(Operators.DIVIDE);
			case CatKeyEvent.KEYCODE_POWER:
				return buildOperator(Operators.POW);
			case CatKeyEvent.KEYCODE_EQUALS:
				return buildOperator(Operators.EQUAL);
			case CatKeyEvent.KEYCODE_NOT_EQUAL:
				return buildOperator(Operators.NOT_EQUAL);
			case CatKeyEvent.KEYCODE_SMALLER_THAN:
				return buildOperator(Operators.SMALLER_THAN);
			case CatKeyEvent.KEYCODE_GREATER_THAN:
				return buildOperator(Operators.GREATER_THAN);
			case CatKeyEvent.KEYCODE_LOGICAL_AND:
				return buildOperator(Operators.LOGICAL_AND);
			case CatKeyEvent.KEYCODE_LOGICAL_OR:
				return buildOperator(Operators.LOGICAL_OR);

				//BRACKET

			case CatKeyEvent.KEYCODE_BRACKET:
				return buildBracket("0");

				//COSTUME

			case CatKeyEvent.KEYCODE_LOOK_X:
				return buildLook(Sensors.LOOK_X_);
			case CatKeyEvent.KEYCODE_LOOK_Y:
				return buildLook(Sensors.LOOK_Y_);
			case CatKeyEvent.KEYCODE_LOOK_GHOSTEFFECT:
				return buildLook(Sensors.LOOK_GHOSTEFFECT_);
			case CatKeyEvent.KEYCODE_LOOK_BRIGHTNESS:
				return buildLook(Sensors.LOOK_BRIGHTNESS_);
			case CatKeyEvent.KEYCODE_LOOK_SIZE:
				return buildLook(Sensors.LOOK_SIZE_);
			case CatKeyEvent.KEYCODE_LOOK_ROTATION:
				return buildLook(Sensors.LOOK_ROTATION_);
			case CatKeyEvent.KEYCODE_LOOK_LAYER:
				return buildLook(Sensors.LOOK_LAYER_);

		}

		return null;

	}

	private List<InternToken> buildPeriod() {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.PERIOD));
		return returnList;
	}

	private List<InternToken> buildNumber(String numberValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.NUMBER, numberValue));
		return returnList;
	}

	private List<InternToken> buildLook(Sensors sensors) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.LOOK, sensors.sensorName));
		return returnList;
	}

	private List<InternToken> buildBracket(String bracketValue) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.BRACKET_OPEN));
		returnList.add(new InternToken(InternTokenType.NUMBER, bracketValue));
		returnList.add(new InternToken(InternTokenType.BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildOperator(Operators operator) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.OPERATOR, operator.operatorName));
		return returnList;
	}

	private List<InternToken> buildSensor(Sensors sensor) {
		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.SENSOR, sensor.sensorName));
		return returnList;
	}

	private List<InternToken> buildDoubleParameterFunction(Functions function, String firstParameterNumberValue,
			String secondParameterNumberValue) {

		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.functionName));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));

		returnList.add(new InternToken(InternTokenType.NUMBER, firstParameterNumberValue));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETER_DELIMITER));

		returnList.add(new InternToken(InternTokenType.NUMBER, secondParameterNumberValue));

		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));

		return returnList;

	}

	private List<InternToken> buildSingleParameterFunction(Functions function, String parameterNumberValue) {

		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.functionName));
		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN));
		returnList.add(new InternToken(InternTokenType.NUMBER, parameterNumberValue));
		returnList.add(new InternToken(InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE));
		return returnList;
	}

	private List<InternToken> buildFunctionWithoutParametersAndBrackets(Functions function) {

		List<InternToken> returnList = new LinkedList<InternToken>();
		returnList.add(new InternToken(InternTokenType.FUNCTION_NAME, function.functionName));
		return returnList;
	}

}

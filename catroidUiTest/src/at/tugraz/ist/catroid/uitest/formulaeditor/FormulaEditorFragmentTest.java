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
package at.tugraz.ist.catroid.uitest.formulaeditor;

import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.formulaeditor.Formula;
import at.tugraz.ist.catroid.formulaeditor.FormulaEditorHistory;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class FormulaEditorFragmentTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private Project project;
	private PlaceAtBrick placeAtBrick;
	CatKeyboardClicker catKeyboardClicker = null;
	private static final int INITIAL_X = 8;
	private static final int INITIAL_Y = 7;

	private static final int X_POS_EDIT_TEXT_ID = 0;
	private static final int Y_POS_EDIT_TEXT_ID = 1;
	private static final int FORMULA_EDITOR_EDIT_TEXT_ID = 2;

	public FormulaEditorFragmentTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
		catKeyboardClicker = new CatKeyboardClicker(solo);
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		placeAtBrick = new PlaceAtBrick(sprite, INITIAL_X, INITIAL_Y);
		script.addBrick(placeAtBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	public void testChangeFormula() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("1");
		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.sleep(50);
		assertTrue("Saved changes message not found!",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.goBack();
		solo.sleep(100);
		assertEquals("Value not saved!", "1 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("+");

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Fix error message not found!", solo.searchText(solo.getString(R.string.formula_editor_parse_fail)));
		solo.sleep(500);
		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Changes saved message not found!",
				solo.searchText(solo.getString(R.string.formula_editor_changes_discarded)));

		solo.goBack();
		solo.goBack();

	}

	public void testOnTheFlyUpdateOfBrickEditText() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("1");

		assertEquals("Wrong text in FormulaEditor", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());
		assertEquals("Wrong text in X EditText", "1 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		catKeyboardClicker.clickOnKey("2");

		assertEquals("Wrong text in FormulaEditor", "12 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());
		assertEquals("Wrong text in X EditText", "12 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.goBack();
		solo.sleep(50);
		assertEquals("Wrong text in X EditText", "12 ", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());
	}

	public void testUndo() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clearEditTextWithCursorBehindLastCharacterOnlyQuickly(FORMULA_EDITOR_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("-");
		catKeyboardClicker.clickOnKey("2");
		catKeyboardClicker.clickOnKey("*");
		catKeyboardClicker.switchToFunctionKeyboard();
		catKeyboardClicker.clickOnKey("cos");
		catKeyboardClicker.clickOnKey("sin");
		catKeyboardClicker.clickOnKey("tan");

		assertEquals("Wrong text in field", "1 - 2 * cos( sin( tan( 0 ) ) ) ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		UiTestUtils.clickOnActionBar(solo, R.id.menu_undo);
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 * cos( sin( 0 ) ) ",
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		UiTestUtils.clickOnActionBar(solo, R.id.menu_undo);
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 * cos( 0 ) ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		UiTestUtils.clickOnActionBar(solo, R.id.menu_undo);
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 * ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		UiTestUtils.clickOnActionBar(solo, R.id.menu_undo);
		solo.sleep(50);
		assertEquals("Undo did something wrong", "1 - 2 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		solo.goBack();

		assertEquals("Undo did something wrong", "1 - 2", solo.getEditText(X_POS_EDIT_TEXT_ID).getText().toString());

		solo.goBack();
		solo.goBack();

	}

	public void testUndoRedo() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		//catKeyboardClicker.clearEditTextWithOnlyNumbersQuickly(FORMULA_EDITOR_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("9");
		catKeyboardClicker.clickOnKey("-");
		catKeyboardClicker.clickOnKey("8");
		catKeyboardClicker.clickOnKey("*");
		catKeyboardClicker.clickOnKey("7");
		catKeyboardClicker.clickOnKey("+");
		catKeyboardClicker.clickOnKey("9");

		assertEquals("Wrong text in field", "9 - 8 * 7 + 9 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText()
				.toString());

		for (int i = 0; i < 7; i++) {
			UiTestUtils.clickOnActionBar(solo, R.id.menu_undo);
		}

		solo.sleep(50);
		assertEquals("Undo did something wrong", INITIAL_X + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		UiTestUtils.clickOnActionBar(solo, R.id.menu_undo);

		assertEquals("Undo did something wrong", INITIAL_X + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		for (int i = 0; i < 7; i++) {
			UiTestUtils.clickOnActionBar(solo, R.id.menu_redo);
		}

		solo.sleep(50);
		assertEquals("Undo did something wrong", "9 - 8 * 7 + 9 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.goBack();

	}

	public void testUndoLimit() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		int maxHistoryElements = (Integer) UiTestUtils.getPrivateField("MAXIMUM_HISTORY_LENGTH",
				new FormulaEditorHistory(null));
		catKeyboardClicker.clickOnKey("del");
		catKeyboardClicker.clickOnKey("1");

		String searchString = "";
		for (int i = 0; i < maxHistoryElements; i++) {
			catKeyboardClicker.clickOnKey("+");
			searchString += " +";
		}
		solo.sleep(50);

		assertEquals("Wrong text in field", "1" + searchString + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		for (int i = 0; i < maxHistoryElements + 2; i++) {
			UiTestUtils.clickOnActionBar(solo, R.id.menu_undo);
		}

		assertEquals("Wrong text in field", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		for (int i = 0; i < maxHistoryElements + 2; i++) {
			UiTestUtils.clickOnActionBar(solo, R.id.menu_redo);
		}

		assertEquals("Wrong text in field", "1" + searchString + " ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.goBack();

	}

	public void testKeyboardSwipeAndSwipeBar() {
		DisplayMetrics currentDisplayMetrics = new DisplayMetrics();
		solo.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(currentDisplayMetrics);

		int displayWidth = currentDisplayMetrics.widthPixels;
		int displayHeight = currentDisplayMetrics.heightPixels;

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("1");
		assertEquals("Wrong keyboard after keyboard switch", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(10, displayWidth - 10, displayHeight - 50, displayHeight - 50, 10);
		catKeyboardClicker.clickOnKey("sin");
		String sinus = getActivity().getString(R.string.formula_editor_function_sin) + "( 0 ) ";
		assertEquals("Wrong keyboard after keyboard switch", sinus, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(10, displayWidth - 10, displayHeight - 50, displayHeight - 50, 10);
		catKeyboardClicker.clickOnKey("x-accel");
		String x_acceleration = getActivity().getString(R.string.formula_editor_sensor_x_acceleration) + " ";
		assertEquals("Wrong keyboard after keyboard switch", x_acceleration,
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(displayWidth - 10, 10, displayHeight - 50, displayHeight - 50, 10);
		catKeyboardClicker.clickOnKey("sin");
		assertEquals("Wrong keyboard after keyboard switch", sinus, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(displayWidth - 10, 10, displayHeight - 50, displayHeight - 50, 10);
		catKeyboardClicker.clickOnKey("1");
		assertEquals("Wrong keyboard after keyboard switch", "1 ", solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());
		catKeyboardClicker.clearEditTextPortraitModeOnlyQuickly(X_POS_EDIT_TEXT_ID);

		solo.drag(displayWidth - 10, 10, displayHeight - 50, displayHeight - 50, 10);
		catKeyboardClicker.clickOnKey("x-accel");
		assertEquals("Wrong keyboard after keyboard switch", x_acceleration,
				solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID).getText().toString());

		solo.goBack();

	}

	public void testSimpleInterpretation() {
		String newXFormula = "10 + 12 - 2 * 3 - 4 ";
		int newXValue = 10 + 12 - 2 * 3 - 4;
		String newYFormula = getActivity().getString(R.string.formula_editor_function_rand) + "( "
				+ getActivity().getString(R.string.formula_editor_function_cos) + "( 90 - - 30 ) , 1 ) ";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clearEditTextWithCursorBehindLastCharacterOnlyQuickly(FORMULA_EDITOR_EDIT_TEXT_ID);

		solo.sleep(50);
		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("0");
		catKeyboardClicker.clickOnKey("+");
		catKeyboardClicker.clickOnKey("1");
		catKeyboardClicker.clickOnKey("2");
		catKeyboardClicker.clickOnKey("-");
		catKeyboardClicker.clickOnKey("2");
		catKeyboardClicker.clickOnKey("*");
		catKeyboardClicker.clickOnKey("3");
		catKeyboardClicker.clickOnKey("-");
		catKeyboardClicker.clickOnKey("4");
		solo.goBack();
		assertTrue("Save failed toast not found",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("rand");
		catKeyboardClicker.switchToFunctionKeyboard();
		catKeyboardClicker.clickOnKey("cos");
		catKeyboardClicker.switchToNumberKeyboard();
		catKeyboardClicker.clickOnKey("9");
		catKeyboardClicker.clickOnKey("0");
		catKeyboardClicker.clickOnKey("-");
		catKeyboardClicker.clickOnKey("-");
		catKeyboardClicker.clickOnKey("3");
		catKeyboardClicker.clickOnKey("0");

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		assertTrue("Changes saved toast not found",
				solo.searchText(solo.getString(R.string.formula_editor_changes_saved)));

		assertEquals("Wrong text in FormulaEditor", newXFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.sleep(50);
		assertEquals("Wrong text in FormulaEditor", newYFormula, solo.getEditText(FORMULA_EDITOR_EDIT_TEXT_ID)
				.getText().toString());

		solo.goBack();
		solo.sleep(300);

		//Interpretation test
		Formula formula = (Formula) UiTestUtils.getPrivateField("xPosition", placeAtBrick);
		assertEquals("Wrong text in field", newXValue, formula.interpretInteger());

		formula = (Formula) UiTestUtils.getPrivateField("yPosition", placeAtBrick);

		float newYValue = formula.interpretFloat();
		assertTrue("Wrong text in field", newYValue >= -0.5f && newYValue <= 1f);

	}

	public void testRandomInterpretationWithFloatParameters() {

		String newXFormula = "rand(9.9,1)";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("rand");
		catKeyboardClicker.clickOnKey("9");
		catKeyboardClicker.clickOnKey(".");
		catKeyboardClicker.clickOnKey("9");

		solo.goBack();
		solo.sleep(300);

		Formula formula = (Formula) UiTestUtils.getPrivateField("xPosition", placeAtBrick);
		float value = formula.interpretFloat();

		Log.i("info", "value: " + value);

		assertTrue("random() interpretation of float parameter is wrong: " + newXFormula + " value=" + value,
				1 <= value && value <= 9.9f && (Math.abs(value) - (int) Math.abs(value)) > 0);

		String newYFormula = "rand(7.0,1)";

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("rand");
		catKeyboardClicker.clickOnKey("7");
		catKeyboardClicker.clickOnKey(".");
		catKeyboardClicker.clickOnKey("0");

		solo.goBack();
		solo.sleep(300);

		Formula anotherFormula = (Formula) UiTestUtils.getPrivateField("yPosition", placeAtBrick);
		float anotherValue = anotherFormula.interpretFloat();

		Log.i("info", "value: " + value);

		assertTrue("random() interpretation of float parameter is wrong: " + newYFormula + " anotherValue="
				+ anotherValue,
				1 <= anotherValue && anotherValue <= 7.0f
						&& (Math.abs(anotherValue) - (int) Math.abs(anotherValue)) > 0);

	}

	public void testRandomInterpretationWithIntegerParameters() {

		String newXFormula = "rand(rand(3),1)";

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("rand");
		catKeyboardClicker.clickOnKey("rand");
		catKeyboardClicker.clickOnKey("3");

		solo.goBack();
		solo.sleep(300);

		Formula formula = (Formula) UiTestUtils.getPrivateField("xPosition", placeAtBrick);
		float value = formula.interpretFloat();

		Log.i("info", "value: " + value);

		assertTrue("random() interpretation of integer parameters is wrong: " + newXFormula + " anotherValue=" + value,
				(value == 1 || value == 2 || value == 3));
		assertEquals("random() interpretation of integer parameters is wrong: " + newXFormula + " anotherValue="
				+ value, 0, Math.abs(value) - (int) Math.abs(value), 0);

		String newYFormula = "rand(4,1)";

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("rand");
		catKeyboardClicker.clickOnKey("4");

		solo.goBack();
		solo.sleep(300);

		Formula anotherFormula = (Formula) UiTestUtils.getPrivateField("yPosition", placeAtBrick);
		float anotherValue = anotherFormula.interpretFloat();

		Log.i("info", "anotherValue: " + anotherValue);

		assertTrue("random() interpretation of integer parameters is wrong: " + newYFormula + " anotherValue="
				+ anotherValue, (anotherValue == 1 || anotherValue == 2 || anotherValue == 3 || anotherValue == 4));
		assertEquals("random() interpretation of integer parameters is wrong: " + newYFormula + " anotherValue="
				+ anotherValue, 0, Math.abs(anotherValue) - (int) Math.abs(anotherValue), 0);

	}

	public void testIfLandscapeOrientationIsDeactivated() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		//		solo.setActivityOrientation(Solo.LANDSCAPE);
		//		int orientation = getActivity().getResources().getConfiguration().orientation;

		int orientation = getActivity().getRequestedOrientation();

		assertTrue("Landscape Orientation isn't deactivated", orientation == Solo.PORTRAIT);

	}

	public void testGoBackAndEditTextSwitches() {

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("6");
		catKeyboardClicker.clickOnKey("-");
		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		solo.goBack();

		boolean isFound = solo.searchText("6") && solo.searchText("-");
		assertTrue("6 or - is/are not found!", isFound);

		catKeyboardClicker.clickOnKey("+");
		catKeyboardClicker.clickOnKey("3");

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		catKeyboardClicker.clickOnKey("5");
		catKeyboardClicker.clickOnKey("+");
		solo.goBack();
		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);

		catKeyboardClicker.clickOnKey("-");
		catKeyboardClicker.clickOnKey("4");

		solo.clickOnEditText(X_POS_EDIT_TEXT_ID);
		isFound = solo.searchText("6") && solo.searchText("+") && solo.searchText("3");
		assertTrue("6 + 3 not found!", isFound);

		solo.clickOnEditText(Y_POS_EDIT_TEXT_ID);
		isFound = solo.searchText("5") && solo.searchText("-") && solo.searchText("4");
		assertTrue("5 - 4 not found!", isFound);

	}
}

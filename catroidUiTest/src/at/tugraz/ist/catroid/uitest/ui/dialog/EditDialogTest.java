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

package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class EditDialogTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;

	public EditDialogTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	public void testIntegerDialog() {
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_place_at);

		int xPosition = 5;
		int yPosition = 7;

		int yPositionEditTextId = solo.getCurrentEditTexts().size() - 1;
		int xPositionEditTextId = yPositionEditTextId - 1;

		UiTestUtils.insertIntegerIntoEditText(solo, xPositionEditTextId, xPosition);
		UiTestUtils.insertIntegerIntoEditText(solo, yPositionEditTextId, yPosition);

		assertEquals(xPosition + "", solo.getEditText(xPositionEditTextId).getText().toString());
		assertEquals(yPosition + "", solo.getEditText(yPositionEditTextId).getText().toString());
	}

	public void testDoubleDialog() {
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_scale_costume);

		double scale = 5.9;

		int scaleEditTextId = solo.getCurrentEditTexts().size() - 1;

		UiTestUtils.insertDoubleIntoEditText(solo, scaleEditTextId, scale);

		assertEquals(scale + "", solo.getEditText(scaleEditTextId).getText().toString());
	}

	public void testEmptyEditDoubleDialog() {
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_scale_costume);

		int editTextId = solo.getCurrentEditTexts().size() - 1;

		solo.clickOnEditText(editTextId);
		UiTestUtils.pause();
		System.out.println(solo.getCurrentEditTexts().size());
		solo.clearEditText(0);
		assertTrue("Toast with warning was not found",
				solo.searchText(getActivity().getString(R.string.notification_invalid_text_entered)));
		assertFalse(solo.getButton(0).isEnabled());

		solo.enterText(0, ".");
		assertFalse(solo.getButton(0).isEnabled());
	}

	public void testEmptyEditIntegerDialog() {
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_place_at);

		int editTextId = solo.getCurrentEditTexts().size() - 1;

		solo.clickOnEditText(editTextId);
		UiTestUtils.pause();
		solo.clearEditText(0);
		assertTrue("Toast with warning was not found",
				solo.searchText(getActivity().getString(R.string.notification_invalid_text_entered)));
		assertFalse(solo.getButton(0).isEnabled());
	}
}

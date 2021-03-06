/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.KeyEvent;
import org.catrobat.catroid.R;

import com.jayway.android.robotium.solo.Solo;

public class ChangeBrightnessByNBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private static final double BRIGHTNESS_TO_CHANGE = 56.6;

	private Solo solo;
	private Project project;
	private ChangeBrightnessByNBrick changeBrightnessByNBrick;

	public ChangeBrightnessByNBrickTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testChangeBrightnessByNBrick() {
		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		ScriptFragment fragment = (ScriptFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SCRIPTS);
		BrickAdapter adapter = fragment.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2 + 1, solo.getCurrentListViews().get(0).getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist",
				solo.getText(solo.getString(R.string.brick_change_brightness)));

		solo.clickOnEditText(0);
		solo.sleep(500);
		assertTrue("Dialog is not visible", solo.searchText(solo.getString(R.string.ok)));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(300);
		assertTrue("Dialog is not visible", solo.searchText(solo.getString(R.string.ok)));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(300);
		assertTrue("Dialog is not visible", solo.searchText(solo.getString(R.string.ok)));
		solo.clearEditText(0);
		solo.enterText(0, BRIGHTNESS_TO_CHANGE + "");
		solo.sleep(500);
		solo.sendKey(KeyEvent.KEYCODE_ENTER);
		solo.sleep(1000);

		assertEquals("Wrong text in field", BRIGHTNESS_TO_CHANGE, changeBrightnessByNBrick.getChangeBrightness());
		assertEquals("Text not updated", BRIGHTNESS_TO_CHANGE,
				Double.parseDouble(solo.getEditText(0).getText().toString()));
	}

	public void testResizeInputField() {
		UiTestUtils.testDoubleEditText(solo, 0, 1.0, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 100.55, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, -0.1, 60, true);
		UiTestUtils.testDoubleEditText(solo, 0, 1000.55, 60, false);
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		changeBrightnessByNBrick = new ChangeBrightnessByNBrick(sprite, 10.2);
		script.addBrick(changeBrightnessByNBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}

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
package org.catrobat.catroid.uitest.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptTabActivity;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class LookFragmentTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private final int RESOURCE_IMAGE = org.catrobat.catroid.uitest.R.drawable.catroid_sunglasses;
	private final int RESOURCE_IMAGE2 = R.drawable.catroid_banzai;

	private ProjectManager projectManager = ProjectManager.getInstance();
	private Solo solo;
	private String lookName = "lookNametest";
	private File imageFile;
	private File imageFile2;
	private File paintroidImageFile;
	private ArrayList<LookData> lookDataList;

	public LookFragmentTest() {
		super(MainMenuActivity.class);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setUp() throws Exception {
		super.setUp();

		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();

		imageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
				RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		imageFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_banzai.png",
				RESOURCE_IMAGE2, getActivity(), UiTestUtils.FileTypes.IMAGE);

		paintroidImageFile = UiTestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/testFile.png",
				R.drawable.catroid_banzai, getActivity());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		LookData lookData = new LookData();
		lookData.setLookFilename(imageFile.getName());
		lookData.setLookName(lookName);
		lookDataList.add(lookData);
		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());
		lookData = new LookData();
		lookData.setLookFilename(imageFile2.getName());
		lookData.setLookName("lookNameTest2");
		lookDataList.add(lookData);
		projectManager.getFileChecksumContainer().addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		projectManager.getCurrentProject().virtualScreenWidth = display.getWidth();
		projectManager.getCurrentProject().virtualScreenHeight = display.getHeight();

		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		paintroidImageFile.delete();
		super.tearDown();
		solo = null;
	}

	public void testAddNewLookActionbarIcon() {
		goToLooksTab();
		String addLookFromCameraText = solo.getString(R.string.add_look_from_camera);
		String addLookFromGalleryText = solo.getString(R.string.add_look_from_gallery);
		assertFalse("Menu to add look from camera should not be visible", solo.searchText(addLookFromCameraText));
		assertFalse("Menu to add look from gallery should not be visible",
				solo.searchText(addLookFromGalleryText));
		UiTestUtils.clickOnActionBar(solo, R.id.menu_add);
		assertTrue("Menu to add look from camera was not visible visible", solo.searchText(addLookFromCameraText));
		assertTrue("Menu to add look from gallery was not visible", solo.searchText(addLookFromGalleryText));
		solo.goBack();
	}

	public void testAddNewLookDialog() {
		// currently disabled, until at least 2 camera apps are installed on testdevice
		// otherwise test would timeout, because chooser is not shown
		// String addLookFromCameraDialogTitle = solo.getString(R.string.select_look_from_camera);
		String addLookFromGalleryDialogTitle = solo.getString(R.string.select_look_from_gallery);

		goToLooksTab();
		solo.clickOnText(solo.getString(R.string.delete_lowercase));
		solo.clickOnButton(0);
		solo.sleep(300);

		// see comment at the top of the method
		//		solo.clickOnView(solo.getView(R.id.view_camera_non_scrollable));
		//		solo.waitForText(addLookFromCameraDialogTitle, 0, 1000);
		//		assertTrue("Dialog to add new look from camera did not appear",
		//				solo.searchText(addLookFromCameraDialogTitle));
		//		solo.goBack();
		//		solo.sleep(200);
		//		solo.clickOnView(solo.getView(R.id.looklist_footerview_camera));
		//		solo.waitForText(addLookFromCameraDialogTitle, 0, 1000);
		//		assertTrue("Dialog to add new look from camera did not appear",
		//				solo.searchText(addLookFromCameraDialogTitle));
		//		solo.goBack();
		//		solo.sleep(200);
		//		solo.clickOnView(solo.getView(R.id.looklist_footerview_camera_add_image));
		//		solo.waitForText(addLookFromCameraDialogTitle, 0, 1000);
		//		assertTrue("Dialog to add new look from camera did not appear",
		//				solo.searchText(addLookFromCameraDialogTitle));
		//		solo.goBack();
		//		solo.sleep(200);

		solo.clickOnView(solo.getView(R.id.view_gallery_non_scrollable));
		solo.waitForText(addLookFromGalleryDialogTitle, 0, 1000);
		assertTrue("Dialog to add new look from gallery did not appear",
				solo.searchText(addLookFromGalleryDialogTitle));
		solo.goBack();
		solo.sleep(200);

		solo.clickOnView(solo.getView(R.id.looklist_footerview_gallery));
		solo.waitForText(addLookFromGalleryDialogTitle, 0, 1000);
		assertTrue("Dialog to add new look from gallery did not appear",
				solo.searchText(addLookFromGalleryDialogTitle));
		solo.goBack();
		solo.sleep(200);
		solo.clickOnView(solo.getView(R.id.looklist_footerview_gallery_add_image));
		solo.waitForText(addLookFromGalleryDialogTitle, 0, 1000);
		assertTrue("Dialog to add new look from gallery did not appear",
				solo.searchText(addLookFromGalleryDialogTitle));
		solo.goBack();
		solo.sleep(200);
	}

	public void testCopyLook() {
		goToLooksTab();
		solo.clickOnText(solo.getString(R.string.copy_look), 1);
		if (solo.searchText(lookName + "_" + solo.getString(R.string.copy_look_addition), 1, true)) {
			assertEquals("the copy of the look wasn't added to the lookDataList in the sprite", 3,
					lookDataList.size());
		} else {
			fail("copy look didn't work");
		}
	}

	public void testDeleteLook() {
		goToLooksTab();
		ListAdapter adapter = getLookFragment().getListAdapter();

		int oldCount = adapter.getCount();
		solo.clickOnButton(solo.getString(R.string.delete_lowercase));
		solo.sleep(200);
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(300);
		int newCount = adapter.getCount();
		assertEquals("the old count was not right", 2, oldCount);
		assertEquals("the new count is not right - all looks should be deleted", 1, newCount);
		assertEquals("the count of the lookDataList is not right", 1, lookDataList.size());
	}

	public void testDeleteLookFile() {
		goToLooksTab();

		Sprite firstSprite = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0);
		LookData lookToDelete = firstSprite.getLookDataList().get(1);

		solo.clickOnText(solo.getString(R.string.delete_lowercase), 2);
		String buttonPositive = solo.getString(R.string.ok);
		solo.clickOnText(buttonPositive);

		File deletedFile = new File(lookToDelete.getAbsolutePath());
		assertFalse("File should be deleted", deletedFile.exists());
	}

	public void testRenameLook() {
		String newName = "newName";
		goToLooksTab();
		solo.clickOnView(solo.getView(R.id.look_name));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(200);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(100);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(newName));
		solo.sleep(100);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(200);
		solo.sendKey(Solo.ENTER);
		solo.sleep(200);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		assertEquals("look is not renamed in LookList", newName, lookDataList.get(0).getLookName());
		if (!solo.searchText(newName)) {
			fail("look not renamed in actual view");
		}
	}

	public void testRenameLookMixedCase() {
		goToLooksTab();
		solo.clickOnView(solo.getView(R.id.look_name));
		solo.sleep(300);
		solo.clearEditText(0);
		String newNameMixedCase = "coSTuMeNamEtESt";
		solo.enterText(0, newNameMixedCase);
		solo.sendKey(Solo.ENTER);
		solo.sleep(100);
		assertEquals("Look is not renamed to Mixed Case", newNameMixedCase, lookDataList.get(0).getLookName());
		if (!solo.searchText(newNameMixedCase)) {
			fail("look not renamed in actual view");
		}
	}

	public void testMainMenuButton() {
		goToLooksTab();
		UiTestUtils.clickOnUpActionBarButton(solo.getCurrentActivity());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		solo.assertCurrentActivity("Clicking on main menu button did not cause main menu to be displayed",
				MainMenuActivity.class);
	}

	public void testDialogsOnChangeOrientation() {
		String newName = "newTestName";
		goToLooksTab();
		solo.clickOnView(solo.getView(R.id.look_name));
		assertTrue("Dialog is not visible", solo.searchText(solo.getString(R.string.ok)));
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(100);
		assertTrue("Dialog is not visible", solo.searchText(solo.getString(R.string.ok)));
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(100);
		solo.clearEditText(0);
		solo.enterText(0, newName);
		solo.setActivityOrientation(Solo.LANDSCAPE);
		solo.sleep(100);
		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.sleep(300);
		assertTrue("EditText field got cleared after changing orientation", solo.searchText(newName));
		solo.goBack();
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(200);
		assertTrue("Look wasnt renamed", solo.searchText(newName));
	}

	public void testGetImageFromPaintroid() {
		goToLooksTab();
		String checksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertTrue("Testfile not added from mockActivity", solo.searchText("testFile"));
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumPaintroidImageFile));

		boolean isInLookDataList = false;
		for (LookData lookData : projectManager.getCurrentSprite().getLookDataList()) {
			if (lookData.getChecksum().equalsIgnoreCase(checksumPaintroidImageFile)) {
				isInLookDataList = true;
			}
		}
		if (!isInLookDataList) {
			fail("File not added in LookDataList");
		}
	}

	public void testEditImageWithPaintroid() {
		goToLooksTab();
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);

		String md5PaintroidImage = Utils.md5Checksum(paintroidImageFile);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);

		solo.sleep(5000);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertNotSame("Picture was not changed", Utils.md5Checksum(new File(lookData.getAbsolutePath())),
				md5PaintroidImage);

		boolean isInLookDataListPaintroidImage = false;
		boolean isInLookDataListSunnglasses = false;
		for (LookData lookDatas : projectManager.getCurrentSprite().getLookDataList()) {
			if (lookDatas.getChecksum().equalsIgnoreCase(md5PaintroidImage)) {
				isInLookDataListPaintroidImage = true;
			}
			if (lookDatas.getChecksum().equalsIgnoreCase(md5ImageFile)) {
				isInLookDataListSunnglasses = true;
			}
		}
		assertTrue("File not added in LookDataList", isInLookDataListPaintroidImage);
		assertFalse("File not deleted from LookDataList", isInLookDataListSunnglasses);
	}

	public void testEditImageWithPaintroidNoChanges() {
		goToLooksTab();
		int numberOfLookDatas = lookDataList.size();
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertEquals("Picture changed", Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ImageFile);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		int newNumberOfLookDatas = lookDataList.size();
		assertEquals("LookData was added", numberOfLookDatas, newNumberOfLookDatas);

		assertEquals("too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testEditImageWithPaintroidNoPath() {
		goToLooksTab();
		int numberOfLookDatas = lookDataList.size();
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertEquals("Picture changed", Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ImageFile);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		int newNumberOfLookDatas = lookDataList.size();
		assertEquals("LookData was added", numberOfLookDatas, newNumberOfLookDatas);
		assertEquals("too many references for checksum", 1,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testGetImageFromPaintroidNoPath() {
		goToLooksTab();
		LookData lookData = lookDataList.get(0);
		String md5ImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString("thirdExtra", "doesn't matter");
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		int numberOfLookDatas = lookDataList.size();
		assertEquals("wrong size of lookdatalist", 2, numberOfLookDatas);
		assertEquals("Picture changed", Utils.md5Checksum(new File(lookData.getAbsolutePath())), md5ImageFile);
	}

	public void testGetImageFromGallery() {
		goToLooksTab();
		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidImageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(200);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		assertTrue("Testfile not added from mockActivity", solo.searchText("testFile"));

		String checksumPaintroidImageFile = Utils.md5Checksum(paintroidImageFile);
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumPaintroidImageFile));

		boolean isInLookDataList = false;
		for (LookData lookData : projectManager.getCurrentSprite().getLookDataList()) {
			if (lookData.getChecksum().equalsIgnoreCase(checksumPaintroidImageFile)) {
				isInLookDataList = true;
			}
		}
		if (!isInLookDataList) {
			fail("File not added in LookDataList");
		}
	}

	public void testGetImageFromGalleryNullData() {
		goToLooksTab();
		lookDataList = ProjectManager.INSTANCE.getCurrentSprite().getLookDataList();
		int numberOfLookDatasBeforeIntent = lookDataList.size();
		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", paintroidImageFile.getAbsolutePath());
		bundleForGallery.putBoolean("returnNullData", true);
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);
		solo.sleep(2000);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName(), 2000);
		solo.assertCurrentActivity("Test should not fail - should be in ScriptTabActivity",
				ScriptTabActivity.class.getSimpleName());
		lookDataList = ProjectManager.INSTANCE.getCurrentSprite().getLookDataList();
		int numberOfLookDatasAfterReturning = lookDataList.size();
		assertEquals("wrong size of lookdatalist", numberOfLookDatasBeforeIntent,
				numberOfLookDatasAfterReturning);
	}

	public void testEditImagePaintroidToSomethingWhichIsAlreadyUsed() throws IOException {
		goToLooksTab();
		int numberOfLookDatas = lookDataList.size();
		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ImageFile = Utils.md5Checksum(imageFile);
		String md5PaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		solo.sleep(500);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(4000);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertNotSame("Picture did not change", Utils.md5Checksum(new File(lookData.getAbsolutePath())),
				md5ImageFile);
		lookDataList = projectManager.getCurrentSprite().getLookDataList();
		int newNumberOfLookDatas = lookDataList.size();
		assertEquals("LookData was added", numberOfLookDatas, newNumberOfLookDatas);
		assertEquals("too many references for checksum", 0,
				projectManager.getFileChecksumContainer().getUsage(md5ImageFile));
		assertEquals("not the right number of checksum references", 2, projectManager.getFileChecksumContainer()
				.getUsage(md5PaintroidImageFile));
	}

	public void testEditImageWhichIsAlreadyUsed() {
		File tempImageFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME,
				"catroid_sunglasses2.png", RESOURCE_IMAGE, getActivity(), UiTestUtils.FileTypes.IMAGE);
		LookData lookDataToAdd = new LookData();
		lookDataToAdd.setLookFilename(tempImageFile.getName());
		lookDataToAdd.setLookName("justforthistest");
		lookDataList.add(lookDataToAdd);
		projectManager.getFileChecksumContainer().addChecksum(lookDataToAdd.getChecksum(),
				lookDataToAdd.getAbsolutePath());

		solo.sleep(200);
		goToLooksTab();

		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);
		String md5ImageFile = Utils.md5Checksum(imageFile);
		//		String md5PaintroidImageFile = Utils.md5Checksum(paintroidImageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		bundleForPaintroid.putString("secondExtra", imageFile2.getAbsolutePath());

		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		solo.sleep(500);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_PAINTROID_EDIT_IMAGE);
		solo.sleep(4000);
		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());

		assertEquals("wrong number of lookdatas", 3, lookDataList.size());
		assertTrue("new added image has been deleted", tempImageFile.exists());
		assertEquals("wrong number of checksum references of sunnglasses picture", 1, projectManager
				.getFileChecksumContainer().getUsage(md5ImageFile));
	}

	public void testLookNames() {
		goToLooksTab();
		String buttonCopyLookText = solo.getString(R.string.copy_look);
		solo.clickOnText(buttonCopyLookText);
		solo.scrollToTop();

		String defaultLookName = solo.getString(R.string.default_look_name);
		String expectedLookName = "";

		renameLook(lookName, defaultLookName);
		renameLook("lookNameTest2", defaultLookName);
		expectedLookName = defaultLookName + "1";
		assertEquals("look not renamed correctly", expectedLookName, lookDataList.get(1).getLookName());
		renameLook("lookNametest_", defaultLookName);
		expectedLookName = defaultLookName + "2";
		assertEquals("look not renamed correctly", expectedLookName, lookDataList.get(2).getLookName());

		renameLook(defaultLookName + "1", "a");
		solo.scrollToTop();
		solo.clickOnText(solo.getString(R.string.copy_look));
		renameLook(defaultLookName + "_", defaultLookName);
		expectedLookName = defaultLookName + "1";
		assertEquals("look not renamed correctly", expectedLookName, lookDataList.get(3).getLookName());

		// test that Image from paintroid is correctly renamed
		String fileName = defaultLookName;
		try {
			imageFile = UiTestUtils.createTestMediaFile(Utils.buildPath(Constants.DEFAULT_ROOT, fileName + ".png"),
					RESOURCE_IMAGE2, getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Image was not created");
		}
		String checksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForPaintroid = new Bundle();
		bundleForPaintroid.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockPaintroidActivity.class);
		intent.putExtras(bundleForPaintroid);
		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);

		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.sleep(5000);
		expectedLookName = defaultLookName + "3";
		assertEquals("look not renamed correctly", expectedLookName, lookDataList.get(4).getLookName());
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumImageFile));

		// test that Image from gallery is correctly renamed
		fileName = defaultLookName;
		try {
			imageFile = UiTestUtils.createTestMediaFile(Utils.buildPath(Constants.DEFAULT_ROOT, fileName + ".png"),
					RESOURCE_IMAGE, getActivity());
		} catch (IOException e) {
			e.printStackTrace();
			fail("Image was not created");
		}
		checksumImageFile = Utils.md5Checksum(imageFile);

		Bundle bundleForGallery = new Bundle();
		bundleForGallery.putString("filePath", imageFile.getAbsolutePath());
		intent = new Intent(getInstrumentation().getContext(),
				org.catrobat.catroid.uitest.mockups.MockGalleryActivity.class);
		intent.putExtras(bundleForGallery);

		getLookFragment().startActivityForResult(intent, LookFragment.REQUEST_SELECT_IMAGE);

		solo.waitForActivity(ScriptTabActivity.class.getSimpleName());
		solo.sleep(5000);
		expectedLookName = defaultLookName + "4";
		assertEquals("look not renamed correctly", expectedLookName, lookDataList.get(5).getLookName());
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumImageFile));
	}

	public void testResolutionWhenEditedAndCroppedWithPaintroid() {
		goToLooksTab();

		LookData lookData = lookDataList.get(0);
		getLookFragment().setSelectedLookData(lookData);

		String pathToImageFile = imageFile.getAbsolutePath();
		int[] fileResolutionBeforeCrop = lookData.getResolution();
		int[] displayedResolutionBeforeCrop = getDisplayedResolution(lookData);

		int sampleSize = 2;

		solo.sleep(1000);
		try {
			UiTestUtils.cropImage(pathToImageFile, sampleSize);
		} catch (FileNotFoundException e) {
			fail("Test failed because file was not found");
			e.printStackTrace();
		}

		UiTestUtils.clickOnUpActionBarButton(solo.getCurrentActivity());
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());
		goToLooksTab();

		int[] fileResolutionAfterCrop = lookData.getResolution();
		int[] displayedResolutionAfterCrop = getDisplayedResolution(lookData);

		assertTrue("Bitmap resolution in file was not cropped",
				fileResolutionAfterCrop[0] < fileResolutionBeforeCrop[0]
						&& fileResolutionAfterCrop[1] < fileResolutionBeforeCrop[1]);
		assertTrue("Image resolution was not updated in look fragment",
				displayedResolutionAfterCrop[0] < displayedResolutionBeforeCrop[0]
						&& fileResolutionAfterCrop[1] < displayedResolutionBeforeCrop[1]);
	}

	private int[] getDisplayedResolution(LookData look) {
		TextView resolutionTextView = (TextView) solo.getView(R.id.look_res);
		String resolutionString = resolutionTextView.getText().toString();
		//resolution string has form "width x height"
		int dividingPosition = resolutionString.indexOf(' ');
		String widthString = resolutionString.substring(0, dividingPosition);
		String heightString = resolutionString.substring(dividingPosition + 3, resolutionString.length());
		int width = Integer.parseInt(widthString);
		int heigth = Integer.parseInt(heightString);

		int[] resolution = new int[2];
		resolution[0] = width;
		resolution[1] = heigth;
		return resolution;
	}

	private void renameLook(String currentLookName, String newLookName) {
		solo.clickOnText(currentLookName);
		EditText editTextLookName = solo.getEditText(0);
		solo.clearEditText(editTextLookName);
		solo.enterText(editTextLookName, newLookName);
		String buttonOKText = solo.getCurrentActivity().getString(R.string.ok);
		solo.clickOnButton(buttonOKText);
	}

	private LookFragment getLookFragment() {
		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		return (LookFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_LOOKS);
	}

	private void goToLooksTab() {
		UiTestUtils.getIntoScriptTabActivityFromMainMenu(solo);
		solo.clickOnText(solo.getString(R.string.backgrounds));
		solo.sleep(500);
	}
}

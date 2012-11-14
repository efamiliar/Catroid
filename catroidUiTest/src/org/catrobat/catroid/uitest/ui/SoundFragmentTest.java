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
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.SoundActivity;
import org.catrobat.catroid.ui.adapter.SoundAdapter;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.uitest.mockups.MockSoundActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageButton;

import com.jayway.android.robotium.solo.Solo;

public class SoundFragmentTest extends ActivityInstrumentationTestCase2<SoundActivity> {
	private final int RESOURCE_SOUND = org.catrobat.catroid.uitest.R.raw.longsound;
	private final int RESOURCE_SOUND2 = org.catrobat.catroid.uitest.R.raw.testsoundui;

	private Solo solo;
	private String soundName = "testSound1";
	private String soundName2 = "testSound2";
	private File soundFile;
	private File soundFile2;
	private File externalSoundFile;
	private ArrayList<SoundInfo> soundInfoList;

	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

	private ProjectManager projectManager;

	public SoundFragmentTest() {
		super(SoundActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.clearAllUtilTestProjects();
		UiTestUtils.createTestProject();
		projectManager = ProjectManager.getInstance();
		soundInfoList = projectManager.getCurrentSprite().getSoundList();

		soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "longsound.mp3",
				RESOURCE_SOUND, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(soundName);

		soundFile2 = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "testsoundui.mp3",
				RESOURCE_SOUND2, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo2 = new SoundInfo();
		soundInfo2.setSoundFileName(soundFile2.getName());
		soundInfo2.setTitle(soundName2);

		soundInfoList.add(soundInfo);
		soundInfoList.add(soundInfo2);
		projectManager.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		projectManager.getFileChecksumContainer().addChecksum(soundInfo2.getChecksum(), soundInfo2.getAbsolutePath());

		externalSoundFile = UiTestUtils.createTestMediaFile(Constants.DEFAULT_ROOT + "/externalSoundFile.mp3",
				RESOURCE_SOUND, getActivity());

		solo = new Solo(getInstrumentation(), getActivity());

		boolean showDetails = getSoundAdapter().getShowDetails();
		if (showDetails) {
			clickOnOverflowMenuItem(solo.getString(R.string.hide_details));
			solo.sleep(50);
		}
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		externalSoundFile.delete();
		super.tearDown();
		solo = null;
	}

	public void testInitialLayout() {
		assertFalse("Initially showing details", getSoundAdapter().getShowDetails());
		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
	}

	public void testDeleteSound() {
		SoundAdapter adapter = getSoundAdapter();
		assertNotNull("Could not get Adapter", adapter);

		int oldCount = adapter.getCount();

		clickOnContextMenuItem(soundName2, solo.getString(R.string.delete));
		solo.waitForText(solo.getString(R.string.delete_sound_dialog));
		solo.clickOnButton(solo.getString(R.string.ok));
		solo.sleep(50);

		int newCount = adapter.getCount();

		assertEquals("Old count is not correct", 2, oldCount);
		assertEquals("New count is not correct - one sound should be deleted", 1, newCount);
		assertEquals("Count of the soundList is not right", 1, getActualNumberOfSounds());
	}

	public void testRenameSound() {
		String newSoundName = "TeStSoUNd1";
		renameSound(soundName, newSoundName);
		solo.sleep(50);

		assertEquals("Sound is not renamed in SoundList", newSoundName, getSoundTitle(0));
		assertTrue("Sound not renamed in actual view", solo.searchText(newSoundName));
	}

	public void testEqualSoundNames() {
		renameSound(soundName2, soundName);
		soundInfoList = projectManager.getCurrentSprite().getSoundList();

		String expectedSoundName = soundName + "1";
		String actualSoundName = getSoundTitle(1);
		assertEquals("Sound not renamed correctly", expectedSoundName, actualSoundName);
	}

	public void testShowAndHideDetails() {
		int timeToWait = 200;

		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
		clickOnOverflowMenuItem(solo.getString(R.string.show_details));
		solo.sleep(timeToWait);
		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, VISIBLE, GONE);

		// Test if showDetails is remembered after pressing back
		goToProgramMenuActivity();
		solo.clickOnText(solo.getString(R.string.sounds));
		solo.waitForActivity(SoundActivity.class.getSimpleName());
		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, VISIBLE, GONE);
		solo.sleep(timeToWait);

		clickOnOverflowMenuItem(solo.getString(R.string.hide_details));
		solo.sleep(timeToWait);
		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
	}

	public void testPlayAndStopSound() {
		int timeToWait = 100;

		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		SoundInfo soundInfo = soundInfoList.get(0);
		assertFalse("Mediaplayer is playing although no play button was touched", soundInfo.isPlaying);

		ImageButton playImageButton = (ImageButton) solo.getView(R.id.btn_sound_play);
		ImageButton pauseImageButton = (ImageButton) solo.getView(R.id.btn_sound_pause);

		solo.clickOnView(playImageButton);
		solo.sleep(timeToWait);

		assertTrue("Mediaplayer is not playing although play button was touched", soundInfo.isPlaying);
		checkVisabilityOfViews(GONE, VISIBLE, VISIBLE, VISIBLE, VISIBLE, GONE, GONE);

		solo.clickOnView(pauseImageButton);
		solo.sleep(timeToWait);

		assertFalse("Mediaplayer is playing after touching stop button", soundInfo.isPlaying);
		checkVisabilityOfViews(VISIBLE, GONE, VISIBLE, GONE, VISIBLE, GONE, GONE);
	}

	public void testAddNewSound() {
		int expectedNumberOfSounds = getActualNumberOfSounds() + 1;

		String newSoundName = "Added Sound";
		addNewSound(newSoundName);

		assertEquals("No sound was added", expectedNumberOfSounds, getActualNumberOfSounds());
		assertTrue("Sound not added in actual view", solo.searchText(newSoundName));
	}

	public void testGetSoundFromExternalSource() {
		int expectedNumberOfSounds = getActualNumberOfSounds() + 1;
		String checksumExternalSoundFile = Utils.md5Checksum(externalSoundFile);

		// Use of MockSoundActivity
		Bundle bundleForExternalSource = new Bundle();
		bundleForExternalSource.putString("filePath", externalSoundFile.getAbsolutePath());
		Intent intent = new Intent(getInstrumentation().getContext(), MockSoundActivity.class);
		intent.putExtras(bundleForExternalSource);

		getSoundFragment().startActivityForResult(intent, SoundFragment.REQUEST_SELECT_MUSIC);
		solo.sleep(1000);
		solo.waitForActivity(SoundActivity.class.getSimpleName());
		solo.assertCurrentActivity("Should be in SoundActivity", SoundActivity.class.getSimpleName());

		assertTrue("External file not added from mockActivity", solo.searchText("externalSoundFile"));
		assertTrue("Checksum not in checksumcontainer",
				projectManager.getFileChecksumContainer().containsChecksum(checksumExternalSoundFile));

		boolean isInSoundInfoList = false;
		for (SoundInfo soundInfo : projectManager.getCurrentSprite().getSoundList()) {
			if (soundInfo.getChecksum().equalsIgnoreCase(checksumExternalSoundFile)) {
				isInSoundInfoList = true;
			}
		}
		if (!isInSoundInfoList) {
			fail("File not added in SoundInfoList");
		}

		checkIfNumberOfSoundsIsEqual(expectedNumberOfSounds);
	}

	private void addNewSound(String title) {
		soundFile = UiTestUtils.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "longsound.mp3",
				RESOURCE_SOUND, getInstrumentation().getContext(), UiTestUtils.FileTypes.SOUND);
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setSoundFileName(soundFile.getName());
		soundInfo.setTitle(title);

		soundInfoList.add(soundInfo);
		projectManager.getFileChecksumContainer().addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
		projectManager.saveProject();
	}

	private void renameSound(String soundToRename, String newSoundName) {
		clickOnContextMenuItem(soundToRename, solo.getString(R.string.rename));
		assertTrue("Wrong title of dialog", solo.searchText(solo.getString(R.string.rename_sound_dialog)));
		assertTrue("No EditText with actual soundname", solo.searchEditText(soundToRename));

		UiTestUtils.enterText(solo, 0, newSoundName);
		solo.sendKey(Solo.ENTER);
	}

	private SoundFragment getSoundFragment() {
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		return (SoundFragment) fragmentManager.findFragmentById(R.id.fr_sound);
	}

	private SoundAdapter getSoundAdapter() {
		return (SoundAdapter) getSoundFragment().getListAdapter();
	}

	private void checkVisabilityOfViews(int playButtonVisibility, int pauseButtonVisibility, int soundNameVisibility,
			int timePlayedVisibility, int soundDurationVisibility, int soundSizeVisibility, int checkBoxVisibility) {
		assertTrue("Play button " + getAssertMessageAffix(playButtonVisibility), solo.getView(R.id.btn_sound_play)
				.getVisibility() == playButtonVisibility);
		assertTrue("Pause button " + getAssertMessageAffix(pauseButtonVisibility), solo.getView(R.id.btn_sound_pause)
				.getVisibility() == pauseButtonVisibility);
		assertTrue("Sound name " + getAssertMessageAffix(soundNameVisibility), solo.getView(R.id.sound_title)
				.getVisibility() == soundNameVisibility);
		assertTrue("Chronometer " + getAssertMessageAffix(timePlayedVisibility),
				solo.getView(R.id.sound_chronometer_time_played).getVisibility() == timePlayedVisibility);
		assertTrue("Sound duration " + getAssertMessageAffix(soundDurationVisibility), solo
				.getView(R.id.sound_duration).getVisibility() == soundDurationVisibility);
		assertTrue("Sound size " + getAssertMessageAffix(soundSizeVisibility), solo.getView(R.id.sound_size)
				.getVisibility() == soundSizeVisibility);
		assertTrue("Checkboxes " + getAssertMessageAffix(checkBoxVisibility), solo.getView(R.id.checkbox)
				.getVisibility() == checkBoxVisibility);
	}

	private String getAssertMessageAffix(int visibility) {
		String assertMessageAffix = "";
		switch (visibility) {
			case View.VISIBLE:
				assertMessageAffix = "not visible";
				break;
			case View.GONE:
				assertMessageAffix = "not gone";
				break;
			default:
				break;
		}
		return assertMessageAffix;
	}

	private void clickOnContextMenuItem(String soundName, String itemName) {
		solo.clickLongOnText(soundName);
		solo.waitForText(itemName);
		solo.clickOnText(itemName);
	}

	private void clickOnOverflowMenuItem(String itemName) {
		solo.clickOnImageButton(0);
		solo.waitForText(itemName);
		solo.clickOnText(itemName);
	}

	private void goToProgramMenuActivity() {
		Activity activity = getActivity();
		Intent intent = new Intent(activity, ProgramMenuActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
	}

	private void checkIfNumberOfSoundsIsEqual(int numberToCompare) {
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		assertEquals("Number of sounds is not as expected", numberToCompare, soundInfoList.size());
	}

	private int getActualNumberOfSounds() {
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		return soundInfoList.size();
	}

	private String getSoundTitle(int soundIndex) {
		soundInfoList = projectManager.getCurrentSprite().getSoundList();
		return soundInfoList.get(soundIndex).getTitle();
	}
}

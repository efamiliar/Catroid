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
package at.tugraz.ist.catroid.test.utiltests;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.utils.UtilFile;

public class UtilFileTest extends InstrumentationTestCase {
	private File testDirectory;
	private File subDirectory;
	private File file1;
	private File file2;

	@Override
	protected void setUp() throws Exception {
		final String catroidDirectory = "/sdcard/catroid";
		UtilFile.deleteDirectory(new File(catroidDirectory + "/testDirectory"));

		testDirectory = new File(catroidDirectory + "/testDirectory");
		testDirectory.mkdir();
		file1 = new File(testDirectory.getAbsolutePath() + "/file1");
		file1.createNewFile();
		subDirectory = new File(testDirectory.getAbsolutePath() + "/subDirectory");
		subDirectory.mkdir();
		file2 = new File(subDirectory.getAbsolutePath() + "/file2");
		file2.createNewFile();

		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		UtilFile.deleteDirectory(testDirectory);
	}

	public void testClearDirectory() {
		UtilFile.clearDirectory(testDirectory);
		assertFalse("File in subdirectory still exists after call to clearDirectory", file2.exists());
		assertFalse("Subdirectory in test directory still exists after call to clearDirectory", subDirectory.exists());
		assertFalse("File in test directory still exists after call to clearDirectory", file1.exists());
		assertTrue("Directory itself was deleted as well after call to clearDirectory", testDirectory.exists());
	}

	public void testDeleteDirectory() {
		UtilFile.deleteDirectory(testDirectory);
		assertFalse("File in subdirectory still exists after call to deleteDirectory", file2.exists());
		assertFalse("Subdirectory in test directory still exists after call to deleteDirectory", subDirectory.exists());
		assertFalse("File in test directory still exists after call to deleteDirectory", file1.exists());
		assertFalse("Test directory still exists after call to deleteDirectory", testDirectory.exists());
	}

	public void testFileSize() throws IOException {

		for (int i = 0; i < 2; i++) {
			UtilFile.saveFileToProject("testDirectory", i + "testsound.mp3",
					at.tugraz.ist.catroid.test.R.raw.longtestsound, getInstrumentation().getContext(),
					UtilFile.TYPE_SOUND_FILE);
		}
		assertEquals("the byte count is not correct", 86188, UtilFile.getSizeOfDirectoryInByte(testDirectory));
		assertEquals("not the expected string", "84 KB", UtilFile.getSizeAsString(testDirectory));

		for (int i = 2; i < 48; i++) {
			UtilFile.saveFileToProject("testDirectory", i + "testsound.mp3",
					at.tugraz.ist.catroid.test.R.raw.longtestsound, getInstrumentation().getContext(),
					UtilFile.TYPE_SOUND_FILE);
		}
		assertEquals("the byte count is not correct", 2068512, UtilFile.getSizeOfDirectoryInByte(testDirectory));
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		String expected = decimalFormat.format(1.97) + " MB";
		assertEquals("not the expected string", expected, UtilFile.getSizeAsString(testDirectory));

		UtilFile.deleteDirectory(testDirectory);
	}
}

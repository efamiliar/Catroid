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
package at.tugraz.ist.catroid.test.content.brick;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.media.MediaPlayer;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.content.brick.gui.PlaySoundBrick;
import at.tugraz.ist.catroid.io.sound.SoundManager;
import at.tugraz.ist.catroid.test.R;

public class PlaySoundBrickTest extends InstrumentationTestCase {
	private static final int SOUND_FILE_ID = R.raw.testsound;
	private File soundFile;

	@Override
	protected void setUp() throws Exception {
		// Note: File needs to be copied as MediaPlayer has no access to resources
		BufferedInputStream inputStream = new BufferedInputStream(getInstrumentation().getContext().getResources().openRawResource(SOUND_FILE_ID));
		soundFile = File.createTempFile("audioTest", ".mp3");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(soundFile), 1024);

		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
	}

	@Override
	protected void tearDown() throws Exception {
		if (soundFile != null && soundFile.exists())
			soundFile.delete();
		SoundManager.getInstance().clear();
	}

	public void testPlaySound() {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		PlaySoundBrick testBrick = new PlaySoundBrick(soundFilePath);
		testBrick.execute();
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());

		final int duration = mediaPlayer.getDuration() + 50;
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse("MediaPlayer is not done playing", mediaPlayer.isPlaying());
	}

	public void testIllegalArgument() {
		final String illegalPath = "file/that/does/not/exist";
		PlaySoundBrick testBrick = new PlaySoundBrick(illegalPath);
		try {
			testBrick.execute();
			fail("Execution of PlaySoundBrick with illegal file path did not cause an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			// expected behavior
		}
	}

	public void testPlayMultipleSounds() {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		final int playerCount = SoundManager.MAX_MEDIA_PLAYERS;
		for (int i = 0; i < playerCount; i++) {
			MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();
			PlaySoundBrick testBrick = new PlaySoundBrick(soundFilePath);
			testBrick.execute();
			assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());
		}
	}
	
	public void testPauseAndResume() {
		final String soundFilePath = soundFile.getAbsolutePath();
		assertNotNull("Could not open test sound file", soundFilePath);
		assertTrue("Could not open test sound file", soundFilePath.length() > 0);

		MediaPlayer mediaPlayer = SoundManager.getInstance().getMediaPlayer();

		PlaySoundBrick testBrick = new PlaySoundBrick(soundFilePath);
		testBrick.execute();
		assertTrue("MediaPlayer is not playing", mediaPlayer.isPlaying());

		mediaPlayer.pause();
		assertFalse("MediaPlayer is still playing after pause", mediaPlayer.isPlaying());
		
		mediaPlayer.start();
		assertTrue("MediaPlayer is not playing after resume", mediaPlayer.isPlaying());
		
		final int duration = mediaPlayer.getDuration() + 50;
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse("MediaPlayer is not done playing", mediaPlayer.isPlaying());
	}
}

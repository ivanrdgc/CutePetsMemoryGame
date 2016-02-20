package com.puyapps.framework;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Audio manager for Puy apps
 */
public class PuyAudio {
	private AssetManager assets;
	private SoundPool soundPool;

	/**
	 * Init with activity class
	 * @param activity
	 */
	public PuyAudio(Activity activity) {
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assets = activity.getAssets();
		this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
	}

	/**
	 * Creates music stream and starts playing it in a loop
	 * @param filename Path to music file in assets folder
	 * @return
	 */
	public PuyMusic createMusic(String filename) {
		try {
			AssetFileDescriptor assetDescriptor = this.assets.openFd(filename);
			return new PuyMusic(assetDescriptor);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load music '" + filename + "'");
		}
	}

	/**
	 * Creates sound stream and starts playing it
	 * @param filename Path to audio file in assets folder
	 * @return
	 */
	public PuySound createSound(String filename) {
		try {
			AssetFileDescriptor assetDescriptor = this.assets.openFd(filename);
			int soundId = this.soundPool.load(assetDescriptor, 0);
			return new PuySound(this.soundPool, soundId);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load sound '" + filename + "'");
		}
	}
}
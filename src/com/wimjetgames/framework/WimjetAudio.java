package com.wimjetgames.framework;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

public class WimjetAudio {
	private AssetManager assets;
	private SoundPool soundPool;

	@SuppressWarnings("deprecation")
	public WimjetAudio(Activity activity) {
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assets = activity.getAssets();
		this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
	}

	public WimjetMusic createMusic(String filename) {
		try {
			AssetFileDescriptor assetDescriptor = this.assets.openFd(filename);
			return new WimjetMusic(assetDescriptor);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load music '" + filename + "'");
		}
	}

	public WimjetSound createSound(String filename) {
		try {
			AssetFileDescriptor assetDescriptor = this.assets.openFd(filename);
			int soundId = this.soundPool.load(assetDescriptor, 0);
			return new WimjetSound(this.soundPool, soundId);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load sound '" + filename + "'");
		}
	}
}
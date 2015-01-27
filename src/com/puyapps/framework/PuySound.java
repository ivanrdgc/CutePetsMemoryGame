package com.puyapps.framework;

import android.media.SoundPool;

public class PuySound {
	private int soundId;
	private SoundPool soundPool;

	public PuySound(SoundPool soundPool, int soundId) {
		this.soundId = soundId;
		this.soundPool = soundPool;
	}

	public void play(float volume) {
		this.soundPool.play(this.soundId, volume, volume, 0, 0, 1);
	}

	public void dispose() {
		this.soundPool.unload(this.soundId);
	}
}
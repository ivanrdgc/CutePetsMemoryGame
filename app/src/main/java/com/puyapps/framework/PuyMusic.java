package com.puyapps.framework;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;

/**
 * Define music stream and provides manipulation functions
 */
public class PuyMusic implements OnCompletionListener, OnSeekCompleteListener, OnPreparedListener, OnVideoSizeChangedListener {
	private MediaPlayer mediaPlayer;
	private boolean isPrepared;

	public PuyMusic(AssetFileDescriptor assetDescriptor) {
		this.isPrepared = false;
		this.mediaPlayer = new MediaPlayer();
		try {
			this.mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(), assetDescriptor.getStartOffset(), assetDescriptor.getLength());
			this.mediaPlayer.prepare();
			this.isPrepared = true;
			this.mediaPlayer.setOnCompletionListener(this);
			this.mediaPlayer.setOnSeekCompleteListener(this);
			this.mediaPlayer.setOnPreparedListener(this);
			this.mediaPlayer.setOnVideoSizeChangedListener(this);

		} catch (Exception e) {
			throw new RuntimeException("Couldn't load music");
		}
	}

	public void dispose() {
		if (this.mediaPlayer.isPlaying()){
			this.mediaPlayer.stop();
		}
		this.mediaPlayer.release();
	}

	public boolean isLooping() {
		return this.mediaPlayer.isLooping();
	}

	public boolean isPlaying() {
		return this.mediaPlayer.isPlaying();
	}

	public boolean isStopped() {
		return !this.isPrepared;
	}

	public void pause() {
		if (this.mediaPlayer.isPlaying()) {
			this.mediaPlayer.pause();
		}
	}

	public void play() {
		if (this.mediaPlayer.isPlaying()) {
			return;
		}

		try {
			synchronized (this) {
				if (!this.isPrepared) {
					this.mediaPlayer.prepare();
				}
				this.mediaPlayer.start();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setLooping(boolean isLooping) {
		this.mediaPlayer.setLooping(isLooping);
	}

	public void setVolume(float volume) {
		this.mediaPlayer.setVolume(volume, volume);
	}

	public void stop() {
		if (this.mediaPlayer.isPlaying() == true) {
			this.mediaPlayer.stop();

			synchronized (this) {
				this.isPrepared = false;
			}
		}
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		synchronized (this) {
			this.isPrepared = false;
		}
	}

	public void seekBegin() {
		this.mediaPlayer.seekTo(0);
	}

	@Override
	public void onPrepared(MediaPlayer player) {
		synchronized (this) {
			this.isPrepared = true;
		}
	}

	@Override
	public void onSeekComplete(MediaPlayer player) {}

	@Override
	public void onVideoSizeChanged(MediaPlayer player, int width, int height) {}
}
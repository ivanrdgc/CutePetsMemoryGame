package com.puyapps.framework;

import java.util.Stack;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public abstract class PuyGame extends Activity {
	protected PuyFastRenderView renderView;
	private PuyGraphics graphics;
	private PuyAudio audio;
	private PuyInput input;
	private PuyFileIO fileIO;
	private PuyScreen screen;
	private Stack<Class<PuyScreen>> screens;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int frameBufferWidth = metrics.widthPixels;
		int frameBufferHeight = metrics.heightPixels;

		Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);

		this.renderView = new PuyFastRenderView(this, frameBuffer);
		this.graphics = new PuyGraphics(getAssets(), frameBuffer);
		this.fileIO = new PuyFileIO(this);
		this.audio = new PuyAudio(this);
		this.input = new PuyInput(this, this.renderView, 1, 1);
		//setContentView(this.renderView);

		if (screens == null) {
			screens = new Stack<Class<PuyScreen>>();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.screen.resume();
		this.renderView.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.renderView.pause();
		this.screen.pause();

		if (isFinishing()) {
			this.screen.dispose();
		}
	}
	
	/*@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		//this.screens.add((Class<WimjetScreen>) this.screen.getClass());
		outState.putSerializable("screens", this.screens);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		this.screens = (Stack<Class<WimjetScreen>>) savedInstanceState.getSerializable("screens");
		//this.onBackPressed();
	}*/

	@Override
	public void onBackPressed() {
		if (getCurrentScreen().backButton()) {
			while (!screens.isEmpty()) {
				try {
					setScreen(screens.pop().getDeclaredConstructor(PuyGame.class).newInstance(this), true);
					return;
				} catch (Exception e) {}
			}

			super.onBackPressed();
		}
	}

	public PuyInput getInput() {
		return this.input;
	}

	public PuyFileIO getFileIO() {
		return this.fileIO;
	}

	public PuyGraphics getGraphics() {
		return this.graphics;
	}

	public PuyAudio getAudio() {
		return this.audio;
	}

	public void setScreen(PuyScreen screen) {
		setScreen(screen, false);
	}

	@SuppressWarnings("unchecked")
	public void setScreen(PuyScreen screen, boolean backMode) {
		if (this.screen != null) {
			this.screen.pause();
			this.screen.dispose();

			if (!backMode) {
				this.screens.add((Class<PuyScreen>) this.screen.getClass());
			}
		}
		screen.resume();
		this.screen = screen;
	}

	public PuyScreen getCurrentScreen() {
		return this.screen;
	}
}
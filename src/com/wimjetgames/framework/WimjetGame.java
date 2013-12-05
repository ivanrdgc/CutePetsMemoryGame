package com.wimjetgames.framework;

import java.util.Stack;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public abstract class WimjetGame extends Activity {
	protected WimjetFastRenderView renderView;
	private WimjetGraphics graphics;
	private WimjetAudio audio;
	private WimjetInput input;
	private WimjetFileIO fileIO;
	private WimjetScreen screen;
	private Stack<Class<WimjetScreen>> screens;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int frameBufferWidth = metrics.widthPixels;
		int frameBufferHeight = metrics.heightPixels;

		Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);

		this.renderView = new WimjetFastRenderView(this, frameBuffer);
		this.graphics = new WimjetGraphics(getAssets(), frameBuffer);
		this.fileIO = new WimjetFileIO(this);
		this.audio = new WimjetAudio(this);
		this.input = new WimjetInput(this, this.renderView, 1, 1);
		//setContentView(this.renderView);

		if (screens == null) {
			screens = new Stack<Class<WimjetScreen>>();
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
					setScreen(screens.pop().getDeclaredConstructor(WimjetGame.class).newInstance(this), true);
					return;
				} catch (Exception e) {}
			}

			super.onBackPressed();
		}
	}

	public WimjetInput getInput() {
		return this.input;
	}

	public WimjetFileIO getFileIO() {
		return this.fileIO;
	}

	public WimjetGraphics getGraphics() {
		return this.graphics;
	}

	public WimjetAudio getAudio() {
		return this.audio;
	}

	public void setScreen(WimjetScreen screen) {
		setScreen(screen, false);
	}

	@SuppressWarnings("unchecked")
	public void setScreen(WimjetScreen screen, boolean backMode) {
		if (this.screen != null) {
			this.screen.pause();
			this.screen.dispose();

			if (!backMode) {
				this.screens.add((Class<WimjetScreen>) this.screen.getClass());
			}
		}
		screen.resume();
		this.screen = screen;
	}

	public WimjetScreen getCurrentScreen() {
		return this.screen;
	}
}
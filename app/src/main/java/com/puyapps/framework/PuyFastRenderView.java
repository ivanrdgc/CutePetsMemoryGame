package com.puyapps.framework;

import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.puyapps.framework.PuyInput.WimjetTouchEvent;

/**
 * Provides android view for the game activity
 */
public class PuyFastRenderView extends SurfaceView implements Runnable {
	private PuyGame game;
	private Bitmap framebuffer;
	private Thread renderThread;
	private SurfaceHolder holder;
	private volatile boolean running;

	public PuyFastRenderView(Context context) {
		super(context);
	}

	public PuyFastRenderView(PuyGame game, Bitmap framebuffer) {
		this(game);
		this.game = game;
		this.framebuffer = framebuffer;
		this.renderThread = null;
		this.holder = getHolder();
		this.running = false;
	}

	public void resume() {
		this.running = true;
		this.renderThread = new Thread(this);
		this.renderThread.start();
	}

	@Override
	public void run() {
		Rect dstRect = new Rect();
		long startTime = System.nanoTime();
		float deltaTime = 0;
		Iterator<WimjetTouchEvent> touchEventsIterator = null;
		WimjetTouchEvent touchEvent = null;
		while(this.running) {  
			if(!this.holder.getSurface().isValid()) {
				continue;
			}

			touchEventsIterator = this.game.getInput().getTouchEvents().iterator();

			// Blank screen
			if (this.game.getCurrentScreen().autoClean) {
				this.game.getCurrentScreen().g.clearScreen(Color.BLACK);
			}

			while(touchEventsIterator.hasNext()) {
				touchEvent = touchEventsIterator.next();

				if (touchEvent.type == WimjetTouchEvent.TOUCH_DOWN) {
					this.game.getCurrentScreen().touchDown(touchEvent.x * 100f / this.game.getCurrentScreen().g.getWidth(), touchEvent.y * 100f / this.game.getCurrentScreen().g.getHeight());

				} else if (touchEvent.type == WimjetTouchEvent.TOUCH_UP) {
					this.game.getCurrentScreen().touchUp(touchEvent.x * 100f / this.game.getCurrentScreen().g.getWidth(), touchEvent.y * 100f / this.game.getCurrentScreen().g.getHeight());

				} else if (touchEvent.type == WimjetTouchEvent.TOUCH_DRAGGED) {
					this.game.getCurrentScreen().touchDragged(touchEvent.x * 100f / this.game.getCurrentScreen().g.getWidth(), touchEvent.y * 100f / this.game.getCurrentScreen().g.getHeight());

				} else if (touchEvent.type == WimjetTouchEvent.TOUCH_HOLD) {
					this.game.getCurrentScreen().touchHold(touchEvent.x * 100f / this.game.getCurrentScreen().g.getWidth(), touchEvent.y * 100f / this.game.getCurrentScreen().g.getHeight());
				}
			}

			deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
			startTime = System.nanoTime();
			this.game.getCurrentScreen().update(deltaTime);

			Canvas canvas = this.holder.lockCanvas();
			canvas.getClipBounds(dstRect);
			canvas.drawBitmap(this.framebuffer, null, dstRect, null);
			this.holder.unlockCanvasAndPost(canvas);
		}
	}

	public void pause() {
		this.running = false;
		while(true) {
			try {
				this.renderThread.join();
				break;

			} catch (InterruptedException e) {}
		}
	}
}
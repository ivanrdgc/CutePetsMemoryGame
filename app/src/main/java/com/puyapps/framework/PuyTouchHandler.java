package com.puyapps.framework;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.puyapps.framework.PuyInput.WimjetTouchEvent;
import com.puyapps.framework.PuyPool.PoolObjectFactory;

/**
 * Provides callbacks for screen touch events.
 */
public class PuyTouchHandler implements OnTouchListener {
	private static final int MAX_TOUCHPOINTS = 10;

	private boolean[] isTouched;
	private int[] touchX;
	private int[] touchY;
	private int[] id;
	private PuyPool<WimjetTouchEvent> touchEventPool;
	private List<WimjetTouchEvent> touchEvents;
	private List<WimjetTouchEvent> touchEventsBuffer;
	private float scaleX;
	private float scaleY;

	public PuyTouchHandler(View view, float scaleX, float scaleY) {
		PoolObjectFactory<WimjetTouchEvent> factory = new PoolObjectFactory<WimjetTouchEvent>() {
			@Override
			public WimjetTouchEvent createObject() {
				return new WimjetTouchEvent();
			}
		};

		this.isTouched = new boolean[MAX_TOUCHPOINTS];
		this.touchX = new int[MAX_TOUCHPOINTS];
		this.touchY = new int[MAX_TOUCHPOINTS];
		this.id = new int[MAX_TOUCHPOINTS];

		this.touchEventPool = new PuyPool<WimjetTouchEvent>(factory, 100);
		view.setOnTouchListener(this);

		this.touchEvents = new ArrayList<WimjetTouchEvent>();
		this.touchEventsBuffer = new ArrayList<WimjetTouchEvent>();

		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		synchronized (this) {
			int action = event.getAction() & MotionEvent.ACTION_MASK;
			int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			int pointerCount = event.getPointerCount();
			WimjetTouchEvent touchEvent;
			for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
				if (i >= pointerCount) {
					this.isTouched[i] = false;
					this.id[i] = -1;
					continue;
				}
				int pointerId = event.getPointerId(i);
				if (event.getAction() != MotionEvent.ACTION_MOVE && i != pointerIndex) {
					// if it's an up/down/cancel/out event, mask the id to see if we should process it for this touch
					// point
					continue;
				}
				switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					touchEvent = this.touchEventPool.newObject();
					touchEvent.type = WimjetTouchEvent.TOUCH_DOWN;
					touchEvent.pointer = pointerId;
					touchEvent.x = this.touchX[i] = (int) (event.getX(i) * this.scaleX);
					touchEvent.y = this.touchY[i] = (int) (event.getY(i) * this.scaleY);
					this.isTouched[i] = true;
					this.id[i] = pointerId;
					this.touchEventsBuffer.add(touchEvent);
					break;

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL:
					touchEvent = this.touchEventPool.newObject();
					touchEvent.type = WimjetTouchEvent.TOUCH_UP;
					touchEvent.pointer = pointerId;
					touchEvent.x = this.touchX[i] = (int) (event.getX(i) * this.scaleX);
					touchEvent.y = this.touchY[i] = (int) (event.getY(i) * this.scaleY);
					this.isTouched[i] = false;
					this.id[i] = -1;
					this.touchEventsBuffer.add(touchEvent);
					break;

				case MotionEvent.ACTION_MOVE:
					touchEvent = this.touchEventPool.newObject();
					touchEvent.type = WimjetTouchEvent.TOUCH_DRAGGED;
					touchEvent.pointer = pointerId;
					touchEvent.x = this.touchX[i] = (int) (event.getX(i) * this.scaleX);
					touchEvent.y = this.touchY[i] = (int) (event.getY(i) * this.scaleY);
					this.isTouched[i] = true;
					this.id[i] = pointerId;
					this.touchEventsBuffer.add(touchEvent);
					break;
				}
			}
			return true;
		}
	}

	public boolean isTouchDown(int pointer) {
		synchronized (this) {
			int index = getIndex(pointer);
			if (index < 0 || index >= MAX_TOUCHPOINTS) {
				return false;
			
			} else {
				return this.isTouched[index];
			}
		}
	}

	public int getTouchX(int pointer) {
		synchronized (this) {
			int index = getIndex(pointer);
			if (index < 0 || index >= MAX_TOUCHPOINTS) {
				return 0;
			
			} else {
				return this.touchX[index];
			}
		}
	}

	public int getTouchY(int pointer) {
		synchronized (this) {
			int index = getIndex(pointer);
			if (index < 0 || index >= MAX_TOUCHPOINTS) {
				return 0;
			
			} else {
				return this.touchY[index];
			}
		}
	}

	public List<WimjetTouchEvent> getTouchEvents() {
		synchronized (this) {
			int len = this.touchEvents.size();
			for (int i = 0; i < len; i++) {
				this.touchEventPool.free(this.touchEvents.get(i));
			}
			this.touchEvents.clear();
			this.touchEvents.addAll(this.touchEventsBuffer);
			this.touchEventsBuffer.clear();
			return this.touchEvents;
		}
	}

	// returns the index for a given pointerId or -1 if no index.
	private int getIndex(int pointerId) {
		for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
			if (this.id[i] == pointerId) {
				return i;
			}
		}
		return -1;
	}
}
package com.wimjetgames.framework;

import java.util.List;

import android.content.Context;
import android.view.View;

public class WimjetInput {
	public static class WimjetTouchEvent {
		public static final int TOUCH_DOWN = 0;
		public static final int TOUCH_UP = 1;
		public static final int TOUCH_DRAGGED = 2;
		public static final int TOUCH_HOLD = 3;

		public int type;
		public int x, y;
		public int pointer;
	}

	private WimjetTouchHandler touchHandler;

	public WimjetInput(Context context, View view, float scaleX, float scaleY) {
		this.touchHandler = new WimjetTouchHandler(view, scaleX, scaleY);
	}

	public boolean isTouchDown(int pointer) {
		return this.touchHandler.isTouchDown(pointer);
	}

	public int getTouchX(int pointer) {
		return this.touchHandler.getTouchX(pointer);
	}

	public int getTouchY(int pointer) {
		return this.touchHandler.getTouchY(pointer);
	}

	public List<WimjetTouchEvent> getTouchEvents() {
		return this.touchHandler.getTouchEvents();
	}
}
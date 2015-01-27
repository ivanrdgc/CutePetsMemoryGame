package com.puyapps.cutepetsmemorygame;

import android.provider.Settings.Secure;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public final class ParseClass {
	private static ParseObject parseObject = null;
	
	public static final void Init() {
		Parse.initialize(MemoryGame.Instance, "<parse_app_id>", "<parse_app_android_key>");
		final String androidId = Secure.getString(MemoryGame.Instance.getContentResolver(), Secure.ANDROID_ID);
		ParseQuery.getQuery("Users").whereEqualTo("androidId", androidId).getFirstInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					parseObject = object;
				} else {
					parseObject = new ParseObject("Users");
					parseObject.put("androidId", androidId);
					parseObject.saveInBackground();
				}
				AppStarted();
			}
		});
	}
	
	public static final void AppStarted() {
		if (parseObject != null) {
			parseObject.increment("appStarted");
			parseObject.saveInBackground();
		}
	}
	
	public static final void GameStarted() {
		if (parseObject != null) {
			parseObject.increment("gameStarted");
			parseObject.saveInBackground();
		}
	}
}

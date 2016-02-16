package com.puyapps.cutepetsmemorygame;

import android.app.Application;
import android.content.Context;
import android.provider.Settings.Secure;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Android application class that automatically adds basic parse.com functions:
 *  - Registers the device as a new Installation
 *  - Adds a appStarted flag as an incremental integer
 *  - Allows to call manually GameStarted() to increment gameStarted flag
 *
 *  This class can be used by calling maually static functions or by setting it as Application for
 *  the android project
 */
public class ParseApplication extends Application {
	private static ParseObject parseObject = null;

	@Override
	public void onCreate() {
		super.onCreate();

		ParseApplication.Init(this);
	}

	/**
	 * Initialize manually parse.com installation for the app
	 * @param instance
	 */
	public static final void Init(final Context instance) {
		Parse.initialize(instance, instance.getString(R.string.parse_application_id), instance.getString(R.string.parse_client_key));
		final String androidId = Secure.getString(instance.getContentResolver(), Secure.ANDROID_ID);
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

	/**
	 * Increment the appStarted flag
	 */
	public static final void AppStarted() {
		if (parseObject != null) {
			parseObject.increment("appStarted");
			parseObject.saveInBackground();
		}
	}

	/**
	 * Increment the gameStarted flag
	 */
	public static final void GameStarted() {
		if (parseObject != null) {
			parseObject.increment("gameStarted");
			parseObject.saveInBackground();
		}
	}
}

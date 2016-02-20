package com.puyapps.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;

/**
 * Provides access to files in assets folder
 */
public class PuyFileIO {
	private Context context;
	private AssetManager assets;
	private String externalStoragePath;

	public PuyFileIO(Context context) {
		this.context = context;
		this.assets = this.context.getAssets();
		this.externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	}

	public InputStream readAsset(String file) throws IOException {
		return this.assets.open(file);
	}

	public InputStream readFile(String file) throws IOException {
		return new FileInputStream(this.externalStoragePath + file);
	}

	public OutputStream writeFile(String file) throws IOException {
		return new FileOutputStream(this.externalStoragePath + file);
	}

	public SharedPreferences getSharedPref() {
		return PreferenceManager.getDefaultSharedPreferences(this.context);
	}
}
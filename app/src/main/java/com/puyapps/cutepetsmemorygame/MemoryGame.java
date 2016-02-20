package com.puyapps.cutepetsmemorygame;

import org.onepf.oms.OpenIabHelper;
import org.onepf.oms.SkuManager;
import org.onepf.oms.SkuMappingException;
import org.onepf.oms.appstore.googleUtils.IabHelper;
import org.onepf.oms.appstore.googleUtils.IabResult;
import org.onepf.oms.appstore.googleUtils.Inventory;
import org.onepf.oms.appstore.googleUtils.Purchase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.puyapps.cutepetsmemorygame.screens.MainMenuScreen;
import com.puyapps.framework.PuyGame;
import com.puyapps.framework.PuyMusic;

public class MemoryGame extends PuyGame {
	
	public static MemoryGame Instance = null;

	public final static float MENU_OPENING = 0.25f;
	public final static float CARDS_TIMEOUT = 0.5f;
	
	public static Level level;
	public static enum Level {
		Easy, Medium, Hard
	}
	public static boolean competition;
	
	private static AdView adView;
	private static InterstitialAd interstitial;
	
	// Does the user have the premium upgrade?
	public boolean mIsPremium;
	static final int RC_REQUEST = 10001;
	static final String SKU_PREMIUM = "com.puyapps.cutepetsmemorygame.adfree";
	// The helper object
	OpenIabHelper mHelper;
	
	public static PuyMusic backgroundMusic;
	public static boolean isPlayingMusic, pauseMusic;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Instance = this;

		mIsPremium = false;

		// Ad free
		String base64EncodedPublicKey = getString(R.string.base64_encoded_public_key);
		OpenIabHelper.Options.Builder builder = new OpenIabHelper.Options.Builder();
		builder.setVerifyMode(OpenIabHelper.Options.VERIFY_EVERYTHING);
		builder.addStoreKey(OpenIabHelper.NAME_GOOGLE, base64EncodedPublicKey);
		mHelper = new OpenIabHelper(this, builder.build());
		try {
			SkuManager.getInstance().mapSku(SKU_PREMIUM, OpenIabHelper.NAME_GOOGLE, SKU_PREMIUM);
		} catch(final SkuMappingException e) {}

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(final IabResult result) {
				if (!result.isSuccess()) {
					return;
				}
				if (mHelper == null) return;
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

		// Admob
		//-- Interstitial
		interstitial = new InterstitialAd(this);
		interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));
		interstitial.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				super.onAdClosed();
				interstitial.loadAd(new AdRequest.Builder().build());
			}
		});
	    interstitial.loadAd(new AdRequest.Builder().build());

		RelativeLayout.LayoutParams adLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,	LayoutParams.WRAP_CONTENT);
		adLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adView = new AdView(this);
		adView.setAdUnitId(getString(R.string.admob_banner_id));
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdListener(new AdListener() {
			
		});
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(renderView);
		layout.addView(adView, adLayout);
		setContentView(layout);
		adView.loadAd(new AdRequest.Builder().build());

		setScreen(new MainMenuScreen(this));
		
		backgroundMusic = getAudio().createMusic("background.mp3");
		backgroundMusic.setLooping(true);
		backgroundMusic.setVolume(1f);
		isPlayingMusic = getPlayMusic();
		if (isPlayingMusic) {
			backgroundMusic.play();
		}
		
		pauseMusic = true;
	}
	
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inv) {
			if (mHelper == null) return;
			
			if (result.isFailure()) {
				return;
			}
			
			Purchase premiumPurchase = inv.getPurchase(SKU_PREMIUM);
			setRemoveAds(premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
		}
	};
	
	boolean verifyDeveloperPayload(Purchase p) {
		// TODO: Secure payload check
	
		return true;
	}
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase info) {
			setRemoveAds(false);

			if (mHelper == null) return;
			
			if (result.isFailure()) {
				return;
			}
			
			if (!verifyDeveloperPayload(info)) {
				return;
			}
			
			if (info.getSku().equals(SKU_PREMIUM)) {
				setRemoveAds(true);
			}
		}
	};
	
	@Override
	protected void onDestroy() {
	    adView.destroy();
	    
	    backgroundMusic.pause();
		
		super.onDestroy();
	}
	
	@Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (mHelper == null) return;
		
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	public void showInterstitial() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (interstitial.isLoaded() && !mIsPremium) {
					pauseMusic = false;
					interstitial.show();
				}
			}
		});
	}
	
	public void removeAds() {
		pauseMusic = false;
		String payload = "";
		mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST, mPurchaseFinishedListener, payload);
	}
    
	
	public void setRemoveAds(boolean s) {
    	// Remove ad view
    	mIsPremium = s;
    	adView.setVisibility(mIsPremium ? View.GONE : View.VISIBLE);
	}
	
	public void setPlayMusic(boolean s) {		
		SharedPreferences userDetails = getSharedPreferences(this.getClass().getPackage().getName(), Context.MODE_PRIVATE);
    	Editor editor = userDetails.edit();
    	
    	editor.putBoolean("playMusic", s);
    	
    	editor.commit();
    	
    	isPlayingMusic = s;
    	if (s) {
    		backgroundMusic.play();

		} else {
			backgroundMusic.pause();
		}
	}
	
	public boolean getPlayMusic() {
		SharedPreferences userDetails = getSharedPreferences(this.getClass().getPackage().getName(), Context.MODE_PRIVATE);
		
		return userDetails.getBoolean("playMusic", true);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (isPlayingMusic && pauseMusic)
			backgroundMusic.pause();
		
		pauseMusic = true;
		
		adView.pause();
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		
		if (isPlayingMusic)
			backgroundMusic.play();
		
		adView.resume();
	}
}

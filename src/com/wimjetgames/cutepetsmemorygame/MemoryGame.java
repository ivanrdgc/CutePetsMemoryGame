package com.wimjetgames.cutepetsmemorygame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;
import com.wimjetgames.cutepetsmemorygame.screens.MainMenuScreen;
import com.wimjetgames.cutepetsmemorygame.util.IabHelper;
import com.wimjetgames.cutepetsmemorygame.util.IabResult;
import com.wimjetgames.cutepetsmemorygame.util.Inventory;
import com.wimjetgames.cutepetsmemorygame.util.Purchase;
import com.wimjetgames.framework.WimjetGame;
import com.wimjetgames.framework.WimjetMusic;

public class MemoryGame extends WimjetGame implements AdListener {

	public final static float MENU_OPENING = 0.25f;
	public final static float CARDS_TIMEOUT = 0.5f;
	
	public static Level level;
	public static enum Level {
		Easy, Medium, Hard
	}
	public static boolean competition;
	
	private static AdView adView;
	private static InterstitialAd interstitial;
	//private static boolean showNextInterstitial;
	
	// Does the user have the premium upgrade?
	public boolean mIsPremium;
	static final int RC_REQUEST = 10001;
	static final String SKU_PREMIUM = "com.wimjetgames.cutepetsmemorygame.adfree";
	// The helper object
	IabHelper mHelper;
	
	public static WimjetMusic backgroundMusic;
	public static boolean isPlayingMusic, pauseMusic;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mIsPremium = false;

		// Ad free
		String base64EncodedPublicKey = "<google_play_license_key>";
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {	
				if (!result.isSuccess()) {
					return;
				}
				
				if (mHelper == null) return;
				
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

		// Admob
		AdRequest adRequest;
		//-- Interstitial
		interstitial = new InterstitialAd(this, "<admob_interstitial_id>");
		interstitial.setAdListener(this);
		adRequest = new AdRequest();
	    interstitial.loadAd(adRequest);
    	//showNextInterstitial = true;

		RelativeLayout.LayoutParams adLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,	LayoutParams.WRAP_CONTENT);
		adLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adView = new AdView(this, AdSize.SMART_BANNER, "<admob_banner_id>");
		adView.setAdListener(this);
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(renderView);
		layout.addView(adView, adLayout);
		setContentView(layout);
		adRequest = new AdRequest();
		adView.loadAd(adRequest);
			
		//adView.setVisibility(View.GONE);

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
		//String payload = p.getDeveloperPayload();
	
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
	public void onDismissScreen(Ad ad) {
		if (ad == interstitial) {
			//showNextInterstitial = false;
			AdRequest adRequest = new AdRequest();
		    interstitial.loadAd(adRequest);
		}
	}

	@Override
	public void onFailedToReceiveAd(Ad ad, ErrorCode err) {}

	@Override
	public void onLeaveApplication(Ad ad) {
		backgroundMusic.pause();
	}

	@Override
	public void onPresentScreen(Ad ad) {}

	@Override
	public void onReceiveAd(Ad ad) {
		//if (!mIsPremium && ad == interstitial && showNextInterstitial && getCurrentScreen().getClass() != GameScreen.class) {
		//	interstitial.show();
		//}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mHelper == null) return;
		
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	public void showInterstitial() {
		if (interstitial.isReady() && !mIsPremium) {
			pauseMusic = false;
			interstitial.show();

		}// else {
		//	showNextInterstitial = true;
		//}
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
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		
		if (isPlayingMusic)
			backgroundMusic.play();
	}
}

package com.wimjetgames.cutepetsmemorygame.screens;

import com.wimjetgames.cutepetsmemorygame.MemoryGame;
import com.wimjetgames.framework.WimjetGame;
import com.wimjetgames.framework.WimjetGraphics.Align;
import com.wimjetgames.framework.WimjetScreen;

public class MainMenuScreen extends WimjetScreen {

	private float opening, ratBtnX, ratBtnY, ratBtnX2, ratBtnY2, ratBtnX3, ratBtnY3;
	
	public MainMenuScreen(WimjetGame game) {
		super(game);
		autoClean = false;
		
		opening = 0f;
		
		ratBtnX = 10f;
		ratBtnY = 100f * (ratBtnX/100f) * g.getWidth() / g.getHeight();
		
		ratBtnX2 = 8.5f;
		ratBtnY2 = 100f * (ratBtnX2/100f) * g.getWidth() / g.getHeight();
		
		ratBtnX3 = 20f;
		ratBtnY3 = 100f * (ratBtnX3/100f) * g.getWidth() / g.getHeight();
	}

	@Override
	public void update(float deltaTime) {
		if (opening == 0) {
			// Background
			g.drawImage("bkg.jpg", 0, 0, 100, 100, 100f/3f, 50, 100f/3f, 50, 0, Align.TOP_LEFT);
		
			// Play
			g.drawImage("play.png", 19.5f, 19.5f, ratBtnX, ratBtnY, 0, 0, 100, 100, 0, Align.MIDDLE_CENTER);
			if (!((MemoryGame) game).mIsPremium) {
				// Shop
				g.drawImage("remove_ads.png", 20f, 35f, ratBtnX3, ratBtnY3, 0, 0, 100, 100, 0, Align.MIDDLE_CENTER);
			}
			g.drawImage(MemoryGame.isPlayingMusic ? "unmute.png" : "mute.png", 19.5f, 52.5f, ratBtnX2, ratBtnY2, 0, 0, 100, 100, 0, Align.MIDDLE_CENTER);
				
		} else if (opening - deltaTime <= 0) {
			game.setScreen(new ChooseModeScreen(game), true);
		
		} else {
			float c = (MemoryGame.MENU_OPENING - opening)*100/MemoryGame.MENU_OPENING;
			g.drawImage("bkg.jpg", 0, 0, 100, 100, 100f/3f, 50f-c/2f, 100f/3f, 50, 0, Align.TOP_LEFT);

			opening -= deltaTime;
		}
	}
	
	@Override
	public void touchDown(float x, float y) {
		if (opening == 0 && x >= 7 && x <= 32 && y >= 11 && y <= 28) {
			//game.setScreen(new ChooseModeScreen(game));
			opening = MemoryGame.MENU_OPENING;
		
		} else if (!((MemoryGame) game).mIsPremium && opening == 0 && x >= 7 && x <= 32 && y >= 28 && y <= 45) {
			((MemoryGame) game).removeAds();
		
		} else if (opening == 0 && x >= 7 && x <= 32 && y >= 44.5 && y <= 61.5) {
			((MemoryGame) game).setPlayMusic(!MemoryGame.isPlayingMusic);
		}
	}
}

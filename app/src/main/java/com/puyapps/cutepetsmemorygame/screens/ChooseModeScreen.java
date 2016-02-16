package com.puyapps.cutepetsmemorygame.screens;

import com.puyapps.cutepetsmemorygame.MemoryGame;
import com.puyapps.framework.PuyGame;
import com.puyapps.framework.PuyScreen;
import com.puyapps.framework.PuyGraphics.Align;

public class ChooseModeScreen extends PuyScreen {

	private float opening, closing;
	
	public ChooseModeScreen(PuyGame game) {
		super(game);
		autoClean = false;
		
		opening = 0f;
		closing = 0f;
	}

	@Override
	public void update(float deltaTime) {
		if (closing == 0 && opening == 0) {
			g.drawImage("bkg.jpg", 0, 0, 100, 100, 100f/3f, 0, 100f/3f, 50, 0, Align.TOP_LEFT);
			//g.drawString("CLASSIC", 50, 100/3, 80, Color.WHITE, Align.MIDDLE_CENTER);
			//g.drawString("TIME ATACK", 50, 200/3, 80, Color.WHITE, Align.MIDDLE_CENTER);
			
		} else if (closing > 0f && closing - deltaTime <= 0) {
			game.setScreen(new MainMenuScreen(game), true);
			return;

		} else if (closing > 0f) {
			float c = (MemoryGame.MENU_OPENING - closing)*100/MemoryGame.MENU_OPENING;
			g.drawImage("bkg.jpg", 0, 0, 100, 100, 100f/3f, c/2f, 100f/3f, 50, 0, Align.TOP_LEFT);

			closing -= deltaTime;
		
		} else if (opening > 0f && opening - deltaTime <= 0) {
			game.setScreen(new ChooseLevelScreen(game), true);
			return;
		
		} else if (opening > 0f) {
			float c = (MemoryGame.MENU_OPENING - opening)*100/MemoryGame.MENU_OPENING;
			
			if (MemoryGame.competition) {
				g.drawImage("bkg.jpg", 0, 0, 100, 100, (100f+c)/3f, 0, 100f/3f, 50, 0, Align.TOP_LEFT);

			} else {
				g.drawImage("bkg.jpg", 0, 0, 100, 100, (100f-c)/3f, 0, 100f/3f, 50, 0, Align.TOP_LEFT);
			}

			opening -= deltaTime;
		}
	}
	
	@Override
	public void touchDown(float x, float y) {
		if (closing == 0 && opening == 0) {
			if (y < 50) {
				// Training
				MemoryGame.competition = false;
			} else {
				// Competition
				MemoryGame.competition = true;
			}
			opening = MemoryGame.MENU_OPENING;
			//game.setScreen(new ChooseLevelScreen(game));
		}
	}
	
	@Override
	public boolean backButton() {
		if (closing == 0) {
			closing = MemoryGame.MENU_OPENING;
	
		} else {
			return super.backButton();
		}
		
		return false;
	}
}

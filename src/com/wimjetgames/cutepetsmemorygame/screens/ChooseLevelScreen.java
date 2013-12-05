package com.wimjetgames.cutepetsmemorygame.screens;

import com.wimjetgames.cutepetsmemorygame.MemoryGame;
import com.wimjetgames.cutepetsmemorygame.MemoryGame.Level;
import com.wimjetgames.framework.WimjetGame;
import com.wimjetgames.framework.WimjetGraphics.Align;
import com.wimjetgames.framework.WimjetScreen;

public class ChooseLevelScreen extends WimjetScreen {

	float closing, opening;
	
	public ChooseLevelScreen(WimjetGame game) {
		super(game);
		autoClean = false;
		
		closing = 0f;
	}

	@Override
	public void update(float deltaTime) {
		if (closing == 0) {
			// Background
			g.drawImage("bkg.jpg", 0, 0, 100, 100, MemoryGame.competition ? 200f/3f : 0, 0, 100f/3f, 50, 0, Align.TOP_LEFT);
			
			//g.drawString("EASY", 50, 25, 80, Color.rgb(255, 165, 0), Align.MIDDLE_CENTER);
			//g.drawString("MEDIUM", 50, 50, 80, Color.GREEN, Align.MIDDLE_CENTER);
			//g.drawString("HARD", 50, 75, 80, Color.RED, Align.MIDDLE_CENTER);
		
		} else if (closing > 0f && closing - deltaTime <= 0) {
			game.setScreen(new ChooseModeScreen(game), true);
			return;

		} else if (closing > 0f) {
			float c = (MemoryGame.MENU_OPENING - closing)*100/MemoryGame.MENU_OPENING;
			
			if (MemoryGame.competition) {
				g.drawImage("bkg.jpg", 0, 0, 100, 100, (200f-c)/3f, 0, 100f/3f, 50, 0, Align.TOP_LEFT);

			} else {
				g.drawImage("bkg.jpg", 0, 0, 100, 100, c/3f, 0, 100f/3f, 50, 0, Align.TOP_LEFT);
			}
			
			closing -= deltaTime;
		}
	}
	
	@Override
	public void touchDown(float x, float y) {
		if (closing == 0 && opening == 0) {
			if (y < 37.5) {
				// Easy
				MemoryGame.level = Level.Easy;
			} else if (y < 62.5) {
				// Medium
				MemoryGame.level = Level.Medium;
			} else {
				// Hard
				MemoryGame.level = Level.Hard;
			}
			game.setScreen(new GameScreen(game), true);
		}
	}
	
	@Override
	public boolean backButton() {
		if (closing == 0) {
			closing = MemoryGame.MENU_OPENING;
	
		} else {
			game.setScreen(new MainMenuScreen(game), true);
			return false;
		}
		
		return false;
	}
}

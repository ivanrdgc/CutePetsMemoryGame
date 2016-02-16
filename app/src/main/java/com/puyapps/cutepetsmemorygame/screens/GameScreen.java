package com.puyapps.cutepetsmemorygame.screens;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Color;

import com.puyapps.cutepetsmemorygame.MemoryApp;
import com.puyapps.cutepetsmemorygame.MemoryGame;
import com.puyapps.cutepetsmemorygame.ParseApplication;
import com.puyapps.cutepetsmemorygame.MemoryGame.Level;
import com.puyapps.framework.PuyGame;
import com.puyapps.framework.PuyScreen;
import com.puyapps.framework.PuyGraphics.Align;

public class GameScreen extends PuyScreen {	
	private int cols, rows, completed;
	private float time, pTime, w, h, mx, my, ratBtnX, ratBtnY, competitionTime, readyStep;
	
	private String card;
	
	private MemoryApp<String> data;
	
	public GameScreen(PuyGame game) {
		super(game);
		ParseApplication.GameStarted();
		autoClean = false;
		
		if (MemoryGame.level == Level.Easy) {
			cols = 4;
			rows = 2;

		} else if (MemoryGame.level == Level.Medium) {
			cols = 6;
			rows = 3;

		} else if (MemoryGame.level == Level.Hard) {
			cols = 8;
			rows = 5;
		}
		
		this.time = 0f;
		this.pTime = 0f;
		this.completed = 0;
		
		w = 100f/cols - 2f;
		h = 100f/rows - 2f;
		
		float
			rw = w * g.getWidth() / 100f,
			rh = h * g.getHeight() / 100f;
		
		if (rw < rh) {
			h = rw / (g.getHeight() / 100f);

		} else {
			w = rh / (g.getWidth() / 100f);
		}
		
		mx = (100f - (w + 2f) * cols);
		my = (100f - (h + 2f) * rows);
		
		ratBtnX = 15f;
		ratBtnY = 100f * (ratBtnX/100f) * g.getWidth() / g.getHeight();
		
		this.initData();
		
		this.readyStep = 2.5f;
	}
	
	private void initData() {
		// Init data
		ArrayList<String> list = new ArrayList<String>(16);
		for (int i = 1; i <= 20; i++) {
			//for (int j = 0; j < 5; j++) {
				list.add("animals/" + i + ".png");
			//}
		}
		
		Collections.shuffle(list);
		
		data = new MemoryApp<String>(list.subList(0, cols * rows / 2), cols, rows);
		
		// Bkg
		card = "cards/" + (1 + (int) Math.floor(Math.random() * 4)) + ".png";
		
		// Competition time
		if (MemoryGame.level == Level.Easy) {
			this.time = 0;
			//this.competitionTime = Math.max(8, 59.9f - 10f * this.completed);
			if (this.completed > 5) {
				this.competitionTime = Math.max(0.9f, 14.9f - this.completed);
			
			} else {
				this.competitionTime = 59.9f - 10 * this.completed;
			}

		} else if (MemoryGame.level == Level.Medium) {
			this.time = 0;
			//this.competitionTime = Math.max(19.9f, 119.9f - 15f * this.completed);
			if (this.completed > 6) {
				this.competitionTime = Math.max(0.9f, 41.9f - 2*this.completed);
			
			} else {
				this.competitionTime = 119.9f - 15 * this.completed;
			}

		} else if (MemoryGame.level == Level.Hard) {
			this.time = 0;
			//this.competitionTime = Math.max(49.9f, 179.9f - 20f*this.completed);
			if (this.completed > 6) {
				this.competitionTime = Math.max(0.9f, 89f - 5*this.completed);
			
			} else {
				this.competitionTime = 179f - 20 * this.completed;
			}
		}
	}
	
	@Override
	public void update(float deltaTime) {		
		// Background
		g.drawImage("bkg.jpg", 0, 0, 100, 100, MemoryGame.competition ? 200f/3f : 0, 50, 100f/3f, 50, 0, Align.TOP_LEFT);
		
		// Update
		data.update(deltaTime);

		if (data.isComplete() && MemoryGame.competition) {
			completed++;
			this.initData();
		}
		
		if (data.isComplete() && !MemoryGame.competition || MemoryGame.competition && this.time >= this.competitionTime) {
			//g.drawARGB(30, 100, 100, 100);
			if (pTime < time) {
				if (time < 10) {
					pTime = Math.min(pTime + deltaTime * time / .75f, time);
				
				} else {
					pTime = Math.min(pTime + deltaTime * time / 1f, time);
				}
			}
			g.drawString("COMPLETED!", 50, 20, 90, Color.BLACK, Align.MIDDLE_CENTER);
			if (MemoryGame.competition) {
				g.drawString("You survived " + this.completed + " rounds", 50, 30, 50, Color.BLACK, Align.MIDDLE_CENTER);
			
			} else {
				g.drawString("Time: " + (Math.round(this.pTime * 100f) / 100f) + "s", 50, 30, 50, Color.BLACK, Align.MIDDLE_CENTER);
			}
			
			g.drawImage("reload.png", 25, 75, ratBtnX, ratBtnY, 0, 0, 100, 100, 0, Align.MIDDLE_CENTER);
			g.drawImage("home.png", 75, 75, ratBtnX, ratBtnY, 0, 0, 100, 100, 0, Align.MIDDLE_CENTER);

		} else {
			// Draw elements
			for (int row = 1; row <= rows; row++) {
				for (int col = 1; col <= cols; col++) {
					String val = data.get(col, row);
					
					float
						x0 = mx/2f + (col-1f)*(100f - mx)/cols + 1f,
						y0 = my/2f + (row-1f)*(100f - my)/rows + 1f;
					
					if (val != null) {
						float
							sV = data.secondsToVisible(col, row),
							sH = data.secondsToInvisible(col, row);

						g.drawImage(val, x0, y0, w, h, 0, 0, 100, 100, 0, Align.TOP_LEFT);

						if (sV > 0) {
							g.drawImage(card, x0, y0, w, h, 0, 0, 100, 100, 100*(1-sV/MemoryGame.CARDS_TIMEOUT), Align.TOP_LEFT);
						
						} else if (sH > 0) {
							g.drawImage(card, x0, y0, w, h, 0, 0, 100, 100, 100*(sH/MemoryGame.CARDS_TIMEOUT), Align.TOP_LEFT);
						}
						
					} else {
						g.drawImage(card, x0, y0, w, h, 0, 0, 100, 100, 0, Align.TOP_LEFT);
					}
				}
			}
			
			// Timeout
			if (MemoryGame.competition) {
				g.drawString("Round: " + String.valueOf(this.completed + 1) + " | Remaining: " + String.valueOf(Math.round(Math.floor(this.competitionTime - this.time + 1f))) + "s", 100, 0, 50, Color.BLACK, Align.BOTTOM_RIGHT);
			}

			if (readyStep > 0f) {
				g.drawARGB(50, 100, 100, 100);
				g.drawString("Ready?", 50, 40, 80, Color.WHITE, Align.MIDDLE_CENTER);
				g.drawString(String.valueOf(Math.round(Math.floor(readyStep) + 1f)), 50, 60, 80, Color.WHITE, Align.MIDDLE_CENTER);
				
				readyStep = Math.max(0, readyStep - deltaTime);
				
			} else {
				time += deltaTime;
			}
		}
	}
	
	@Override
	public void touchDown(float x, float y) {
		if (!data.isComplete() && this.readyStep <= 0f && (!MemoryGame.competition || this.time < this.competitionTime)) {
			for (int row = 1; row <= rows; row++) {
				for (int col = 1; col <= cols; col++) {
			
					float
						x0 = mx/2f + (col-1f)*(100f - mx)/cols + 1f,
						y0 = my/2f + (row-1f)*(100f - my)/rows + 1f,
						xf = x0 + w,
						yf = y0 + h;
					
					if (x > x0 && x < xf &&
						y > y0 && y < yf) {
						
						data.turn(col, row);
					}
				}
			}
		} else {
			if (x > 75f - ratBtnX / 2f && x < 75f + ratBtnX / 2f && y > 75f - ratBtnY / 2f && y < 75f + ratBtnY / 2f) {				
				((MemoryGame) game).showInterstitial();
				game.setScreen(new MainMenuScreen(game), true);

			} else if (x > 25f - ratBtnX / 2f && x < 25f + ratBtnX / 2f && y > 75f - ratBtnY / 2f && y < 75f + ratBtnY / 2f) {
				((MemoryGame) game).showInterstitial();
				
				this.time = 0f;
				this.pTime = 0f;
				this.completed = 0;
				this.readyStep = 2.5f;

				initData();
			}
		}
	}
	
	@Override
	public boolean backButton() {
		((MemoryGame) game).showInterstitial();
		game.setScreen(new MainMenuScreen(game), true);
		return false;
	}
}


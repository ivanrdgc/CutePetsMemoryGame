package com.puyapps.framework;

/**
 * Defines a game screen and provides callbacks for game development.
 */
public abstract class PuyScreen {
	protected final PuyGame game;
	protected PuyGraphics g;
	protected boolean autoClean;

	public PuyScreen(PuyGame game) {		
		this.game = game;
		this.g = this.game.getGraphics();
		this.autoClean = true;
	}

	public void update(float deltaTime) {};

	public void pause() {}

	public void resume() {}

	public void dispose() {}

	public boolean backButton() {
		return true;
	}

	public void touchDown(float x, float y) {}

	public void touchUp(float x, float y) {}

	public void touchDragged(float x, float y) {}

	public void touchHold(float x, float y) {}
}
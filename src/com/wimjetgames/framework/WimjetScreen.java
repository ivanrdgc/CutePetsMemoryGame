package com.wimjetgames.framework;


public abstract class WimjetScreen {
	protected final WimjetGame game;
	protected WimjetGraphics g;
	protected boolean autoClean;

	public WimjetScreen(WimjetGame game) {		
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
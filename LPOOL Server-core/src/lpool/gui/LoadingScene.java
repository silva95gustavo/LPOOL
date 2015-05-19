package lpool.gui;

import lpool.gui.assets.Manager;
import lpool.gui.assets.Models;
import lpool.gui.assets.Sounds;
import lpool.gui.assets.Textures;

import com.badlogic.gdx.Screen;

public class LoadingScene implements Screen {

	private com.badlogic.gdx.Game GdxGame;
	private Manager manager;
	
	public LoadingScene(com.badlogic.gdx.Game GdxGame) {
		this.GdxGame = GdxGame;
		manager = Manager.getInstance();
		
		Textures.getInstance();
		Sounds.getInstance();
		Models.getInstance();
	}

	@Override
	public void dispose() {
		manager.dispose();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		if (manager.getAssetManager().update())
			GdxGame.setScreen(new LobbyScene(GdxGame));
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

}

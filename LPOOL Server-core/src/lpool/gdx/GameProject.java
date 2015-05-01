package lpool.gdx;

import lpool.gdx.assets.Sounds;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameProject extends Game {
	int width;
	int height;
	
	@Override
	public void create () {
		width = Gdx.graphics.getDesktopDisplayMode().width;
		height = Gdx.graphics.getDesktopDisplayMode().height;
		Gdx.graphics.setDisplayMode(width, height, false);
		
		lpool.logic.Game game = new lpool.logic.Game();
		setScreen(new MatchScene(game));
	}
}

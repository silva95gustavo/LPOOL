package lpool.gdx;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameProject extends Game {
	SpriteBatch batch;
	Texture img;
	int width;
	int height;
	
	@Override
	public void create () {
		width = Gdx.graphics.getDesktopDisplayMode().width;
		height = Gdx.graphics.getDesktopDisplayMode().height;
		//Gdx.graphics.setDisplayMode(width, height, false);
		
		batch = new SpriteBatch();
		
		setScreen(new MatchScene(width, height));
	}
}

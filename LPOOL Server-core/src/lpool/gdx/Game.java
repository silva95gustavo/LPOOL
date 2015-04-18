package lpool.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	int width;
	int height;
	
	@Override
	public void create () {
		width = Gdx.graphics.getDesktopDisplayMode().width;
		height = Gdx.graphics.getDesktopDisplayMode().height;
		Gdx.graphics.setDisplayMode(width, height, true);
		batch = new SpriteBatch();
		img = new Texture("table.png");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0, width, height);
		batch.end();
	}
}

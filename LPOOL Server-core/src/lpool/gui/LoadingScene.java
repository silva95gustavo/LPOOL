package lpool.gui;

import lpool.gui.assets.Manager;
import lpool.gui.assets.Models;
import lpool.gui.assets.Sounds;
import lpool.gui.assets.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

public class LoadingScene implements Screen {

	private OrthographicCamera camera;
	
	private com.badlogic.gdx.Game GdxGame;
	private Manager manager;
	
	private int width, height;
	
	private BitmapFont font;
	private SpriteBatch batch;
	private NinePatch empty;
	private NinePatch full;
	
	private float percent;
	
	public LoadingScene(com.badlogic.gdx.Game GdxGame) {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(new Vector2(0, 0), 0);
		camera.update();
		
		this.GdxGame = GdxGame;
		manager = Manager.getInstance();
		
		Textures.getInstance();
		Sounds.getInstance();
		Models.getInstance();
		
		this.percent = 0;

		font=new BitmapFont();
		batch=new SpriteBatch();
		empty=new NinePatch(new TextureRegion(new Texture(Gdx.files.internal("loading/empty9.png")),24,24),8,8,8,8);
		full=new NinePatch(new TextureRegion(new Texture(Gdx.files.internal("loading/full9.png")),24,24),8,8,8,8);
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
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		if (manager.getAssetManager().update())
			GdxGame.setScreen(new LobbyScene(GdxGame));
		
		int barWidth = width / 2;
		int barHeight = barWidth / 20;
		int x = - barWidth / 2;
		int y = - barHeight / 2;
		
		percent = Interpolation.linear.apply(percent, manager.getAssetManager().getProgress(), 0.2f);
		
		if (percent < 0.5f)
			font.setColor(Color.BLACK);
		else
			font.setColor(Color.WHITE);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		empty.draw(batch, x, y, barWidth, barHeight);
		full.draw(batch, x, y, percent * barWidth, barHeight);
		String text = (int)(percent * 100) + "%";
		font.drawMultiLine(batch, text, 0, font.getMultiLineBounds(text).height / 2, 0, BitmapFont.HAlignment.CENTER);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
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

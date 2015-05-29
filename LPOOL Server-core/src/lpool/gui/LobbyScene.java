package lpool.gui;

import java.util.Observable;
import java.util.Observer;

import lpool.gui.assets.Manager;
import lpool.gui.assets.Textures;
import lpool.logic.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LobbyScene implements Screen, Observer {
	
	private int width;
	private int height;
	
	private com.badlogic.gdx.Game GdxGame;
	private Game game;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	
	private SpriteBatch batch;
	private Sprite QRCode;
	private Stage stage;
	
	private FadingColor fadingColor;
	
	public LobbyScene(com.badlogic.gdx.Game GdxGame, FadingColor fadingColor) {
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		
		this.GdxGame = GdxGame;
		
		camera = new OrthographicCamera();
		camera.position.set(new Vector2(0, 0), 0);
		camera.update();
		
		viewport = new FitViewport(1920, 1080, camera);
		
		QRCode = new Sprite(Textures.getInstance().getQRCode());
		
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
		stage = new Stage(new FitViewport(width, height), batch);
		
		game = new Game();
		game.getNetwork().addConnObserver(this);
		
		this.fadingColor = fadingColor;
		
		Textures.getInstance().getLobby().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	@Override
	public void update(Observable o, Object obj) {
		GdxGame.setScreen(new MatchScene(game, width, height));
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
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
		game.tick(delta);
		
		Color interpolated = fadingColor.tick(delta);
		Gdx.gl.glClearColor(interpolated.r, interpolated.g, interpolated.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(Textures.getInstance().getLobby(), -1920 / 2, -1080 / 2, 1920, 1080);
		batch.draw(QRCode, -150, -70, 300, 300);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		
		viewport.update(width, height);
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

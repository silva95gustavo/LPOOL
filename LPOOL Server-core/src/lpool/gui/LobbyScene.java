package lpool.gui;

import java.util.Observable;
import java.util.Observer;

import lpool.logic.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LobbyScene implements Screen, Observer {
	
	private int width;
	private int height;
	
	private com.badlogic.gdx.Game GdxGame;
	private Game game;
	
	private OrthographicCamera camera;
	
	private SpriteBatch batch;
	private Sprite QRCode;
	private Stage stage;
	
	public LobbyScene(int width, int height, String qr_dir, com.badlogic.gdx.Game GdxGame) {
		this.width = width;
		this.height = height;
		
		this.GdxGame = GdxGame;
		
		camera = new OrthographicCamera(width, height);
		camera.position.set(new Vector2(0, 0), 0);
		camera.update();
		
		QRCode = null;
		if(qr_dir != "") {
			QRCode = new Sprite(new Texture(qr_dir));
		}
		
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);
		stage = new Stage(new FitViewport(width, height), batch);
		
		game = new Game();
		game.getNetwork().addConnObserver(this);
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
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(QRCode, -height / 2, -height / 2, height, height);
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

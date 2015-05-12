package lpool.gdx;

import java.util.Observable;
import java.util.Observer;

import lpool.logic.Game;
import lpool.logic.Table;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class LobbyScene implements Screen, Observer {
	
	private int width;
	private int height;
	
	private com.badlogic.gdx.Game GdxGame;
	private Game game;
	
	private OrthographicCamera camera;
	
	private SpriteBatch batch;
	private Sprite QRSprite;
	
	public LobbyScene(int width, int height, String qr_dir, com.badlogic.gdx.Game GdxGame) {
		this.width = width;
		this.height = height;
		
		this.GdxGame = GdxGame;
		
		camera = new OrthographicCamera(width, height);
		camera.position.set(new Vector2(width / 2, height / 2), 0);
		camera.update();
		
		QRSprite = null;
		if(qr_dir != "") {
			Texture tex = new Texture(qr_dir);
			QRSprite = new Sprite(tex);
		}
		
		batch = new SpriteBatch();
		
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
		batch.begin();
		batch.draw(QRSprite, 0, 0, width, height);
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

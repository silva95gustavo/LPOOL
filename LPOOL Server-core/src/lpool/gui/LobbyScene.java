package lpool.gui;

import java.util.Observable;
import java.util.Observer;

import lpool.gui.assets.Fonts;
import lpool.gui.assets.Manager;
import lpool.gui.assets.Textures;
import lpool.logic.Game;
import lpool.network.Info;
import lpool.network.Network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LobbyScene implements Screen, Observer {
	private static final float startingTime = 3; /** Time between all players are connected and the match starts **/

	private int width;
	private int height;

	private com.badlogic.gdx.Game GdxGame;
	private Game game;

	private OrthographicCamera camera;
	private Viewport viewport;

	private ShaderBatch batch;
	private Sprite QRCode;
	private Sprite AndroidAppQRCode;

	private FadingColor fadingColor;

	private boolean player1;
	private boolean player2;

	private BitmapFont font;

	private float readyTime;
	private float brightness;

	public LobbyScene(lpool.logic.Game game, com.badlogic.gdx.Game GdxGame, FadingColor fadingColor) {
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.game = game;
		this.GdxGame = GdxGame;

		camera = new OrthographicCamera();
		camera.position.set(new Vector2(0, 0), 0);
		camera.update();

		viewport = new FitViewport(Textures.getInstance().getLobby().getWidth(), Textures.getInstance().getLobby().getHeight(), camera);

		QRCode = new Sprite(Textures.getInstance().getQRCode());
		AndroidAppQRCode = new Sprite(Textures.getInstance().getAndroidAppQRCode());

		//batch = new SpriteBatch();
		batch = new ShaderBatch(100);
		batch.setProjectionMatrix(camera.combined);

		game.getNetwork().addConnObserver(this);

		this.fadingColor = fadingColor;

		Textures.getInstance().getLobby().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		Textures.getInstance().getDisconnected().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		this.player1 = false;
		this.player2 = false;

		font = new BitmapFont();
		this.readyTime = 0;
	}

	@Override
	public void update(Observable o, Object obj) {
		int clientID = (Integer)obj;

		if (clientID == 0)
			player1 = !player1;

		if (clientID == 1)
			player2 = !player2;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void render(float delta) {
		game.tick(delta);
		renderBackground(delta);
		batch.setProjectionMatrix(camera.combined);
		renderLobby(batch);
		renderPlayerStatus(batch);
		renderQRCodes(batch);

		if (player1 && player2)
		{
			if (readyTime <= 0)
				GdxGame.setScreen(new MatchScene(game, GdxGame, width, height));
			else
			{
				readyTime -= delta;

				if (startingTime - readyTime <= 1)
					brightness = (readyTime - startingTime);
				else
					brightness = -1f;
				renderStartOverlay(batch);
			}
		}
		else
		{
			readyTime = startingTime;
			brightness = 0;
		}

		batch.end();
	}

	public void renderBackground(float delta)
	{
		float brightnessFactor = brightness + 1;
		Color interpolated = fadingColor.tick(delta);
		Gdx.gl.glClearColor(brightnessFactor * interpolated.r, brightnessFactor * interpolated.g, brightnessFactor * interpolated.b, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}
	
	public void renderLobby(ShaderBatch batch)
	{
		batch.brightness = brightness;
		batch.contrast = brightness + 1;
		batch.begin();		
		batch.draw(Textures.getInstance().getLobby(),
				-Textures.getInstance().getLobby().getWidth() / 2,
				-Textures.getInstance().getLobby().getHeight() / 2);
	}
	
	public void renderQRCodes(Batch batch)
	{
		Fonts.getInstance().getArial32().setColor(Color.WHITE);
		Fonts.getInstance().getArial32().drawMultiLine(batch, Info.getServerIP() + ":" + Info.getServerPort(), 0, -180, 0, BitmapFont.HAlignment.CENTER);
		Fonts.getInstance().getArial32().drawMultiLine(batch, Info.androidAppUrl, 0, -920, 0, BitmapFont.HAlignment.CENTER);
		batch.draw(QRCode, -300, -160, 600, 600);
		batch.draw(AndroidAppQRCode, -200, -900, 400, 400);
	}
	
	public void renderPlayerNames(Batch batch)
	{
		float displacement = 680;
		BitmapFont font = Fonts.getInstance().getBritannicBold72();
		font.setColor(Color.WHITE);
		font.drawMultiLine(batch, game.getPlayerName(0), -displacement, -400, 0, BitmapFont.HAlignment.CENTER);
		font.drawMultiLine(batch, game.getPlayerName(1), displacement, -400, 0, BitmapFont.HAlignment.CENTER);
	}
	
	public void renderPlayerStatus(Batch batch)
	{
		renderPlayerNames(batch);
		
		Texture connected = Textures.getInstance().getConnected();
		Texture disconnected = Textures.getInstance().getDisconnected();

		if (player1)
			batch.draw(connected, -960, -900, connected.getWidth(), connected.getHeight());
		else
			batch.draw(disconnected, -960, -900, disconnected.getWidth(), disconnected.getHeight());

		if (player2)
			batch.draw(connected, 412, -900, connected.getWidth(), connected.getHeight());
		else
			batch.draw(disconnected, 412, -900, disconnected.getWidth(), disconnected.getHeight());
	}
	
	public void renderStartOverlay(ShaderBatch batch)
	{
		batch.brightness = 0;
		batch.contrast = 1;
		batch.end();
		batch.begin();
		batch.draw(Textures.getInstance().getStartingIn(), -Textures.getInstance().getStartingIn().getWidth() / 2, -Textures.getInstance().getStartingIn().getHeight() / 2);
		Fonts.getInstance().getArial150().setColor(Color.BLACK);
		Fonts.getInstance().getArial150().drawMultiLine(batch, "" + Math.round(readyTime + 0.5f), 0, 0, 0, BitmapFont.HAlignment.CENTER);
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;

		viewport.update(width, height);
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
	}
}

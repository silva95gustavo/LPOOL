package lpool.gui.assets;

import lpool.gui.GameProject;
import lpool.logic.match.Match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Textures { // Singleton
	private static Textures instance = null;
	
	private Textures() {
		TextureLoader tl = new TextureLoader(Manager.getInstance().getIfhr());
		tl.loadAsync(Manager.getInstance().getAssetManager(), "match/table.png", Gdx.files.internal("match/table.png"), new TextureLoader.TextureParameter());
		
		AssetManager am = Manager.getInstance().getAssetManager();
		am.load("match/table.png", Texture.class);
		am.load("match/table_border.png", Texture.class);
		am.load(GameProject.QR_IP_DIR + GameProject.QR_IP_FILENAME, Texture.class);
		am.load(GameProject.QR_ANDROID_APP_DIR + GameProject.QR_ANDROID_APP_FILENAME, Texture.class);
		am.load("match/cue.png", Texture.class);
		am.load("match/cue_ball_prediction.png", Texture.class);
		am.load("match/cue_ball_prediction_blocked.png", Texture.class);
		am.load("match/background.png", Texture.class);
		am.load("match/name_background.png", Texture.class);
		am.load("balls/ball_shadow.png", Texture.class);
		am.load("lobby/lobby.png", Texture.class);
		am.load("lobby/disconnected.png", Texture.class);
		am.load("lobby/connected.png", Texture.class);
		am.load("lobby/starting_in.png", Texture.class);
		am.load("dialog_box.png", Texture.class);
		am.load("logo.png", Texture.class);
		
		for (int i = 0; i < 2 * Match.ballsPerPlayer + 2; i++)
		{
			am.load("balls/" + i + ".jpg", Texture.class);
			am.load("match/ball_icons/" + i + ".png", Texture.class);
		}
	}
	
	public static Textures getInstance()
	{
		if (instance == null)
			instance = new Textures();
		
		return instance;
	}
	
	public Texture getTable()
	{
		return Manager.getInstance().getAssetManager().get("match/table.png");
	}
	
	public Texture getTableBorder()
	{
		return Manager.getInstance().getAssetManager().get("match/table_border.png");
	}
	
	public Texture getQRCode()
	{
		return Manager.getInstance().getAssetManager().get(GameProject.QR_IP_DIR + GameProject.QR_IP_FILENAME);
	}
	
	public Texture getAndroidAppQRCode()
	{
		return Manager.getInstance().getAssetManager().get(GameProject.QR_ANDROID_APP_DIR + GameProject.QR_ANDROID_APP_FILENAME);
	}
	
	public Texture getCue()
	{
		return Manager.getInstance().getAssetManager().get("match/cue.png");
	}
	
	public Texture getNameBackground()
	{
		return Manager.getInstance().getAssetManager().get("match/name_background.png");
	}
	
	public Texture getBallShadow()
	{
		return Manager.getInstance().getAssetManager().get("balls/ball_shadow.png");
	}
	
	public Texture getCueBallPrediction()
	{
		return Manager.getInstance().getAssetManager().get("match/cue_ball_prediction.png");
	}
	
	public Texture getCueBallPredictionBlocked()
	{
		return Manager.getInstance().getAssetManager().get("match/cue_ball_prediction_blocked.png");
	}
	
	public Texture getBackground()
	{
		return Manager.getInstance().getAssetManager().get("match/background.png");
	}
	
	public Texture getLogo()
	{
		return Manager.getInstance().getAssetManager().get("logo.png");
	}
	
	public Texture getLobby()
	{
		return Manager.getInstance().getAssetManager().get("lobby/lobby.png");
	}
	
	public Texture getDisconnected()
	{
		return Manager.getInstance().getAssetManager().get("lobby/disconnected.png");
	}
	
	public Texture getConnected()
	{
		return Manager.getInstance().getAssetManager().get("lobby/connected.png");
	}
	
	public Texture getStartingIn()
	{
		return Manager.getInstance().getAssetManager().get("lobby/starting_in.png");
	}
	
	public Texture getDialogBox()
	{
		return Manager.getInstance().getAssetManager().get("dialog_box.png");
	}
	
	public Texture getBallIcon(int ballNumber)
	{
		return Manager.getInstance().getAssetManager().get("match/ball_icons/" + ballNumber + ".png");
	}
}

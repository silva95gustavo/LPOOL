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
		tl.loadAsync(Manager.getInstance().getAssetManager(), "table.png", Gdx.files.internal("table.png"), new TextureLoader.TextureParameter());
		
		AssetManager am = Manager.getInstance().getAssetManager();
		am.load("table.png", Texture.class);
		am.load(GameProject.QR_IP_DIR, Texture.class);
		am.load("cue.png", Texture.class);
		am.load("balls/ball_shadow.png", Texture.class);
		
		for (int i = 0; i < 2 * Match.ballsPerPlayer + 2; i++)
		{
			am.load("balls/" + i + ".jpg", Texture.class);
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
		return Manager.getInstance().getAssetManager().get("table.png");
	}
	
	public Texture getQRCode()
	{
		return Manager.getInstance().getAssetManager().get(GameProject.QR_IP_DIR);
	}
	
	public Texture getCue()
	{
		return Manager.getInstance().getAssetManager().get("cue.png");
	}
	
	public Texture getBallShadow()
	{
		return Manager.getInstance().getAssetManager().get("balls/ball_shadow.png");
	}
}

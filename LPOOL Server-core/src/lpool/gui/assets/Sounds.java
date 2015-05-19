package lpool.gui.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader.SoundParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class Sounds { // Singleton
	private static Sounds instance = null;
	
	private Sounds() {
		AssetManager am = Manager.getInstance().getAssetManager();
		am.load("ballBallCollision.wav", Sound.class);
	}
	
	public static Sounds getInstance()
	{
		if (instance == null)
			instance = new Sounds();
		
		return instance;
	}
	
	public Sound getBallBallCollision()
	{
		return Manager.getInstance().getAssetManager().get("ballBallCollision.wav");
	}
}

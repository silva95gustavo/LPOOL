package lpool.gdx.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.SoundLoader;
import com.badlogic.gdx.assets.loaders.SoundLoader.SoundParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class Sounds { // Singleton
	private static Sounds instance = null;
	
	private Sound ballBallCollision;
	
	private Sounds() {
		Manager.getInstance();
		SoundLoader soundLoader = new SoundLoader(Manager.getIfhr());
		
		//ballBallCollision = soundLoader.loadSync(Manager.getAssetManager(), "tailtoddle_lo.mp3", Gdx.files.internal("tailtoddle_lo.mp3"), new SoundParameter());
		ballBallCollision = Gdx.audio.newSound(Gdx.files.internal("ballBallCollision.wav"));
	}
	
	public static Sounds getInstance()
	{
		if (instance == null)
			instance = new Sounds();
		
		return instance;
	}
	
	public Sound getBallBallCollision()
	{
		return ballBallCollision;
	}
}

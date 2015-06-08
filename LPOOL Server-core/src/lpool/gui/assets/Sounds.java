package lpool.gui.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public class Sounds { // Singleton
	private static Sounds instance = null;
	
	private Sounds() {
		AssetManager am = Manager.getInstance().getAssetManager();
		am.load("ballBallCollision.wav", Sound.class);
		am.load("sounds/ball_hitting_ball.mp3", Sound.class);
		am.load("sounds/ball_in_hole.mp3", Sound.class);
		am.load("sounds/racking.mp3", Sound.class);
		am.load("sounds/cue_hitting_cue_ball.mp3", Sound.class);
		am.load("sounds/ball_hitting_border.mp3", Sound.class);
	}
	
	public static Sounds getInstance()
	{
		if (instance == null)
			instance = new Sounds();
		
		return instance;
	}
	
	public Sound getBallBallCollision()
	{
		if (Math.random() < 0.5f)
			return Manager.getInstance().getAssetManager().get("ballBallCollision.wav");
		else
			return Manager.getInstance().getAssetManager().get("sounds/ball_hitting_ball.mp3");
	}
	
	public Sound getBallInHole()
	{
		return Manager.getInstance().getAssetManager().get("sounds/ball_in_hole.mp3", Sound.class);
	}
	
	public Sound getRacking()
	{
		return Manager.getInstance().getAssetManager().get("sounds/racking.mp3", Sound.class);
	}
	
	public Sound getCueHittingCueBall()
	{
		return Manager.getInstance().getAssetManager().get("sounds/cue_hitting_cue_ball.mp3", Sound.class);
	}
	
	public Sound getBallHittingBorder()
	{
		return Manager.getInstance().getAssetManager().get("sounds/ball_hitting_border.mp3", Sound.class);
	}
}

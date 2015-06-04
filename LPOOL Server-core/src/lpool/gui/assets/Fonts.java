package lpool.gui.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Fonts { // Singleton
	private static Fonts instance = null;
	
	private Fonts() {
		AssetManager am = Manager.getInstance().getAssetManager();
		
		am.load("fonts/arial/32/font.fnt", BitmapFont.class);;
		am.load("fonts/arial/50/font.fnt", BitmapFont.class);;
		am.load("fonts/arial/100/font.fnt", BitmapFont.class);
		am.load("fonts/arial/150/font.fnt", BitmapFont.class);;
	}
	
	public static Fonts getInstance()
	{
		if (instance == null)
			instance = new Fonts();
		
		return instance;
	}
	
	public BitmapFont getArial32()
	{
		return Manager.getInstance().getAssetManager().get("fonts/arial/32/font.fnt");
	}
	
	public BitmapFont getArial50()
	{
		return Manager.getInstance().getAssetManager().get("fonts/arial/50/font.fnt");
	}
	
	public BitmapFont getArial100()
	{
		return Manager.getInstance().getAssetManager().get("fonts/arial/100/font.fnt");
	}
	
	public BitmapFont getArial150()
	{
		return Manager.getInstance().getAssetManager().get("fonts/arial/150/font.fnt");
	}
}

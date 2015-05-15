package lpool.gui.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

public class Manager { // Singleton
	private static Manager instance = null;
	private static InternalFileHandleResolver ifhr;
	private static AssetManager assetManager;
	
	private Manager() {
		ifhr = new InternalFileHandleResolver();
		assetManager = new AssetManager(ifhr);
	}
	
	public static Manager getInstance()
	{
		if (instance != null)
			return instance;
		
		return instance = new Manager();
	}
	
	public static AssetManager getAssetManager()
	{
		return assetManager;
	}
	
	public static InternalFileHandleResolver getIfhr()
	{
		return ifhr;
	}
}

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
	
	public AssetManager getAssetManager()
	{
		return assetManager;
	}
	
	public InternalFileHandleResolver getIfhr()
	{
		return ifhr;
	}
	
	public void dispose()
	{
		assetManager.dispose();
		assetManager = null;
		ifhr = null;
		instance = null;
	}
}

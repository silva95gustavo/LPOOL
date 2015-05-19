package lpool.gui.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.BaseJsonReader;
import com.badlogic.gdx.utils.JsonReader;

public class Model { // Singleton
	private static Model instance = null;
	
	private Model() {
		AssetManager am = Manager.getInstance().getAssetManager();
		am.setLoader(com.badlogic.gdx.graphics.g3d.Model.class, "g3dj", new G3dModelLoader(new JsonReader()));
		
		for (int i = 0; i < 16; i++)
			am.load("balls/" + i + ".g3dj", com.badlogic.gdx.graphics.g3d.Model.class);
	}
	
	public static Model getInstance()
	{
		if (instance == null)
			instance = new Model();
		
		return instance;
	}
	
	public com.badlogic.gdx.graphics.g3d.Model getBall(int ID)
	{
		return Manager.getInstance().getAssetManager().get("balls/" + ID + ".g3dj");
	}
}

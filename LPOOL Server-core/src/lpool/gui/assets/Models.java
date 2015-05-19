package lpool.gui.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.utils.BaseJsonReader;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;

public class Models { // Singleton
	private static Models instance = null;
	
	private Models() {
		AssetManager am = Manager.getInstance().getAssetManager();
		am.setLoader(com.badlogic.gdx.graphics.g3d.Model.class, "g3dj", new G3dModelLoader(new JsonReader()));
		am.setLoader(com.badlogic.gdx.graphics.g3d.Model.class, "g3db", new G3dModelLoader(new UBJsonReader()));
		am.setLoader(com.badlogic.gdx.graphics.g3d.Model.class, "obj", new ObjLoader());
		
		for (int i = 0; i < 16; i++)
			am.load("balls/" + i + ".g3dj", Model.class);
		am.load("table.g3db", Model.class);
	}
	
	public static Models getInstance()
	{
		if (instance == null)
			instance = new Models();
		
		return instance;
	}
	
	public Model getBall(int ID)
	{
		return Manager.getInstance().getAssetManager().get("balls/" + ID + ".g3dj");
	}
	
	public Model getTable()
	{
		return Manager.getInstance().getAssetManager().get("table.g3db");
	}
}

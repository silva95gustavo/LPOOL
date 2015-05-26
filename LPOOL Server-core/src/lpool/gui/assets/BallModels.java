package lpool.gui.assets;

import lpool.gui.BallModel;
import lpool.gui.assets.BallModelLoader.BallModelParameter;
import lpool.logic.ball.Ball;
import lpool.logic.match.Match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.BaseJsonReader;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.UBJsonReader;

public class BallModels { // Singleton
	private static BallModels instance = null;

	private static BallModel[] ballModel;

	private BallModels() {
		AssetManager am = Manager.getInstance().getAssetManager();
		am.setLoader(com.badlogic.gdx.graphics.g3d.Model.class, "g3dj", new G3dModelLoader(new JsonReader()));
		am.setLoader(com.badlogic.gdx.graphics.g3d.Model.class, "g3db", new G3dModelLoader(new UBJsonReader()));
		am.setLoader(com.badlogic.gdx.graphics.g3d.Model.class, "obj", new ObjLoader());
		am.setLoader(BallModel.class, new BallModelLoader(Manager.getInstance().getIfhr()));

		ballModel = new BallModel[2 * Match.ballsPerPlayer + 2];
		ModelBuilder modelBuilder = new ModelBuilder();

		for (int i = 0; i < 2 * Match.ballsPerPlayer + 2; i++)
		{
			BallModelParameter bmp = new BallModelParameter();
			bmp.number = i;
			am.load("balls/" + i + ".obj", BallModel.class, bmp);
		}
	}

	public static BallModels getInstance()
	{
		if (instance == null)
			instance = new BallModels();

		return instance;
	}

	public BallModel getBall(int ID)
	{
		return Manager.getInstance().getAssetManager().get("balls/" + ID + ".obj");
	}
}

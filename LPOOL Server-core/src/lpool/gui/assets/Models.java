package lpool.gui.assets;

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

public class Models { // Singleton
	private static Models instance = null;

	private static Model[] ballModel;

	private Models() {
		AssetManager am = Manager.getInstance().getAssetManager();
		am.setLoader(com.badlogic.gdx.graphics.g3d.Model.class, "g3dj", new G3dModelLoader(new JsonReader()));
		am.setLoader(com.badlogic.gdx.graphics.g3d.Model.class, "g3db", new G3dModelLoader(new UBJsonReader()));
		am.setLoader(com.badlogic.gdx.graphics.g3d.Model.class, "obj", new ObjLoader());

		ballModel = new Model[2 * Match.ballsPerPlayer + 2];
		ModelBuilder modelBuilder = new ModelBuilder();

		for (int i = 0; i < 2 * Match.ballsPerPlayer + 2; i++)
		{
			Texture ballTexture = new Texture(Gdx.files.internal("balls/" + i + ".jpg"));
			Material matBall = new Material(new TextureAttribute(TextureAttribute.Diffuse, ballTexture));
			ballModel[i] = modelBuilder.createSphere(2 * Ball.radius, 2 * Ball.radius, 2 * Ball.radius, 24, 24, matBall, Usage.Normal | Usage.Position | Usage.TextureCoordinates);
		}
	}

	public static Models getInstance()
	{
		if (instance == null)
			instance = new Models();

		return instance;
	}

	public Model getBall(int ID)
	{
		//return Manager.getInstance().getAssetManager().get("balls/" + ID + ".g3dj");
		return ballModel[ID];
	}

	public Model getTable()
	{
		return Manager.getInstance().getAssetManager().get("table.g3db");
	}
}

package lpool.gui.assets;

import lpool.gui.BallModel;
import lpool.logic.ball.Ball;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;

public class BallModelLoader extends SynchronousAssetLoader<BallModel, BallModelLoader.BallModelParameter> {

	public BallModelLoader(FileHandleResolver resolver) {
		super(resolver);
	}
	
	@Override
	public BallModel load(AssetManager manager, String fileName, FileHandle file, BallModelParameter parameter) {
		Texture ballTexture = manager.get("balls/" + parameter.number + ".jpg");
		Material matBall = new Material(new TextureAttribute(TextureAttribute.Diffuse, ballTexture), new TextureAttribute(TextureAttribute.Specular, ballTexture), new FloatAttribute(FloatAttribute.Shininess, 150));
		ModelBuilder mb = new ModelBuilder();
		Model model = mb.createSphere(2 * Ball.radius, 2 * Ball.radius, 2 * Ball.radius, 100, 100, matBall, Usage.Normal | Usage.Position | Usage.TextureCoordinates);
		return new BallModel(model);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, BallModelParameter parameter) {
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		
		FileHandle handle = resolve(fileName);
		
		deps.add(new AssetDescriptor("balls/" + parameter.number + ".jpg", Texture.class));
		return deps;
	}
	
	static public class BallModelParameter extends AssetLoaderParameters<BallModel> {
		public int number = 0;
	}
}

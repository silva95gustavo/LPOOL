package lpool.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class BallModel {
	private int number;
	private Model model;

	public BallModel(int number) {
		this.number = number;
		if (number > 0 && number <= 15)
		{
			ObjLoader loader = new ObjLoader();
			this.model = loader.loadModel(Gdx.files.internal("balls/" + number + ".obj"));
		}
		else
		{
			float diameter = 2 * lpool.logic.Ball.radius;
			this.model = new ModelBuilder().createSphere(diameter, diameter, diameter, 16, 16, new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE)), Usage.Position | Usage.Normal);
		}
	}

	public ModelInstance instanciateModel(Vector2 position)
	{
		ModelInstance ballModelInstance = new ModelInstance(model, position.x, position.y, 0);
		if (number > 0 && number <= 15)
		{
			ballModelInstance.transform.scl(0.027f);
			ballModelInstance.transform.rotateRad(new Vector3(1, 0, 0), (float)Math.PI/2);
			ballModelInstance.transform.rotateRad(new Vector3(0, 1, 0), (float)Math.PI/2);
		}
		return ballModelInstance;
	}

}

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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
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
			this.model = loader.loadModel(Gdx.files.internal("balls/1.obj"));
		}
		else
		{
			float diameter = 2 * lpool.logic.Ball.radius;
			this.model = new ModelBuilder().createSphere(diameter, diameter, diameter, 16, 16, new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE)), Usage.Position | Usage.Normal);
		}
	}

	public ModelInstance instanciateModel(Vector2 position, Quaternion rotation)
	{
		ModelInstance ballModelInstance = new ModelInstance(model, position.x, position.y, 0);
		if (number > 0 && number <= 15)
		{
			ballModelInstance.transform.scl(0.027f);
			Matrix4 rotationMatrix = new Matrix4();
			//rotationMatrix.setFromEulerAngles((float)Math.toDegrees(rotation.x), (float)Math.toDegrees(rotation.y), 0);
			rotationMatrix.set(new Vector3(position.x, position.y, 0), rotation);
			ballModelInstance.transform.mul(rotationMatrix);
		}
		return ballModelInstance;
	}

}

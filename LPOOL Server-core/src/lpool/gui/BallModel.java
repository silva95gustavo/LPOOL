package lpool.gui;

import lpool.logic.ball.Ball;
import lpool.logic.match.Match;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class BallModel {
	//private static final float scale = 0.025f * Match.physicsScaleFactor;
	private static final float scale = 1;
	private static final Vector3 scaleVec = new Vector3(scale, scale, scale);
	private Model model;

	public BallModel(Model model) {
		this.model = model;
	}

	public ModelInstance instanciateModel(Vector2 position, Quaternion rotation)
	{
		ModelInstance ballModelInstance = new ModelInstance(model, position.x, position.y, Ball.radius);
		ballModelInstance.transform.set(new Vector3(position.x, position.y, Ball.radius), rotation, scaleVec);
		return ballModelInstance;
	}

}

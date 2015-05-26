package lpool.gui;

import lpool.gui.assets.Manager;
import lpool.gui.assets.Models;
import lpool.logic.ball.Ball;
import lpool.logic.match.Match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;

public class BallModel {
	//private static final float scale = 0.025f * Match.physicsScaleFactor;
	private static final float scale = 0.1f * Match.physicsScaleFactor;
	private static final Vector3 scaleVec = new Vector3(scale, scale, scale);
	private int number;

	public BallModel(int number) {
		this.number = number;
	}

	public ModelInstance instanciateModel(Vector2 position, Quaternion rotation)
	{
		ModelInstance ballModelInstance = new ModelInstance(lpool.gui.assets.Models.getInstance().getBall(number), position.x, position.y, 0);
		ballModelInstance.transform.set(new Vector3(position.x, position.y, 0), rotation, scaleVec);
		return ballModelInstance;
	}

}

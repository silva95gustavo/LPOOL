package lpool.logic;

import java.util.Queue;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Ball {
	static public final float radius = 0.028575f;
	static public final float mass = 0.163f;
	public static final short cat = 0x0001;

	private int number;
	private Quaternion rotation;
	private boolean onTable = true;

	private Body body;
	private FixtureDef ballBallFixtureDef;
	private FixtureDef ballBorderFixtureDef;
	private Fixture ballBallFixture;
	private Fixture ballBorderFixture;
	
	private Queue<Body> ballsToBeDeleted;

	public Ball(World world, Vector2 position, int number, Queue<Body> ballsToBeDeleted) {
		rotation = new Quaternion();
		
		BodyDef bd = new BodyDef();
		bd.position.set(position);
		bd.type = BodyType.DynamicBody;

		CircleShape cs = new CircleShape();
		cs.setRadius(radius);

		// Ball
		ballBallFixtureDef = new FixtureDef();
		ballBallFixtureDef.shape = cs;
		ballBallFixtureDef.density = (float) (mass / (Math.PI * Math.pow(radius, 2)));
		ballBallFixtureDef.friction = 0.05f;
		ballBallFixtureDef.restitution = 0.80f;
		ballBallFixtureDef.filter.categoryBits = cat;
		ballBallFixtureDef.filter.maskBits = cat;

		// Ball <-> Border
		ballBorderFixtureDef = new FixtureDef();
		ballBorderFixtureDef.shape = cs;
		ballBorderFixtureDef.density = (float) (mass / (Math.PI * Math.pow(radius, 2)));
		ballBorderFixtureDef.friction = 1.0f;
		ballBorderFixtureDef.restitution = 0.5f;
		ballBorderFixtureDef.filter.categoryBits = cat;
		ballBorderFixtureDef.filter.maskBits = Table.cat;

		body = world.createBody(bd);
		ballBallFixture = body.createFixture(ballBallFixtureDef);
		ballBorderFixture = body.createFixture(ballBorderFixtureDef);
		body.setLinearDamping(0.4f);
		body.setAngularDamping(100.0f);
		body.setBullet(true);
		body.setUserData(new BodyInfo(BodyInfo.Type.BALL, number));

		this.number = number;
		this.ballsToBeDeleted = ballsToBeDeleted;
	}

	public int getNumber() {
		return number;
	}

	public Vector2 getPosition()
	{
		return body.getPosition();
	}

	public Quaternion getRotation()
	{
		return rotation.cpy();
	}

	public void tick(float deltaT)
	{
		if (!onTable)
			return;
		
		if (body.getLinearVelocity().len2() < 0.00015)
		{
			body.setLinearVelocity(new Vector2(0, 0));
		}
		else
		{
			Vector3 velocity = new Vector3(body.getLinearVelocity().x, body.getLinearVelocity().y, 0);
			Vector3 rotatingAxis = velocity.cpy().nor().crs(Vector3.Z);
			float rotationAmount = 45 * velocity.len() * deltaT / radius; // TODO Find out why we need to multiply by a value around 45
			Quaternion dRotation = new Quaternion(rotatingAxis, rotationAmount);
			rotation.mulLeft(dRotation);
		}
	}

	public void makeShot(float angle, float force)
	{
		body.applyLinearImpulse(new Vector2(force, 0).rotate((float)Math.toDegrees(angle)), new Vector2(0, 0), true);
	}

	public boolean isOnTable() {
		return onTable;
	}

	public void setOnTable(boolean onTable) {
		if (this.onTable == onTable)
			return;
		
		this.onTable = onTable;
		
		if (onTable)
		{
			// TODO
		}
		else
		{
			ballsToBeDeleted.add(body);
		}
	}
}

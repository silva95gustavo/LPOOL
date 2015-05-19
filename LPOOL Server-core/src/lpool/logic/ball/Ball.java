package lpool.logic.ball;

import java.util.Queue;

import lpool.logic.BodyInfo;
import lpool.logic.Table;
import lpool.logic.BodyInfo.Type;
import lpool.logic.match.Match;

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
	static public final float radius = 0.028575f * Match.physicsScaleFactor;
	static public final float mass = 0.163f;
	public static final short cat = 0x0001;

	private int number;
	private Quaternion rotation;
	private float lastAngle;
	private Vector2 position;
	private boolean onTable = true;
	private boolean visible = true;
	
	private lpool.logic.state.Context<Ball> stateMachine;

	private Body body;
	private FixtureDef ballBallFixtureDef;
	private FixtureDef ballBorderFixtureDef;
	private FixtureDef sensorFixtureDef;
	private Fixture ballBallFixture;
	private Fixture ballBorderFixture;
	private Fixture sensorFixture;
	
	private Queue<Body> ballsToBeDeleted;

	public Ball(World world, Vector2 position, int number, Queue<Body> ballsToBeDeleted) {
		rotation = new Quaternion();
		this.position = position;
		
		stateMachine = new lpool.logic.state.Context<Ball>(this, new OnTable());
		
		BodyDef bd = new BodyDef();
		bd.position.set(position);
		bd.type = BodyType.DynamicBody;

		ballBallFixtureDef = createBallBallFixtureDef();
		ballBorderFixtureDef = createBallBorderFixtureDef();
		
		CircleShape cs2 = new CircleShape(); // Sensor
		cs2.setRadius(2 * radius);
		sensorFixtureDef = new FixtureDef();
		sensorFixtureDef.shape = cs2;
		sensorFixtureDef.isSensor = true;
		
		body = world.createBody(bd);
		ballBallFixture = body.createFixture(ballBallFixtureDef);
		ballBallFixture.setUserData(new BodyInfo(BodyInfo.Type.BALL, number));
		ballBorderFixture = body.createFixture(ballBorderFixtureDef);
		ballBorderFixture.setUserData(new BodyInfo(BodyInfo.Type.BALL, number));
		sensorFixture = body.createFixture(sensorFixtureDef);
		sensorFixture.setUserData(new BodyInfo(BodyInfo.Type.BALL_SENSOR, number));
		body.setLinearDamping(0.5f);
		body.setAngularDamping(0.7f);
		body.setBullet(true);
		body.setUserData(new BodyInfo(BodyInfo.Type.BALL, number));
		
		lastAngle = body.getAngle();

		this.number = number;
		this.ballsToBeDeleted = ballsToBeDeleted;
	}

	public int getNumber() {
		return number;
	}
	
	public void updatePosition()
	{
		this.position = body.getPosition().cpy();
	}
	
	public void setPosition(Vector2 position)
	{
		this.position = position;
	}

	public Vector2 getPosition()
	{
		return position.cpy();
	}

	public Quaternion getRotation()
	{
		return rotation.cpy();
	}

	public void setVelocity(Vector2 velocity)
	{
		body.setLinearVelocity(velocity);
	}
	
	public Vector2 getVelocity()
	{
		return body.getLinearVelocity();
	}

	public void tick(float deltaT)
	{
		stateMachine.update(deltaT);
		
		if (!onTable)
			return;
		
		if (body.getLinearVelocity().len() < 0.0125 * Match.physicsScaleFactor)
		{
			body.setLinearVelocity(new Vector2(0, 0));
		}
		else
		{
			float rotationScalar = 45; // TODO Find out why we need to multiply by a value around 45
			
			Vector3 velocity = new Vector3(body.getLinearVelocity().x, body.getLinearVelocity().y, 0);
			Vector3 rotatingAxis = velocity.cpy().nor().crs(Vector3.Z);
			float rotationAmount = rotationScalar * velocity.len() * deltaT / radius;
			Quaternion dRotation = new Quaternion(rotatingAxis, rotationAmount);
			rotation.mulLeft(dRotation);
			
			Quaternion dAngle = new Quaternion(Vector3.Z, (body.getAngle() - lastAngle) * rotationScalar);
			lastAngle = body.getAngle();
			rotation.mulLeft(dAngle);
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
		if (number == 0)
			return;
		
		if (this.onTable == onTable)
			return;
		
		this.onTable = onTable;
		
		if (onTable)
		{
			// TODO
		}
		else
		{
			stateMachine.changeState(new EnteringHole(0)); // TODO change 0 to a number obtained with a box2d contact listener
		}
	}
	
	public static FixtureDef createBallBallFixtureDef()
	{	
		CircleShape cs = new CircleShape();
		cs.setRadius(radius);
		
		FixtureDef ballBallFixtureDef = new FixtureDef();
		ballBallFixtureDef.shape = cs;
		ballBallFixtureDef.density = (float) (mass / (Math.PI * Math.pow(radius, 2)));
		ballBallFixtureDef.friction = 0.05f;
		ballBallFixtureDef.restitution = 0.95f;
		ballBallFixtureDef.filter.categoryBits = cat;
		ballBallFixtureDef.filter.maskBits = cat;
		
		return ballBallFixtureDef;
	}
	
	public static FixtureDef createBallBorderFixtureDef()
	{
		CircleShape cs = new CircleShape();
		cs.setRadius(radius);
		
		// Ball <-> Border
		FixtureDef ballBorderFixtureDef = new FixtureDef();
		ballBorderFixtureDef.shape = cs;
		ballBorderFixtureDef.density = (float) (mass / (Math.PI * Math.pow(radius, 2)));
		ballBorderFixtureDef.friction = 1.0f;
		ballBorderFixtureDef.restitution = 0.6f;
		ballBorderFixtureDef.filter.categoryBits = cat;
		ballBorderFixtureDef.filter.maskBits = Table.cat;
		
		return ballBorderFixtureDef;
	}
	
	public lpool.logic.state.Context<Ball> getStateMachine()
	{
		return stateMachine;
	}
	
	public void setToBeDeleted()
	{
		ballsToBeDeleted.add(body);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isStopped()
	{
		return body.getAngularVelocity() == 0 || body.getLinearVelocity().equals(new Vector2(0, 0));
	}
}

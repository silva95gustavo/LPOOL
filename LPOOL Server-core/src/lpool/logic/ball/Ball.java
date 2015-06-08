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
	static public final Vector2 hiddenPos = new Vector2(-999, -999);
	public static final short cat = 0x0001;

	private int number;
	private Quaternion rotation;
	private float lastAngle;
	private Vector3 horSpin;
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
	
	public enum Type
	{
		CUE,
		SOLID,
		BLACK,
		STRIPE
	}

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
		body.setAngularDamping(1f);
		body.setBullet(true);
		body.setUserData(new BodyInfo(BodyInfo.Type.BALL, number));
		
		lastAngle = body.getAngle();
		horSpin = new Vector3(0, 0, 0);

		this.number = number;
		this.ballsToBeDeleted = ballsToBeDeleted;
	}

	public int getNumber() {
		return number;
	}
	
	public void updatePosition()
	{
		if (body == null)
			this.position = hiddenPos;
		else
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
		if (body != null)
			body.setLinearVelocity(velocity);
	}
	
	public Vector2 getVelocity()
	{
		if (body == null)
			return new Vector2(0, 0);
		else
			return body.getLinearVelocity();
	}

	public void tick(float deltaT)
	{
		updatePosition();
		stateMachine.update(deltaT);
		
		if (!onTable || body == null)
			return;
		
		if (body.getLinearVelocity().len() < 0.0125 * Match.physicsScaleFactor)
		{
			body.setLinearVelocity(new Vector2(0, 0));
		}
		if (horSpin.len() < 0.0125 * Match.physicsScaleFactor)
		{
			horSpin.scl(0);
		}
		if (Math.abs(body.getAngularVelocity()) < 0.0125 * Match.physicsScaleFactor)
		{
			body.setAngularVelocity(0);
		}
		if (!isStopped())
		{
			float rotationScalar = (float)Math.toDegrees(1); // Radians to degrees
			
			Vector3 velocity = new Vector3(body.getLinearVelocity().x, body.getLinearVelocity().y, 0);
			Vector3 rotatingAxis = Vector3.Z.cpy().crs(velocity.cpy().nor());
			float rotationAmount = rotationScalar * velocity.len() * deltaT / radius;
			Quaternion dRotation = new Quaternion(rotatingAxis, rotationAmount);
			rotation.mulLeft(dRotation);
			
			Quaternion dAngle = new Quaternion(Vector3.Z, (body.getAngle() - lastAngle) * rotationScalar);
			lastAngle = body.getAngle();
			rotation.mulLeft(dAngle);
			
			rotatingAxis = Vector3.Z.cpy().crs(horSpin.cpy().nor());
			rotationAmount = rotationScalar * horSpin.len() * deltaT / radius;
			Quaternion dHorSpin = new Quaternion(rotatingAxis, rotationAmount);
			rotation.mulLeft(dHorSpin);
			
			Vector2 dHS = new Vector2(horSpin.x, horSpin.y).scl(deltaT);
			body.setLinearVelocity(body.getLinearVelocity().add(dHS));
			horSpin.scl(1 - 2 * deltaT);
		}
	}

	public void makeShot(float angle, float force, float xSpin, float ySpin)
	{
		if (body == null)
			return;
		body.applyLinearImpulse(new Vector2(2 * force * Match.physicsScaleFactor, 0).rotateRad(angle), body.getPosition().cpy(), true);
		body.setAngularVelocity(force * xSpin * 150);
		horSpin = new Vector3(4f * force * ySpin, 0, 0).rotateRad(Vector3.Z, angle);
		horSpin.scl(force * Match.physicsScaleFactor);
	}

	public boolean isOnTable() {
		return onTable;
	}

	public void enterHole(int holeNumber) {
		if (!this.visible || !this.onTable)
			return;
		
		this.onTable = false;
		
		stateMachine.changeState(new EnteringHole(holeNumber));
	}
	
	public static FixtureDef createBallBallFixtureDef()
	{	
		CircleShape cs = new CircleShape();
		cs.setRadius(radius);
		
		FixtureDef ballBallFixtureDef = new FixtureDef();
		ballBallFixtureDef.shape = cs;
		ballBallFixtureDef.density = (float) (mass / (Math.PI * Math.pow(radius, 2)));
		ballBallFixtureDef.friction = 0.07f;
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
		ballBorderFixtureDef.friction = 2.0f;
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
		this.onTable = false;
		if (body != null && ballsToBeDeleted != null)
			ballsToBeDeleted.add(body);
		body = null;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isStopped()
	{
		if (stateMachine.getCurrentState().getClass() == EnteringHole.class)
			return false;
		
		if (body == null)
			return true;
		
		if (stateMachine.getCurrentState().getClass() == InHole.class)
			return true;
		
		if (body.getAngularVelocity() != 0)
			return false;
		
		if (!(horSpin.len2() == 0))
			return false;
		
		if (!body.getLinearVelocity().equals(new Vector2(0, 0)))
			return false;
		
		return true;
	}
	
	public boolean isStripped()
	{
		return number >= 9 && number <= 15;
	}
	
	public boolean isSolid()
	{
		return number >= 1 && number <= 7;
	}
	
	public Type getType()
	{
		if (number == 0)
			return Type.CUE;
		if (isSolid())
			return Type.SOLID;
		if (number == 8)
			return Type.BLACK;
		if (isStripped())
			return Type.STRIPE;
		return null;
	}
	public void stop()
	{
		if (body == null)
			return;
		body.setLinearVelocity(new Vector2(0, 0));
		body.setAngularVelocity(0);
		horSpin = new Vector3(0, 0, 0);
	}
}

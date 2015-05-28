package lpool.logic.match;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Random;

import lpool.gui.assets.Sounds;
import lpool.logic.BodyInfo;
import lpool.logic.ObservableCollision;
import lpool.logic.Table;
import lpool.logic.BodyInfo.Type;
import lpool.logic.ball.Ball;
import lpool.logic.state.Context;
import lpool.network.Network;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class Match implements Observer{
	public static int ballsPerPlayer = 7;
	public static float physicsScaleFactor = 10;

	private lpool.logic.state.Context<Match> stateMachine;
	private Network network;

	private Vector2 gravity;
	private World world;
	private Queue<Body> ballsToBeDeleted;

	private Ball[] balls1;
	private Ball[] balls2;
	private Ball blackBall;
	private Ball cueBall;
	private Ball[] balls;

	private Table border;

	private float cueAngle = (float)Math.PI;

	private ObservableCollision observableCollision;

	private boolean aiming;

	private Ball createBall(World world, int posID, int number)
	{
		switch (posID)
		{
		case 0: return createBall(world, 25, 0, number);
		case 1: return createBall(world, 0, 0, number);
		case 2: return createBall(world, -1, 1, number);
		case 3: return createBall(world, -1, -1, number);
		case 4: return createBall(world, -2, 2, number);
		case 5: return createBall(world, -2, 0, number);
		case 6: return createBall(world, -2, -2, number);
		case 7: return createBall(world, -3, 3, number);
		case 8: return createBall(world, -3, 1, number);
		case 9: return createBall(world, -3, -1, number);
		case 10: return createBall(world, -3, -3, number);
		case 11: return createBall(world, -4, 4, number);
		case 12: return createBall(world, -4, 2, number);
		case 13: return createBall(world, -4, 0, number);
		case 14: return createBall(world, -4, -2, number);
		case 15: return createBall(world, -4, -4, number);
		default: return null;
		}
	}
	
	private Ball createBall(World world, int x, int y, int number)
	{
		Vector2 pos = new Vector2((Table.width - 2 * Table.border) / 4 + (float)Math.sqrt(3) * Ball.radius * x, Table.height / 2 + y * Ball.radius);
		pos.add(new Vector2((float)Math.random() * 0.01f * Ball.radius, (float)Math.random() * 0.01f * Ball.radius)); // noise
		Ball ball = new Ball(world, pos, number, ballsToBeDeleted);
		if (number == 0)
			cueBall = ball;
		else if (number < 8)
			balls1[number - 1] = ball;
		else if (number == 8)
			blackBall = ball;
		else if (number < 16)
			balls2[number - 9] = ball;
		else return null;
		return balls[number] = ball;
	}

	private void createBalls()
	{		
		createBall(world, 25, 0, 0);
		
		int[] rack = Racker.rack();
		
		for (int i = 1; i < rack.length; i++)
		{
			createBall(world, rack[i], i);
		}
	}

	public Match(Network network) {
		stateMachine = new Context<Match>(this, new FreezeTime());
		this.network = network;

		gravity = new Vector2(0, 0);
		world = new World(gravity, false);
		World.setVelocityThreshold(0.00001f);
		world.setContactListener(observableCollision = new ObservableCollision());
		addColisionObserver(this);
		ballsToBeDeleted = new LinkedList<Body>();

		balls1 = new Ball[ballsPerPlayer];
		balls2 = new Ball[ballsPerPlayer];
		balls = new Ball[ballsPerPlayer * 2 + 2];

		createBalls();

		border = new Table(world);
		
	}

	public void tick(float dt)
	{
		stateMachine.update(dt);
	}

	public void worldStep(float dt)
	{
		world.step(dt, 60, 20);
	}

	public void tickBalls(float dt)
	{
		for (int i = 0; i < balls.length; i++)
		{
			balls[i].tick(dt);
		}
	}

	public void deleteRemovedballs()
	{
		while (!ballsToBeDeleted.isEmpty())
		{
			world.destroyBody(ballsToBeDeleted.poll());
		}
	}

	public Ball getBlackBall() {
		return blackBall;
	}

	public Ball[] getBalls1()
	{
		return balls1;
	}

	public Ball[] getBalls2()
	{
		return balls2;
	}

	public Ball getCueBall() {
		return cueBall;
	}

	public Ball[] getBalls()
	{
		return balls;
	}

	public void setCueAngle(float angle)
	{
		cueAngle = angle;
	}

	public void makeShot(float force)
	{
		cueBall.makeShot(cueAngle, force);
	}

	public float getCueAngle()
	{
		return cueAngle;
	}

	public Vector2[] predictShot()
	{
		/*
		 * result:
		 * 0 - cue ball position
		 * 1 - 2nd ball position
		 * 2 - cue ball prediction
		 * 3 - 2nd ball prediction
		 * 4 - aiming point
		 * all results must be checked for not being null
		 */
		final boolean[] b = new boolean[1];
		b[0] = false;
		final Vector2[] result = new Vector2[5];
		RayCastCallback callBack = new RayCastCallback() {
			float smallestDistance;
			boolean foundAimingPoint = false;
			float closestAP;

			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
			{
				if (fixture.getUserData() == null)
					return -1;
				if ((((BodyInfo)fixture.getUserData()).getType() == BodyInfo.Type.BALL_SENSOR && ((BodyInfo)fixture.getUserData()).getID() != 0) || 
						((BodyInfo)fixture.getUserData()).getType() == BodyInfo.Type.TABLE)
				{
					float distance = point.dst2(cueBall.getPosition());
					if (foundAimingPoint)
					{
						if (distance < closestAP)
						{
							result[4] = point.cpy();
							closestAP = distance;
						}
					}
					else
					{
						foundAimingPoint = true;
						result[4] = point.cpy();
						closestAP = distance;
					}
				}
				if (((BodyInfo)fixture.getUserData()).getType() != BodyInfo.Type.BALL_SENSOR)
					return -1;
				if (((BodyInfo)fixture.getUserData()).getID() == 0)
					return -1;
				if (!b[0] || cueBall.getPosition().dst2(point) < smallestDistance)
				{
					b[0] = true;
					smallestDistance = cueBall.getPosition().dst2(point);
					result[0] = point.cpy();
					result[1] = fixture.getBody().getPosition().cpy();
					result[2] = normal.cpy().rotateRad((float)Math.PI / 2).scl((float)Math.sin(cueBall.getPosition().cpy().sub(result[0]).angleRad(normal)));
					result[3] = normal.cpy().rotateRad((float)Math.PI).scl((float)Math.cos(cueBall.getPosition().cpy().sub(result[0]).angleRad(normal)));
				}
				return 1;
			}
		};
		float diagonal = (float)((new Vector2(Table.width, Table.height)).len());
		world.rayCast(callBack, cueBall.getPosition(), cueBall.getPosition().cpy().add(new Vector2(1, 0).scl(diagonal).rotateRad(cueAngle)));
		if (!b[0])
		{
			result[0] = null;
			result[1] = null;
			result[2] = null;
			result[3] = null;
		}
		return result;
	}

	public void addColisionObserver(Observer o)
	{
		observableCollision.addObserver(o);
	}

	@Override
	public void update(Observable o, Object obj) {
		Contact contact = (Contact)obj;

		BodyInfo userDataA = ((BodyInfo)contact.getFixtureA().getUserData());
		BodyInfo userDataB = ((BodyInfo)contact.getFixtureB().getUserData());

		if (userDataA == null || userDataB == null)
			return;

		switch (userDataA.getType())
		{
		case BALL:
			if (userDataB.getType() == BodyInfo.Type.HOLE)
				ballInHoleHandler(userDataA.getID(), userDataB.getID());
			break;
		case TABLE:
			break;
		case HOLE:
			if (userDataB.getType() == BodyInfo.Type.BALL)
				ballInHoleHandler(userDataB.getID(), userDataA.getID());
			break;
		case BALL_SENSOR:
			break;
		default:
			break;
		}
	}

	private void ballInHoleHandler(int ballNumber, int holeNumber)
	{
		balls[ballNumber].enterHole(holeNumber);
	}

	public lpool.logic.state.Context<Match> getStateMachine() {
		return stateMachine;
	}

	public Network getNetwork()
	{
		return network;
	}

	public boolean isAiming() {
		return aiming;
	}

	public void setAiming(boolean isAiming) {
		this.aiming = isAiming;
	}
	
	public World getWorld()
	{
		return world;
	}
}

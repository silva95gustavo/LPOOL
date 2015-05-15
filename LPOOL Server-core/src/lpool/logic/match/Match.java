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

	private Ball createBall(World world, int x, int y, int number)
	{
		Ball ball = new Ball(world, new Vector2((Table.width - 2 * Table.border) / 4 + (float)Math.sqrt(3) * Ball.radius * x, Table.height / 2 + y * Ball.radius), number, ballsToBeDeleted);
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
		createBall(world, 0, 0, 1);
		createBall(world, -1, 1, 3);
		createBall(world, -1, -1, 11);
		createBall(world, -2, 2, 14);
		createBall(world, -2, 0, 8);
		createBall(world, -2, -2, 6);
		createBall(world, -3, 3, 9);
		createBall(world, -3, 1, 4);
		createBall(world, -3, -1, 15);
		createBall(world, -3, -3, 13);
		createBall(world, -4, 4, 12);
		createBall(world, -4, 2, 5);
		createBall(world, -4, 0, 10);
		createBall(world, -4, -2, 2);
		createBall(world, -4, -4, 7);
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
		balls[ballNumber].setOnTable(false);
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

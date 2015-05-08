package lpool.logic;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Random;

import lpool.gdx.assets.Sounds;

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
	
	public Match() {
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
		world.step(dt, 6, 2);
		while (!ballsToBeDeleted.isEmpty())
		{
			world.destroyBody(ballsToBeDeleted.poll());
		}
		for (int i = 0; i < balls.length; i++)
		{
			balls[i].tick(dt);
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
	
	/*public Vector2[] predictShot()
	{
		/*final Vector2[] n = new Vector2[2];
		n[0] = new Vector2(1, 0);
		n[1] = new Vector2(-999, -999);
		RayCastCallback callBack = new RayCastCallback() {
			
			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
			{
				if (cueBall.getPosition().dst2(point) < cueBall.getPosition().dst2(n[1]))
				{
					n[0] = normal;
					n[1] = point;
				}
				return 1;
			}
		};
		float diagonal = (float)((new Vector2(Table.width, Table.height)).len());
		Vector2 copy = new Vector2(cueBall.getPosition().cpy());
		//copy = new Vector2(0, 0);
		world.rayCast(callBack, copy, cueBall.getPosition().add(new Vector2(1, 0).scl(diagonal).rotate((float)Math.toDegrees(cueAngle))));
		return n;
	
		World predWorld = new World(gravity, false);
		Ball predCueBall = new Ball(world, cueBall.getPosition(), cueBall.getNumber());
	}*/
	
	public void addColisionObserver(Observer o)
	{
		observableCollision.addObserver(o);
	}

	@Override
	public void update(Observable o, Object obj) {
		Contact contact = (Contact)obj;
		
		BodyInfo userDataA = ((BodyInfo)contact.getFixtureA().getBody().getUserData());
		BodyInfo userDataB = ((BodyInfo)contact.getFixtureB().getBody().getUserData());
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
		}
	}
	
	private void ballInHoleHandler(int ballNumber, int holeNumber)
	{
		System.out.println("derp: " + ballNumber);
		balls[ballNumber].setOnTable(false);
	}
}

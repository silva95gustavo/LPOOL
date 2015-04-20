package lpool.logic;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

public class Match {
	public static int ballsPerPlayer = 7;

	private Vector2 gravity;
	private World world;

	private Ball[] balls1;
	private Ball[] balls2;
	private Ball blackBall;
	private Ball cueBall;
	private Table border;
	
	private float cueAngle = (float)Math.PI;

	private void createBalls()
	{
		float x = (float)Math.sqrt(3) * Ball.radius; // Obtained with the Pythagorean Theorem
		int column = 0;
		int columnILast = -1;
		for (int i = 0; i < ballsPerPlayer * 2 + 1; i++)
		{
			Ball ball = new Ball(world, new Vector2(Table.width / 4 - column * x, Table.height / 2 - (i - columnILast - 1 - column) * Ball.radius), 10);
			if (i % 2 == 0 && i != 2)
				balls1[((i > 2) ? i - 1 : i) / 2] = ball;
			else
				balls2[i / 2] = ball;
			if (i - columnILast == column + 1)
			{
				column++;
				columnILast = i;
			}
		}
		
		blackBall = new Ball(world, new Vector2(Table.width / 4 - x, Table.height / 2), 8);
	}
	
	public Match() {
		gravity = new Vector2(0, 0);
		world = new World(gravity, false);
		World.setVelocityThreshold(0.00001f);

		balls1 = new Ball[ballsPerPlayer];
		balls2 = new Ball[ballsPerPlayer];
		
		createBalls();
		
		Random r = new Random();
		/*float x = (float)Math.sqrt(3) * Ball.radius;
		
		if (ballsPerPlayer == 7)
		{
			balls1[0] = new Ball(world, new Vector2(Table.width / 4, Table.height / 2), 1);
			balls2[0] = new Ball(world, new Vector2(Table.width / 4 - x, Table.height / 2 - Ball.radius), 9);
			balls1[1] = new Ball(world, new Vector2(Table.width / 4 - x, Table.height / 2 + Ball.radius), 2);
			balls2[1] = new Ball(world, new Vector2(Table.width / 4 - 2 * x, Table.height / 2 - 2 * Ball.radius), 10);
			balls1[2] = new Ball(world, new Vector2(Table.width / 4 - 2 * x, Table.height / 2), 3);
			balls2[2] = new Ball(world, new Vector2(Table.width / 4 - 2 * x, Table.height / 2 + 2 * Ball.radius), 11);
			ballsPerPlayer = 3;
		}
		else
		{
			for (int i = 0; i < ballsPerPlayer; i++)
			{
				balls1[i] = new Ball(world, new Vector2(r.nextFloat() * Table.width, r.nextFloat() * Table.height), i + 1);
				balls2[i] = new Ball(world, new Vector2(r.nextFloat() * Table.width, r.nextFloat() * Table.height), i + 9);
			}
		}*/
		cueBall = new Ball(world, new Vector2(3 * Table.width / 4, Table.height / 2), 0);
		
		border = new Table(world);

	}

	public void tick(float dt)
	{
		world.step(dt, 6, 2);
		for (int i = 0; i < ballsPerPlayer; i++)
		{
			balls1[i].tick();
			balls2[i].tick();
		}
		blackBall.tick();
		cueBall.tick();
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
}

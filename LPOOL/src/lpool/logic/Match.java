package lpool.logic;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Match {
	public static int ballsPerPlayer = 7;

	private Vector2 gravity;
	private World world;

	private Ball[] balls1;
	private Ball[] balls2;
	private Ball blackBall;
	private Ball cueBall;
	private Border border;

	public Match() {
		gravity = new Vector2(0, 0);
		world = new World(gravity, false);
		World.setVelocityThreshold(0.00001f);

		balls1 = new Ball[ballsPerPlayer];
		balls2 = new Ball[ballsPerPlayer];
		Random r = new Random();
		if (ballsPerPlayer == 7)
		{
			balls1[0] = new Ball(world, new Vector2(Border.width / 4, Border.height / 2), 1);
			balls2[0] = new Ball(world, new Vector2(Border.width / 4 - 2* Ball.radius, Border.height / 2 - Ball.radius), 9);
			balls1[1] = new Ball(world, new Vector2(Border.width / 4 - 2 * Ball.radius, Border.height / 2 + Ball.radius), 2);
			balls2[1] = new Ball(world, new Vector2(Border.width / 4 - 4 * Ball.radius, Border.height / 2 - 2 * Ball.radius), 10);
			balls1[2] = new Ball(world, new Vector2(Border.width / 4 - 4 * Ball.radius, Border.height / 2), 3);
			balls2[2] = new Ball(world, new Vector2(Border.width / 4 - 4 * Ball.radius, Border.height / 2 + 2 * Ball.radius), 11);
			ballsPerPlayer = 3;
		}
		else
		{
			for (int i = 0; i < ballsPerPlayer; i++)
			{
				balls1[i] = new Ball(world, new Vector2(r.nextFloat() * Border.width, r.nextFloat() * Border.height), i + 1);
				balls2[i] = new Ball(world, new Vector2(r.nextFloat() * Border.width, r.nextFloat() * Border.height), i + 9);
			}
		}

		blackBall = new Ball(world, new Vector2(r.nextFloat() * Border.width, r.nextFloat() * Border.height), 8);
		cueBall = new Ball(world, new Vector2(3 * Border.width / 4, Border.height / 2), 0);
		
		border = new Border(world);

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
}

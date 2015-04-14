package lpool.logic;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Match {
	public static final int ballsPerPlayer = 7;
	
	private Vector2 gravity;
	private World world;
	
	private Ball[] balls1;
	private Ball[] balls2;
	private Ball blackBall;
	private Border border;
	
	public Match() {
		gravity = new Vector2(0, 0);
		world = new World(gravity, false);
		World.setVelocityThreshold(0.00001f);
		
		balls1 = new Ball[ballsPerPlayer];
		balls2 = new Ball[ballsPerPlayer];
		Random r = new Random();
		for (int i = 0; i < ballsPerPlayer; i++)
		{
			balls1[i] = new Ball(world, new Vector2(r.nextFloat() * Border.width, r.nextFloat() * Border.height), i + 1);
			balls2[i] = new Ball(world, new Vector2(r.nextFloat() * Border.width, r.nextFloat() * Border.height), i + 9);
		}
		blackBall = new Ball(world, new Vector2(r.nextFloat() * Border.width, r.nextFloat() * Border.height), 8);
		
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
}

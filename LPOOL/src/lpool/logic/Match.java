package lpool.logic;

import java.util.Random;

public class Match {
	public static final int ballsPerPlayer = 7;
	
	private Ball[] balls1;
	private Ball[] balls2;
	private Ball blackBall;
	
	public Match() {
		balls1 = new Ball[ballsPerPlayer];
		balls2 = new Ball[ballsPerPlayer];
		Random r = new Random();
		for (int i = 0; i < ballsPerPlayer; i++)
		{
			balls1[i] = new Ball(new Vector2D(r.nextDouble() * 300, r.nextDouble() * 300), i + 1);
			balls2[i] = new Ball(new Vector2D(r.nextDouble() * 300, r.nextDouble() * 300), i + 9);
		}
		blackBall = new Ball(new Vector2D(r.nextDouble() * 300, r.nextDouble() * 300), 8);
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

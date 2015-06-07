package lpool.test;

import static org.junit.Assert.*;
import lpool.logic.ball.Ball;
import lpool.logic.match.PlayValidator;
import lpool.logic.match.Racker;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class MatchTest {

	@Test
	public void testRacker() {
		for (int i = 0; i < 100; i++)
		{
			rackerTestIteration();
		}
	}

	
	private void rackerTestIteration()
	{
		int[] result = Racker.rack();
		assertEquals(1, result[1]);
		
		boolean[] visited = new boolean[result.length];
		int left = 8;
		int right = 8;
		for (int i = 1; i < result.length; i++)
		{
			assertEquals(false, visited[i]);
			visited[i] = true;
			if (result[i] == 11)
				left = i;
			if (result[i] == 15)
				right = i;
		}
		
		for (int i = 1; i < result.length; i++)
		{
			assertEquals(true, visited[i]);
		}
		
		assertEquals(false, left < 8 && right < 8);
		assertEquals(false, left > 8 && right > 8);
	}
	
	@Test
	public void testPlayValidator()
	{
		Ball[] balls = new Ball[16];
		for (int i = 0; i < balls.length; i++)
			balls[i] = new Ball(createDefaultWorld(), new Vector2(0, 0), i, null);
		PlayValidator pv = new PlayValidator(true, false, null, balls);
		pv.actionBallHit(Ball.Type.SOLID);
		assertEquals(false, pv.isValid());
		pv.actionBallHit(Ball.Type.STRIPE);
		assertEquals(false, pv.isValid());
		pv.actionBorderHit();
		assertEquals(false, pv.isValid());
		pv.actionBorderHit();
		assertEquals(false, pv.isValid());
		pv.actionBorderHit();
		assertEquals(false, pv.isValid());
		pv.actionBorderHit();
		assertEquals(true, pv.isValid());
		pv.actionBallScore(Ball.Type.SOLID);
		assertEquals(true, pv.isValid());
		pv.actionBallScore(Ball.Type.CUE);
		assertEquals(false, pv.isValid());
	}
	
	private World createDefaultWorld()
	{
		return new World(new Vector2(0, 0), false);
	}
}

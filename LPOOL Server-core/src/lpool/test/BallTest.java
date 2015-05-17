package lpool.test;

import static org.junit.Assert.*;
import lpool.logic.ball.Ball;
import lpool.logic.ball.OnTable;
import lpool.logic.match.Match;
import lpool.network.Network;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class BallTest {
	
	@Test
	public void testCreation() {		
		Vector2 pos = new Vector2(0, 0);
		Ball b = new Ball(createDefaultWorld(), pos, 14, null);
		
		assertEquals(b.getNumber(), 14);
		assertEquals(b.getPosition(), pos);
		assertEquals(b.getStateMachine().getCurrentState().getClass(), new OnTable().getClass());
	}

	@Test
	public void testPosition() {
		Vector2 pos = new Vector2(0, 0);
		Ball b = new Ball(createDefaultWorld(), pos, 4, null);
		
		Vector2 newPos = new Vector2(5, 5.666f);
		b.setPosition(newPos);
		
		assertEquals(b.getPosition(), newPos);
	}
	
	@Test
	public void testMakeShot() {
		World w = createDefaultWorld();
		Vector2 pos = new Vector2(0, 0);
		Ball b = new Ball(w, pos, 4, null);
		
		b.makeShot(0, 1000);
		w.step(1, 10, 10);
		b.tick(1);
		
		assertTrue(b.getPosition().x > 0);
		assertEquals(b.getPosition().y, 0, 0.001 * Match.physicsScaleFactor);
		assertTrue(b.getVelocity().x >= 0);
		assertEquals(b.getVelocity().y, 0, 0.001 * Match.physicsScaleFactor);
	}
	
	private World createDefaultWorld()
	{
		return new World(new Vector2(0, 0), false);
	}
	
}

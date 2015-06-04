package lpool.test;

import static org.junit.Assert.*;
import lpool.logic.BodyInfo;
import lpool.logic.ball.Ball;
import lpool.logic.ball.OnTable;
import lpool.logic.match.Match;
import lpool.network.Network;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class BallTest {
	
	@Test
	public void testCreation() {		
		Vector2 pos = new Vector2(0, 0);
		Ball b = new Ball(createDefaultWorld(), pos, 14, null);
		
		assertEquals(14, b.getNumber());
		assertEquals(pos, b.getPosition());
		assertEquals(new OnTable().getClass(), b.getStateMachine().getCurrentState().getClass());
	}

	@Test
	public void testPosition() {
		Vector2 pos = new Vector2(0, 0);
		Ball b = new Ball(createDefaultWorld(), pos, 4, null);
		
		Vector2 newPos = new Vector2(5, 5.666f);
		b.setPosition(newPos);
		
		assertEquals(newPos, b.getPosition());
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
		assertEquals(0, b.getPosition().y, 0.001 * Match.physicsScaleFactor);
		//assertTrue(b.getVelocity().x >= 0);
		assertEquals(0, b.getVelocity().y, 0.001 * Match.physicsScaleFactor);
	}
	
	@Test
	public void testCollision() {
		World w = createDefaultWorld();
		Vector2 pos1 = new Vector2(-1, 0);
		Vector2 pos2 = new Vector2(1, 0);
		Ball b1 = new Ball(w, pos1, 0, null);
		Ball b2 = new Ball(w, pos2, 8, null);
		
		final Vector2 r[] = new Vector2[3];
		r[0] = new Vector2(10, 10);
		r[1] = new Vector2(10, 10);
		r[2] = new Vector2(10, 10);
		w.setContactListener(new ContactListener() {

			@Override
			public void beginContact(Contact arg0) {
				if (!arg0.isTouching())
					return;
				
				if (((BodyInfo)arg0.getFixtureA().getBody().getUserData()).getType() == BodyInfo.Type.BALL && ((BodyInfo)arg0.getFixtureB().getBody().getUserData()).getType() == BodyInfo.Type.BALL)
				{
					r[0] = arg0.getWorldManifold().getPoints()[0];
					
					if (((BodyInfo)arg0.getFixtureA().getBody().getUserData()).getID() == 0)
					{
						r[1] = arg0.getFixtureA().getBody().getPosition();
						r[2] = arg0.getFixtureB().getBody().getPosition();
					}
					else
					{
						r[1] = arg0.getFixtureB().getBody().getPosition();
						r[2] = arg0.getFixtureA().getBody().getPosition();
					}
				}
			}

			@Override
			public void endContact(Contact arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact arg0, ContactImpulse arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void preSolve(Contact arg0, Manifold arg1) {
				// TODO Auto-generated method stub
				
			}
			
		});
		b1.makeShot(0, 10000);
		b2.makeShot((float)Math.PI, 10000);
		w.step(10, 10, 10);
		assertEquals(0, r[0].x, 0.01 * Match.physicsScaleFactor);
		assertEquals(0, r[0].y, 0.01 * Match.physicsScaleFactor);
		Vector2 expectedPos1 = new Vector2(-Ball.radius, 0);
		Vector2 expectedPos2 = new Vector2(Ball.radius, 0);
		assertEquals(expectedPos1.x, r[1].x, 0.01 * Match.physicsScaleFactor);
		assertEquals(expectedPos1.y, r[1].y, 0.01 * Match.physicsScaleFactor);
		assertEquals(expectedPos2.x, r[2].x, 0.01 * Match.physicsScaleFactor);
		assertEquals(expectedPos2.y, r[2].y, 0.01 * Match.physicsScaleFactor);
	}
	
	private World createDefaultWorld()
	{
		return new World(new Vector2(0, 0), false);
	}
	
}

package lpool.test;

import static org.junit.Assert.*;
import lpool.logic.BodyInfo;
import lpool.logic.ball.Ball;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class BodyInfoTest {

	@Test
	public void testCreation() {
		BodyInfo bd = new BodyInfo(BodyInfo.Type.BALL, 3);
		
		assertEquals(BodyInfo.Type.BALL, bd.getType());
		assertEquals(3, bd.getID());
		assertEquals("BodyInfo [type=" + BodyInfo.Type.BALL + ", ID=3]", "" + bd);
	}

}

package lpool.logic;

import java.util.Random;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Ball {
	static public final float radius = 10;
	private int number;
	
	// Box2D object body stuff
	BodyDef bd;
	CircleShape cs;
	FixtureDef fd;
	Body body;
	
	public Ball(World world, Vec2 position, int number) {
		bd = new BodyDef();
		bd.position.set(position);
		bd.type = BodyType.DYNAMIC;
		
		cs = new CircleShape();
		cs.m_radius = radius;
		
		fd = new FixtureDef();
		fd.shape = cs;
		fd.density = 1.0f;
		fd.friction = 1.0f;
		fd.restitution = 10.0f;
		
		body = world.createBody(bd);
		body.createFixture(fd);
		Random r = new Random();
		body.applyLinearImpulse(new Vec2(r.nextFloat() * 10, r.nextFloat() * 10), new Vec2(0, 0));
		body.setLinearDamping(0.3f);
		
		this.number = number;
	}

	public int getNumber() {
		return number;
	}
	
	public Vec2 getPosition()
	{
		return body.getPosition();
	}
	
	public void tick()
	{
		//body.setLinearDamping(0.5f);
	}
}

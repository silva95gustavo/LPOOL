package lpool.logic;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Border {
	public static final float width = 2.74f;
	public static final float height = width / 2;
	public static final float border = 0.07f;
	
	private Body right;
	private Body top;
	private Body left;
	private Body bottom;
	
	public Border(World world) {
		// RIGHT
		BodyDef bd = new BodyDef();
		bd.position.set(width - border, height / 2);
		bd.type = BodyType.STATIC;
		
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(border, height);
		
		FixtureDef fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 5f;
		fd.friction = 1.0f;
		fd.restitution = 0.0001f;
		
		right = world.createBody(bd);
		right.createFixture(fd);
		
		
		// TOP
		bd = new BodyDef();
		bd.position.set(0, 0);
		bd.type = BodyType.STATIC;
		
		ps = new PolygonShape();
		ps.setAsBox(width, border);
		
		fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 5f;
		fd.friction = 0.7f;
		fd.restitution = -99999999f;
		
		top = world.createBody(bd);
		top.createFixture(fd);
		
		
		// LEFT
		bd = new BodyDef();
		bd.position.set(0, 0);
		bd.type = BodyType.STATIC;
		
		ps = new PolygonShape();
		ps.setAsBox(border, height);
		
		fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 59999999f;
		fd.friction = 1.0f;
		fd.restitution = 0.1f;
		
		left = world.createBody(bd);
		left.createFixture(fd);
		
		
		// BOTTOM
		bd = new BodyDef();
		bd.position.set(0, height - border);
		bd.type = BodyType.STATIC;
		
		ps = new PolygonShape();
		ps.setAsBox(width, border);
		
		fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 5f;
		fd.friction = 1.0f;
		fd.restitution = 0.0001f;
		
		bottom = world.createBody(bd);
		bottom.createFixture(fd);
	}

}

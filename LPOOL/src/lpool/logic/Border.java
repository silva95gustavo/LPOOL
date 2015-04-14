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
	
	private Body body;
	
	public Border(World world) {
		BodyDef bd = new BodyDef();
		bd.position.set(0, 0);
		bd.type = BodyType.STATIC;
		
		Vec2[] vertices = {
				new Vec2(- width / 2, height / 2),
				new Vec2(width / 2, height / 2),
				new Vec2(- width / 2, - height / 2),
				new Vec2(width / 2, height / 2)
		};
		
		PolygonShape ps = new PolygonShape();
		ps.set(vertices, vertices.length);
		ps.m_centroid.set(bd.position);
		
		FixtureDef fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 0.5f;
		fd.friction = 0.3f;
		fd.restitution = 0.5f;
		
		body = world.createBody(bd);
		body.createFixture(fd);
	}

}

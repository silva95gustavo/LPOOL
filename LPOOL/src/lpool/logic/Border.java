package lpool.logic;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Border {
	public static final short cat = 0x0001;
	public static final float width = 2.74f;
	public static final float height = width / 2;
	public static final float border = 0.07f;
	
	private Body right;
	private Body top;
	private Body left;
	private Body bottom;
	
	public Border(World world) {
		float rest = 0.1f;
		
		// RIGHT
		BodyDef bd = new BodyDef();
		bd.position.set(width - border, height / 2);
		bd.type = BodyType.StaticBody;
		
		PolygonShape ps = new PolygonShape();
		ps.setAsBox(border, height);
		
		FixtureDef fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 5f;
		fd.friction = 1.0f;
		fd.restitution = rest;
		
		right = world.createBody(bd);
		right.createFixture(fd);
		
		
		// TOP
		bd = new BodyDef();
		bd.position.set(0, 0);
		bd.type = BodyType.StaticBody;
		
		ps = new PolygonShape();
		ps.setAsBox(width, border);
		
		fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 5f;
		fd.friction = 0.7f;
		fd.restitution = rest;
		
		top = world.createBody(bd);
		top.createFixture(fd);
		
		
		// LEFT
		bd = new BodyDef();
		bd.position.set(0, 0);
		bd.type = BodyType.StaticBody;
		
		ps = new PolygonShape();
		ps.setAsBox(border, height);
		
		fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 59999999f;
		fd.friction = 1.0f;
		fd.restitution = rest;
		
		left = world.createBody(bd);
		left.createFixture(fd);
		
		
		// BOTTOM
		bd = new BodyDef();
		bd.position.set(0, height - border);
		bd.type = BodyType.StaticBody;
		
		ps = new PolygonShape();
		ps.setAsBox(width, border);
		
		fd = new FixtureDef();
		fd.shape = ps;
		fd.density = 5f;
		fd.friction = 1.0f;
		fd.restitution = rest;
		
		bottom = world.createBody(bd);
		bottom.createFixture(fd);
	}

}

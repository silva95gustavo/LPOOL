package lpool.logic;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Border {
	public static final short cat = 0x0002;
	public static final float width = 2.74f;
	public static final float height = width / 2;
	public static final float border = 0.07f;
	
	private Body right;
	private Body top;
	private Body left;
	private Body bottom;
	private Body body;
	
	public Border(World world) {
		float rest = 0.5f;
		
		//FileHandle fh = 
		//BodyEditorLoader loader = InternalLoader.loadBodyFromJSON("lpool.json");
		System.out.println(Gdx.files.internal("lpool.json"));
		
		/*// RIGHT
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
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = Ball.cat;
		
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
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = Ball.cat;
		
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
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = Ball.cat;
		
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
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = Ball.cat;
		
		bottom = world.createBody(bd);
		bottom.createFixture(fd);*/
		
		// 1. Create a BodyDef, as usual.
	    BodyDef bd = new BodyDef();
	    bd.position.set(0, 0);
	    bd.type = BodyType.StaticBody;
	 
	    // 2. Create a FixtureDef, as usual.
	    FixtureDef fd = new FixtureDef();
	    fd.density = 5f;
	    fd.restitution = rest;
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = Ball.cat;
	 
	    // 3. Create a Body, as usual.
	    body = world.createBody(bd);
	 
	    // 4. Create the body fixture automatically by using the loader.
	    //loader.attachFixture(body, "lpool", fd, width);
	}

}

package lpool.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Table {
	public static final short cat = 0x0002;
	public static final float border = 0.125f;
	public static final float height = 1.1f + 2 * border;
	public static final float width = (height - 2 * border) * 2 + 2 * border;
	public static final float holeOffset = border / 6;
	public static final float holeRadius = 0.1f;
	public static final int numHoles = 6;
	
	private Body body;
	private Body[] holes = new Body[numHoles];
	
	public Table(World world) {
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("lpool.json"));
		
		createTable(world, loader);
	}
	
	private void createTable(World world, BodyEditorLoader loader)
	{
		// 1. Create a BodyDef, as usual.
	    BodyDef bd = new BodyDef();
	    bd.position.set(0, 0);
	    bd.type = BodyType.StaticBody;
	 
	    // 2. Create a FixtureDef, as usual.
	    FixtureDef fd = new FixtureDef();
	    fd.density = 5f;
	    fd.restitution = 0.5f;
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = Ball.cat;
	 
	    // 3. Create a Body, as usual.
	    body = world.createBody(bd);
	 
	    // 4. Create the body fixture automatically by using the loader.
	    loader.attachFixture(body, "table", fd, width);
	}
	
	private void createHole(World world)
	{
		BodyDef bd = new BodyDef();
		bd.position.set(0, 0);
		bd.type = BodyType.StaticBody;
		
		FixtureDef fd = new FixtureDef();
		CircleShape cs = new CircleShape();
		fd.shape = cs;
		
		holes[0] = world.createBody(bd);
		
		holes[0].createFixture(cs, 1f);
	}

}

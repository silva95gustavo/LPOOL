package lpool.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Table {
	public static final short cat = 0x0002;
	public static final float width = 2.74f;
	public static final float height = width * 0.547953964f;
	
	private Body body;
	
	public Table(World world) {
		float rest = 0.5f;
		
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("lpool.json"));
		
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
	    loader.attachFixture(body, "Table", fd, width);
	}

}

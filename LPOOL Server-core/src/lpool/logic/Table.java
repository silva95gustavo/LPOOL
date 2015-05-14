package lpool.logic;

import lpool.logic.match.Match;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Table {
	public static final short cat = 0x0002;
	public static final float border = 0.125f * Match.physicsScaleFactor;
	public static final float height = 1.1f * Match.physicsScaleFactor + 2 * border;
	public static final float width = (height - 2 * border) * 2 + 2 * border;
	public static final float holeOffset = border / 6;
	public static final float holeRadius = 0.045f * Match.physicsScaleFactor;
	public static final int numHoles = 6;
	
	private Body body;
	private Body[] holes = new Body[numHoles];
	
	public Table(World world) {
		BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("lpool.json"));
		
		createTable(world, loader);
		createHole(world, 0, border - holeOffset, height - border + holeOffset); // Top left
		createHole(world, 1, width / 2, height - border + holeOffset); // Top middle
		createHole(world, 2, width - border + holeOffset, height - border + holeOffset); // Top right
		createHole(world, 3, border - holeOffset, border - holeOffset); // Bottom left
		createHole(world, 4, width / 2, border - holeOffset); // Bottom middle
		createHole(world, 5, width - border + holeOffset, border - holeOffset); // Bottom right
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
	    fd.restitution = 0.7f;
		fd.filter.categoryBits = cat;
		fd.filter.maskBits = Ball.cat;
	 
	    // 3. Create a Body, as usual.
	    body = world.createBody(bd);
	    body.setUserData(new BodyInfo(BodyInfo.Type.TABLE, 0));
	 
	    // 4. Create the body fixture automatically by using the loader.
	    loader.attachFixture(body, "table", fd, width);
	    
	    body.getFixtureList().get(body.getFixtureList().size - 1).setUserData(new BodyInfo(BodyInfo.Type.TABLE, 0)); 
	    Array<Fixture> fixtureList = body.getFixtureList();
	    for (int i = 0; i < fixtureList.size; i++)
	    {
	    	fixtureList.get(i).setUserData(new BodyInfo(BodyInfo.Type.TABLE, 0));
	    }
	}
	
	private void createHole(World world, int number, float x, float y)
	{
		BodyDef bd = new BodyDef();
		bd.position.set(x, y);
		bd.type = BodyType.StaticBody;
		
		CircleShape cs = new CircleShape();
		cs.setRadius(holeRadius - Ball.radius);
		
		holes[number] = world.createBody(bd);
		holes[number].createFixture(cs, 1f).setUserData(new BodyInfo(BodyInfo.Type.HOLE, number));
		holes[number].setUserData(new BodyInfo(BodyInfo.Type.HOLE, number));
	}

}

package lpool.logic;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Ball {
	static public final float radius = 0.028575f;
	static public final float mass = 0.163f;
	public static final short cat = 0x0001;

	private int number;
	private Quaternion rotation;

	private Body body;

	public Ball(World world, Vector2 position, int number) {
		rotation = new Quaternion();

		BodyDef bd = new BodyDef();
		bd.position.set(position);
		bd.type = BodyType.DynamicBody;

		CircleShape cs = new CircleShape();
		cs.setRadius(radius);

		// Ball
		FixtureDef fd1 = new FixtureDef();
		fd1.shape = cs;
		fd1.density = (float) (mass / (Math.PI * Math.pow(radius, 2)));
		fd1.friction = 0.05f;
		fd1.restitution = 0.80f;
		fd1.filter.categoryBits = cat;
		fd1.filter.maskBits = cat;

		// Ball <-> Border
		FixtureDef fd2 = new FixtureDef();
		fd2.shape = cs;
		fd2.density = (float) (mass / (Math.PI * Math.pow(radius, 2)));
		fd2.friction = 1.0f;
		fd2.restitution = 0.5f;
		fd2.filter.categoryBits = cat;
		fd2.filter.maskBits = Table.cat;

		body = world.createBody(bd);
		body.createFixture(fd1);
		body.createFixture(fd2);
		body.setLinearDamping(0.4f);
		body.setAngularDamping(100.0f);
		body.setBullet(true);

		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public Vector2 getPosition()
	{
		return body.getPosition();
	}

	public Quaternion getRotation()
	{
		return rotation.cpy();
	}

	public void tick(float deltaT)
	{
		if (body.getLinearVelocity().len2() < 0.00015)
		{
			body.setLinearVelocity(new Vector2(0, 0));
		}
		else
		{
			Vector3 velocity = new Vector3(body.getLinearVelocity().x, body.getLinearVelocity().y, 0);
			Vector3 rotatingAxis = velocity.cpy().nor().crs(Vector3.Z);
			float rotationAmount = 40 * velocity.len() * deltaT / radius; // TODO Find out why we need to multiply by a value around 40
			Quaternion dRotation = new Quaternion(rotatingAxis, rotationAmount);
			rotation.mulLeft(dRotation);
		}
	}

	public void makeShot(float angle, float force)
	{
		body.applyLinearImpulse(new Vector2(force, 0).rotate((float)Math.toDegrees(angle)), new Vector2(0, 0), true);
	}
}

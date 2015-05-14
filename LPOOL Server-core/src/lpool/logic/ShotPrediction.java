package lpool.logic;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class ShotPrediction {
	private World world;
	private Body cueBall;
	private boolean collided = false;
	private boolean collidedEnd = false;
	Fixture second;
	Contact c;

	/*
	 * result:
	 * 0 - cue ball position
	 * 1 - 2nd ball position
	 * 2 - cue ball prediction
	 * 3 - 2nd ball prediction
	 * 4 - aiming point
	 * all results must be checked for not being null
	 */
	private Vector2[] result = new Vector2[5];

	public ShotPrediction(final Ball[] balls) {
		world = new World(new Vector2(0, 0), false);
		World.setVelocityThreshold(0.00001f);
		world.setContactListener(new ContactListener() {

			@Override
			public void beginContact(Contact contact) {
				if (!contact.isTouching())
					return;
				if (!collided)
				{
					boolean foundCueBall = false;
					boolean cueIsA = false;

					if (((BodyInfo)contact.getFixtureA().getBody().getUserData()).getType() == BodyInfo.Type.BALL && ((BodyInfo)contact.getFixtureA().getBody().getUserData()).getID() == 0)
						foundCueBall = cueIsA = true;

					if (((BodyInfo)contact.getFixtureB().getBody().getUserData()).getType() == BodyInfo.Type.BALL && ((BodyInfo)contact.getFixtureB().getBody().getUserData()).getID() == 0)
						foundCueBall = true;

					if (foundCueBall)
					{
						if (cueIsA)
							second = contact.getFixtureB();
						else
							second = contact.getFixtureA();

						Vector2 secondPos = second.getBody().getPosition().cpy();

						result[0] = cueBall.getPosition().cpy();
						result[1] = ((BodyInfo)second.getBody().getUserData()).getType() == BodyInfo.Type.BALL ? secondPos : contact.getWorldManifold().getPoints()[0];

						Vector2 normal = result[0].cpy().sub(result[1]);

						//result[2] = normal.cpy().rotateRad((float)Math.PI / 2).scl((float)Math.sin(cueBall.getPosition().cpy().sub(result[0]).angleRad(normal)));
						//result[3] = normal.cpy().rotateRad((float)Math.PI).scl((float)Math.cos(cueBall.getPosition().cpy().sub(result[0]).angleRad(normal)));
						collided = true;
					}
				}
			}

			@Override
			public void endContact(Contact contact) {
				if (collided && !collidedEnd)
				{
					boolean foundCueBall = false;
					boolean cueIsA = false;

					if (((BodyInfo)contact.getFixtureA().getBody().getUserData()).getType() == BodyInfo.Type.BALL && ((BodyInfo)contact.getFixtureA().getBody().getUserData()).getID() == 0)
						foundCueBall = cueIsA = true;

					if (((BodyInfo)contact.getFixtureB().getBody().getUserData()).getType() == BodyInfo.Type.BALL && ((BodyInfo)contact.getFixtureB().getBody().getUserData()).getID() == 0)
						foundCueBall = true;

					if (foundCueBall)
					{
						if (cueIsA)
							second = contact.getFixtureB();
						else
							second = contact.getFixtureA();

						result[2] = cueBall.getLinearVelocity().cpy();
						result[3] = second.getBody().getLinearVelocity().cpy();
						collidedEnd = true;
					}
				}
			}

			@Override
			public void postSolve(Contact arg0, ContactImpulse arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void preSolve(Contact arg0, Manifold arg1) {
				// TODO Auto-generated method stub

			}

		});
		for (int i = 0; i < balls.length; i++)
		{
			BodyDef bd = new BodyDef();
			bd.position.set(balls[i].getPosition());
			bd.type = BodyType.DynamicBody;

			Body body = world.createBody(bd);
			body.setUserData(new BodyInfo(BodyInfo.Type.BALL, i));
			body.createFixture(Ball.createBallBallFixtureDef());
			body.setBullet(true);

			if (i == 0)
			{
				cueBall = body;
				body.createFixture(Ball.createBallBorderFixtureDef()).setUserData(new BodyInfo(BodyInfo.Type.BALL, 0));
			}
		}
		new Table(world);
	}

	public Vector2[] predict(float angle)
	{	
		cueBall.applyLinearImpulse(new Vector2(10000, 0).rotateRad(angle), new Vector2(0, 0), true);

		int i = 0;
		collided = collidedEnd = false;
		while (!collided && i < 1000)
		{
			world.step(0.01f, 5, 5);
			i++;
		}
		return result;
	}
}

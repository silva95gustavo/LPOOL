package lpool.logic;

import java.util.Observable;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class ObservableCollision extends Observable implements ContactListener {
	
	@Override
	public void beginContact(Contact contact) {
		if (!contact.isTouching())
			return;
		
		setChanged();
		notifyObservers(contact);
	}

	@Override
	public void endContact(Contact contact) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}
}
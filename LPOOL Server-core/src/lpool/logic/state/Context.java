package lpool.logic.state;

public class Context<E> {

	private E owner;
	private State<E> previousState;
	private State<E> currentState;
	
	public Context(E owner, State<E> initialState) {
		this.owner = owner;
		previousState = null;
		currentState = initialState;
		currentState.enter(owner);
	}

	public State<E> getPreviousState() {
		return previousState;
	}

	public State<E> getCurrentState() {
		return currentState;
	}
	
	public void changeState(State<E> newState) {
		currentState.exit(owner);
		previousState = currentState;
		currentState = newState;
		currentState.enter(owner);
	}
	
	public void update(float dt)
	{
		if (currentState != null)
			currentState.update(owner, dt);
	}
}

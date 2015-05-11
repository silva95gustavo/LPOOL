package lpool.logic.state;

public class Context {

	private State previousState;
	private State currentState;
	
	public Context(State initialState) {
		this.previousState = null;
		this.currentState = initialState;
	}

	public State getPreviousState() {
		return previousState;
	}
	
	public State request()
	{
		return getCurrentState();
	}

	public State getCurrentState() {
		return currentState;
	}
	
	public void changeState(State newState) {
		previousState = currentState;
		
		if (currentState != null)
			currentState.exit();
		
		currentState = newState;
		
		if (currentState != null)
			currentState.enter();
	}
}

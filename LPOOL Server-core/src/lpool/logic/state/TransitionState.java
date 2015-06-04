package lpool.logic.state;

public class TransitionState<E> implements State<E> {
	private State<E> previous;
	private State<E> next;
	protected float time;
	
	protected TransitionState(State<E> previous, State<E> next) {
		super();
		this.previous = previous;
		this.next = next;
		this.time = 0;
	}

	@Override
	public void enter(E owner) {
	}

	@Override
	public void update(E owner, float dt) {
		time += dt;
	}

	protected State<E> getPreviousState()
	{
		return previous;
	}
	
	protected State<E> getNextState()
	{
		return next;
	}
}

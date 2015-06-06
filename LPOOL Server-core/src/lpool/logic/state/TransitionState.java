package lpool.logic.state;

public class TransitionState<E> implements State<E> {
	private Context<E> context;
	private State<E> previous;
	private State<E> next;
	protected float time;
	
	public TransitionState(Context<E> context, State<E> previous, State<E> next) {
		super();
		this.context = context;
		this.previous = previous;
		this.next = next;
	}

	@Override
	public void enter(E owner) {
	}

	@Override
	public void update(E owner, float dt) {
	}

	public State<E> getPreviousState()
	{
		return previous;
	}
	
	public State<E> getNextState()
	{
		return next;
	}
	
	public void next()
	{
		context.changeState(next);
	}

	@Override
	public void exit(E owner) {
		// TODO Auto-generated method stub
		
	}
}

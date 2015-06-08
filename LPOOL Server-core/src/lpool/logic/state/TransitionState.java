package lpool.logic.state;

public class TransitionState<E> implements State<E> {
	private Context<E> context;
	private State<E> previous;
	private State<E> next;
	protected float time;
	public Object data;
	
	public TransitionState(Context<E> context, State<E> previous, State<E> next) {
		super();
		this.context = context;
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
	}
	
	public float getTime()
	{
		return time;
	}
}

package lpool.logic.state;

public interface State<E> {
	public void enter(E owner);
	public void update(E owner, float dt);
}

package lpool.logic.state;

public interface State {
	public void enter();
	public void update(float dt);
	public void exit();
}

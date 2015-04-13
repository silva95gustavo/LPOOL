package lpool.logic;

public class Ball {
	static private final double drag = 0.01;
	private int number;
	
	private Vector2D pos;
	private Vector2D velocity;
	
	public Ball(Vector2D pos, int number) {
		this.pos = pos;
		this.velocity = new Vector2D(100, 0);
		this.number = number;
	}
	
	public void tick(double t)
	{
		pos = pos.add(velocity.scale(t));
		velocity = velocity.scale(1 - drag);
	}
	
	public Vector2D getPos()
	{
		return pos;
	}

	public int getNumber() {
		return number;
	}
}

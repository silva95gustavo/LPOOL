package lpool.logic;

public class Vector2D {
	public double x;
	public double y;
	
	public Vector2D()
	{
		this.x = this.y = 0;
	}
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double distance(Vector2D v)
	{
		return subtract(v).length();
	}
	
	public Vector2D add(Vector2D v)
	{
		return new Vector2D(x + v.x, y + v.y);
	}
	
	public Vector2D subtract(Vector2D v)
	{
		return new Vector2D(x - v.x, y - v.y);
	}
	
	public Vector2D rotate(double degrees)
	{
		double sin = Math.sin(degrees);
		double cos = Math.cos(degrees);
		return new Vector2D(x * cos - y * sin, x * sin + y * cos);
	}
	
	public double squareLength()
	{
		return Math.pow(x, 2) + Math.pow(y, 2);
	}
	
	public double length()
	{
		return Math.sqrt(squareLength());
	}
	
	public Vector2D scale(double factor)
	{
		return new Vector2D(x * factor, y * factor);
	}
	
	public double dotProduct(Vector2D v)
	{
		return x * v.x + y * v.y;
	}
	
	public Vector2D normalize()
	{
		Vector2D v = new Vector2D();
		double length = length();
		if (length != 0)
		{
			v.x = x / length;
			v.y = y / length;
		}
		return v;
	}
}

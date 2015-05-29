package lpool.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class FadingColor {

	private Color last;
	private Color current;
	private float t;
	private float period;
	
	public FadingColor(float period) {
		this(period, generateRandomColor());
	}

	public FadingColor(float period, Color start)
	{
		this.t = 0;
		this.period = period;
		this.last = start;
		this.current = generateRandomColor();
	}
	
	public Color tick(float delta)
	{
		t += delta;

		if (t > period)
		{
			last.set(current);
			current.set((float)Math.random(), (float)Math.random(), (float)Math.random(), 1);
			t -= period;
		}

		return last.cpy().lerp(current, t / period);
	}
	
	private static Color generateRandomColor()
	{
		return new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1);
	}
}

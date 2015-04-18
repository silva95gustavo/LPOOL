package lpool.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import lpool.logic.Ball;
import lpool.logic.Border;

public class Match implements Screen{
	private int width;
	private int height;

	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Texture table;

	private lpool.logic.Game game;

	public Match(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		table = new Texture("table.png");
		
		game =  new lpool.logic.Game();
	}
	
	@Override
	public void render(float delta)
	{
		game.tick(delta);

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		// Draw background
		batch.draw(table, 0, 0, width, height);
		batch.end();

		shapeRenderer.begin(ShapeType.Filled);
		
		lpool.logic.Match m = game.getMatch();

		Ball[] balls1 = m.getBalls1();
		Ball[] balls2 = m.getBalls2();
		for (int i = 0; i < m.ballsPerPlayer; i++)
		{
			drawBall(balls1[i], Color.RED);
			drawBall(balls2[i], Color.BLUE);
		}
		Ball blackBall = m.getBlackBall();
		drawBall(blackBall, Color.BLACK);
		Ball cueBall = m.getCueBall();
		drawBall(cueBall, Color.WHITE);

		float cueAngle = m.getCueAngle();
		Vector2 cueBallPos = physicsToPixel(cueBall.getPosition());
		Vector2 cue = new Vector2(1000, 0).rotate(cueAngle * 180f / (float)Math.PI).add(cueBallPos);
		
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.line(cueBallPos,  cue);
		
		shapeRenderer.end();
	}

	private Vector2 physicsToPixel(Vector2 v)
	{
		Vector2 v2 = new Vector2();
		v2.x = v.x * width / Border.width;
		v2.y = v.y * height / Border.height;
		return v2;
	}

	private void drawBall(Ball ball, Color c) {
		Vector2 ballPosPixel = physicsToPixel(ball.getPosition());
		Vector2 ballRadiusPixel = physicsToPixel(new Vector2(Ball.radius, Ball.radius));

		shapeRenderer.setColor(c);
		shapeRenderer.circle(ballPosPixel.x, ballPosPixel.y, 30);
		//shapeRenderer.rect(0, 0, 50, 50);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}
}

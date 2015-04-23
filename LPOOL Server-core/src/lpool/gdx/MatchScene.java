package lpool.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import lpool.logic.Ball;
import lpool.logic.Match;
import lpool.logic.Table;

public class MatchScene implements Screen{
	private int width;
	private int height;

	private OrthographicCamera camera;
	
	private PerspectiveCamera camera3D;
	private Model ballModel;
	private ModelBatch modelBatch = new ModelBatch();
	private Environment environment;

	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private ModelInstance table;
	private Array<ModelInstance> modelInstances;

	private lpool.logic.Game game;

	public MatchScene(int width, int height)
	{
		this.width = width;
		this.height = height;

		camera = new OrthographicCamera(width, height);
		camera.position.set(physicsToPixel(new Vector2(Table.width / 2, Table.height / 2)), 0);
		camera.update();
		
		camera3D = new PerspectiveCamera(30, width, height);
        camera3D.position.set(Table.width / 2, Table.height / 2, 4f);
        camera3D.lookAt(Table.width / 2, Table.height / 2, 0f);
        camera3D.near = 0.00001f;
        camera3D.far = 300f;
        camera3D.update();
        
        modelInstances = new Array<ModelInstance>();
        
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
        environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.9f, -0.4f, -0.6f, -1f));
        ModelBuilder modelBuilder = new ModelBuilder();
        ballModel = modelBuilder.createSphere(Ball.radius * 2, Ball.radius * 2, Ball.radius * 2, 80, 80, new Material(ColorAttribute.createDiffuse(Color.BLUE), ColorAttribute.createSpecular(Color.WHITE)), Usage.Position | Usage.Normal);

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		ObjLoader loader = new ObjLoader();
		Model tableModel = loader.loadModel(Gdx.files.internal("table2.obj"));
		table = new ModelInstance(tableModel, Table.width / 2, Table.height / 2, 0);
		table.transform.scl(0.027f);
		table.transform.rotateRad(new Vector3(1, 0, 0), (float)Math.PI/2);
		
		game =  new lpool.logic.Game();
	}

	@Override
	public void render(float delta)
	{
		game.tick(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		/*batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// Draw background
		batch.draw(table, 0, 0, physicsToPixel(Table.width), physicsToPixel(Table.height));
		batch.end();*/
		
		lpool.logic.Match m = game.getMatch();
		
		Ball[] balls1 = m.getBalls1();
		Ball[] balls2 = m.getBalls2();

		Gdx.gl.glViewport(0, 0, width, height);
		modelBatch.begin(camera3D);
		modelInstances.clear();
		modelInstances.add(table);
		for (int i = 0; i < m.ballsPerPlayer; i++)
		{
			draw3DBall(balls1[i], Color.RED, modelInstances);
			draw3DBall(balls2[i], Color.BLUE, modelInstances);
		}
		draw3DBall(m.getBlackBall(), Color.BLACK, modelInstances);
		draw3DBall(m.getCueBall(), Color.WHITE, modelInstances);
		modelBatch.render(modelInstances, environment);
        modelBatch.end();
		
		shapeRenderer.setProjectionMatrix(camera3D.combined);
		shapeRenderer.begin(ShapeType.Filled);
		float cueAngle = m.getCueAngle();

		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.rectLine(m.getCueBall().getPosition(), new Vector2(1, 0).rotateRad(cueAngle).add(m.getCueBall().getPosition()), 0.01f);
		
		shapeRenderer.end();
	}

	private float physicsToPixel(float x)
	{
		return x * (float)width / Table.width;
	}

	private Vector2 physicsToPixel(Vector2 v)
	{
		return v.cpy().scl((float)width / Table.width);

	}
	
	private void draw3DBall(Ball ball, Color c, Array<ModelInstance> modelInstances)
	{
		ModelInstance ballModelInstance = new ModelInstance(ballModel, ball.getPosition().x, ball.getPosition().y, 0);
		modelInstances.add(ballModelInstance);
	}

	private void drawBall(Ball ball, Color c) {
		Vector2 ballPosPixel = physicsToPixel(ball.getPosition());
		Vector2 ballRadiusPixel = physicsToPixel(new Vector2(Ball.radius, Ball.radius));

		shapeRenderer.setColor(c);
		shapeRenderer.circle(ballPosPixel.x, ballPosPixel.y, physicsToPixel(Ball.radius));
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

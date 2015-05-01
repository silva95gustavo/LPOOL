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
import com.badlogic.gdx.graphics.g2d.Sprite;
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
	private ModelBatch modelBatch = new ModelBatch();
	private Environment environment;

	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Texture table;
	private lpool.gdx.BallModel[] ballModels;
	private Array<ModelInstance> modelInstances;
	
	private Sprite qr_sprite;

	private lpool.logic.Game game;

	public MatchScene(int width, int height, String qr_dir)
	{
		this.width = width;
		this.height = height;

		camera = new OrthographicCamera(1.5f * Table.width, 1.5f * Table.height);
		camera.position.set(new Vector2(Table.width / 2, Table.height / 2), 0);
		camera.update();
		
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
        environment.add(new DirectionalLight().set(0.9f, 0.9f, 0.9f, -0.4f, -0.6f, -1f));
        
        ballModels = new lpool.gdx.BallModel[16];
        for (int i = 0; i < ballModels.length; i++)
        {
        	ballModels[i] = new BallModel(i);
        }
        
        modelInstances = new Array<ModelInstance>();
        
        ObjLoader loader = new ObjLoader();
        
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		Model tableModel = loader.loadModel(Gdx.files.internal("table2.obj"));
		table = new Texture("table.png");
		game = new lpool.logic.Game();
		
		qr_sprite = null;
		if(qr_dir != "") {
			Texture tex = new Texture(qr_dir);
			qr_sprite = new Sprite(tex);
		}
	}

	@Override
	public void render(float delta)
	{
		game.tick(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		lpool.logic.Match m = game.getMatch();
		
		Ball[] balls1 = m.getBalls1();
		Ball[] balls2 = m.getBalls2();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(table, 0, 0, Table.width, Table.height);
		batch.end();
		
		modelBatch.begin(camera);
		modelInstances.clear();
		for (int i = 0; i < m.ballsPerPlayer; i++)
		{
			modelInstances.add(ballModels[balls1[i].getNumber()].instanciateModel(balls1[i].getPosition(), balls1[i].getRotation()));
			modelInstances.add(ballModels[balls2[i].getNumber()].instanciateModel(balls2[i].getPosition(), balls2[i].getRotation()));
		}
		modelInstances.add(ballModels[m.getBlackBall().getNumber()].instanciateModel(m.getBlackBall().getPosition(), m.getBlackBall().getRotation()));
		modelInstances.add(ballModels[m.getCueBall().getNumber()].instanciateModel(m.getCueBall().getPosition(), m.getCueBall().getRotation()));
		modelBatch.render(modelInstances, environment);
        modelBatch.end();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		float cueAngle = m.getCueAngle();

		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.rectLine(m.getCueBall().getPosition(), new Vector2(1, 0).rotateRad(cueAngle).add(m.getCueBall().getPosition()), 0.005f);
		
		shapeRenderer.end();
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

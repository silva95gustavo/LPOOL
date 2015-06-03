package lpool.gui;

import java.util.Observable;
import java.util.Observer;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
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
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.media.sound.ModelSource;

import lpool.gui.assets.Manager;
import lpool.gui.assets.BallModels;
import lpool.gui.assets.Sounds;
import lpool.gui.assets.Textures;
import lpool.logic.BodyInfo;
import lpool.logic.Table;
import lpool.logic.ball.Ball;
import lpool.logic.match.CueBallInHand;
import lpool.logic.match.Match;
import lpool.logic.match.Play;
import lpool.logic.state.State;

public class MatchScene implements Screen, Observer{
	private OrthographicCamera camera;
	private Viewport viewport;
	
	private ModelBatch modelBatch = new ModelBatch();
	private ModelBatch shadowBatch = new ModelBatch(new DepthShaderProvider());
	private Environment environment;

	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private Texture table;
	private Texture table_border;

	private Texture cueBallPrediction;
	private Texture cueBallPredictionBlocked;
	private Sprite cue;
	private Array<ModelInstance> modelInstances;

	private DirectionalShadowLight shadowLight;

	private lpool.logic.Game game;
	
	private final float tableMargin = Table.border;
	private final float headerHeight = 4 * Table.border;
	private final float worldWidth = Table.width + 2 * tableMargin;
	private final float worldHeight = Table.height + 2 * tableMargin + headerHeight;

	public MatchScene(lpool.logic.Game game, int width, int height)
	{
		camera = new OrthographicCamera();
		camera.position.set(Table.width / 2, worldHeight / 2 - tableMargin, 3);
		//camera.lookAt(Table.width / 2, Table.height / 2, 0);
		camera.near = 0.1f; 
		camera.far = 300.0f;
		camera.update();
		viewport = new FitViewport(worldWidth, worldHeight, camera);

		//shadowLight = new DirectionalShadowLight(2048, 2048, camera.viewportWidth, camera.viewportHeight, camera.near, camera.far);
		//shadowLight.set(0.1f, 0.1f, 0.1f, -0.08f, -0.08f, -1f);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.Specular, 0.8f, 0.8f, 0.8f, 1f));
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1f));
		environment.add(new DirectionalLight().set(0.7f, 0.7f, 0.7f, -0.1f, -0.1f, -1f));
		//environment.add(shadowLight);
		//environment.shadowMap = shadowLight;

		modelInstances = new Array<ModelInstance>();

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		table = Textures.getInstance().getTable();
		table_border = Textures.getInstance().getTableBorder();

		//Material matTable = new Material(new TextureAttribute(TextureAttribute.Diffuse, table));
		//ModelBuilder mb = new ModelBuilder();
		//table3D = mb.createRect(0, 0, 0, Table.width, 0, 0, Table.width, Table.height, 0, 0, Table.height, 0, 0, 0, 1, matTable, Usage.Normal | Usage.Position | Usage.TextureCoordinates);

		cueBallPrediction = Textures.getInstance().getCueBallPrediction();
		cueBallPrediction.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		cueBallPredictionBlocked = Textures.getInstance().getCueBallPredictionBlocked();
		cueBallPredictionBlocked.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		cue = new Sprite(Textures.getInstance().getCue());
		cue.setSize(1.5f, 0.04f);
		this.game = game;
		game.startMatch();
		game.getMatch().addColisionObserver(this);
	}

	@Override
	public void render(float delta)
	{
		game.tick(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		lpool.logic.match.Match m = game.getMatch();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(Textures.getInstance().getBackground(), -tableMargin, -tableMargin, worldWidth, worldHeight);
		batch.draw(table, 0, 0, Table.width, Table.height);
		batch.end();

		modelInstances.clear();
		Ball[] balls = m.getBalls();
		for (int i = 0; i < balls.length; i++)
		{
			if (balls[i].isVisible())
			{
				modelInstances.add(BallModels.getInstance().getBall(balls[i].getNumber()).instanciateModel(balls[i].getPosition(), balls[i].getRotation()));
				
				batch.begin();
				batch.draw(Textures.getInstance().getBallShadow(), balls[i].getPosition().x - 6 * Ball.radius / 5, balls[i].getPosition().y - 3 * Ball.radius / 2, 2 * Ball.radius, 2 * Ball.radius);
				batch.end();
			}
		}
		
		batch.begin();
		batch.draw(table_border, 0, 0, Table.width, Table.height);
		batch.end();
		//modelInstances.add(new ModelInstance(table3D));

		/*shadowLight.begin(camera.position, camera.direction);
		shadowBatch.begin(camera);
		shadowBatch.render(modelInstances);
		shadowBatch.end();
		shadowLight.end();*/

		modelBatch.begin(camera);
		modelBatch.render(modelInstances, environment);
		modelBatch.end();

		State<Match> currentState = m.getStateMachine().getCurrentState();
		if (currentState.getClass() == Play.class)
		{
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.WHITE);

			Vector2[] prediction = m.predictShot();

			if (prediction[4] != null)
				shapeRenderer.rectLine(m.getCueBall().getPosition(), prediction[4], 0.005f * Match.physicsScaleFactor); // Aiming line
			if (prediction[0] != null)
			{
				batch.begin();
				batch.draw(cueBallPrediction, prediction[0].x - Ball.radius, prediction[0].y - Ball.radius, Ball.radius * 2, Ball.radius * 2);
				batch.end();
				if (prediction[2] != null)
				{
					shapeRenderer.rectLine(prediction[0], prediction[0].cpy().add(prediction[2].cpy().scl(0.075f * Match.physicsScaleFactor)), 0.0025f * Match.physicsScaleFactor); // Cue ball
				}
			}
			if (prediction[1] != null && prediction[3] != null)
				shapeRenderer.rectLine(prediction[1], prediction[1].cpy().add(prediction[3].cpy().scl(0.15f * Match.physicsScaleFactor)), 0.0025f * Match.physicsScaleFactor); // 2nd ball

			shapeRenderer.end();

			drawCue(m.getCueBall().getPosition(), 0, m.getCueAngle());
		}
		else if (currentState.getClass() == CueBallInHand.class)
		{
			batch.begin();
			batch.draw(((CueBallInHand)currentState).isValidPosition() ? cueBallPrediction : cueBallPredictionBlocked, m.getCueBall().getPosition().x, m.getCueBall().getPosition().y, Ball.radius * 2, Ball.radius * 2);
			batch.end();
		}
		Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
		debugRenderer.render(m.getWorld(), batch.getProjectionMatrix());
	}

	private void drawCue(Vector2 cueBallPos, float force, float angle)
	{
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		Vector2 cuePos = cueBallPos.cpy().add(new Vector2(2 * Ball.radius + force, 0).rotateRad(angle + (float)Math.PI)).add(new Vector2(0, 0.02f * Match.physicsScaleFactor));
		Vector2 cueSize = new Vector2(1.5f * Match.physicsScaleFactor, 0.04f * Match.physicsScaleFactor);
		cue.setOrigin(1.5f * Match.physicsScaleFactor, Match.physicsScaleFactor * 0.04f / 2);
		cue.setRotation((float)Math.toDegrees(angle));
		Vector2 cueStart = cuePos.cpy().sub(cueSize);
		cue.setBounds(cueStart.x, cueStart.y, cueSize.x, cueSize.y);
		cue.draw(batch);
		batch.end();
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
		viewport.update(width, height);
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Observable o, Object obj) {
		Contact contact = (Contact)obj;

		BodyInfo userDataA = ((BodyInfo)contact.getFixtureA().getUserData());
		BodyInfo userDataB = ((BodyInfo)contact.getFixtureB().getUserData());

		if (userDataA == null || userDataB == null)
			return;

		switch (userDataA.getType())
		{
		case BALL:
			if (userDataB.getType() == BodyInfo.Type.BALL)
				ballBallCollisionHandler(userDataA.getID(), userDataB.getID(), contact.getWorldManifold().getPoints()[0]);
			else if (userDataB.getType() == BodyInfo.Type.TABLE)
				ballTableCollisionHandler(userDataA.getID(), contact.getWorldManifold().getPoints()[0]);
			break;
		case TABLE:
			if (userDataB.getType() == BodyInfo.Type.BALL)
				ballTableCollisionHandler(userDataB.getID(), contact.getWorldManifold().getPoints()[0]);
			break;
		case HOLE:
			break;
		case BALL_SENSOR:
			break;
		default:
			break;
		}
	}

	private void ballBallCollisionHandler(int ballNumber1, int ballNumber2, Vector2 contactPoint)
	{
		Ball[] balls = game.getMatch().getBalls();
		Vector2 impactVelocity = balls[ballNumber1].getVelocity().sub(balls[ballNumber2].getVelocity());
		Sounds.getInstance().getBallBallCollision().play(impactVelocity.len() / 40, 1, 2 * (contactPoint.x - Table.width / 2) / Table.width);
	}
	
	private void ballTableCollisionHandler(int ballNumber, Vector2 contactPoint)
	{
		// TODO
	}
}

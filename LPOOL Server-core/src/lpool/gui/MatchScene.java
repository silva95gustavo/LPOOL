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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.media.sound.ModelSource;

import lpool.gui.assets.Fonts;
import lpool.gui.assets.Manager;
import lpool.gui.assets.BallModels;
import lpool.gui.assets.Sounds;
import lpool.gui.assets.Textures;
import lpool.logic.BodyInfo;
import lpool.logic.Table;
import lpool.logic.ball.Ball;
import lpool.logic.match.BallsMoving;
import lpool.logic.match.CueBallInHand;
import lpool.logic.match.End;
import lpool.logic.match.FreezeTime;
import lpool.logic.match.Match;
import lpool.logic.match.Play;
import lpool.logic.state.State;
import lpool.logic.state.TransitionState;

public class MatchScene implements Screen, Observer{
	private OrthographicCamera camera;
	private Viewport viewport;

	private ModelBatch modelBatch = new ModelBatch();
	private ModelBatch shadowBatch = new ModelBatch(new DepthShaderProvider());
	private Environment environment;

	private ShapeRenderer shapeRenderer;
	private ShaderBatch batch;
	private Texture table;
	private Texture table_border;

	private Texture cueBallPrediction;
	private Texture cueBallPredictionBlocked;
	private Sprite cue;
	private Array<ModelInstance> modelInstances;
	private DialogMessage dialogMessage;

	private DirectionalLight directionalLight;

	private lpool.logic.Game game;
	private com.badlogic.gdx.Game GdxGame;

	public static final float tableMargin = Table.border;
	public static final float headerHeight = 3 * Table.border;
	public static final float worldWidth = Table.width + 2 * tableMargin;
	public static final float worldHeight = Table.height + 2 * tableMargin + headerHeight;
	public static final float worldXCenter = worldWidth / 2 - tableMargin;

	public MatchScene(lpool.logic.Game game, com.badlogic.gdx.Game GdxGame, int width, int height)
	{
		Sounds.getInstance().getRacking().play();

		camera = new OrthographicCamera();
		camera.position.set(Table.width / 2, worldHeight / 2 - tableMargin, 3);
		camera.near = 0.1f; 
		camera.far = 300.0f;
		camera.update();
		viewport = new FitViewport(worldWidth, worldHeight, camera);
		dialogMessage = null;

		directionalLight = new DirectionalLight();
		environment = new Environment();
		environment.add(directionalLight);

		modelInstances = new Array<ModelInstance>();
		batch = new ShaderBatch(100);
		shapeRenderer = new ShapeRenderer();

		table = Textures.getInstance().getTable();
		table_border = Textures.getInstance().getTableBorder();

		cueBallPrediction = Textures.getInstance().getCueBallPrediction();
		cueBallPrediction.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		cueBallPredictionBlocked = Textures.getInstance().getCueBallPredictionBlocked();
		cueBallPredictionBlocked.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		for (int i = 0; i < 2 * Match.ballsPerPlayer + 2; i++)
			Textures.getInstance().getBallIcon(i).setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		Textures.getInstance().getLogo().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		cue = new Sprite(Textures.getInstance().getCue());
		cue.setSize(1.5f, 0.04f);
		this.game = game;
		this.GdxGame = GdxGame;
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
		State<Match> currentState = m.getStateMachine().getCurrentState();
		if (currentState instanceof FreezeTime)
		{
			if (dialogMessage == null)
				dialogMessage = new DialogMessage(batch, "LPOOL", "A new match is about to begin... " + m.getPlayerName(m.getCurrentPlayer()) + " starts.", FreezeTime.freezeTime - 2 * DialogMessage.animTime, -tableMargin, -tableMargin, worldWidth, worldHeight);
			updateEnvironment();
			updateBatch();
			if (!dialogMessage.update(delta))
				dialogMessage = null;
		}
		else if (currentState instanceof TransitionState)
		{
			State nextState = ((TransitionState)currentState).getNextState();
			if (dialogMessage == null)
				if (nextState instanceof Play)
					dialogMessage = new DialogMessage(batch, "Shot " + (m.getPlayNum() + 1), "It's " + m.getPlayerName(m.getCurrentPlayer()) + "'s turn.", 4, -tableMargin, -tableMargin, worldWidth, worldHeight);
				else if (nextState instanceof End)
					dialogMessage = new DialogMessage(batch, m.getPlayerName(((End)nextState).getWinner()) + " won!", reasonToMessage(((End)nextState).getReason()), 4, -tableMargin, -tableMargin, worldWidth, worldHeight);
				else if (nextState instanceof CueBallInHand)
					dialogMessage = new DialogMessage(batch, "Foul", m.getPlayerName(m.getCurrentPlayer()) + " has the ball in his hand.", 4, -tableMargin, -tableMargin, worldWidth, worldHeight);
			updateEnvironment();
			updateBatch();
			if (dialogMessage != null && !dialogMessage.update(delta))
				dialogMessage = null;
		}
		else
		{
			updateEnvironment(0);
			updateBatch(0, 1);
		}

		batch.begin();
		batch.draw(Textures.getInstance().getBackground(), -tableMargin, -tableMargin, worldWidth, worldHeight);
		batch.draw(table, 0, 0, Table.width, Table.height);
		batch.end();
		
		drawHeader(m);

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

		modelBatch.begin(camera);
		modelBatch.render(modelInstances, environment);
		modelBatch.end();

		if (currentState instanceof Play)
		{
			dialogMessage = null;
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

			drawCue(m.getCueBall().getPosition(), Ball.radius, m.getCueAngle());
		}
		else if (currentState instanceof CueBallInHand)
		{
			CueBallInHand cbih = (CueBallInHand)currentState;
			batch.begin();
			batch.draw(((CueBallInHand)currentState).isValidPosition() ? cueBallPrediction : cueBallPredictionBlocked, cbih.getAttemptedPosition().x - Ball.radius, cbih.getAttemptedPosition().y - Ball.radius, Ball.radius * 2, Ball.radius * 2);
			batch.end();
		}
		else if (currentState instanceof TransitionState)
		{
			TransitionState<Match> transitionState = (TransitionState)currentState;
			if (transitionState.getNextState() instanceof BallsMoving)
			{
				float cueAnimTime = 0.4f;
				float force = (transitionState.data instanceof Float) ? (Float)transitionState.data : 1;
				if (transitionState.getTime() >= cueAnimTime)
				{
					Sounds.getInstance().getCueHittingCueBall().play(force);
					transitionState.next();
				}
				else // Animate cue
				{
					drawCue(balls[0].getPosition(), animateCue(force, cueAnimTime, transitionState.getTime()), m.getCueAngle());
				}
			}
			else if (dialogMessage == null)
				transitionState.next();
			
		}
		else if (currentState instanceof End)
		{
			if (dialogMessage == null)
			{
				game.endMatch();
				GdxGame.setScreen(new LobbyScene(game, GdxGame, new FadingColor(GameProject.blackgroundColorPeriod)));
			}
		}
		if (dialogMessage != null)
			dialogMessage.renderDialog();
		//Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
		//debugRenderer.render(m.getWorld(), batch.getProjectionMatrix());
	}
	
	private float animateCue(float force, float animTime, float t)
	{
		System.out.println("animTime: " + animTime + " t: " + t);
		float startingDist = Ball.radius;
		float backDist = 20 * force * Ball.radius;
		float backDuration = 0.6f * animTime;
		float forwardDuration = animTime - backDuration;
		if (t <= backDuration)
			return Interpolation.linear.apply(startingDist, backDist, t / backDuration);
		else
			return Interpolation.linear.apply(backDist, 0, (t - backDuration) / forwardDuration);
						
		//return Interpolation.sineOut.apply(Ball.radius, 0,t);
	}

	private String reasonToMessage(End.Reason reason)
	{
		switch (reason)
		{
		case BLACK_BALL_SCORED_ACCIDENTALLY: return "The black ball was accidentally scored by the losing player.";
		case BLACK_BALL_SCORED_AS_LAST: return "The black ball was successfully scored.";
		case DISCONNECT: return "The losing player disconnected from the match.";
		case TIMEOUT: return "The losing player timed out.";
		default: return null;
		}
	}

	private void drawCue(Vector2 cueBallPos, float distance, float angle)
	{
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		Vector2 cuePos = cueBallPos.cpy().add(new Vector2(Ball.radius + distance, 0).rotateRad(angle + (float)Math.PI)).add(new Vector2(0, 0.02f * Match.physicsScaleFactor));
		Vector2 cueSize = new Vector2(1.5f * Match.physicsScaleFactor, 0.04f * Match.physicsScaleFactor);
		cue.setOrigin(1.5f * Match.physicsScaleFactor, Match.physicsScaleFactor * 0.04f / 2);
		cue.setRotation((float)Math.toDegrees(angle));
		Vector2 cueStart = cuePos.cpy().sub(cueSize);
		cue.setBounds(cueStart.x, cueStart.y, cueSize.x, cueSize.y);
		cue.draw(batch);
		batch.end();
	}
	
	private void drawHeader(Match match)
	{
		batch.begin();
		
		// Name background
		float nameDisplacement = worldWidth * 0.2f;
		float nameWidth = Table.width * 0.2f;
		float nameHeight = nameWidth * Textures.getInstance().getNameBackground().getHeight() / (float)Textures.getInstance().getNameBackground().getWidth();
		batch.draw(Textures.getInstance().getNameBackground(), worldXCenter -nameWidth - nameDisplacement, worldHeight * 0.85f - tableMargin, nameWidth, nameHeight);
		batch.draw(Textures.getInstance().getNameBackground(), worldXCenter + nameDisplacement, worldHeight * 0.85f - tableMargin, nameWidth, nameHeight);
		
		// Logo
		float logoWidth = 0.3f * Table.width;
		float logoHeight = logoWidth * Textures.getInstance().getLogo().getHeight() / (float)Textures.getInstance().getLogo().getWidth();
		batch.draw(Textures.getInstance().getLogo(), (Table.width - logoWidth) / 2, worldHeight * 0.75f, logoWidth, logoHeight);
		
		// Name
		BitmapFont nameFont = Fonts.getInstance().getArial100();
		nameFont.setScale(0.005f);
		nameFont.setColor(Color.BLACK);
		nameFont.drawMultiLine(batch, match.getPlayerName(0), worldXCenter - nameDisplacement - worldHeight * 0.02f, worldHeight * 0.83f, 0, BitmapFont.HAlignment.RIGHT);
		nameFont.drawMultiLine(batch, match.getPlayerName(1), worldXCenter + nameDisplacement + worldHeight * 0.02f, worldHeight * 0.83f, 0, BitmapFont.HAlignment.LEFT);
		nameFont.setScale(1);
		
		batch.end();
		drawBallIcons(match, nameDisplacement);
	}

	private void drawBallIcons(Match match, float displacement)
	{
		if (!match.playerBallsDefined())
			return;
		displacement += worldHeight * 0.01f; // Shadow margin
		float y = worldHeight * 0.75f;
		float ballSize = Table.width * 0.025f;
		float ballsWidth = Match.ballsPerPlayer * ballSize;
		
		Ball[] p1Balls = match.getPlayerBallsType(0) == Ball.Type.SOLID ? match.getBallsSolid() : match.getBallsStripe();
		Ball[] p2Balls = match.getPlayerBallsType(1) == Ball.Type.STRIPE ? match.getBallsStripe() : match.getBallsSolid();
		
		batch.begin();
		// Balls on table
		for (int i = 0; i < p1Balls.length; i++)
		{
			if (p1Balls[i].isOnTable())
				batch.draw(Textures.getInstance().getBallIcon(p1Balls[i].getNumber()), worldXCenter - displacement - ballsWidth + ballSize * i, y, ballSize, ballSize);
			if (p2Balls[i].isOnTable())
				batch.draw(Textures.getInstance().getBallIcon(p2Balls[i].getNumber()), worldXCenter + displacement + ballsWidth - ballSize * (i + 1), y, ballSize, ballSize);
		}
		batch.end();
		batch.brightness = 0.6f * batch.brightness - (1 - 0.6f);
		batch.contrast = batch.brightness + 1;
		batch.begin();
		// Scored balls
		for (int i = 0; i < p2Balls.length; i++)
		{
			if (!p1Balls[i].isOnTable())
				batch.draw(Textures.getInstance().getBallIcon(p1Balls[i].getNumber()), worldXCenter - displacement - ballsWidth + ballSize * i, y, ballSize, ballSize);
			if (!p2Balls[i].isOnTable())
				batch.draw(Textures.getInstance().getBallIcon(p2Balls[i].getNumber()), worldXCenter + displacement + ballsWidth - ballSize * (i + 1), y, ballSize, ballSize);
		}
		batch.end();
		updateBatch();
	}
	
	@Override
	public void dispose() {
		modelBatch.dispose();
		shadowBatch.dispose();
		shapeRenderer.dispose();
		batch.dispose();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resize(int width, int height) {		
		viewport.update(width, height);
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
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
			else if (userDataB.getType() == BodyInfo.Type.HOLE)
				ballHoleCollisionHandler();
			break;
		case TABLE:
			if (userDataB.getType() == BodyInfo.Type.BALL)
				ballTableCollisionHandler(userDataB.getID(), contact.getWorldManifold().getPoints()[0]);
			break;
		case HOLE:
			if (userDataB.getType() == BodyInfo.Type.BALL)
				ballHoleCollisionHandler();
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
		Ball ball = game.getMatch().getBalls()[ballNumber];
		Sounds.getInstance().getBallHittingBorder().play(ball.getVelocity().len() / 40, 1, 2 * (contactPoint.x - Table.width / 2) / Table.width);
	}

	private void ballHoleCollisionHandler()
	{
		Sounds.getInstance().getBallInHole().play();
	}

	private void updateEnvironment()
	{
		if (dialogMessage == null)
			updateEnvironment(0);
		else
			updateEnvironment(dialogMessage.getBackgroundBrightness());
	}

	private void updateEnvironment(float brightness)
	{
		float factor = brightness + 1;
		directionalLight.set(factor * 0.7f, factor * 0.7f, factor * 0.7f, -0.1f, -0.1f, -1f);
		environment.clear();
		environment.set(new ColorAttribute(ColorAttribute.Specular, factor * 0.8f, factor * 0.8f, factor * 0.8f, 1f));
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, factor * 0.2f, factor * 0.2f, factor * 0.2f, 1f));
		environment.add(directionalLight);
	}

	private void updateBatch()
	{
		if (dialogMessage == null)
			updateBatch(0, 1);
		else
			updateBatch(dialogMessage.getBackgroundBrightness(), dialogMessage.getBackgroundContrast());
	}

	private void updateBatch(float brightness, float contrast)
	{
		batch.brightness = brightness;
		batch.contrast = contrast;
		batch.begin();
		batch.end();
	}
}

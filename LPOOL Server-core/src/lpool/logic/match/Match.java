package lpool.logic.match;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

import lpool.gui.assets.Sounds;
import lpool.logger.Logger;
import lpool.logic.BodyInfo;
import lpool.logic.Game;
import lpool.logic.ObservableCollision;
import lpool.logic.Table;
import lpool.logic.BodyInfo.Type;
import lpool.logic.ball.Ball;
import lpool.logic.state.Context;
import lpool.logic.state.State;
import lpool.logic.state.TransitionState;
import lpool.network.Message;
import lpool.network.Network;
import lpool.network.ObservableConnection;
import lpool.network.ObservableMessage;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;

public class Match implements Observer {
	public static int ballsPerPlayer = 7;
	public static float physicsScaleFactor = 10;
	public static int numPlayers = 2;
	public static float stateSendingFrequency = 2;

	private lpool.logic.state.Context<Match> stateMachine;
	private Network network;

	private Vector2 gravity;
	private World world;
	private Queue<Body> ballsToBeDeleted;

	private Ball[] ballsSolid;
	private Ball[] ballsStripe;
	private Ball blackBall;
	private Ball cueBall;
	private Ball[] balls;
	private String[] playerNames;

	private float cueAngle = (float)Math.PI;
	private Ball.Type ballsPlayer[];
	private int playNum;
	private int currentPlayer;
	private PlayValidator playValidator;
	private float lastStateSendTime;
	private Logger logger;

	public enum PlayingForBall
	{
		NONE,
		SOLID,
		STRIPE,
		BLACK
	}

	private ObservableCollision observableCollision;

	public Match(Network network, String[] playerNames, Logger logger) {
		stateMachine = new Context<Match>(this, new FreezeTime());
		this.network = network;
		network.addConnObserver(this);
		this.ballsPlayer = new Ball.Type[numPlayers];
		this.playNum = 0;
		Random r = new Random();
		this.currentPlayer = r.nextInt(numPlayers);

		gravity = new Vector2(0, 0);
		world = new World(gravity, false);
		World.setVelocityThreshold(0.00001f);
		world.setContactListener(observableCollision = new ObservableCollision());
		addColisionObserver(this);
		ballsToBeDeleted = new LinkedList<Body>();

		ballsSolid = new Ball[ballsPerPlayer];
		ballsStripe = new Ball[ballsPerPlayer];
		balls = new Ball[ballsPerPlayer * numPlayers + 2];
		this.playerNames = playerNames;

		createBalls();

		Table table = new Table(world);
		this.lastStateSendTime = 0;
		this.logger = logger;
		logStart();
	}

	void logStart()
	{
		String names = "";
		for(int i = 0; i < playerNames.length; i++) {
			names += playerNames[i];
			if(i < playerNames.length - 1) {
				names += ", ";
			}
		}
		logger.log("Game started with players " + names);
	}

	void logEnd(int winner)
	{
		if (winner < 0 || winner >= numPlayers) return;
		logger.log("Game ended, won by " + playerNames[winner]);
	}

	private Ball createBall(World world, int posID, int number)
	{
		switch (posID)
		{
		case 0: return createBall(world, 25, 0, number);
		case 1: return createBall(world, 0, 0, number);
		case 2: return createBall(world, -1, 1, number);
		case 3: return createBall(world, -1, -1, number);
		case 4: return createBall(world, -2, 2, number);
		case 5: return createBall(world, -2, 0, number);
		case 6: return createBall(world, -2, -2, number);
		case 7: return createBall(world, -3, 3, number);
		case 8: return createBall(world, -3, 1, number);
		case 9: return createBall(world, -3, -1, number);
		case 10: return createBall(world, -3, -3, number);
		case 11: return createBall(world, -4, 4, number);
		case 12: return createBall(world, -4, 2, number);
		case 13: return createBall(world, -4, 0, number);
		case 14: return createBall(world, -4, -2, number);
		case 15: return createBall(world, -4, -4, number);
		default: return null;
		}
	}

	private Ball createBall(World world, int x, int y, int number)
	{
		Vector2 pos = new Vector2((Table.width - 2 * Table.border) / 4 + (float)Math.sqrt(3) * Ball.radius * x, Table.height / 2 + y * Ball.radius);
		pos.add(new Vector2((float)Math.random() * 0.01f * Ball.radius, (float)Math.random() * 0.01f * Ball.radius)); // noise
		Ball ball = new Ball(world, pos, number, ballsToBeDeleted);
		if (number == 0)
			cueBall = ball;
		else if (number < 8)
			ballsSolid[number - 1] = ball;
		else if (number == 8)
			blackBall = ball;
		else if (number < 16)
			ballsStripe[number - 9] = ball;
		else return null;
		return balls[number] = ball;
	}

	private void createBalls()
	{		
		createBall(world, 25, 0, 0);

		int[] rack = Racker.rack();

		for (int i = 1; i < rack.length; i++)
		{
			createBall(world, rack[i], i);
		}
	}

	public void tick(float dt)
	{
		this.lastStateSendTime += dt;
		if (this.lastStateSendTime >= stateSendingFrequency)
		{
			sendStateToClients();
			this.lastStateSendTime = 0;
		}
		stateMachine.update(dt);
	}

	public void worldStep(float dt)
	{
		world.step(dt, 60, 20);
	}

	public void tickBalls(float dt)
	{
		for (int i = 0; i < balls.length; i++)
		{
			balls[i].tick(dt);
		}
	}

	public void deleteRemovedBalls()
	{
		while (!ballsToBeDeleted.isEmpty())
		{
			world.destroyBody(ballsToBeDeleted.poll());
		}
	}

	public Ball getBlackBall() {
		return blackBall;
	}

	public Ball[] getBallsSolid()
	{
		return ballsSolid;
	}

	public Ball[] getBallsStripe()
	{
		return ballsStripe;
	}

	public Ball getCueBall() {
		return cueBall;
	}

	public Ball[] getBalls()
	{
		return balls;
	}

	public void setCueAngle(float angle)
	{
		cueAngle = angle;
	}

	public void makeShot(float force, float xSpin, float ySpin)
	{
		cueBall.makeShot(cueAngle, force, xSpin, ySpin);
	}

	public float getCueAngle()
	{
		return cueAngle;
	}

	public Vector2[] predictShot()
	{
		/*
		 * result:
		 * 0 - cue ball position
		 * 1 - 2nd ball position
		 * 2 - cue ball prediction
		 * 3 - 2nd ball prediction
		 * 4 - aiming point
		 * all results must be checked for not being null
		 */
		final boolean[] b = new boolean[1];
		b[0] = false;
		final Vector2[] result = new Vector2[5];
		RayCastCallback callBack = new RayCastCallback() {
			float smallestDistance;
			boolean foundAimingPoint = false;
			float closestAP;

			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction)
			{
				if (fixture.getUserData() == null)
					return -1;
				if ((((BodyInfo)fixture.getUserData()).getType() == BodyInfo.Type.BALL_SENSOR && ((BodyInfo)fixture.getUserData()).getID() != 0) || 
						((BodyInfo)fixture.getUserData()).getType() == BodyInfo.Type.TABLE)
				{
					float distance = point.dst2(cueBall.getPosition());
					if (foundAimingPoint)
					{
						if (distance < closestAP)
						{
							result[4] = point.cpy();
							closestAP = distance;
						}
					}
					else
					{
						foundAimingPoint = true;
						result[4] = point.cpy();
						closestAP = distance;
					}
				}
				if (((BodyInfo)fixture.getUserData()).getType() != BodyInfo.Type.BALL_SENSOR)
					return -1;
				if (((BodyInfo)fixture.getUserData()).getID() == 0)
					return -1;
				if (!b[0] || cueBall.getPosition().dst2(point) < smallestDistance)
				{
					b[0] = true;
					smallestDistance = cueBall.getPosition().dst2(point);
					result[0] = point.cpy();
					result[1] = fixture.getBody().getPosition().cpy();
					result[2] = normal.cpy().rotateRad((float)Math.PI / 2).scl((float)Math.sin(cueBall.getPosition().cpy().sub(result[0]).angleRad(normal)));
					result[3] = normal.cpy().rotateRad((float)Math.PI).scl((float)Math.cos(cueBall.getPosition().cpy().sub(result[0]).angleRad(normal)));
				}
				return 1;
			}
		};
		float diagonal = (float)((new Vector2(Table.width, Table.height)).len());
		world.rayCast(callBack, cueBall.getPosition(), cueBall.getPosition().cpy().add(new Vector2(1, 0).scl(diagonal).rotateRad(cueAngle)));
		if (!b[0])
		{
			result[0] = null;
			result[1] = null;
			result[2] = null;
			result[3] = null;
		}
		return result;
	}

	public void addColisionObserver(Observer o)
	{
		observableCollision.addObserver(o);
	}

	@Override
	public void update(Observable o, Object obj) {
		if (o instanceof ObservableCollision && obj instanceof Contact)
		{
			Contact contact = (Contact)obj;

			BodyInfo userDataA = ((BodyInfo)contact.getFixtureA().getUserData());
			BodyInfo userDataB = ((BodyInfo)contact.getFixtureB().getUserData());

			if (userDataA == null || userDataB == null)
				return;

			switch (userDataA.getType())
			{
			case BALL:
				switch (userDataB.getType())
				{
				case BALL: ballBallCollisionHandler(Math.min(userDataA.getID(), userDataB.getID()), Math.max(userDataA.getID(), userDataB.getID())); break;
				case HOLE: ballInHoleHandler(userDataA.getID(), userDataB.getID()); break;
				case TABLE: ballTableCollisionHandler(userDataA.getID());
				default: break;
				}
				break;
			case TABLE:
				switch(userDataB.getType())
				{
				case BALL: ballTableCollisionHandler(userDataB.getID()); break;
				default: break;
				}
				break;
			case HOLE:
				switch (userDataB.getType())
				{
				case BALL: ballInHoleHandler(userDataB.getID(), userDataA.getID()); break;
				default: break;
				}
				break;
			case BALL_SENSOR: break;
			default: break;
			}
		}
		else if (o instanceof ObservableMessage && obj instanceof Message)
		{
			Message msg = (Message)obj;
			Scanner sc = new Scanner(msg.body);
			Game.ProtocolCmd cmd = Message.readCmd(sc);
			if (cmd == Game.ProtocolCmd.JOIN)
				sendStateToClient(msg.clientID);
			sc.close();
		}
		else if (o instanceof ObservableConnection && obj instanceof Integer)
		{
			if (!(stateMachine.getCurrentState() instanceof End))
				stateMachine.changeState(new TransitionState<Match>(stateMachine, stateMachine.getCurrentState(), new End(End.Reason.DISCONNECT, (Integer)obj == 0 ? 1 : 0)));
		}
	}

	private void ballTableCollisionHandler(int ballNumber)
	{
		playValidator.actionBorderHit();
	}

	private void ballBallCollisionHandler(int ball1, int ball2)
	{
		if (balls[ball1].getType() != Ball.Type.CUE) return; // Ignore if not cue ball
		playValidator.actionBallHit(balls[ball2].getType());
	}

	private void ballInHoleHandler(int ballNumber, int holeNumber)
	{
		balls[ballNumber].enterHole(holeNumber);

		if (!playerBallsDefined() && !isOpeningShot())
		{
			int otherPlayer = (currentPlayer == 0 ? 1 : 0);
			if (balls[ballNumber].isSolid())
			{
				ballsPlayer[currentPlayer] = Ball.Type.SOLID;
				ballsPlayer[otherPlayer] = Ball.Type.STRIPE;
			}
			else if (balls[ballNumber].isStripped())
			{
				ballsPlayer[currentPlayer] = Ball.Type.STRIPE;
				ballsPlayer[otherPlayer] = Ball.Type.SOLID;
			}
			// Do nothing if cue or black ball
		}

		playValidator.actionBallScore(balls[ballNumber].getType());
	}

	public lpool.logic.state.Context<Match> getStateMachine() {
		return stateMachine;
	}

	public Network getNetwork()
	{
		return network;
	}

	public World getWorld()
	{
		return world;
	}

	public boolean isBallInHand() {
		return !playValidator.isValid();
	}

	public boolean currentPlayerPlaysAgain()
	{
		return playValidator.playsAgain();
	}

	public int getPlayNum()
	{
		return playNum;
	}

	public int incrementPlayNum()
	{
		return ++playNum;
	}

	public Ball.Type getPlayerBallsType(int playerID)
	{
		if (playerID >= 0 && playerID < 2)
			return ballsPlayer[playerID];
		return null;
	}

	/**
	 * 
	 * @return true if it has been already defined who plays for the solid balls and who plays for the stripe balls, false otherwise
	 */
	public boolean playerBallsDefined()
	{
		return ballsPlayer[0] != null;
	}

	public int getCurrentPlayer()
	{
		return currentPlayer;
	}

	public boolean isOpeningShot() {
		return playNum == 0;
	}

	public PlayValidator getPlayValidator() {
		return playValidator;
	}

	public void setPlayValidator(PlayValidator playValidator) {
		this.playValidator = playValidator;
	}

	public void sendStateToClient(int clientID)
	{
		State<Match> currentState = stateMachine.getCurrentState();
		if (currentState instanceof TransitionState || currentState instanceof FreezeTime || currentState instanceof BallsMoving)
		{
			network.send(new Message(clientID, Game.ProtocolCmd.WAIT.ordinal()));
		}
		else if (currentState instanceof Play)
		{
			if (currentPlayer == clientID)
				network.send(new Message(clientID, Game.ProtocolCmd.PLAY.ordinal(), getPlayingForBall(clientID).ordinal()));
			else network.send(new Message(clientID, Game.ProtocolCmd.WAIT.ordinal()));
		}
		else if (currentState instanceof CueBallInHand)
		{
			if (currentPlayer == clientID)
				network.send(new Message(clientID, Game.ProtocolCmd.BIH.ordinal()));
			else network.send(new Message(clientID, Game.ProtocolCmd.WAIT.ordinal()));
		}
		else if (currentState instanceof End)
		{
			End endState = (End)currentState;
			if (currentPlayer == clientID)
				network.send(new Message(clientID, Game.ProtocolCmd.END.ordinal(), endState.getWinner() == clientID ? 1 : 0, endState.getReason().ordinal()));
			else network.send(new Message(clientID, Game.ProtocolCmd.END.ordinal(), endState.getWinner() == clientID ? 1 : 0, endState.getReason().ordinal()));
		}
	}

	public void sendStateToClients()
	{
		for (int i = 0; i < Game.numPlayers; i++)
		{
			sendStateToClient(i);
		}
	}

	public void changeCurrentPlayer()
	{
		currentPlayer += 1;
		currentPlayer %= Game.numPlayers;
	}

	public void deleteCollisionObserver(Observer obs)
	{
		observableCollision.deleteObserver(obs);
	}

	public void respawnCueBall(Vector2 pos)
	{
		balls[0] = cueBall = new Ball(world, pos, 0, ballsToBeDeleted);
	}

	public PlayingForBall getPlayingForBall(int playerID)
	{
		if (playerID < 0 || playerID >= Match.numPlayers) return null;
		if (!playerBallsDefined())
			return PlayingForBall.NONE;
		if (isPlayingForBlack(playerID))
			return PlayingForBall.BLACK;
		if (ballsPlayer[playerID] == Ball.Type.SOLID)
			return PlayingForBall.SOLID;
		if (ballsPlayer[playerID] == Ball.Type.STRIPE)
			return PlayingForBall.STRIPE;
		return null;
	}

	boolean isPlayingForBlack(int playerID)
	{
		if (playerID < 0 || playerID >= Match.numPlayers) return false;
		if (!playerBallsDefined()) return false;
		for (int i = 0; i < balls.length; i++)
		{
			if (balls[i].isOnTable() && balls[i].getType().equals(ballsPlayer[playerID]))
			{
				return false;
			}
		}
		return true;
	}

	public String getPlayerName(int playerID)
	{
		if (playerID >= 0 && playerID < Game.numPlayers)
			return playerNames[playerID];
		else return null;
	}
}

package lpool.logic;

import java.util.Observable;
import java.util.Observer;
import lpool.logic.match.Match;
import lpool.network.Network;

public class Game implements Observer {
	public static final int numPlayers = 2;
	
	private Network network;
	
	private Match match;
	private float angleVar;
	private float lastAngle;
	private long lastAngleTime;

	public enum ProtocolCmd {
		ANGLE, // angle
		FIRE, // force
		PING,
		PONG,
		JOIN,
		QUIT
	};

	public Game() {
		network = new Network(2);
		
		angleVar = 0;
		lastAngle = (float)Math.PI;
		lastAngleTime = System.currentTimeMillis();

		network.startConnecting();
		network.addConnObserver(this);
	}

	public void tick(float dt)
	{
		if (match != null)
			match.tick(dt);
		
		network.tick();
	}

	public Match getMatch()
	{
		return match;
	}

	protected void conEvent(int clientID) {
		if (network.isClientConnected(clientID))
			System.out.println("Client #" + clientID + " connected!");
		else
			System.out.println("Client #" + clientID + " disconnected!");
	}

	private void interpolateAngle(float dt)
	{
		if (match != null)
			match.setCueAngle(match.getCueAngle() + angleVar * dt);
	}
	
	public Network getNetwork()
	{
		return network;
	}

	@Override
	public void update(Observable o, Object obj) {
		conEvent((Integer)obj);
	}
	
	public void startMatch()
	{
		match = new Match(network);
	}
}

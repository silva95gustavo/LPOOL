package lpool.logic;

import java.util.Observable;
import java.util.Observer;
import lpool.logic.match.Match;
import lpool.network.Network;

public class Game implements Observer {
	public static final int numPlayers = 2;
	
	private Network network;
	
	private Match match;

	public enum ProtocolCmd {
		ANGLE, // angle
		FIRE, // force[0, 1] x-spin[-1, 1] y-spin[-1, 1]
		PING,
		PONG,
		JOIN,
		QUIT,
		KICK,
		MOVECB, // x-pos[0, 1] y-pos[0, 1]
		PLACECB, // x-pos[0, 1] y-pos[0, 1]
		PLAY,
		WAIT,
		BIH,
		END // winner(boolean) End.Reason
	};

	public Game() {
		network = new Network(2);
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
	
	public void endMatch()
	{
		match = null;
	}
}

package lpool.logic;

import lpool.network.EventChecker;

public class Game extends EventChecker{
	public static final int numPlayers = 2;
	private Match match = new Match();
	
	public Game() {
		super(numPlayers);
		network.startConnecting();
	}
	
	public void tick(float dt)
	{
		match.tick(dt);
		network.tick();
		triggerEvents();
	}
	
	public Match getMatch()
	{
		return match;
	}

	@Override
	protected void conEvent(int clientID) {
		if (network.isClientConnected(clientID))
			System.out.println("Client #" + clientID + " connected!");
		else
			System.out.println("Client #" + clientID + " disconnected!");
	}

	@Override
	protected void commEvent(int clientID, String msg) {
		System.out.println("Client #" + clientID + " sent the following message: " + msg);
		match.makeShot(Float.parseFloat(msg));
	}
}

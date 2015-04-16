package lpool.logic;

import lpool.network.EventChecker;

public class Game extends EventChecker{
	public static final int numPlayers = 2;
	
	public Game() {
		super(numPlayers);
		network.startConnecting();
	}
	
	public void tick(float dt)
	{
		network.tick();
	}

	@Override
	protected void conEvent(int clientID) {
		if (network.isClientConnected(clientID))
			System.out.println("Client #" + clientID + " connected!");
		else
		{
			System.out.println("Client #" + clientID + " disconnected!");
		}
	}

	@Override
	protected void commEvent(int clientID) {
		// TODO Auto-generated method stub
		
	}
}

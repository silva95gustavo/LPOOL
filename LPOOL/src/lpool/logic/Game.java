package lpool.logic;

import lpool.network.EventChecker;

public class Game extends EventChecker{
	public static final int numPlayers = 2;
	
	public Game() {
		super(numPlayers);
	}

	@Override
	protected void conEvent(int clientID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void commEvent(int clientID) {
		// TODO Auto-generated method stub
		
	}
}

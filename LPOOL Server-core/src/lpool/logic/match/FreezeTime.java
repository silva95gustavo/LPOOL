package lpool.logic.match;

import lpool.logic.state.State;

public class FreezeTime implements State<Match> {	
	
	public static final float freezeTime = 5;
	private float elapsedTime;
	
	@Override
	public void enter(Match match) {
		elapsedTime = 0;
	}

	@Override
	public void update(Match match, float dt) {
		elapsedTime += dt;
		if (elapsedTime >= freezeTime)
			match.getStateMachine().changeState(new Play());
	}

	@Override
	public void exit(Match owner) {
		// TODO Auto-generated method stub
		
	}

}

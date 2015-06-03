package lpool.logic.match;

import lpool.logic.ball.Ball;
import lpool.logic.state.State;

public class BallsMoving implements State<Match> {
	
	@Override
	public void enter(Match match) {
	}

	@Override
	public void update(Match match, float dt) {
		match.worldStep(dt);
		match.deleteRemovedballs();
		match.tickBalls(dt);
		if (allStopped(match.getBalls()))
			continueMatch(match);
	}
	
	private boolean allStopped(Ball[] balls)
	{
		for (int i = 0; i < balls.length; i++)
		{
			if (!balls[i].isStopped())
				return false;
		}
		return true;
	}
	
	private void continueMatch(Match match)
	{
		if (match.isBallInHand())
			match.getStateMachine().changeState(new CueBallInHand());
		else
			match.getStateMachine().changeState(new Play()); // TODO check if the cue ball can be moved by hand or not
	}
}

package lpool.logic.match;

import lpool.logic.ball.Ball;
import lpool.logic.state.State;
import lpool.logic.state.TransitionState;

public class BallsMoving implements State<Match>{
	
	@Override
	public void enter(Match match) {
	}

	@Override
	public void update(Match match, float dt) {
		match.worldStep(dt);
		match.deleteRemovedBalls();
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
		if (matchEnded(match))
		{
			Integer winner = new Integer(0);
			End.Reason reason = whyMatchEnded(match, winner);
			match.getStateMachine().changeState(new End(reason, winner));
		}
		else if (match.isBallInHand())
			match.getStateMachine().changeState(new TransitionState<Match>(match.getStateMachine(), this, new CueBallInHand()));
		else
			match.getStateMachine().changeState(new TransitionState<Match>(match.getStateMachine(), this, new Play()));
	}
	
	private boolean matchEnded(Match match)
	{
		return !match.getBlackBall().isOnTable();
	}
	
	private End.Reason whyMatchEnded(Match match, Integer winner)
	{
		Ball[] balls = match.getBalls();
		for (int i = 0; i < balls.length; i++)
		{
			if (!balls[i].isOnTable())
				continue;
			if (match.getPlayerBallsType(match.getCurrentPlayer()).equals(balls[i].getType()))
			{
				winner = (match.getCurrentPlayer() == 0 ? 1 : 0);
				return End.Reason.BLACK_BALL_SCORED_ACCIDENTALLY;
			}
		}
		winner = match.getCurrentPlayer();
		return End.Reason.BLACK_BALL_SCORED_AS_LAST;
	}
}

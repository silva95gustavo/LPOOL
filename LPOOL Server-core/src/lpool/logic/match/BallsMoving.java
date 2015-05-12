package lpool.logic.match;

import com.badlogic.gdx.math.Vector2;

import lpool.logic.Ball;
import lpool.logic.state.State;

public class BallsMoving implements State<Match> {
	
	@Override
	public void enter(Match match) {
		match.setAiming(false);
	}

	@Override
	public void update(Match owner, float dt) {
		owner.worldStep(dt);
		owner.deleteRemovedballs();
		owner.tickBalls(dt);
		if (allStopped(owner.getBalls()))
			owner.getStateMachine().changeState(new Play());
	}
	
	private boolean allStopped(Ball[] balls)
	{
		for (int i = 0; i < balls.length; i++)
		{
			if (balls[i].isOnTable())
				if (!balls[i].getVelocity().equals(new Vector2(0, 0)))
					return false;
		}
		return true;
	}

}

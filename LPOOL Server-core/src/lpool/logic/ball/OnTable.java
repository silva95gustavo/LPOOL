package lpool.logic.ball;

import lpool.logic.state.State;

public class OnTable implements State<Ball> {

	@Override
	public void enter(Ball ball) {
	}

	@Override
	public void update(Ball ball, float dt) {
		ball.updatePosition();
	}

	@Override
	public void exit(Ball owner) {
	}

}

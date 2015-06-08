package lpool.logic.ball;

import lpool.logic.state.State;

public class InHole implements State<Ball> {

	@Override
	public void enter(Ball ball) {
		ball.setVisible(false);
	}

	@Override
	public void update(Ball ball, float dt) {
	}

	@Override
	public void exit(Ball owner) {
	}

}

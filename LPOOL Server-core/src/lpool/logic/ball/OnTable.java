package lpool.logic.ball;

import lpool.logic.state.State;

public class OnTable implements State<Ball> {

	public OnTable() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void enter(Ball ball) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Ball ball, float dt) {
		ball.updatePosition();
	}

}

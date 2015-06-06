package lpool.logic.ball;

import lpool.logic.state.State;

public class InHole implements State<Ball> {

	public InHole() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void enter(Ball ball) {
		ball.setVisible(false);
	}

	@Override
	public void update(Ball ball, float dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit(Ball owner) {
		// TODO Auto-generated method stub
		
	}

}

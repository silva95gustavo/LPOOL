package lpool.logic.ball;

import com.badlogic.gdx.math.Vector2;

import lpool.logic.Table;
import lpool.logic.state.State;

public class EnteringHole implements State<Ball> {
	private int holeID;
	private Vector2 droppingPoint;
	private float droppingSpeed;
	private float droppingTime;
	private float t;
	
	public EnteringHole(int holeID) {
		this.holeID = holeID;
	}

	@Override
	public void enter(Ball ball) {
		this.droppingPoint = ball.getPosition();
		this.droppingSpeed = ball.getVelocity().len();
		this.droppingTime = droppingPoint.dst(Table.getHolePos(holeID)) / droppingSpeed;
		this.t = 0;
		ball.setToBeDeleted();
	}

	@Override
	public void update(Ball ball, float dt) {
		t += dt;
		
		if (t > droppingTime)
			ball.getStateMachine().changeState(new InHole());
		
		Vector2 holePos = Table.getHolePos(holeID);
		ball.setPosition(droppingPoint.cpy().lerp(holePos, t * droppingSpeed / Table.holeRadius));
	}

	@Override
	public void exit(Ball owner) {
		// TODO Auto-generated method stub
		
	}

}

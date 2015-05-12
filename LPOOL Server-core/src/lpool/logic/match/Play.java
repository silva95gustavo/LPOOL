package lpool.logic.match;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import lpool.logic.Game.ProtocolCmd;
import lpool.logic.state.State;
import lpool.network.Message;

public class Play implements State<Match>, Observer{
	private Match match;
	
	private float angleVar;
	private float lastAngle;
	private long lastAngleTime;
	
	@Override
	public void enter(Match match) {
		match.setAiming(true);
		this.match = match;
		angleVar = 0;
		lastAngle = (float)Math.PI;
		lastAngleTime = System.currentTimeMillis();
		match.getNetwork().addMsgObserver(this);
	}

	@Override
	public void update(Match match, float dt) {
	}

	@Override
	public void update(Observable o, Object obj) {
		Message msg = (Message)obj;
		Scanner sc = new Scanner(msg.msg);
		ProtocolCmd cmd = Message.readCmd(sc);
		
		switch (cmd)
		{
		case ANGLE:
		{
			if (!sc.hasNextFloat())
				break;
			float angle = -sc.nextFloat();
			long currTime = System.currentTimeMillis();

			if (currTime - lastAngleTime < 10)
				angleVar = 0; // Don't interpolate
			else
			{
				angleVar = (float)((angle - lastAngle) / ((double)currTime/1000 - (double)lastAngleTime/1000));
				lastAngleTime = currTime;
				lastAngle = angle;
			}
			match.setCueAngle(angle);
			break;
		}
		case FIRE:
		{
			if (!sc.hasNextFloat())
				break;
			float force = sc.nextFloat();
			match.makeShot(force);
			match.getStateMachine().changeState(new BallsMoving());
			match.getNetwork().deleteMsgObserver(this);
			break;
		}
		default:
			break;
		}
		sc.close();
	}
}

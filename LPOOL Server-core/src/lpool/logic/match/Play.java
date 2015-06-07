package lpool.logic.match;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import com.badlogic.gdx.physics.box2d.CircleShape;

import lpool.logic.Game.ProtocolCmd;
import lpool.logic.state.State;
import lpool.logic.state.TransitionState;
import lpool.network.Message;

public class Play implements State<Match>, Observer{
	private Match match;
	
	private float angleVar;
	private float lastAngle;
	private long lastAngleTime;
	
	public Play()
	{
	}
	
	@Override
	public void enter(Match match) {
		this.match = match;
		angleVar = 0;
		lastAngle = (float)Math.PI;
		lastAngleTime = System.currentTimeMillis();
		match.getNetwork().addMsgObserver(this);
		match.setPlayValidator(new PlayValidator(match.isOpeningShot(), match.playerBallsDefined(), match.getPlayerBallsType(match.getCurrentPlayer()), match.getBalls())); // TODO opening shot
		match.sendStateToClients();
	}

	@Override
	public void update(Match match, float dt) {
		match.predictShot();
	}

	@Override
	public void update(Observable o, Object obj) {
		Message msg = (Message)obj;
		Scanner sc = new Scanner(msg.body);
		ProtocolCmd cmd = Message.readCmd(sc);
		if (cmd == null) return;
		if (msg.clientID != match.getCurrentPlayer()) return;
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
			if (!sc.hasNextFloat())
				break;
			float xSpin = sc.nextFloat();
			if (!sc.hasNextFloat())
				break;
			float ySpin = sc.nextFloat();
			match.makeShot(force, xSpin, ySpin);
			TransitionState<Match> next = new TransitionState<Match>(match.getStateMachine(), this, new BallsMoving());
			next.data = force;
			match.getStateMachine().changeState(next);
			break;
		}
		default:
			break;
		}
		sc.close();
	}

	@Override
	public void exit(Match match) {
		match.getNetwork().deleteMsgObserver(this);
	}
}

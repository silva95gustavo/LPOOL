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

	@Override
	public void enter(Match match) {
		this.match = match;
		match.getNetwork().addMsgObserver(this);
		match.setPlayValidator(new PlayValidator(match.isOpeningShot(), match.playerBallsDefined(), match.getPlayerBallsType(match.getCurrentPlayer()), match.getBalls()));
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

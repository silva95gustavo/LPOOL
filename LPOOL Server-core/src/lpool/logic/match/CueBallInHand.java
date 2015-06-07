package lpool.logic.match;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;

import lpool.logic.Game;
import lpool.logic.Table;
import lpool.logic.Game.ProtocolCmd;
import lpool.logic.ball.Ball;
import lpool.logic.state.State;
import lpool.network.Message;

public class CueBallInHand implements State<Match>, Observer{
	private Match match;
	private boolean validPosition;
	private Vector2 attemptedPosition;
	public CueBallInHand()
	{
		this.validPosition = false;
	}

	@Override
	public void enter(Match match) {
		this.match = match;
		match.getNetwork().addMsgObserver(this);
		match.sendStateToClients();
		attemptedPosition = new Vector2(Table.width / 2, Table.height / 2);
		if (match.getCueBall().isOnTable())
		{
			match.getCueBall().setToBeDeleted();
			match.getCueBall().setVisible(false);
		}
		match.getCueBall().setPosition(new Vector2(-999, -999)); // Hide the ball
	}

	@Override
	public void update(Observable o, Object obj) {
		Message msg = (Message)obj;
		if (msg.clientID != match.getCurrentPlayer()) return;
		Scanner sc = new Scanner(msg.body);
		ProtocolCmd cmd = Message.readCmd(sc);
		if (cmd == null) return;
		switch (cmd)
		{
		case MOVECB: // x-pos[0, 1] y-pos[0, 1]
		case PLACECB: // x-pos[0, 1] y-pos[0, 1]
		{
			if (!sc.hasNextFloat())
				break;
			float xPos = sc.nextFloat();

			if (!sc.hasNextFloat())
				break;
			float yPos = sc.nextFloat();
			System.out.println("Updating attempted position to: " + attemptedPosition);
			attemptedPosition = new Vector2(xPos, yPos);
			if (attemptedPosition.x < 0) attemptedPosition.x = 0;
			if (attemptedPosition.x > 1) attemptedPosition.x = 1;
			if (attemptedPosition.y < 0) attemptedPosition.y = 0;
			if (attemptedPosition.y > 1) attemptedPosition.y = 1;
			validPosition = isPositionValid(attemptedPosition);
			if (cmd.equals(Game.ProtocolCmd.PLACECB) && validPosition)
			{
				match.respawnCueBall(attemptedPosition);
				match.getStateMachine().changeState(new Play());
			}
			break;
		}
		default:
			break;
		}
		sc.close();
	}

	private boolean isPositionValid(Vector2 dest)
	{
		if (dest.x < 0 || dest.x > 1 || dest.y < 0 || dest.y > 1) return false;

		dest.y = 1 - dest.y;		
		dest.scl(Table.width - 2 * Ball.radius, Table.height - 2 * Ball.radius).add(new Vector2(Ball.radius, Ball.radius));

		for (int i = 1; i < match.getBalls().length; i++)
		{
			if (dest.dst(match.getBalls()[i].getPosition()) < 2 * Ball.radius)
				return false;
		}
		return true;
	}

	@Override
	public void update(Match owner, float dt) {
	}

	public boolean isValidPosition()
	{
		return validPosition;
	}
	
	public Vector2 getAttemptedPosition()
	{
		return attemptedPosition;
	}

	@Override
	public void exit(Match owner) {
		match.getNetwork().deleteMsgObserver(this);
	}
}

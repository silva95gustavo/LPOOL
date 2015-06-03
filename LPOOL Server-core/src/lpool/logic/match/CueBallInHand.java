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
	
	public CueBallInHand()
	{
		this.validPosition = false;
	}
	
	@Override
	public void enter(Match match) {
		this.match = match;
		match.getNetwork().addMsgObserver(this);
	}

	@Override
	public void update(Observable o, Object obj) {
		Message msg = (Message)obj;
		Scanner sc = new Scanner(msg.body);
		ProtocolCmd cmd = Message.readCmd(sc);
		
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
			
			validPosition = tryMoveCueBall(new Vector2(xPos, yPos));
				if (cmd.equals(Game.ProtocolCmd.PLACECB) && validPosition)
				{
					match.setBallInHand(false);
					match.getStateMachine().changeState(new Play());
				}
			break;
		}
		default:
			break;
		}
		sc.close();
	}
	
	private boolean tryMoveCueBall(Vector2 dest)
	{
		if (dest.x < 0 || dest.x > 1 || dest.y < 0 || dest.y > 1)
			return false;
		
		dest.scl(Table.width - 2 * Ball.radius, Table.height - 2 * Ball.radius).add(new Vector2(Table.border, Table.border));
		
		for (int i = 1; i < match.getBalls().length; i++)
		{
			if (dest.dst(match.getBalls()[i].getPosition()) < 2 * Ball.radius)
				return false;
		}
		
		match.getCueBall().setPosition(dest);
		return true;
	}

	@Override
	public void update(Match owner, float dt) {
	}
	
	public boolean isValidPosition()
	{
		return validPosition;
	}
}

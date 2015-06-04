package lpool.logic.match;

import lpool.logic.state.State;

public class End implements State<Match> {
	public enum Reason
	{
		BLACK_BALL_SCORED_AS_LAST,
		BLACK_BALL_SCORED_ACCIDENTALLY,
		TIMEOUT,
		DISCONNECT
	}
	private Reason reason;
	private int winner;

	public End(Reason reason, int winner) {
		this.reason = reason;
		this.winner = winner;
	}

	@Override
	public void enter(Match match) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Match match, float dt) {
		// TODO Auto-generated method stub
		
	}

	public int getWinner() {
		return winner;
	}

	public Reason getReason() {
		return reason;
	}
}

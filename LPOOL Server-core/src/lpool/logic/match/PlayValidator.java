package lpool.logic.match;

import lpool.logic.ball.Ball;

/**
 * Class responsible for analyzing the legality of a {@link Play} and determining whether or not the current player should play again.
 * Some of its methods must be called at the appropriate time so that it knows what events happened and is able to decide accordingly.
 * 
 * @see #actionBallHit(Ball.Type)
 * @see #actionBorderHit()
 * @see #actionBallScore(Ball.Type)
 * 
 * @author Gustavo
 *
 */
public class PlayValidator {
	private boolean openingShot;
	private boolean playerBallsDefined;
	private Ball.Type currentPlayerBalls;
	private boolean cueTouchedBall;
	private boolean cueTouchedValidBall;
	private int numBorderHits;
	private boolean validBallScored;
	private boolean cueBallScored;
	private boolean blackBallAccidentallyScored;
	private boolean scoredHisBall;
	private Ball[] balls;
	
	/**
	 * Constructor.
	 * @param openingShot Whether or not this is the opening shot.
	 * @param playerBallsDefined Whether or not the balls each player has to score have already been defined.
	 * @param currentPlayerBalls The ball type of the current player or null if not yet defined.
	 * @param balls An array with all the balls (scored or not).
	 */
	public PlayValidator(boolean openingShot, boolean playerBallsDefined, Ball.Type currentPlayerBalls, Ball[] balls) {
		this.openingShot = openingShot;
		this.playerBallsDefined = playerBallsDefined;
		this.currentPlayerBalls = currentPlayerBalls;
		this.cueTouchedBall = false;
		this.cueTouchedValidBall = false;
		this.numBorderHits = 0;
		this.validBallScored = false;
		this.blackBallAccidentallyScored = false;
		this.cueBallScored = false;
		this.scoredHisBall = false;
		this.balls = balls;
	}

	/**
	 * Method that must be called when the cue ball collides with other ball.
	 * @param hitBallType The type of the ball hit by the cue ball.
	 */
	public void actionBallHit(Ball.Type hitBallType)
	{
		if (!cueTouchedBall)
			cueTouchedValidBall = isValidBall(hitBallType);
		cueTouchedBall = true;
	}

	/**
	 * Method that must be called when a ball hits a table bumper.
	 */
	public void actionBorderHit()
	{
		if (cueTouchedValidBall)
			numBorderHits++;
	}

	/**
	 * Method that must be called when any ball is scored.
	 * @param scoredBallType The {@link lpool.logic.ball.Ball#Type} of the scored ball.
	 */
	public void actionBallScore(Ball.Type scoredBallType)
	{
		if (scoredBallType == Ball.Type.BLACK)
		{
			if (isPlayingForBlack())
				validBallScored = true;
			else
				blackBallAccidentallyScored = true;
		}
		else
		{
			if (scoredBallType == Ball.Type.SOLID || scoredBallType == Ball.Type.STRIPE)
				validBallScored = true;
			else if (scoredBallType == Ball.Type.CUE)
				cueBallScored = true;
			if (scoredBallType == currentPlayerBalls)
				scoredHisBall = true;
		}
	}

	/**
	 * Analyzes the legality of a play according to the game rules.
	 * @return True if the play was valid, false otherwise.
	 */
	public boolean isValid()
	{
		if (!cueTouchedBall || cueBallScored || !cueTouchedValidBall || blackBallAccidentallyScored)
			return false;
		else if (openingShot)
			return (numBorderHits >= 4) || validBallScored;
		else
			return (numBorderHits >= 1) || validBallScored;
	}

	/**
	 * 
	 * @return Whether or not the current player has the right to play again.
	 */
	public boolean playsAgain()
	{
		if (!playerBallsDefined && validBallScored)
			return true;
		
		if (validBallScored && openingShot)
			return true;
		
		if (scoredHisBall && isValid())
			return true;
		
		return false;
	}

	private boolean isValidBall(Ball.Type ballType)
	{
		if (!playerBallsDefined)
			return true;
		if (isPlayingForBlack())
			return ballType == Ball.Type.BLACK;
		else
			return ballType == currentPlayerBalls;
	}

	private boolean isPlayingForBlack()
	{
		if (!playerBallsDefined)
			return false;
		for (int i = 0; i < balls.length; i++)
		{
			if (balls[i].isOnTable() && balls[i].getType().equals(currentPlayerBalls))
			{
				return false;
			}
		}
		return true;
	}
}

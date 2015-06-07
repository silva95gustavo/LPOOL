package lpool.logic.match;

import lpool.logic.ball.Ball;

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

	public void actionBallHit(Ball.Type hitBallType)
	{
		if (!cueTouchedBall)
			cueTouchedValidBall = isValidBall(hitBallType);
		cueTouchedBall = true;
	}

	public void actionBorderHit()
	{
		if (cueTouchedValidBall)
			numBorderHits++;
	}

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

	public boolean isValid()
	{
		if (!cueTouchedBall || cueBallScored || !cueTouchedValidBall || blackBallAccidentallyScored)
			return false;
		else if (openingShot)
			return (numBorderHits >= 4) || validBallScored;
		else
			return (numBorderHits >= 1) || validBallScored;
	}

	public boolean playsAgain()
	{
		return (validBallScored && openingShot) || (scoredHisBall && isValid());
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

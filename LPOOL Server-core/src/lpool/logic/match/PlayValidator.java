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
		if (scoredBallType == Ball.Type.SOLID || scoredBallType == Ball.Type.STRIPE)
			validBallScored = true;
		else if (scoredBallType == Ball.Type.CUE)
			cueBallScored = true;
		else if (scoredBallType == Ball.Type.BLACK)
		{
			if (isPlayingForBlack())
				validBallScored = true;
			else
				blackBallAccidentallyScored = true;
		}
	}

	public boolean isValid()
	{
		if (!cueTouchedBall)
		{
			System.out.println("Play invalid because cue ball didn't touch any other ball.");
			return false;
		}
		if (cueBallScored)
		{
			System.out.println("Play invalid because the cue ball was scored.");
			return false;
		}
		if (!cueTouchedValidBall)
		{
			System.out.println("Play invalid because the cue ball didn't touch any valid ball.");
			return false;
		}
		if (blackBallAccidentallyScored)
		{
			System.out.println("Play invalid because the black ball was accidentally scored.");
			return false;
		}
		if (!cueTouchedBall || cueBallScored || !cueTouchedValidBall || blackBallAccidentallyScored)
			return false;
		else if (openingShot)
			return (numBorderHits >= 4) || validBallScored;
		else
			return (numBorderHits >= 1) || validBallScored;
	}

	private boolean isValidBall(Ball.Type ballType)
	{
		if (!playerBallsDefined)
			return true;
		if (isPlayingForBlack())
			return ballType == Ball.Type.BLACK;
		else return ballType == currentPlayerBalls;
	}

	private boolean isPlayingForBlack()
	{
		for (int i = 0; i < balls.length; i++)
		{
			if (balls[i].getType() == currentPlayerBalls)
				return false;
		}
		return true;
	}
}

package lpool.logic;

import java.util.Scanner;

import lpool.network.EventChecker;

public class Game extends EventChecker{
	public static final int numPlayers = 2;
	private Match match = new Match();
	private float last;

	public Game() {
		super(numPlayers);
		network.startConnecting();
	}

	public void tick(float dt)
	{
		match.tick(dt);
		network.tick();
		triggerEvents();
	}

	public Match getMatch()
	{
		return match;
	}

	@Override
	protected void conEvent(int clientID) {
		if (network.isClientConnected(clientID))
			System.out.println("Client #" + clientID + " connected!");
		else
			System.out.println("Client #" + clientID + " disconnected!");
	}

	@Override
	protected void commEvent(int clientID, String msg) {
		System.out.println("Client #" + clientID + " sent the following message: " + msg);
		try
		{
			float angle = -Float.parseFloat(msg);
			/*if (Math.abs(angle - last) < 0.1)
			{
				System.out.println("Angle: " + angle + " Turning " + (- 0.2f * angle * Math.abs(angle)));
				match.setCueAngle(match.getCueAngle() - 0.2f * angle * Math.abs(angle));
			}
			last -= (angle - last) * 1f;*/
			match.setCueAngle(angle);
		}
		catch(NumberFormatException e)
		{
			Scanner sc = new Scanner(msg);
			if (sc.hasNext() && sc.next().equals("FIRE") && sc.hasNextFloat())
			{
				float force = (float)sc.nextLong() / 1000;
				match.makeShot(force);
			}
			sc.close();
		}
	}
}

package lpool.logic;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

import com.badlogic.gdx.Net.Protocol;

import lpool.logic.match.Match;
import lpool.network.EventChecker;

public class Game extends EventChecker{
	public static final int numPlayers = 2;
	
	
	private Match match;
	private float angleVar;
	private float lastAngle;
	private long lastAngleTime;

	public enum ProtocolCmd {
		ANGLE, // angle
		FIRE // force
	};

	public Game() {
		super(numPlayers);

		match = new Match();
		angleVar = 0;
		lastAngle = (float)Math.PI;
		lastAngleTime = System.currentTimeMillis();

		network.startConnecting();
	}

	public void tick(float dt)
	{
		//interpolateAngle(dt);
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
		Scanner sc = new Scanner(msg);
		sc.useLocale(Locale.US);
		if (!sc.hasNextInt())
		{
			sc.close();
			return;
		}
		int readCmd = sc.nextInt();
		if (readCmd < 0 || readCmd >= ProtocolCmd.values().length)
		{
			sc.close();
			return;
		}
		ProtocolCmd cmd = ProtocolCmd.values()[readCmd];
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
			break;
		}
		default:
			break;
		}
		sc.close();
	}

	private void interpolateAngle(float dt)
	{
		match.setCueAngle(match.getCueAngle() + angleVar * dt);
	}

}

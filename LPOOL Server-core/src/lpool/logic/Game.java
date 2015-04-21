package lpool.logic;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Scanner;

import lpool.network.EventChecker;

public class Game extends EventChecker{
	public static final int numPlayers = 2;
	private Match match = new Match();
	private float last;

	public enum ProtocolCmd {
		ANGLE, // angle
		FIRE // force
	};

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
	protected void commEvent(int clientID, byte[] msg) {
		final ByteArrayInputStream bais = new ByteArrayInputStream(msg);
		final DataInputStream dais = new DataInputStream(bais);
		try {
			int aaa = dais.readInt();
			if (aaa < 0 || aaa >= ProtocolCmd.values().length)
				return;
			System.out.println("Command: " + aaa);
			ProtocolCmd cmd = ProtocolCmd.values()[aaa];
			switch (cmd)
			{
			case ANGLE:
			{
				float angle = -dais.readFloat();
				System.out.println("Angle: " + angle + " Turning " + (- 0.2f * angle * Math.abs(angle)));
				match.setCueAngle(match.getCueAngle() - 0.2f * angle * Math.abs(angle));
				last = angle;
				match.setCueAngle(angle);
				break;
			}
			case FIRE:
			{
				float force = dais.readFloat();
				match.makeShot(force);
				break;
			}
			default:
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

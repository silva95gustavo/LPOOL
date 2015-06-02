package lpool.network;

import java.util.Locale;
import java.util.Scanner;

import lpool.logic.Game.ProtocolCmd;

public class Message {
	public int clientID;
	public final String body;
	
	public Message(int clientID, String body) {
		this.clientID = clientID;
		this.body = body;
	}
	
	public static lpool.logic.Game.ProtocolCmd readCmd(Scanner sc)
	{
		sc.useLocale(Locale.US);
		if (!sc.hasNextInt())
		{
			sc.close();
			return null;
		}
		int readCmd = sc.nextInt();
		if (readCmd < 0 || readCmd >= ProtocolCmd.values().length)
		{
			sc.close();
			return null;
		}
		ProtocolCmd cmd = ProtocolCmd.values()[readCmd];
		
		return cmd;
	}
}

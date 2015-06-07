package lpool.network;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import lpool.logic.Game;

public class JoinReceiver extends Thread {
	private Network n;
	private Socket s;
	public volatile boolean finished = false;

	public JoinReceiver(Network n, Socket s) {
		this.n = n;
		this.s = s;
	}

	@Override
	public void run()
	{
		System.out.println("Running join receiver.");
		while (!finished)
		{
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String str = br.readLine();
				if (str == null)
					stopMe();
				else
				{
					Scanner sc = new Scanner(str);
					Game.ProtocolCmd cmd = Message.readCmd(sc);
					if (cmd == Game.ProtocolCmd.JOIN)
					{
						String name = null;
						if (sc.hasNextLine())
						{
							name = sc.nextLine();
							name = name.trim();
							if (name.length() > Game.maxNameLength) {
								name = name.substring(0, Game.maxNameLength);
							}
						}
						if (!n.addClient(s, name))
						{
							s.close();
						}
						stopMe();
					}
					sc.close();
				}
			} catch (IOException e) {
				stopMe();
			}
		}
	}

	public void stopMe()
	{
		System.out.println("Stopped join receiver");
		finished = true;
	}
}

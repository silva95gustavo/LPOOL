package lpool.network;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javafx.concurrent.Task;
import lpool.logic.Game;

public class JoinReceiver implements Runnable {
	public static final long timeout = 5;
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
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			executor.submit(new Runnable() {
				@Override
				public void run() {
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
					}
				}
			}).get(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		} catch (TimeoutException e) {
		}
	}

	public void stopMe()
	{
		System.out.println("Stopped join receiver");
		finished = true;
	}
}

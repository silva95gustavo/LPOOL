package lpool.network;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import lpool.logic.Game;

public class Receiver extends Thread {
	private Socket s;
	private ConcurrentLinkedQueue<String> clientCommEvents;
	public volatile boolean finished = false;

	public Receiver(Socket s, ConcurrentLinkedQueue<String> clientCommEvents) {
		this.s = s;
		this.clientCommEvents = clientCommEvents;
	}

	@Override
	public void run()
	{
		while (!finished)
		{
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String str = br.readLine();
				if (str == null)
					stopMe();
				else
				{
					System.out.println("Received message: " + str);
					clientCommEvents.add(str);
				}
			} catch (IOException e) {
				stopMe();
			}
		}
	}

	public void stopMe()
	{
		System.out.println("Stopped receiver");
		finished = true;
	}
}

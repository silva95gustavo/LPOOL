package lpool.network;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

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
				System.out.println("Receiving...");
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String str = br.readLine();
				System.out.println("Received...");
				if (str == null)
					System.out.println("received null");
				if (str == null)
					stopMe();
				else
					clientCommEvents.add(str);
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

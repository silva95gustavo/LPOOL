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
		try {
			while (!finished)
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String str = br.readLine();
				if (str == null)
					stopMe();
				else
					clientCommEvents.add(str);
			}
			// TODO
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void stopMe()
	{
		finished = true;
	}
}

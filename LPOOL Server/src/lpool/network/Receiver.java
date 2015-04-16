package lpool.network;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Receiver extends Thread {
	private Socket s;
	public volatile boolean finished = false;
	public Receiver(Socket s) {
		this.s = s;
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

package lpool.network;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Receiver extends Thread {
	private Socket s;

	public Receiver(Socket s) {
		this.s = s;
	}

	@Override
	public void run()
	{
		try {
			while (true)
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				System.out.println(br.readLine());
			}
			// TODO
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

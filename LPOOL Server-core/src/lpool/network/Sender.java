package lpool.network;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender extends Thread {
	private Socket s;
	private LinkedBlockingQueue<String> toBeSent;
	public volatile boolean finished = false;

	public Sender(Socket s, LinkedBlockingQueue<String> toBeSent) {
		this.s = s;
		this.toBeSent = toBeSent;
	}

	@Override
	public void run()
	{
		while (!finished)
		{
			try
			{
				String msg = toBeSent.take();
				System.out.println("Sending msg " + msg);
				PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
				pw.println(msg);
			}
			catch (InterruptedException e) {
				stopMe();
			} catch (IOException e) {
				e.printStackTrace();
				stopMe();
			}
		}
	}

	public void stopMe()
	{
		this.interrupt(); // In case it's waiting for messages in the queue
		finished = true;
	}
}

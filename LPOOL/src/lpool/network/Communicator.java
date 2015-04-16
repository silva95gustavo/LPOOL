package lpool.network;

import java.net.Socket;
import java.util.Queue;

public class Communicator extends Thread{
	private Socket s;
	private Receiver r;
	private Queue<String> queue;
	
	public Communicator(Socket s) {
		this.s = s;
		r = new Receiver(s);
	}
	
	@Override
	public void run()
	{
		r.start();
	}
	
	public Socket getSocket()
	{
		return s;
	}
}

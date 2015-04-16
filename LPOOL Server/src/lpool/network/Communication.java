package lpool.network;

import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Communication {
	private Socket s;
	private Receiver r;
	private ConcurrentLinkedQueue<String> clientCommEvents;
	
	public Communication(Socket s) {
		this.s = s;
		this.clientCommEvents = new ConcurrentLinkedQueue<String>();
		r = new Receiver(s, clientCommEvents);
		r.start();
	}
	
	public Socket getSocket()
	{
		return s;
	}
	
	public void close()
	{
		r.stopMe();
	}
	
	public boolean isConnClosed()
	{
		return r.finished;
	}
	
	public ConcurrentLinkedQueue<String> getClientCommEvents()
	{
		return clientCommEvents;
	}
}

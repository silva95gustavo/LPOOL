package lpool.network;

import java.net.Socket;

public class Communication {
	private Socket s;
	private Receiver r;
	
	public Communication(Socket s) {
		this.s = s;
		r = new Receiver(s);
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
}

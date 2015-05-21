package lpool.network;

import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Communication {
	private Socket s;
	private Receiver rec;
	private Sender sen;
	private ConcurrentLinkedQueue<String> clientCommEvents;
	private LinkedBlockingQueue<String> toBeSent;
	
	private AliveKeeper ak;
	
	private boolean alive;
	
	public Communication(Network network, Socket s) {
		System.out.println("Creating communication...");
		this.alive = true;
		this.s = s;
		this.clientCommEvents = new ConcurrentLinkedQueue<String>();
		this.toBeSent = new LinkedBlockingQueue<String>();
		
		rec = new Receiver(s, clientCommEvents);
		rec.start();
		
		sen = new Sender(s, toBeSent);
		sen.start();
		
		ak = new AliveKeeper(network, this);
	}
	
	public Socket getSocket()
	{
		return s;
	}
	
	public void close()
	{
		System.out.println("Closing communication");
		rec.stopMe();
		sen.stopMe();
		alive = false;
	}
	
	public boolean isConnClosed()
	{
		if (rec.finished)
			alive = false;
		
		if (sen.finished)
			alive = false;
		
		if (alive)
			return false;
		
		close();
		return true;
	}
	
	public ConcurrentLinkedQueue<String> getClientCommEvents()
	{
		return clientCommEvents;
	}
	
	public void send(String msg)
	{
		toBeSent.add(msg);
	}
	
	public void resetHeartbeat()
	{
		ak.reset();
	}
	
	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}
}

package lpool.network;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.utils.Timer;

public class Communication {
	private Socket s;
	private Receiver rec;
	private Sender sen;
	private ConcurrentLinkedQueue<String> clientCommEvents;
	private LinkedBlockingQueue<String> toBeSent;
	private int clientID;

	private AliveKeeper ak;

	private boolean alive;

	public Communication(Network network, Socket s, int clientID) {
		System.out.println("Creating communication...");
		this.alive = true;
		this.s = s;
		this.clientCommEvents = new ConcurrentLinkedQueue<String>();
		this.toBeSent = new LinkedBlockingQueue<String>();
		this.clientID = clientID;

		rec = new Receiver(s, clientCommEvents);
		rec.start();

		sen = new Sender(s, toBeSent);
		sen.start();

		ak = new AliveKeeper(network, this);
	}

	/**
	 * 
	 * @return The socket associated with this communication.
	 */
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
		Timer.schedule(new SocketCloseTask(s), 1000);
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

	public int getClientID()
	{
		return clientID;
	}
}

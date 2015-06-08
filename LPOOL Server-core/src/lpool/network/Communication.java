package lpool.network;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.badlogic.gdx.utils.Timer;

/**
 * Class responsible for handling a communication with a client, making sure it stays alive and managing the {@link Receiver} and {@link Sender}.
 * @author Gustavo
 *
 */
public class Communication {
	private Socket s;
	private Receiver rec;
	private Sender sen;
	private ConcurrentLinkedQueue<String> clientCommEvents;
	private LinkedBlockingQueue<String> toBeSent;
	private int clientID;

	private AliveKeeper ak;

	private boolean alive;

	/**
	 * Constructor.
	 * @param network The network this communication is associated to.
	 * @param s The socket this communication is associated to.
	 * @param clientID The client this communication is associated to.
	 */
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

	/**
	 * Closes the communication.
	 * The socket is closed 1 second later to leave time for pending messages to be read.
	 */
	public void close()
	{
		System.out.println("Closing communication");
		rec.stopMe();
		sen.stopMe();
		alive = false;
		Timer.schedule(new SocketCloseTask(s), 1000);
	}

	/**
	 * 
	 * @return True if the connection is closed, false otherwise.
	 */
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

	/**
	 * 
	 * @return A queue with the messages received.
	 */
	ConcurrentLinkedQueue<String> getClientCommEvents()
	{
		return clientCommEvents;
	}

	/**
	 * Sends a message to this client.
	 * @param msg The message to be sent.
	 */
	void send(String msg)
	{
		toBeSent.add(msg);
	}

	/**
	 * Resets the {@link AliveKeeper}.
	 */
	void resetHeartbeat()
	{
		ak.reset();
	}

	void setAlive(boolean alive)
	{
		this.alive = alive;
	}

	int getClientID()
	{
		return clientID;
	}
}

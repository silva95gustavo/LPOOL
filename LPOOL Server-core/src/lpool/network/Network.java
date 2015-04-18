package lpool.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Network {
	public final int maxClients;
	private int numClients;
	private ServerSocket serverSocket;
	private Connector con;
	private ConcurrentLinkedQueue<Socket> clientSockets;
	private Communication[] comms;
	private Queue<Integer> clientConnEvents;

	public Network(int maxClients) {
		try {
			this.serverSocket = new ServerSocket(69);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Server socket successfully started.");
		this.clientSockets = new ConcurrentLinkedQueue<Socket>();
		this.con = new Connector(serverSocket, maxClients, clientSockets);
		this.comms = new Communication[maxClients];
		this.maxClients = maxClients;
		this.numClients = 0;
		this.clientConnEvents = new LinkedList<Integer>();
	}

	public void tick()
	{
		while (!clientSockets.isEmpty())
		{
			Socket clientSocket = clientSockets.poll();
			if (!addClient(clientSocket))
			{
				try {
					clientSocket.close();
				} catch (IOException e) {
					// Do nothing
				}
			}
		}
		
		for (int i = 0; i < maxClients; i++)
		{
			if (comms[i] != null)
			{
				if (comms[i].isConnClosed())
				{
					kickClient(i);
					clientConnEvents.add(i);
				}
			}
		}
	}

	private boolean addClient(Socket client)
	{
		if (numClients >= maxClients)
		{
			return false; // Too many clients
		}

		for (int i = 0; i < comms.length; i++)
		{
			if (comms[i] == null)
				continue;

			if (client.getInetAddress().getHostAddress().equals(comms[i].getSocket().getInetAddress().getHostAddress()))
			{
				return false; // Client already connected
			}
		}

		for (int i = 0; i < comms.length; i++)
		{
			if (comms[i] == null)
			{
				numClients++;
				comms[i] = new Communication(client);
				clientConnEvents.add(i);
				return true; // Success
			}
		}
		return false; // No null position in clients array
	}

	public boolean kickClient(int clientID)
	{
		if (clientID >= maxClients)
			return false;

		if (comms[clientID] == null)
			return false;

		try {
			comms[clientID].getSocket().close();
		} catch (IOException e) {
			// Do nothing
		}
		comms[clientID].close();
		comms[clientID] = null;
		numClients--;
		return true;
	}
	
	public void startConnecting()
	{
		con.start();
	}
	
	public boolean isClientConnQueueEmpty()
	{
		return clientConnEvents.isEmpty();
	}
	
	public Integer pollClientConnQueue()
	{
		if (clientConnEvents.isEmpty())
			return null;
		else
			return clientConnEvents.poll();
	}
	
	public boolean isClientCommQueueEmpty(int clientID)
	{
		if (comms[clientID] == null)
			return true;
		
		return comms[clientID].getClientCommEvents().isEmpty();
	}
	
	public String pollClientCommQueue(int clientID)
	{
		if (isClientCommQueueEmpty(clientID))
			return null;
		else
			return comms[clientID].getClientCommEvents().poll();
	}

	public boolean isClientConnected(int clientID) {
		return comms[clientID] != null;
	}
}
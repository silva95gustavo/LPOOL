package lpool.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connector extends Thread {
	private ServerSocket s;
	private final int maxClients;
	private int numClients;
	private Socket[] clients;
	private Communicator[] comms;

	public Connector(int maxClients) {
		this.maxClients = maxClients;
		this.numClients = 0;
		this.clients = new Socket[maxClients];
		this.comms = new Communicator[maxClients];

		try {
			s = new ServerSocket(69);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run()
	{
		try {
			while (true)
			{
				if (!addClient(s.accept()))
					s.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean addClient(Socket client) throws IOException
	{
		if (numClients >= maxClients)
		{
			return false; // Too many clients
		}
		
		for (int i = 0; i < clients.length; i++)
		{
			if (clients[i] == null)
				continue;
			
			if (client.getInetAddress().getHostAddress().equals(clients[i].getInetAddress().getHostAddress()))
				return false; // Client already connected
		}
		
		for (int i = 0; i < clients.length; i++)
		{
			if (clients[i] == null)
			{
				clients[i] = client;
				numClients++;
				comms[i] = new Communicator(client);
				comms[i].start();
				return true; // Success
			}
		}
		return false; // No null position in clients array
	}

	public boolean kickClient(int clientID)
	{
		if (clientID >= maxClients)
			return false;
		
		if (clients[clientID] == null)
			return false;
		
		try {
			clients[clientID].close();
		} catch (IOException e) {
			// Do nothing
		}
		clients[clientID] = null;
		comms[clientID] = null;
		return true;
	}

	public boolean isClientConnected(int clientID)
	{
		return clients[clientID] != null;
	}
}

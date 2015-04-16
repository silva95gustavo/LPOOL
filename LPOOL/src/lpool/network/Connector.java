package lpool.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connector extends Thread {
	private ServerSocket s;
	private final int maxClients;
	private int numClients;
	private Communicator[] comms;

	public Connector(int maxClients) {
		this.maxClients = maxClients;
		this.numClients = 0;
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
		
		for (int i = 0; i < comms.length; i++)
		{
			if (comms[i] == null)
				continue;
			
			if (client.getInetAddress().getHostAddress().equals(comms[i].getSocket().getInetAddress().getHostAddress()))
				return false; // Client already connected
		}
		
		for (int i = 0; i < comms.length; i++)
		{
			if (comms[i] == null)
			{
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
		
		if (comms[clientID] == null)
			return false;
		
		try {
			comms[clientID].getSocket().close();
		} catch (IOException e) {
			// Do nothing
		}
		comms[clientID] = null;
		return true;
	}

	public boolean isClientConnected(int clientID)
	{
		return comms[clientID] != null;
	}
}

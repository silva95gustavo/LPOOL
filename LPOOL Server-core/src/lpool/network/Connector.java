package lpool.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connector extends Thread {
	private ServerSocket serverSocket;
	public ConcurrentLinkedQueue<Socket> clientSockets;

	public Connector(ServerSocket serverSocket, int maxClients, ConcurrentLinkedQueue<Socket> clientSockets) {
		this.clientSockets = clientSockets;
		this.serverSocket = serverSocket;
	}

	@Override
	public void run()
	{
		try {
			while (true)
			{
				Socket clientSocket = serverSocket.accept();
				clientSockets.add(clientSocket);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

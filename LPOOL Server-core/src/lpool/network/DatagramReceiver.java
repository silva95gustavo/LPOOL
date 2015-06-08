package lpool.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Receives data via UDP.
 * @author Gustavo
 *
 */
public class DatagramReceiver extends Thread{
	private DatagramSocket UDPServerSocket;
	private ConcurrentLinkedQueue<DatagramPacket> UDPreceived;

	public DatagramReceiver(DatagramSocket UDPServerSocket, ConcurrentLinkedQueue<DatagramPacket> UDPreceived) {
		this.UDPServerSocket = UDPServerSocket;
		this.UDPreceived = UDPreceived;
	}

	@Override
	public void run()
	{
		while (true)
		{
			byte[] receiveData = new byte[64];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				UDPServerSocket.receive(receivePacket);
				UDPreceived.add(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

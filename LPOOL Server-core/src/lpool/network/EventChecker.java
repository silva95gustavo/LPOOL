package lpool.network;

import java.net.DatagramPacket;

public abstract class EventChecker {
	protected Network network;

	public EventChecker(int maxPlayers)
	{
		this.network = new Network(maxPlayers);
	}

	protected boolean triggerEvents()
	{
		if (network == null)
			return false;

		while (!network.isClientConnQueueEmpty())
		{
			int clientID = network.pollClientConnQueue().intValue();
			conEvent(clientID);
		}

		Integer clientID = new Integer(0);
		String msg;
		while ((msg = network.pollClientCommQueue(clientID)) != null)
		{
			commEvent(clientID, msg);
		}

		// TODO
		return true;
	}

	protected abstract void conEvent(int clientID);
	protected abstract void commEvent(int clientID, String msg);
}

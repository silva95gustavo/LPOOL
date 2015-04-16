package lpool.network;

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

		for (int clientID = 0; clientID < network.maxClients; clientID++)
		{
			while (!network.isClientCommQueueEmpty(clientID))
			{
				String msg = network.pollClientCommQueue(clientID);
				commEvent(clientID, msg);
			}
		}

		// TODO
		return true;
	}

	protected abstract void conEvent(int clientID);
	protected abstract void commEvent(int clientID, String msg);
}

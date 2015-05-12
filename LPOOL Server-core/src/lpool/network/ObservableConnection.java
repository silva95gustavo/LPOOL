package lpool.network;

import java.util.Observable;

public class ObservableConnection extends Observable{
	private Network network;
	
	public ObservableConnection(Network network) {
		this.network = network;
	}
	
	public void tick()
	{
		while (!network.isClientConnQueueEmpty())
		{
			int clientID = network.pollClientConnQueue().intValue();
			setChanged();
			notifyObservers(clientID);
		}
	}
}

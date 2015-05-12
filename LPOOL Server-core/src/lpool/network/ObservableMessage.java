package lpool.network;

import java.util.Observable;

public class ObservableMessage extends Observable {
	private Network network;
	
	public ObservableMessage(Network network) {
		this.network = network;
	}
	
	public void tick()
	{
		Integer clientID = new Integer(0);
		String msg;
		while ((msg = network.pollClientCommQueue(clientID)) != null)
		{
			setChanged();
			notifyObservers(new Message(clientID, msg));
		}
	}
}

package lpool.network;

import java.util.Observable;

public class ObservableMessage extends Observable {
	private Network network;
	
	public ObservableMessage(Network network) {
		this.network = network;
	}
	
	public void tick()
	{
		Message msg;
		while ((msg = network.pollClientCommQueue()) != null)
		{
			setChanged();
			notifyObservers(msg);
		}
	}
}

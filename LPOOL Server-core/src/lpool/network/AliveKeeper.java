package lpool.network;

import com.badlogic.gdx.utils.Timer;

public class AliveKeeper {
	
	public static final float periodicity = 5;
	
	private Network network;
	private Communication comm;
	
	private Timer timer;
	
	public AliveKeeper(Network network, Communication comm) {
		this.network = network;
		this.comm = comm;
		timer = new Timer();
		reset();
	}
	
	public synchronized void reset()
	{
		System.out.println("Reseting heartbeat");
		timer.clear();
		timer.scheduleTask(new Heartbeat(network, comm), periodicity);
	}
}
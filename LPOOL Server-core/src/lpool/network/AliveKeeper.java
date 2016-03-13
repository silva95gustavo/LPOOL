package lpool.network;

import com.badlogic.gdx.utils.Timer;

/**
 * Makes sure the connection is alive by periodically sending heartbeats.
 * @author Gustavo
 *
 * @see Heartbeat
 */
public class AliveKeeper {
	
	public static final float periodicity = 5;
	
	private Network network;
	private Communication comm;
	
	private Timer timer;
	public boolean finished = false;
	
	public AliveKeeper(Network network, Communication comm) {
		this.network = network;
		this.comm = comm;
		timer = new Timer();
		reset();
	}
	
	/**
	 * Resets the timer and reschedules it.
	 */
	public synchronized void reset()
	{
		timer.clear();
		if (this.finished) return; 
		System.out.println("Reseting heartbeat");
		timer.scheduleTask(new Heartbeat(network, comm), periodicity, periodicity);
	}
	
	public void stopMe()
	{
		timer.clear();
		finished = true;
	}
}

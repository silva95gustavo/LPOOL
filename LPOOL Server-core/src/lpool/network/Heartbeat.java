package lpool.network;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import lpool.logic.Game;

import com.badlogic.gdx.utils.Timer;

public class Heartbeat extends Timer.Task implements Observer{

	public static final float timeout = 30;

	private Network network;
	private Communication comm;
	private Timer timer;

	public Heartbeat(Network network, Communication comm) {
		this.network = network;
		this.comm = comm;
		this.timer = new Timer();
	}

	@Override
	public void run() {
		network.addMsgObserver(this);
		System.out.println("Sending PING");
		comm.send(Game.ProtocolCmd.PING + "");
		timer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				// TODO timeout
				System.out.println("TIMEOUT");
				comm.setAlive(false);
			}
		}, timeout);
	}

	@Override
	public void update(Observable o, Object obj) {
		if (o instanceof ObservableMessage)
		{
			Message msg = (Message)obj;
			if (msg.clientID != comm.getClientID())
				return;
			Scanner sc = new Scanner(msg.body);
			sc.close();
			System.out.println("Received response, heartbeat successfull.");
			timer.clear();
			network.deleteMsgObserver(this);
			comm.resetHeartbeat();
		}
	}

}

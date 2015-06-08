package lpool.network;

import java.io.IOException;
import java.net.Socket;

import com.badlogic.gdx.utils.Timer;

public class SocketCloseTask extends Timer.Task {
	private Socket s;
	public SocketCloseTask(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {
		try {
			s.close();
		} catch (IOException e) {
		}
	}
}

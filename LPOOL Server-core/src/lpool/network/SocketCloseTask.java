package lpool.network;

import java.io.IOException;
import java.net.Socket;

import com.badlogic.gdx.utils.Timer;

/**
 * Task that closes a given socket when run.
 * @author Gustavo
 *
 */
public class SocketCloseTask extends Timer.Task {
	private Socket s;
	
	/**
	 * Constructor.
	 * @param s The socket to be closed.
	 */
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

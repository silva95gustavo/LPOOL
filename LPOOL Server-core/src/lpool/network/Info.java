package lpool.network;

import java.io.IOException;
import java.net.Socket;

/**
 * Holds info about the network.
 * @author Gustavo
 *
 */
public class Info {
	private static final String IPCheckURL = "google.pt"; /** The address to connect to in order to check what IP is being used by the machine. **/
	private static final int IPCheckPort = 80; /** Port to be used when checking what IP is being used by the machine. **/
	public static final String androidAppUrl = "http://lpool.pt.vu/"; /** Link to the Android App. **/
	private static String serverIP;
	
	public Info() {
		try {
			// serverIP = InetAddress.getLocalHost().getHostAddress();
			
			Socket s = new Socket(IPCheckURL, IPCheckPort);
		    serverIP = s.getLocalAddress().getHostAddress();
		    s.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * 
	 * @return The IP address of the server.
	 */
	public static String getServerIP()
	{
		return serverIP;
	}
}

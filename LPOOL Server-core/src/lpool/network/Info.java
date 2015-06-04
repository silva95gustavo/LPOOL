package lpool.network;

import java.io.IOException;
import java.net.Socket;

public class Info {
	private static final String IPCheckURL = "fe.up.pt";
	private static final int IPCheckPort = 80;
	public static final String androidAppUrl = "http://lpool.pt.vu/";
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
	
	public static String getServerIP()
	{
		return serverIP;
	}
}

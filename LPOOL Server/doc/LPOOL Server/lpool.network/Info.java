package lpool.network;


/**
 * @author Gustavo
 * @version 1.0
 * @created 15-abr-2015 23:06:06
 */
public class Info {

	private static final String androidAppURL;
	private static final BufferedImage androidAppURLQRCode;
	private Map<id, Socket> clients;
	private static String serverIP;
	private static BufferedImage serverIPQRCode;

	public Info(){

	}

	public void finalize() throws Throwable {

	}

	public static String getAndroidAppURL(){
		return "";
	}

	public static BufferedImage getAndroidAppURLQRCode(){
		return null;
	}

	public static String getServerIP(){
		return "";
	}

	public static BufferedImage getServerIPQRCode(){
		return null;
	}

}
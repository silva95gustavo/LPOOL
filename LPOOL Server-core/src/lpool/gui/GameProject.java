package lpool.gui;

import java.net.Inet4Address;

import lpool.gui.zxing.QRGenerator;
import lpool.network.Info;
import lpool.network.Network;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameProject extends Game {	
	public static final String DATA_DIR = "data/";
	public static final String QR_IP_DIR = DATA_DIR;
	public static final String QR_IP_FILENAME = "ip_qrcode.png";
	public static final String QR_IP_TYPE = "png";
	public static final String QR_ANDROID_APP_DIR = DATA_DIR;
	public static final String QR_ANDROID_APP_FILENAME = "android_app_qrcode.png";
	public static final String QR_ANDROID_APP_TYPE = "png";
	public static final float blackgroundColorPeriod = 2;
	
	SpriteBatch batch;
	Texture img;
	int width;
	int height;

	@Override
	public void create () {

		String ip_address = "";

		try {
			ip_address = Inet4Address.getLocalHost().getHostAddress();
		} catch(Exception e) {
			System.out.println("Unable to get IP address");
		}

		QRGenerator.generateFromStringToFile(ip_address + "\n" + Network.port, QR_IP_DIR, QR_IP_FILENAME, QR_IP_TYPE);
		QRGenerator.generateFromStringToFile(Info.androidAppUrl, QR_ANDROID_APP_DIR, QR_ANDROID_APP_FILENAME, QR_ANDROID_APP_TYPE);

		width = Gdx.graphics.getDesktopDisplayMode().width;
		height = Gdx.graphics.getDesktopDisplayMode().height;
		Gdx.graphics.setDisplayMode(width, height, false);
		
		batch = new SpriteBatch();
		
		setScreen(new LoadingScene(this));
	}
}

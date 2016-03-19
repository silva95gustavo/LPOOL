package lpool.gui;

import lpool.gui.zxing.QRGenerator;
import lpool.network.Info;

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

		new Info();

		QRGenerator.generateFromStringToFile(Info.getServerIP() + "\n" + Info.getServerPort(), QR_IP_DIR, QR_IP_FILENAME, QR_IP_TYPE);
		QRGenerator.generateFromStringToFile(Info.androidAppUrl, QR_ANDROID_APP_DIR, QR_ANDROID_APP_FILENAME, QR_ANDROID_APP_TYPE);

		width = Gdx.graphics.getDesktopDisplayMode().width;
		height = Gdx.graphics.getDesktopDisplayMode().height;
		Gdx.graphics.setDisplayMode(width, height, true);
		
		batch = new SpriteBatch();
		
		setScreen(new LoadingScene(this));
	}
}

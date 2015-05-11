package lpool.gdx;

import java.net.Inet4Address;

import lpool.zxing.QRGenerator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameProject extends Game {	
	
	public static final String QR_IP_DIR = "./data/ip_qrcode.png";
	public static final String QR_IP_TYPE = "png";
	
	SpriteBatch batch;
	Texture img;
	int width;
	int height;

	@Override
	public void create () {

		String ip_address = "";
		String ip_qr_path = "";

		try {
			ip_address = Inet4Address.getLocalHost().getHostAddress();
		} catch(Exception e) {
			System.out.println("Unable to get IP address");
		}

		if(QRGenerator.generateFromStringToFile(ip_address, QR_IP_DIR, QR_IP_TYPE)) {
			ip_qr_path = QR_IP_DIR;
		}

		width = Gdx.graphics.getDesktopDisplayMode().width;
		height = Gdx.graphics.getDesktopDisplayMode().height;
		Gdx.graphics.setDisplayMode(width, height, false);
		
		batch = new SpriteBatch();
		
		setScreen(new MatchScene(new lpool.logic.Game(), width, height, ip_qr_path));
	}
}

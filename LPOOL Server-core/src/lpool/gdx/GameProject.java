package lpool.gdx;

import java.awt.image.BufferedImage;
import java.net.Inet4Address;

import lpool.zxing.QRGenerator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameProject extends Game {	
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
		
		BufferedImage qr_code = null;
		
		if(ip_address != "")
			qr_code = QRGenerator.generateFromString("");
		
		width = Gdx.graphics.getDesktopDisplayMode().width;
		height = Gdx.graphics.getDesktopDisplayMode().height;
		//Gdx.graphics.setDisplayMode(width, height, false);
		
		batch = new SpriteBatch();
		
		if(qr_code == null)
			System.out.println("Unable to generate QR code");
		else
		{
			
		}
		
		
		setScreen(new MatchScene(width, height));
	}
}

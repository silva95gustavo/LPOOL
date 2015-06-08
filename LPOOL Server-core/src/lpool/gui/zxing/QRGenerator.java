package lpool.gui.zxing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.badlogic.gdx.Gdx;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRGenerator {

	public static boolean generateFromStringToFile(String text, String folder, String filename, String filetype) {
		
		int size = 125;
		try {
			Hashtable<EncodeHintType, Object> hintMap = new Hashtable<EncodeHintType, Object>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(text,BarcodeFormat.QR_CODE, size, size, hintMap);
			int imageWidth = byteMatrix.getWidth();
			BufferedImage image = new BufferedImage(imageWidth, imageWidth,
					BufferedImage.TYPE_INT_RGB);
			image.createGraphics();

			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, imageWidth, imageWidth);
			graphics.setColor(Color.BLACK);

			for (int i = 0; i < imageWidth; i++) {
				for (int j = 0; j < imageWidth; j++) {
					if (byteMatrix.get(i, j)) {
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}
			
			try {
				new File(folder).mkdirs();
				File destFile = new File(Gdx.files.getLocalStoragePath() + folder + filename);

				ImageIO.write(image, filetype, destFile);
			} catch (Exception e) {
				System.err.println("Unable to save generated QR code to specified file/filetype.");
				e.printStackTrace();
				return false;
			}
				
		} catch (WriterException e) {
			System.err.println("Unable to generate QR code.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}  

}

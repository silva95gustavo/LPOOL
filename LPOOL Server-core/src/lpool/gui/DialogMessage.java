package lpool.gui;

import lpool.gui.assets.Fonts;
import lpool.gui.assets.Textures;
import lpool.network.Info;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;

public class DialogMessage {
	public static final float animTime = 0.75f;
	public static final float brightness = -0.5f;
	public static final float widthPct = 0.6f;
	public static final float heightPct = 0.2f;
	private ShaderBatch batch;
	private String title;
	private String message;
	private float duration;
	private float time;
	private float backgroundBrightness;
	private float backgroundContrast;
	private float worldX;
	private float worldY;
	private float worldWidth;
	private float worldHeight;
	private float messageX;
	private float messageY;
	private float width;
	private float height;
	public DialogMessage(ShaderBatch batch, String title, String message, float duration, float worldX, float worldY, float worldWidth, float worldHeight) {
		this.batch = batch;
		this.title = title;
		this.message = message;
		this.duration = duration;
		this.time = 0;
		this.backgroundBrightness = 0;
		this.backgroundContrast = 1;
		this.worldX = worldX;
		this.worldY = worldY;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		this.width = widthPct * worldWidth;
		this.height = heightPct * worldHeight;
		this.messageY = worldY + (worldHeight - height) / 2;
	}

	public void renderDialog()
	{
		batch.brightness = 0;
		batch.contrast = 1;
		Texture db = Textures.getInstance().getDialogBox();
		batch.begin();
		batch.draw(db, messageX, messageY, width, height);
		
		BitmapFont title = Fonts.getInstance().getArial100();
		title.setColor(Color.WHITE);
		title.setScale(0.015f);
		title.setUseIntegerPositions(false);
		title.draw(batch, this.title, messageX + width * 0.05f, messageY + height * 0.8f);
		
		BitmapFont message = Fonts.getInstance().getArial100();
		message.setColor(Color.WHITE);
		message.setScale(0.005f);
		message.setUseIntegerPositions(false);
		message.draw(batch, this.message, messageX + width * 0.05f, messageY + height * 0.35f);
		
		batch.end();
	}
	
	public float getBackgroundBrightness()
	{
		return backgroundBrightness;
	}
	
	public float getBackgroundContrast()
	{
		return backgroundContrast;
	}
	
	/**
	 * 
	 * @param delta time passed this frame
	 * @return false if the animation has ended, true otherwise
	 */
	public boolean update(float delta)
	{
		time += delta;
		if (time < animTime)
		{
			float a = time / animTime;
			backgroundBrightness = Interpolation.linear.apply(0, brightness, a);
			messageX = Interpolation.swingIn.apply(worldX + worldWidth, worldX + (worldWidth - width) / 2, a);
		}
		else if (time <= animTime + duration)
		{
			backgroundBrightness = brightness;
		}
		else if (time < 2 * animTime + duration)
		{
			float a = (time - (animTime + duration)) / animTime;
			backgroundBrightness = Interpolation.linear.apply(brightness, 0, a);
			messageX = Interpolation.swingOut.apply(worldX + (worldWidth - width) / 2, worldX - width, a);
		}
		else backgroundBrightness = 0;
		backgroundContrast = backgroundBrightness + 1;
		if (time <= 2 * animTime + duration)
			return true;
		else return false;
	}
}

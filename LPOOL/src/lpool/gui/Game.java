package lpool.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jbox2d.common.Vec2;

import lpool.logic.Ball;
import lpool.logic.Border;
import lpool.logic.Match;

@SuppressWarnings("serial")
public class Game extends JPanel{
	private int FPS = 60;
	private float deltaT = (float)1/FPS;
	private Match match = new Match();
	private BufferedImage table;

	public Game(JFrame parent)
	{
		ActionListener myTimerListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Ball[] balls1 = match.getBalls1();
				Ball[] balls2 = match.getBalls2();
				match.tick(deltaT);
				repaint();
			}
		};

		try {
			table = ImageIO.read(new File("res/table.png"));
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		
		Timer myTimer = new Timer((int)(deltaT * 1000), myTimerListener);
		myTimer.start();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		// Draw background
		 g.drawImage(table, 0, 0, this.getWidth(), this.getHeight(), null);
		
		Ball[] balls1 = match.getBalls1();
		Ball[] balls2 = match.getBalls2();
		for (int i = 0; i < match.ballsPerPlayer; i++)
		{
			drawBall(g, balls1[i], Color.RED);
			drawBall(g, balls2[i], Color.BLUE);
		}
		Ball blackBall = match.getBlackBall();
		drawBall(g, blackBall, Color.BLACK);
	}
	
	private Vec2 physicsToPixel(Vec2 v)
	{
		Vec2 v2 = new Vec2();
		v2.x = v.x * this.getWidth() / Border.width;
		v2.y = v.y * this.getHeight() / Border.height;
		return v2;
	}

	private void drawBall(Graphics g, Ball ball, Color c) {
		Vec2 ballPosPixel = physicsToPixel(ball.getPosition());
		Vec2 ballRadiusPixel = physicsToPixel(new Vec2(Ball.radius, Ball.radius));
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(c);
		g2d.fillOval((int)(ballPosPixel.x - ballRadiusPixel.x), (int)(ballPosPixel.y - ballRadiusPixel.y), (int)(2 * ballRadiusPixel.x), (int)(2 * ballRadiusPixel.y));
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("LPOOL");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		f.setPreferredSize(new Dimension(500,500));
		JPanel panel = new Game(f);
		f.getContentPane().add(panel);
		f.setSize(800, 400);
		f.setVisible(true);
		panel.requestFocus();      
	}
}

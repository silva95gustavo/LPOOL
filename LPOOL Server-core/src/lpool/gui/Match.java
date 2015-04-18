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

import com.badlogic.gdx.math.Vector2;

import lpool.logic.Ball;
import lpool.logic.Border;
import lpool.network.Connector;

@SuppressWarnings("serial")
public class Match extends JPanel{
	private int FPS = 60;
	private float deltaT = (float)1/FPS;
	private BufferedImage table;
	private lpool.logic.Game game = new lpool.logic.Game();

	public Match(JFrame parent)
	{
		ActionListener myTimerListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				game.tick(deltaT);
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
		
		lpool.logic.Match m = game.getMatch();
		 
		Ball[] balls1 = m.getBalls1();
		Ball[] balls2 = m.getBalls2();
		for (int i = 0; i < m.ballsPerPlayer; i++)
		{
			drawBall(g, balls1[i], Color.RED);
			drawBall(g, balls2[i], Color.BLUE);
		}
		Ball blackBall = m.getBlackBall();
		drawBall(g, blackBall, Color.BLACK);
		Ball cueBall = m.getCueBall();
		drawBall(g, cueBall, Color.WHITE);
		
		float cueAngle = m.getCueAngle();
		Vector2 cueBallPos = physicsToPixel(cueBall.getPosition());
		Vector2 cue = new Vector2(1000, 0).rotate(cueAngle * 180f / (float)Math.PI).add(cueBallPos);
		g.setColor(Color.WHITE);
		g.drawLine((int)cueBallPos.x, (int)cueBallPos.y, (int)cue.x, (int)cue.y);
	}
	
	private Vector2 physicsToPixel(Vector2 v)
	{
		Vector2 v2 = new Vector2();
		v2.x = v.x * this.getWidth() / Border.width;
		v2.y = v.y * this.getHeight() / Border.height;
		return v2;
	}

	private void drawBall(Graphics g, Ball ball, Color c) {
		Vector2 ballPosPixel = physicsToPixel(ball.getPosition());
		Vector2 ballRadiusPixel = physicsToPixel(new Vector2(Ball.radius, Ball.radius));
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(c);
		g2d.fillOval((int)(ballPosPixel.x - ballRadiusPixel.x), (int)(ballPosPixel.y - ballRadiusPixel.y), (int)(2 * ballRadiusPixel.x), (int)(2 * ballRadiusPixel.y));
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("LPOOL");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		f.setPreferredSize(new Dimension(500,500));
		JPanel panel = new Match(f);
		f.getContentPane().add(panel);
		f.setSize(800, 400);
		f.setVisible(true);
		panel.requestFocus();
	}
}

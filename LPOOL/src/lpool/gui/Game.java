package lpool.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import lpool.logic.Ball;
import lpool.logic.Match;

@SuppressWarnings("serial")
public class Game extends JPanel{
	private int FPS = 60;
	private float deltaT = (float)1/FPS;
	Match match = new Match();

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

		Timer myTimer = new Timer((int)(deltaT * 1000), myTimerListener);
		myTimer.start();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
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

	private void drawBall(Graphics g, Ball ball, Color c) {
		Graphics2D g2d = (Graphics2D)g;

		g2d.setRenderingHint (RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setPaint(c);
		g2d.fillOval((int)ball.getPosition().x, (int)ball.getPosition().y, 20, 20);
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("LPOOL");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		f.setPreferredSize(new Dimension(500,500));
		JPanel panel = new Game(f);
		f.getContentPane().add(panel);
		f.pack();
		f.setVisible(true);
		panel.requestFocus();      
	}
}

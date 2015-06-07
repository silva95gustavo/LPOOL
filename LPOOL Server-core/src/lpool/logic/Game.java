package lpool.logic;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import lpool.logic.match.Match;
import lpool.network.Message;
import lpool.network.Network;
import lpool.network.ObservableConnection;
import lpool.network.ObservableMessage;

public class Game implements Observer {
	public static final int numPlayers = 2;
	public static final int maxNameLength = 16;
	private Network network;
	private Match match;
	private String playerNames[];

	public enum ProtocolCmd {
		ANGLE, // angle
		FIRE, // force[0, 1] x-spin[-1, 1] y-spin[-1, 1]
		PING,
		PONG,
		JOIN, // name
		QUIT,
		KICK,
		MOVECB, // x-pos[0, 1] y-pos[0, 1]
		PLACECB, // x-pos[0, 1] y-pos[0, 1]
		PLAY,
		WAIT,
		BIH,
		END // winner(boolean) End.Reason
	};

	public Game() {
		network = new Network(2);
		network.startConnecting();
		network.addConnObserver(this);
		network.addMsgObserver(this);
		playerNames = new String[numPlayers];
		
		for(int i = 0; i < playerNames.length; i++)
			playerNames[i] = defaultPlayerName(i);
	}

	public void tick(float dt)
	{
		if (match != null)
			match.tick(dt);
		
		network.tick();
	}

	public Match getMatch()
	{
		return match;
	}

	protected void conEvent(int clientID) {
		if (network.isClientConnected(clientID))
			System.out.println("Client #" + clientID + " connected!");
		else
		{
			playerNames[clientID] = defaultPlayerName(clientID);
			System.out.println("Client #" + clientID + " disconnected!");
		}
	}
	
	protected void commEvent(Message msg)
	{
		System.out.println("message: " + msg.body);
		if (match != null) return; // Ignore if the match has already started
		Scanner sc = new Scanner(msg.body);
		ProtocolCmd cmd = Message.readCmd(sc);
		if (cmd == null) return;
		switch (cmd)
		{
		case JOIN:
		{
			playerNames[msg.clientID] = defaultPlayerName(msg.clientID);
			if (!sc.hasNextLine()) break;
			String name = sc.nextLine();
			name = name.trim();
			if (name.length() > maxNameLength) {
			    name = name.substring(0, maxNameLength);
			}
			playerNames[msg.clientID] = name;
		}
		default:
			break;
		}
		sc.close();
	}
	
	public String defaultPlayerName(int clientID)
	{
		return new String("Player " + (clientID + 1));
	}
	
	public Network getNetwork()
	{
		return network;
	}

	@Override
	public void update(Observable o, Object obj) {
		if (o instanceof ObservableConnection && obj instanceof Integer)
			conEvent((Integer)obj);
		else if (o instanceof ObservableMessage && obj instanceof Message)
			commEvent((Message)obj);
	}
	
	public void startMatch()
	{
		match = new Match(network, playerNames);
	}
	
	public void endMatch()
	{
		match = null;
	}
}

package lpool.logic;

import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

import lpool.logger.Logger;
import lpool.logic.match.End;
import lpool.logic.match.Match;
import lpool.network.Message;
import lpool.network.Network;
import lpool.network.ObservableConnection;
import lpool.network.ObservableMessage;

public class Game implements Observer {
	public static final int numPlayers = Match.numPlayers;
	public static final int maxNameLength = 16;
	private Network network;
	private Match match;
	private String playerNames[];
	private Logger logger;

	/**
	 * Network communication protocol.
	 * Messages should begin with a number corresponding to the command position in this enum. Arguments should be separated by spaces and the message must end with a newline char.
	 * @author Gustavo
	 *
	 */
	public enum ProtocolCmd {
		/**
		 * Sent by the client during a play to inform where the player wants to aim the cue.
		 * Arguments: angle(float).
		 */
		ANGLE,
		
		/**
		 * 
		 * Arguments: force(float(0 to 1)), x-spin(float(-1 to 1)), y-spin(float(-1 to 1)).
		 */
		FIRE,
		
		/**
		 * Echo request message.
		 */
		PING,
		
		/**
		 * {@link ProtocolCmd#PING} response.
		 */
		PONG,
		
		/**
		 * Must be sent by the client in order to join the server.
		 * Arguments: name(String).
		 */
		JOIN, // name
		
		/**
		 * Sent by the client when he wants to leave the game.
		 */
		QUIT,
		
		/**
		 * Sent by the server to inform a client he has been kicked.
		 */
		KICK,
		
		/**
		 * Sent by the client to move the cue ball when it is being repositioned.
		 * Arguments: x-pos(float(0 to 1)), y-pos(float(0 to 1)).
		 */
		MOVECB, // x-pos[0, 1] y-pos[0, 1]
		
		/**
		 * Sent by the client to place the cue ball when it is being repositioned.
		 * Arguments: x-pos(float(0 to 1)), y-pos(float(0 to 1)).
		 */
		PLACECB, // x-pos[0, 1] y-pos[0, 1]
		
		/**
		 * Sent by the server to notify the player that it's his turn to make a shot.
		 */
		PLAY,
		
		/**
		 * Sent by the server to inform the player he has to wait for his turn.
		 */
		WAIT,
		
		/**
		 * Sent by the server to inform the player he has to reposition the cue ball because the other player committed a foul.
		 */
		BIH,
		
		/**
		 * Sent by the server to inform a client that the match has ended.
		 * Arguments: winner(boolean), reason({@link lpool.logic.match.End.Reason}).
		 */
		END
	};

	/**
	 * Constructor.
	 */
	public Game() {
		network = new Network(2);
		network.startConnecting();
		network.addConnObserver(this);
		network.addMsgObserver(this);
		playerNames = new String[numPlayers];
		
		for(int i = 0; i < playerNames.length; i++)
			playerNames[i] = defaultPlayerName(i);
		
		logger = new Logger();
		logger.print();
	}

	/**
	 * Updates the match and the network
	 * @param dt Time passed since last update.
	 */
	public void tick(float dt)
	{
		if (match != null)
			match.tick(dt);
		
		network.tick();
	}

	/**
	 * 
	 * @return The match or null if no match is being played.
	 */
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
	
	/**
	 * 
	 * @param clientID The ID of the client whose default name to obtain.
	 * @return A string containing the default player name if none has been specified by the client.
	 */
	public static String defaultPlayerName(int clientID)
	{
		return new String("Player " + (clientID + 1));
	}
	
	/**
	 * 
	 * @return The {@link Network} this game is associated to.
	 */
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
	
	/**
	 * Start the match. This action will be logged.
	 */
	public void startMatch()
	{
		String names = "";
		for(int i = 0; i < playerNames.length; i++) {
			names += playerNames[i];
			if(i < playerNames.length - 1) {
				names += ", ";
			}
		}
		logger.log("Game started with players " + names);
		match = new Match(network, playerNames);
	}
	
	/**
	 * End the match. This action will be logged.
	 */
	public void endMatch()
	{
		if((match != null) && (match.getStateMachine().getCurrentState() instanceof End)) {
				int index = ((End) match.getStateMachine().getCurrentState()).getWinner();
				logger.log("Game ended, won by " + playerNames[index]);
		}
		else 
			logger.log("Game ended");
		
		match = null;
	}
	
	public String getPlayerName(int playerID)
	{
		if (playerID >= 0 && playerID < playerNames.length)
			return playerNames[playerID];
		else
			return null;
	}
}

package lpool.network;

public abstract class EventChecker {
	private Connector con;
	
	public EventChecker(int maxPlayers)
	{
		this.con = new Connector(maxPlayers);
	}
	
	protected void startConnector(int maxPlayers)
	{
		con.start();
	}
	
	protected boolean triggerEvents()
	{
		if (con == null)
			return false;
		
		// TODO
		return true;
	}
	
	protected abstract void conEvent(int clientID);
	protected abstract void commEvent(int clientID);
}

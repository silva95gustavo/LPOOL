package lpool.logic;

public class BodyInfo {

	/**
	 * Body type identifier to be used with Box2D.
	 * @author Gustavo
	 *
	 */
	public enum Type
	{
		BALL,
		BALL_SENSOR,
		TABLE,
		HOLE
	};
	
	private Type type;
	private int ID;
	
	/**
	 * Constructor.
	 * @param type
	 * @param ID
	 */
	public BodyInfo(Type type, int ID)
	{
		this.type = type;
		this.ID = ID;
	}

	/**
	 * 
	 * @return The body type.
	 */
	public Type getType() {
		return type;
	}

	/**
	 * 
	 * @return The ball number, the hole number (left to right, top to bottom order) or 0 if the type is {@link Type#TABLE}.
	 */
	public int getID() {
		return ID;
	}
	
	@Override
	public String toString() {
		return "BodyInfo [type=" + type + ", ID=" + ID + "]";
	}
}

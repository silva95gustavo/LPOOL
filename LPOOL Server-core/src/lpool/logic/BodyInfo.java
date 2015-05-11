package lpool.logic;

public class BodyInfo {

	public enum Type
	{
		BALL,
		BALL_SENSOR,
		TABLE,
		HOLE
	};
	
	private Type type;
	private int ID;
	
	public BodyInfo(Type type, int ID)
	{
		this.type = type;
		this.ID = ID;
	}

	public Type getType() {
		return type;
	}

	public int getID() {
		return ID;
	}
	
	@Override
	public String toString() {
		return "BodyInfo [type=" + type + ", ID=" + ID + "]";
	}
}

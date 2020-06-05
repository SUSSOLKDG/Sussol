package model.statistics;

public class Neighbor implements Comparable<Neighbor>
{
	private String name;
	private int neighborCounter;
	
	public Neighbor(String name, int neighborCounter) 
	{
		this.name = name;
		this.neighborCounter = neighborCounter;
	}

	public String getName() 							{ return name; }
	public void setName(String name)					{ this.name = name; }
	public int getNeighborCounter() 					{ return neighborCounter; }
	public void setNeighborCounter(int neighborCounter)	{ this.neighborCounter = neighborCounter; }

	@Override
	public int compareTo(Neighbor otherNeighbor) 
	{
		// Descending
		return -Integer.compare(this.getNeighborCounter(), otherNeighbor.getNeighborCounter());
	}
	
}

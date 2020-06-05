package model.clustering;

public class DistanceClusterPair implements Comparable<DistanceClusterPair>
{
	private int cluster1Number;
	private int cluster2Number;
	private Double distance;
	
	public DistanceClusterPair(int cluster1Number, int cluster2Number, Double distance) 
	{
		this.cluster1Number = cluster1Number;
		this.cluster2Number = cluster2Number;
		this.distance = distance;
	}
	
	@Override
	public int compareTo(DistanceClusterPair otherDistanceClusterPair) 
	{
		int cluster1NumberComparison = Integer.compare(this.getCluster1Number(), otherDistanceClusterPair.getCluster1Number());
		if (cluster1NumberComparison != 0)
            return cluster1NumberComparison;
				
		int distanceComparison = Double.compare(this.getDistance(), otherDistanceClusterPair.getDistance());
		if (distanceComparison != 0)
            return distanceComparison;
		
		return 0;
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + cluster1Number;
		result = prime * result + cluster2Number;
		result = prime * result + ((distance == null) ? 0 : distance.hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)							return true;
		if (obj == null)							return false;
		if (getClass() != obj.getClass())			return false;
		
		DistanceClusterPair other = (DistanceClusterPair) obj;
		
		// Heavy stuff.
		// Distance(0, 1) == Distance(1, 0)
		
		// Original code:
		//
		// if (cluster1Number != other.cluster1Number)			return false;
		// if (cluster2Number != other.cluster2Number)			return false;
		
		// Adapted code :
		if (cluster1Number != other.cluster2Number)			return false;
		if (cluster2Number != other.cluster1Number)			return false;

		if (distance == null) 
		{
			if (other.distance != null)				return false;
		} 
		else if (!distance.equals(other.distance))	return false;
		
		return true;
	}

	@Override
	public String toString() 
	{
		return "DistanceClusterPair [cluster1Number=" + cluster1Number + ", cluster2Number=" + cluster2Number + ", distance=" + distance + "]";
	}

	public int getCluster1Number()	{ return cluster1Number; 	}
	public int getCluster2Number()	{ return cluster2Number; 	}
	public Double getDistance() { return distance; 		}
	
}

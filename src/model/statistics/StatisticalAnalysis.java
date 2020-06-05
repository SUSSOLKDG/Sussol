package model.statistics;

public class StatisticalAnalysis 
{
	private double[][] neighborhoods;
	double lowerLimit;
	double upperLimit;
	double neighbors;
	double neighborsPercentage;
	double notNeighbors;
	double notNeighborsPercentage;
	double notSignificants;
	double notSignificantsPercentage;
	
	public StatisticalAnalysis(double[][] finalNeighborhoods, double lowerLimit, double upperLimit) 
	{
		neighborhoods = new double[finalNeighborhoods.length][finalNeighborhoods.length];
	
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		
		for (int i=0; i < finalNeighborhoods.length; i++)
			for (int j=0; j < finalNeighborhoods.length; j++)
			{
				if (finalNeighborhoods[i][j] > lowerLimit && finalNeighborhoods[i][j] < upperLimit)
				{
					neighborhoods[i][j] = -1.0;
					notSignificants++;
				}
				else if (finalNeighborhoods[i][j] < 50)	// TODO : vanwaar die 50 ? Niet zo belangrijk ?
				{
					neighborhoods[i][j] = 100;
					neighbors++;
				}
				else
				{
					neighborhoods[i][j] = 0;
					notNeighbors++;
				}
			}
		
		double totalEntries = neighbors + notNeighbors + notSignificants;
		neighborsPercentage = neighbors / totalEntries * 100;
		notNeighborsPercentage = notNeighbors / totalEntries * 100;
		notSignificantsPercentage = notSignificants / totalEntries * 100;
		
	}

	public double[][] getNeighborhoods()			{ return neighborhoods; }
	public double getLowerLimit() 					{ return lowerLimit; }
	public double getUpperLimit() 					{ return upperLimit; }
	public double getNeighbors() 					{ return neighbors; }
	public double getNeighborsPercentage() 			{ return neighborsPercentage; }
	public double getNotNeighbors() 				{ return notNeighbors; }
	public double getNotNeighborsPercentage() 		{ return notNeighborsPercentage; }
	public double getNotSignificants() 				{ return notSignificants; }
	public double getNotSignificantsPercentage()	{ return notSignificantsPercentage; }
	
}

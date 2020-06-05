package presenter.filtering.post;

import java.util.ArrayList;
import java.util.HashMap;

import general.GeneralData;
import general.GeneralData.DistanceData;
import javafx.scene.chart.XYChart.Series;
import mdsj.MDSJ;
import model.solvents.Solvent;
import utilities.MathManager;

public class AllSolventsPresenter 
{
	public ArrayList<Solvent> initialiseSolventsChartdata(DistanceData distanceType, ArrayList<Solvent> solvents)
	{
		ArrayList<Solvent> result = new ArrayList<Solvent>();
		
		// Input distance matrix for MDS.
		double[][] input = new double[solvents.size()][solvents.size()]; 
	
		int i, j;
		switch (distanceType)
		{
			case ALL_FEATURES:
				
				i = 0;
				for (Solvent solvent1 : solvents)
				{
					double[] solvent1Values = solvent1.getFeatureValuesAsdouble();
					
					j = 0;
					for (Solvent solvent2 : solvents)
					{
						double[] solvent2Values = solvent2.getFeatureValuesAsdouble();
						input[i][j++] = MathManager.getEuclideanDistance(solvent1Values, solvent2Values);
					}
					i++;
				}
				break;
			case HANSEN:
				
				i = 0;
				for (Solvent solvent1 : solvents)
				{
					double[] solvent1Values = solvent1.getHansenData().getHansenDataAsArray();
					
					j = 0;
					for (Solvent solvent2 : solvents)
					{
						double[] solvent2Values = solvent2.getHansenData().getHansenDataAsArray();
						input[i][j++] = MathManager.getEuclideanDistance(solvent1Values, solvent2Values);
					}
					i++;
				}
				break;
		}			
		
		// Apply MDS.
		double[][] output = MDSJ.classicalScaling(input); 
		
		// Fill series.
		i = 0;
		for (Solvent solvent : solvents)
		{  
		    // x and y : MDS using the vectors of the solvents.
		    int x = (int) output[0][i];
		    if (distanceType == DistanceData.HANSEN) x *= 100;		// * 100 for Hansen distances.
		    
		    int y = (int) output[1][i];
		    if (distanceType == DistanceData.HANSEN) y *= 100;		// * 100 for Hansen distances.
		    
		    int radius = 50;
		    
		    Solvent newSolvent = new Solvent(solvent);
		    newSolvent.initialiseChartData(x, y, radius, i++);
		    
		    result.add(newSolvent);
		}
		
		return result;
	}
	
	public Series<Integer, Integer> getSolventsChartData(DistanceData dataType, ArrayList<Solvent> solvents)
	{
		Series<Integer, Integer> chartData = new Series<Integer, Integer>();
		
		ArrayList<Solvent> newSolvents = initialiseSolventsChartdata(dataType, solvents);
		
		// Run through solvents and fill Series with chart data.
		for (Solvent solvent : newSolvents)
			chartData.getData().add(solvent.getChartData());
		
		return chartData;
	}

	public Solvent getSolvent(int bubbleNumber) 
	{
		Solvent result = new Solvent(-1);
		
		for (HashMap.Entry<String, Solvent> solvent : GeneralData.solvents.entrySet())
			if (solvent.getValue().getBubbleNumber() == bubbleNumber)
			{
				result = solvent.getValue();
				break;
			}
		
		return result;
	}
	
	public Solvent getSolvent(ArrayList<Solvent> solvents, int bubbleNumber) 
	{
		Solvent result = new Solvent(-1);
		
		for (Solvent solvent : solvents)
			if (solvent.getBubbleNumber() == bubbleNumber)
			{
				result = solvent;
				break;
			}
		
		return result;
	}
}

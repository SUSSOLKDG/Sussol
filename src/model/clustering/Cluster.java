package model.clustering;

import java.awt.Point;
import java.util.ArrayList;

import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import mdsj.MDSJ;
import model.features.Feature;
import model.solvents.Solvent;
import utilities.MathManager;

public class Cluster implements Comparable<Cluster>
{
	private int number;
	
	private int xCoordinate;
	private int yCoordinate;
	private Data<Integer, Integer> chartData;
	
	private ArrayList<Solvent> solvents;
	
	private ArrayList<Double> centroidFeatureValues;
		
	private Double quantizationError;
	
	public Cluster(int number, Point pointOnMap)
	{
		this.number = number;
		this.xCoordinate = (int) pointOnMap.getX();
		this.yCoordinate = (int) pointOnMap.getY();
		
		solvents = new ArrayList<Solvent>();
		
		centroidFeatureValues =  new ArrayList<Double>();
		quantizationError = new Double(0.0);
	}
	
	public void addSolvent(Solvent solvent)
	{
		solvents.add(solvent);
	}
	
	public Solvent getSolvent(int solventBubbleNumber)
	{
		for (Solvent solvent : solvents)
		{
			if (solvent.getBubbleNumber() == solventBubbleNumber)
				return solvent;
		}
		return null;
	}
	
	public int getNumberOfSolvents()
	{
		return solvents.size();
	}
	
	public void initialiseSolventChartdataMDS()
	{
		// Input distance matrix for MDS.
		int numberOfSolvents = solvents.size();
		double[][] input = new double[numberOfSolvents][numberOfSolvents]; 
	
		/* Distances based on the Hansen distances.
		for (int i = 0; i < dimension; i++)
		{
			// Get the solvent feature values.
			double[] solvent1 = solvents.get(i).getHansenData().getHansenDataAsArray();
			
			// Calculate distances.
			for (int j = 0; j < dimension; j++)
			{
				double[] solvent2 = solvents.get(j).getHansenData().getHansenDataAsArray();
				input[i][j] = MathManager.getEuclideanDistance(solvent1, solvent2);
			}
		}
		*/
		
		// Distances based on the whole featureset.
		for (int i = 0; i < numberOfSolvents; i++)
		{
			// Get the solvent feature values.
			double[] solvent1 = solvents.get(i).getFeatureValuesAsdouble();
			
			// Calculate distances.
			for (int j = 0; j < numberOfSolvents; j++)
			{
				double[] solvent2 = solvents.get(j).getFeatureValuesAsdouble();
				input[i][j] = MathManager.getEuclideanDistance(solvent1, solvent2);
			}
		}
		
		// Apply MDS.
		double[][] output = MDSJ.classicalScaling(input); 
		
		// Fill series.
		for (int i = 0; i < numberOfSolvents; i++) 
		{  
		    // x and y : MDS using the feature vectors of the solvents.
		    int x = (int) output[0][i]; // * 100;	// * 100 for Hansen distances.
		    int y = (int) output[1][i]; // * 100;
		    int radius = 50;
		    
		    solvents.get(i).initialiseChartData(x, y, radius, i);
		}
	}
	
	public void initialiseChartData(int x, int y, int radius) 
	{				
		chartData = new Data<Integer, Integer>(x, y, radius);		
	}
	
	public Series<Integer, Integer> getSolventChartData()
	{
		Series<Integer, Integer> chartData = new Series<Integer, Integer>();
		
		// Run through solvents and fill Series with chart data.
		for (Solvent solvent : solvents)
		{
			chartData.getData().add(solvent.getChartData());
		}
		
		return chartData;
	}
	
	// Returns the list of solvents to be shown.
	public ArrayList<Integer> filterSolventChartData(String featureName, double min, double max)
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		// Run through solvents and fill result with solvent numbers to be shown.
		for (Solvent solvent : solvents)
		{
			for (Feature feature : solvent.getFeatures())
			{
				if (feature.getName().equalsIgnoreCase(featureName)	&&
					feature.getValue() >= min 						&&
					feature.getValue() <= max)
				{
					result.add(solvent.getBubbleNumber());	
				}
			}
		}
		
		return result;
	}
	
	// Returns the ArrayList of average features of this cluster.
	public ArrayList<Feature> getCentroidFeatures()
	{
		ArrayList<Feature> result = new ArrayList<Feature>();		
		
		// Initialise the list of Features in function of the number of features.
		for (Feature feature : solvents.get(0).getFeatures())
		{
			result.add(new Feature(feature.getName(), 0.0));
		}
		
		// Run through solvents and sum for each feature the value.
		for (Solvent solvent : solvents)
		{
			int counter = 0;
			for (Feature feature : solvent.getFeatures())
			{
				result.get(counter).setValue(result.get(counter).getValue() + feature.getValue());
				counter++;
			}			
		}
		
		// Get average.
		for (Feature feature : result)
		{
			Double average = feature.getValue() / solvents.size();
			feature.setValue(average);
			centroidFeatureValues.add(average);
		}			
		
		return result;		
	}
	
	public double getStandardDeviationAllFeaturesDistances()
	{
		return MathManager.getStandardDeviation(MathManager.getAllFeaturesDistances(solvents));
	}
	
	public double getStandardDeviationHansenDistances()
	{
		return MathManager.getStandardDeviation(MathManager.getHansenDistances(solvents));
	}
	
	public double[] getCentroidValues()
	{
		// Compute the cluster centroids.
		// TODO : is dit al gebeurd, dus overbodig voor het opvragen van de chart data ?
		getCentroidFeatures();
		
		return centroidFeatureValues.stream().mapToDouble(d -> d).toArray();
	}
		
	public String printSolvents()
	{
		StringBuilder result = new StringBuilder();
		
		for (Solvent solvent : solvents)
		{
			result.append(String.format("\tSolvent : %3d\t%30s%5d\n", solvent.getNumber(), solvent.getName(), solvent.getClusterNumber()));
			// result.append(solvent.printHansenData());
		}
		
		return result.toString();
	}

	@Override
	public int compareTo(Cluster otherCluster) 
	{
		return Double.compare(this.quantizationError, otherCluster.quantizationError);
	}
	
	public int getNumber() 										{ return number; }
	public void setNumber(int number) 							{ this.number = number; }
	public Data<Integer, Integer> getChartData()				{ return chartData; }
	public ArrayList<Solvent> getSolvents() 					{ return solvents; }
	public ArrayList<Double> getCentroidFeatureValues() 		{ return centroidFeatureValues; }
	public int getxCoordinate() 								{ return xCoordinate; }
	public int getyCoordinate() 								{ return yCoordinate; }
	public Double getQuantizationError() 						{ return quantizationError; }
	public void setQuantizationError(Double quantizationError)	{ this.quantizationError = quantizationError; }

}

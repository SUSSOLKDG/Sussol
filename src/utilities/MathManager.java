package utilities;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;

import general.GeneralData;
import model.candidates.CandidatesManager;
import model.clustering.DistanceClusterPair;
import model.features.Feature;
import model.features.FullSetMetaData;
import model.solvents.HansenData;
import model.solvents.Solvent;
import model.statistics.Neighbor;
import model.statistics.StatisticalAnalysis;

public class MathManager 
{
	public static double getEuclideanDistance(double[] vector1, double[] vector2)
	{
		double sum = 0.0;
		
		for (int i=0; i < vector1.length; i++)
		{
			sum += (vector1[i] - vector2[i]) * (vector1[i] - vector2[i]);
		}
		
		return Math.sqrt(sum);
	}
	
	public static double getEuclideanDistance(ArrayList<Double> vector1, ArrayList<Double> vector2)
	{
		double sum = 0.0;
		
		for (int i=0; i < vector1.size(); i++)
		{
			sum += (vector1.get(i).doubleValue() - vector2.get(i).doubleValue()) * (vector1.get(i).doubleValue() - vector2.get(i).doubleValue());
		}
		
		return Math.sqrt(sum);
	}
	
	public static double getChebyshevDistance(ArrayList<Double> vector1, ArrayList<Double> vector2)
	{
		double distance = 0.0;
		
		for (int i=0; i < vector1.size(); i++)
		{
			double currentDistance = Math.abs(vector1.get(i).doubleValue() - vector2.get(i).doubleValue()); 
            distance = (currentDistance > distance) ? currentDistance : distance; 
		}
		
		return distance;
	}
	
	public static double getManhattanDistance(ArrayList<Double> vector1, ArrayList<Double> vector2)
	{
		double sum = 0.0;
		
		for (int i=0; i < vector1.size(); i++)
		{
			sum += Math.abs(vector1.get(i).doubleValue() - vector2.get(i).doubleValue());
		}
		
		return sum;
	}
	
	// Measure the stability of the quantization error in the SOM.
	public static double getCoefficientOfVariaton(double[] values)
	{
		return 100 * (new StandardDeviation().evaluate(values) / new Mean().evaluate(values));
	}
	
	public static double getMean(double[] values)
	{
		return new Mean().evaluate(values);
	}
	
	public static double getStandardDeviation(double[] values)
	{
		return new StandardDeviation().evaluate(values);	
	}
	
	public static double[] normalize(double[] values)
	{
		// x = (x-min)/(max - min)
		
		double[] result = new double[values.length];
		
		double min = new Min().evaluate(values);
		double max = new Max().evaluate(values);
		double denominator = max - min;
		
		for (int i=0; i < values.length; i++)
		{
			result[i] = (values[i] - min) / denominator;
		}
		
		return result;
	}
	
	public static double getMin(ArrayList<Double> values)
	{
		double[] tempValues = new double[values.size()];
		
		int i = 0;
		for (Double value : values)
			tempValues[i++] = value.doubleValue(); 		
		
		return new Min().evaluate(tempValues);
	}
	
	public static double getMax(ArrayList<Double> values)
	{
		double[] tempValues = new double[values.size()];
		
		int i = 0;
		for (Double value : values)
			tempValues[i++] = value.doubleValue(); 		
		
		return new Max().evaluate(tempValues);
	}
	
	public static Double[][] getDistanceMatrix(ArrayList<ArrayList<Double>> vectors)
	{
		Double[][] result = new Double[vectors.size()][vectors.size()];

		ArrayList<Double> featuresVector1 = null;
		ArrayList<Double> featuresVector2 = null;
		
		// Get distances.
		for (int i=0; i< vectors.size(); i++) 
		{
			featuresVector1 = vectors.get(i);
			for (int j=0; j < vectors.size(); j++) 
			{
				featuresVector2 = vectors.get(j);
				result[i][j] = MathManager.getEuclideanDistance(featuresVector1, featuresVector2);
			}
		}
		
		return result;
	}	
	
	public static void printDistanceMatrix(Double[][] distanceMatrix)
	{
		// Print headers.
		String logString = "\t";
		for (int i=0; i < distanceMatrix[0].length; i++)
		{
			logString += String.format("%5d", i);
			if (i < distanceMatrix[0].length - 1)
			{	
				logString += "\t";
			}			
		}
		logString += "\n";
		
		// Print distances.
		for (int i=0; i< distanceMatrix[0].length; i++) 
		{
			logString += String.format("%5d\t", i);
			for (int j=0; j < distanceMatrix[0].length; j++) 
			{
				logString += String.format("%s", GeneralData.decimalFormat(distanceMatrix[i][j]));
				
				if (j < distanceMatrix[0].length - 1)
				{	
					logString += "\t";
				}
			}
			logString += "\n";
		}
		
//		SussolLogger.getInstance().info(logString);
	}
	
	public static ArrayList<DistanceClusterPair> getDistanceClusterPairs(int solventClusterNumber, Double[] clusterDistances)
	{
		ArrayList<DistanceClusterPair> result = new ArrayList<DistanceClusterPair>();
		
		for (int i=0; i < clusterDistances.length; i++) 
		{
			result.add(new DistanceClusterPair(solventClusterNumber, i, clusterDistances[i]));
		}
		
		Collections.sort(result);
		return result;
	}
	
	// Get all distances between all solvents.
	public static double[] getAllFeaturesDistances(ArrayList<Solvent> solvents)
	{
		double[] result = new double[solvents.size() * solvents.size()];
		
		for (int i = 0; i < solvents.size(); i++)
			for (int j = i + 1; j < solvents.size(); j++)
			{
				result[i] = getEuclideanDistance(solvents.get(i).getFeatureValuesAsdouble(), solvents.get(j).getFeatureValuesAsdouble());
			}
		
		return result;
	}	
	
	// Get all Hansen distances between all solvents.
	public static double[] getHansenDistances(ArrayList<Solvent> solvents)
	{
		double[] result = new double[solvents.size() * solvents.size()];
		
		for (int i = 0; i < solvents.size(); i++)
			for (int j = i + 1; j < solvents.size(); j++)
			{
				result[i] = getHansenDistance(solvents.get(i).getHansenData(), solvents.get(j).getHansenData());
			}
		
		return result;
	}	
	
	public static double getHansenDistance(HansenData hansenData1, HansenData hansenData2)
	{
		double result = 4 * Math.pow(hansenData1.getHansenDeltaD() - hansenData2.getHansenDeltaD(), 2);
		result += Math.pow(hansenData1.getHansenDeltaP() - hansenData2.getHansenDeltaP(), 2);
		result += Math.pow(hansenData1.getHansenDeltaH() - hansenData2.getHansenDeltaH(), 2);
		
		return Math.sqrt(result);
	}

	// Returns the Arraylist of neighbors of The Solvent, sorted on the neighborhood counter, in descending order.
	public static ArrayList<Neighbor> printNeighborhoods(double[][] neighborhoods, ArrayList<String> solventNames) 
	{
		ArrayList<Neighbor> theSolventNeighbors = new ArrayList<Neighbor>();
		
		int columnNumberOfTheSolvent = 0;
		
		// Print headers.
		String logString = "\t";
		for (int i=0; i < solventNames.size(); i++)
		{
			logString += String.format("%s", solventNames.get(i));
			if (i < solventNames.size() - 1)
				logString += "\t";
			
			if (GeneralData.theSolvent.getName().equalsIgnoreCase(solventNames.get(i)))
				columnNumberOfTheSolvent = i;
		}
		logString += "\n";
		
		// Print values of final neighborhood.
		for (int i=0; i < neighborhoods.length; i++)
		{
			logString += String.format("%s\t", solventNames.get(i));
			for (int j=0; j < neighborhoods.length; j++)
			{
				logString += GeneralData.decimalFormat(neighborhoods[i][j]);
				if (j < solventNames.size() - 1)
					logString += "\t";
				
				if (columnNumberOfTheSolvent == j)
					theSolventNeighbors.add(new Neighbor(solventNames.get(i), (int) neighborhoods[i][j]));
			}
			logString += "\n";
			
//			GeneralData.logProgress(mainView, "Neighborhoods [" + i + "]");
			
			if (i % 50 == 0)
				System.gc();
		}
//		SussolLogger.getInstance().info(logString);
		
		Collections.sort(theSolventNeighbors);
		
		logString = "Neighbors of " + GeneralData.theSolvent.getName() + "\n*******************************\n";
		for (Neighbor theSolventNeighbor : theSolventNeighbors)
			logString += theSolventNeighbor.getName() + "\t" + theSolventNeighbor.getNeighborCounter() + "\n";
//		SussolLogger.getInstance().info(logString);
		
		return theSolventNeighbors;
	}

	public static int getMatrixSum(int[][] neighborhoods) 
	{
		int result = 0;
		
		for (int i=0; i < neighborhoods.length; i++)
		{
			for (int j=0; j < neighborhoods.length; j++)
			{
				result += neighborhoods[i][j];
			}
		}
		
		return result;
	}
	
	public static StatisticalAnalysis getNeighborhoodStats(int width, int height, int radius, int numberOfClusterings, double[][] finalNeighborhoods)
	{
		double mapRange = width * height;
		double nu = radius + 1;
		
		double mu = (double)numberOfClusterings * nu / mapRange;
		double sigma = Math.sqrt((double)numberOfClusterings * nu / mapRange * (1 - nu / mapRange));
	
		double lowerLimit = mu - (1.96 * sigma);
		double upperLimit = mu + (1.96 * sigma);		
		
		return(new StatisticalAnalysis(finalNeighborhoods, lowerLimit, upperLimit));
	}
	
	public static double normalize(Feature feature, Double min, Double max)
	{
		// x = (x-min)/(max - min)
		
		// Normalised on full dataset min and max.
//		double min = FullSetMetaData.getInstance().getFeatureMetaData(feature.getName()).getMin();
//		double max = FullSetMetaData.getInstance().getFeatureMetaData(feature.getName()).getMax();

		// Normalised on candidates min and max.
//		double min = GeneralData.minFeatureValue;
//		double max = GeneralData.maxFeatureValue;

		return (feature.getValue() - min) / (max - min);
	}
}

package model.clustering;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import general.GeneralData;
import general.SolventSortOrder;
import javafx.scene.chart.XYChart.Series;
import mdsj.MDSJ;
import model.solvents.Solvent;
import model.som.SOMNet;
import utilities.MathManager;

public class Clustering implements Comparable<Clustering>
{
	private ArrayList<Solvent> solvents;	
	private ArrayList<Cluster> clusters;	
	private int numberOfClusters;
	
	private Double totalQuantizationError;			// Sum of the QE's of all solvents in the SOM.
	
	private double[][] neighborhoods;
		
	public Clustering() 
	{
		solvents = new ArrayList<Solvent>();
		clusters = new ArrayList<Cluster>();
		
		totalQuantizationError = 0.0;
	}
	
	public void initialiseNeighborhoodMatrix(int dimension)
	{
		neighborhoods = new double[dimension][dimension];
	}
	
	public void makeClusters(SOMNet somNet)
	{
		int currentClusterNumber = -1;
		int clusterCounter = -1;
		Cluster currentCluster = null;
		
		// Sort collections on the SOM clusterNumber : necessary to make a new cluster.
		GeneralData.solventSortOrder = SolventSortOrder.BY_CLUSTER_NUMBER;
		Collections.sort(solvents);
		
		// Run through solvents and fill clusters with them.
		for (Solvent solvent : solvents)
		{
			if (currentClusterNumber != solvent.getClusterNumber())		// The SOM cluster number.
			{					
				clusterCounter++;
				
				// Get x and y coordinates from SOMNet, based on the cluster number of the solvent in the SOM.
				Point point = somNet.getCoordinates(solvent.getClusterNumber());
				
				currentCluster = new Cluster(clusterCounter, point);
				clusters.add(currentCluster);	
				
				currentClusterNumber = solvent.getClusterNumber();		// Only purpose is to know when to make a new cluster.
			}
			
			solvent.setClusterNumber(clusterCounter);		// New cluster number, overwrites the SOM cluster number.
			currentCluster.addSolvent(solvent);
		}
		
		/* MDS
		// Run through solvents and fill clusters with them.
		for (Solvent solvent : solvents)
		{
			if (currentClusterId != solvent.getClusterNumber())
			{	
				// We cannot use the clusternumbers of the SOM for the charts.
				// In adding the eventhandlers in the view, we have to use a simple increasing counter.
				// So we do that here as well.
				// Also better for the user, simple list starting from 0.
				//
				// For building the list of clusters, we still have to work with the SOM clusterId.
				
				currentCluster = new Cluster(clusterCounter++);
				clusters.add(currentCluster);		
				
				currentClusterId = solvent.getClusterNumber();
			}
			
			// Reset the clusternumber of the solvent.
			// We needed the SOM numbering to make the clusters, see above.
			// We no longer need that numbering, and reset it here to a series starting from 0.
			// Needed for further processing, eg in the distance matrix in the stability analysis.
			
			solvent.setClusterNumber(clusterCounter - 1);
			
			currentCluster.addSolvent(solvent);
		}
		*/
		
		numberOfClusters = clusters.size();
		
		// For each cluster, initialise the solvent chart data.
		for (Cluster cluster : clusters)
			cluster.initialiseSolventChartdataMDS();
	}
	
	public void initialiseClusterChartDataMDS()
	{
		// Input distance matrix for MDS.
		int dimension = clusters.size();
		double[][] input = new double[dimension][dimension]; 
	
		for (int i = 0; i < dimension; i++)
		{
			// Get the cluster centroids.
			double[] centroid1 = clusters.get(i).getCentroidValues(); 
			
			// Calculate distances.
			for (int j = 0; j < dimension; j++)
			{
				double[] centroid2 = clusters.get(j).getCentroidValues(); 
				input[i][j] = MathManager.getEuclideanDistance(centroid1, centroid2);
			}
		}

		// Apply MDS.
		double[][] output = MDSJ.classicalScaling(input); 
		
		// Fill series.
		for(int i = 0; i < dimension; i++) 
		{  
		    // x and y : MDS using the centroid vectors of the clusters.
		    int x = (int) output[0][i];
		    int y = (int) output[1][i];
		    
		    // Radius = for each cluster the standard deviation of all-features distances of the solvents.
		    double radius = 100 + clusters.get(i).getStandardDeviationAllFeaturesDistances() * 100;
		    
		    // Or
		    
		    // Radius = for each cluster the standard deviation of the Hansen distances of the solvents.
		    // double radius = 100 + clusters.get(i).getStandardDeviationHansenDistances() * 100;
		    
		    clusters.get(i).initialiseChartData(x, y, (int)radius);
		}	
	}
	
	public void initialiseClusterChartDataSOM()
	{
		for(int i = 0; i < clusters.size(); i++) 
		{  
			int x = clusters.get(i).getxCoordinate() * 10000;
			int y = clusters.get(i).getyCoordinate() * 10000;
			
			double radius = 5000 + clusters.get(i).getStandardDeviationAllFeaturesDistances() * 100;
			
			clusters.get(i).initialiseChartData(x, y, (int)radius);
		}
	}
	
	public Series<Integer, Integer> getClusterChartData()
	{
		Series<Integer, Integer> chartData = new Series<Integer, Integer>();
		
		// Run through clusters and fill Series with chart data.
		for (Cluster cluster : clusters)
		{
			chartData.getData().add(cluster.getChartData());
		}
		
		return chartData;
	}
	
	public void computeTotalQuantizationError()
	{
		// Intra Class distortion : sum of all the distances in the SOM, regardless of the clusters.
		
		// Run through the clusters.
		// For all solvents in each cluster, compute the quantization error.
		// For all solvents in each cluster, add the quantization error to the total quantization error.
		
		for (Cluster cluster : clusters)
		{
			// Calculate and initialise the cluster centroids.
			cluster.getCentroidValues();
			
			Double clusterQuantizationError = new Double(0.0);
			for (Solvent solvent : cluster.getSolvents())
			{
				clusterQuantizationError += MathManager.getEuclideanDistance(solvent.getFeatureValues(), cluster.getCentroidFeatureValues());
			}
			cluster.setQuantizationError(clusterQuantizationError);
			
			totalQuantizationError += clusterQuantizationError;
		}
	}
	
	public Cluster getCluster(int clusterNumber)
	{
		return clusters.get(clusterNumber);
	}
	
	public Solvent getSolvent(String casNumber)
	{
		for (Solvent solvent : solvents)
			if (solvent.getCasNumber().equalsIgnoreCase(casNumber))
				return solvent;
		
		return new Solvent(-1);
	}
	
	public void printClusters()
	{
		String logString = "";
		
		if (clusters.size() > 0)
		{
			logString = "-------------------------------\nAfter clustering, cluster list\n\n";
			for (Cluster cluster : clusters)
			{				
				logString += "Cluster " + cluster.getNumber() + "\n";
				logString += cluster.printSolvents();
				logString += "\nQuantization error = " + cluster.getQuantizationError() + "\n";
			}
		}
		else
		{
			logString = "-------------------------------\nNo clusters.\n\n";
		}
		
//		SussolLogger.getInstance().info(logString);
	}
	
	@Override
	public int compareTo(Clustering otherClustering) 
	{
		return Double.compare(this.totalQuantizationError, otherClustering.totalQuantizationError);
	}
	
	public ArrayList<Solvent> getSolvents()		{ return solvents; }
	public ArrayList<Cluster> getClusters()		{ return clusters; }
	public Double getTotalQuantizationError()	{ return totalQuantizationError; }
	public int getNumberOfClusters() 			{ return numberOfClusters; }
	public double[][] getNeighborhoods() 		{ return neighborhoods; }
}

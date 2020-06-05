package model.clustering;

import java.util.ArrayList;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;

import general.GeneralData;
import javafx.scene.chart.XYChart.Series;
import model.features.Feature;
import model.solvents.Solvent;
import model.som.SOM;
import utilities.MathManager;
import utilities.SussolLogger;

public class ClusteringManager 
{
	public static Clustering doTheClustering(int width, int height, double learningRate) 
	{
//		GeneralData.logDataSet();
		
		Clustering clustering = new Clustering();
		
		// The SOM
		SOM som = new SOM();
		som.setColumnCount(width);
		som.setRowCount(height);
		som.setLearningRate(learningRate);
		
		// Let the SOM cluster the data. Result is a DataFrame containing solvents.
		DataFrame clusteredSolvents = som.fitAndTransform(GeneralData.solventDataFrame);
   				
		// Extract a solvent from each datarow.
		// Add it to the solventlist of the Clustering.
		for (int i = 0; i < clusteredSolvents.rowCount(); i++)
		{
			DataRow tuple = clusteredSolvents.row(i);
			
			String casNumber = tuple.getCategoricalTargetCell("Cas Number");			   
			Integer clusterNumber = Integer.parseInt(tuple.getCategoricalTargetCell("cluster"));	// The SOM cluster number.		
					
			// Use copy constructor : the new solvent is decoupled from the GeneralData.solvents.
			Solvent solvent = new Solvent(GeneralData.solvents.get(casNumber));
			solvent.setClusterNumber(clusterNumber);
			
			clustering.getSolvents().add(solvent);
		}
		
		// We now have the final list of Solvent objects. The clustering will run through the list and make clusters.
		clustering.makeClusters(som.getNet());
		
//		clustering.initialiseClusterChartDataMDS();
		clustering.initialiseClusterChartDataSOM();
		
		clustering.initialiseNeighborhoodMatrix(clustering.getSolvents().size());
		
		clustering.computeTotalQuantizationError();
		
		return clustering;
	}
	
	public static void doTheHansenAnalysis(DataFrame solventData)
	{
		ArrayList<Solvent> solvents = new ArrayList<Solvent>();
		
		// No SOM, first get the solvents from the DataFrame.
		for (int i = 0; i < solventData.rowCount(); i++) 
        {
            DataRow row = solventData.row(i);
            
            String solventName = row.getCategoricalTargetCell("Name");			
			
			ArrayList<Feature> featureValues = new ArrayList<Feature>();
			for (String featureName : row.getColumnNames())
			{
				featureValues.add(new Feature(featureName, row.getCell(featureName)));
			}
			
			solvents.add(new Solvent(i, solventName, -1, featureValues));
        }  
		
		String logString = "Hansen Distances for this dataset:\n\n";
		// For each solvent, let the MathManager compute the Hansen distances to the other solvents.
		for (Solvent solvent1 : solvents)
		{
			logString += solvent1.getName() + "\n";
			for (Solvent solvent2 : solvents)
			{
				logString += "\t" + solvent2.getName() + "\t";
				logString += GeneralData.decimalFormat(MathManager.getHansenDistance(solvent1.getHansenData(), solvent2.getHansenData())) + "\n";
			}
		}
//		SussolLogger.getInstance().info(logString);
		
		// MathManager.hansenDistanceMatrix(solvents);
	}
	
	public static Series<Integer, Integer> getSolventChartData(int clusterNumber)
	{
		Cluster cluster = GeneralData.finalClustering.getCluster(clusterNumber);
		
		Series<Integer, Integer> series = new Series<Integer, Integer>();
		
		for (Solvent solvent : cluster.getSolvents())
		{			
			series.getData().add(solvent.getChartData());
		}	
		
		return series;
	}
	
	public static Cluster getCluster(int clusterNumber)
	{
		return GeneralData.finalClustering.getCluster(clusterNumber);
	}

}

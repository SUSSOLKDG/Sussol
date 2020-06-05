package presenter;

import java.util.ArrayList;
import java.util.Collections;

import general.GeneralData;
import general.SolventSortOrder;
import model.candidates.CandidatesManager;
import model.clustering.Cluster;
import model.clustering.Clustering;
import model.clustering.ClusteringManager;
import model.clustering.DistanceClusterPair;
import model.solvents.Solvent;
import model.statistics.Neighbor;
import model.statistics.StatisticalAnalysis;
import utilities.MathManager;
import view.MainView;

public class MainPresenter 
{
	private MainView mainView;
	
	private static ArrayList<Clustering> clusterings;
	
	public MainPresenter(MainView mainView) 
	{
		this.mainView = mainView;
	}

	public static Clustering getClustering(int clusteringNumber)
	{
		return clusterings.get(clusteringNumber);
	}
	
	public void cluster(int width, int height, double learningRate, int numberOfClusterings)
	{
		mainView.logProgress(0.25);
		
		// Perform the clusterings and save them in a list.
		clusterings = new ArrayList<Clustering>();
		for (int i=0; i < numberOfClusterings; i++)
		{
			clusterings.add(ClusteringManager.doTheClustering(width, height, learningRate));
		}
		
		//////////////////////////////////////////////////////////
		// CLUSTERING ANALYSIS : clustering data and metadata.
		//////////////////////////////////////////////////////////
		
		// Sort the clusterings on totalQuantizationError.	
		Collections.sort(clusterings);
		
		// Collect for all clusterings the quantization errors and the numbers of clusters, for the clustering analysis.
		double[] numbersOfClusters = new double[numberOfClusterings];
		double[] quantizationErrors = new double[numberOfClusterings];
		
		String logString = "\n*********************************\n\n";
		logString += "**** Clustering analysis\n\nThe clusterings are sorted on Sum of Quantization Errors.\n";
//		SussolLogger.getInstance().info(logString);		
		
		int clusteringCounter = 0;
		for (Clustering clustering : clusterings)
		{		
			numbersOfClusters[clusteringCounter] = clustering.getNumberOfClusters();
			quantizationErrors[clusteringCounter++] = clustering.getTotalQuantizationError();
		}

		mainView.logProgress(0.35);
		
		//
		//////////////////////////////////////////////////////////
		// CV ANALYSIS : find the optimal clustering.
		//////////////////////////////////////////////////////////
		
		// Run through the clusterings.
		// After each clustering compute the CV for all the clusterings up to that point.		
		logString = "\n*********************************\n\n";
		logString += "**** Incremental CV Analysis, on sorted list of clusterings.\n";
//		logString += "\nAfter clustering\tAvg nr of clusters\tStdDev of nr of clusters\tCV\tDelta";
//		SussolLogger.getInstance().info(logString);	
		
		// TODO : warning oplossen.
		
		double previousCV = 0;
		double currentCV = 0;
		
		ArrayList<Double> numberOfClustersCumulative = new ArrayList<Double>();
		ArrayList<Double> quantizationErrorsCumulative = new ArrayList<Double>();
		
		logString = "";
		clusteringCounter = 0;
		for (Clustering clustering : clusterings)
		{	
//			GeneralData.logProgress(mainView, "CV Analysis  " + clusteringCounter);
			
			clusteringCounter++;
			
			numberOfClustersCumulative.add((double) clustering.getClusters().size());
			quantizationErrorsCumulative.add(clustering.getTotalQuantizationError());
			
			double[] cvAnalysisNumbersOfClusters = new double[numberOfClustersCumulative.size()];
			double[] cvAnalysisQuantizationErrors = new double[quantizationErrorsCumulative.size()];
			
			for (int i = 0; i < clusteringCounter; i++)
			{
				cvAnalysisNumbersOfClusters[i] = numberOfClustersCumulative.get(i);
				cvAnalysisQuantizationErrors[i] = quantizationErrorsCumulative.get(i);
			}
			
			currentCV = MathManager.getCoefficientOfVariaton(cvAnalysisQuantizationErrors);
			
			previousCV = currentCV;
		}
		
		mainView.logProgress(0.5);
		
		if (GeneralData.theSolventLives)
		{		
			//////////////////////////////////////////////////////////
			// STABILITY ANALYSIS
			//////////////////////////////////////////////////////////
			
			logString = "\n*********************************\n\n";
			logString += "**** Stability Analysis\n";
//			SussolLogger.getInstance().info(logString);	
			
			int radius = 0;
			
			// Finalneighborhoods contains the neighbors over all clusterings.
			double[][] finalNeighborhoods = new double[GeneralData.numberOfSolvents][GeneralData.numberOfSolvents];
			
			// For all clusterings, fill finalneighborhoods for radius 0.
			clusteringCounter = 0;
			for (Clustering clustering : clusterings)
			{
//				GeneralData.logProgress(mainView, "Stability Analysis  " + clusteringCounter++);
				
				// For the current clustering, get the distancematrix of the cluster centroids.	
				ArrayList<ArrayList<Double>> centroids = new ArrayList<ArrayList<Double>>();
				for (Cluster cluster : clustering.getClusters())
				{
					centroids.add(cluster.getCentroidFeatureValues());
				}
				Double[][] clusterDistances = MathManager.getDistanceMatrix(centroids);
				
	//			SussolLogger.getInstance().info("Distance matrix of cluster centroids:\n\t");
	//			MathManager.printDistanceMatrix(clusterDistances);
				
				// Sort the solvents of the current clustering alphabetically.
				GeneralData.solventSortOrder = SolventSortOrder.BY_NAME;
				Collections.sort(clustering.getSolvents());
				
				for (int i=0; i < GeneralData.numberOfSolvents; i++)
				{							
					Solvent solvent1 = clustering.getSolvents().get(i);
					
					// Get the list of distanceClusterPairs for solvent 1.
					ArrayList<DistanceClusterPair> distanceClusterPairs = 
						MathManager.getDistanceClusterPairs(solvent1.getClusterNumber(), clusterDistances[solvent1.getClusterNumber()]);								
										
					for (int j=0; j < GeneralData.numberOfSolvents; j++)
					{
						Solvent solvent2 = clustering.getSolvents().get(j);	
						
						if (solvent1.getNumber() != solvent2.getNumber())		// To avoid a solvent being a neighbor of itself.
							if (solvent2.getClusterNumber() == distanceClusterPairs.get(radius).getCluster2Number())
								finalNeighborhoods[i][j] += 1.0;
					}
				}
				
	//				SussolLogger.getInstance().info("\n---------------------------------\nThe neighborhoods of clustering\t" + clusteringCounter + "\n");
	//				MathManager.printNeighborhoods(clustering.getNeighborhoods(), clustering.getSolvents());
			}
					
			logString = "Making Stability Matrix";
//			SussolLogger.getInstance().info(logString);	

			mainView.logProgress(0.6);
			
//			SussolLogger.getInstance().info("\nThe neighborhood matrix, over all clusterings:\n");
			ArrayList<Neighbor> neighborsOfTheSolvent = MathManager.printNeighborhoods(finalNeighborhoods, GeneralData.solventNames);
			
//			GeneralData.logProgress(mainView, "Statistical analysis");
			mainView.logProgress(0.7);
			StatisticalAnalysis statisticalAnalysis = MathManager.getNeighborhoodStats(width, height, radius, numberOfClusterings, finalNeighborhoods);
//			SussolLogger.getInstance().info("Statistical analysis:\n");
			logString = "Number of Neighbors:\t" + statisticalAnalysis.getNeighbors();
			logString += "\t=\t" + GeneralData.decimalFormat(statisticalAnalysis.getNeighborsPercentage()) +"\t%\n";
			logString += "Number of Not Neighbors:\t" + statisticalAnalysis.getNotNeighbors();
			logString += "\t=\t" + GeneralData.decimalFormat(statisticalAnalysis.getNotNeighborsPercentage()) +"\t%\n";
			logString += "Number of Not Significants:\t" + statisticalAnalysis.getNotSignificants();
			logString += "\t=\t" + GeneralData.decimalFormat(statisticalAnalysis.getNotSignificantsPercentage()) +"\t%\n\n";
			logString += "Lowerlimit =\t" + GeneralData.decimalFormat(statisticalAnalysis.getLowerLimit()) + "\n";
			logString += "Upperlimit =\t" + GeneralData.decimalFormat(statisticalAnalysis.getUpperLimit()) + "\n";
//			SussolLogger.getInstance().info(logString);
				
			mainView.logProgress(0.9);
			
			CandidatesManager.getInstance().setNeighborsOfTheSolvent(neighborsOfTheSolvent);
			CandidatesManager.getInstance().setUpperLimit(statisticalAnalysis.getUpperLimit());
			CandidatesManager.getInstance().generateCandidates();
			
			mainView.logProgress(1);
		}
		
		
		// Initialise the final clustering, to be used for visualisation.
		GeneralData.finalClustering = clusterings.get(0);
		
//		GeneralData.logProgress(mainView, "Finished");
	}
}

package presenter;

import general.GeneralData;
import javafx.scene.chart.XYChart.Series;
import model.clustering.ClusteringManager;
import view.ClusteringView;

public class ClusteringPresenter 
{
	private ClusteringView clusteringView;

	public ClusteringPresenter(ClusteringView clusteringView) 
	{
		this.clusteringView = clusteringView;
	}
	
	public Series<Integer, Integer> getFinalClusteringChartData()
	{
		return GeneralData.finalClustering.getClusterChartData();
	}

	public Series<Integer, Integer> getSolventChartData(int clusterNumber)
	{
		return ClusteringManager.getSolventChartData(clusterNumber);
	}	
}

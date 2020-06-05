package model.solvents;

import java.util.ArrayList;

import general.GeneralData;
import javafx.scene.chart.XYChart.Data;
import model.features.Feature;
import model.features.FullSetMetaData;
import utilities.MathManager;

public class Solvent implements Comparable<Solvent>
{
	private int number;						// As in the csv file, starting from 1.
	private String name;
	private String casNumber;
	private EHSData ehsData;
	private ArrayList<Feature> features;
	
	private int clusterNumber;
	private int bubbleNumber;				// On the solvent chart.
	
	private Data<Integer, Integer> chartData;
	
	private HansenData hansenData;

	public Solvent(int number)
	{
		this.number = number;
	}
	
	// Copy constructor, is used in doTheClustering of the ClusteringManager to copy the current solvent from the GeneralData.solvents set.
	public Solvent(Solvent otherSolvent)
	{
		this.number = otherSolvent.getNumber();
		this.name = otherSolvent.getName();
		this.casNumber = otherSolvent.getCasNumber();
		this.ehsData = otherSolvent.getEhsData();
		this.features = otherSolvent.getFeatures();
		this.clusterNumber = otherSolvent.getClusterNumber();
		this.bubbleNumber = otherSolvent.getBubbleNumber();	
	}
	
	// TODO : nog nodig ? Denk het niet.
	public Solvent(int number, String name, int clusterNumber, ArrayList<Feature> features) 
	{
		this.number = number;
		this.name = name;
		
		this.clusterNumber = clusterNumber;
		this.features = new ArrayList<Feature>();
		this.features.addAll(features);
		
		hansenData = new HansenData();
		for (Feature feature : features)
		{
			if (feature.getName().equalsIgnoreCase("Hansen Delta D (MPa1/2)")) hansenData.setHansenDeltaD(feature.getValue());
			if (feature.getName().equalsIgnoreCase("Hansen Delta P (MPa1/2)")) hansenData.setHansenDeltaP(feature.getValue());
			if (feature.getName().equalsIgnoreCase("Hansen Delta H (MPa1/2)")) hansenData.setHansenDeltaH(feature.getValue());
		}
		hansenData.makeVector();
	}
	
	public void initialiseChartData(int x, int y, int radius, int bubbleNumber) 
	{				
		chartData = new Data<Integer, Integer>(x, y, radius);
		
		this.bubbleNumber = bubbleNumber;
	}
	
	public ArrayList<Double> getFeatureValues()
	{
		ArrayList<Double> result = new ArrayList<Double>();
		
		for (Feature feature : features)
			result.add(feature.getValue());		
		
		return result;
	}
	
	public double[] getFeatureValuesAsdouble()
	{
		double[] result = new double[features.size()];
		
		for (int i = 0; i < features.size(); i++)
			result[i] = features.get(i).getValue();		
		
		return result;
	}
	
	public double[] getFeatureValuesAsdoubleNormalised()
	{
		double[] result = new double[features.size()];
		
		for (int i = 0; i < features.size(); i++)
		{
			double min = FullSetMetaData.getInstance().getFeatureMetaData(features.get(i).getName()).getMin();
			double max = FullSetMetaData.getInstance().getFeatureMetaData(features.get(i).getName()).getMax();
			result[i] = MathManager.normalize(features.get(i), min, max);	
		}
		
		return result;
	}
	
	public void copyFeature(String featureName, String newFeatureName)
	{
		for (int i=0; i < features.size(); i++)
			if (features.get(i).getName().equalsIgnoreCase(featureName))
			{
				Feature feature = features.get(i).clone();
				feature.setName(newFeatureName);
				features.add(feature);
			}
	}
	
	public void removeFeature(String featureName)
	{
		for (int i=0; i < features.size(); i++)
			if (features.get(i).getName().equalsIgnoreCase(featureName))
				features.remove(features.get(i));
	}
	
	public void initialiseHansenData() 
	{
		hansenData = new HansenData();
		for (Feature feature : features)
		{
			if (feature.getName().equalsIgnoreCase("Hansen Delta D (MPa1/2)")) hansenData.setHansenDeltaD(feature.getValue());
			if (feature.getName().equalsIgnoreCase("Hansen Delta P (MPa1/2)")) hansenData.setHansenDeltaP(feature.getValue());
			if (feature.getName().equalsIgnoreCase("Hansen Delta H (MPa1/2)")) hansenData.setHansenDeltaH(feature.getValue());
		}
		hansenData.makeVector();
	}
	
	public String printHansenData()
	{
		return "HDD = " + hansenData.getHansenDeltaD() + " HDP = " + hansenData.getHansenDeltaP() + " HDH = " + hansenData.getHansenDeltaH() + "\n";
	}
		
	@Override
	public int compareTo(Solvent otherSolvent) 
	{
		switch (GeneralData.solventSortOrder)
		{
			case BY_CLUSTER_NUMBER:
				return Integer.compare(this.getClusterNumber(), otherSolvent.getClusterNumber());
			case BY_NAME:
				return this.getName().compareTo(otherSolvent.getName());
			case BY_CAS_NUMBER:
				return this.getCasNumber().compareTo(otherSolvent.getCasNumber());
			default:
				break;
		}
		
		return 0;
	}
	
	@Override
	public String toString() 
	{
		String result = "Solvent [number=" + number + ", casNumber=" + casNumber + " , name=" + name + ", clusterNumber=" + clusterNumber + ", bubbleNumber=" + bubbleNumber + "]";
		
		result += "\n";
		for (Feature feature : features)
			result += "\t" + feature.toString() + "\n";
		result += "\n";
		
		return result;
	}
	
	public String toStringNoFeatures() 
	{
		String result = "Solvent [number=" + number + ", casNumber=" + casNumber + " , name=" + name + ", clusterNumber=" + clusterNumber + ", bubbleNumber=" + bubbleNumber + "]";
		
		return result;
	}

	public int getNumber() 									{ return number; }
	public void setName(String name) 						{ this.name = name; }
	public String getName() 								{ return name; }
	public void setCasNumber(String casNumber) 				{ this.casNumber = casNumber; }
	public String getCasNumber() 							{ return casNumber; }	
	public void setEhsData(EHSData eHSData)					{ this.ehsData = eHSData; }
	public EHSData getEhsData() 							{ return ehsData; }
	public void setFeatures(ArrayList<Feature> features)	{ this.features = features; }
	public ArrayList<Feature> getFeatures() 				{ return features; }
	
	public void setClusterNumber(int clusterNumber)			{ this.clusterNumber = clusterNumber; }
	public int getClusterNumber() 							{ return clusterNumber; }
	public void setBubbleNumber(int bubbleNumber)			{ this.bubbleNumber = bubbleNumber; }
	public int getBubbleNumber() 							{ return bubbleNumber; }
	
	public Data<Integer, Integer> getChartData() 			{ return chartData; }
	
	public HansenData getHansenData()						{ return hansenData; }
}

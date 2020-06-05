package general;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.data.frame.InputDataColumn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.clustering.Clustering;
import model.features.FeatureRange;
import model.solvents.Solvent;
import utilities.MathManager;

public class GeneralData 
{
	public static String currentDataSetName;
	
	public static DataFrame solventDataFrame;
	public static int numberOfSolvents;
	public static SolventSortOrder solventSortOrder;
	public static ArrayList<String> solventNames;
	public static HashMap<String, Solvent> solvents;
	
	public static Solvent theSolvent;
	public static boolean theSolventLives;
	
	public static ArrayList<String> featureNames;
	public static int numberOfFeatures;
	
	public static Clustering finalClustering;
	public static boolean finalClusteringIsInitialised;
	
	public static Stage filteringStage;
	
	public static enum DistanceData { HANSEN, ALL_FEATURES } 

	public static enum PostFilteringMode { ALL_SOLVENTS, SOLVENTS_FROM_ONE_CLUSTER }
	public static PostFilteringMode postFilteringMode;
	public static enum PreFilteringMode { COPY, REMOVE }
	public static PreFilteringMode preFilteringMode;
	public static ArrayList<Solvent> solventsOfOneCluster;
		
	public final static int PRECISION = 3;
	
	// TODO : all css in css files, and only the css that is used.
	
	public static String normalCss = "-fx-background-color: radial-gradient(radius 180%, darkred, derive(burlywood, -30%), derive(darkred, 30%)); -fx-opacity: 0.70;";
	public static String selectedCss = "-fx-background-color: ivory; -fx-text-fill: red;";
	public static String hansenCss = "-fx-background-color: maroon;";
	public static String normalLabelCss = "-fx-font-size: 16px;";
	public static String selectedlabelCss = "-fx-font-size: 16px; -fx-text-fill: red;";
	public static String normalButtonCss = "-fx-text-fill: ivory; -fx-background-color: maroon;";
	public static String selectedButtonCss = "-fx-text-fill: maroon; -fx-background-color: ivory;";
	public static String selectedFeatureCss = "-fx-background-color: #500000;";	
	public static String sussolLabelCss = "-fx-font-size: 16px;";
	
	public static Solvent getSolventByCasNumber(String casNumber)
	{
		return solvents.get(casNumber);
	}
	
	public static Solvent getSolventByName(String solventName)
	{
		Iterator<Entry<String, Solvent>> it = solvents.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        Map.Entry pair = it.next();
	        
	        Solvent solvent = (Solvent) pair.getValue();
	        
	        if (solvent.getName().equalsIgnoreCase(solventName))
	        {
	        	return solvent;
	        }
	    }
	    
		return null;
	}
	
	public static ArrayList<FeatureRange> getFeatureRanges()
	{
		ArrayList<FeatureRange> featureRanges = new ArrayList<FeatureRange>();
		
		// Get number of columns and make a FeatureRange object for each of them.
		for (InputDataColumn column : GeneralData.solventDataFrame.getInputColumns())
			featureRanges.add(new FeatureRange(column.getColumnName()));
		
		for (FeatureRange featureRange : featureRanges)
		{
			ArrayList<Double> featureValues = new ArrayList<Double>();
			
			for (DataRow row : GeneralData.solventDataFrame.rows())
				featureValues.add(row.getCell(featureRange.getName()));
			
			featureRange.setMinValue(MathManager.getMin(featureValues));
			featureRange.setMaxValue(MathManager.getMax(featureValues));
		}
		
		return featureRanges;
	}
	
	// TODO : not used.
	public static ObservableList<String> getCasNumbersList()
	{
		ObservableList<String> result = FXCollections.observableArrayList();
		
		for (HashMap.Entry<String, Solvent> solvent : solvents.entrySet())
			result.add(solvent.getValue().getCasNumber());
		
		return result;
	}
	
	public static void initialiseTheSolvent(String casNumber)
	{
		for (HashMap.Entry<String, Solvent> solvent : solvents.entrySet())
			if (solvent.getValue().getCasNumber().equalsIgnoreCase(casNumber))
			{
				theSolvent = solvent.getValue();
//				SussolLogger.getInstance().info("\n*********************************\n\nThe Current Solvent : " + theSolvent.getCasNumber() + ", " + theSolvent.getName());
			}
	}
	
	public static String decimalFormat(double value)
	{
		String precisionString = "#####.";
		
		for (int i = 0; i < PRECISION; i++)
			precisionString += "#";
		
		DecimalFormat doubleFormat = new DecimalFormat(precisionString);
		doubleFormat.setRoundingMode(RoundingMode.HALF_UP);
		
		return String.format("%s", doubleFormat.format(value));
	}
	
	static String tempString;
	public static void logSolventsDataFrame()
	{
		tempString = "";
		String logString = "\n*********************************\nThe Solvent DataFrame:\n\n";
		
		solventDataFrame.stream().forEach(row -> tempString += row.toString().replace(',', '\t') + "\n");	
		
		logString += tempString;
		
//		SussolLogger.getInstance().info(logString);
	}
	
	public static void logClusteredSolventsDataFrame(DataFrame solvents)
	{
		tempString = "";
		String logString = "\n*********************************\nThe Solvent DataFrame after clustering:\n\n";
		
		solvents.stream().forEach(row -> tempString += row.toString().replace(',', '\t') + "\n");	
		
		logString += tempString;
		
//		SussolLogger.getInstance().info(logString);
	}
	
	// TODO newlines platform independent.
	
	public static void logSolventHashMap()
	{
		String logString = "\n*********************************\nThe Solvent HashMap:\n\n";
		
		for (HashMap.Entry<String, Solvent> solvent : GeneralData.solvents.entrySet())
			logString += solvent.getValue().toString() + "\n";
		
		logString += "\n";
				
//		SussolLogger.getInstance().info(logString);
	}
	
	public static void logSolventNames()
	{
		tempString = "";
		String logString = "\n*********************************\nThe Solvent Names:\n\n";
		
		solventNames.stream().forEach(name -> tempString += name + "\n");	
		
		logString += tempString;
		
//		SussolLogger.getInstance().info(logString);
	}
}

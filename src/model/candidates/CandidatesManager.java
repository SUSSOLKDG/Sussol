package model.candidates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import general.GeneralData;
import general.SolventSortOrder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import model.features.Feature;
import model.solvents.Solvent;
import model.statistics.Neighbor;
import utilities.MathManager;
import utilities.UIManager;

public class CandidatesManager 
{
	private static CandidatesManager instance = null;
	
	private static ArrayList<Neighbor> neighborsOfTheSolvent;
	private static double upperLimit; 
		
	private ArrayList<Candidate> candidates;		
	
	public double minFeatureValue;
	public double maxFeatureValue;

	private CandidatesManager() 
	{
	}
	
    public static CandidatesManager getInstance() 
    {
       if (instance == null) 
       {
    	   instance = new CandidatesManager();
       }
       return instance;
    }
    
	public void generateCandidates()
	{
		candidates = new ArrayList<Candidate>();
		
		UIManager.resetColorCounter();
		
		// Fill the list of candidates.
		for (Neighbor neighbor : neighborsOfTheSolvent)
			for (HashMap.Entry<String, Solvent> solvent : GeneralData.solvents.entrySet())
				if (
						solvent.getValue().getName().equalsIgnoreCase(neighbor.getName())
						&&
						neighbor.getNeighborCounter() > upperLimit	// Significant neighbors/not neighbors.
				   )
				{
					candidates.add
					(
						new Candidate
						(
							solvent.getValue(), 
							MathManager.getHansenDistance(solvent.getValue().getHansenData(), GeneralData.theSolvent.getHansenData()),
							UIManager.getNextLineColor(),
							"-fx-text-fill: " + solvent.getValue().getEhsData().getEhsColor() + "; -fx-font-weight: bold;"
						)
					);
					UIManager.increaseColorCounter();
				}
		
		// Don't forget to add The Solvent we are looking alternatives for.
		candidates.add(new Candidate(GeneralData.theSolvent, 0.0D, "-fx-stroke: black;", "-fx-text-fill: black; -fx-font-weight: bold;"));
		
		GeneralData.solventSortOrder = SolventSortOrder.BY_HES_SCORES;
		Collections.sort(candidates);
		logCandidates(candidates, SolventSortOrder.BY_HES_SCORES);
		
		GeneralData.solventSortOrder = SolventSortOrder.BY_HANSEN_DISTANCE_TO_THE_SOLVENT;
		Collections.sort(candidates);
		logCandidates(candidates, SolventSortOrder.BY_HANSEN_DISTANCE_TO_THE_SOLVENT);			
	}
	
	public void prepareNormalisation()
	{
		Double[] values = new Double[candidates.size() * GeneralData.numberOfFeatures];
		
		int j=0;
		for (Candidate candidate : candidates)
			for (int i=0; i < candidate.getSolvent().getFeatureValuesAsdouble().length; i++)
				values[j++] = candidate.getSolvent().getFeatureValuesAsdouble()[i];
		
		minFeatureValue = Collections.min(Arrays.asList(values));
		maxFeatureValue = Collections.max(Arrays.asList(values));
	}
	
	private void logCandidates(ArrayList<Candidate> candidates, SolventSortOrder solventSortOrder)
	{
		prepareNormalisation();
		
		// Absolute data.
		String logString = "Candidates, sorted " + solventSortOrder.toString() + ", absolute data :\n**********\n";
		logString += getHeader();
		for (Candidate candidate: candidates)
		{
			logString += getMetaData(candidate);
			
			for (Feature feature : candidate.getSolvent().getFeatures())
			{
				logString += GeneralData.decimalFormat(feature.getValue()) + "\t";
			}
			
			// Log the Hansen score between The Solvent and the current solvent.
			logString += GeneralData.decimalFormat(candidate.getHansenDistanceToTheSolvent()) + "\n";
		}
				
		// Normalised data.
		logString += "\nCandidates, sorted " + solventSortOrder.toString() + ", normalised data :\n**********\n";
		logString += getHeader();
		for (Candidate candidate: candidates)
		{
			logString += getMetaData(candidate);
			
			// Apply normalisation.
			for (Feature feature : candidate.getSolvent().getFeatures())
			{
				double normalisedValue = MathManager.normalize(feature, minFeatureValue, maxFeatureValue);
				logString += GeneralData.decimalFormat(normalisedValue) + "\t";
			}
			
			// Log the Hansen score between The Solvent and the current solvent.
			logString += GeneralData.decimalFormat(candidate.getHansenDistanceToTheSolvent()) + "\n";
		}
//		SussolLogger.getInstance().info(logString);
	}
	
	private String getHeader()
	{
		String headerString = "";
		
		// First log the header.
		headerString += "Name\t";
		headerString += "Cas Number\t";
		headerString += "E Score\t";
		headerString += "E Color\t";
		headerString += "H Score\t";
		headerString += "H Color\t";
		headerString += "S Score\t";
		headerString += "S Color\t";
		headerString += "EHS Color\t";
		
		for (Feature feature : GeneralData.theSolvent.getFeatures())
			headerString += feature.getName() + "\t";
		headerString += "Hansen Distance to " + GeneralData.theSolvent.getName() + "\n";
		
		return headerString;
	}
	
	private String getMetaData(Candidate candidate)
	{
		String metaDataString = "";
		
		metaDataString += candidate.getSolvent().getName() + "\t";
		metaDataString += candidate.getSolvent().getCasNumber() + "\t";
		metaDataString += candidate.getSolvent().getEhsData().getEnvironmentScore() + "\t";
		metaDataString += candidate.getSolvent().getEhsData().getEnvironmentColor() + "\t";
		metaDataString += candidate.getSolvent().getEhsData().getHealthScore() + "\t";
		metaDataString += candidate.getSolvent().getEhsData().getHealthColor() + "\t";
		metaDataString += candidate.getSolvent().getEhsData().getSafetyScore() + "\t";
		metaDataString += candidate.getSolvent().getEhsData().getSafetyColor() + "\t";
		metaDataString += candidate.getSolvent().getEhsData().getEhsColor() + "\t";		
		
		return metaDataString;
	}
	
	public ArrayList<CandidateChartData> getCandidatesChartData()
	{
		ArrayList<CandidateChartData> chartData = new ArrayList<CandidateChartData>();
		
		GeneralData.solventSortOrder = SolventSortOrder.BY_NAME;
		Collections.sort(candidates);
		
		for (Candidate candidate: candidates)
			chartData.add(candidate.getChartData());
		
		return chartData;
	}
	
	public ArrayList<CandidateChartData> getCandidatesChartDataNormalised()
	{
		ArrayList<CandidateChartData> chartData = new ArrayList<CandidateChartData>();
		
		GeneralData.solventSortOrder = SolventSortOrder.BY_NAME;
		Collections.sort(candidates);
		
		for (Candidate candidate: candidates)
			chartData.add(candidate.getChartDataNormalised());
		
		return chartData;
	}
	
	public ObservableList<String> getObservableFeatureNames()
	{
		ObservableList<String> result = FXCollections.observableArrayList();
		
		for (String featureName : GeneralData.featureNames)
			result.add(featureName);
		
		return result;
	}
	
	public ObservableList<Label> getObservableCandidateNames()
	{
		ObservableList<Label> result = FXCollections.observableArrayList();
		
		GeneralData.solventSortOrder = SolventSortOrder.BY_HES_SCORES;
		Collections.sort(candidates);
		
		for (int i=0; i < candidates.size(); i++)
			result.add(new Label(candidates.get(i).getSolvent().getName()));
		
		return result;
	}
	
	public void setNeighborsOfTheSolvent(ArrayList<Neighbor> neighborsOfTheSolvent) 
	{		
		CandidatesManager.neighborsOfTheSolvent = neighborsOfTheSolvent;
		CandidatesManager.neighborsOfTheSolvent.removeIf(x -> x.getNeighborCounter() < upperLimit); 
	}

	public void setUpperLimit(double upperLimit) 
	{
		CandidatesManager.upperLimit = upperLimit;
	}

	public Candidate getCandidate(String candidateName)
	{
		for (Candidate candidate: candidates)
			if (candidate.getSolvent().getName().equalsIgnoreCase(candidateName))
				return candidate;
			
		return null;
	}
	
	public ArrayList<Candidate> getCandidates() { return candidates; }
}

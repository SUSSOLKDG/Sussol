package model.candidates;

import general.GeneralData;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import model.features.Feature;
import model.solvents.Solvent;

public class Candidate implements Comparable<Candidate>
{
	private Solvent solvent;	
	private String name;
	private double hansenDistanceToTheSolvent;
	private CandidateChartData chartData;
	private CandidateChartData chartDataNormalised;

	public Candidate(Solvent solvent, double hansenDistanceToTheSolvent, String lineColorStyle, String textColorStyle) 
	{		
		this.solvent = solvent;
		name = solvent.getName();
		this.hansenDistanceToTheSolvent = hansenDistanceToTheSolvent;
		
		chartData = new CandidateChartData(name, getChartValues(), lineColorStyle, textColorStyle);
		
		chartDataNormalised = new CandidateChartData(name, getChartValuesNormalised(), lineColorStyle, textColorStyle);
	}
	
	public Series<String, Double> getChartValues()
	{
		Series<String, Double> candidateSeries = new Series<String, Double>();
			
		for (int i=0; i < solvent.getFeatureValuesAsdouble().length; i++)
			if (! GeneralData.featureNames.get(i).contains("_Copy"))
				candidateSeries.getData().add(new Data<String, Double>(GeneralData.featureNames.get(i), solvent.getFeatureValuesAsdouble()[i]));
		
		return candidateSeries;
	}
	
	public Series<String, Double> getChartValuesNormalised()
	{
		Series<String, Double> candidateSeries = new Series<String, Double>();
			
		for (int i=0; i < solvent.getFeatureValuesAsdouble().length; i++)
			if (! GeneralData.featureNames.get(i).contains("_Copy"))
				candidateSeries.getData().add(new Data<String, Double>(GeneralData.featureNames.get(i), solvent.getFeatureValuesAsdoubleNormalised()[i]));
		
		return candidateSeries;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public int compareTo(Candidate otherCandidate) 
	{
		switch (GeneralData.solventSortOrder)
		{
			case BY_HES_SCORES:
				int healthComparison1 = Integer.compare(solvent.getEhsData().getHealthScoreInt(),  otherCandidate.solvent.getEhsData().getHealthScoreInt());
				if (healthComparison1 != 0)
					return healthComparison1;
				int environmentComparison1 = Integer.compare(solvent.getEhsData().getEnvironmentScoreInt(),  otherCandidate.solvent.getEhsData().getEnvironmentScoreInt());
				if (environmentComparison1 != 0)
					return environmentComparison1;
				return Integer.compare(solvent.getEhsData().getSafetyScoreInt(),  otherCandidate.solvent.getEhsData().getSafetyScoreInt());
				
			case BY_HANSEN_DISTANCE_TO_THE_SOLVENT:
				return Double.compare(hansenDistanceToTheSolvent, otherCandidate.hansenDistanceToTheSolvent);
				
			case BY_NAME:
				return solvent.getName().compareToIgnoreCase(otherCandidate.getName());
		}
		return 0;
	}
	
	public String getName()								{ return name; }
	public Solvent getSolvent() 						{ return solvent; }
	public double getHansenDistanceToTheSolvent() 		{ return hansenDistanceToTheSolvent; }
	public CandidateChartData getChartData() 			{ return chartData; }
	public CandidateChartData getChartDataNormalised()	{ return chartDataNormalised; }
	
}

package presenter;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import model.candidates.Candidate;
import model.candidates.CandidateChartData;
import model.candidates.CandidatesManager;
import view.CandidatesView;

public class CandidatesPresenter 
{
	private CandidatesView candidatesView;

	public CandidatesPresenter(CandidatesView candidatesView) 
	{
		this.candidatesView = candidatesView;
	}

	public ArrayList<CandidateChartData> getCandidatesChartData() 
	{
		return CandidatesManager.getInstance().getCandidatesChartData();
	}

	public ArrayList<CandidateChartData> getCandidatesChartDataNormalised() 
	{
		return CandidatesManager.getInstance().getCandidatesChartDataNormalised();
	}
	
	public ObservableList<Label> getCandidatesNames() 
	{
		return CandidatesManager.getInstance().getObservableCandidateNames();
	}
	
	public ArrayList<Candidate> getCandidates() 
	{
		return CandidatesManager.getInstance().getCandidates();
	}
}

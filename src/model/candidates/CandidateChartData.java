package model.candidates;

import javafx.scene.chart.XYChart.Series;

public class CandidateChartData 
{
	private String lineName;
	private Series<String, Double> lineData;	// Feature value and name.
	private String lineColorStyle;
	private String textColorStyle;
	
	public CandidateChartData(String lineName, Series<String, Double> lineData, String lineColorStyle, String textColorStyle) 
	{
		this.lineName = lineName;
		this.lineData = lineData;
		this.lineColorStyle = lineColorStyle;
		this.textColorStyle = textColorStyle;
	}

	public String getLineName() 				{ return lineName; }
	public Series<String, Double> getLineData()	{ return lineData; }
	public String getLineColorStyle()			{ return lineColorStyle; }
	public String getTextColorStyle()			{ return textColorStyle; }
}

package model.features;

public class FeatureRange 
{
	private String name;
	private Double minValue;
	private Double maxValue;
	
	public FeatureRange(String name)
	{
		this.name = name;
	}
	
	public FeatureRange(String name, Double minValue, Double maxValue) 
	{
		this.name = name;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public Double getMinValue() 				{ return minValue; }
	public void setMinValue(Double minValue)	{ this.minValue = minValue; }
	public Double getMaxValue() 				{ return maxValue; }
	public void setMaxValue(Double maxValue) 	{ this.maxValue = maxValue; }
	public String getName() 					{ return name; }
	
}

package model.features;

public class FeatureMetaData 
{
	private String name; 
	private double min;
	private double max;
	
	public FeatureMetaData(String name, double min, double max) 
	{
		this.name = name;
		this.min = min;
		this.max = max;
	}

	@Override
	public String toString() 
	{
		return "FeatureMetaData [name=" + name + ", min=" + min + ", max=" + max + "]";
	}

	public String getName()	{ return name; }
	public double getMin() 	{ return min; }
	public double getMax() 	{ return max; }

}

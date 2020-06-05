package model.features;

public class Feature 
{
	private String name;
	private Double value;
	
	public Feature(String name, Double value) 
	{
		this.name = name;
		this.value = value;
	}

	public Feature clone()
	{
		return new Feature(this.name, this.value);
	}
	
	public String getName()				{ return name; }
	public void setName(String name)	{ this.name = name; }
	public Double getValue() 			{ return value; }
	public void setValue(Double value)	{ this.value = value; }

	@Override
	public String toString() 
	{
		return "Feature [name=" + name + ", value=" + value + "]";
	}
	
	
}

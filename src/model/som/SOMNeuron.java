package model.som;

import general.GeneralData;

public class SOMNeuron 
{
	private int x;
	private int y;
	private int output;
	private double[] weights;

	public int getX() 							{ return x; }
	public void setX(int x) 					{ this.x = x; }
	public int getY() 							{ return y; }
	public void setY(int y) 					{ this.y = y; }
	public int getOutput() 						{ return output; }
	public void setOutput(int output) 			{ this.output = output; }
	public double[] getWeights()              	{ return weights.clone(); }
	public void setWeights(double[] weights)	{ this.weights = weights.clone(); }
	
	public SOMNeuron()
	{		
	}
	
	public double getDistance(SOMNeuron rhs)
	{
		double dx = rhs.x - x;
		double dy = rhs.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public void updateWeight(int j, double weight) 
	{
		this.weights[j] = weight;
	}

	public double getWeight(int j) 
	{
		return this.weights[j];
	}
	
	@Override
	public String toString() 
	{
		String localString = "SOMNeuron [x=" + x + ", y=" + y + ", output=" + output + ",\tweights= ";
		
		for (int i=0; i< weights.length; i++)
			localString += GeneralData.decimalFormat(weights[i]) + "\t";
		
		return localString;
	}

}

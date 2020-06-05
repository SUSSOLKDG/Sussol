package model.solvents;

import java.util.ArrayList;

public class HansenData 
{
	private double hansenDeltaD = 0.0;
	private double hansenDeltaP = 0.0;
	private double hansenDeltaH = 0.0;
	
	private ArrayList<Double> hansenDataVector;
	
	public void makeVector() 
	{
		hansenDataVector = new ArrayList<Double>();
		hansenDataVector.add(hansenDeltaD);
		hansenDataVector.add(hansenDeltaP);
		hansenDataVector.add(hansenDeltaH);
	}
	
	public double[] getHansenDataAsArray()
	{
		double[] result = new double[3];
		
		result[0] = hansenDeltaD;
		result[1] = hansenDeltaP;
		result[2] = hansenDeltaH;
		
		return result;
	}
	
	public double getHansenDeltaD() 					{ return hansenDeltaD; }
	public void setHansenDeltaD(double hansenDeltaD)	{ this.hansenDeltaD = hansenDeltaD; }
	public double getHansenDeltaP() 					{ return hansenDeltaP; }
	public void setHansenDeltaP(double hansenDeltaP) 	{ this.hansenDeltaP = hansenDeltaP; }
	public double getHansenDeltaH() 					{ return hansenDeltaH; }
	public void setHansenDeltaH(double hansenDeltaH) 	{ this.hansenDeltaH = hansenDeltaH; }
	public ArrayList<Double> getHansenDataVector() 		{ return hansenDataVector; }
	
}

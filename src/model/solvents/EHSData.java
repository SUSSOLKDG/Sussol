package model.solvents;

public class EHSData 
{
	private String environmentScore;
	private String environmentColor;
	private String healthScore;
	private String healthColor;
	private String safetyScore;
	private String safetyColor;
	
	private String ehsColor;
	
	private int environmentScoreInt;
	private int healthScoreInt;
	private int safetyScoreInt;

	public String getEnvironmentScore() { return environmentScore; }
	public String getEnvironmentColor()	{ return environmentColor; }
	public String getHealthScore() 		{ return healthScore; }
	public String getHealthColor() 		{ return healthColor; }
	public String getSafetyScore() 		{ return safetyScore; }
	public String getSafetyColor() 		{ return safetyColor; }
	public String getEhsColor()			{ return ehsColor; }
	
	public int getEnvironmentScoreInt() { return environmentScoreInt; }
	public int getHealthScoreInt() 		{ return healthScoreInt; }
	public int getSafetyScoreInt() 		{ return safetyScoreInt; }

	public void setEnvironmentScore(String environmentScore) 	{ this.environmentScore = environmentScore; }
	public void setEnvironmentColor(String environmentColor) 	{ this.environmentColor = environmentColor; }
	public void setHealthScore(String healthScore) 				{ this.healthScore = healthScore; }
	public void setHealthColor(String healthColor) 				{ this.healthColor = healthColor; }
	public void setSafetyScore(String safetyScore) 				{ this.safetyScore = safetyScore; }
	public void setSafetyColor(String safetyColor) 				{ this.safetyColor = safetyColor; }
	public void setEhsColor(String ehsColor)					{ this.ehsColor = ehsColor; }
	
	public void setEnvironmentScoreInt() 						{ this.environmentScoreInt = Integer.parseInt(environmentScore); }
	public void setHealthScoreInt() 							{ this.healthScoreInt = Integer.parseInt(healthScore); }
	public void setSafetyScoreInt() 							{ this.safetyScoreInt = Integer.parseInt(safetyScore); }
}

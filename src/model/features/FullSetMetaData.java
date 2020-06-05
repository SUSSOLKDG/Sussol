package model.features;

import java.util.ArrayList;

import utilities.SussolLogger;

public class FullSetMetaData 
{
	private static FullSetMetaData instance = null;
	
	private ArrayList<FeatureMetaData> featureMetaData;
	
	private FullSetMetaData() 
    {
		featureMetaData = new ArrayList<FeatureMetaData>();
		
		featureMetaData.add(new FeatureMetaData("Antoine A", 6.606D, 8.418D));
		featureMetaData.add(new FeatureMetaData("Antoine B", 729.2D, 2575.6D));
		featureMetaData.add(new FeatureMetaData("Antoine C", 72.5D, 262D));
		featureMetaData.add(new FeatureMetaData("Autoignition Temperature (°C)", 100D, 982D));
		featureMetaData.add(new FeatureMetaData("Boiling Point (°C)", -42.5D, 377.88D));
		featureMetaData.add(new FeatureMetaData("Density (25°C kg/L)", 0.283D, 11.02D));
		featureMetaData.add(new FeatureMetaData("Dielectric Constant (20°C)", 1.8D, 22600D));
		featureMetaData.add(new FeatureMetaData("Flash Point (°C)", -83D, 1000D));
		featureMetaData.add(new FeatureMetaData("Hansen Delta D (MPa1/2)", 6.5D, 21.5D));
		featureMetaData.add(new FeatureMetaData("Hansen Delta H (MPa1/2)", 0D, 28.4D));
		featureMetaData.add(new FeatureMetaData("Hansen Delta P (MPa1/2)", 0D, 26.2D));
		featureMetaData.add(new FeatureMetaData("Log P Octanol Water (20°C)", -2.61D, 9.7D));
		featureMetaData.add(new FeatureMetaData("Log S", -22.91D, 4D));
		featureMetaData.add(new FeatureMetaData("Melting Point (°C)", -189D, 83D));
		featureMetaData.add(new FeatureMetaData("Molar Volume", 31.9D, 476.8D));
		featureMetaData.add(new FeatureMetaData("Molecular Weight", 30D, 434D));
		featureMetaData.add(new FeatureMetaData("Refractive Index (20°C)", 1.252D, 20.7D));
		featureMetaData.add(new FeatureMetaData("Relative Evaporation Rate (BuAc=100)", 0D, 24824D));
		featureMetaData.add(new FeatureMetaData("Relative Vapour Density (Air=1)", 0.001D, 287D));
		featureMetaData.add(new FeatureMetaData("Solubility Water (20°C g/L)", -1D, 1630D));
		featureMetaData.add(new FeatureMetaData("Surface Tension (25°C mN/m)", 0.000000382D, 85.5D));
		featureMetaData.add(new FeatureMetaData("Vapour Pressure (25°C mmHg)", 0D, 7150D));
		featureMetaData.add(new FeatureMetaData("Viscosity (25°C mPa.s)", 0.00945D, 945D));  
    }
	   
    public static FullSetMetaData getInstance() 
    {
       if (instance == null) 
       {
    	   instance = new FullSetMetaData();
       }
       return instance;
    }
	
	public FeatureMetaData getFeatureMetaData(String featureName)
	{
		FeatureMetaData result = new FeatureMetaData("NoFeature", 1, 1);
		
		for (FeatureMetaData featureMetaDatum : featureMetaData)
			if (featureMetaDatum.getName().equalsIgnoreCase(featureName))
				result = featureMetaDatum;
		
		if (result.getName().equalsIgnoreCase("NoFeature"))
//			SussolLogger.getInstance().info("\t\t@@@@@@@@@@@@@@@@@@@@@@@@@ : Feature " + featureName + " not found in full set.");
			;
		
		return result;
	}
}

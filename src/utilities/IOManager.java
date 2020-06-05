package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import com.github.chen0040.data.frame.BasicDataFrame;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;

import general.GeneralData;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BubbleChart;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import model.features.Feature;
import model.solvents.EHSData;
import model.solvents.Solvent;

public class IOManager 
{
	public static DataFrame getSolventDataFrameFromFile(String fileName)
	{
		DataFrame result = new BasicDataFrame();
		Path path = Paths.get(fileName);			
		
		// Fill DataFrame, dynamically.
		try 
		{
			List<String> lines = Files.readAllLines(path);		// Read all lines from csv file.
			
			String[] columnNames = lines.get(0).split(";");		// Get all column names (header line of csv file).
		
			int numberOfMetaDataColumns = 9;												// Name, Casnumber and EHS data are metadata.
			GeneralData.numberOfFeatures = columnNames.length - numberOfMetaDataColumns;	
			GeneralData.numberOfSolvents = lines.size() - 1;								// Header line must be skipped.
			
			GeneralData.solventNames = new ArrayList<String>();
			GeneralData.solvents = new HashMap<String, Solvent>();
			
			GeneralData.featureNames = new ArrayList<String>();
			for (int i = numberOfMetaDataColumns; i < columnNames.length; i++)
			{
				GeneralData.featureNames.add(columnNames[i]);
			}
			
			int lineCounter = 0;
			for (String line : lines)
			{
				if (lineCounter > 0)												// Skip header line.
				{
					String[] values = line.split(";");
					
					DataRow row = result.newRow();
					row.setCategoricalTargetCell(columnNames[1], values[1]);		// Set casNumber in MIT dataframe as target cell.
																					// The casNumber is unique, the names may differ.
					
					GeneralData.solventNames.add(values[0]);
					
					Solvent solvent = new Solvent(lineCounter);
					
					solvent.setName(values[0]);
					solvent.setCasNumber(values[1]);
										
					EHSData ehsData = new EHSData();
					ehsData.setEnvironmentScore(values[2]);
					ehsData.setEnvironmentColor(values[3]);
					ehsData.setHealthScore(values[4]);
					ehsData.setHealthColor(values[5]);
					ehsData.setSafetyScore(values[6]);
					ehsData.setSafetyColor(values[7]);
					ehsData.setEhsColor(values[8]);
					
					ehsData.setEnvironmentScoreInt();
					ehsData.setHealthScoreInt();
					ehsData.setSafetyScoreInt();
					
					solvent.setEhsData(ehsData);
					
					ArrayList<Feature> features = new ArrayList<Feature>();
					String featureName;
					Double featureValue;
					
					for (int i = numberOfMetaDataColumns; i < columnNames.length; i++)
					{				
						featureName = columnNames[i];
						featureValue = Double.parseDouble(values[i]);
						
						row.setCell(featureName, featureValue);						// Set value in MIT dataframe.
						
						Feature feature = new Feature(featureName, featureValue);
						features.add(feature);
					}
					solvent.setFeatures(features);
					
					solvent.initialiseHansenData();
					
					result.addRow(row);
					GeneralData.solvents.put(values[1], solvent);
				}
				
				lineCounter++;
			}
		
			// Call lock to perform aggregation and prevent further addition of new rows.			
			result.lock();
			
			Collections.sort(GeneralData.solventNames);
		} 
		catch (IOException iOE) { iOE.printStackTrace(); }
		
		return result;
	}
	
	public static void saveChart(BubbleChart<Integer, Integer> clustersBubbleChart) 
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save chart to file");
		File selectedFile = fileChooser.showSaveDialog(null);
		
		if (selectedFile != null)
		{
			WritableImage image = clustersBubbleChart.snapshot(new SnapshotParameters(), null);
			File file = new File(selectedFile.getAbsolutePath());
		    try
		    {
		        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		    } 
		    catch (IOException e) { e.printStackTrace(); }
		}
	}
}

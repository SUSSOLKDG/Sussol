package presenter.filtering.pre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.github.chen0040.data.frame.BasicDataFrame;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.data.frame.InputDataColumn;

import general.GeneralData;
import model.solvents.Solvent;
import utilities.SussolLogger;
import view.filtering.pre.PreFilteringView;

public class PreFilteringPresenter 
{
	private PreFilteringView preFilteringView;

	public PreFilteringPresenter(PreFilteringView preFilteringView) 
	{
		this.preFilteringView = preFilteringView;
	}

	public void copyFeatures(ArrayList<String> featuresToBeCopied)
	{
		// TODO fix prefilter COPY bug.
		
		Collections.sort(featuresToBeCopied);
		
		ArrayList<String> newColumnNames = new ArrayList<String>();
		
		// Copy the original column names.
		for (InputDataColumn column : GeneralData.solventDataFrame.getInputColumns())
			newColumnNames.add(column.getColumnName());
		
		// Make for each selected feature a new InputDataColumn.
		// Add the new column to the dataset.
		// Add the new name to the column names.
		// Add the new feature(s) to the solvent HashMap of GeneralData.
		// Add the new feature(s) to The Solvent.
		// Ad the new featureNames to GeneralData.featureNames.
		
		String currentFeatureName = "";
		int copyCounter = 1;
		
		for (int i=0; i < featuresToBeCopied.size(); i++)
		{
			InputDataColumn column = new InputDataColumn();
			
			// When a feature has to be copied more than once.
			if (currentFeatureName.equalsIgnoreCase(featuresToBeCopied.get(i)))
				copyCounter++;
			
			String featureName = featuresToBeCopied.get(i);
			String newFeatureName = featureName + " Copy" + "_" + copyCounter;
			
			// Update the solvent DataFrame for the SOM.
			column.setColumnName(newFeatureName);
			newColumnNames.add(newFeatureName);
			
			GeneralData.solventDataFrame.getInputColumns().add(column);
			GeneralData.numberOfFeatures++;
			
			// The solvent HashMap is used in Sussol code.
			for (HashMap.Entry<String, Solvent> solvent : GeneralData.solvents.entrySet())
				solvent.getValue().copyFeature(featureName, newFeatureName);
			
			// Copy featurenames to GeneralData.featureNames
			GeneralData.featureNames.add(newFeatureName);
			
			currentFeatureName = featureName;
		}
		
		// Copy the rows.
		for (DataRow row : GeneralData.solventDataFrame.rows())
		{
			row.setColumnNames(newColumnNames);
			
			for (int i=0; i < newColumnNames.size(); i++)
			{
				if (newColumnNames.get(i).contains(" Copy"))
				{
					String originalColumnName = newColumnNames.get(i).substring(0, newColumnNames.get(i).indexOf(" Copy"));
					row.setCell(newColumnNames.get(i), row.getCell(originalColumnName));
				}
				else
					row.setCell(newColumnNames.get(i), row.getCell(newColumnNames.get(i)));
			}
		}
		
//		SussolLogger.getInstance().info("Data set after copying :");
		GeneralData.logSolventsDataFrame();
		GeneralData.logSolventHashMap();
	}
	
	public void removeFeatures(ArrayList<String> featuresToBeRemoved)
	{
		// Make new DataFrame.
		DataFrame newDataFrame = new BasicDataFrame();
		
		// Make Output columns.
		for (int i=0; i < GeneralData.solventDataFrame.getOutputColumns().size(); i++)
			newDataFrame.getOutputColumns().add(GeneralData.solventDataFrame.getOutputColumns().get(i));
		
		// Make Input columns, but leave the selected columns out.
		int localNumberOfColumns = 0;
		for (int i=0; i < GeneralData.solventDataFrame.getInputColumns().size(); i++)
			if (! featuresToBeRemoved.contains(GeneralData.solventDataFrame.getInputColumns().get(i).getColumnName()))
			{
				newDataFrame.getInputColumns().add(GeneralData.solventDataFrame.getInputColumns().get(i));
				localNumberOfColumns++;
			}
		GeneralData.numberOfFeatures = localNumberOfColumns;
		
		// Copy the rows, again leaving the entries in the selected columns out.	
		for (DataRow row : GeneralData.solventDataFrame.rows())
		{
			DataRow newRow = newDataFrame.newRow();
			
			newRow.setCategoricalTargetCell("Cas Number", "" + row.categoricalTarget());

			// Copy the values of the features that are not selected.
			for (int i=0; i < GeneralData.solventDataFrame.getInputColumns().size(); i++)
				if (! featuresToBeRemoved.contains(GeneralData.solventDataFrame.getInputColumns().get(i).getColumnName()))
					newRow.setCell(row.getColumnNames().get(i), row.getCell(row.getColumnNames().get(i)));
			
			newDataFrame.addRow(newRow);
		}			
		
		GeneralData.solventDataFrame.unlock();
		GeneralData.solventDataFrame = newDataFrame;
		GeneralData.solventDataFrame.lock();		
		
		// Remove features from solvents.
		// First from the hashmap 'solvents'.
		for (HashMap.Entry<String, Solvent> solvent : GeneralData.solvents.entrySet())
			for (String featureName : featuresToBeRemoved)
				solvent.getValue().removeFeature(featureName);
		
		// Remove feature names from GeneralData.featureNames
		for (String featureName : featuresToBeRemoved)
			GeneralData.featureNames.removeIf(s -> s.equalsIgnoreCase(featureName));
		
//		SussolLogger.getInstance().info("Data set after removing :");
		GeneralData.logSolventsDataFrame();
		GeneralData.logSolventHashMap();
	}
}

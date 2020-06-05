package view.filtering.pre;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.chen0040.data.frame.DataRow;

import general.GeneralData;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

@SuppressWarnings("rawtypes")
public class DataFrameSheet extends TableView
{
	private PreFilteringView filteringView;
	
	private ObservableList<String> selectedFeatures = FXCollections.observableArrayList();
	
	@SuppressWarnings("unchecked")
	public void initialize()
	{		
		// Add columns.
		for (int i = 0; i < GeneralData.solventDataFrame.getInputColumns().size(); i++) 
		{
		    int index = i;
		    
		    TableColumn<ObservableList<String>, String> column = new TableColumn<>(GeneralData.solventDataFrame.getInputColumns().get(i).getColumnName());
		    column.setCellValueFactory
		    (
	    		param 
	    		->
	            new ReadOnlyObjectWrapper<>(param.getValue().get(index))
		    );
		    this.getColumns().add(column);
		}
		
		// Add datarows.
		Iterator<DataRow> iterator = GeneralData.solventDataFrame.iterator();
		while (iterator.hasNext())
		{
			List<String> values = new ArrayList<String>();
			
			DataRow row = iterator.next();
			for (int i=0; i < GeneralData.solventDataFrame.getInputColumns().size(); i++)
			{
				values.add(GeneralData.decimalFormat(row.getCell(GeneralData.solventDataFrame.getInputColumns().get(i).getColumnName())));
			}
			getItems().add(FXCollections.observableArrayList(values));
		}
		
		initialiseListener();
	}
	
	@SuppressWarnings("unchecked")
	private void initialiseListener() 
	{
		setEditable(false);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getSelectionModel().setCellSelectionEnabled(true);
        
		getFocusModel().focusedCellProperty().addListener
        (
    		(obs, oldVal, newVal) 
    		-> 
    		{
    			TablePosition<String, String> tablePosition = (TablePosition<String, String>) newVal;
    			if (tablePosition.getTableColumn() != null)
    			{
    				switch (GeneralData.preFilteringMode)
    				{
						case COPY:
							selectedFeatures.add(tablePosition.getTableColumn().getText());
							break;
						case REMOVE:
							if (! selectedFeatures.contains(tablePosition.getTableColumn().getText()))
								selectedFeatures.add(tablePosition.getTableColumn().getText());
							break;
						default:
							break;
    				}
    				
    				filteringView.refreshFeatureList(selectedFeatures);
	    		}
    		}
		);
	}
	
	public void reset()
	{
		getSelectionModel().clearSelection();
	}
	
	public void setFilteringView(PreFilteringView filteringView)	{ this.filteringView = filteringView; }
}

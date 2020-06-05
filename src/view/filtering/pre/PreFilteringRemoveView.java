package view.filtering.pre;

import java.util.ArrayList;
import java.util.Collections;

import application.Main;
import general.GeneralData;
import general.ScreenType;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import presenter.filtering.pre.PreFilteringPresenter;
import utilities.SussolLogger;
import utilities.UIManager;
import view.filtering.pre.DataFrameSheet;

public class PreFilteringRemoveView extends PreFilteringView
{
	private PreFilteringPresenter filteringPresenter;
	
	@FXML private GridPane filterGrid;
	@FXML private GridPane buttonGrid;
		@FXML private Button removeButton;
		@FXML private Button cancelButton;
	@FXML private GridPane dataGrid;
		@FXML private DataFrameSheet dataFrameSheet;
		@FXML private GridPane listGrid;
		@FXML private ListView<String> featuresToBeRemoved;
	
	@FXML 
	public void initialize()
	{
		filteringPresenter = new PreFilteringPresenter(this);
		
		filterGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 100));
		filterGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 10));
		filterGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 90));
		
		buttonGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));
		buttonGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));
		buttonGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 100));
		
		dataGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 75));
		dataGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 25));
		dataGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 100));
		
		listGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 100));
		listGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 10));
		listGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 90));
		listGrid.setPadding(new Insets(5, 10, 5, 10));
		
		dataFrameSheet.setFilteringView(this);
		dataFrameSheet.initialize();
		
		featuresToBeRemoved.setOnMouseReleased
		(
			new EventHandler<MouseEvent>() 
			{
			    @Override
			    public void handle(MouseEvent mouseEvent) 
			    {			    				    
			        if (mouseEvent.isControlDown()) 
			        	featuresToBeRemoved.getItems().remove(featuresToBeRemoved.getSelectionModel().getSelectedItem());
			    }
			}
        );
		
		GeneralData.preFilteringMode = GeneralData.PreFilteringMode.REMOVE;
	}	
	
	private ArrayList<String> getSelectedFeatureNames()
	{
		ArrayList<String> selectedFeatureNames = new ArrayList<String>();
		
		for (String feature : featuresToBeRemoved.getItems())
			selectedFeatureNames.add(feature);
		
		Collections.sort(selectedFeatureNames);
		
		return selectedFeatureNames;
	}
	
	@FXML void onRemoveButtonClicked()
	{
		String logString = "\n*********************************\n\n**** You chose to remove the following features:\n\n";
		for (String feature : featuresToBeRemoved.getItems())
		{
			logString += "\t" + feature + "\n";
			
		}
//		SussolLogger.getInstance().info(logString);
		
		filteringPresenter.removeFeatures(getSelectedFeatureNames());
		
//		Main.root.getCenter().setVisible(false);
//		Main.showScreen(ScreenType.ANALYSIS);
	}
	
	@FXML public void onCancelButtonClicked() 
	{
//		SussolLogger.getInstance().info("\n*********************************\n\n**** You chose not to remove any features.\n");
		
//		Main.root.getCenter().setVisible(false);
//		Main.showScreen(ScreenType.ANALYSIS);
	}
	
	@Override
	public void refreshFeatureList(ObservableList<String> items) 
	{
		featuresToBeRemoved.setItems(items);
	}
}

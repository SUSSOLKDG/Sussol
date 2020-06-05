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

public class PreFilteringCopyView extends PreFilteringView
{
	private PreFilteringPresenter filteringPresenter;
	
	@FXML private GridPane filterGrid;
	@FXML private GridPane buttonGrid;
		@FXML private Button copyButton;
		@FXML private Button cancelButton;
	@FXML private GridPane dataGrid;
		@FXML private DataFrameSheet dataFrameSheet;
		@FXML private GridPane listGrid;
		@FXML private ListView<String> featuresToBeCopied;
	
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
		listGrid.setPadding(new Insets(10, 15, 10, 15));
		
		dataFrameSheet.setFilteringView(this);
		dataFrameSheet.initialize();
		
		featuresToBeCopied.setOnMouseReleased
		(
			new EventHandler<MouseEvent>() 
			{
			    @Override
			    public void handle(MouseEvent mouseEvent) 
			    {			    				    
			        if (mouseEvent.isShiftDown()) 
			        	featuresToBeCopied.getItems().remove(featuresToBeCopied.getSelectionModel().getSelectedItem());
			    }
			}
        );
		
		GeneralData.preFilteringMode = GeneralData.PreFilteringMode.COPY;
	}	
	
	private ArrayList<String> getSelectedFeatureNames()
	{
		ArrayList<String> selectedFeatureNames = new ArrayList<String>();
		
		for (String feature : featuresToBeCopied.getItems())
			selectedFeatureNames.add(feature);
		
		Collections.sort(selectedFeatureNames);
		
		return selectedFeatureNames;
	}
		
	@FXML void onCopyButtonClicked()
	{
		if (featuresToBeCopied.getItems().size() > 0)
		{
			String logString = "\n*********************************\n\n**** You chose to copy the following features:\n\n";		
			for (String feature : featuresToBeCopied.getItems())
				logString += "\t" + feature + "\n";
//			SussolLogger.getInstance().info(logString);
			
			filteringPresenter.copyFeatures(getSelectedFeatureNames());
		}
		
//		Main.root.getCenter().setVisible(false);
//		Main.showScreen(ScreenType.ANALYSIS);
	}
	
	@FXML public void onCancelButtonClicked() 
	{
//		SussolLogger.getInstance().info("\n*********************************\n\n**** You chose not to copy any features.\n");
		
//		Main.root.getCenter().setVisible(false);
//		Main.showScreen(ScreenType.ANALYSIS);
	}
	
	@Override
	public void refreshFeatureList(ObservableList<String> items) 
	{
		featuresToBeCopied.setItems(items);
	}
}

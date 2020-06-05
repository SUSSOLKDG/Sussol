package view.filtering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import general.GeneralData;
import general.GeneralData.DistanceData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.features.Feature;
import model.features.FeatureRange;
import model.solvents.Solvent;
import presenter.filtering.post.AllSolventsPresenter;
import utilities.UIManager;
import view.filtering.post.FeatureRangeGridPane;

public class AllSolventsView implements FilteringView
{
	private AllSolventsPresenter allSolventsPresenter;
	
	private SpinnerValueFactory<Integer> solventNumberFactory;
	
	private int currentBubbleNumber;
	private ArrayList<Solvent> startSolvents;
	private ArrayList<Solvent> filteredSolvents;
	
	private DistanceData currentDistanceData;
	private Boolean filteringIsOn;
	
	@FXML private GridPane grid;
	@FXML private Button filterScreenButton;
	@FXML private Label nrOfSolventsLabel;
	@FXML private Spinner<Integer> solventNumberSpinner;
	@FXML private TextField solventNameTextField;
	@FXML private BubbleChart<Integer, Integer> solventsChart;
	
	@FXML 
	public void initialize()
	{	
		allSolventsPresenter = new AllSolventsPresenter();
		
		// Main grid
		grid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 15));
		grid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 20));
		grid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 15));
		grid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));
		
		grid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 10));
		grid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 90));
		
		solventsChart.setAnimated(false);
		solventsChart.getXAxis().setTickLabelsVisible(false);
		solventsChart.getYAxis().setTickLabelsVisible(false);
		solventsChart.getData().clear();
		
		filteringIsOn = false;
		filterScreenButton.setDisable(false);
		
		nrOfSolventsLabel.setText(Integer.toString(startSolvents.size()) + " Solvents");
		
		resetSolventSpinner(startSolvents.size());
		
		solventNameTextField.setVisible(false);
		solventNameTextField.setStyle("-fx-display-caret: false;");
		solventNameTextField.prefColumnCountProperty().bind(solventNameTextField.textProperty().length());
		
		currentDistanceData = DistanceData.ALL_FEATURES;
		
		resetAllSolventsChart();
	}
	
	// Constructor is executed before the initialize() method.
	public AllSolventsView() 
	{
		startSolvents = new ArrayList<Solvent>();
		filteredSolvents = new ArrayList<Solvent>();
		
		// Initialise ArrayList startSolventSet with all solvents, that is the start dataset.
		// startSolventSet will be used to reset the chart to all solvents.
		int i = 0;
		for (HashMap.Entry<String, Solvent> solvent : GeneralData.solvents.entrySet())
		{
			Solvent newSolvent = solvent.getValue();
			newSolvent.setBubbleNumber(i++);
			startSolvents.add(newSolvent);
		}
	}

	private void addMouseEvents() 
	{
		for (Series<Integer, Integer> solventBubbles : solventsChart.getData())
			for (int i = 0; i < solventBubbles.getData().size(); i++)
			{
				Node currentSolventBubble = solventBubbles.getData().get(i).getNode();
				int localSolventBubbleNumber = i;
				
				currentSolventBubble.setOnMouseClicked
				(
					(MouseEvent solventEvent)
					-> 
					{
						currentBubbleNumber = localSolventBubbleNumber;
						selectCurrentSolventBubble();
						
						solventNumberSpinner.getValueFactory().setValue(currentBubbleNumber + 1);
					}
				);
			}
		
		currentBubbleNumber = 0;
		selectCurrentSolventBubble();
	}
	
	@FXML
	private void onFilterScreenButtonClicked()
	{
		unSelectCurrentSolventBubble();
		
		// Main gridpane.
		GridPane solventFilterPane = new GridPane();
		solventFilterPane.setGridLinesVisible(true);
		solventFilterPane.setStyle ("-fx-background-color: darkslategray");
		solventFilterPane.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 100));
		solventFilterPane.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 5));			
		solventFilterPane.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 95));
		
		// Feature gridpanes in row 1.
	    ListView<FeatureRangeGridPane> filterListView = new ListView<FeatureRangeGridPane>();
	    ObservableList<FeatureRangeGridPane> featureRangeGridPanes = FXCollections.observableArrayList();
		for (FeatureRange featureRange : GeneralData.getFeatureRanges())
		{
			FeatureRangeGridPane featureRangeGridPane = new FeatureRangeGridPane(featureRange, this);
			featureRangeGridPane.reset();
			featureRangeGridPanes.add(featureRangeGridPane);
		}
		filterListView.setItems(featureRangeGridPanes);
		solventFilterPane.getChildren().add(filterListView);
		GridPane.setRowIndex(filterListView, 1);
		
		// HBox with 2 buttons in row 0.
		GridPane buttonPane = new GridPane();
		buttonPane.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));
		buttonPane.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));
		buttonPane.setPadding(new Insets(10));
		
		// Filter button.
		Button filterButton = new Button("Filter");
		filterButton.setOnAction
		(
			(ActionEvent event)
			-> 
			{
				filteringIsOn = false;
				for (FeatureRangeGridPane featureRangeGridPane : featureRangeGridPanes)
					if (featureRangeGridPane.hasChanged())
					{
						filteringIsOn = true;						
						break;
					}

				if (filteringIsOn)
				{
					// Perform the filters.
					ArrayList<FeatureRange> filterFeatures = new ArrayList<FeatureRange>();
					
					for (FeatureRangeGridPane featureRangeGridPane : featureRangeGridPanes)
						if (featureRangeGridPane.hasChanged())
							filterFeatures.add(new FeatureRange(featureRangeGridPane.getFeatureName(), featureRangeGridPane.getCurrentMin(), featureRangeGridPane.getCurrentMax()));						
					
					filteredSolvents = filterSolvents(filterFeatures);
					
					// Update chart.
					resetFilteredSolventsChart();
				}
			}
		);
		
		filterButton.addEventHandler
		(
			MouseEvent.MOUSE_ENTERED,
			new EventHandler<MouseEvent>() 
			{
				@Override
				public void handle(MouseEvent e) 
				{
					filterButton.setStyle("-fx-cursor: hand; -fx-font-size: 16; -fx-text-fill: ivory; -fx-background-color: red;");
				}
			}
		);
		
		filterButton.addEventHandler
		(
			MouseEvent.MOUSE_EXITED,
			new EventHandler<MouseEvent>() 
			{
				@Override
				public void handle(MouseEvent e) 
				{
					filterButton.setStyle("-fx-cursor: default; -fx-font-size: 16; -fx-text-fill: ivory; -fx-background-color: maroon;");
				}
			}
		);
		
		filterButton.setStyle("-fx-font-size: 16; -fx-text-fill: ivory; -fx-background-color: maroon;");
		GridPane.setColumnIndex(filterButton, 0);
		
		// Reset All button.
		Button resetAllButton = new Button("Reset All");
		resetAllButton.setStyle(GeneralData.normalButtonCss);
		resetAllButton.setOnAction
		(
			(ActionEvent event)
			-> 
			{
				removeFiltering(false);					
								
				for (FeatureRangeGridPane featureRangeHBox : featureRangeGridPanes)
					featureRangeHBox.reset();
			}
		);
		
		resetAllButton.addEventHandler
		(
			MouseEvent.MOUSE_ENTERED,
			new EventHandler<MouseEvent>() 
			{
				@Override
				public void handle(MouseEvent e) 
				{
					resetAllButton.setStyle("-fx-cursor: hand; -fx-font-size: 16; -fx-text-fill: ivory; -fx-background-color: red;");
				}
			}
		);
		
		resetAllButton.addEventHandler
		(
			MouseEvent.MOUSE_EXITED,
			new EventHandler<MouseEvent>() 
			{
				@Override
				public void handle(MouseEvent e) 
				{
					resetAllButton.setStyle("-fx-cursor: default; -fx-font-size: 16; -fx-text-fill: ivory; -fx-background-color: maroon;");
				}
			}
		);
		
		resetAllButton.setStyle("-fx-font-size: 16; -fx-text-fill: ivory; -fx-background-color: maroon;");
		GridPane.setColumnIndex(resetAllButton, 1);

		buttonPane.getChildren().addAll(filterButton, resetAllButton);
		
		solventFilterPane.getChildren().add(buttonPane);
		GridPane.setRowIndex(buttonPane, 0);
		
		Scene scene = new Scene(solventFilterPane);
		GeneralData.filteringStage = new Stage();
		GeneralData.filteringStage.setTitle("Filter Solvents");
		GeneralData.filteringStage.setMaximized(true);
		GeneralData.filteringStage.setScene(scene);
		
		GeneralData.filteringStage.setOnShowing
		(
			(WindowEvent event)
			-> 
			{
				filterScreenButton.setDisable(true);
				
				GeneralData.filteringStage.setWidth(1800);
				GeneralData.filteringStage.setHeight(1000);
			}			
		);
		
		GeneralData.filteringStage.setOnCloseRequest
		(
			(WindowEvent event)
			->
			{
				filterScreenButton.setDisable(false);		// = setEnable().
				
				unSelectCurrentSolventBubble();
				
				removeFiltering(true);	
			}
		);
		
		GeneralData.filteringStage.show();		   
	}
	
	// Reset chart to all solvents, first solvent selected.
	private void resetAllSolventsChart()
	{
		resetStartSolvents();
		
		solventsChart.getData().clear();
		if (startSolvents.size() > 0)
			solventsChart.getData().add(allSolventsPresenter.getSolventsChartData(currentDistanceData, startSolvents));
		
		nrOfSolventsLabel.setText(Integer.toString(startSolvents.size()) + " Solvents");
		
		resetSolventSpinner(startSolvents.size());
		
		addMouseEvents();
	}
	
	// Reset chart to filtered solvents, first solvent selected.
	private void resetFilteredSolventsChart()
	{
//		printSolvents();
		
		solventsChart.getData().clear();
		if (filteredSolvents.size() > 0)
			solventsChart.getData().add(allSolventsPresenter.getSolventsChartData(currentDistanceData, filteredSolvents));
		
		nrOfSolventsLabel.setText(Integer.toString(filteredSolvents.size()) + " Solvents");
		
		resetSolventSpinner(filteredSolvents.size());
		
		addMouseEvents();
	}
	
	private void removeFiltering(boolean closeRequest)
	{
		if (closeRequest)
			GeneralData.filteringStage.close();
		
		solventNameTextField.setText("");
		solventNameTextField.setVisible(false);
		
		filteringIsOn = false;
		filteredSolvents = new ArrayList<Solvent>();
		
		resetAllSolventsChart();
	}
	
	private void selectCurrentSolventBubble()
	{
		// We only use the current data on the chart. We do not need to test if filteringIsOn is true.
		for (Series<Integer, Integer> solventBubbles : solventsChart.getData())
			for (int i = 0; i < solventBubbles.getData().size(); i++)
				if (i == currentBubbleNumber)
				{
					UIManager.selectBubble(solventBubbles.getData().get(i).getNode(), GeneralData.selectedCss);
					
					Solvent solvent;
					
					if (filteringIsOn)
						solvent = allSolventsPresenter.getSolvent(filteredSolvents, currentBubbleNumber);
					else
						solvent = allSolventsPresenter.getSolvent(startSolvents, currentBubbleNumber);
					
					UIManager.updateSolventTextField(solventNameTextField, solvent);
					UIManager.updateVisibility(solventNameTextField, true);
				}
				else
					UIManager.selectBubble(solventBubbles.getData().get(i).getNode(), GeneralData.normalCss);
	}
	
	private void unSelectCurrentSolventBubble()
	{
		for (Series<Integer, Integer> solventBubbles : solventsChart.getData())
			for (int i = 0; i < solventBubbles.getData().size(); i++)
				UIManager.selectBubble(solventBubbles.getData().get(i).getNode(), GeneralData.normalCss);
		
		solventNameTextField.setText("");
		solventNameTextField.setVisible(false);
		
		currentBubbleNumber = 0;
	}
	
	private ArrayList<Solvent> filterSolvents(ArrayList<FeatureRange> filterFeatures)
	{
		ArrayList<Solvent> result = new ArrayList<Solvent>();
		
		ArrayList<ArrayList<Solvent>> filterResults = new ArrayList<ArrayList<Solvent>>();
		for(FeatureRange featureRange : filterFeatures)
			filterResults.add(filter(startSolvents, featureRange));

		Collections.sort
		(
			filterResults, (a1, a2) -> a1.size() - a2.size()
		);
		
		// For all solvents of the first filter, count how many times it occurs in the other filters.
		// If that number == the total number of filters -> add the solvent.
		
		int solventCounter = 0;
		for (Solvent solvent : filterResults.get(0))
		{
			int filterCounter = 0;
			for (int i=1; i < filterResults.size(); i++)
				if (filterResults.get(i).contains(solvent))
					filterCounter++;
			
			if (filterCounter == filterResults.size() - 1)
			{
				solvent.setBubbleNumber(solventCounter++);	
				result.add(solvent);
			}
		}
		
		return result;
	}
	
	// Goede code om een lijst te veranderen met filters:
	//
	//		for(FeatureRange featureRange : filterFeatures)
	//			result = filter(result, featureRange);
	
	private ArrayList<Solvent> filter(ArrayList<Solvent> currentList, FeatureRange featureRange)
	{
		ArrayList<Solvent> result = new ArrayList<Solvent>();
		
		for (Solvent solvent : startSolvents)
			for (Feature feature : solvent.getFeatures())
				if (feature.getName().equalsIgnoreCase(featureRange.getName())	&&
					feature.getValue() >= featureRange.getMinValue() 			&&
					feature.getValue() <= featureRange.getMaxValue())
				{
					result.add(solvent);
				}
		
		return result;
	}
	
	private void resetStartSolvents()
	{
		startSolvents = new ArrayList<Solvent>();
		
		int i = 0;
		// Initialise ArrayList startSolventSet with all solvents, that is the start dataset.
		// startSolventSet will be used to reset the chart to all solvents.
		for (HashMap.Entry<String, Solvent> solvent : GeneralData.solvents.entrySet())
		{
			Solvent newSolvent = solvent.getValue();
			newSolvent.setBubbleNumber(i++);
			startSolvents.add(newSolvent);
		}
			
	}
	
	private void resetSolventSpinner(int nrOfSolvents)
	{
		solventNumberFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, nrOfSolvents);
		solventNumberFactory.setWrapAround(true);
		solventNumberSpinner.setValueFactory(solventNumberFactory); 
		solventNumberSpinner.getValueFactory().setValue(1);
		solventNumberSpinner.setOnMouseReleased
		(
			(MouseEvent event)
			-> 
			{
				currentBubbleNumber = solventNumberSpinner.getValue() - 1;
				selectCurrentSolventBubble();
			}
		);
	}
	
	private void printSolvents()
	{
		System.out.println("All Solvents : " + startSolvents.size());
		for (Solvent solvent : startSolvents)
			System.out.println(solvent.toStringNoFeatures());
		
		System.out.println("Filtered Solvents : " + filteredSolvents.size());
		for (Solvent solvent : filteredSolvents)
			System.out.println(solvent.toStringNoFeatures());
	}

	@Override
	public void filterSolvents(String featureName, double min, double max) 
	{
		// TODO Auto-generated method stub
		
	}

	public void setStartSolventSet(ArrayList<Solvent> startSolventSet) { this.startSolvents = startSolventSet; }
}

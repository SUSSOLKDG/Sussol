package view;

import java.util.ArrayList;

import application.Main;
import general.GeneralData;
import general.GeneralData.PostFilteringMode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.clustering.Cluster;
import model.features.Feature;
import model.solvents.Solvent;
import presenter.ClusteringPresenter;
import presenter.MainPresenter;
import utilities.IOManager;
import utilities.MathManager;
import utilities.UIManager;
import view.filtering.FilteringView;

public class ClusteringView implements FilteringView
{
	private ClusteringPresenter clusteringPresenter;		
	
	@FXML private GridPane grid;
	
		@FXML private GridPane clusterGrid;
			@FXML private HBox clusterLabelsHBox;
			@FXML private Spinner<Integer> clusteringSpinner;
			@FXML private Label numberOfSolvents;
			@FXML private BubbleChart<Integer, Integer> clustersChart;
			@FXML private VBox solventsInClusterList;
			@FXML private Label datasetLabel;
		
		@FXML private GridPane solventGrid;
			@FXML private HBox solventLabelsHBox;
			@FXML private Label solventLabel;
			@FXML private Label solventNumber;
			@FXML private Label solventName;
			
			@FXML private BubbleChart<Integer, Integer> solventsChart;
			
			@FXML private ScrollPane featureListScrollPane;
			@FXML private VBox featureListVBox;
			
			@FXML private TextField searchTextField;
			
			@FXML private GridPane solventDataGrid;
				@FXML private HBox ehsDataHBox;
				@FXML private GridPane ehsDataGrid;
				@FXML private Label ehsDataLabel;
				@FXML private HBox environmentHBox;
				@FXML private Label environmentLabel;
				@FXML private Label environmentScore;
				@FXML private HBox healthHBox;
				@FXML private Label healthLabel;
				@FXML private Label healthScore;
				@FXML private HBox safetyHBox;
				@FXML private Label safetyLabel;
				@FXML private Label safetyScore;
			
	@FXML 
	public void initialize()
	{	
		// Main grid
		grid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));
		grid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));

		// Cluster grid
		clusterGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 60));
		clusterGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 15));
		clusterGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 25));
		clusterGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 5));
		clusterGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 8));
		clusterGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 7));
		clusterGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 80));
		
		UIManager.updateVisibility(numberOfSolvents, true);
		
		clustersChart.setAnimated(false);
		clustersChart.getXAxis().setTickLabelsVisible(false);
		clustersChart.getYAxis().setTickLabelsVisible(false);
		
		solventsInClusterList.setPadding(new Insets(10));			
		
		// Solvent grid
		solventGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 65));
		solventGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 35));
		solventGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 5));
		solventGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 8));
		solventGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 7));
		solventGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 80));
		
		UIManager.updateVisibility(solventLabel, false);
		
		solventsChart.setAnimated(false);
		solventsChart.getXAxis().setTickLabelsVisible(false);
		solventsChart.getYAxis().setTickLabelsVisible(false);
		
		// solventDataGrid
		solventDataGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 35));
		solventDataGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 50));
		
		// EHS Data grid
		ehsDataGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));
		ehsDataGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));
		ehsDataGrid.setPadding(new Insets(10));			
		
		ehsDataLabel.setPadding(new Insets(10));	
		ehsDataLabel.setStyle("-fx-text-fill: maroon;");
		environmentLabel.setPadding(new Insets(10));
		healthLabel.setPadding(new Insets(10));
		safetyLabel.setPadding(new Insets(10));
		
		UIManager.updateVisibility(ehsDataGrid, false);
		UIManager.updateVisibility(ehsDataLabel, false);
		UIManager.updateVisibility(environmentLabel, false);
		UIManager.updateVisibility(environmentScore, false);
		UIManager.updateVisibility(healthLabel, false);
		UIManager.updateVisibility(healthScore, false);
		UIManager.updateVisibility(safetyLabel, false);
		UIManager.updateVisibility(safetyScore, false);
		
		featureListVBox.setPadding(new Insets(10));	
		
		
		if (GeneralData.finalClusteringIsInitialised)
			initializeFinalClustering();
		
		addEventHandlers();
	}
	
	private void initializeFinalClustering()
	{
		clusteringPresenter = new ClusteringPresenter(this);			
		
		clusterLabels = new ArrayList<ClusterLabel>();
		for (int i=0; i < GeneralData.finalClustering.getClusters().size(); i++)
			clusterLabels.add(new ClusterLabel(i));

		resetClusterChart();
		
		clusterLabelsHBox.getChildren().clear();
		clusterLabelsHBox.getChildren().addAll(clusterLabels);
		
		clustersChart.getData().clear();
		clustersChart.getData().add(clusteringPresenter.getFinalClusteringChartData());
		
		clearSolventChart();
		
		previousClusterBubbleNumber = -1;
		previousSolventBubbleNumber = -1;
	}

	private SpinnerValueFactory<Integer> clusteringValueFactory;
	
	private ArrayList<ClusterLabel> clusterLabels;
	private ArrayList<SolventLabel> solventLabels;
	
	private int previousClusterBubbleNumber;
	private int previousSolventBubbleNumber;
	
	protected Cluster currentCluster;
	
	protected Solvent currentSolvent;
	protected Node currentSolventBubble;
	
	private boolean firstControlClicked;
	private boolean secondControlClicked;
	private Solvent solvent1;
	private Solvent solvent2;
	private Node solvent1Bubble;
	private Node solvent2Bubble;
	
	private void addEventHandlers()
	{
		clusteringValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20);		
		clusteringSpinner.setValueFactory(clusteringValueFactory); 
		clusteringSpinner.getValueFactory().setValue(0);
		clusteringSpinner.setOnMouseReleased
		(
			(MouseEvent event)
			-> 
			{
				//
				GeneralData.finalClustering = MainPresenter.getClustering(clusteringValueFactory.getValue() - 1);
            	initializeFinalClustering();
			}
		);
		
		// Event processing for cluster- and solventbubbles.
		//
		// Run through the cluster bubbles.
		//
		// 		Get the corresponding cluster, based on i.
		// 		This works because when making the clusters (Clustering.makeClusters) we use the same numbering.
		
		// For each cluster, get the corresponding solvent data (note : no foreach loop possible, we need i).
		// Add the solvent data to the solventsBubbleChart in the MouseEvent.
		//
		// Only this sequence will work.
					
		for (Series<Integer, Integer> clusterBubbles : clustersChart.getData())
			for (int i = 0; i < clusterBubbles.getData().size(); i++)
			{
				int localClusterBubbleNumber = i;
				
				clusterBubbles.getData().get(i).getNode().setOnMouseClicked	
				(
					(MouseEvent clusterEvent)
					-> 
					{	
						currentCluster = GeneralData.finalClustering.getCluster(localClusterBubbleNumber);
						
						// To prevent 'duplicate children' error.
	                	if (previousClusterBubbleNumber != localClusterBubbleNumber)
						{			                
	                		updateClusterSelection(localClusterBubbleNumber);
	                		
	                		updateSolventChart();
	                		
							for (Series<Integer, Integer> solventBubbles : solventsChart.getData())
								for (int j = 0; j < solventBubbles.getData().size(); j++)
								{
									currentSolventBubble = solventBubbles.getData().get(j).getNode();
									
									// Get EHS code for each solvent, based on bubbleNumber.
									currentSolventBubble.setStyle(currentCluster.getSolvent(j).getEhsData().getEhsColor());
									
									int localSolventBubbleNumber = j;
									
									solventBubbles.getData().get(j).getNode().setOnMouseClicked				
									(
										(MouseEvent solventEvent)
										-> 
										{
											currentSolvent = currentCluster.getSolvent(localSolventBubbleNumber);
											
											if (solventEvent.isControlDown())
							                {
												if (! firstControlClicked)
												{
													currentSolventBubble = solventBubbles.getData().get(localSolventBubbleNumber).getNode();
													solvent1 = currentSolvent;
													updateSolvent1();
												}
												else
												{
													currentSolventBubble = solventBubbles.getData().get(localSolventBubbleNumber).getNode();
													solvent2 = currentSolvent;
													updateSolvent2();
												}
												
												if (firstControlClicked && secondControlClicked)
												{
													updateDistances();
												}
							                }
							                else
							                {
												currentSolventBubble = solventBubbles.getData().get(localSolventBubbleNumber).getNode();
												resetSolventChart();
												updateSolventSelection(localSolventBubbleNumber);
							                }
										}
									);
								}
							previousClusterBubbleNumber = localClusterBubbleNumber;
						}
                	}	               	                
				);
			}					

		// Rightclick on clustersBubbleChart.
		clustersChart.setOnMouseReleased
		(
			(MouseEvent event)
			-> 
			{
				MouseButton clusterEvent = event.getButton();
				if (clusterEvent == MouseButton.PRIMARY)
				{
					if (GeneralData.finalClusteringIsInitialised)
					{
						resetClusterChart();
						removeSolventFiltering();
						clearSolventChart();
					}
				}
				else if (clusterEvent == MouseButton.SECONDARY)
                {                	           
                	IOManager.saveChart(clustersChart);                	                
                }
			}			
		);
		
		// Rightclick on solventsBubbleChart.
		solventsChart.setOnMouseReleased
		(
			(MouseEvent event)
			-> 
			{
				MouseButton solventEvent = event.getButton();
				if (solventEvent == MouseButton.SECONDARY)
                {                	           
                	IOManager.saveChart(solventsChart);                	                
                }
			}			
		);
		
	}	
	
	// Reset = go back to initial state.
	// Update = change to new state.
	
	// Reset cluster part to start state.
	private void resetClusterChart()
	{			
		for (ClusterLabel clusterLabel : clusterLabels)
			clusterLabel.reset();
		
		UIManager.updateVisibility(numberOfSolvents, false);
		
		for (Series<Integer, Integer> clusterBubbles : clustersChart.getData())
			for (int i = 0; i < clusterBubbles.getData().size(); i++)
				clusterBubbles.getData().get(i).getNode().setStyle(GeneralData.normalCss);
		
		solventsInClusterList.getChildren().clear();
	}
	
	// Change cluster part to new state.
	protected void updateClusterSelection(int clusterBubbleNumber)
	{
		resetClusterChart();
		
		for (ClusterLabel clusterLabel : clusterLabels)
        {		
			if (clusterLabel.getBubbleNumber() == clusterBubbleNumber)
				clusterLabel.setStyle(GeneralData.selectedCss);
        }
		
		UIManager.updateVisibility(numberOfSolvents, true);
		String text = currentCluster.getNumberOfSolvents() == 1 ? " Solvent" : " Solvents";
		numberOfSolvents.setText(Integer.toString(currentCluster.getNumberOfSolvents()) + text);
		
		for (Series<Integer, Integer> clusterBubbles : clustersChart.getData())
			for (int i = 0; i < clusterBubbles.getData().size(); i++)
			{
				if (i == clusterBubbleNumber)
					UIManager.selectBubble(clusterBubbles.getData().get(i).getNode(), GeneralData.selectedCss);
			}
		
		ArrayList<Label> solventsInClusterNames = new ArrayList<Label>();
		for (Solvent solvent : currentCluster.getSolvents())
		{
			Label solventLabel = new Label(solvent.getName());
			solventLabel.setStyle(GeneralData.normalLabelCss);
			solventsInClusterNames.add(solventLabel);
		}
		solventsInClusterList.getChildren().clear();
		solventsInClusterList.getChildren().addAll(solventsInClusterNames);
		
		resetSolventChart();
	}
	
	// Reset solvent part to start state.
	private void resetSolventChart()
	{		
		if (solventLabels != null)
			for (SolventLabel solventLabel : solventLabels)
				solventLabel.setStyle(GeneralData.sussolLabelCss);
		
		UIManager.updateVisibility(solventLabel, false);
		UIManager.updateVisibility(solventNumber, false);
		UIManager.updateVisibility(solventName, false);		
		
		UIManager.updateVisibility(ehsDataGrid, false);
		UIManager.updateVisibility(ehsDataLabel, false);
		UIManager.updateVisibility(environmentLabel, false);
		UIManager.updateVisibility(environmentScore, false);
		UIManager.updateVisibility(healthLabel, false);
		UIManager.updateVisibility(healthScore, false);
		UIManager.updateVisibility(safetyLabel, false);
		UIManager.updateVisibility(safetyScore, false);
		
		if (solvent1Bubble != null)
			UIManager.selectBubble(solvent1Bubble, GeneralData.normalCss);
		if (solvent2Bubble != null)
			UIManager.selectBubble(solvent2Bubble, GeneralData.normalCss);
		
		firstControlClicked = false;
		secondControlClicked = false;
	}
	
	// Change solvent part to new state : show solvents + labels of current cluster.
	protected void updateSolventChart()
	{
		resetSolventChart();
		
		solventsChart.getData().clear();
		solventsChart.getData().add(currentCluster.getSolventChartData());			
			
//		for (Solvent solvent : currentCluster.getSolvents())
//			System.out.println(solvent.getName());
		
		for (Series<Integer, Integer> solventBubbles : solventsChart.getData())
			for (int i = 0; i < solventBubbles.getData().size(); i++)
			{
				// Get EHS code for each solvent, based on bubbleNumber.
				String cssString = "-fx-background-color: " + currentCluster.getSolvent(i).getEhsData().getEhsColor() + ";";
				UIManager.selectBubble(solventBubbles.getData().get(i).getNode(), cssString);
				
				int localSolventBubbleNumber = i;
				
				solventBubbles.getData().get(i).getNode().setOnMouseClicked				
				(
					(MouseEvent solventEvent)
					-> 
					{
						currentSolvent = currentCluster.getSolvent(localSolventBubbleNumber);
						
						resetSolventChart();
						
						UIManager.selectBubble(solventBubbles.getData().get(localSolventBubbleNumber).getNode(), GeneralData.selectedCss);
						
						updateSolventSelection(localSolventBubbleNumber);
					}
				);
			}
		
		solventLabels = new ArrayList<SolventLabel>();
		for (int i=0; i < currentCluster.getSolvents().size(); i++)
		{
			solventLabels.add(new SolventLabel(i));
		}
		solventLabelsHBox.getChildren().clear();
		solventLabelsHBox.getChildren().addAll(solventLabels);
	}
	
	// Change solvent part to new state : show metadata of selected solvent and select bubble.
	private void updateSolventSelection(int solventBubbleNumber)
	{		
		for (SolventLabel solventLabel : solventLabels)
        {		
			if (solventLabel.getBubbleNumber() == solventBubbleNumber)
				solventLabel.setStyle(GeneralData.selectedCss);
        	else
        		solventLabel.setStyle(GeneralData.sussolLabelCss);
        }
		
		for (Series<Integer, Integer> solventBubbles : solventsChart.getData())
			for (int i = 0; i < solventBubbles.getData().size(); i++)
			{
				// Get EHS code for each solvent, based on bubbleNumber.
				String cssString = "-fx-background-color: " + currentCluster.getSolvent(i).getEhsData().getEhsColor() + ";";
				UIManager.selectBubble(solventBubbles.getData().get(i).getNode(), cssString);
			
				if (i == currentSolvent.getBubbleNumber())
					UIManager.selectBubble(solventBubbles.getData().get(i).getNode(), GeneralData.selectedCss);
			}
		
		UIManager.updateVisibility(solventLabel, true);
		UIManager.updateVisibility(solventNumber, true);
		UIManager.updateVisibility(solventName, true);
		
		solventNumber.setText(Integer.toString(currentSolvent.getNumber()) + " : ");
		solventName.setText(currentSolvent.getName());
				
		ArrayList<Label> solventFeatures = new ArrayList<Label>();
		for (Feature feature : currentSolvent.getFeatures())
		{
			if (! feature.getName().contains("_Copy"))
			{
				Label featureLabel = new Label(feature.getName() + " :\t" + GeneralData.decimalFormat(feature.getValue()));
				featureLabel.setStyle(GeneralData.normalLabelCss);
				solventFeatures.add(featureLabel);
			}
		}
		featureListVBox.getChildren().clear();	
		featureListVBox.getChildren().addAll(solventFeatures);
		featureListVBox.setFocusTraversable(true);
		
		environmentScore.setText(currentSolvent.getEhsData().getEnvironmentScore());
		healthScore.setText(currentSolvent.getEhsData().getHealthScore());
		safetyScore.setText(currentSolvent.getEhsData().getSafetyScore());
		
		ehsDataHBox.setBackground
		(
			new Background( new BackgroundFill(Color.web(currentSolvent.getEhsData().getEhsColor(), 1.0), new CornerRadii(10), new Insets(2)) )
		);
		environmentHBox.setBackground
		(
			new Background( new BackgroundFill(Color.web(currentSolvent.getEhsData().getEnvironmentColor(), 1.0), new CornerRadii(10), new Insets(2)) )
		);
		healthHBox.setBackground
		(
			new Background( new BackgroundFill(Color.web(currentSolvent.getEhsData().getHealthColor(), 1.0), new CornerRadii(10), new Insets(2)) )
		);
		safetyHBox.setBackground
		(
			new Background( new BackgroundFill(Color.web(currentSolvent.getEhsData().getSafetyColor(), 1.0), new CornerRadii(10), new Insets(2)) )
		);
		
		UIManager.updateVisibility(ehsDataGrid, true);
		UIManager.updateVisibility(ehsDataLabel, true);
		UIManager.updateVisibility(environmentLabel, true);
		UIManager.updateVisibility(environmentScore, true);
		UIManager.updateVisibility(healthLabel, true);
		UIManager.updateVisibility(healthScore, true);
		UIManager.updateVisibility(safetyLabel, true);
		UIManager.updateVisibility(safetyScore, true);

	}
	
	private void clearSolventChart()
	{
		solventsChart.getData().clear();
		
		UIManager.updateVisibility(solventLabel, false);
		UIManager.updateVisibility(solventNumber, false);
		UIManager.updateVisibility(solventName, false);
		
		UIManager.updateVisibility(ehsDataGrid, false);
		UIManager.updateVisibility(ehsDataLabel, false);
		UIManager.updateVisibility(environmentLabel, false);
		UIManager.updateVisibility(environmentScore, false);
		UIManager.updateVisibility(healthLabel, false);
		UIManager.updateVisibility(healthScore, false);
		UIManager.updateVisibility(safetyLabel, false);
		UIManager.updateVisibility(safetyScore, false);
		
		firstControlClicked = false;
		secondControlClicked = false;
		
		solventLabelsHBox.getChildren().clear();
		
		featureListVBox.getChildren().clear();
	}
	
	protected void updateSolvent1()
	{
		resetSolventChart();
		
		solvent1Bubble = currentSolventBubble;
		UIManager.selectBubble(currentSolventBubble, GeneralData.hansenCss);

		firstControlClicked = true;
	}
	
	protected void updateSolvent2()
	{
		solvent2Bubble = currentSolventBubble;
		UIManager.selectBubble(currentSolventBubble, GeneralData.hansenCss);
		
		secondControlClicked = true;
	}
	
	protected void updateDistances()
	{
		firstControlClicked = false;
		secondControlClicked = false;
	}

	private Stage stage;

	@FXML
	private void onFilterButtonClicked()
	{
		GeneralData.postFilteringMode = PostFilteringMode.SOLVENTS_FROM_ONE_CLUSTER;
		
		GeneralData.solventsOfOneCluster = currentCluster.getSolvents();
		
		FXMLLoader loaderAllSolvents = new FXMLLoader();
		loaderAllSolvents.setLocation(Main.class.getResource("/view/filtering/AllSolventsScreen.fxml"));	
		
		Main.loadAllSolventsScreen();
	}

	public void removeSolventFiltering()
	{
		if (stage != null)
			stage.close();
		
		for (Series<Integer, Integer> solventBubbles : solventsChart.getData())
			for (int i = 0; i < solventBubbles.getData().size(); i++)
				solventBubbles.getData().get(i).getNode().setVisible(true);
	}

	@Override
	public void filterSolvents(String featureName, double min, double max) 
	{
		ArrayList<Integer> solventsToBeShown = currentCluster.filterSolventChartData(featureName, min, max);
		
		for (Series<Integer, Integer> solventBubbles : solventsChart.getData())
			for (int i = 0; i < solventBubbles.getData().size(); i++)
			{
				solventBubbles.getData().get(i).getNode().setVisible(false);
				if (solventsToBeShown.contains(i))
				{
					solventBubbles.getData().get(i).getNode().setVisible(true);
				}
			}
		
		for (Solvent solvent : currentCluster.getSolvents())
		{
			// If the solvent is filtered out, do :
			if (! solventsToBeShown.contains(solvent.getBubbleNumber()))
			{
				((SolventLabel) solventLabelsHBox.getChildren().get(solvent.getBubbleNumber())).setVisible(false);
				solventLabels.get(solvent.getBubbleNumber()).setStyle(GeneralData.selectedlabelCss);
			}
			else
			{
				((SolventLabel) solventLabelsHBox.getChildren().get(solvent.getBubbleNumber())).setVisible(true);
				solventLabels.get(solvent.getBubbleNumber()).setStyle(GeneralData.normalLabelCss);
			}
		}
	}
	
	@FXML
	public void onSearch(KeyEvent event)
	{
		if (event.getCode() == KeyCode.ENTER) 
		{
			Solvent solvent = GeneralData.finalClustering.getSolvent(searchTextField.getText());
			
			if (solvent.getNumber() != -1)
			{
				currentCluster = GeneralData.finalClustering.getCluster(solvent.getClusterNumber());
				updateClusterSelection(solvent.getClusterNumber());
			
				currentSolvent = currentCluster.getSolvent(solvent.getBubbleNumber());
				updateSolventChart();
				updateSolventSelection(solvent.getBubbleNumber());
			}
			else
			{
				searchTextField.setText("Unkown Solvent");
				resetClusterChart();
				removeSolventFiltering();
				clearSolventChart();
			}
		}
	}
	
	private class ClusterLabel extends Label 
	{
		private int clusterBubbleNumber;
		
		public ClusterLabel(int clusterBubbleNumber) 
		{
			this.clusterBubbleNumber = clusterBubbleNumber;
			
			setText(Integer.toString(clusterBubbleNumber));
			setAlignment(Pos.CENTER);
			setId("sussolLabel");
			
			setOnMouseClicked
	        (
	        	new LabelEventHandler()
			);
		}
		
		private class LabelEventHandler implements EventHandler<MouseEvent> 
	    {
			@Override
			public void handle(MouseEvent event) 
			{
				if (previousClusterBubbleNumber != clusterBubbleNumber)
				{
					currentCluster = GeneralData.finalClustering.getCluster(clusterBubbleNumber);
					
					updateClusterSelection(clusterBubbleNumber);
					
					updateSolventChart();
				}
				previousClusterBubbleNumber = clusterBubbleNumber;
			}
	    }
		
		public void reset() { setStyle(GeneralData.sussolLabelCss); }

		public int getBubbleNumber() { return clusterBubbleNumber; }
	}

	private class SolventLabel extends Label 
	{
		private int solventBubbleNumber;
		
		public SolventLabel(int solventBubbleNumber)
		{
			this.solventBubbleNumber = solventBubbleNumber;
			
			setText(Integer.toString(solventBubbleNumber));
			setAlignment(Pos.CENTER);
			setId("sussolLabel");
			
			setOnMouseEntered
			(
				new MouseEnteredEventHandler()
			);
			
			setOnMouseClicked
	        (
	        	new MouseClickEventHandler()
			);
		}				
		
		Tooltip tooltip;
		private class MouseEnteredEventHandler implements EventHandler<MouseEvent> 
	    {	
			@Override
			public void handle(MouseEvent event) 
			{
				currentSolvent = currentCluster.getSolvent(solventBubbleNumber);
								
				tooltip = new Tooltip();
				tooltip.setText(currentSolvent.getName());
				setTooltip(tooltip);
			}
	    }
		
		private class MouseClickEventHandler implements EventHandler<MouseEvent> 
	    {	
			@Override
			public void handle(MouseEvent event) 
			{		
				if (previousSolventBubbleNumber != solventBubbleNumber)
				{
					currentSolvent = currentCluster.getSolvent(solventBubbleNumber);
					
					for (Series<Integer, Integer> solventBubbles : solventsChart.getData())
						for (int i = 0; i < solventBubbles.getData().size(); i++)
						{			
							if (i == solventBubbleNumber)
							{
								currentSolventBubble = solventBubbles.getData().get(i).getNode();
								break;
							}
						}
					
					if (event.isControlDown())
	                {
						if (! firstControlClicked)
						{
							solvent1 = currentSolvent;
							updateSolvent1();
							selectHansen();
						}
						else
						{
							solvent2 = currentSolvent;
							updateSolvent2();
							selectHansen();
						}
						UIManager.selectBubble(currentSolventBubble, GeneralData.hansenCss);
						
						if (firstControlClicked && secondControlClicked)
						{
							updateDistances();
						}
	                }
	                else
	                {
						resetSolventChart();
						
						select();
						
						updateSolventSelection(solventBubbleNumber);
	                }								
				}
				previousSolventBubbleNumber = solventBubbleNumber;
	        }
	    }
		
		public void select() { setStyle(GeneralData.selectedCss); }
		private void selectHansen() { setStyle(GeneralData.hansenCss); }
		
		public int getBubbleNumber() { return solventBubbleNumber; }
	}

	protected void showSolventLabels()
	{
		for (int i=0; i < solventLabelsHBox.getChildren().size(); i++)
		{
			((SolventLabel) solventLabelsHBox.getChildren().get(i)).setVisible(true);
		}
	}

}


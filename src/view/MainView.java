package view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.Main;
import general.GeneralData;
import general.GeneralData.PostFilteringMode;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.solvents.Solvent;
import presenter.MainPresenter;
import utilities.IOManager;
import utilities.UIManager;

public class MainView 
{	
	private MainPresenter mainPresenter;

	public final BooleanProperty clusteringsReady = new SimpleBooleanProperty(false);
	public final BooleanProperty allSolventsReady = new SimpleBooleanProperty(false);
	public final BooleanProperty candidatesReady = new SimpleBooleanProperty(false);
	
	private ObservableList<Label> solventLabels;
	private ListView<Label> solventList;
	
	@FXML private MenuItem clusteringVisualisationMenuItem;
	@FXML private MenuItem allSolventsMenuItem;
	@FXML private MenuItem generateCandidatesMenuItem;
	
	@FXML private GridPane centralGridPane;
		@FXML private ScrollPane solventsScrollPane;
		@FXML private VBox solvents;
		
		@FXML private GridPane clusterGridPane;
			@FXML private TextField solventName;
			@FXML private TextField solventCasNumber;
			@FXML public  Label progressLabel;
			@FXML public  ProgressBar progressBar;			
			@FXML private Button clusterButton;
			
	@FXML 
	public void initialize()
	{
		mainPresenter = new MainPresenter(this);

		clusteringVisualisationMenuItem.disableProperty().bind(clusteringsReady.not());
		allSolventsMenuItem.disableProperty().bind(allSolventsReady.not());
		generateCandidatesMenuItem.disableProperty().bind(candidatesReady.not());
		
		centralGridPane.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 40));
		centralGridPane.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 60));
		centralGridPane.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 10));
		centralGridPane.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 90));
		centralGridPane.setPadding(new Insets(10));
		
		String pathToSolventFile = "freemium.csv";
		GeneralData.solventDataFrame = IOManager.getSolventDataFrameFromFile(pathToSolventFile);
		allSolventsReady.set(true);
		
		solventLabels = FXCollections.observableArrayList();
		if (GeneralData.solvents != null)
		{
//			for (HashMap.Entry<String, Solvent> solvent : GeneralData.solvents.entrySet())
			for (int i=0; i < GeneralData.solventNames.size(); i++)
			{
//				Label label = new Label(solvent.getValue().getName() + " : " + solvent.getValue().getCasNumber());
				Label label = new Label(GeneralData.solventNames.get(i));
				solventLabels.add(label);
			}					
		}
        solventList = new ListView<>(solventLabels);
        solventList.setPrefHeight(2000);      
        solventList.setOnMouseClicked
        (
    		new EventHandler<MouseEvent>() 
    		{
	            @Override
	            public void handle(MouseEvent click)
	            {
	            	String[] selectedSolvent = solventList.getSelectionModel().getSelectedItem().getText().split(" : ");
	            	
//	            	Solvent solvent = GeneralData.getSolventByCasNumber(selectedSolvent[1]);
	            	Solvent solvent = GeneralData.getSolventByName(selectedSolvent[0]);
	            	
	            	solventName.setText(solvent.getName());
                	solventCasNumber.setText(solvent.getCasNumber());                         
                	
                	if (click.getClickCount() == 1)
                		clusterButton.setDisable(false);
	                if (click.getClickCount() == 2) 
	                	onClusterButtonClicked();
	            }
	        }
		);
		
        solvents.setPadding(new Insets(10));
		solvents.getChildren().clear();
		solvents.getChildren().addAll(solventList);
		
		solventsScrollPane.setFitToHeight(true);
		solventsScrollPane.setFitToWidth(true);
		
		clusterGridPane.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));
		clusterGridPane.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 50));
		clusterGridPane.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 25));
		clusterGridPane.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 25));
		clusterGridPane.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 25));
		clusterGridPane.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 25));
		clusterGridPane.setPadding(new Insets(20));
		
		clusterButton.setDisable(true);
	}
	
	@FXML void onRestart()
	{	
		Main.restart();
	}
	
	@FXML void onShowClusteringScreen()
	{
		Main.loadClusteringScreen();
	}
	
	@FXML void onShowAllSolventsScreen()
	{
		GeneralData.postFilteringMode = PostFilteringMode.ALL_SOLVENTS;
		Main.loadAllSolventsScreen();
	}
	
	@FXML public void onShowCandidatesScreen()
	{
		Main.loadCandidatesScreen();
	}
	
	@FXML 
	void onClusterButtonClicked()
	{	
		clusteringsReady.set(false);
		candidatesReady.set(false);
		
		String casNumber = solventCasNumber.getText();
		// Check The Solvent.
		if (! GeneralData.solvents.containsKey(casNumber))
		{
//			System.out.println("Solvent not in dataset !");
			return;
		}
		else
		{
			GeneralData.initialiseTheSolvent(casNumber);
			GeneralData.theSolventLives = true;
		}
		
		ExecutorService service = Executors.newSingleThreadExecutor();
		
		Task<Void> currentTask = new Task<Void>()
		{								
			@Override
			protected Void call() throws Exception 
			{
				if (GeneralData.solventDataFrame != null)
				{
					clusteringsReady.set(false);
					candidatesReady.set(false);					
					
					UIManager.updateButton(clusterButton, "Working");
					
					mainPresenter.cluster
					(
						4, 
						4, 
						0.00001,
						1000
					);
					
					try 
					{
						UIManager.updateButton(clusterButton, "Done");
						Thread.sleep(1000);
						UIManager.updateButton(clusterButton, "Cluster");
					}
					catch (InterruptedException e) { e.printStackTrace(); }
				}
			
				return null;
			}
			
			@Override 
			protected void succeeded() 
			{
				service.shutdown();
								
				GeneralData.finalClusteringIsInitialised = true;
				
				clusteringsReady.set(true);
				
				if (GeneralData.theSolventLives)
					candidatesReady.set(true);
			}
		};
	
		service.submit(currentTask);		
	}
	
	public void logProgress(double step)
	{
		Platform.runLater
		(
			new Runnable()
			{
				@Override
				public void run() 
				{
					progressBar.setProgress(step);
				}
			}
		);
	}	
}

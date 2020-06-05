package view;

import java.util.ArrayList;
import java.util.Set;
import general.GeneralData;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import model.candidates.Candidate;
import model.candidates.CandidateChartData;
import model.candidates.CandidatesManager;
import presenter.CandidatesPresenter;
import utilities.ExcelManager;
import utilities.UIManager;

public class CandidatesView 
{
	private CandidatesPresenter candidatesPresenter;
	
	private ArrayList<Candidate> candidates;
	private ArrayList<CandidateChartData> candidatesChartData;	
	private ObservableList<Label> candidatesNames;
	
	private Boolean actualValuesShown;
	
	@FXML private GridPane candidatesGrid;
		@FXML private GridPane buttonGrid;
			@FXML private Button resetButton;
			@FXML private Button normaliseButton;
			@FXML public Button exportButton;
	
		@FXML private GridPane dataGrid;	
			@FXML private LineChart<String, Double> candidatesChart;
				@FXML private CategoryAxis xAxis;
				@FXML private NumberAxis yAxis;
			
			@FXML private GridPane candidatesListGrid;
			@FXML private TextField nrOfCandidatesTextField;
			@FXML private ListView<Label> candidatesList;

	@FXML 
	public void initialize()
	{
		candidatesPresenter = new CandidatesPresenter(this);
		
		if (GeneralData.finalClusteringIsInitialised)
		{
			candidatesGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 100));
			candidatesGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 10));
			candidatesGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 90));
			
			buttonGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 33));
			buttonGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 33));
			buttonGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 33));
			buttonGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 100));
			
			dataGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 75));
			dataGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 25));
			dataGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 100));
			
			candidatesListGrid.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 100));
			candidatesListGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 10));
			candidatesListGrid.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 90));
			candidatesListGrid.setPadding(new Insets(5, 10, 5, 10));
			
			xAxis.setLabel("Features");
			yAxis.setLabel("Feature Values");					    
			
			candidates = candidatesPresenter.getCandidates();
			candidatesChartData = candidatesPresenter.getCandidatesChartData();
			candidatesNames = candidatesPresenter.getCandidatesNames();
			
			actualValuesShown = true;
			
			// Solution to known bug in JavaFX : 'added duplicate series'.
			candidatesChart.setAnimated(false);
			resetCandidatesChart();
	            
			nrOfCandidatesTextField.setText(candidatesNames.size() + " Candidates");
			
			// Event handling.
			candidatesList.setOnMouseReleased
			(
				// 1 Left click : HighLight solvent and leave the others.
				// 1 Right click : HighLight solvent and remove the others.
					
				(mouseEvent)
				->
				{
					String selectedCandidate = candidatesList.getSelectionModel().getSelectedItem().getText();
		        	
			    	MouseButton button = mouseEvent.getButton();
	                if (button == MouseButton.PRIMARY)
	                {
	                	resetCandidatesChart();
	            		for (int i=0; i < candidatesNames.size(); i++)
	            			if (! candidatesNames.get(i).getText().equalsIgnoreCase(selectedCandidate))
	            			{
					        	Set<Node> nodes = candidatesChart.lookupAll(".series" + i);
					        	for (Node node : nodes) 
					        		node.setStyle("-fx-opacity: 0.1;");
	            			}
	                }
	                else if (button == MouseButton.SECONDARY)
	                {
	                	candidatesChart.getData().clear();
	                	for (CandidateChartData candidateChartData : candidatesChartData)
	                		if (selectedCandidate.equalsIgnoreCase(candidateChartData.getLineName()))
			        		{
			        			candidatesChart.getData().add(candidateChartData.getLineData());
			        			break;
			        		}
	                }
				}	
	        );
		}
	}

	@FXML
	private void onResetButtonClicked()
	{
		resetCandidatesChart();
	}
	
	private void resetCandidatesChart()
	{
		candidatesChart.getData().clear();	
		for (CandidateChartData candidateChartdata : candidatesChartData)
			candidatesChart.getData().add(candidateChartdata.getLineData());
		
		int counter = 0;
		for (XYChart.Series<String, Double> series : candidatesChart.getData()) 
		{
			Candidate candidate = CandidatesManager.getInstance().getCandidate(candidatesNames.get(counter++).getText());
			
			series.getNode().lookup(".chart-series-line").setStyle(candidate.getChartData().getLineColorStyle());
			
			for (XYChart.Data<String, Double> data : series.getData()) 
            {
            	Tooltip toolTip = new Tooltip(data.getXValue().toString() + "\n" + GeneralData.decimalFormat(data.getYValue()) + "\n" + candidate.getName());
            	toolTip.setTextAlignment(TextAlignment.CENTER);
                Tooltip.install(data.getNode(), toolTip);

                data.getNode().setOnMouseEntered(event -> data.getNode().getStyleClass().add("onHover"));
                data.getNode().setOnMouseExited(event -> data.getNode().getStyleClass().remove("onHover"));
            }
		}
		
		candidatesList.setItems(candidatesNames);
		
		// Textcolor of ListView items should correspond to line color.
		counter = 0;
		for (Label item : candidatesList.getItems())
		{
			Candidate candidate = CandidatesManager.getInstance().getCandidate(candidatesNames.get(counter++).getText());
			item.setStyle(candidate.getChartData().getTextColorStyle());
		}
	}
	
	@FXML
	private void onNormaliseButtonClicked()
	{
		if (actualValuesShown)
		{
			candidatesChartData = candidatesPresenter.getCandidatesChartDataNormalised();
			normaliseButton.setText("Actual Values");
		}
		else
		{
			candidatesChartData = candidatesPresenter.getCandidatesChartData();
			normaliseButton.setText("Normalised Values");
		}
		
		resetCandidatesChart();
		actualValuesShown = !actualValuesShown;
	}
	
	@FXML
	private void onExportButtonClicked()
	{
		ExcelManager excelManager = new ExcelManager();
		
		excelManager.makeCandidateWorkbook(candidates);
		excelManager.saveCandidateWorkbook();
	}
}

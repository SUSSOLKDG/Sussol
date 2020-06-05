package view.filtering.post;

import java.text.NumberFormat;

import org.controlsfx.control.RangeSlider;

import general.GeneralData;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import model.features.FeatureRange;
import utilities.UIManager;
import view.filtering.FilteringView;

@SuppressWarnings("unused")
public class FeatureRangeGridPane extends GridPane
{
	private FilteringView parentView;
	private FeatureRange featureRange;
	private RangeSlider rangeSlider;
	private TextField minTextField;
	private TextField maxTextField;
	
	private String featureName;
	private double currentMin;
	private double currentMax;
	
	public FeatureRangeGridPane(FeatureRange featureRange, FilteringView parentView) 
	{
		this.parentView = parentView;
		this.featureRange = featureRange;
		this.featureName = featureRange.getName();
		
		setGridLinesVisible(true);
				
		currentMin = featureRange.getMinValue();
		currentMax = featureRange.getMaxValue();
		
		getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 20));
		getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 60));
		getColumnConstraints().add(UIManager.getColumnConstraints(HPos.CENTER, 20));
		
		// First cell : name of feature
		HBox nameBox = new HBox();
			nameBox.setAlignment(Pos.CENTER_LEFT);
			nameBox.setPadding(new Insets(10));
			Label featureNameLabel = new Label(featureRange.getName());
			featureNameLabel.setStyle("-fx-text-fill: ivory; -fx-font-size: 12pt;");
			nameBox.getChildren().add(featureNameLabel);
		add(nameBox, 0, 0);
			
		// Second cell : sliders
		HBox sliderBox = new HBox();
			sliderBox.setAlignment(Pos.CENTER);
			sliderBox.setPadding(new Insets(10));
			rangeSlider = getSlider(featureRange.getMinValue(), featureRange.getMaxValue());
			HBox.setHgrow(rangeSlider, Priority.ALWAYS);
			sliderBox.getChildren().add(rangeSlider);
		add(sliderBox, 1, 0);
			
		// Third cell : input labels and individual Reset button.
		GridPane minMaxGridPane = new GridPane();
		
		minMaxGridPane.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.LEFT, 20));
		minMaxGridPane.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.LEFT, 50));
		minMaxGridPane.getColumnConstraints().add(UIManager.getColumnConstraints(HPos.LEFT, 30));
		minMaxGridPane.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 50));
		minMaxGridPane.getRowConstraints().add(UIManager.getRowConstraints(VPos.CENTER, 50));
		
			Label minLabel = new Label("Min : ");
			minLabel.setStyle("-fx-control-inner-background: #2f4f4f; -fx-text-fill: ivory; -fx-font-size: 12pt; -fx-padding: 10;");
			Label maxLabel = new Label("Max : ");
			maxLabel.setStyle("-fx-control-inner-background: #2f4f4f; -fx-text-fill: ivory; -fx-font-size: 12pt; -fx-padding: 10;");
			
			minMaxGridPane.add(minLabel, 0, 0);
			minMaxGridPane.add(maxLabel, 0, 1);
			
			minTextField = new TextField();
			minTextField.setText("" + currentMin);
			minTextField.setStyle("-fx-control-inner-background: #2f4f4f; -fx-text-fill: ivory; -fx-font-size: 12pt;");
			minTextField.setMaxWidth(100);
			
			// Note : we cannot work with a local variable here, 'final' stuff.
			minTextField.setOnAction
			(
				(event)
				->
				{
					if (! minTextField.getText().isEmpty())
					{
						try
						{
							double newValue = Double.parseDouble(minTextField.getText().replace(',', '.'));
							
							if (newValue < featureRange.getMinValue() || newValue > featureRange.getMaxValue())
								currentMin = featureRange.getMinValue();
							else
								currentMin = newValue;
										
							if (currentMin < currentMax)
							{
								minTextField.setText(String.valueOf(currentMin));
								rangeSlider.setLowValue(currentMin);
							}
							else
								reset();
						}
						catch (Exception e)
						{
							reset();
						}
					}
				}
			);
			
			maxTextField = new TextField();
			maxTextField.setText("" + currentMax);
			maxTextField.setStyle("-fx-control-inner-background: #2f4f4f; -fx-text-fill: ivory; -fx-font-size: 12pt;");
			maxTextField.setMaxWidth(100);
			
			maxTextField.setOnAction
			(
				(event)
				->
				{
					if (! maxTextField.getText().isEmpty())
					{
						try
						{
							double newValue = Double.parseDouble(maxTextField.getText().replace(',', '.'));
	
							if (newValue < featureRange.getMinValue() || newValue > featureRange.getMaxValue())
								currentMax = featureRange.getMaxValue();
							else
								currentMax = newValue;
									
							if (currentMin < currentMax)
							{
								maxTextField.setText(String.valueOf(currentMax));
								rangeSlider.setHighValue(currentMax);
							}
							else
								reset();
						}
						catch (Exception e)
						{
							reset();
						}
					}
				}
			);
			
			Button resetButton = new Button("Reset");
			resetButton.setStyle(GeneralData.normalButtonCss);
			GridPane.setRowSpan(resetButton, 2);
			
			resetButton.setOnAction
			(
				(event)
				->
				{
					reset();
				}
			);
			
			resetButton.addEventHandler
			(
				MouseEvent.MOUSE_ENTERED,
				new EventHandler<MouseEvent>() 
				{
					@Override
					public void handle(MouseEvent e) 
					{
						resetButton.setStyle("-fx-cursor: hand; -fx-text-fill: ivory; -fx-background-color: red;");
					}
				}
			);
			
			resetButton.addEventHandler
			(
				MouseEvent.MOUSE_EXITED,
				new EventHandler<MouseEvent>() 
				{
					@Override
					public void handle(MouseEvent e) 
					{
						resetButton.setStyle("-fx-cursor: default; -fx-text-fill: ivory; -fx-background-color: maroon;");
					}
				}
			);
			
			minMaxGridPane.add(resetButton, 2, 0);
			minMaxGridPane.add(minTextField, 1, 0);
			minMaxGridPane.add(maxTextField, 1, 1);
		add(minMaxGridPane, 2, 0);
		
		addEventFilter
		(
			MouseEvent.MOUSE_CLICKED, 
			(event)
			->
			{
				setStyle(GeneralData.selectedFeatureCss);
			}
		);
	}

	private RangeSlider getSlider(double lowValue, double highValue)
	{
		RangeSlider hSlider = new RangeSlider(lowValue, highValue, lowValue, highValue);
		
		hSlider.setMin(lowValue);
		hSlider.setMax(highValue);
		
    	hSlider.setShowTickMarks(true);
    	hSlider.setShowTickLabels(true);
    	hSlider.setBlockIncrement(10);
    	hSlider.setBackground(new Background(new BackgroundFill(Color.DARKGREY, null, null)));
    	hSlider.setPadding(new Insets(10, 20, 20, 10));
    	 
    	hSlider.lowValueProperty().addListener
    	(
			new ChangeListener<Object>() 
			{
				@Override
				public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) 
				{
					double minValue = hSlider.getLowValue();
		       		if (minValue < featureRange.getMaxValue())
		       		{
						currentMin = minValue;
						minTextField.setText(String.valueOf(GeneralData.decimalFormat(currentMin)));
		       		}
				}
		    }
    	);
    	hSlider.highValueProperty().addListener
    	(
			new ChangeListener<Object>() 
			{
				@Override
				public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) 
				{
					double maxValue = hSlider.getHighValue();
					if (maxValue > featureRange.getMinValue())
		       		{
						currentMax = maxValue;
						maxTextField.setText(String.valueOf(GeneralData.decimalFormat(currentMax)));
		       		}
				}
		    }
    	);
    	
    	return hSlider;
	}
	
	public void reset()
	{
		Platform.runLater
		(
			new Runnable()
			{
				@Override
				public void run() 
				{
					rangeSlider.setLowValue(rangeSlider.getMin());
					rangeSlider.setHighValue(rangeSlider.getMax());
					
					minTextField.setText(String.valueOf(featureRange.getMinValue()));
					maxTextField.setText(String.valueOf(featureRange.getMaxValue()));
					
					setStyle("-fx-background-color: #2f4f4f;");
				}
			}
		);
	}
	
	public Boolean hasChanged()
	{
		return (currentMin > featureRange.getMinValue() || currentMax < featureRange.getMaxValue());
	}
	
	public String getFeatureName()	{ return featureName;	}
	public double getCurrentMin() 	{ return currentMin; 	}
	public double getCurrentMax() 	{ return currentMax; 	}

}

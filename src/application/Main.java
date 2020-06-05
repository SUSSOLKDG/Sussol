package application;
	
import java.io.IOException;

import general.GeneralData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application 
{
	private static Scene scene;
	public static BorderPane root;
	private static String styleSheet;
	public static Stage mainStage;
	
	@Override
	public void start(Stage primaryStage) 
	{
		mainStage = primaryStage;
		styleSheet = getClass().getResource("application.css").toExternalForm();
		
		restart();
	}
	
	public static void main(String[] args) 
	{
		launch(args);
	}	
	
	public static void restart()
	{
		loadMainScreen();
		
		scene.getStylesheets().add(styleSheet);
		
		mainStage.setScene(scene);
		mainStage.setTitle("Sussol");
		mainStage.setOnCloseRequest
		(
			(WindowEvent event)
			->
			{
				if (GeneralData.filteringStage != null)
					GeneralData.filteringStage.close();
			}	
		);
		
		Screen screen = Screen.getPrimary();
	    Rectangle2D bounds = screen.getVisualBounds();
	    mainStage.setWidth(bounds.getWidth());
	    mainStage.setHeight(bounds.getHeight());
	    mainStage.setMaximized(true);
	    
		mainStage.show();
	}
	
	public static void loadMainScreen()
	{
		FXMLLoader loaderMain = new FXMLLoader();
		loaderMain.setLocation(Main.class.getResource("/view/MainScreen.fxml"));	
		
		try 
		{
			root = (BorderPane)loaderMain.load();
			scene = new Scene(root);
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void loadAllSolventsScreen()
	{
		FXMLLoader loaderAllSolvents = new FXMLLoader();
		loaderAllSolvents.setLocation(Main.class.getResource("/view/filtering/AllSolventsScreen.fxml"));	
		
		try 
		{
			root.setCenter((BorderPane)loaderAllSolvents.load());
		} 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void loadClusteringScreen()
	{
		FXMLLoader loaderClustering = new FXMLLoader();
		loaderClustering.setLocation(Main.class.getResource("/view/ClusteringScreen.fxml"));	
		
		try 
		{
			root.setCenter((BorderPane)loaderClustering.load());
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void loadCandidatesScreen()
	{
		FXMLLoader loaderCandidates = new FXMLLoader();
		loaderCandidates.setLocation(Main.class.getResource("/view/CandidatesScreen.fxml"));	
		
		try 
		{
			root.setCenter((BorderPane)loaderCandidates.load());
		} 
		catch (IOException e) { e.printStackTrace(); }	
	}
}

package utilities;

import java.awt.Color;
import java.io.*; 
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import application.Main;
import general.GeneralData;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import model.candidates.Candidate;

public class ExcelManager 
{
	private Workbook workbook;

	public ExcelManager() 
	{
		workbook = new XSSFWorkbook();
	}
	
	public void makeCandidateWorkbook(ArrayList<Candidate> candidates)
	{
		Sheet sheet = workbook.createSheet("Candidates");
		
		int rowCounter = 0;
		
		// Create a Row
        Row headerRow = sheet.createRow(rowCounter++);
        Cell cell = (Cell) headerRow.createCell(0);
        cell.setCellValue("Alternatives for " + GeneralData.theSolvent.getName());
        
        Row featureRow = sheet.createRow(rowCounter++);
        cell = (Cell) featureRow.createCell(0);
        cell = (Cell) featureRow.createCell(1);
        cell.setCellValue("Cas Number");
        
        int columnCounter = 2;
    	for (int i=0; i < GeneralData.featureNames.size(); i++)
    		if (! GeneralData.featureNames.get(i).contains("_Copy"))
    		{
	    		cell = (Cell) featureRow.createCell(columnCounter++);
	    		cell.setCellValue(GeneralData.featureNames.get(i));
    		}
        
        for (int i=0; i < candidates.size(); i++)
        {
        	Row candidateRow = sheet.createRow(rowCounter++);
        	cell = (Cell) candidateRow.createCell(0);
        	
        	Color tempColor = Color.decode(candidates.get(i).getSolvent().getEhsData().getEhsColor());
        	XSSFColor color = new XSSFColor(tempColor);
        	CellStyle cellStyle = workbook.createCellStyle();
        	((XSSFCellStyle) cellStyle).setFillBackgroundColor(color);
        	
        	cellStyle.setFillPattern(FillPatternType.FINE_DOTS);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(candidates.get(i).getSolvent().getName());
            
            cell = (Cell) candidateRow.createCell(1);
            cell.setCellValue(candidates.get(i).getSolvent().getCasNumber());
            
            columnCounter = 2;
        	for (int j=0; j < candidates.get(i).getSolvent().getFeatureValuesAsdouble().length; j++)
        		if (! GeneralData.featureNames.get(j).contains("_Copy"))
        		{
	            	cell = (Cell) candidateRow.createCell(columnCounter++);
	        		cell.setCellValue(candidates.get(i).getSolvent().getFeatureValuesAsdouble()[j]);
        		}
        }
	}
	
	public void saveCandidateWorkbook()
	{
		FileChooser fileChooser = new FileChooser();
		  
        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Exporting candidates to Excel.");
        fileChooser.setInitialDirectory(new File("c:\\temp"));
        
        // Show save file dialog
        File file = fileChooser.showSaveDialog(Main.mainStage);
        
        if (file != null)
        {
//        	File sameFileName = new File(file.getName());
//	        if (! file.renameTo(sameFileName))
//	        {
//	        	updateButton(candidatesView.exportButton, "File is open in Excel !");
//				
//	        	try 
//				{
//					Thread.sleep(2000);
//				}
//				catch (InterruptedException e) { e.printStackTrace(); }
//				
//	        	updateButton(candidatesView.exportButton, "Export");
//				
//	        	return;
//	        }
	        	
        	try 
        	{
        		OutputStream fileOut = new FileOutputStream(file.getAbsolutePath()); 
                workbook.write(fileOut);
                fileOut.close();

                workbook.close();
            }
        	catch (IOException e) { e.printStackTrace();
	        }
        }
    }
	
	private void updateButton(Button button, String text)
	{
		Platform.runLater
		(
			new Runnable()
			{
				@Override
				public void run() 
				{
					button.setText(text);				
				}
			}
		);
	}
}

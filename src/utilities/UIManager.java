package utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import model.solvents.Solvent;

public class UIManager
{
	public static void updateButton(Button button, String text)
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
	
	public static void updateLabel(Label label, String text) 
	{
		Platform.runLater
		(
			new Runnable()
			{
				@Override
				public void run() 
				{
					label.setText(text);				
				}
			}
		);
	}
	
	public static void updateSolventTextField(TextField textField, Solvent solvent) 
	{
		Platform.runLater
		(
			new Runnable()
			{
				@Override
				public void run() 
				{
					textField.setText(solvent.getCasNumber() + " : " + solvent.getName());
					textField.setPadding(new Insets(10));
					textField.setBackground
					(
						new Background
						(
							new BackgroundFill(Color.web(solvent.getEhsData().getEhsColor(), 1.0), new CornerRadii(2), new Insets(1))
						)
					);				
				}
			}
		);
	}
	
	public static void updateVisibility(Node node, boolean visible) 
	{
		Platform.runLater
		(
			new Runnable()
			{
				@Override
				public void run() 
				{
					node.setVisible(visible);			
				}
			}
		);
	}
	
	public static void selectBubble(Node node, String cssString) 
	{
		Platform.runLater
		(
			new Runnable()
			{
				@Override
				public void run() 
				{
					node.setStyle(cssString);	
				}
			}
		);
	}
	
	public static ColumnConstraints getColumnConstraints(HPos hAlignment, double percentage)
	{
		ColumnConstraints cc = new ColumnConstraints();
		
		cc.setHalignment(hAlignment);
		cc.setPercentWidth(percentage);
		
		return cc;
	}
	
	public static RowConstraints getRowConstraints(VPos vAlignment, double percentage)
	{
		RowConstraints rc = new RowConstraints();
		
		rc.setValignment(vAlignment);
		rc.setPercentHeight(percentage);
		
		return rc;
	}
	
	public static ArrayList<String> listOfLineColors = new ArrayList<String>
	( 
		Arrays.asList
		(
			"-fx-stroke: #5D8AA8;",
			"-fx-stroke: #F0F8FF;",
			"-fx-stroke: #FFBF00;",
			"-fx-stroke: #A4C639;",
			"-fx-stroke: #8DB600;",
			"-fx-stroke: #FBCEB1;",
			"-fx-stroke: #7FFFD4;",
			"-fx-stroke: #4B5320;",
			"-fx-stroke: #B2BEB5;",
			"-fx-stroke: #87A96B;",
			"-fx-stroke: #6D351A;",
			"-fx-stroke: #007FFF;",
			"-fx-stroke: #F4C2C2;",
			"-fx-stroke: #848482;",
			"-fx-stroke: #F5F5DC;",
			"-fx-stroke: #3D2B1F;",
			"-fx-stroke: #000000;",
			"-fx-stroke: #FAF0BE;",
			"-fx-stroke: #0000FF;",
			"-fx-stroke: #DE5D83;",
			"-fx-stroke: #CC0000;",
			"-fx-stroke: #B5A642;",
			"-fx-stroke: #66FF00;",
			"-fx-stroke: #BF94E4;",
			"-fx-stroke: #C32148;",
			"-fx-stroke: #FF007F;",
			"-fx-stroke: #D19FE8;",
			"-fx-stroke: #CD7F32;",
			"-fx-stroke: #964B00;",
			"-fx-stroke: #E7FEFF;",
			"-fx-stroke: #F0DC82;",
			"-fx-stroke: #800020;",
			"-fx-stroke: #8A3324;",
			"-fx-stroke: #BD33A4;",
			"-fx-stroke: #702963;",
			"-fx-stroke: #006B3C;",
			"-fx-stroke: #78866B;",
			"-fx-stroke: #FFEF00;",
			"-fx-stroke: #00CC99;",
			"-fx-stroke: #92A1CF;",
			"-fx-stroke: #ACE1AF;",
			"-fx-stroke: #007BA7;",
			"-fx-stroke: #2A52BE;",
			"-fx-stroke: #36454F;",
			"-fx-stroke: #DFFF00;",
			"-fx-stroke: #DE3163;",
			"-fx-stroke: #FFB7C5;",
			"-fx-stroke: #CD5C5C;",
			"-fx-stroke: #98817B;",
			"-fx-stroke: #E34234;",
			"-fx-stroke: #FBCCE7;",
			"-fx-stroke: #00FF6F;",
			"-fx-stroke: #9BDDFF;",
			"-fx-stroke: #8C92AC;",
			"-fx-stroke: #FF3800;",
			"-fx-stroke: #FF7F50;",
			"-fx-stroke: #FFF8DC;",
			"-fx-stroke: #FFFDD0;",
			"-fx-stroke: #00FFFF;",
			"-fx-stroke: #00008B;",
			"-fx-stroke: #5D3954;",
			"-fx-stroke: #A40000;",
			"-fx-stroke: #986960;",
			"-fx-stroke: #008B8B;",
			"-fx-stroke: #B8860B;",
			"-fx-stroke: #013220;",
			"-fx-stroke: #734F96;",
			"-fx-stroke: #8B008B;",
			"-fx-stroke: #003366;",
			"-fx-stroke: #556B2F;",
			"-fx-stroke: #03C03C;",
			"-fx-stroke: #03C03C;",
			"-fx-stroke: #966FD6;",
			"-fx-stroke: #E75480;",
			"-fx-stroke: #003399;",
			"-fx-stroke: #872657;",
			"-fx-stroke: #E9967A;",
			"-fx-stroke: #560319;",
			"-fx-stroke: #3C1414;",
			"-fx-stroke: #2F4F4F;",
			"-fx-stroke: #918151;",
			"-fx-stroke: #9400D3;",
			"-fx-stroke: #1560BD;",
			"-fx-stroke: #85BB65;",
			"-fx-stroke: #00009C;",
			"-fx-stroke: #614051;",
			"-fx-stroke: #A82EFF;",
			"-fx-stroke: #E5AA70;",
			"-fx-stroke: #E25822;",
			"-fx-stroke: #FC8EAC;",
			"-fx-stroke: #014421;",
			"-fx-stroke: #94005A;",
			"-fx-stroke: #F8F8FF;",
			"-fx-stroke: #996515;",
			"-fx-stroke: #FFDF00;",
			"-fx-stroke: #808080;",
			"-fx-stroke: #008000;",
			"-fx-stroke: #A0285A;",
			"-fx-stroke: #71A6D2;",
			"-fx-stroke: #FCF75E;",
			"-fx-stroke: #B2EC5D;",
			"-fx-stroke: #FF5C5C;",
			"-fx-stroke: #FFFFF0;",
			"-fx-stroke: #D73B3E;",
			"-fx-stroke: #C3B091;",
			"-fx-stroke: #646496;",
			"-fx-stroke: #B57EDC;",
			"-fx-stroke: #CCCCFF;",
			"-fx-stroke: #FFF0F5;",
			"-fx-stroke: #7CFC00;",
			"-fx-stroke: #BFFF00;",
			"-fx-stroke: #FF00FF;",
			"-fx-stroke: #800000;",
			"-fx-stroke: #191970;",
			"-fx-stroke: #3EB489;",
			"-fx-stroke: #BE4A95;",
			"-fx-stroke: #FFDB58;",
			"-fx-stroke: #000080;",
			"-fx-stroke: #808000;",
			"-fx-stroke: #FF7F00;",
			"-fx-stroke: #002147;",
			"-fx-stroke: #AEC6CF;",
			"-fx-stroke: #CFCFC4;",
			"-fx-stroke: #77DD77;",
			"-fx-stroke: #F49AC2;",
			"-fx-stroke: #FFB347;",
			"-fx-stroke: #FFD1DC;",
			"-fx-stroke: #B39EB5;",
			"-fx-stroke: #CB99C9;",
			"-fx-stroke: #FDFD96;",
			"-fx-stroke: #FFE5B4;",
			"-fx-stroke: #D1E231;",
			"-fx-stroke: #F0EAD6;",
			"-fx-stroke: #D3D3ED;",
			"-fx-stroke: #01796F;",
			"-fx-stroke: #FFC0CB;",
			"-fx-stroke: #93C572;",
			"-fx-stroke: #8E4585;",
			"-fx-stroke: #FF5A36;",
			"-fx-stroke: #C8B4DC;",
			"-fx-stroke: #FF00BE;",
			"-fx-stroke: #69359C;",
			"-fx-stroke: #E30B5D;",
			"-fx-stroke: #FF0000;",
			"-fx-stroke: #414833;",
			"-fx-stroke: #002366;",
			"-fx-stroke: #E0115F;",
			"-fx-stroke: #F4C430;",
			"-fx-stroke: #C2B280;",
			"-fx-stroke: #967117;",
			"-fx-stroke: #FFF5EE;",
			"-fx-stroke: #CB410B;",
			"-fx-stroke: #87CEEB;",
			"-fx-stroke: #CF71AF;",
			"-fx-stroke: #FFFAFA;",
			"-fx-stroke: #A7FC00;",
			"-fx-stroke: #4682B4;",
			"-fx-stroke: #E4D96F;",
			"-fx-stroke: #008080;",
			"-fx-stroke: #E2725B;",
			"-fx-stroke: #00755E;",
			"-fx-stroke: #30D5C8;",
			"-fx-stroke: #5B92E5;",
			"-fx-stroke: #8F00FF;",
			"-fx-stroke: #FFFFFF;",
			"-fx-stroke: #738678;",
			"-fx-stroke: #0F4D92;",
			"-fx-stroke: #FFFF00;"
		)
	); 

	// TODO : No longer used.
	public static ArrayList<String> listOfItemColors = new ArrayList<String>
	( 
		Arrays.asList
		(
			"-fx-text-fill: green; ",
			"-fx-text-fill: aliceblue; ",
			"-fx-text-fill: darkblue; ",
			"-fx-text-fill: aqua; ",
			"-fx-text-fill: fuchsia; ",
			"-fx-text-fill: bisque; ",
			"-fx-text-fill: darkkhaki; ",
			"-fx-text-fill: blue; ",
			"-fx-text-fill: gold; ",
			"-fx-text-fill: blueviolet; ",
			"-fx-text-fill: brown; ",
			"-fx-text-fill: cyan; ",
			"-fx-text-fill: cadetblue; ",
			"-fx-text-fill: chartreuse; ",
			"-fx-text-fill: coral; ",
			"-fx-text-fill: cornflowerblue; ",
			"-fx-text-fill: crimson; ",
			"-fx-text-fill: darkcyan; ",
			"-fx-text-fill: darkgray; ",
			"-fx-text-fill: darkgreen; ",
			"-fx-text-fill: darkmagenta; ",
			"-fx-text-fill: darkolivegreen; ",
			"-fx-text-fill: darkorange; ",
			"-fx-text-fill: darkred; ",
			"-fx-text-fill: darkseagreen; ",
			"-fx-text-fill: dodgerblue; ",
			"-fx-text-fill: forestgreen; ",
			"-fx-text-fill: indianred; ",
			"-fx-text-fill: lightblue; ",
			"-fx-text-fill: lightcoral; ",
			"-fx-text-fill: lightcyan; ",
			"-fx-text-fill: mediumorchid; ",
			"-fx-text-fill: mediumpurple; "
		)
	); 

	public static int counter;
	public static void resetColorCounter()
	{
		counter = 0;
	}
	public static String getNextLineColor()
	{
		return listOfLineColors.get(counter);
	}
	public static String getNextItemColor()
	{
		return listOfItemColors.get(counter);
	}
	public static void increaseColorCounter()
	{
		counter++;
	}
	public static int getColorCounter() 
	{
		return counter;
	}
	
}

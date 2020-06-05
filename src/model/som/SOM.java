package model.som;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.data.utils.transforms.Standardization;

import java.util.Vector;

public class SOM
{
    private SOMNet net;
    private Standardization dataNormalization;
    private int rowCount = 5;
    private int columnCount = 5;
    private double learningRate = 0.1;
    
	public SOMNet getNet() 								{ return net; }    
	public Standardization getDataNormalization() 		{ return dataNormalization; }
	public void setRowCount(int rowCount) 				{ this.rowCount = rowCount; }
	public void setColumnCount(int columnCount) 		{ this.columnCount = columnCount; }
	public void setLearningRate(double learningRate)	{ this.learningRate = learningRate; }
	
	public SOM()
    {
    }

    public int transform(DataRow tuple) 
    {
        double[] x = tuple.toArray();
        x = dataNormalization.standardize(x);

        SOMNeuron winner = net.match(x);
        return winner.getOutput();
    }

    public DataFrame fitAndTransform(DataFrame dataFrame) 
    {
        dataFrame = dataFrame.makeCopy();
        fit(dataFrame);
        
        for(int i = 0; i < dataFrame.rowCount(); i++) 
        {
            DataRow row = dataFrame.row(i);
            int clusterId = transform(row);
            row.setCategoricalTargetCell("cluster", "" + clusterId);            
        }
        return dataFrame;
    }

    public void fit(DataFrame batch) 
    {
        int dimension = batch.row(0).toArray().length;
        int rows = batch.rowCount();

        dataNormalization = new Standardization(batch);

        // Number of neuron rows is [Rows], number of neuron cols is [Cols], input dimension is [Input Dimension]
        net = new SOMNet(rowCount, columnCount, dimension);
        net.setLearningRate(learningRate);
        net.setPhase1Epochs(1000); 	// SOM training consists of a self-organizing phase and a converging phase.
        							// This parameter specifies the number of training inputs for the self-organizing phase.
        							// Note that an epoch simply means a training input here.

//        SussolLogger.getInstance().info("///////////// SOMNet after allocation\n");
//        net.printNeurons();
        
        // Initialize weights on the SOM network.
        Vector<Double> weight_lower_bounds = new Vector<Double>();
        Vector<Double> weight_upper_bounds = new Vector<Double>();
        for (int i=0; i < dimension; i++)
        {
            weight_lower_bounds.add(-1.0); 			// Lower bound for each input dimension is [Weight Lower Bound].
            weight_upper_bounds.add(1.0); 			// Upper bound for each input dimension is [Weight Upper Bound].
        }
        net.initialize(weight_lower_bounds, weight_upper_bounds);

//        SussolLogger.getInstance().info("\n///////////// SOMNet after initialisation\n");
//        net.printNeurons();
        
        // Set unique label for each neuron in SOM net.
        for (int r=0; r < rowCount; ++r)
        {
            for(int c=0; c < columnCount; c++)
            {
                net.getNeuronAt(r, c).setOutput(r * columnCount + c); // Neuron at row r and column c will have label "[r, c]".
            }
        }
        
//        SussolLogger.getInstance().info("\n///////////// SOMNet after labeling\n");
//        net.printNeurons();

        // The SOM net can be trained using a typical 3000 training inputs, repeated the above code for other training inputs.
        for (int i=0; i < rows; i++)
        {
            DataRow tuple = batch.row(i);
            double[] x = tuple.toArray();
            
            x = dataNormalization.standardize(x);
            net.train(x);
            
//            SussolLogger.getInstance().info("\n///////////// SOMNet during training, solvent " + i + "\n");
//            net.printNeurons();
        }
        
//        SussolLogger.getInstance().info("\n///////////// SOMNet after training\n");
//        net.printNeurons();
    }

}

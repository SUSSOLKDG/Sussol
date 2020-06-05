package model.som;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import utilities.SussolLogger;

public class SOMNet 
{
	private static final Random random = new Random();

	private int rows;
	private int columns;
	private double sigma;
	private double tau;
	private int phase1Epochs = 1000; // The iteration number in phase 1: the self-organizing phase.
	private int epochs = 0;
	private double learningRate = 0.1;
	private int inputDimension = 0;
	private final List<SOMNeuron> neurons = new ArrayList<>();

	public void setPhase1Epochs(int phase1Epochs)		{ this.phase1Epochs = phase1Epochs; }
	public double getLearningRate() 					{ return learningRate; }
	public void setLearningRate(double learningRate)	{ this.learningRate = learningRate; }	
	public List<SOMNeuron> getNeurons() 				{ return neurons; }

	public SOMNet()
	{
	}
	
	// Input_size should be smaller or equal to the actual input size.
	public SOMNet(int rows, int cols, int inputDimension)
	{
		this.rows = rows;
		this.columns = cols;
		this.inputDimension = inputDimension;

		for(int y=0; y < this.rows; ++y)
		{
			for(int x=0; x < this.columns; ++x)
			{
				SOMNeuron neuron = new SOMNeuron();
				neuron.setX(x);
				neuron.setY(y);
				neuron.setWeights(new double[this.inputDimension]);
				neuron.setOutput(x * this.rows + y);
				neurons.add(neuron);
			}
		}

		sigma = 0.707 * Math.sqrt((this.rows -1) * (this.rows -1) + (this.columns -1) * (this.columns -1));
		tau = 1000 / Math.log(sigma);
	}
	
	public SOMNeuron getNeuronAt(int row, int col)
	{
		return neurons.get(row * columns + col);
	}
	
	public Point getCoordinates(int clusterNumber)
	{
		Point result = null;
		
		for(int i=0; i < neurons.size(); i++)
		{
			if (neurons.get(i).getOutput() == clusterNumber)
			{
				result = new Point(neurons.get(i).getX(), neurons.get(i).getY());
				break;
			}
		}
		
		return result;
	}

	public void initialize(Vector<Double> lowest_weight, Vector<Double> highest_weight)
	{
		int [] seq = new int[neurons.size()];
		for(int i=0; i < neurons.size(); i++)
		{
			seq[i]=i;
		}
				
		for(int j=0; j < inputDimension; j++)
		{
			double inc = (highest_weight.get(j).doubleValue() - lowest_weight.get(j).doubleValue()) / neurons.size();
			
			for(int i=0; i < neurons.size(); i++)
			{
				int k = random.nextInt(neurons.size());
				int tmp = seq[i];
				seq[i] = seq[k];
				seq[k] = tmp;
			}
			
			for(int i=0; i < neurons.size(); i++)
			{
				SOMNeuron neuron = neurons.get(i);
				neuron.updateWeight(j, lowest_weight.get(j).doubleValue() + inc * seq[i] + inc * random.nextDouble());
			}
		}
		
		epochs = 0;
	}
	
	protected double getNewLearningRate(int n)
	{
		double result = learningRate * Math.exp(-n / tau);
		if (result < 0.01) 
			result = 0.01;
		return result;
	}
	
	protected double h(double distance, int n)
	{
		if (n < phase1Epochs) // Self-organizing phase.
		{
			double localSigma = sigma * Math.exp(- n / tau);
			return Math.exp(-distance * distance / (2 * localSigma * localSigma));
		}
		else // Convergence phase.
		{
			return 1;
		}
	}
	
	public void train(double[] input)
	{
		// Determine the winner neuron.
		SOMNeuron m_winner = null;
		double max_sum = Double.MAX_VALUE;
		
		for (int i=0; i < neurons.size(); i++)
		{
			SOMNeuron neuron = neurons.get(i);
			double sum = 0;
			for (int j=0; j < inputDimension; j++)
			{
				// System.out.println("j = " + j + "\tinput[j] = " + input[j]);
				double d = input[j] - neuron.getWeight(j);
				sum += d * d;
			}
			if (sum < max_sum)
			{
				m_winner = neuron;
				max_sum = sum;
			}
		}
		
		// Update neuron weights.
		for(int i=0; i < neurons.size(); i++)
		{
			SOMNeuron neuron = neurons.get(i);
			if (neuron == m_winner) 
				continue;
			double d = neuron.getDistance(m_winner);
			
			for(int j=0; j < inputDimension; j++)
			{
				double current_weight = neuron.getWeight(j);
				double weight_delta = getNewLearningRate(epochs) * h(d, epochs) * (input[j] - current_weight);
				neuron.updateWeight(j, current_weight + weight_delta);
			}
		}		
		
		epochs++;
	}
	
	public SOMNeuron match(double[] input)
	{
		SOMNeuron winner = null;
		double max_sum = Double.MAX_VALUE;
		
		for(int i=0; i < neurons.size(); i++)
		{
			SOMNeuron neuron= neurons.get(i);
			double sum = 0;
			for(int j=0; j < inputDimension; j++)
			{
				double d = input[j] - neuron.getWeight(j);
				sum += d * d;
			}
			if(sum < max_sum)
			{
				winner = neuron;
				max_sum = sum;
			}
		}

		return winner;
	}
	
	public void printNeurons()
	{
		for(int i=0; i < neurons.size(); i++)	;
//			SussolLogger.getInstance().info(neurons.get(i).toString());
	}
}

package org.allmon.client.controller.neuralrules;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
//import org.encog.ml.data.MLData;
//import org.encog.ml.data.MLDataPair;
//import org.encog.ml.data.MLDataSet;
//import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.RequiredImprovementStrategy;
import org.neuroph.nnet.learning.BackPropagation;

// Example for Encog 3.0.+
public class XORHelloWorld {

	public static double XOR_INPUT[][] = { 
		{0.20, 0.17, 0.01, 0,},//    0
		{0.32, 0.81, 0.19, 0.06,},// 0
		{0.68, 0.94, 0.33, 0.04,},// 0
		{0.31, 0.83, 0.17, 0.02,},// 0
		{0.06, 0.42, 0.01, 0,},//    0
		{0.15, 0.58, 0.05, 0,},//    0
		{0.66, 0.84, 0.34, 0.12,},// 0
		{0.75, 0.92, 0.44, 0.29,},// 1
		{0.72, 0.93, 0.55, 0.24,},// 1
		{0.76, 0.98, 0.54, 0.29,},// 1
		{0.62, 0.98, 0.54, 0.15,},// 1
		{0.77, 0.97, 0.41, 0.29,},// 1
		{0.59, 0.91, 0.24, 0.14,},// 1
		{0.68, 0.95, 0.38, 0.24,},// 1
		{0.28, 0.91, 0.10, 0.44,},// 1
		{0.18, 0.61, 0.10, 0.88,},// 0
		{0.13, 0.31, 0.0,  0.0,},//  0
		{0.03, 0.11, 0.0,  0.0,},//  0
	};
	// 1 in output means that the control was applied 
	public static double XOR_IDEAL[][] = 
		{{0}, {0}, {0}, {0}, {0}, {0}, {0}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {1}, {0}, {0}, {0}};
	
//	public static double XOR_INPUT[][] = { 	
//		{0.25, 0.77, 0.01, 0},//    1
//		{0.38, 0.81, 0.29, 0.06},// 1
//		{0.31, 0.83, 0.17, 0.02},// 1
//		{0.15, 0.42, 0.01, 0},//    1
//		{0.17, 0.58, 0.05, 0},//    1
//		{0.62, 0.84, 0.34, 0.12},// 1
//		//{0.77, 0.98, 0.54, 0.15},// 0
//		{0.74, 0.97, 0.41, 0.29},// 0
//		{0.69, 0.91, 0.24, 0.14},// 0
//		{0.63, 0.95, 0.38, 0.24},// 0
//		{0.10, 0.91, 0.10, 0.44}// 0
//	};
//	// 1 in output means that the control was applied 
//	public static double XOR_IDEAL[][] = 
//		{{1}, {1}, {1}, {1}, {1}, {1}, {0}, {0}, {0}, {0}};

	/**
	 * The main method.
	 * 
	 * @param args
	 *            No arguments are used.
	 */
	/*
	public static void main(final String args[]) {

		// create a neural network, without using a factory
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, 4));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		network.getStructure().finalizeStructure();
		network.reset();

		// create training data
		MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);
		
		// train the neural network
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		// reset if improve is less than 1% over 5 cycles
		train.addStrategy(new RequiredImprovementStrategy(5));
		
		int epoch = 1;
		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > 0.01);

		// test the neural network
		System.out.println("Neural Network Results:");
		for (MLDataPair pair : trainingSet) {
			final MLData output = network.compute(pair.getInput());
			System.out.println(pair.getInput().getData(0) + ","
					+ pair.getInput().getData(1) + ", actual="
					+ output.getData(0) + ",ideal="
					+ pair.getIdeal().getData(0));
		}

		Encog.getInstance().shutdown();
	}
	*/

}

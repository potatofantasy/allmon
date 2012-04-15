package org.allmon.client.controller.neuralrules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.allmon.client.controller.rules.State;
import org.encog.neural.data.NeuralDataPair;
import org.junit.Before;
import org.junit.Test;

public class NeuralRulesEncogTest {

	private NeuralRulesEncog rules;
	
	@Before
	public void setUp() {
		String [] resources = {"CPU", "IO", "SLA1", "SLA2"};
		String action = "Action.action";
		
		rules = new NeuralRulesEncog(resources, action);
		
		rules.train(INPUT_GOOD, OUTPUT_GOOD, INPUT_BAD, OUTPUT_BAD);
	}
	
	@Test
	public void test() {
		assertNotNull(rules);
	}

	public static double INPUT_GOOD[][] = { 
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
	public static double OUTPUT_GOOD[] = {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0};
	
	public static double INPUT_BAD[][] = { 	
		{0.25, 0.77, 0.01, 0},//    1   //{0.25, 0.17, 0.01, 0},//    1
		{0.38, 0.81, 0.29, 0.06},// 1
		{0.31, 0.83, 0.17, 0.02},// 1
		{0.15, 0.42, 0.01, 0},//    1
		{0.17, 0.58, 0.05, 0},//    1
		{0.62, 0.84, 0.34, 0.12},// 1
		//{0.77, 0.98, 0.54, 0.15},// 0
		{0.74, 0.97, 0.41, 0.29},// 0
		{0.69, 0.91, 0.24, 0.14},// 0
		{0.63, 0.95, 0.38, 0.24},// 0
		{0.10, 0.91, 0.10, 0.44}// 0
	};
	// 1 in output means that the control was applied 
	public static double OUTPUT_BAD[] = {1, 1, 1, 1, 1, 1, 0, 0, 0, 0};

	
	@Test
	public void test_() {
		Map<String, State> currentState = new HashMap<String, State>();
		currentState.put("CPU", new State("CPU", 0.7));
		currentState.put("IO", new State("IO", 0.95));
		currentState.put("SLA1", new State("SLA1", 0.7)); // .5
		currentState.put("SLA2", new State("SLA2", 0.1)); // .3
		
		for (NeuralDataPair pair : rules.convertToMLDataSet(currentState)) {
			double badControlOutput = rules.checkBadControl(pair);
			double goodControlOutput = rules.checkGoodControl(pair);
			System.out.println("badControlOutput:" + badControlOutput + ", goodControlOutput:" + goodControlOutput);
			
			assertEquals(badControlOutput, 0.1, 0.1);
			assertEquals(goodControlOutput, 0.4, 0.1);
		}
		
//		assertTrue(rules.checkRule(currentState));
//		assertEquals(2, 1);
	}
	
	
//	public static void main(final String args[]) {
//
//		// create a neural network, without using a factory
//		BasicNetwork network = new BasicNetwork();
//		network.addLayer(new BasicLayer(null, true, 4));
//		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3));
//		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 2));
//		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
//		network.getStructure().finalizeStructure();
//		network.reset();
//
////		Arrays.copyOfRange(original, from, to)
//		// create training data
//		MLDataSet trainingSet = new BasicMLDataSet(INPUT, IDEAL);
//		// train the neural network
//		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
//
//		int epoch = 1;
//		do {
//			train.iteration();
//			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
//			epoch++;
//		} while (train.getError() > 0.02);
//
//		// test the neural network
//		System.out.println("Neural Network Results:");
//		for (MLDataPair pair : trainingSet) {
//			final MLData output = network.compute(pair.getInput());
//			System.out.println(pair.getInput().getData(0) + ","
//					+ pair.getInput().getData(1) + ", actual="
//					+ output.getData(0) + ",ideal="
//					+ pair.getIdeal().getData(0));
//		}
//
//		//Encog.getInstance().shutdown();
//		
//		
//		XStream xstream = new XStream(new StaxDriver());
//		xstream.alias("nn", BasicNetwork.class);
//		String xml = xstream.toXML(network);
//		System.out.println(xml);
//		
//		BasicNetwork newNN = (BasicNetwork)xstream.fromXML(xml);
//		// test the neural network
//		System.out.println("Neural Network Results: 2!!");
//		for (MLDataPair pair : trainingSet) {
//			final MLData output = newNN.compute(pair.getInput());
//			System.out.println(pair.getInput().getData(0) + ","
//					+ pair.getInput().getData(1) + ", actual="
//					+ output.getData(0) + ",ideal="
//					+ pair.getIdeal().getData(0));
//		}
////		double[] input = {0.10, 0.91, 0.10, 0.44}; double[] output = {0.0d};
////		final MLData output2 = newNN.compute(input, output);
////		MLDataPair pair = //new BasicMLDataSet(); //new NeuralDataMapping();
//		double[][] input = {{0.12, 0.96, 0.45, 0.47}}; double[][] output = {{0.0d}};
//		MLDataSet dataSet = new BasicMLDataSet(input, null);
//		for (MLDataPair pair : dataSet) {
//			final MLData output2 = newNN.compute(pair.getInput());
//			System.out.println(output2.getData(0));
//		}
//		
//	}
	
}

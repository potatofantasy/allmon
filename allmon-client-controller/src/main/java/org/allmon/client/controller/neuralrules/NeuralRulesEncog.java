package org.allmon.client.controller.neuralrules;

import java.util.Map;

import org.allmon.client.controller.rules.State;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.RequiredImprovementStrategy;

public class NeuralRulesEncog {

	// TODO move to Action, Resource - introduce classes instead Strings
	private String [] resources;
	private String action;
	private BasicNetwork goodNetwork;
	private BasicNetwork badNetwork;
	
	public NeuralRulesEncog(String[] resources, String action) {
		this.resources = resources;
		this.action = action;
	}
	
	public String [] getResources() {
		return resources;
	}
	public String getAction() {
		return action;
	}
	
	public void train(double [][] goodDecisionsResources, double [] goodDecisionsControlFlag, 
			double [][] badDecisionsSet, double [] badDecisionsControlFlag) {
		goodNetwork = train(goodDecisionsResources, goodDecisionsControlFlag);
		badNetwork = train(badDecisionsSet, badDecisionsControlFlag);
	}
	
	public BasicNetwork train(double [][] decisionsResources, double [] decisionsControlFlag) {
		// validate training data set
		if (decisionsResources.length != decisionsControlFlag.length) {
			throw new RuntimeException("Dimensions of input and output must be same");
		}
		if (resources.length != decisionsResources[0].length) {
			throw new RuntimeException("Dimensions of declared resources and training set must be same");
		}
		
		// prepare network structure
		final BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, 4));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3));
//		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3));
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		network.getStructure().finalizeStructure();
		network.reset();
		
		// create training data
		double [][] output = new double[decisionsControlFlag.length][1];
		for (int i = 0; i < output.length; i++) {
			output[i][0] = decisionsControlFlag[i];
		}
		//MLDataSet trainingSet = new BasicMLDataSet(decisionsResources, output);
		NeuralDataSet trainingSet = new BasicNeuralDataSet(decisionsResources, output);
		
		// train the neural network
		final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
		// reset if improve is less than 1% over 5 cycles
		train.addStrategy(new RequiredImprovementStrategy(5));
		int epoch = 1;
		do {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Error:" + train.getError());
			epoch++;
		} while (train.getError() > 0.02 && epoch < 1000);
		
		Encog.getInstance().shutdown();
		
		return network;
	}
	
	@Deprecated
	public boolean checkRuleDeprecated(Map<String, State> currentState) {
		// prepare data set - rules resources indexes are same than the current state
		double [][] stateValues = new double[1][currentState.size()];
		int i = 0;
		for (String resource : resources) {
			State state = currentState.get(resource);
			stateValues[0][i++] = state.getValue();
		}
		NeuralDataSet trainingSet = new BasicNeuralDataSet(stateValues, null);

		// search the current state in the rules neural model
		// check if the current state can be considered as 'bad' state for control 
		double badControlOutput = 1;
		for (NeuralDataPair pair : trainingSet) {
			final NeuralData output = badNetwork.compute(pair.getInput());
			badControlOutput = output.getData(0);
		}
		
		// if badly applied control in the past is quite small then 
		// check the state if can be seen as for 'good' control 
		if (badControlOutput < 0.5) {
			double goodControlOutput = 0;
			for (NeuralDataPair pair : trainingSet) {
				final NeuralData output = goodNetwork.compute(pair.getInput());
				goodControlOutput = output.getData(0);
			}
			
			// if the current state is matching with well applied control then
			// set this as appropriate for termination - otherwise do not terminate
			if (goodControlOutput > 0.9) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkRule(Map<String, State> currentState) {
		// prepare data set - rules resources indexes are same than the current state
		NeuralDataSet dataSet = convertToMLDataSet(currentState);
		
		for (NeuralDataPair pair : dataSet) {
			// search the current state in the rules neural model
			// check if the current state can be considered as 'bad' state for control 
			double badControlOutput = checkBadControl(pair);
			
			// if badly applied control in the past is quite small then 
			// check the state if can be seen as for 'good' control 
			if (badControlOutput < 0.5) {
				double goodControlOutput = checkGoodControl(pair);
				
				// if the current state is matching with well applied control then
				// set this as appropriate for termination - otherwise do not terminate
				if (goodControlOutput > 0.9) {
					return true;
				}
			}
		}
		return false;
	}
	
	NeuralDataSet convertToMLDataSet(Map<String, State> currentState) {
		double [][] stateValues = new double[1][currentState.size()];
		int i = 0;
		for (String resource : resources) {
			State state = currentState.get(resource);
			stateValues[0][i++] = state.getValue();
		}
		return new BasicNeuralDataSet(stateValues, null);
	}
	double checkBadControl(NeuralDataPair pair) {
		final NeuralData output = badNetwork.compute(pair.getInput());
		return output.getData(0);
	}
	double checkGoodControl(NeuralDataPair pair) {
		final NeuralData output = goodNetwork.compute(pair.getInput());
		return output.getData(0);
	}

	
}

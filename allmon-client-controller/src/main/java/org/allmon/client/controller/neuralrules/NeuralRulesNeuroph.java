package org.allmon.client.controller.neuralrules;

import java.util.Map;

import org.allmon.client.controller.rules.State;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Neural Network holds only normalized data - 
 *
 */
public class NeuralRulesNeuroph {

	// TODO move to Action, Resource - introduce classes instead Strings
	final private Resource [] resources;
	final private String action;
	private MultiLayerPerceptron goodNetwork;
	private MultiLayerPerceptron badNetwork;
	
	private static final double BAD_CONTROL_OUTPUT_ACTIVATION_THRESHOLD = 0.25;
	private static final double GOOD_CONTROL_OUTPUT_ACTIVATION_THRESHOLD = 0.75;
	
	public NeuralRulesNeuroph(String action, Resource [] resources) {
		this.action = action;
		this.resources = resources;
	}
	
	public Resource [] getResources() {
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
	
	public MultiLayerPerceptron train(double [][] decisionsResources, double [] decisionsControlFlag) {
		// validate training data set
		if (resources.length != decisionsResources[0].length) {
			throw new RuntimeException("Dimensions of declared resources and training set must be same");
		}
		if (decisionsResources.length != decisionsControlFlag.length) {
			throw new RuntimeException("Dimensions of input and output must be same");
		}
		
		// TODO ???? - a ‘rule of thumb’ called the Baum-Haussler rule:
		// Nhidden <= (Ntrain * Etolerance) / (Ninput * Noutput)
		// Where Nhidden is the number of hidden nodes, Ntrain is the number of training patterns, 
		// Etolerance is the error we desire of the network, Ninput and Noutput are the number of input and 
		// output nodes respectively. This rule of thumb generally ensures that the network generalises 
		//rather than memorises the problem.
		int firstLayerNeurons = resources.length;
		int secondLayerNeurons = resources.length - 1;
		
		// prepare network structure
        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(
        		TransferFunctionType.SIGMOID, 
        		resources.length, // input layer count - same as number of dimensions used
        		firstLayerNeurons, secondLayerNeurons, 
        		1);
        
		// create normalized training data
        TrainingSet<SupervisedTrainingElement> trainingSet =
        	createNormalizedTrainingSet(decisionsResources, decisionsControlFlag);
        
        // learn the training set
		myMlPerceptron.setLearningRule(new MomentumBackpropagation());
		MomentumBackpropagation mback = (MomentumBackpropagation)myMlPerceptron.getLearningRule();
        // enable batch if using MomentumBackpropagation
		mback.setBatchMode(true);
		System.out.print("Training neural network...");
        mback.learn(trainingSet, 50000);
    	
    	System.out.printf(", network trained..., TotalNetworkError: %4.4f, CurrentIteration: %5d \n", 
    			mback.getTotalNetworkError(), mback.getCurrentIteration());
    	
		return myMlPerceptron;
	}
	
	public boolean checkRule(Map<String, State> currentState) {
		System.out.println("ckeckRule for state:" + currentState.values());

		// prepare data set - rules resources indexes are same than the current state
		SupervisedTrainingElement element;
		try {
			element = convertToMLDataSet(currentState);
		} catch (NeuralRulesException e) {
			System.out.println("ckeckRule --> " + e.getMessage());
			return false;
		}
		
		// search the current state in the rules neural model
		// check if the current state can be considered as 'bad' state for control 
		double badControlOutput = checkBadControl(element);
		System.out.println("badControlOutput: " + badControlOutput);
		
		// if badly applied control in the past is quite small then 
		// check the state if can be seen as for 'good' control 
		if (badControlOutput < BAD_CONTROL_OUTPUT_ACTIVATION_THRESHOLD) {
			double goodControlOutput = checkGoodControl(element);
			System.out.println("goodControlOutput: " + goodControlOutput);
			
			// if the current state is matching with well applied control then
			// set this as appropriate for termination - otherwise do not terminate
			if (goodControlOutput > GOOD_CONTROL_OUTPUT_ACTIVATION_THRESHOLD) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Neural Network is trained using normalized data.
	 * 
	 * @param decisionsResources
	 * @param decisionsControlFlag
	 * @return
	 */
	private TrainingSet<SupervisedTrainingElement> createNormalizedTrainingSet(
			double [][] decisionsResources, double [] decisionsControlFlag) {
		// find max/min values for a resource
		for (int r = 0; r < resources.length; r++) {
			double min = resources[r].getMinValue();
			double max = resources[r].getMaxValue();
			for (int i = 0; i < decisionsResources.length; i++) {
				double value = decisionsResources[i][r];
				if (value < min) { min = value; }
				if (value > max) { max = value; }
			}
			resources[r].setMinMaxValue(min, max);
		}
		
		// create and feed training set with data
        TrainingSet<SupervisedTrainingElement> trainingSet = 
			new TrainingSet<SupervisedTrainingElement>(resources.length, 1);
		for (int i = 0; i < decisionsResources.length; i++) {
			double [] originalSystemStateDecisionResources = decisionsResources[i];
			double [] normalizedSystemStateDecisionResources = new double[decisionsResources[i].length];
			for (int r = 0; r < resources.length; r++) {
				normalizedSystemStateDecisionResources[r] = 
//					originalSystemStateDecisionResources[r];
					resources[r].normalize(originalSystemStateDecisionResources[r]);
			}
	        trainingSet.addElement(new SupervisedTrainingElement(
	        		normalizedSystemStateDecisionResources, new double [] {decisionsControlFlag[i]}));
		}
		return trainingSet;
	}
	
	SupervisedTrainingElement convertToMLDataSet(Map<String, State> currentState) throws NeuralRulesException {
		double [] stateValues = new double[currentState.size()];
		int i = 0;
		for (Resource resource : resources) {
			State state = currentState.get(resource.getName());
			if (state == null) {
				throw new NeuralRulesException("Resource [" + resource + "] defined as one of NN inputs does not exist in App current state");
			}
			double nomormalizedValue = resource.normalize(state.getValue());
			System.out.println(">> state: " + state + ", original value: " + state.getValue() + " normalized value: " + nomormalizedValue);
			stateValues[i++] = nomormalizedValue;
		}
		System.out.println();
		return new SupervisedTrainingElement(stateValues, null);
	}
	double checkBadControl(SupervisedTrainingElement element) {
		badNetwork.setInput(element.getInput());
		badNetwork.calculate();
        double[] networkOutput = badNetwork.getOutput();
        return networkOutput[0];
	}
	double checkGoodControl(SupervisedTrainingElement element) {
		goodNetwork.setInput(element.getInput());
		goodNetwork.calculate();
        double[] networkOutput = goodNetwork.getOutput();
        return networkOutput[0];
	}
	
	public String serialize() {
		final XStream xstream = new XStream(new StaxDriver());
		//xstream.alias(NeuralRulesNeuroph.class);
		String xml = xstream.toXML(this);
		//System.out.println(xml);
		return xml;
	}
	public static NeuralRulesNeuroph instantiateSerialized(String xml) {
		final XStream xstream = new XStream(new StaxDriver());
		return (NeuralRulesNeuroph)xstream.fromXML(xml);
	}
}

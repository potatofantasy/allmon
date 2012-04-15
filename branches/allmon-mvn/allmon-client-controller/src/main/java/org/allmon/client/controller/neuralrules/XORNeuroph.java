package org.allmon.client.controller.neuralrules;

import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

public class XORNeuroph {

    /**
     * Runs this sample
     */
    public static void main(String[] args) {
    	
        // create training set (logical XOR function)
        TrainingSet<SupervisedTrainingElement> trainingSet = new TrainingSet<SupervisedTrainingElement>(3, 1);
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{0, 0, 0}, new double[]{0}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{0, 1, 0}, new double[]{1}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{1, 0, 0}, new double[]{1}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{1, 1, 0}, new double[]{0}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{1, 1, 0}, new double[]{0})); // stronger
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{0, 0, 1}, new double[]{1}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{0, 1, 1}, new double[]{0}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{1, 0, 1}, new double[]{0}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{1, 1, 1}, new double[]{1}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{1, 1, 0.95}, new double[]{1})); // stronger
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{1, 1, 0.90}, new double[]{1})); // stronger

        // create multi layer perceptron
        MultiLayerPerceptron myMlPerceptron = 
        	new MultiLayerPerceptron(TransferFunctionType.TANH, 3, 10, 1);
        
        myMlPerceptron.setLearningRule(new MomentumBackpropagation());
        
        // enablebatch if using MomentumBackpropagation
        if(myMlPerceptron.getLearningRule() instanceof MomentumBackpropagation) {
        	((MomentumBackpropagation)myMlPerceptron.getLearningRule()).setBatchMode(true);
        }
        
        // learn the training set
        System.out.println("Training neural network...");
//        myMlPerceptron.learn(trainingSet);
    	((MomentumBackpropagation)myMlPerceptron.getLearningRule()).learn(trainingSet, 10000);
    	
        // test perceptron
        System.out.println("Testing trained neural network");
        testNeuralNetwork(myMlPerceptron, trainingSet);
        
        // save trained neural network
        //myMlPerceptron.save("myMlPerceptron.nnet");

//        // load saved neural network
//        //NeuralNetwork loadedMlPerceptron = NeuralNetwork.load("myMlPerceptron.nnet");
//        NeuralNetwork loadedMlPerceptron = myMlPerceptron;
//        
//        // test loaded neural network
//        System.out.println("Testing loaded neural network");
//        testNeuralNetwork(loadedMlPerceptron, trainingSet);
    }

    /**
     * Prints network output for the each element from the specified training set.
     * @param neuralNet neural network
     * @param trainingSet training set
     */
    public static void testNeuralNetwork(NeuralNetwork neuralNet, TrainingSet<SupervisedTrainingElement> trainingSet) {
        for(SupervisedTrainingElement trainingElement : trainingSet.elements()) {
            neuralNet.setInput(trainingElement.getInput());
            neuralNet.calculate();
            double[] networkOutput = neuralNet.getOutput();

            System.out.print("Input: " + Arrays.toString( trainingElement.getInput() ) );
            System.out.println(" Output: " + Arrays.toString( networkOutput) );
        }
    }

}

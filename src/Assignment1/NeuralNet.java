package ece.CPEN502;

import ece.CPEN502.Interface.NeuralNetInterface;

public class NeuralNet implements NeuralNetInterface {
    /** Initialization:
     * input : 2 , hidden : 4, output: 1
     * XOR training set
     * weights randomize in between min = -0.5 and max = +0.5
     * learning rate : 0.2
     * momentum : 0.0
     * error threshold: 0.05
    **/

    // private members of this class
    private int numInput;
    private int numHidden;
    private int numOutput;
    private double learning_rate;
    private double momentum;
    private double min_weight;
    private double max_weight;
    private double errorThreshold;

    // weights
    private double [][] weightInputToHidden; // The weight matrix input-hidden
    private double [] weightHiddenToOutput; // The weights hidden-output

    private double [][] lastWeightChangeInputToHidden; // Used for momentum term
    private double [] lastWeightChangeHiddenToOutput; // Used for momentum tern

    // neuron vector
    private double [] hiddenLayer;
    private double output;

    // constructor
    public NeuralNet (int argInputNum, int argHiddenNum, int argOutputNum, int argLearningRate,
                      double argMomentum, double min_weight, double max_weight, boolean datasetType) {

    }


    /**
     * Algorithm
     * Step 1 : Initialize Weights
    **/


    /**
     * Step 2 : Forward Propagation
     **/

    /**
     * Step 3 : Backward Propagation
     **/
}

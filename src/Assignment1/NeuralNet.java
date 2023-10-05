package Assignment1;

import java.io.*;
import java.util.Random;

public class NeuralNet implements NeuralNetInterface, CommonInterface   {
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
    private double learningRate;
    private double momentum;


    // weights
    private double [][] weightInputToHidden; // The weight matrix input-hidden
    private double [][] weightHiddenToOutput; // The weights hidden-output

    private double [][] lastWeightChangeInputToHidden; // Used for momentum term
    private double [] lastWeightChangeHiddenToOutput; // Used for momentum tern

    // neuron vector
    private double [] hiddenLayer;
    private double[] inputs;       // Input values
    private double[] outputs;

    // Constructor
    public NeuralNet(int numInput, int numHidden, int numOutput, double learningRate, double momentum) {
        this.numInput = numInput;
        this.numHidden = numHidden;
        this.numOutput = numOutput;

        this.inputs = new double[numInput];
        this.hiddenLayer = new double[numHidden];
        this.outputs = new double[numOutput];

        this.weightInputToHidden = new double[numInput + 1][numHidden];
        this.weightHiddenToOutput = new double[numHidden + 1][numOutput];

        this.learningRate = learningRate;
        this.momentum = momentum;

        initializeWeights();
    }

    // Implement the sigmoid activation function
    @Override
    public double sigmoid(double x) {
        return 2.0 / (1.0 + Math.exp(-x)) - 1.0;
    }

    // Implement the custom sigmoid activation function
    @Override
    public double customSigmoid(double x) {
        // Modify this method as needed
        return 0.0;
    }
    /**
     * Algorithm
     * Step 1 : Initialize Weights
     **/
    // Initialize weights to random values
    @Override
    public void initializeWeights() {
        Random rand = new Random();
        for (int i = 0; i <= numInput; i++) {
            for (int j = 0; j < numHidden; j++) {
                weightInputToHidden[i][j] = -0.5 + rand.nextDouble();
            }
        }
        for (int i = 0; i <= numHidden; i++) {
            for (int j = 0; j < numOutput; j++) {
                weightHiddenToOutput[i][j] = -0.5 + rand.nextDouble();
            }
        }
    }

    // Initialize weights to zero
    @Override
    public void zeroWeights() {
        for (int i = 0; i <= numInput; i++) {
            for (int j = 0; j < numHidden; j++) {
                weightInputToHidden[i][j] = 0.0;
            }
        }
        for (int i = 0; i <= numHidden; i++) {
            for (int j = 0; j < numOutput; j++) {
                weightHiddenToOutput[i][j] = 0.0;
            }
        }
    }

    /**
     * Step 2 : Forward Propagation
     **/
    @Override
    public double outputFor(double[] X) {
        // Implement the feedforward calculation
        // Modify this method to compute the output of the neural network
        if (X.length != numInput) {
            throw new IllegalArgumentException("Input vector size does not match the number of input neurons.");
        }

        // Step 1: Compute activations of hidden layer neurons
        for (int j = 0; j < numHidden; j++) {
            double activation = 0.0;
            // Include bias input (last input element)
            for (int i = 0; i <= numInput; i++) {
                if (i == numInput) {
                    // Bias input
                    activation += bias * weightInputToHidden[i][j];
                } else {
                    activation += X[i] * weightInputToHidden[i][j];
                }
            }
            hiddenLayer[j] = sigmoid(activation);
        }

        // Step 2: Compute activations of output layer neurons
        for (int k = 0; k < numOutput; k++) {
            double activation = 0.0;
            // Include bias input (last hidden layer element)
            for (int j = 0; j <= numHidden; j++) {
                if (j == numHidden) {
                    // Bias input
                    activation += bias * weightHiddenToOutput[j][k];
                } else {
                    activation += hiddenLayer[j] * weightHiddenToOutput[j][k];
                }
            }
            outputs[k] = sigmoid(activation);
        }

        return outputs[0];
    }

    /**
     * Step 3 : Backward Propagation
     **/
    @Override
    public double train(double[] X, double argValue) {
        // Implement the backpropagation training algorithm
        // Modify this method to update weights and return the error
        if (X.length != numInput) {
            throw new IllegalArgumentException("Input vector size does not match the number of input neurons.");
        }

        // Step 1: Compute the network's output for the input
        double predictedOutput = outputFor(X);

        // Step 2: Calculate the error (the difference between predicted and target output)
        double error = argValue - predictedOutput;

        // Step 3: Compute the output layer deltas and update output layer weights
        for (int k = 0; k < numOutput; k++) {
            double deltaK = error * predictedOutput * (1.0 - predictedOutput); // Assuming sigmoid activation
            for (int j = 0; j < numHidden; j++) {
                double weightChange = learningRate * deltaK * hiddenLayer[j];
                weightHiddenToOutput[j][k] += weightChange;
            }
        }

        // Step 4: Compute the hidden layer deltas and update hidden layer weights
        for (int j = 0; j < numHidden; j++) {
            double deltaJ = hiddenLayer[j] * (1.0 - hiddenLayer[j]);
            double sum = 0.0;
            for (int k = 0; k < numOutput; k++) {
                sum += deltaJ * weightHiddenToOutput[j][k];
            }
            deltaJ *= sum;

            for (int i = 0; i <= numInput; i++) {
                double weightChange = learningRate * deltaJ * (i == numInput ? bias : X[i]);
                weightInputToHidden[i][j] += weightChange;
            }
        }

        // Step 5: Calculate and return the error (squared error)
        return 0.5 * error * error;
    }

    // Implement the save method
    @Override
    public void save(File argFile) {
        // Implement code to save neural network weights to a file
    }

    // Implement the load method
    @Override
    public void load(String argFileName) throws IOException {
        // Implement code to load neural network weights from a file
    }
}




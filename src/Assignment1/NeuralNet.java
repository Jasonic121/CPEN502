package Assignment1;

import java.io.File;
import java.io.IOException;
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
    private double [][] lastWeightChangeHiddenToOutput; // Used for momentum tern

    // neuron vector
    private double [] hiddenLayer;
    private double[] inputs;       // Input values
    private double[] outputs;

    private int datasetChoice;
    // Constructor
    public NeuralNet(int numInput, int numHidden, int numOutput, double learningRate, double momentum, int datasetChoice) {
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

        // Initialize arrays for tracking previous weight changes
        this.lastWeightChangeInputToHidden = new double[numInput + 1][numHidden];
        this.lastWeightChangeHiddenToOutput = new double[numHidden + 1][numOutput];

        this.datasetChoice = datasetChoice;

        initializeWeights();
    }

    // Implement the binary sigmoid activation function
    @Override
    public double sigmoid(double x) {
        if (datasetChoice == 1) {
            return 1.0 / (1.0 + Math.exp(-x));
        } else {
            return -1.0 + (2.0 / (1.0 + Math.exp(-x)));
        }
    }

    // Implement the custom sigmoid activation function
    @Override
    public double customSigmoid(double x) {
        int a = -1;
        int b = 1;
        return (double)(b - a) / (1 + Math.exp(-x)) + a;
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
//        System.out.println("Hidden Layer Activations:");
        for (int j = 0; j < numHidden; j++) {
            double activation = 0.0;
            // Include bias input (last input element)
            for (int i = 0; i <= numInput; i++) {
                if (i == numInput) {
                    // Bias input
                    activation += bias * weightInputToHidden[i][j]; // Bias also have weight
                } else {
                    activation += X[i] * weightInputToHidden[i][j];
                }
            }
            hiddenLayer[j] = sigmoid(activation);
//            System.out.println("Hidden Neuron " + j + ": " + hiddenLayer[j]);
        }

        // Step 2: Compute activations of output layer neurons
//        System.out.println("Output Layer Activations:");
        for (int k = 0; k < numOutput; k++) {
            double activation = 0.0;
            // Include bias input (last hidden layer element)
            for (int j = 0; j <= numHidden; j++) {
                if (j == numHidden) {
                    // Bias input
                    activation += bias * weightHiddenToOutput[j][k]; // Bias also have weight
                } else {
                    activation += hiddenLayer[j] * weightHiddenToOutput[j][k];
                }
            }
            outputs[k] = sigmoid(activation);
//            System.out.println("Output Neuron " + k + ": " + outputs[k]);

        }

        return outputs[0];
    }

    /**
     * Step 3 : Backward Propagation
     **/
    @Override
    public void train(double[] X, double argValue, double fOutput) {
        // Implement the backpropagation training algorithm
        // Modify this method to update weights and return the error
        if (X.length != numInput) {
            throw new IllegalArgumentException("Input vector size does not match the number of input neurons.");
        }

        // Step 1: The predicted output for the forward propagation
        double forwardOutput = fOutput;

        // Step 2: Calculate the error (the difference between predicted and target output)
        double error = argValue - forwardOutput; // Ci - yi
        double deltaK;
        // Compute the output layer deltas
        if (datasetChoice == 1) {
            deltaK = error * forwardOutput * (1.0 - forwardOutput); // Assuming sigmoid activation
        }
        else {
            deltaK = error * (0.5) * (1 - Math.pow(forwardOutput, 2));
        }

        // Step 3: Update output layer weights
//        System.out.println("Hidden-to-Output Layer Weight Changes:");
        for (int k = 0; k < numOutput; k++) {
            for (int j = 0; j <= numHidden; j++) {
                double weightChange = learningRate * deltaK * (j == numHidden ? bias : hiddenLayer[j]); // rho  * delta * xi
                weightChange += momentum * lastWeightChangeHiddenToOutput[j][k]; // alpha * delta w
                weightHiddenToOutput[j][k] += weightChange;
//                System.out.println("Weight Change (" + j + " -> " + k + "): " + weightChange);
                lastWeightChangeHiddenToOutput[j][k] = weightChange; // Update the previous weight change

            }
        }

        // Step 4: Compute the hidden layer deltas and update hidden layer weights
//        System.out.println("Input-to-Hidden Layer Weight Changes:");
        double deltaJ;
        for (int j = 0; j < numHidden; j++) {
            if (datasetChoice == 1){
                deltaJ = hiddenLayer[j] * (1.0 - hiddenLayer[j]);
            }
            else {
                deltaJ = (0.5) * (1 - Math.pow(hiddenLayer[j], 2));
            }
            double sum = 0.0;
            for (int k = 0; k < numOutput; k++) {
                sum += deltaK * weightHiddenToOutput[j][k];
            }
            deltaJ *= sum;

            for (int i = 0; i <= numInput; i++) {
                double weightChange = learningRate * deltaJ * (i == numInput ? bias : X[i]); // rho  * delta * xi
                weightChange += momentum * lastWeightChangeInputToHidden[i][j]; // alpha * delta w
                weightInputToHidden[i][j] += weightChange;
//                System.out.println("Weight Change (" + i + " -> " + j + "): " + weightChange);
                lastWeightChangeInputToHidden[i][j] = weightChange; // Update the previous weight change
            }
        }
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




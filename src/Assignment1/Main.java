package Assignment1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) {
        // Define the neural network configuration
        int numInput = 2;
        int numHidden = 4;
        int numOutput = 1;
        double learningRate = 0.2;
        double momentum = 0.0;

        // Create a neural network instance
        NeuralNet neuralNet = new NeuralNet(numInput, numHidden, numOutput, learningRate, momentum);

        // Define the XOR training set
        double[][] inputPatterns = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[][] targetOutputs = {{0}, {1}, {1}, {0}};

        // Train the neural network on the XOR problem
        int maxEpochs = 10000;  // Adjust as needed
        double targetError = 0.05;  // Adjust as needed
        double totalError;

        // Create a PrintWriter to write to the CSV file
        try (PrintWriter writer = new PrintWriter(new FileWriter("src/Assignment1/script/graphing_data.csv"))) {
            writer.println("Epoch,totalError");

            for (int epoch = 0; epoch < maxEpochs; epoch++) {
                totalError = 0.0;

                for (int i = 0; i < inputPatterns.length; i++) {
                    double[] input = inputPatterns[i];
                    double[] target = targetOutputs[i];
//                    System.out.println();
//                    System.out.println("Training.... targetOutput: "+ target[0]);

                    // Forward pass
                    double output = neuralNet.outputFor(input);
//                    System.out.println("output: "+ output);

                    // Backpropagation and training
                    neuralNet.train(input, target[0], output);

                    // Sum the errors
                    totalError += Math.pow(output - target[0], 2);
                }

                // Calculate the average error for this epoch (RMS error)
                totalError /= 2;

                // Check if the training has reached the target error
                if (totalError < targetError) {
                    System.out.println("Training completed in " + (epoch + 1) + " epochs.");
                    break;
                }

                // Printout for Epoch and averageError
                System.out.println("Epoch:" + epoch + "  "+"totalError:" + totalError);
                writer.print(epoch);
                writer.print(",");
                writer.println(totalError);

            }


        // Test the trained neural network
        System.out.println("Testing trained neural network:");
        for (int i = 0; i < inputPatterns.length; i++) {
            double[] input = inputPatterns[i];
            double output = neuralNet.outputFor(input);
            System.out.println("Input: " + input[0] + ", " + input[1] + " => Output: " + output);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

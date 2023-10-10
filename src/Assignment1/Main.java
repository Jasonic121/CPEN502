package Assignment1;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
public class Main {

    public static void main(String[] args) {
        // Initialize the rerun flag
        boolean rerun = true;

        // Create a Scanner to read user input
        Scanner scanner = new Scanner(System.in);

        while(rerun) {
            // Prompt the user for input: 1 for binary, 2 for bipolar
            System.out.println("Enter '1' for binary XOR dataset or '2' for bipolar XOR dataset: ");
            int datasetChoice = scanner.nextInt();

            // Prompt the user for input: Momentum
            System.out.println("Enter Momentum: ");
            double momentum = scanner.nextDouble();

            // Prompt the user for input: Number of trials to run
            System.out.println("Enter the number of trials you want to run: ");
            int trialnum = scanner.nextInt();
            int[] EpochsFrequency = new int[trialnum];
            for (int trials = 0; trials < trialnum ; trials++) {
                EpochsFrequency[trials] = nnStarter(datasetChoice, momentum);
            }


            // Print the frequencies
            for (int i = 0; i < EpochsFrequency.length; i++) {
                System.out.println("Trial " + (i + 1) + " Epochs: " + EpochsFrequency[i]);
            }

            // Save EpochsFrequency to a CSV file
            saveEpochsFrequencyToCSV(EpochsFrequency, "src/Assignment1/script/Graph_EpochFrequency.csv");

            // Ask the user if they want to rerun the program
            System.out.println("Do you want to rerun the program? (yes/no):");
            String rerunChoice = scanner.next();

            // Check if the user wants to rerun or exit
            rerun = rerunChoice.equalsIgnoreCase("yes");
        }

        // Close the scanner at the end of the program
        scanner.close();
    }
    public static int nnStarter(int datasetChoice, double momentum) {

        // Define the neural network configuration
        int numInput = 2;
        int numHidden = 4;
        int numOutput = 1;
        double learningRate = 0.2;

        // Create a neural network instance
        NeuralNet neuralNet = new NeuralNet(numInput, numHidden, numOutput, learningRate, momentum, datasetChoice);

        double[][] inputPatterns;
        double[][] targetOutputs;

        if (datasetChoice == 1) {
            // Binary XOR training set
            inputPatterns = new double[][] {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
            targetOutputs = new double[][] {{0}, {1}, {1}, {0}};
        } else if (datasetChoice == 2) {
            // Bipolar XOR training set
            inputPatterns = new double[][] {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
            targetOutputs = new double[][] {{-1}, {1}, {1}, {-1}};
        } else {
            System.out.println("Invalid choice. Please enter '1' or '2' for the dataset.");
            return -1; // Exit the program
        }

        // Train the neural network on the XOR problem
        int maxEpochs = 10000;  // Adjust as needed
        double targetError = 0.05;  // Adjust as needed
        double totalError;
        int epoch = 0;

        // Create a PrintWriter to write to the CSV file
        try (PrintWriter writer = new PrintWriter(new FileWriter("src/Assignment1/script/graphing_data.csv"))) {
            writer.println("Epoch,totalError");

            for (epoch = 0; epoch < maxEpochs; epoch++) {
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

                // Printout for Epoch and averageError
//                System.out.println("Epoch:" + epoch + "  "+"totalError:" + totalError);
                writer.print(epoch);
                writer.print(",");
                writer.println(totalError);

                // Check if the training has reached the target error
                if (totalError < targetError) {
                    System.out.println("Training completed in " + (epoch + 1) + " epochs.");

                    break;
                }

            }

        // Test the trained neural network
//        System.out.println("Testing trained neural network:");
        for (int i = 0; i < inputPatterns.length; i++) {
            double[] input = inputPatterns[i];
            double output = neuralNet.outputFor(input);
//            System.out.println("Input: " + input[0] + ", " + input[1] + " => Output: " + output);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return epoch + 1;
    }

    public static void saveEpochsFrequencyToCSV(int[] epochsFrequency, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Trial,Epochs");

            for (int i = 0; i < epochsFrequency.length; i++) {
                writer.print((i + 1));
                writer.print(",");
                writer.println(epochsFrequency[i]);
            }

            System.out.println("Epochs frequency data saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

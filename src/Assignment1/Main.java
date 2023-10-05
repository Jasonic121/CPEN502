package Assignment1;

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

        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            totalError = 0.0;

            for (int i = 0; i < inputPatterns.length; i++) {
                double[] input = inputPatterns[i];
                double[] target = targetOutputs[i];

                // Forward pass
                double output = neuralNet.outputFor(input);

                // Backpropagation and training
                double error = neuralNet.train(input, target[0]);

                totalError += error;
            }

            // Calculate the average error for this epoch
            double averageError = totalError / inputPatterns.length;

            // Check if the training has reached the target error
            if (averageError < targetError) {
                System.out.println("Training completed in " + (epoch + 1) + " epochs.");
                break;
            }
            System.out.println("Epoch:" + epoch + "  "+"averageError:" + averageError);
        }

        // Test the trained neural network
        System.out.println("Testing trained neural network:");
        for (int i = 0; i < inputPatterns.length; i++) {
            double[] input = inputPatterns[i];
            double output = neuralNet.outputFor(input);
            System.out.println("Input: " + input[0] + ", " + input[1] + " => Output: " + output);
        }
    }

//    // Method to create and display the graph
//    private static void createEpochVsAverageErrorGraph(List<Double> epochList, List<Double> averageErrorList) {
//        DefaultXYDataset dataset = new DefaultXYDataset();
//        double[][] data = new double[2][epochList.size()];
//
//        for (int i = 0; i < epochList.size(); i++) {
//            data[0][i] = epochList.get(i);
//            data[1][i] = averageErrorList.get(i);
//        }
//
//        dataset.addSeries("Epoch vs. Average Error", data);
//
//        JFreeChart chart = ChartFactory.createXYLineChart(
//                "Epoch vs. Average Error",
//                "Epoch",
//                "Average Error",
//                dataset,
//                PlotOrientation.VERTICAL,
//                true,
//                true,
//                false
//        );
//
//        XYPlot plot = chart.getXYPlot();
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
//        plot.setRenderer(renderer);
//
//        JFrame frame = new JFrame("Epoch vs. Average Error");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(new ChartPanel(chart));
//        frame.pack();
//        frame.setVisible(true);
//    }
}

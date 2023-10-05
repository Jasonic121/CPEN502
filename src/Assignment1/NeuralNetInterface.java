package Assignment1;

/**
 * This interface represents a neural network.
 *
 * @date 20 June 2012
 * @author sarbjit
 */
public interface NeuralNetInterface extends CommonInterface {

    double bias = 1.0; // The input for each neuron's bias weight

    /**
     * Return a bipolar sigmoid of the input X.
     *
     * @param x The input
     * @return f(x) = 2 / (1 + e(-x)) - 1
     */
    public double sigmoid(double x);

    /**
     * This method implements a general sigmoid with asymptotes bounded by (a, b).
     *
     * @param x The input
     * @return f(x) = b_minus_a / (1 + e(-x)) - a
     */
    public double customSigmoid(double x);

    /**
     * Initialize the weights to random values.
     * For example, if there are 2 inputs, the input vector is [0] & [1]. We add [2] for the bias.
     * Similarly, for hidden units, if there are 2 hidden units stored in an array,
     * [0] & [1] are the hidden units & [2] is the bias.
     * We also initialize the last weight change arrays. This is to implement the alpha term.
     */
    public void initializeWeights();

    /**
     * Initialize the weights to 0.
     */
    public void zeroWeights();
}

package ece.CPEN502.Interface;

import java.io.File;
import java.io.IOException;

/**
 * This interface is common to both the Neural Net and LUT interfaces.
 * The idea is that you should be able to easily switch the LUT
 * for the Neural Net since the interfaces are identical.
 *
 * @date 20 June 2012
 * @author sarbjit
 */
public interface CommonInterface {

    /**
     * Calculate the output for the given input vector.
     *
     * @param X The input vector as an array of doubles.
     * @return The value returned by the LUT or NN for this input vector.
     */
    public double outputFor(double[] X);

    /**
     * Train the NN or the LUT with the desired correct output value for an input.
     *
     * @param X         The input vector.
     * @param argValue  The new value to learn.
     * @return The error in the output for that input.
     */
    public double train(double[] X, double argValue);

    /**
     * Write either a LUT or weights of a neural net to a file.
     *
     * @param argFile The file to save to, of type File.
     */
    public void save(File argFile);

    /**
     * Loads the LUT or neural net weights from a file.
     * The load must have knowledge of how the data was written out by the save method.
     * An error should be raised in the case that an attempt is being
     * made to load data into an LUT or neural net whose structure does not match
     * the data in the file (e.g., wrong number of hidden neurons).
     *
     * @param argFileName The name of the file to load from.
     * @throws IOException If there's an error reading the file.
     */
    public void load(String argFileName) throws IOException;
}

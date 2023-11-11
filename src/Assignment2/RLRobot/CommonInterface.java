package Assignment2.RLRobot;

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


    public double outputFor(double[] x);
    public void train(double[] x, double target) throws IOException;
    public void save(File fileName);
    public void load(String FileName) throws IOException;
    public void initialize();
}

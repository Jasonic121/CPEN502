package Assignment2.RLRobot;

import robocode.RobocodeFileOutputStream;

import java.io.*;

public class LUT implements CommonInterface {
    private double[][][][][] lut;
    private int[][][][][] visits;
    private int numDim1Levels;
    private int numDim2Levels;
    private int numDim3Levels;
    private int numDim4Levels;
    private int numDim5Levels;
    private int actionSize;



    public LUT(int numDim1Levels, int numDim2Levels, int numDim3Levels, int numDim4Levels, int numDim5Levels) {
        this.numDim1Levels = numDim1Levels;
        this.numDim2Levels = numDim2Levels;
        this.numDim3Levels = numDim3Levels;
        this.numDim4Levels = numDim4Levels;
        this.numDim5Levels = numDim5Levels;

        lut = new double[numDim1Levels][numDim2Levels][numDim3Levels][numDim4Levels][numDim5Levels];
        visits = new int[numDim1Levels][numDim2Levels][numDim3Levels][numDim4Levels][numDim5Levels];
        this.initialize();
    };

    public int getBestAction(int myHP, int enemyHP, int distance, int distanceWall) {
        double maxQ = -1;
        int actionIndex = -1;

        for(int i = 0; i < actionSize; i++) {
            if(lut[myHP][enemyHP][distance][distanceWall][i] > maxQ) {
                actionIndex = i;
                maxQ = lut[myHP][enemyHP][distance][distanceWall][i];
            }
        }
        return actionIndex;
    }

    @Override
    public void initialize() {
        for(int i = 0; i < numDim1Levels; i++) {
            for(int j = 0; j < numDim2Levels; j++) {
                for(int k = 0; k < numDim3Levels; k++) {
                    for(int m = 0; m < numDim4Levels; m++) {
                        for(int n = 0; n < numDim5Levels; n++) {
                            lut[i][j][k][m][n] = Math.random();
                            visits[i][j][k][m][n] = 0;
                        }
                    }
                }
            }
        }
    }


    public int visits(double[] x) throws ArrayIndexOutOfBoundsException {
        if(x.length !=5)
            throw new ArrayIndexOutOfBoundsException();
        else {
            int a = (int)x[0];
            int b = (int)x[1];
            int c = (int)x[2];
            int d = (int)x[3];
            int e = (int)x[4];
            return visits[a][b][c][d][e];
        }
    }

    @Override
    public void load(String argFileName) throws IOException {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(argFileName));
            lut = (double[][][][][]) ois.readObject();
            visits = (int[][][][][]) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void print () {
        for(int i = 0; i < numDim1Levels; i++) {
            for(int j = 0; j < numDim2Levels; j++) {
                for(int k = 0; k < numDim3Levels; k++) {
                    for(int m = 0; m < numDim4Levels; m++) {
                        for(int n = 0; n < numDim5Levels; n++) {
                            System.out.printf("+++ {%d, %d, %d, %d, %d} = %2.3f visits: %d\n", i, j, k, m, n,
                            lut[i][j][k][m][n] ,
                            visits[i][j][k][m][n]);
                        }
                    }
                }
            }
        }
//        return output;
    }


    @Override
    public double outputFor(double[] x) throws ArrayIndexOutOfBoundsException {
        if (x.length != 5)
            throw new ArrayIndexOutOfBoundsException();
        else {
            int a = (int)x[0];
            int b = (int)x[1];
            int c = (int)x[2];
            int d = (int)x[3];
            int e = (int)x[4];
            return lut[a][b][c][d][e];
        }
    }

    @Override
    public void train(double[] x, double target) throws ArrayIndexOutOfBoundsException {
        if (x.length !=5)
            throw new ArrayIndexOutOfBoundsException();
        else {
            int a = (int)x[0];
            int b = (int)x[1];
            int c = (int)x[2];
            int d = (int)x[3];
            int e = (int)x[4];
            lut[a][b][c][d][e] = target;
            visits[a][b][c][d][e]++;
        }
    }

    @Override
    public void save(File fileName) {
        PrintStream saveFile = null;

        try {
            saveFile = new PrintStream (new RobocodeFileOutputStream( fileName ) );
        }
        catch (IOException e) {
            System.out.println("*** Could not create output stream for NN save file:");
        }
    }
}

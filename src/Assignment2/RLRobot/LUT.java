package Assignment2.RLRobot;

import robocode.RobocodeFileOutputStream;
import robocode.RobocodeFileWriter;
import robocode.*;
import robocode.robotinterfaces.IAdvancedRobot;

import java.io.*;

import static Assignment2.RLRobot.JBot.log;

public class LUT implements LUTInterface{

    /* Log file preprocess */
    private double[][][][][] lut;
    private int[][][][][] visits;
    private int numDim1Levels;
    private int numDim2Levels;
    private int numDim3Levels;
    private int numDim4Levels;
    private int numDim5Levels;


    public LUT(int numDim1Levels, int numDim2Levels, int numDim3Levels, int numDim4Levels, int numDim5Levels) {
        this.numDim1Levels = numDim1Levels;
        this.numDim2Levels = numDim2Levels;
        this.numDim3Levels = numDim3Levels;
        this.numDim4Levels = numDim4Levels;
        this.numDim5Levels = numDim5Levels;

        lut = new double[numDim1Levels][numDim2Levels][numDim3Levels][numDim4Levels][numDim5Levels];
        visits = new int[numDim1Levels][numDim2Levels][numDim3Levels][numDim4Levels][numDim5Levels];
        initialiseLUT();
    }


    public int getBestAction(int myHP, int enemyHP, int distance, int distance2Center) {
        double maxQ = -1;
        int actionIndex = -1;

        for(int i = 0; i < numDim5Levels; i++) {
            if(lut[myHP][enemyHP][distance][distance2Center][i] > maxQ) {
                actionIndex = i;
                maxQ = lut[myHP][enemyHP][distance][distance2Center][i];
            }
        }
        return actionIndex;
    }
    public double getQValue(int myHP, int enemyHP, int distance, int distanceWall, int action) {
        return lut[myHP][enemyHP][distance][distanceWall][action];
    }
    public void setQValue(int[] x, double argValue) {

        lut[x[0]][x[1]][x[2]][x[3]][x[4]] = argValue;
        visits[x[0]][x[1]][x[2]][x[3]][x[4]]++;
    }


    public int visits(int[] x) throws ArrayIndexOutOfBoundsException {
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
//        System.out.println("outputFor x.length: " + x.length);  // Debug statement

        if (x.length != 5)
            throw new ArrayIndexOutOfBoundsException();
        else {
            int a = (int)x[0];
            int b = (int)x[1];
            int c = (int)x[2];
            int d = (int)x[3];
            int e = (int)x[4];
//            System.out.println("outputFor a: " + a);  // Debug statement
//            System.out.println("outputFor b: " + b);  // Debug statement
//            System.out.println("outputFor c: " + c);  // Debug statement
//            System.out.println("outputFor d: " + d);  // Debug statement
//            System.out.println("outputFor e: " + e);  // Debug statement

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
    public void initialiseLUT() {
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

//    @Override
//    public void load(String argFileName) throws IOException {
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(argFileName));
//            lut = (double[][][][][]) ois.readObject();
//            visits = (int[][][][][]) ois.readObject();
//            ois.close();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
    public void load(String argFileName) throws IOException {
        try {
            BufferedReader in = new BufferedReader(new FileReader(argFileName));
            for(int i = 0; i < numDim1Levels; i++) {
                for(int j = 0; j < numDim2Levels; j++) {
                    for(int k = 0; k < numDim3Levels; k++) {
                        for(int m = 0; m < numDim4Levels; m++) {
                            for(int n = 0; n < numDim5Levels; n++) {
                                String line = in.readLine();
                                String [] args = line.split(",");
                                System.out.println(line);
                                double q = Double.parseDouble(args[5]);
                                lut[i][j][k][m][n] = q;
                            }
                        }
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    @Override
//    public void save(File fileName) {
//        PrintStream saveFile = null;
//
//        try {
//            saveFile = new PrintStream (new RobocodeFileOutputStream( fileName ) );
//        }
//        catch (IOException e) {
//            System.out.println("*** Could not create output stream for LUT save file:");
//        }
//    }

//    @Override
//    public void save(File fileName) {
//        try (PrintStream saveFile = new PrintStream(new RobocodeFileOutputStream(fileName))) {
//            for (int i = 0; i < lut.length; i++) {
//                for (int j = 0; j < lut[i].length; j++) {
//                    for (int k = 0; k < lut[i][j].length; k++) {
//                        for (int l = 0; l < lut[i][j][k].length; l++) {
//                            for (int m = 0; m < lut[i][j][k][l].length; m++) {
//                                saveFile.println(lut[i][j][k][l][m]);
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            System.out.println("*** Could not create output stream for LUT save file:");
//            e.printStackTrace();
//        }
//    }


    public void save(File file) throws IOException {
        for(int i = 0; i < numDim1Levels; i++) {
            for(int j = 0; j < numDim2Levels; j++) {
                for(int k = 0; k < numDim3Levels; k++) {
                    for(int m = 0; m < numDim4Levels; m++) {
                        for(int n = 0; n < numDim5Levels; n++) {
                            String s = String.format("%d,%d,%d,%d,%d,%3f,%d", i,j, k, m, n, lut[i][j][k][m][n],visits[i][j][k][m][n]);
                            RobocodeFileWriter fileWriter = new RobocodeFileWriter(file.getAbsolutePath(), true);
                            fileWriter.write(s + "\r\n");
                            fileWriter.close();
                        }
                    }
                }
            }
        }
    }
//    public void print() {
//        for (int a = 0; a < numDim1Levels; a++) {
//            for (int b = 0; b < numDim2Levels; b++) {
//                for (int c = 0; c < numDim3Levels; c++) {
//                    for (int d = 0; d < numDim4Levels; d++) {
//                        for (int e = 0; e < numDim5Levels; e++) {
//                            System.out.printf("+++ {%d, %d, %d, %d, %d} = %2.3f visits: %d\n",
//                                    a, b, c, d, e,
//                                    lut[a][b][c][d][e],
//                                    visits[a][b][c][d][e]
//                            );
//                        }
//                    }
//                }
//            }
//        }
//    }
}

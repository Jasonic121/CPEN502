package Assignment2.RLRobot;

import robocode.RobocodeFileWriter;
import java.io.*;


public class LogFile {
//    public void writeToFile(File fileToWrite, double winRate, int roundCount) {
//        try{
//            RobocodeFileWriter fileWriter = new RobocodeFileWriter(fileToWrite.getAbsolutePath(), true);
//            fileWriter.write("RoundCount: " + roundCount + " " + "WinRate: " + Double.toString(winRate * 100) + " %\r\n");
//            fileWriter.close();
//        }
//        catch(Exception e){
//            System.out.println(e);
//        }
//    }
    public void writeToFile(File fileToWrite, double winRate, int roundCount) {
        try{
            RobocodeFileWriter fileWriter = new RobocodeFileWriter(fileToWrite.getAbsolutePath(), true);
            fileWriter.write("RoundCount: " + roundCount + " " + "WinRate: " + Double.toString(winRate * 100) + " %\r\n");
            fileWriter.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public void writelutToFile(File fileToWrite, int dim1, int dim2, int dim3, int dim4, int dim5, int freq) {
        try{
            RobocodeFileWriter fileWriter = new RobocodeFileWriter(fileToWrite.getAbsolutePath(), true);
            fileWriter.write(dim1 + " " + dim2 + " " + dim3 + " " + dim4 + " " + dim5 + " " + freq + "\r\n");
            fileWriter.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
}

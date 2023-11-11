package Assignment2.RLRobot;

import robocode.*;
import java.util.logging.Logger;
import sun.rmi.log.ReliableLog;
import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import sun.rmi.log.ReliableLog;

// Test robot for Jason
public class JasonBot extends AdvancedRobot {

    private String lutFilename = getClass().getSimpleName() + "-lut.txt";

    // Enum Type
    public enum enumEnergy {zero, low, medium, high};
    public enum enumDistance {veryClose, near, far};
    public enum enumActions {circle, retreat, advance, head2Centre, fire};
    public enum enumOperationalMode {onScan, onAction};
    public static LUT q = new LUT (
            enumEnergy.values().length,
            enumDistance.values().length,
            enumEnergy.values().length,
            enumDistance.values().length,
            enumActions.values().length);

    static int totalNumRounds = 0;
    static int numRoundsTo100 = 0;
    static int numWins = 0;
    // Initialization: operationMode
    public enumOperationalMode operaMode = enumOperationalMode.onScan;

    // Initialization: Battle Variables
    public double myX = 0.0;
    public double myY = 0.0;
    public double myEnergy = 100;
    public double enemyEnergy = 100;
    public double enemyDistance = 0.0;
    public static double enemyBearing;

    // Initialization: Reward
    private double currentReward = 0.0;

    private final double badTerminalReward = -0.2;



    // Initialization: Hyperparameters
    private double gamma = 0.9;
    private double alpha = 0.1;
    private double epsilon = 0;


    private enumEnergy currentMyEnergyState = enumEnergy.high;
    private enumDistance currentDistance2EnemyState = enumDistance.near;
    private enumEnergy currentEnemyEnergyState = enumEnergy.high;
    private enumDistance currentDistance2CenterState = enumDistance.near;
    private enumActions currentAction = enumActions.circle;

    private enumEnergy previousMyEnergyState = enumEnergy.high;
    private enumDistance previousDistance2EnemyState = enumDistance.near;
    private enumEnergy previousEnemyEnergyState = enumEnergy.high;
    private enumDistance previousDistance2CenterState = enumDistance.near;
    private enumActions previousAction = enumActions.circle;


    public void run() {
        /* Customize the tank */
        setBulletColor(Color.red);
        setBodyColor(Color.black);
        while (true) {
//            ahead(100);
            turnGunRight(360);
//            back(100);
            turnGunRight(360);
        }


    }

    // Get the level of HP
    public enumEnergy getEnergyLevel(double hp) {
        enumEnergy energyLevel = null;
        if(hp < 0) {
            energyLevel = enumEnergy.zero;

        } else if(hp <= 33) {
            energyLevel = enumEnergy.low;
        } else if(hp <= 67) {
            energyLevel = enumEnergy.medium;
        } else {
            energyLevel = enumEnergy.high;
        }
        return energyLevel;
    }


    // Get the distance of my robot to the center of battlefield
    public enumDistance getDistanceFromCenter(double x1, double y1) {
        enumDistance disWCenter = null;
        double width = getBattleFieldWidth();
        double height = getBattleFieldHeight();
        double dis1 = y1;
        double dis2 = height / 2;
        double dis3 = x1;
        double dis4 = width / 2;
        double distance = Math.sqrt((dis1 - dis2) * (dis1 - dis2)) + ((dis3 - dis4) * (dis3 - dis4));
        double metric = Math.sqrt((width * width) + (height * height));

        if(distance <= (metric / 3)) {
            disWCenter = enumDistance.veryClose;
        } else if(distance <= (metric * (2 / 3))) {
            disWCenter = enumDistance.near;
        } else {
            disWCenter = enumDistance.far;
        }
        return disWCenter;
    }

    // Get the distance of enemy robot to my robot
    public enumDistance getDistanceLevel(double x) {
        enumDistance dis = null;

        if(x <= 0) {
            return dis;
        } else if(x <= 300) {
            dis = enumDistance.veryClose;
        } else if(x <= 600) {
            dis = enumDistance.near;
        } else {
            dis = enumDistance.far;
        }
        return dis;
    }

    public void onHitByBullet(HitByBulletEvent event) {
        ahead(100);
    }


    @Override
    public void onScannedRobot (ScannedRobotEvent e) {
        myX = getX();
        myY = getY();
        enemyBearing = e.getBearing();
        enemyDistance = e.getDistance();
        enemyEnergy = e.getEnergy();
        myEnergy = getEnergy();

        // Capture previous current before updating current
        previousMyEnergyState = currentMyEnergyState;
        previousDistance2EnemyState = currentDistance2EnemyState;
        previousEnemyEnergyState = currentEnemyEnergyState;
        previousDistance2CenterState = currentDistance2CenterState;
        previousAction = currentAction;

        // Now update current
        currentMyEnergyState = getEnergyLevel(myEnergy);
        currentDistance2EnemyState = getDistanceLevel(enemyDistance);
        currentEnemyEnergyState = getEnergyLevel(enemyEnergy);
        currentDistance2CenterState = getDistanceFromCenter(myX, myY);
        operaMode = enumOperationalMode.onAction;
    }

    public double computeQ (double r){

        enumActions maxA = bestAction(
                currentMyEnergyState.ordinal(),
                currentDistance2EnemyState.ordinal(),
                currentEnemyEnergyState.ordinal(),
                currentDistance2CenterState.ordinal());

        double[] previousStateAction = new double []{
                previousMyEnergyState.ordinal(),
                previousDistance2EnemyState.ordinal(),
                previousEnemyEnergyState.ordinal(),
                previousDistance2CenterState.ordinal(),
                previousAction.ordinal()
        };
        double[] currenMaxStateAction = new double [] {
                currentMyEnergyState.ordinal(),
                currentDistance2EnemyState.ordinal(),
                currentEnemyEnergyState.ordinal(),
                currentDistance2CenterState.ordinal(),
                maxA.ordinal()
        };
        double priorQ = q.outputFor((previousStateAction));
        double currentQmax = q.outputFor(currenMaxStateAction);

        double updatedQ = priorQ + alpha * (r + gamma * currentQmax - priorQ);
        return updatedQ;
    }

    public enumActions selectRandomAction () {
        Random rand = new Random();
        int r = rand.nextInt(enumActions.values().length);
        return enumActions.values()[r];
    }

    public enumActions bestAction(double e, double d, double e2, double d2) {
        double bestQ = -Double.MAX_VALUE;
        enumActions bestAction = null;

        for (int a= 0; a < enumActions.values().length; a++) {
            double[] x = new double []{e,d,e2,d2,a};
            double predictedQ = outputForWrapper(x);

            if (predictedQ > bestQ) {
                bestQ = predictedQ;
                bestAction = enumActions.values()[a];
            }
        }
        return bestAction;
    }

    public double outputForWrapper(double[] x) {
        return q.outputFor(x);
    }

    public void onDeath(DeathEvent e) {
        currentReward = badTerminalReward;
        PrintStream stream = System.out;
        double[] x = new double []{
                previousMyEnergyState.ordinal(),
                previousDistance2EnemyState.ordinal(),
                previousEnemyEnergyState.ordinal(),
                previousDistance2CenterState.ordinal(),
                previousAction.ordinal()
        };

        q.train(x, computeQ(currentReward));

        if (numRoundsTo100 < 100) {
            numRoundsTo100++;
            totalNumRounds++;
        }
        else{
            System.out.printf("win percentage, %.1f\n", 100.0 + numWins / numRoundsTo100);
            stream.printf("num rounds: %d, win percentage: %.1f\n", totalNumRounds, 100.0 + numWins/ numRoundsTo100);
            stream.flush();
            numRoundsTo100 = 0;
            numWins = 0;
        }

        q.save(getDataFile(lutFilename));

    }

}

package Assignment2.RLRobot;

import robocode.*;
import robocode.Event;
import robocode.util.Utils;
import java.awt.*;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.*;


// Test robot for Jason
public class JBot extends AdvancedRobot {

    /* Helper method */
//    private boolean spaceKeyPressed = false;
//    // Override the onKeyPressed event
//    @Override
//    public void onKeyPressed(KeyEvent e) {
//        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
//            spaceKeyPressed = (!spaceKeyPressed) ;
//        }
//    }


    /* Enum Declare */
    public enum enumEnergy {zero, low, medium, high}
    public enum enumDistance {veryClose, near, far}
    public enum enumActions {circle, retreat, advance, head2Center, fire}
    public enum enumOperationalMode {onScan, onAction}

    /* Initialization: Stats */
    static int totalNumRounds = 0;
    static int numRoundsTo100 = 0;
    static int numWins = 0;
    public static double winPercentage = 0.0;

    static LogFile log = new LogFile();

    /* Initialization: operationMode */
    public enumOperationalMode operaMode = enumOperationalMode.onScan;

     /* Initialization: State Variables */
    public double myX = 0.0;
    public double myY = 0.0;
    public double myEnergy = 100;
    public double enemyEnergy = 100;
    public double enemyDistance = 0.0;
    public static double enemyBearing;
    public int actionSize;
    int direction = 1;

    /* Initialization: Reward */


    private double currentReward = 0.0;
    final double terminalBonus = 1.0;
    private final double badTerminalReward = -0.2;
    private final double immediatePenalty = -0.1;
    private final double immediateBonus = 0.5;
    private double Q;




    /* Initialization: Hyperparameters */
    private static double gamma = 0.9; // Discount factor
    private static double alpha = 0.1; // Learning rate
    public static boolean onPolicy = false;
    private static double epsilon = 0.45;
    public static boolean takeImmediate = true;     // Whether take immediate rewards

    private enumEnergy currentMyEnergyState = enumEnergy.high;
    private enumDistance currentDistance2EnemyState = enumDistance.near;
    private enumEnergy currentEnemyEnergyState = enumEnergy.high;
    private enumDistance currentDistance2CenterState = enumDistance.near;
    private enumActions currentAction = enumActions.fire;

    private enumEnergy previousMyEnergyState = enumEnergy.high;
    private enumDistance previousDistance2EnemyState = enumDistance.near;
    private enumEnergy previousEnemyEnergyState = enumEnergy.high;
    private enumDistance previousDistance2CenterState = enumDistance.near;
    private enumActions previousAction = enumActions.circle;

    /* Log file preprocess */
    public static String fileToSaveName = JBot.class.getSimpleName() + "_" + "a=" + Double.toString(alpha) + "_policy=" + onPolicy +  "_e=" + Double.toString(epsilon) + "_intermediate=" + takeImmediate  + ".log";
    public static String fileToSaveLUT = JBot.class.getSimpleName() + "-"  + "LUT";
    File folderDst1 = new File("/Users/jason/Courses/UBC/CPEN502/CPEN502/out/production/CPEN502/Assignment2/RLRobot", fileToSaveName);
    File folderDst2 = new File("/Users/jason/Courses/UBC/CPEN502/CPEN502/out/production/CPEN502/Assignment2/RLRobot", fileToSaveLUT);

    /* Constructor */
    public static LUT lut = new LUT (
            enumEnergy.values().length,
            enumEnergy.values().length,
            enumDistance.values().length,
            enumDistance.values().length,
            enumActions.values().length);

    /* Run method */
    public void run() {

        /* Customize the tank */
        setBulletColor(Color.red);
        setGunColor(Color.darkGray);
        setBodyColor(Color.blue);
        setRadarColor(Color.white);
        currentMyEnergyState = enumEnergy.high;

        while (true) {

            /* Epsilon Toggle */
//            // Check if the space key is pressed
//            if (spaceKeyPressed) {
//                epsilon = 0.0;
//            } else {
//                // You may want to set epsilon to its original value when the space key is released
//                epsilon = 0.5; // Default value, adjust as needed
//            }
            // set epsilon to 0 after 8000 round
            if (totalNumRounds > 5000) epsilon = 0;


            switch (operaMode) {
                case onScan: {
                    currentReward = 0.0;
                    turnRadarLeft(90);
                    break;
                }
                case onAction: {
                    currentDistance2CenterState = getDistanceFromCenter(myX, myY);

                    currentAction = (Math.random() <= epsilon)
                            ? selectRandomAction() // explore a random action
                            : bestAction(
                            getEnergyLevel(myEnergy).ordinal(),
                            getEnergyLevel(enemyEnergy).ordinal(),
                            getDistanceLevel(enemyDistance).ordinal(),
                            currentDistance2CenterState.ordinal()); // select greedy action
                    System.out.println("Current Action: " + currentAction);  // Debug statement

                    switch (currentAction) {
                        case circle: {
                            setTurnLeft(enemyBearing + 90);
                            setAhead(50 * direction);
                            execute();
                            break;
                        }

                        case advance: {
                            setTurnRight(enemyBearing);
                            setAhead(100);
                            execute();
                            break;
                        }

                        case retreat: {
                            setTurnRight(enemyBearing + 180);
                            setAhead(100);
                            execute();
                            break;
                        }
                        case head2Center: {
                            double bearing = getBearingToCenter(getX(), getY(), getHeadingRadians());
                            setTurnRight(bearing);
                            setAhead(100);
                            execute();
                            break;
                        }
                        case fire: {
                            turnGunRight(getHeading() - getGunHeading() + enemyBearing);
                            fire(3);
                            break;
                        }
                    }
                    int[] indexes = new int[]{
                            previousMyEnergyState.ordinal(),
                            previousEnemyEnergyState.ordinal(),
                            previousDistance2EnemyState.ordinal(),
                            previousDistance2CenterState.ordinal(),
                            previousAction.ordinal()
                    };
                    Q = computeQ(currentReward, onPolicy);
                    lut.setQValue(indexes, Q);
                    operaMode = operaMode.onScan;

//                    /*save lut table*/
//                    folderDst2 = getDataFile(fileToSaveLUT);
//                    System.out.println("lut file location: " + folderDst2.getAbsolutePath());
//                    log.writelutToFile(folderDst2, indexes[0], indexes[1], indexes[2], indexes[3], indexes[4], lut.visits(indexes));
//
                }

            }

        }
    }

    /* Get the level of HP */
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

    /* Get the bearing of my robot to the center of battlefield */
    public double getBearingToCenter(double x, double y, double heading) {
        double width = getBattleFieldWidth();
        double height = getBattleFieldHeight();
        double absoluteBearing = Math.atan2(width - x, height - y);
        double bearing = Utils.normalRelativeAngle(absoluteBearing - heading);
        return bearing;
    }

    /* Get the distance of my robot to the center of battlefield */
    public enumDistance getDistanceFromCenter (double x1, double y1) {
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

    /* Get the distance of enemy robot to my robot */
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
        if(takeImmediate) {
            currentReward += immediatePenalty;
        }
    }
    @Override
    public void onBulletHit(BulletHitEvent e){
        if(takeImmediate) {
            currentReward += immediateBonus;
        }
    }
    @Override
    public void onBulletMissed(BulletMissedEvent e){
        if(takeImmediate) {
            currentReward += immediatePenalty;
        }
    }

    @Override
    public void onHitWall(HitWallEvent e){
        if(takeImmediate) {
            currentReward += immediatePenalty;
        }
        evadeObstacle();
    }
    public void evadeObstacle() {
        setBack(200);
        setTurnRight(60);
        execute();
    }
    @Override
    public void onHitRobot(HitRobotEvent e) {
        if(takeImmediate) {
            currentReward += immediatePenalty;
        }
        evadeObstacle();
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

    public double computeQ (double r, boolean onPolicy){

//        enumActions maxA = bestAction(
//                currentMyEnergyState.ordinal(),
//                currentEnemyEnergyState.ordinal(),
//                currentDistance2EnemyState.ordinal(),
//                currentDistance2CenterState.ordinal());
//
//        double[] previousStateAction = new double []{
//                previousMyEnergyState.ordinal(),
//                previousEnemyEnergyState.ordinal(),
//                previousDistance2EnemyState.ordinal(),
//                previousDistance2CenterState.ordinal(),
//                previousAction.ordinal()
//        };
//        double[] currentMaxStateAction = new double [] {
//                currentMyEnergyState.ordinal(),
//                currentEnemyEnergyState.ordinal(),
//                currentDistance2EnemyState.ordinal(),
//                currentDistance2CenterState.ordinal(),
//                maxA.ordinal()
//        };
        double previousQ = lut.getQValue(
                previousMyEnergyState.ordinal(),
                previousEnemyEnergyState.ordinal(),
                previousDistance2EnemyState.ordinal(),
                previousDistance2CenterState.ordinal(),
                previousAction.ordinal()
        );

        double curQ = lut.getQValue(
                currentMyEnergyState.ordinal(),
                currentEnemyEnergyState.ordinal(),
                currentDistance2EnemyState.ordinal(),
                currentDistance2CenterState.ordinal(),
                currentAction.ordinal()
        );

        int bestActionIndex = lut.getBestAction(
                currentMyEnergyState.ordinal(),
                currentEnemyEnergyState.ordinal(),
                currentDistance2EnemyState.ordinal(),
                currentDistance2CenterState.ordinal()
        );

        // Get the maximum Q ( Off-policy )
        double maxQ = lut.getQValue(
                currentMyEnergyState.ordinal(),
                currentEnemyEnergyState.ordinal(),
                currentDistance2EnemyState.ordinal(),
                currentDistance2CenterState.ordinal(),
                bestActionIndex
        );

        // ON-POLICY: SARSA , OFF-POLICY: Q-LEARNING
//        double priorQ = lut.outputFor((previousStateAction));
//        double currentQ = lut.outputFor(currentMaxStateAction);
//
//        double updatedQ = priorQ + alpha * (r + gamma * currentQ - priorQ);
//        return updatedQ;
        double res = onPolicy ?
                previousQ + alpha * (currentReward + gamma * curQ - previousQ) :
                previousQ + alpha * (currentReward + gamma * maxQ - previousQ);

        return res;
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
        return lut.outputFor(x);
    }

    @Override
    public void onDeath(DeathEvent e){
        PrintStream stream = System.out;
        currentReward = badTerminalReward;
        int[] indexes = new int []{
                currentMyEnergyState.ordinal(),
                currentEnemyEnergyState.ordinal(),
                currentDistance2EnemyState.ordinal(),
                currentDistance2CenterState.ordinal(),
                currentAction.ordinal()};
        Q = computeQ(currentReward, onPolicy);
        lut.setQValue(indexes, Q);

        /*save winrate table;*/
        totalNumRounds++;
        if((totalNumRounds % 100 == 0) && (totalNumRounds != 0)){
            winPercentage = (double) numWins / 100;
            folderDst1 = getDataFile(fileToSaveName);
            System.out.println("winrate file location: " + folderDst1.getAbsolutePath());
            log.writeToFile(folderDst1, winPercentage, totalNumRounds);
            numWins = 0;
            //saveTable();
        }
    }
    @Override
    public void onWin(WinEvent e) {
        PrintStream stream = System.out;
        currentReward = terminalBonus;
        int[] indexes = new int[]{
                currentMyEnergyState.ordinal(),
                currentEnemyEnergyState.ordinal(),
                currentDistance2EnemyState.ordinal(),
                currentDistance2CenterState.ordinal(),
                currentAction.ordinal()};
        Q = computeQ(currentReward, onPolicy);
        lut.setQValue(indexes, Q);

        /*save winrate table*/
        totalNumRounds++;
        numWins++;
        if ((totalNumRounds % 100 == 0) && (totalNumRounds != 0)) {
            winPercentage = (double) numWins / 100;
            folderDst1 = getDataFile(fileToSaveName);
            System.out.println("winrate file location: " + folderDst1.getAbsolutePath());
            log.writeToFile(folderDst1, winPercentage, totalNumRounds);
            numWins = 0;
            //saveTable();
        }

//        public void saveTable() {
//            try {
//                String file = fileToSaveLUT + "-" + round + ".log";
//                lut.save(getDataFile(file));
//            } catch (Exception e) {
//                System.out.println("Save Error!" + e);
//            }
//        }
//
//        public void loadTable() {
//            try {
//                lut.load(fileToSaveLUT);
//            } catch (Exception e) {
//                System.out.println("Save Error!" + e);
//            }
//        }
    }

}

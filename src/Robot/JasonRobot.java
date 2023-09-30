package Robot;

import robocode.*;


// Test robot for Jason
public class JasonRobot extends Robot{

    public void run(){

        while (true) {
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
    }

}

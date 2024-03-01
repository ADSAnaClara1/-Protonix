package Teste2;
import robocode.*;
/**
 * TESTE2 - a robot by (protonix)
 */
public class TESTE2 extends AdvancedRobot {
    boolean movingForward = true;

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            if (movingForward) {
                setAhead(200);
                setTurnRight(90);
            } else {
                setBack(200);
                setTurnLeft(90);
            }
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double enemyBearing = getHeadingRadians() + e.getBearingRadians();
        double enemyDistance = e.getDistance();
        
        // Radar Lock
        double radarTurn = enemyBearing - getRadarHeadingRadians();
        setTurnRadarRightRadians(2.0 * Utils.normalRelativeAngle(radarTurn));
        
        // Gun Rotation
        double gunTurn = enemyBearing - getGunHeadingRadians();
        setTurnGunRightRadians(Utils.normalRelativeAngle(gunTurn));
        
        // Precise Fire
        if (enemyDistance < 200) {
            setFire(3);
        } else if (enemyDistance < 600) {
            setFire(2);
        } else {
            setFire(1);
        }
        
        // Direction change
        if (e.getDistance() < 100 && !movingForward) {
            movingForward = true;
        } else if (e.getDistance() > 400 && movingForward) {
            movingForward = false;
        }
    }
}
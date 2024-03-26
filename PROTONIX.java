package PROTONIX;

import robocode.*;
import java.awt.Color;
import robocode.util.Utils;

public class PROTONIX extends AdvancedRobot {

    boolean movingForward = true;
    EnemyInfo target = null;
    boolean aggressivePhase = false;
    int numOpponents;

    class EnemyInfo {
        String name;
        double distance;
        double bearing;
        double energy;

        EnemyInfo(String name, double distance, double bearing, double energy) {
            this.name = name;
            this.distance = distance;
            this.bearing = bearing;
            this.energy = energy;
        }
    }

    public void run() {
         setColors(Color.PINK, Color.PINK, Color.PINK); // Definindo as cores do robô
        setAdjustRadarForGunTurn(true); // Movimenta o radar independentemente do canhão
        setAdjustGunForRobotTurn(true); // Movimenta o canhão independentemente do robô
        
        // Obtém o número de oponentes na batalha
        numOpponents = getOthers();

        while (true) {
            if (!aggressivePhase && getOthers() <= numOpponents / 2) {
                aggressivePhase = true;
                out.println("Aggressive Phase activated!");
            }
            
            if (!aggressivePhase) {
                defensivePhase();
            } else {
                aggressivePhase();
            }
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double absoluteBearing = getHeadingRadians() + e.getBearingRadians();

        // Atualiza as informações sobre o alvo
        if (target == null || e.getDistance() < target.distance || e.getEnergy() > target.energy) {
            target = new EnemyInfo(e.getName(), e.getDistance(), absoluteBearing, e.getEnergy());
        }

        // Mira no alvo
        setTurnGunRightRadians(Utils.normalRelativeAngle(target.bearing - getGunHeadingRadians()));

        // Calcula a potência do tiro baseado na energia do alvo e na distância
        double bulletPower = Math.min(3.0, Math.max(0.1, Math.min(getEnergy() / 10, 400 / e.getDistance())));
        
        // Na fase agressiva, aumenta a potência dos tiros
        if (aggressivePhase) {
            bulletPower = Math.min(3.0, Math.max(0.1, Math.min(getEnergy() / 5, 600 / e.getDistance())));
        }

        setFire(bulletPower);
    }

    public void onHitWall(HitWallEvent e) {
        // Se bateu na parede, muda a direção
        setBack(100);
        setTurnRight(90);
        execute();
    }

    public void onHitRobot(HitRobotEvent e) {
        // Se colidiu com outro robô, retrocede e gira
        setBack(100);
        setTurnRight(90);
        execute();
    }

    private void defensivePhase() {
        scan(); // Escaneia o ambiente
        moveAround(); // Movimentação inteligente para esquivar-se de tiros
    }

    private void aggressivePhase() {
        if (target != null) {
            // Se tem um alvo, move-se em direção a ele
            double angleToTarget = getHeadingRadians() + target.bearing;
            setTurnRightRadians(Utils.normalRelativeAngle(angleToTarget - getHeadingRadians()));

            // Movimenta-se considerando a distância do alvo e as paredes
            if (target.distance > 200) {
                setAhead(150);
            } else {
                setBack(100);
            }
        } else {
            // Se não há alvo, move-se aleatoriamente
            setTurnRight(90);
            setAhead(100);
        }
    }

    private void moveAround() {
        // Movimentação inteligente para esquivar-se de tiros
        setTurnRight(90);
        setAhead(100);
    }
}

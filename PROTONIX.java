package PROTONIX;

import robocode.*;
import java.awt.Color;
import robocode.util.Utils;
import java.util.HashMap;

public class PROTONIX extends AdvancedRobot {

    boolean movingForward = true; // Variável booleana para controlar o movimento do robô
    double energyThreshold = 50; // Limite de energia para ativar o "escudo"
    HashMap<String, EnemyInfo> enemies = new HashMap<>(); // Armazena informações sobre os inimigos detectados

    class EnemyInfo {
        double distance;
        double energy;

        EnemyInfo(double distance, double energy) {
            this.distance = distance;
            this.energy = energy;
        }
    }

    public void run() {
        setAdjustGunForRobotTurn(true); // Configuração para ajustar a arma quando o robô girar
        setAdjustRadarForGunTurn(true); // Configuração para ajustar o radar quando a arma girar

        setColors(Color.PINK, Color.PINK, Color.PINK); // Define as cores do corpo, arma e radar como rosa

        while (true) { // Loop principal do robô
            setTurnRadarRight(Double.POSITIVE_INFINITY); // Gira o radar continuamente
            moveAround(); // Movimenta-se pelo campo de batalha
            execute(); // Executa os comandos
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        String enemyName = e.getName();
        double enemyDistance = e.getDistance();
        double enemyEnergy = e.getEnergy();

        // Armazena informações sobre o inimigo
        enemies.put(enemyName, new EnemyInfo(enemyDistance, enemyEnergy));

        // Atira em todos os inimigos detectados
        for (EnemyInfo enemy : enemies.values()) {
            // Verifica se o inimigo está em alcance de tiro
            if (enemy.distance < 600) {
                // Calcula o ângulo do inimigo em relação ao robô
                double enemyBearing = getHeadingRadians() + e.getBearingRadians();
                // Gira a arma para mirar no inimigo
                setTurnGunRightRadians(Utils.normalRelativeAngle(enemyBearing - getGunHeadingRadians()));
                // Dispara com potência proporcional à distância do inimigo
                if (enemy.distance < 200) {
                    setFire(3);
                } else if (enemy.distance < 600) {
                    setFire(2);
                }
            }
        }

        // Esquiva-se do inimigo se estiver muito próximo
        if (e.getDistance() < 100) {
            evadeEnemy();
        }
    }

    public void onHitRobot(HitRobotEvent event) {
        // Recua se colidir com um inimigo
        if (event.getBearing() > -90 && event.getBearing() <= 90) {
            setBack(100);
        } else {
            setAhead(100);
        }
    }

    // Método para movimentar-se pelo campo de batalha ao longo das bordas
private void moveAround() {
    double fieldWidth = getBattleFieldWidth();
    double fieldHeight = getBattleFieldHeight();

    double x = getX();
    double y = getY();

    // Define a distância mínima das bordas para manter o robô
    double margin = 50;

    // Calcula a direção para se mover ao longo das bordas
    double moveAngle = 0;

    if (x < margin) {
        // Perto da borda esquerda
        moveAngle = 0; // Mova para a direita
    } else if (y < margin) {
        // Perto da borda superior
        moveAngle = Math.PI / 2; // Mova para baixo
    } else if (x > fieldWidth - margin) {
        // Perto da borda direita
        moveAngle = Math.PI; // Mova para a esquerda
    } else if (y > fieldHeight - margin) {
        // Perto da borda inferior
        moveAngle = -Math.PI / 2; // Mova para cima
    }

    // Ajusta a direção do robô
    setTurnRightRadians(Utils.normalRelativeAngle(moveAngle - getHeadingRadians()));

    // Move para frente
    setAhead(100);
}

    // Método para esquivar-se de um inimigo
    private void evadeEnemy() {
        // Obtém o ângulo do inimigo em relação ao robô
        double bearingToEnemy = getHeadingRadians() + getRadarHeadingRadians();
        setBack(100 * Math.sin(bearingToEnemy));
        setTurnRightRadians(Utils.normalRelativeAngle(bearingToEnemy - getHeadingRadians() + Math.PI / 2));
    }
}

package PROTONIX;

import robocode.*;
import java.awt.Color;
import robocode.util.Utils;

public class PROTONIX extends AdvancedRobot {
    
    boolean movingForward = true; // Variável booleana para controlar o movimento do robô
    double energyThreshold = 50; // Limite de energia para ativar o "escudo"

    public void run() {
        setAdjustGunForRobotTurn(true); // Configuração para ajustar a arma quando o robô girar
        setAdjustRadarForGunTurn(true); // Configuração para ajustar o radar quando a arma girar

        setColors(Color.PINK, Color.PINK, Color.PINK); // Define as cores do corpo, arma e radar como rosa

        while (true) { // Loop principal do robô
            if (movingForward) { // Se estiver movendo para frente
                setAhead(200); // Move para frente 200 pixels
                setTurnRight(90); // Vira 90 graus para a direita
            } else { // Caso contrário
                setBack(200); // Move para trás 200 pixels
                setTurnLeft(90); // Vira 90 graus para a esquerda
            }
            
            // Verifica se a energia está abaixo do limite para ativar o "escudo"
            if (getEnergy() < energyThreshold) {
                setMaxVelocity(8); // Diminui a velocidade para evitar danos
            } else {
                setMaxVelocity(12); // Restaura a velocidade normal
            }
            
            // Gira o radar para escanear o ambiente
            setTurnRadarRight(Double.POSITIVE_INFINITY);
            
            execute(); // Executa os comandos definidos
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // Obtém o ângulo do inimigo em relação ao robô
        double enemyBearing = getHeadingRadians() + e.getBearingRadians();
        
        // Gira o radar para escanear o inimigo
        setTurnRadarRightRadians(Utils.normalRelativeAngle(enemyBearing - getRadarHeadingRadians()) * 2);
        
        // Gira a arma para mirar no inimigo
        setTurnGunRightRadians(Utils.normalRelativeAngle(enemyBearing - getGunHeadingRadians()));

        // Dispara com potência proporcional à distância do inimigo
        double enemyDistance = e.getDistance();
        if (enemyDistance < 200) {
            setFire(3);
        } else if (enemyDistance < 600) {
            setFire(2);
        } else {
            setFire(1);
        }

        // Se o inimigo estiver a menos de 100 pixels de distância e o robô não estiver movendo para frente, muda a direção do movimento para frente
        // Se estiver a mais de 400 pixels de distância e o robô estiver movendo para frente, muda a direção do movimento para trás
        if (enemyDistance < 100 && !movingForward) {
            movingForward = true;
        } else if (enemyDistance > 400 && movingForward) {
            movingForward = false;
        }

    }

    public void onHitRobot(HitRobotEvent event) {
        if (event.getBearing() > -90 && event.getBearing() <= 90) {
            setBack(100); // Recua quando o robô está na frente
        } else {
            setAhead(100); // Avança quando o robô está atrás
        }
    }
}
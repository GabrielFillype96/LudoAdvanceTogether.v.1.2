// Classe responsável por cuidar da funcionalidade shake do peão do tabuleiro do jogador

// Packages
package gui.events;

// Imports internos
import gui.components.PlayerPawn;

// Imports externos
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShakeListener implements ActionListener {
    // VARIÁVEIS DE INSTÂNCIA
     private PlayerPawn playerPawnTarget;

    public ShakeListener(PlayerPawn playerPawnTarget) {
        this.playerPawnTarget = playerPawnTarget;
    }
    
     @Override
    public void actionPerformed(ActionEvent e) {
        boolean isMoving = playerPawnTarget.isMoving();
        boolean isJumpingUp = playerPawnTarget.isMoving();
        int currentX = playerPawnTarget.getPawnCurrentPos.x(); // perguntar se o "isMoving" vai aqui dentro pois é um mecanismo para impedir que o bug ao se mexer
        int originalY = playerPawnTarget.getPawnCurrentPos.y();

        // Se o peão está se movendo impede que impede que "pule"
        if (isMoving) {
            playerPawnTarget.stopBoardPawnShake();
            return;
        }

        if (isJumpingUp) {
                    // Se o peão deve "pular" então sobe 8 pixeis
                    PlayerPawn.this.setLocation(currentX, originalY - 8); 
                } else {
                    // Volta o peão para a posição original
                    PlayerPawn.this.setLocation(currentX, originalY); 
                }
                
                // Inverte a direção para o próximo ciclo
                isJumpingUp = !isJumpingUp;




    }
}

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
    
    // Sobrescreve o método nativo de "actionPerformed"
    // Método para que o peão faça o movimento shake
    @Override
    public void actionPerformed(ActionEvent e) {
        int currentX = playerPawnTarget.getX();
        int originalY = playerPawnTarget.getOriginalY();    

        // Se o peão está se movendo impede que "pule"
        if (playerPawnTarget.isMoving()) {
            playerPawnTarget.stopBoardPawnShake();
            return;
        }

        if (playerPawnTarget.isJumpingUp()) {
            // Se o peão deve "pular" então sobe 8 pixeis
            playerPawnTarget.setLocation(currentX, originalY - 8); 
        } else {
            // Volta o peão para a posição original
            playerPawnTarget.setLocation(currentX, originalY); 
        }

        // Inverte a direção para o próximo ciclo
        boolean nextStage = !playerPawnTarget.isJumpingUp();
        playerPawnTarget.setJumpingUp(nextStage);
        
    }

        
}

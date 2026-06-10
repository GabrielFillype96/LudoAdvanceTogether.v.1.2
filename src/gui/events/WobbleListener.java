// Classe responsável por cuidar da funcionalidade wobble do peão de referência

// Packages
package gui.events;

// Imports internos
import gui.components.ReferencePawn;

// Imports externos
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WobbleListener implements ActionListener {
    // VARIÁVEIS DE INSTÂNCIA
    private ReferencePawn referencePawnTarget;
        
    public WobbleListener(ReferencePawn referencePawnTarget) {
        this.referencePawnTarget = referencePawnTarget;
    }

    // Sobrescreve o método nativo de "actionPerformed"
    // Método para que o peão faça o movimento wobble
    @Override
    public void actionPerformed(ActionEvent e) {
        double actualAngle = referencePawnTarget.getActualAngle();
        boolean isInclinedToRight = referencePawnTarget.isInclinedToRight();

        if (isInclinedToRight) {
            actualAngle += 4;
            if (actualAngle >= 15) isInclinedToRight = false;
        } else {
            actualAngle -= 4;
            if (actualAngle <= -15) isInclinedToRight = true;
        }
        

        referencePawnTarget.setActualAngle(actualAngle);
        referencePawnTarget.setInclinedToRight(isInclinedToRight);
        
        // Atualiza a tela
        referencePawnTarget.repaint();  
        
    }
}




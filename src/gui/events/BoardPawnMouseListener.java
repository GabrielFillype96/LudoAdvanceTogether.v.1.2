// Classe responsável por construir o listener do peão do tabuleiro, permitindo assim a comunicação com a classe "PawnControlManager" para que a funcionalidade wobble da classe "WobbleListener" seja executada

// Packages
package gui.events;

// Imports internos
import control.PawnControlManager;

// Imports externos
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class BoardPawnMouseListener extends MouseAdapter {
    // VARIÁVEIS DE INSTÂNCIA
    private final PawnControlManager pawnControlManager;
    private final int pawnIndex;

    // Construtor da classe
    public BoardPawnMouseListener(PawnControlManager pawnControlManager, int pawnIndex) {
        this.pawnIndex = pawnIndex;
        this.pawnControlManager = pawnControlManager;
    }

    // Sobrescreve o método nativo "mouseEntered" da classe "MouseAdapter"
    // Métodos para que o peão do tabuleiro posso ser "sensível" ao hover
    @Override
    // Quando o mouse está sobre o peão do tabuleiro chama o método para fazer o peão de referência iniciar o wobble
    public void mouseEntered(MouseEvent e) {
        if (pawnControlManager != null) {
            // Se o "pawnControlManager" não for nulo executa a chamada do método (espécie de trava de segurança)
            System.out.println(
                "Mouse ENTROU no peão do tabuleiro " + pawnIndex + " - Iniciar wobble!"
            );

            /*
            * Avisa a classe "PawnControlManager" para dar a ordem direta ao "ReferencePawn" para que esta classe possa executar a funcionalidade wobble através do método "startReferencePawnWobble" que está na classe "ReferencePawn". O método "onBoardPawnHoverEntered" é uma espécie de telefone que escuta quando o mouse passa por cima do peão do tabuleiro
            */
            pawnControlManager.onBoardPawnHoverEntered(pawnIndex);
        }
    }

    // Sobrescreve o método nativo "mouseEntered" da classe "MouseAdapter"
    @Override
    // Quando o mouse sai de cima do peão do tabuleiro chama o método para fazer o peão de referência parar tremer
    public void mouseExited(java.awt.event.MouseEvent e) {
        if (pawnControlManager != null) {
            // Se o "pawnControlManager" não for nulo executa a chamada do método (espécie de trava de segurança)
            System.out.println(
                "Mouse SAIU do peão do tabuleiro" + pawnIndex + " - Parar wobble."
            );

            /*
            * Avisa a classe "PawnControlManager" para dar ordem direta ao "ReferencePawn" para que esta classe possa parar de executar a funcionalidade wobble através do método "stopReferencePawnWobble"
            */
            pawnControlManager.onBoardPawnHoverExit(pawnIndex);
        }
    }
}

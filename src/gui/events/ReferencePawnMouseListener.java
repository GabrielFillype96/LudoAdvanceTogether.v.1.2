// Classe responsável por construir o listener do peão de referência, permitindo assim a comunicação com a classe "PawnControlManager" para que a funcionalidade shake da classe "ShakeListener" seja executada. Funciona como uma espécie de telefone, assim ao instanciar um objeto dessa classe permite que ele seja capaz de se comunicar com essa funcionalidade

// Packages
package gui.events;

// Imports internos
import control.PawnControlManager;

// Imports externos
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ReferencePawnMouseListener extends MouseAdapter {
    // VARIÁVEIS DE INSTÂNCIA
    private final PawnControlManager pawnControlManager;
    private final int pawnIndex;

    // Construtor da classe
    public ReferencePawnMouseListener(PawnControlManager pawnControlManager, int pawnIndex) {
        this.pawnIndex = pawnIndex;
        this.pawnControlManager = pawnControlManager;
    }

    // Métodos para que o peão de referência posso ser "sensível" ao hover e ao clique
    // Sobrescreve o método nativo "mouseEntered" da classe abstrata "MouseAdapter"
    @Override
    // Quando o mouse está sobre o peão de referência chama o método para fazer o peão do tabuleiro do jogador iniciar o shake
    public void mouseEntered(MouseEvent e) {
        // Quando o mouse está sobre o peão do tabuleiro chama o método para fazer o peão do tabuleiro pular
        if ("NORMAL".equals(pawnControlManager.getPawnState(pawnIndex)) || 
            "DESABILITADO".equals(pawnControlManager.getPawnState(pawnIndex))) {
            // Se o estado do peão do tabuleiro/referência é "NORMAL" OU "DESABILITADO" chama o método para fazer o peão do tabuleiro pular
            System.out.println(
                "Mouse ENTROU no peão de referência " + pawnIndex + " - Iniciar tremor!"
            );

            /*
            * Avisa a classe "PawnControlManager" para que peão do tabuleiro ("PlayerPawn") possa executar a funcionalidade de pulo através do método "startBoardPawnShake" que está na classe "PlayerPawn". O método "onReferencePawnHoverEntered" é uma espécie de telefone que escuta quando o mouse passa por cima do peão de referência
            */
            pawnControlManager.onReferencePawnHoverEntered(pawnIndex);
        }  
    }

    // Sobrescreve o método nativo de "mouseExited" da classe abstrata "MouseAdapter"
    @Override
    // Quando o mouse sai de cima do peão de referência chama o método para fazer o peão do tabuleiro parar de pular
    public void mouseExited(java.awt.event.MouseEvent e) {
        if ("NORMAL".equals(pawnControlManager.getPawnState(pawnIndex)) ||
            "DESABILITADO".equals(pawnControlManager.getPawnState(pawnIndex))) {
            // Se o estado do peão do tabuleiro/referência é "NORMAL" OU "DESABILITADO" chama o método para fazer o peão do tabuleiro parar pular    
            System.out.println(
                "Mouse SAIU do peão de referência " + pawnIndex + " - Parar tremor."
            );

            /*
            * Avisa a classe "PawnControlManager" para que peão do tabuleiro ("PlayerPawn") possa executar a funcionalidade de parar pulo através do método "stopBoardPawnShake" que está na classe "PlayerPawn". O método "onReferencePawnHoverExited" é uma espécie de telefone que escuta quando o mouse sai de cima do peão de referência
            */
            pawnControlManager.onReferencePawnHoverExited(pawnIndex);
        }
    }

    // Sobrescreve o método nativo de "mouseClicked" da classe abstrata "MouseAdapter"
    @Override
    // Quando o peão de referência é clicado, chama o método para que o peão do tabuleiro seja selecionado para realizar sua ação
    public void mouseClicked(java.awt.event.MouseEvent e) {
        if ("NORMAL".equals(pawnControlManager.getPawnState(pawnIndex))) {
            // Se o estado do peão do tabuleiro/referência é "NORMAL" chama o método para fazer o peão do tabuleiro executar sua ação (avançar, retroceder, etc)
            System.out.println(
                "Peão de referência " + pawnIndex + " foi CLICADO! Avisar o GameManager!"
            );

            /*
            * Avisa a classe "PawnControlManager" para que peão do tabuleiro ("PlayerPawn") possa executar sua ação de avanço ou retroação. O método "onReferencePawnClicked" é uma espécie de telefone que escuta quando o peão de referência é clicado
            */
            pawnControlManager.onReferencePawnClicked(pawnIndex);
        }
    }

    
}

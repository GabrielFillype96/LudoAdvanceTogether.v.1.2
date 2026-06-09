// Classe responsável por realizar a comunicação entre as funcionalidades dos peões do tabuleiro e de referência, controlando principalmente elementos UX

// Packages
package control;

// Imports internos
import gui.windows.PawnControlContainer;
import gui.components.PlayerPawn;
import gui.components.ReferencePawn;
import gui.windows.BoardScreen;


public class PawnControlManager {
    // VARIÁVEIS DE INSTÂNCIA
    private String[] pawnStates = new String[4];
    private BoardScreen boardScreen;
    private PawnControlContainer pawnControlContainer;

    public PawnControlManager(BoardScreen boardScreen) {
        this.boardScreen = boardScreen;
        // Inicializa todos como NORMAL no começo do jogo, por exemplo
        for(int i = 0; i < 4; i++) {
            pawnStates[i] = "NORMAL";
        }
    }

    /**
     * Retorna o estado atual de um peão específico.
     * @param pawnIndex O índice do peão (0 a 3).
     * @return O estado ("NORMAL", "DESABILITADO", "DOURADO") ou "INVALIDO".
     */
    public String getPawnState(int pawnIndex) {
        // Trava de segurança para evitar erro de ArrayOutOfBounds
        if (pawnIndex < 0 || pawnIndex >= 4) {
            return "INVÁLIDO"; 
        }
        
        // Retorna o estado guardado na memória do Manager
        return pawnStates[pawnIndex];
    }

    public void setPawnState(int pawnIndex, String newPawnState) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            pawnStates[pawnIndex] = newPawnState;
        }
    }

    public void setPawnControlContainer(PawnControlContainer container) {
        this.pawnControlContainer = container;
    }
    
    public void onReferencePawnHoverEntered(int pawnIndex) {
        System.out.println(
            "[Manager] Iniciando tremor no peão físico " + pawnIndex
        );
        // FUTURO CÓDIGO AQUI:
        PlayerPawn boardPawn = boardScreen.getPlayer1Pawn(pawnIndex);
        if (boardPawn != null) {
            boardPawn.startBoardPawnShake();
        }
    }

    public void onReferencePawnHoverExited(int pawnIndex) {
        System.out.println(
            "[Manager] Parando tremor no peão físico " + pawnIndex
        );
        PlayerPawn boardPawn = boardScreen.getPlayer1Pawn(pawnIndex);
        if (boardPawn != null) {
            boardPawn.stopBoardPawnShake();
        }
    }

    public void onReferencePawnClicked(int pawnIndex) {
        System.out.println(
            "[Manager] O peão " + pawnIndex + " foi escolhido para jogar!"
        );
        // FUTURO CÓDIGO AQUI:
        // Travar a tela de escolhas
        // Passar o peão para o GameManager iniciar a animação de andar pelas casas
    }

    public void onBoardPawnHoverEntered(int pawnIndex) {
        System.out.println(
            "[Manager] Iniciando wobble no peão de referência " + pawnIndex
        );
         if (pawnControlContainer != null) { 
            
            // 2. Agora sim, com segurança, pegamos o peão
            ReferencePawn referencePawn = pawnControlContainer.getReferencePawn(pawnIndex);
            
            // 3. Confere se o peão realmente foi encontrado
            if (referencePawn != null) { 
                referencePawn.startReferencePawnWobble();
            }
        }
    }

    public void onBoardPawnHoverExit(int pawnIndex) {
        System.out.println(
            "[Manager] Parando wobble no peão de referência " + pawnIndex
        );
        if (pawnControlContainer != null) {
            ReferencePawn referencePawn = pawnControlContainer.getReferencePawn(pawnIndex);
            
            if (referencePawn != null) {
                referencePawn.stopReferencePawnWobble();
            }
        }
    }
}
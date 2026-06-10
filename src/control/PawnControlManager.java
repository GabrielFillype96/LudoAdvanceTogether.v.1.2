// Classe responsável por realizar a comunicação entre as funcionalidades dos peões do tabuleiro e de referência, controlando principalmente elementos UX

// Packages
package control;

// Imports internos
import gui.windows.PawnControlContainer;
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
       
        if (boardScreen != null) {
            boardScreen.startBoardPawnShake(pawnIndex);
        }
    }

    public void onReferencePawnHoverExited(int pawnIndex) {
        System.out.println(
            "[Manager] Parando tremor no peão físico " + pawnIndex
        );

        if (boardScreen != null) {
            boardScreen.stopBoardPawnShake(pawnIndex);
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


    /*
    * O fluxo de comunicação funciona assim: a classe "BoardScreen" chama o método "onBoardPawnHoverEntered" aqui no "PawnControlManager" --> O "PawnControlManager" executa o método "onBoardPawnHoverEntered" e manda o "PawnControlContainer" realizar o método "startReferencePawnWobble" --> O "PawnControlContainer" executa o método "startReferencePawnWobble" que por sua vez faz o peão de referência wobble
    * O método "onBoardPawnHoverEntered" funciona como uma espécie de telefone, pois ele comunica à classe "PawnControlContainer" para que ela faça o peão wobble
    */
    public void onBoardPawnHoverEntered(int pawnIndex) {
        System.out.println(
            "[Manager] Iniciando wobble no peão de referência " + pawnIndex
        );
        if (pawnControlContainer != null) { 
            // Se o container do peão de referência não for nulo, determina que a classe "PawnControlContainer" execute a funcionalidade wobble
            pawnControlContainer.startReferencePawnWobble(pawnIndex);
        }
    }

    /*
    * O fluxo de comunicação funciona assim: "BoardScreen" chama o método "onBoardPawnHoverExit" aqui no "PawnControlManager" --> O "PawnControlManager" executa o método "onBoardPawnHoveExit" e manda o "PawnControlContainer" realizar o método "stopReferencePawnWobble" --> O "PawnControlContainer" executa o método "stopReferencePawnWobble" que por sua vez faz o peão de referência parar de wobble
    * O método "onBoardPawnHoverExit" funciona como uma espécie de telefone, pois ele comunica à classe "PawnControlContainer" para que ela faça o peão parar de wobble
    */
    public void onBoardPawnHoverExit(int pawnIndex) {
        System.out.println(
            "[Manager] Parando wobble no peão de referência " + pawnIndex
        );
        if (pawnControlContainer != null) {
            // Se o container do peão de referência não for nulo, determina que a classe "PawnControlContainer" pare de executar a funcionalidade wobble
            pawnControlContainer.stopReferencePawnWobble(pawnIndex);
        }
    }
}
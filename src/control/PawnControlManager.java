// Classe responsável por realizar a comunicação entre as funcionalidades dos peões do tabuleiro e de referência, controlando principalmente elementos UX

// Packages
package control;

// Imports internos
import gui.windows.PawnControlContainer;
import gui.windows.BoardScreen;


public class PawnControlManager {
    // VARIÁVEIS DE INSTÂNCIA
    private String[] pawnState = new String[4];
    private BoardScreen boardScreen;
    private PawnControlContainer pawnControlContainer;
    private int pendingSteps = 0;
    private String pendingEffect = "";
    private boolean awaitingPawnSelection = false;
    private GameManager gameManager;


    public PawnControlManager(BoardScreen boardScreen, GameManager gameManager) {
        this.boardScreen = boardScreen;
        this.gameManager = gameManager;
        // Inicializa todos como NORMAL no começo do jogo, por exemplo
        for(int i = 0; i < 4; i++) {
            pawnState[i] = "NORMAL";
        }
    }

    /**
     * Método getter que retorna o estado atual de um peão específico.
     * @param pawnIndex O índice do peão (0 a 3).
     * @return O estado ("NORMAL", "DESABILITADO", "DOURADO") ou "INVALIDO".
     */
    public String getPawnState(int pawnIndex) {
        // Trava de segurança para evitar erro de ArrayOutOfBounds
        if (pawnIndex < 0 || pawnIndex >= 4) {
            // se o índice do peão é menor que 0 OU igual ou maior a 4, retorna "INVÁLIDO"
            return "INVÁLIDO"; 
        }
        
        // Retorna o estado que o peão está ("NORMAL", "DESABILITADO" e "DOURADO")
        return pawnState[pawnIndex];
    }

    /**
    ** Método setter para que a classe "PawnControlContainer" permita alterar a variável privada "pawnState" e possa mudar seu valor de "NORMAL" para outro estado ("DESABILITADO" ou "DOURADO")
    ** Lógica: a variável "pawnState" nasce com o estado "NORMAL" --> Quando algum evento ocorre no jogo, o método "pawnVisualState" da classe "PawnControlContainer" é acionado e o estado do peão muda --> Esse novo estado entra no setter "pawnControlManager.setPawnState(pawnIndex, pawnState)" alterando o parâmetro "pawnState" referente ao "newPawnState" --> Assim a variável "pawnState" deixa de ser "NORMAL" para assumir outro estado
    * @param pawnIndex O índice do peão (0 a 3)
    * @param newPawnState O novo estado que peão assumiu
    */
    public void setPawnState(int pawnIndex, String newPawnState) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            pawnState[pawnIndex] = newPawnState;
        }
    }

    // Método setter para que a classe "PawnControlContainer" permita alterar a variável "pawnControlContainer" e possa passar a si mesmo para ela
    public void setPawnControlContainer(PawnControlContainer pawnControlContainer) {
        this.pawnControlContainer = pawnControlContainer;
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

    /**
     ** Método acionado quando o jogador clica em um peão de referência na lateral da tela.
    */
    public void onReferencePawnClicked(int pawnIndex) {
        System.out.println(
            "[PawnControlManager] O peão " + pawnIndex + " foi escolhido para jogar!"
        );

        if (!awaitingPawnSelection) {
            System.out.println(
                "[PawnControlManager] Clique recusado: Você precisa responder uma carta corretamente primeiro.");
            return;
        }

        System.out.println(
            "[PawnControlManager] SUCESSO! O jogador escolheu mover o peão índice: " + pawnIndex
        );
        System.out.println(
            "[PawnControlManager] Movendo " + this.pendingSteps + " casas com o efeito: " + this.pendingEffect
        );

        if (this.gameManager != null) {
            // Vamos pedir ao GameManager para mover o peão estratégico correspondente
            this.gameManager.moveChosenPawn(pawnIndex, this.pendingSteps, this.pendingEffect);
        }

        // Após aplicar o movimento, limpamos a memória para o peão não andar duas vezes
        this.awaitingPawnSelection = false;
        this.pendingSteps = 0;
        this.pendingEffect = "";
    }

    /**
     ** Prepara o gerenciador de peões dizendo que há uma certa quantidade de casas na memória aguardando o jogador escolher quem vai andar
    */
    public void preparePendingMovement(int steps, String effect) {
        this.pendingSteps = steps;
        this.pendingEffect = effect;
        this.awaitingPawnSelection = true;
        System.out.println(
            "[PawnControlManager] Movimento guardado: " + steps + " casas. Aguardando seleção do peão..."
        );
    }


    /*
    * O fluxo de comunicação funciona assim: a classe "BoardScreen" chama o método "onBoardPawnHoverEntered" aqui no "PawnControlManager" --> O "PawnControlManager" executa o método "onBoardPawnHoverEntered" e manda o "PawnControlContainer" realizar o método "startReferencePawnWobble" --> O "PawnControlContainer" executa o método "startReferencePawnWobble" que por sua vez faz o peão de referência wobble
    * O método "onBoardPawnHoverEntered" funciona como uma espécie de telefone, pois ele comunica à classe "PawnControlContainer" para que ela faça o peão wobble
    */
    public void onBoardPawnHoverEntered(int pawnIndex) {
        System.out.println(
            "[PawnControlManager] Iniciando wobble no peão de referência " + pawnIndex
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
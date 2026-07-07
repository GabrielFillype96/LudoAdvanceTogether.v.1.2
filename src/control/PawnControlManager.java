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
    
    // Memória do peão selecionado (-1 significa nenhum)
    private int selectedPawnIndex = -1; 

    public PawnControlManager(BoardScreen boardScreen, GameManager gameManager) {
        this.boardScreen = boardScreen;
        this.gameManager = gameManager;
        // Inicializa todos como NORMAL no começo do jogo
        for(int i = 0; i < 4; i++) {
            pawnState[i] = "NORMAL";
        }
    }

    public String getPawnState(int pawnIndex) {
        if (pawnIndex < 0 || pawnIndex >= 4) {
            return "INVÁLIDO"; 
        }
        return pawnState[pawnIndex];
    }

    public void setPawnState(int pawnIndex, String newPawnState) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            pawnState[pawnIndex] = newPawnState;
        }
    }

    public void setPawnControlContainer(PawnControlContainer pawnControlContainer) {
        this.pawnControlContainer = pawnControlContainer;
    }
    
    /**
     * NOVO: Método ponte para permitir que o GameManager mude o estado visual e lógico de um peão
     */
    public void updatePawnVisualState(int pawnIndex, String state) {
        if (pawnControlContainer != null) {
            pawnControlContainer.pawnVisualState(pawnIndex, state);
        } else {
            setPawnState(pawnIndex, state);
        }
    }
    
    // --- MÉTODOS DE HOVER (Permitem peões desabilitados, bloqueiam APENAS os dourados) ---
    public void onReferencePawnHoverEntered(int pawnIndex) {
        String state = getPawnState(pawnIndex);
        if ("DOURADO".equalsIgnoreCase(state)) {
            return;
        }
        
        if (boardScreen != null) {
            boardScreen.startBoardPawnShake(pawnIndex);
        }
    }

    public void onReferencePawnHoverExited(int pawnIndex) {
        if (boardScreen != null) {
            boardScreen.stopBoardPawnShake(pawnIndex);
        }
    }

    public void onBoardPawnHoverEntered(int pawnIndex) {
        String state = getPawnState(pawnIndex);
        if ("DOURADO".equalsIgnoreCase(state)) {
            return;
        }
        
        if (pawnControlContainer != null) { 
            pawnControlContainer.startReferencePawnWobble(pawnIndex);
        }
    }

    public void onBoardPawnHoverExit(int pawnIndex) {
        if (pawnControlContainer != null) {
            pawnControlContainer.stopReferencePawnWobble(pawnIndex);
        }
    }

    // --- LÓGICA DE CLIQUE (Com bloqueio total para peões inativos/finalizados) ---
    public void onReferencePawnClicked(int pawnIndex) {
        
        if (!awaitingPawnSelection) {
            System.out.println("[PawnControlManager] Clique recusado: Você precisa responder uma carta corretamente primeiro.");
            return;
        }

        // TRAVA JOGABILIDADE: Impede que o jogador selecione ou mova um peão desabilitado ou dourado
        String state = getPawnState(pawnIndex);
        if ("DESABILITADO".equalsIgnoreCase(state) || "DOURADO".equalsIgnoreCase(state)) {
            System.out.println("[PawnControlManager] Clique recusado: O peão " + pawnIndex + " está " + state + " e não pode jogar!");
            return;
        }

        // CENÁRIO 1: O jogador clicou num peão novo (Primeiro clique / Seleção)
        if (this.selectedPawnIndex != pawnIndex) {
            this.selectedPawnIndex = pawnIndex; // Guarda quem foi o escolhido
            System.out.println("[PawnControlManager] Peão " + pawnIndex + " SELECIONADO. Clique nele novamente para confirmar!");
            
            // MOSTRA A PREVISÃO VISUAL NO TABULEIRO!
            if (gameManager != null) {
                gameManager.showMovementPreview(pawnIndex, pendingSteps, pendingEffect);
            }
            return; 
        }

        // CENÁRIO 2: O jogador clicou no MESMO peão que já estava selecionado (Segundo clique / Confirmação)
        if (this.selectedPawnIndex == pawnIndex) {
            System.out.println("[PawnControlManager] Jogada CONFIRMADA para o peão " + pawnIndex + "!");

            if (this.gameManager != null) {
                // Tenta fazer o movimento
                boolean movimentoRealizado = this.gameManager.moveChosenPawn(pawnIndex, this.pendingSteps, this.pendingEffect);

                if (movimentoRealizado) {
                    // SUCESSO: Limpa a memória toda para o próximo turno
                    this.awaitingPawnSelection = false;
                    this.pendingSteps = 0;
                    this.pendingEffect = "";
                    this.selectedPawnIndex = -1; // Reseta a seleção
                    
                   
                    
                    System.out.println("[PawnControlManager] Turno encerrado com sucesso.");
                } else {
                    // FALHA
                    this.selectedPawnIndex = -1;
                    
                    if (this.boardScreen != null) {
                        this.boardScreen.clearPreview();
                    }
                    
                    System.out.println("[PawnControlManager] Movimento inválido. Seleção cancelada. Escolha outro peão.");
                }
            }
        }
    }

    public void preparePendingMovement(int steps, String effect) {
        this.pendingSteps = steps;
        this.pendingEffect = effect;
        this.awaitingPawnSelection = true;
        this.selectedPawnIndex = -1; 
        System.out.println(
            "[PawnControlManager] Movimento guardado: " + steps + " casas. Aguardando seleção do peão..."
        );
    }
}
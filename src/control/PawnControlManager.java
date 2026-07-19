package control;

import gui.windows.PawnControlContainer;
import gui.windows.BoardScreen;

public class PawnControlManager {
    private String[] pawnState = new String[4];
    private BoardScreen boardScreen;
    private PawnControlContainer pawnControlContainer;
    private int pendingSteps = 0;
    private String pendingEffect = "";
    private boolean awaitingPawnSelection = false;
    private GameManager gameManager;
    private int selectedPawnIndex = -1; 

    public PawnControlManager(BoardScreen boardScreen, GameManager gameManager) {
        this.boardScreen = boardScreen;
        this.gameManager = gameManager;
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
        if (this.pawnControlContainer != null) {
            // Sincroniza o estado visual da película com a variável lógica de controle
            this.pawnControlContainer.setLocked(!this.awaitingPawnSelection);
        }
    }
    
    public void updatePawnVisualState(int pawnIndex, String state) {
        if (pawnControlContainer != null) {
            pawnControlContainer.pawnVisualState(pawnIndex, state);
        } else {
            setPawnState(pawnIndex, state);
        }
    }
    
    // Controla o cursor e os efeitos de Hover nos peões de referência (painel)
    public void onReferencePawnHoverEntered(int pawnIndex) {
        gui.components.ReferencePawn refPawn = (pawnControlContainer != null) ? pawnControlContainer.getReferencePawn(pawnIndex) : null;
        
        // Se o painel estiver bloqueado, força o cursor padrão e barra a animação
        if (!awaitingPawnSelection) {
            if (refPawn != null) refPawn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
            return; 
        }
        
        // Se o peão estiver dourado ou desabilitado, impede a mãozinha e o shake
        String state = getPawnState(pawnIndex);
        if ("DOURADO".equalsIgnoreCase(state) || "DESABILITADO".equalsIgnoreCase(state)) {
            if (refPawn != null) refPawn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
            return;
        }
        
        // Caso passe nas validações, ativa a mãozinha e treme o peão correspondente no tabuleiro
        if (refPawn != null) {
            refPawn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
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

    // Controla o cursor e os efeitos de Hover nos peões físicos (tabuleiro)
    public void onBoardPawnHoverEntered(int pawnIndex) {
        // Busca o peão correspondente ao jogador humano (ID 0) no tabuleiro
        gui.components.PlayerPawn boardPawn = (boardScreen != null) ? boardScreen.getPlayerPawn(0, pawnIndex) : null;
        
        // Se o painel estiver bloqueado, força o cursor padrão no peão do tabuleiro e barra o wobble
        if (!awaitingPawnSelection) {
            if (boardPawn != null) boardPawn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
            return; 
        }
        
        // Se o peão estiver dourado ou desabilitado, impede a mãozinha e o wobble
        String state = getPawnState(pawnIndex);
        if ("DOURADO".equalsIgnoreCase(state) || "DESABILITADO".equalsIgnoreCase(state)) {
            if (boardPawn != null) boardPawn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
            return;
        }
        
        // Caso passe nas validações, ativa a mãozinha no tabuleiro e balança a referência no painel
        if (boardPawn != null) {
            boardPawn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
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

    public void onReferencePawnClicked(int pawnIndex) {
        if (!awaitingPawnSelection) {
            System.out.println("[PawnControlManager] Clique recusado: Painel bloqueado visualmente.");
            return;
        }

        String state = getPawnState(pawnIndex);
        if ("DESABILITADO".equalsIgnoreCase(state) || "DOURADO".equalsIgnoreCase(state)) {
            System.out.println("[PawnControlManager] Clique recusado: Peão inválido.");
            return;
        }

        if (this.selectedPawnIndex != pawnIndex) {
            this.selectedPawnIndex = pawnIndex; 
            System.out.println("[PawnControlManager] Peão " + pawnIndex + " selecionado.");
            
            if (gameManager != null) {
                gameManager.showMovementPreview(pawnIndex, pendingSteps, pendingEffect);
                
                int numeroPeao = getRealPawnNumber(pawnIndex);
                if (!gameManager.isMovimentoAutomaticoEmAndamento()) {
                    gameManager.emitirStatus("♟️ Peão " + numeroPeao + " selecionado para andar. Confirme sua jogada clicando nele novamente!", java.awt.Color.WHITE);
                }
            }
            return; 
        }

        if (this.selectedPawnIndex == pawnIndex) {
            System.out.println("[PawnControlManager] Jogada CONFIRMADA para o peão " + pawnIndex + "!");

            if (this.gameManager != null) {
                boolean movimentoRealizado = this.gameManager.moveChosenPawn(pawnIndex, this.pendingSteps, this.pendingEffect);

                if (movimentoRealizado) {
                    this.awaitingPawnSelection = false;
                    this.pendingSteps = 0;
                    this.pendingEffect = "";
                    this.selectedPawnIndex = -1;
                    
                    // Bloqueia novamente o painel após concluir com sucesso
                    if (this.pawnControlContainer != null) {
                        this.pawnControlContainer.setLocked(true);
                    }
                    System.out.println("[PawnControlManager] Turno encerrado com sucesso.");
                } else {
                    this.selectedPawnIndex = -1;
                    if (this.boardScreen != null) {
                        this.boardScreen.clearPreview();
                    }
                    System.out.println("[PawnControlManager] Movimento inválido.");
                }
            }
        }
    }

    public void preparePendingMovement(int steps, String effect) {
        if ("RETROCEDER".equalsIgnoreCase(effect) || "VOLTAR".equalsIgnoreCase(effect) || "RETRÓGRADO".equalsIgnoreCase(effect)) {
            int currentPlayerId = (this.gameManager.getTurnManager() != null) ? this.gameManager.getTurnManager().getCurrentTurn() : 0;
            int furthestPawnIndex = this.gameManager.getFurthestPawnIndex(currentPlayerId);
            
            if (furthestPawnIndex != -1) {
                this.gameManager.moveChosenPawn(furthestPawnIndex, steps, effect);
                this.awaitingPawnSelection = false;
                
                if (currentPlayerId == 0 && this.gameManager.getTurnManager() != null) {
                    this.gameManager.getTurnManager().nextTurn();
                }
                return;
            } else {
                if (this.gameManager.getTurnManager() != null) {
                    this.gameManager.getTurnManager().nextTurn();
                }
                return;
            }
        }
        
        this.pendingSteps = steps;
        this.pendingEffect = effect;
        this.awaitingPawnSelection = true;
        this.selectedPawnIndex = -1; 
        
        // Libera o painel visualmente para o clique do jogador humano
        if (this.pawnControlContainer != null) {
            this.pawnControlContainer.setLocked(false);
        }
        System.out.println("[PawnControlManager] Aguardando seleção do peão. Painel liberado.");
    }

    public void resetHumanPawnsVisuals() {
        if (this.pawnControlContainer != null) {
            this.pawnControlContainer.resetAllPawnsToNormal();
        }
    }

    public int getRealPawnNumber(int pawnIndex) {
        if (this.pawnControlContainer != null && this.pawnControlContainer.getReferencePawn(pawnIndex) != null) {
            return this.pawnControlContainer.getReferencePawn(pawnIndex).getPawnNumber();
        }
        return pawnIndex + 1;
    }

    // Retorna se o sistema está travado aguardando o clique em um peão válido
    public boolean isAwaitingPawnSelection() {
        return this.awaitingPawnSelection;
    }
}
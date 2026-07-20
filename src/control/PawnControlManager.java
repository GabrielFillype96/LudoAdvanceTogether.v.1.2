package control;

import gui.windows.PawnControlContainer;
import gui.windows.BoardScreen;
import network.GameClient;
import network.NetworkMessage;

import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.List;

public class PawnControlManager {
    private String[] pawnState = new String[4];
    private BoardScreen boardScreen;
    private PawnControlContainer pawnControlContainer;
    private int pendingSteps = 0;
    private String pendingEffect = "";
    private boolean awaitingPawnSelection = false;
    private GameManager gameManager;
    private GameClient gameClient;
    private int selectedPawnIndex = -1; 
    private boolean hasUserInteracted = false;
    private int currentlyShakingPawnIndex = -1;

    public PawnControlManager(BoardScreen boardScreen, GameManager gameManager) {
        this.boardScreen = boardScreen;
        this.gameManager = gameManager;
        for(int i = 0; i < 4; i++) {
            pawnState[i] = "NORMAL";
        }
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
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

    private void pararShakeAtual() {
        if (currentlyShakingPawnIndex != -1) {
            if (boardScreen != null) {
                boardScreen.stopBoardPawnShake(currentlyShakingPawnIndex);
            }
            currentlyShakingPawnIndex = -1;
        }
    }

    private void exibirMensagemEscolhaInicial() {
        if (gameManager == null) return;

        List<Integer> peoesValidos = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if ("NORMAL".equalsIgnoreCase(getPawnState(i))) {
                peoesValidos.add(getRealPawnNumber(i));
            }
        }

        if (peoesValidos.isEmpty()) {
            return;
        }

        String textoPeoes;
        if (peoesValidos.size() == 1) {
            textoPeoes = "o peão " + peoesValidos.get(0);
        } else {
            StringBuilder sb = new StringBuilder("os peões ");
            for (int i = 0; i < peoesValidos.size(); i++) {
                if (i > 0) {
                    if (i == peoesValidos.size() - 1) {
                        sb.append(" e ");
                    } else {
                        sb.append(", ");
                    }
                }
                sb.append(peoesValidos.get(i));
            }
            textoPeoes = sb.toString();
        }

        gameManager.emitirStatus("👉 Escolha entre " + textoPeoes + ".", Color.WHITE);
    }

    public void onCentralPawnFocused(int pawnIndex) {
        if (!awaitingPawnSelection) {
            pararShakeAtual();
            return;
        }

        pararShakeAtual();

        if (this.selectedPawnIndex != -1 && this.selectedPawnIndex != pawnIndex) {
            this.hasUserInteracted = true;
        }

        String state = getPawnState(pawnIndex);
        int numeroPeao = getRealPawnNumber(pawnIndex);

        if ("NORMAL".equalsIgnoreCase(state)) {
            this.selectedPawnIndex = pawnIndex;

            if (boardScreen != null) {
                boardScreen.startBoardPawnShake(pawnIndex);
                currentlyShakingPawnIndex = pawnIndex;
            }

            if (gameManager != null) {
                gameManager.showMovementPreview(pawnIndex, pendingSteps, pendingEffect);
                if (!gameManager.isMovimentoAutomaticoEmAndamento()) {
                    if (hasUserInteracted) {
                        gameManager.emitirStatus("♟️ Peão " + numeroPeao + " selecionado. Confirme sua jogada clicando nele!", Color.WHITE);
                    } else {
                        exibirMensagemEscolhaInicial();
                    }
                }
            }
        } else {
            this.selectedPawnIndex = -1;
            if (boardScreen != null) {
                boardScreen.clearPreview();
            }

            if (gameManager != null && !gameManager.isMovimentoAutomaticoEmAndamento()) {
                if (hasUserInteracted) {
                    String mensagemStatus;
                    if ("DOURADO".equalsIgnoreCase(state)) {
                        mensagemStatus = "O peão " + numeroPeao + " já chegou ao final do percurso.";
                    } else {
                        if (gameManager.isPeaoNaBase(0, pawnIndex)) {
                            mensagemStatus = "O peão " + numeroPeao + " está na base. Você precisa de 1 ou 6 para tirá-lo.";
                        } else {
                            mensagemStatus = "O peão " + numeroPeao + " não tem movimentos válidos nesta jogada.";
                        }
                    }
                    gameManager.emitirStatus(mensagemStatus, new Color(230, 180, 80));
                } else {
                    exibirMensagemEscolhaInicial();
                }
            }
        }
    }
    
    public void onReferencePawnHoverEntered(int pawnIndex) {
        gui.components.ReferencePawn refPawn = (pawnControlContainer != null) ? pawnControlContainer.getReferencePawn(pawnIndex) : null;
        
        if (!awaitingPawnSelection) {
            if (refPawn != null) refPawn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return; 
        }
        
        String state = getPawnState(pawnIndex);
        if ("DOURADO".equalsIgnoreCase(state) || "DESABILITADO".equalsIgnoreCase(state)) {
            if (refPawn != null) refPawn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        
        if (refPawn != null) {
            refPawn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public void onReferencePawnHoverExited(int pawnIndex) {
    }

    public void onBoardPawnHoverEntered(int pawnIndex) {
        gui.components.PlayerPawn boardPawn = (boardScreen != null) ? boardScreen.getPlayerPawn(0, pawnIndex) : null;
        
        if (!awaitingPawnSelection) {
            if (boardPawn != null) boardPawn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return; 
        }
        
        String state = getPawnState(pawnIndex);
        if ("DOURADO".equalsIgnoreCase(state) || "DESABILITADO".equalsIgnoreCase(state)) {
            if (boardPawn != null) boardPawn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        
        if (boardPawn != null) {
            boardPawn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
            return;
        }

        String state = getPawnState(pawnIndex);
        if ("DESABILITADO".equalsIgnoreCase(state) || "DOURADO".equalsIgnoreCase(state)) {
            if (!hasUserInteracted) {
                hasUserInteracted = true;
            }
            onCentralPawnFocused(pawnIndex);
            return;
        }

        if (!hasUserInteracted) {
            this.hasUserInteracted = true;
            onCentralPawnFocused(pawnIndex);
            return;
        }

        if (this.selectedPawnIndex == pawnIndex) {
            if (this.gameManager != null) {
                pararShakeAtual();
                boolean movimentoRealizado = this.gameManager.moveChosenPawn(pawnIndex, this.pendingSteps, this.pendingEffect);

                if (movimentoRealizado) {
                    // Notifica a rede sobre o movimento realizado
                    if (gameClient != null) {
                        String payload = pawnIndex + ":" + this.pendingSteps + ":" + this.pendingEffect;
                        gameClient.send(new NetworkMessage("MOVE_PAWN", gameClient.getMyPlayerId(), payload));
                    }

                    this.awaitingPawnSelection = false;
                    this.hasUserInteracted = false;
                    this.pendingSteps = 0;
                    this.pendingEffect = "";
                    this.selectedPawnIndex = -1;
                    
                    if (this.pawnControlContainer != null) {
                        this.pawnControlContainer.setLocked(true);
                    }
                } else {
                    this.selectedPawnIndex = -1;
                    if (this.boardScreen != null) {
                        this.boardScreen.clearPreview();
                    }
                }
            }
        } else {
            onCentralPawnFocused(pawnIndex);
        }
    }

    public void onBoardPawnClicked(int pawnIndex) {
        onReferencePawnClicked(pawnIndex);
    }

    public void preparePendingMovement(int steps, String effect) {
        if ("RETROCEDER".equalsIgnoreCase(effect) || "VOLTAR".equalsIgnoreCase(effect) || "RETRÓGRADO".equalsIgnoreCase(effect)) {
            int currentPlayerId = (this.gameManager.getTurnManager() != null) ? this.gameManager.getTurnManager().getCurrentTurn() : 0;
            int furthestPawnIndex = this.gameManager.getFurthestPawnIndex(currentPlayerId);
            
            if (furthestPawnIndex != -1) {
                pararShakeAtual();
                this.gameManager.moveChosenPawn(furthestPawnIndex, steps, effect);
                this.awaitingPawnSelection = false;
                this.hasUserInteracted = false;
                
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
        this.hasUserInteracted = false;
        
        if (this.pawnControlContainer != null) {
            this.pawnControlContainer.setLocked(false);
            onCentralPawnFocused(this.pawnControlContainer.getCurrentIndex());
        } else {
            this.selectedPawnIndex = -1;
        }
    }

    public void resetHumanPawnsVisuals() {
        this.awaitingPawnSelection = false;
        this.hasUserInteracted = false;
        this.selectedPawnIndex = -1;
        pararShakeAtual();
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

    public boolean isAwaitingPawnSelection() {
        return this.awaitingPawnSelection;
    }
}
// Classe responsável por gerenciar as regras de movimentação dos peões de acordo com as cartas

package control;

import gui.windows.BoardScreen;
import gui.components.PlayerPawn;
import java.awt.Point;
import javax.swing.Timer;
import javax.swing.JOptionPane;

public class GameManager {
    
    private BoardScreen boardScreen;
    private Timer timerAnimation; 
    private PawnControlManager pawnControlManager;
    private TurnManager turnManager;
    
    // NOVA VARIÁVEL: O Cérebro da Inteligência Artificial
    private CPUIManager cpuIManager;

    public GameManager(BoardScreen boardScreen) {
        this.boardScreen = boardScreen;
    }

    public void setTurnManager(TurnManager turnManager) {
        this.turnManager = turnManager;
    }

    public TurnManager getTurnManager() {
        return this.turnManager;
    }

    public void setPawnControlManager(PawnControlManager pawnControlManager) {
        this.pawnControlManager = pawnControlManager;
    }
    
    // NOVO MÉTODO: Injeção de dependência da IA
    public void setCPUIManager(CPUIManager cpuIManager) {
        this.cpuIManager = cpuIManager;
    }

    public void cardResultVerification(boolean correct, String cardValue, String cardEffect) {
        if (boardScreen == null) return;
        if (timerAnimation != null && timerAnimation.isRunning()) return;

        int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;
 
        PlayerPawn p1 = boardScreen.getPlayerPawn(activePlayerId, 0);
        
        // BÚSSOLA CORRIGIDA: Pega o caminho específico da cor do jogador atual
        Point[] mapaCasas = boardScreen.getCaminhoCasas(activePlayerId);

        if (p1 == null || mapaCasas == null) return;

        if (!correct) {
            System.out.println("[GameManager] Resposta incorreta. Passando o turno.");
            if (this.turnManager != null) {
                this.turnManager.nextTurn();
            }
            return;
        }

       if ("AVANÇAR".equalsIgnoreCase(cardEffect) || "VOLTAR".equalsIgnoreCase(cardEffect) || "RETRÓGRADO".equalsIgnoreCase(cardEffect)) {
            try {
                String cardValueTreated = cardValue.trim();
                if (cardValueTreated.contains("/")) {
                    cardValueTreated = cardValueTreated.split("/")[0].trim();
                }

                int valorDado = Integer.parseInt(cardValueTreated);

                if (this.pawnControlManager != null) {
                    
                    java.util.List<Integer> peoesDisponiveis = updatePlayablePawns(valorDado, activePlayerId);
                    
                    if (peoesDisponiveis.isEmpty()) {
                        System.out.println("[Status Label] Nenhum peão do Jogador " + activePlayerId + " pode se mover.");
                        if (activePlayerId == 0) {
                            JOptionPane.showMessageDialog(boardScreen, 
                                "Nenhum peão pode se mover com este número.", 
                                "Turno Sem Movimentos", JOptionPane.WARNING_MESSAGE);
                        }
                        if (this.turnManager != null) this.turnManager.nextTurn();
                        return;
                    }

                    // ====== DELEGAÇÃO DE RESPONSABILIDADE ======
                    if (activePlayerId > 0) {
                        // É A VEZ DA CPU: O GameManager lava as mãos e passa a bola para a IA!
                        System.out.println("[GameManager] Delegando escolha de peão para a CPUIManager...");
                        if (this.cpuIManager != null) {
                            this.cpuIManager.iniciarJogadaCPU(activePlayerId, peoesDisponiveis, valorDado, cardEffect);
                        }
                    } 
                    else {
                        // É A VEZ DO HUMANO (ID == 0)
                        if (peoesDisponiveis.size() == 1) {
                            int peaoAutomatico = peoesDisponiveis.get(0);
                            executarMovimentoAutomaticoHumano(peaoAutomatico, valorDado, cardEffect);
                        } else {
                            System.out.println("[Status Label] Escolha qual peão deseja mover.");
                            this.pawnControlManager.preparePendingMovement(valorDado, cardEffect);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("[GameManager] Erro de conversão: " + cardValue);
            }
        }
    }

    /**
     * MÉTODOS MANTIDO APENAS PARA O JOGADOR HUMANO (0)
     */
    private void executarMovimentoAutomaticoHumano(int peaoIndex, int valorDado, String cardEffect) {
        this.pawnControlManager.preparePendingMovement(valorDado, cardEffect);
        this.pawnControlManager.onReferencePawnHoverEntered(peaoIndex);
        this.pawnControlManager.onBoardPawnHoverEntered(peaoIndex);
        
        showMovementPreview(peaoIndex, valorDado, cardEffect);
        
        Timer atrasoDramatico = new Timer(1500, e -> {
            pawnControlManager.onReferencePawnHoverExited(peaoIndex);
            pawnControlManager.onBoardPawnHoverExit(peaoIndex);
            
            pawnControlManager.onReferencePawnClicked(peaoIndex); 
            pawnControlManager.onReferencePawnClicked(peaoIndex); 
        });
        atrasoDramatico.setRepeats(false); 
        atrasoDramatico.start();
    }

    private java.util.List<Integer> updatePlayablePawns(int cardValue, int activePlayerId) {
        java.util.List<Integer> peoesValidos = new java.util.ArrayList<>();
        
        for (int i = 0; i < 4; i++) {
            PlayerPawn pawn = boardScreen.getPlayerPawn(activePlayerId, i);
            if (pawn == null) continue;
            
            String currentState = "NORMAL";
            if (activePlayerId == 0) {
                currentState = this.pawnControlManager.getPawnState(i);
            }
            
            if ("DOURADO".equalsIgnoreCase(currentState)) {
                continue; 
            }
            
            int pos = pawn.getPawnCurrentPos();
            
            if (pos < 4) {
                if (Math.abs(cardValue) == 1 || Math.abs(cardValue) == 6) {
                    if (activePlayerId == 0) this.pawnControlManager.updatePawnVisualState(i, "NORMAL");
                    peoesValidos.add(i);
                } else {
                    if (activePlayerId == 0) this.pawnControlManager.updatePawnVisualState(i, "DESABILITADO");
                }
            } else {
                if (activePlayerId == 0) this.pawnControlManager.updatePawnVisualState(i, "NORMAL");
                peoesValidos.add(i);
            }
        }
        return peoesValidos;
    }
    
    private void pawnMovement(PlayerPawn playerPawn, final int pawnIndex, int fromWhere, int toWhere, Point[] mapaCasas, boolean ganhouTurnoExtra, boolean baseExit) {
        java.util.List<Point> pawnPathList = new java.util.ArrayList<>(); 
        
        if (baseExit) {
            pawnPathList.add(mapaCasas[fromWhere]); 
            for (int i = 4; i <= toWhere; i++) {
                pawnPathList.add(mapaCasas[i]);
            }
        } else {
            for (int i = fromWhere; i <= toWhere; i++) {
                pawnPathList.add(mapaCasas[i]); 
            }
        }

        final int[] STEP_INDEX = {0}; 
        final double[] VISUAL_POS_X = {playerPawn.getX()}; 
        final double[] VISUAL_POS_Y = {playerPawn.getY()};
        final int SPEED = 8; 

        playerPawn.setMoving(true);
        playerPawn.stopBoardPawnShake();

        timerAnimation = new Timer(15, e -> {
            if (STEP_INDEX[0] >= pawnPathList.size() - 1) {
                timerAnimation.stop();
                if (boardScreen != null) boardScreen.clearPreview();
                verificarCondicoesFinais(playerPawn, pawnIndex, toWhere, ganhouTurnoExtra);
                return;
            }

            Point intermediateSteps = pawnPathList.get(STEP_INDEX[0] + 1); 
            double dx = intermediateSteps.getX() - VISUAL_POS_X[0];
            double dy = intermediateSteps.getY() - VISUAL_POS_Y[0];
            double remainingDistance = Math.sqrt(dx * dx + dy * dy);

            if (remainingDistance <= SPEED) {
                VISUAL_POS_X[0] = intermediateSteps.getX();
                VISUAL_POS_Y[0] = intermediateSteps.getY();
                playerPawn.setPawnVisualCoordinates(intermediateSteps);
                playerPawn.setMoving(false);
                
                if (boardScreen != null) boardScreen.consumePreviewDot();
                boardScreen.repaint();
                STEP_INDEX[0]++;  
            } else {
                VISUAL_POS_X[0] += (dx / remainingDistance) * SPEED; 
                VISUAL_POS_Y[0] += (dy / remainingDistance) * SPEED;
                playerPawn.setPawnVisualCoordinates(new Point((int) VISUAL_POS_X[0], (int) VISUAL_POS_Y[0]));
            }
        });

        timerAnimation.start();
    }

    public boolean moveChosenPawn(int pawnIndex, int cardValue, String cardEffect) {
        int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;
        
        PlayerPawn chosenPawn = boardScreen.getPlayerPawn(activePlayerId, pawnIndex);
        if (chosenPawn == null) return false;

        // BÚSSOLA CORRIGIDA
        Point[] pawnPath = boardScreen.getCaminhoCasas(activePlayerId);
        int pawnActualPosition = chosenPawn.getPawnCurrentPos();
        
        boolean isBackwards = "VOLTAR".equalsIgnoreCase(cardEffect) || "RETRÓGRADO".equalsIgnoreCase(cardEffect);
        if (isBackwards && cardValue > 0) cardValue = -cardValue; 
        
        boolean ganhouTurnoExtra = (Math.abs(cardValue) == 6);
        boolean exitBase = false;

        if (pawnActualPosition < 4) {
            if (Math.abs(cardValue) == 1 || Math.abs(cardValue) == 6) {
                int pawnStarterPath = 4; 
                chosenPawn.setPawnCurrentPos(pawnStarterPath);
                exitBase = true; 
                pawnMovement(chosenPawn, pawnIndex, pawnActualPosition, pawnStarterPath, pawnPath, ganhouTurnoExtra, exitBase);
                return true; 
            } else {
                if (activePlayerId == 0) {
                    JOptionPane.showMessageDialog(boardScreen, 
                        "Este peão específico precisa de um 1 ou 6 para sair da base!", 
                        "Movimento Inválido", JOptionPane.WARNING_MESSAGE);
                }
                return false; 
            }
        }

        int pawnStarterPath = pawnActualPosition + cardValue; 
        if (pawnStarterPath >= pawnPath.length) pawnStarterPath = pawnPath.length - 1;
        if (pawnStarterPath < 4) pawnStarterPath = 4;

        chosenPawn.setPawnCurrentPos(pawnStarterPath);
        pawnMovement(chosenPawn, pawnIndex, pawnActualPosition, pawnStarterPath, pawnPath, ganhouTurnoExtra, exitBase);
        return true; 
    }

    public void showMovementPreview(int pawnIndex, int cardValue, String cardEffect) {
        int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;
        
        PlayerPawn chosenPawn = boardScreen.getPlayerPawn(activePlayerId, pawnIndex);
        if (chosenPawn == null) return;

        // A PREVISÃO VISUAL (Fantasma) É APENAS PARA O HUMANO (Caminho 0)
        Point[] pawnPath = boardScreen.getCaminhoCasas(0);
        int pawnActualPosition = chosenPawn.getPawnCurrentPos();

        boolean isBackwards = "VOLTAR".equalsIgnoreCase(cardEffect) || "RETRÓGRADO".equalsIgnoreCase(cardEffect);
        int localCardValue = cardValue;
        if (isBackwards && localCardValue > 0) localCardValue = -localCardValue;

        int destIndex = pawnActualPosition + localCardValue;
        boolean exitBase = false;

        if (pawnActualPosition < 4) {
            if (Math.abs(localCardValue) == 1 || Math.abs(localCardValue) == 6) {
                destIndex = 4;
                exitBase = true;
            } else {
                boardScreen.clearPreview();
                return;
            }
        }

        if (destIndex >= pawnPath.length) destIndex = pawnPath.length - 1;
        if (destIndex < 4 && !exitBase) destIndex = 4;

        java.util.List<Point> previewPathList = new java.util.ArrayList<>();
        if (exitBase) {
            previewPathList.add(pawnPath[4]);
        } else {
            if (pawnActualPosition < destIndex) {
                for (int i = pawnActualPosition + 1; i <= destIndex; i++) previewPathList.add(pawnPath[i]);
            } else if (pawnActualPosition > destIndex) {
                for (int i = pawnActualPosition - 1; i >= destIndex; i--) previewPathList.add(pawnPath[i]);
            }
        }

        boardScreen.setPreviewData(pawnIndex, destIndex, previewPathList);
    }

   private void verificarCondicoesFinais(PlayerPawn peao, int pawnIndex, int posicaoAlcancada, boolean ganhouTurnoExtra) {
        boolean passarVez = true;
        int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;

        if (posicaoAlcancada >= boardScreen.getCaminhoCasas(activePlayerId).length - 1) {
            if (this.pawnControlManager != null && activePlayerId == 0) {
                this.pawnControlManager.updatePawnVisualState(pawnIndex, "DOURADO");
            }
            peao.updatePawnVisual("/assets/peaoAmarelo_90x90.png");

            JOptionPane.showMessageDialog(boardScreen, 
                "🏆 INCRÍVEL! O peão " + (pawnIndex + 1) + " de " + peao.getPlayerName() + " alcançou o Centro!", 
                "Peão Vitorioso", JOptionPane.INFORMATION_MESSAGE);
        }

        if (ganhouTurnoExtra) {
            if (activePlayerId == 0) {
                JOptionPane.showMessageDialog(boardScreen, 
                    "Incrível! Você tirou um '6'!\nJogue novamente.", 
                    "Turno Bônus", JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("[GameManager] CPU " + activePlayerId + " tirou um '6' e ganhou um turno extra!");
            }
            passarVez = false; 
        }

        if (passarVez && this.turnManager != null) {
            // Movimento normal: passa para o próximo jogador
            this.turnManager.nextTurn();
            
        } else if (!passarVez && this.turnManager != null && activePlayerId > 0) {
            // === A MÁGICA ACONTECE AQUI ===
            // Turno extra da CPU: Força o TurnManager a disparar o Timer da IA novamente
            this.turnManager.processExtraTurn();
        }
    }
}
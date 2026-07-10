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
    private CPUIManager cpuIManager;
    
    // Rastreador de 6s consecutivos para cada um dos 4 jogadores (0 a 3)
    private int[] consecutiveSixesCounters = new int[4]; 
    
    // Flag para congelar o jogo quando alguém vencer
    private boolean jogoFinalizado = false;

    // =========================================================================
    // CONFIGURAÇÕES DINÂMICAS 
    // =========================================================================
    private boolean usarRegraTorre = true; 
    private boolean usarZonasSeguras = true; 
    private boolean usarReboteCentro = true; 

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
    
    public void setCPUIManager(CPUIManager cpuIManager) {
        this.cpuIManager = cpuIManager;
    }

    public void cardResultVerification(boolean correct, String cardValue, String cardEffect) {
        if (boardScreen == null || jogoFinalizado) return;
        if (timerAnimation != null && timerAnimation.isRunning()) return;

        int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;
 
        PlayerPawn p1 = boardScreen.getPlayerPawn(activePlayerId, 0);
        Point[] mapaCasas = boardScreen.getCaminhoCasas(activePlayerId);

        if (p1 == null || mapaCasas == null) return;

        if (!correct) {
            System.out.println("[GameManager] Resposta incorreta. Passando o turno.");
            consecutiveSixesCounters[activePlayerId] = 0; 
            if (this.turnManager != null) {
                this.turnManager.nextTurn();
            }
            return;
        }

       if ("AVANÇAR".equalsIgnoreCase(cardEffect) || "VOLTAR".equalsIgnoreCase(cardEffect) || "RETROCEDER".equalsIgnoreCase(cardEffect)) {
            try {
                String cardValueTreated = cardValue.trim();
                if (cardValueTreated.contains("/")) {
                    cardValueTreated = cardValueTreated.split("/")[0].trim();
                }

                int valorDado = Integer.parseInt(cardValueTreated);

                // PENALIDADE DOS TRÊS 6 CONSECUTIVOS
                if (Math.abs(valorDado) == 6) {
                    consecutiveSixesCounters[activePlayerId]++;
                    if (consecutiveSixesCounters[activePlayerId] == 3) {
                        consecutiveSixesCounters[activePlayerId] = 0; 
                        
                        if (activePlayerId == 0) {
                            JOptionPane.showMessageDialog(boardScreen, 
                                "🚨 PENALIDADE!\nVocê tirou o número '6' três vezes seguidas.\nSua jogada foi cancelada e você perdeu a vez!", 
                                "Três 6s Consecutivos", JOptionPane.ERROR_MESSAGE);
                        } else {
                            System.out.println("[GameManager] CPU " + activePlayerId + " tirou o terceiro '6' seguido e perdeu a vez.");
                        }
                        
                        if (this.turnManager != null) {
                            this.turnManager.nextTurn();
                        }
                        return; 
                    }
                } else {
                    consecutiveSixesCounters[activePlayerId] = 0; 
                }

                if (this.pawnControlManager != null) {
                    
                    java.util.List<Integer> peoesDisponiveis = updatePlayablePawns(valorDado, cardEffect, activePlayerId);
                    
                    if (peoesDisponiveis.isEmpty()) {
                        System.out.println("[Status Label] Nenhum peão do Jogador " + activePlayerId + " pode se mover.");
                        consecutiveSixesCounters[activePlayerId] = 0; 
                        if (activePlayerId == 0) {
                            JOptionPane.showMessageDialog(boardScreen, 
                                "Nenhum peão pode se mover. Caminho totalmente bloqueado!", 
                                "Turno Sem Movimentos", JOptionPane.WARNING_MESSAGE);
                        }
                        if (this.turnManager != null) this.turnManager.nextTurn();
                        return;
                    }

                    if (activePlayerId > 0) {
                        if (this.cpuIManager != null) {
                            this.cpuIManager.iniciarJogadaCPU(activePlayerId, peoesDisponiveis, valorDado, cardEffect);
                        }
                    } 
                    else {
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

    private java.util.List<Integer> updatePlayablePawns(int cardValue, String cardEffect, int activePlayerId) {
        java.util.List<Integer> peoesValidos = new java.util.ArrayList<>();
        Point[] pawnPath = boardScreen.getCaminhoCasas(activePlayerId);
        if (pawnPath == null || jogoFinalizado) return peoesValidos;

        boolean isBackwards = "VOLTAR".equalsIgnoreCase(cardEffect) || "RETRÓGRADO".equalsIgnoreCase(cardEffect);
        int localCardValue = isBackwards ? -cardValue : cardValue;
        
        for (int i = 0; i < 4; i++) {
            PlayerPawn pawn = boardScreen.getPlayerPawn(activePlayerId, i);
            if (pawn == null) continue;
            
            int pos = pawn.getPawnCurrentPos();
            
            if (pos >= pawnPath.length - 1) {
                if (activePlayerId == 0) this.pawnControlManager.updatePawnVisualState(i, "DESABILITADO");
                continue;
            }
            
            String currentState = "NORMAL";
            if (activePlayerId == 0) {
                currentState = this.pawnControlManager.getPawnState(i);
            }
            if ("DOURADO".equalsIgnoreCase(currentState)) {
                continue; 
            }
            
            int destIndex = -1;
            boolean podeMoverLinguagemOriginal = false;
            
            if (pos < 4) {
                if (Math.abs(cardValue) == 1 || Math.abs(cardValue) == 6) {
                    destIndex = 4;
                    podeMoverLinguagemOriginal = true;
                }
            } else {
                destIndex = pos + localCardValue;
                int maxIndex = pawnPath.length - 1;
                
                if (usarReboteCentro && destIndex > maxIndex) {
                    destIndex = maxIndex - (destIndex - maxIndex);
                }

                if (destIndex >= pawnPath.length) destIndex = maxIndex;
                if (destIndex < 4) destIndex = 4;
                podeMoverLinguagemOriginal = true;
            }
            
            if (podeMoverLinguagemOriginal && destIndex != -1) {
                Point pontoDestinoFisico = pawnPath[destIndex];
                
                if (usarRegraTorre && isTorreInimigaEm(pontoDestinoFisico, activePlayerId)) {
                    if (activePlayerId == 0) this.pawnControlManager.updatePawnVisualState(i, "DESABILITADO");
                    continue; 
                }

                if (activePlayerId == 0) this.pawnControlManager.updatePawnVisualState(i, "NORMAL");
                peoesValidos.add(i);
            } else {
                if (activePlayerId == 0) this.pawnControlManager.updatePawnVisualState(i, "DESABILITADO");
            }
        }
        return peoesValidos;
    }

    private boolean isZonaSegura(Point ponto) {
        if (!usarZonasSeguras || ponto == null) return false;
        
        for(int p = 0; p < 4; p++) {
            Point[] camino = boardScreen.getCaminhoCasas(p);
            if (camino != null && camino.length > 12) {
                if (ponto.equals(camino[4]) || ponto.equals(camino[12])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTorreInimigaEm(Point pontoDestino, int activePlayerId) {
        if (pontoDestino == null) return false;
        if (isZonaSegura(pontoDestino)) return false;

        for (int p = 0; p < 4; p++) {
            if (p == activePlayerId) continue;

            int peoesNaMesmaCasa = 0;
            Point[] caminhoInimigo = boardScreen.getCaminhoCasas(p);
            if (caminhoInimigo == null) continue;

            for (int e = 0; e < 4; e++) {
                PlayerPawn peaoInimigo = boardScreen.getPlayerPawn(p, e);
                if (peaoInimigo == null) continue;

                int posInimigo = peaoInimigo.getPawnCurrentPos();
                
                if (posInimigo < 4 || posInimigo >= caminhoInimigo.length - 1) {
                    continue; 
                }

                Point pontoInimigoFisico = caminhoInimigo[posInimigo];
                
                if (pontoDestino.x == pontoInimigoFisico.x && pontoDestino.y == pontoInimigoFisico.y) {
                    peoesNaMesmaCasa++;
                }
            }

            if (peoesNaMesmaCasa >= 2) {
                return true;
            }
        }
        return false;
    }
    
    private void pawnMovement(PlayerPawn playerPawn, final int pawnIndex, int fromWhere, int toWhere, Point[] mapaCasas, boolean ganhouTurnoExtra, boolean baseExit, boolean houveRebote) {
        java.util.List<Point> pawnPathList = new java.util.ArrayList<>(); 
        int maxIndex = mapaCasas.length - 1;
        
        if (baseExit) {
            pawnPathList.add(mapaCasas[fromWhere]); 
            for (int i = 4; i <= toWhere; i++) {
                pawnPathList.add(mapaCasas[i]);
            }
        } else if (houveRebote) {
            for (int i = fromWhere; i <= maxIndex; i++) {
                pawnPathList.add(mapaCasas[i]);
            }
            for (int i = maxIndex - 1; i >= toWhere; i--) {
                pawnPathList.add(mapaCasas[i]);
            }
        } else {
            if (fromWhere <= toWhere) {
                for (int i = fromWhere; i <= toWhere; i++) {
                    pawnPathList.add(mapaCasas[i]); 
                }
            } else {
                for (int i = fromWhere; i >= toWhere; i--) {
                    pawnPathList.add(mapaCasas[i]);
                }
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
                
                int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;
                verificarCaptura(activePlayerId, toWhere);
                
                verificarConditionsFinais(playerPawn, pawnIndex, toWhere, ganhouTurnoExtra);
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
        if (jogoFinalizado) return false;
        
        int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;
        
        PlayerPawn chosenPawn = boardScreen.getPlayerPawn(activePlayerId, pawnIndex);
        if (chosenPawn == null) return false;

        Point[] pawnPath = boardScreen.getCaminhoCasas(activePlayerId);
        int pawnActualPosition = chosenPawn.getPawnCurrentPos();
        
        if (pawnActualPosition >= pawnPath.length - 1) return false;

        boolean isBackwards = "VOLTAR".equalsIgnoreCase(cardEffect) || "RETRÓGRADO".equalsIgnoreCase(cardEffect);
        if (isBackwards && cardValue > 0) cardValue = -cardValue; 
        
        boolean ganhouTurnoExtra = (Math.abs(cardValue) == 6);
        boolean exitBase = false;

        if (pawnActualPosition < 4) {
            if (Math.abs(cardValue) == 1 || Math.abs(cardValue) == 6) {
                int pawnStarterPath = 4; 
                chosenPawn.setPawnCurrentPos(pawnStarterPath);
                exitBase = true; 
                pawnMovement(chosenPawn, pawnIndex, pawnActualPosition, pawnStarterPath, pawnPath, ganhouTurnoExtra, exitBase, false);
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
        int maxIndex = pawnPath.length - 1;
        boolean houveRebote = false;

        if (usarReboteCentro && pawnStarterPath > maxIndex) {
            houveRebote = true;
            pawnStarterPath = maxIndex - (pawnStarterPath - maxIndex);
        }

        if (pawnStarterPath >= pawnPath.length) pawnStarterPath = maxIndex;
        if (pawnStarterPath < 4) pawnStarterPath = 4;

        chosenPawn.setPawnCurrentPos(pawnStarterPath);
        pawnMovement(chosenPawn, pawnIndex, pawnActualPosition, pawnStarterPath, pawnPath, ganhouTurnoExtra, exitBase, houveRebote);
        return true; 
    }

    public void showMovementPreview(int pawnIndex, int cardValue, String cardEffect) {
        if (jogoFinalizado) return;
        
        int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;
        
        PlayerPawn chosenPawn = boardScreen.getPlayerPawn(activePlayerId, pawnIndex);
        if (chosenPawn == null) return;

        Point[] pawnPath = boardScreen.getCaminhoCasas(0);
        int pawnActualPosition = chosenPawn.getPawnCurrentPos();
        
        if (pawnActualPosition >= pawnPath.length - 1) {
            boardScreen.clearPreview();
            return;
        }

        boolean isBackwards = "VOLTAR".equalsIgnoreCase(cardEffect) || "RETRÓGRADO".equalsIgnoreCase(cardEffect);
        int localCardValue = cardValue;
        if (isBackwards && localCardValue > 0) localCardValue = -localCardValue;

        int destIndex = pawnActualPosition + localCardValue;
        int maxIndex = pawnPath.length - 1;
        boolean exitBase = false;
        boolean houveRebote = false;

        if (pawnActualPosition < 4) {
            if (Math.abs(localCardValue) == 1 || Math.abs(localCardValue) == 6) {
                destIndex = 4;
                exitBase = true;
            } else {
                boardScreen.clearPreview();
                return;
            }
        }

        if (usarReboteCentro && destIndex > maxIndex) {
            houveRebote = true;
            destIndex = maxIndex - (destIndex - maxIndex);
        }

        if (destIndex >= pawnPath.length) destIndex = maxIndex;
        if (destIndex < 4 && !exitBase) destIndex = 4;

        java.util.List<Point> previewPathList = new java.util.ArrayList<>();
        if (exitBase) {
            previewPathList.add(pawnPath[4]);
        } else if (houveRebote) {
            for (int i = pawnActualPosition + 1; i <= maxIndex; i++) previewPathList.add(pawnPath[i]);
            for (int i = maxIndex - 1; i >= destIndex; i--) previewPathList.add(pawnPath[i]);
        } else {
            if (pawnActualPosition < destIndex) {
                for (int i = pawnActualPosition + 1; i <= destIndex; i++) previewPathList.add(pawnPath[i]);
            } else if (pawnActualPosition > destIndex) {
                for (int i = pawnActualPosition - 1; i >= destIndex; i--) previewPathList.add(pawnPath[i]);
            }
        }

        boardScreen.setPreviewData(pawnIndex, destIndex, previewPathList);
    }

    private void verificarConditionsFinais(PlayerPawn peao, int pawnIndex, int posicaoAlcancada, boolean ganhouTurnoExtra) {
        verificarCondicoesFinais(peao, pawnIndex, posicaoAlcancada, ganhouTurnoExtra);
    }

    private void verificarCondicoesFinais(PlayerPawn peao, int pawnIndex, int posicaoAlcancada, boolean ganhouTurnoExtra) {
        if (jogoFinalizado) return;
        
        boolean passarVez = true;
        int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;
        Point[] caminhoJogador = boardScreen.getCaminhoCasas(activePlayerId);

        if (posicaoAlcancada >= caminhoJogador.length - 1) {
            if (this.pawnControlManager != null && activePlayerId == 0) {
                this.pawnControlManager.updatePawnVisualState(pawnIndex, "DOURADO");
            }
            peao.updatePawnVisual("/assets/peaoAmarelo_90x90.png");

            JOptionPane.showMessageDialog(boardScreen, 
                "🏆 INCRÍVEL! O peão " + (pawnIndex + 1) + " de " + peao.getPlayerName() + " alcançou o Centro!", 
                "Peão Vitorioso", JOptionPane.INFORMATION_MESSAGE);
                
            // === NOVO: VERIFICAÇÃO DA CONDIÇÃO DE VITÓRIA ===
            int peoesNoCentro = 0;
            for (int i = 0; i < 4; i++) {
                PlayerPawn p = boardScreen.getPlayerPawn(activePlayerId, i);
                if (p != null && p.getPawnCurrentPos() >= caminhoJogador.length - 1) {
                    peoesNoCentro++;
                }
            }
            
            // Se todos os 4 peões deste jogador chegaram ao centro, temos um vencedor!
            if (peoesNoCentro == 4) {
                jogoFinalizado = true; // Bloqueia novas jogadas
                
                String mensagemVitoria = (activePlayerId == 0) ? 
                    "🎉👑 PARABÉNS! Você levou todos os 4 peões ao Centro e VENCEU O JOGO!" :
                    "🤖 FIM DE JOGO! A " + peao.getPlayerName() + " levou todos os peões ao Centro e venceu a partida.";
                
                JOptionPane.showMessageDialog(boardScreen, mensagemVitoria, "👑 Temos um Campeão!", JOptionPane.INFORMATION_MESSAGE);
                return; // Encerra o método e não passa o turno
            }
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
            consecutiveSixesCounters[activePlayerId] = 0; 
            this.turnManager.nextTurn();
        } else if (!passarVez && this.turnManager != null && activePlayerId > 0) {
            this.turnManager.processExtraTurn();
        }
    }

    private void verificarCapture(int activePlayerId, int destIndex) {
        verificarCaptura(activePlayerId, destIndex);
    }

    private void verificarCaptura(int activePlayerId, int destIndex) {
        if (jogoFinalizado) return;
        
        Point destPoint = boardScreen.getCaminhoCasas(activePlayerId)[destIndex];

        if (isZonaSegura(destPoint)) {
            System.out.println("[GameManager] Peão pousou em Zona Segura! Captura desativada.");
            return;
        }

        for (int p = 0; p < 4; p++) {
            if (p == activePlayerId) continue;

            for (int i = 0; i < 4; i++) {
                PlayerPawn enemyPawn = boardScreen.getPlayerPawn(p, i);
                if (enemyPawn == null) continue;

                int enemyPos = enemyPawn.getPawnCurrentPos();
                
                if (enemyPos < 4 || enemyPos >= boardScreen.getCaminhoCasas(p).length - 1) {
                    continue; 
                }

                Point enemyPoint = boardScreen.getCaminhoCasas(p)[enemyPos];

                if (destPoint.x == enemyPoint.x && destPoint.y == enemyPoint.y) {
                    
                    System.out.println("[GameManager] CAPTURA! O Jogador " + activePlayerId + " comeu o peão " + i + " do Jogador " + p);
                    
                    String atacante = (activePlayerId == 0) ? "Você" : "A CPU " + activePlayerId;
                    String vitima = (p == 0) ? "seu peão" : "peão da CPU " + p;
                    JOptionPane.showMessageDialog(boardScreen, 
                        "⚔️ ATAQUE! " + atacante + " capturou o " + vitima + "!\nEle voltará para a base.", 
                        "Peão Capturado", JOptionPane.WARNING_MESSAGE);
                    
                    enemyPawn.setPawnCurrentPos(i);
                    Point basePoint = boardScreen.getCaminhoCasas(p)[i];
                    enemyPawn.setPawnVisualCoordinates(basePoint);
                    boardScreen.repaint();
                }
            }
        }
    }

    
}
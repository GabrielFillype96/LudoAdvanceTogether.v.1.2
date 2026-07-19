// Classe responsável por gerenciar as regras de movimentação dos peões de acordo com as cartas

package control;

import gui.windows.BoardScreen;
import gui.components.GameStatusBar;
import gui.components.PlayerPawn;

import java.awt.Color;
import java.awt.Point;
import javax.swing.Timer;

public class GameManager {
    
    private BoardScreen boardScreen;
    private Timer timerAnimation; 
    private PawnControlManager pawnControlManager;
    private TurnManager turnManager;
    private CPUIManager cpuIManager;
    private GameStatusBar gameStatusBar; // Variável da barra de status

    // =========================================================================
    // CONFIGURAÇÕES DE TIMERS E DELAYS (Altere aqui o ritmo do seu jogo!)
    // =========================================================================
    private static final int DELAY_ERRO_E_AVISOS = 3000;     // Tempo para ler erros/penalidades (era 1500ms)
    private static final int DELAY_ACAO_TEXTO = 2500;        // Tempo para ler o que o jogador/CPU vai fazer (era 1500ms)
    private static final int DELAY_MOVIMENTO_AUTO = 2000;     // Atraso antes do peão humano andar sozinho (era 1500ms)
    private static final int DELAY_FINAL_TURNO = 3500;       // A última mensagem na tela antes de mudar o turno (era 2000ms)

    // Definição de cores dinâmicas para o sistema de status
    private static final Color COLOR_INFO = Color.WHITE;
    private static final Color COLOR_SUCCESS = new Color(46, 204, 113);  // Verde
    private static final Color COLOR_WARNING = new Color(241, 196, 15);  // Amarelo
    private static final Color COLOR_ERROR = new Color(231, 76, 60);     // Vermelho
    private static final Color COLOR_ACTION = new Color(230, 126, 34);    // Laranja
    
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

    public void setGameStatusBar(GameStatusBar gameStatusBar) {
        this.gameStatusBar = gameStatusBar;
    }

    public void emitirStatus(String mensagem, Color cor) {
        if (this.gameStatusBar != null) {
            this.gameStatusBar.updateStatus(mensagem, cor);
        }
    }

    public void emitirStatusSorteio(String mensagem, Color cor) {
        if (this.gameStatusBar != null) {
            this.gameStatusBar.updateSlotMachineStatus(mensagem, cor);
        }
    }

    public void setPawnControlManager(PawnControlManager pawnControlManager) {
        this.pawnControlManager = pawnControlManager;
    }
    
    public void setCPUIManager(CPUIManager cpuIManager) {
        this.cpuIManager = cpuIManager;
    }

    // Retorna o nome real do jogador ou da CPU baseado no ID (0 a 3)
    public String getPlayerNameById(int playerId) {
        if (playerId == 0) return "Você";
        
        if (this.boardScreen != null) {
            PlayerPawn pawn = this.boardScreen.getPlayerPawn(playerId, 0);
            if (pawn != null && pawn.getPlayerName() != null && !pawn.getPlayerName().trim().isEmpty()) {
                return pawn.getPlayerName();
            }
        }
        return "CPU " + playerId; 
    }

    // ASSINATURA ANTIGA: Mantida para compatibilidade com as cartas de perguntas comuns
    public void cardResultVerification(boolean correct, String cardValue, String cardEffect) {
        cardResultVerification(correct, cardValue, cardEffect, "PERGUNTA");
    }

    // Processa os textos e regras especiais de cada tipo de carta (Humano e CPU)
    public void cardResultVerification(boolean correct, String cardValue, String cardEffect, String cardType) {
        if (boardScreen == null || jogoFinalizado) return;
        if (timerAnimation != null && timerAnimation.isRunning()) return;

        int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;
        String nomeJogador = getPlayerNameById(activePlayerId);
 
        PlayerPawn p1 = boardScreen.getPlayerPawn(activePlayerId, 0);
        Point[] mapaCasas = boardScreen.getCaminhoCasas(activePlayerId);

        if (p1 == null || mapaCasas == null) return;

        // SE FOR UMA CARTA DE PEGADINHA (Humano)
        if ("PEGADINHA".equalsIgnoreCase(cardType) && activePlayerId == 0) {
            emitirStatus("🃏 Seu espertinho! Escolha um jogador para sacanear.", COLOR_ACTION);
            return;
        }

        // =========================================================================
        // TRACK DE ERRO (Humano e CPU)
        // =========================================================================
        if (!correct) {
            System.out.println("[GameManager] Resposta incorreta. Passando o turno.");
            consecutiveSixesCounters[activePlayerId] = 0; 
            
            if (activePlayerId == 0) {
                emitirStatus("❌ Resposta incorreta! Você perdeu a vez.", COLOR_ERROR);
            } else {
                emitirStatus("❌ " + nomeJogador + " errou a resposta e perdeu a vez!", COLOR_ERROR);
            }

            // Delay configurado para leitura do erro
            Timer delayErro = new Timer(DELAY_ERRO_E_AVISOS, eErro -> {
                if (this.turnManager != null) this.turnManager.nextTurn();
            });
            delayErro.setRepeats(false);
            delayErro.start();
            return;
        }

        // SE FOR CARTA DE SUCESSO OU EFEITO IMEDIATO (AVANÇAR / VOLTAR)
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
                            emitirStatus("🚨 PENALIDADE! Três 6s seguidos. Você perdeu a vez!", COLOR_ERROR);
                        } else {
                            emitirStatus("🚨 PENALIDADE! " + nomeJogador + " tirou três 6s seguidos e perdeu a vez!", COLOR_ERROR);
                        }
                        Timer delayPenalidade = new Timer(DELAY_ERRO_E_AVISOS, ePen -> {
                            if (this.turnManager != null) this.turnManager.nextTurn();
                        });
                        delayPenalidade.setRepeats(false);
                        delayPenalidade.start();
                        return; 
                    }
                } else {
                    consecutiveSixesCounters[activePlayerId] = 0; 
                }

                if (this.pawnControlManager != null) {
                    
                    // REGRA ESPECIAL: Carta de Azar do jogador Humano
                    if (activePlayerId == 0 && "AZAR".equalsIgnoreCase(cardType)) {
                        int peaoAzarado = getFurthestPawnIndex(activePlayerId);
                        
                        if (peaoAzarado == -1) {
                            emitirStatus("🍀 Que sorte! Você não tem peões no tabuleiro para retroceder.", COLOR_SUCCESS);
                            Timer delaySorte = new Timer(DELAY_ACAO_TEXTO, eS -> {
                                if (this.turnManager != null) this.turnManager.nextTurn();
                            });
                            delaySorte.setRepeats(false);
                            delaySorte.start();
                            return;
                        }

                        emitirStatus("💀 Voltando o peão azarado " + (peaoAzarado + 1) + " " + Math.abs(valorDado) + " casas!", COLOR_ERROR);
                        
                        Timer delayAzar = new Timer(DELAY_ACAO_TEXTO, eAzar -> {
                            moveChosenPawn(peaoAzarado, valorDado, cardEffect);
                        });
                        delayAzar.setRepeats(false);
                        delayAzar.start();
                        return;
                    }

                    java.util.List<Integer> peoesDisponiveis = updatePlayablePawns(valorDado, cardEffect, activePlayerId);
                    
                    // =========================================================================
                    // TRACK DE EVENTOS DA CPU (Mensagens das Cartas e Respostas)
                    // =========================================================================
                    if (activePlayerId > 0) {
                        
                        // 1. Identifica o tipo de carta e define a mensagem ideal no status
                        if ("SORTE".equalsIgnoreCase(cardType)) {
                            emitirStatus("🍀 " + nomeJogador + " está com sorte e vai avançar " + valorDado + " casas!", COLOR_SUCCESS);
                        } 
                        else if ("AZAR".equalsIgnoreCase(cardType)) {
                            emitirStatus("💀 " + nomeJogador + " deu azar e terá que retroceder " + Math.abs(valorDado) + " casas!", COLOR_ERROR);
                        } 
                        else if ("PEGADINHA".equalsIgnoreCase(cardType)) {
                            emitirStatus("🃏 " + nomeJogador + " vai usar uma Pegadinha contra outro jogador!", COLOR_ACTION);
                        } 
                        else {
                            String acaoVerbo = "AVANÇAR".equalsIgnoreCase(cardEffect) ? "avançar" : "retroceder";
                            emitirStatus("🧠 " + nomeJogador + " acertou a resposta e vai " + acaoVerbo + " " + valorDado + " casas!", COLOR_SUCCESS);
                        }

                        // Delay configurado para ler o evento da CPU antes dela agir
                        Timer delayAcaoCPU = new Timer(DELAY_ACAO_TEXTO, eAcao -> {
                            
                            if ("PEGADINHA".equalsIgnoreCase(cardType)) {
                                System.out.println("[GameManager] CPU executando Pegadinha (Lógica a ser implementada)...");
                                if (this.turnManager != null) this.turnManager.nextTurn();
                                return;
                            }
                            
                            if ("AZAR".equalsIgnoreCase(cardType)) {
                                int peaoAzarado = getFurthestPawnIndex(activePlayerId);
                                if (peaoAzarado == -1) {
                                    emitirStatus("🤖 " + nomeJogador + " não tem peões no tabuleiro para retroceder.", COLOR_INFO);
                                    Timer delayTurno = new Timer(DELAY_ERRO_E_AVISOS, eT -> { if (this.turnManager != null) this.turnManager.nextTurn(); });
                                    delayTurno.setRepeats(false);
                                    delayTurno.start();
                                    return;
                                }
                                moveChosenPawn(peaoAzarado, valorDado, cardEffect);
                                return;
                            }

                            if (peoesDisponiveis.isEmpty()) {
                                emitirStatus("🤖 " + nomeJogador + " não tem movimentos válidos.", COLOR_INFO);
                                Timer delayTurno = new Timer(DELAY_ERRO_E_AVISOS, eTurno -> {
                                    if (this.turnManager != null) this.turnManager.nextTurn();
                                });
                                delayTurno.setRepeats(false);
                                delayTurno.start();
                                return;
                            }
                            
                            if (this.cpuIManager != null) {
                                this.cpuIManager.iniciarJogadaCPU(activePlayerId, peoesDisponiveis, valorDado, cardEffect);
                            }
                        });
                        delayAcaoCPU.setRepeats(false);
                        delayAcaoCPU.start();
                    }
                    // =========================================================================
                    // TRACK DE ACERTO DO HUMANO
                    // =========================================================================
                    else {
                        boolean ehCartaSorte = "SORTE".equalsIgnoreCase(cardType);
                        
                        if (ehCartaSorte) {
                            emitirStatus("🍀 Parece que alguém aqui tem muita sorte!", COLOR_SUCCESS);
                        } else {
                            emitirStatus("🎉 Parabéns, você acertou a resposta!", COLOR_SUCCESS);
                        }
                        
                        Timer delayPosAcerto = new Timer(DELAY_ACAO_TEXTO, eDelay -> {
                            if (peoesDisponiveis.isEmpty()) {
                                consecutiveSixesCounters[activePlayerId] = 0; 
                                emitirStatus("⚠️ Infelizmente, não há peões disponíveis para jogar.", COLOR_WARNING);
                                Timer delayTurno = new Timer(DELAY_ERRO_E_AVISOS, eTurno -> {
                                    if (this.turnManager != null) this.turnManager.nextTurn();
                                });
                                delayTurno.setRepeats(false);
                                delayTurno.start();
                                return;
                            }
                            
                            if (peoesDisponiveis.size() == 1) {
                                int peaoAutomatico = peoesDisponiveis.get(0);
                                if (ehCartaSorte) {
                                    emitirStatus("🤖 Movendo o peão sortudo " + (peaoAutomatico + 1) + " automaticamente...", COLOR_INFO);
                                } else {
                                    emitirStatus("🤖 Apenas o peão " + (peaoAutomatico + 1) + " está disponível. Movendo automaticamente...", COLOR_INFO);
                                }
                                executarMovimentoAutomaticoHumano(peaoAutomatico, valorDado, cardEffect);
                            } 
                            else {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < peoesDisponiveis.size(); i++) {
                                    int numeroPeao = peoesDisponiveis.get(i) + 1;
                                    if (i == 0) sb.append(numeroPeao);
                                    else if (i == peoesDisponiveis.size() - 1) sb.append(" ou ").append(numeroPeao);
                                    else sb.append(", ").append(numeroPeao);
                                }
                                
                                if (ehCartaSorte) {
                                    emitirStatus("👉 Escolha o peão sortudo " + sb.toString() + " para se mover.", COLOR_INFO);
                                } else {
                                    emitirStatus("👉 Escolha o peão " + sb.toString() + " para se mover.", COLOR_INFO);
                                }
                                this.pawnControlManager.preparePendingMovement(valorDado, cardEffect);
                            }
                        });
                        delayPosAcerto.setRepeats(false);
                        delayPosAcerto.start();
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
        
        Timer atrasoDramatico = new Timer(DELAY_MOVIMENTO_AUTO, e -> {
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
        if (jogoFinalizado) return false;
        
        int activePlayerId = (this.turnManager != null) ? this.turnManager.getCurrentTurn() : 0;
        
        PlayerPawn chosenPawn = boardScreen.getPlayerPawn(activePlayerId, pawnIndex);
        if (chosenPawn == null) return false;

        Point[] pawnPath = boardScreen.getCaminhoCasas(activePlayerId);
        int pawnActualPosition = chosenPawn.getPawnCurrentPos();
        
        if (pawnActualPosition >= pawnPath.length - 1) return false;

        boolean isBackwards = "VOLTAR".equalsIgnoreCase(cardEffect) || 
                              "RETRÓGRADO".equalsIgnoreCase(cardEffect) || 
                              "RETROCEDER".equalsIgnoreCase(cardEffect);
        if (isBackwards && cardValue > 0) cardValue = -cardValue; 
        
        boolean ganhouTurnoExtra = (Math.abs(cardValue) == 6);
        boolean exitBase = false;

        if (pawnActualPosition < 4) {
            if (isBackwards) {
                System.out.println("[GameManager] Peão na base não pode retroceder.");
                return false;
            }
            if (Math.abs(cardValue) == 1 || Math.abs(cardValue) == 6) {
                int pawnStarterPath = 4; 
                chosenPawn.setPawnCurrentPos(pawnStarterPath);
                exitBase = true; 
                pawnMovement(chosenPawn, pawnIndex, pawnActualPosition, pawnStarterPath, pawnPath, ganhouTurnoExtra, exitBase, false);
                return true; 
            } else {
                if (activePlayerId == 0) {
                    emitirStatus("❌ Movimento inválido! Esse peão precisa de 1 ou 6 para sair da base.", COLOR_ERROR);
                }
                return false; 
            }
        }

        int pawnStarterPath = pawnActualPosition + cardValue; 

        if (isBackwards && pawnStarterPath < 4) {
            pawnStarterPath = 4; 
        }

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

            emitirStatus("🏆 INCRÍVEL! O peão " + (pawnIndex + 1) + " de " + peao.getPlayerName() + " chegou ao Centro!", COLOR_SUCCESS);
                
            int peoesNoCentro = 0;
            for (int i = 0; i < 4; i++) {
                PlayerPawn p = boardScreen.getPlayerPawn(activePlayerId, i);
                if (p != null && p.getPawnCurrentPos() >= caminhoJogador.length - 1) {
                    peoesNoCentro++;
                }
            }
            
            if (peoesNoCentro == 4) {
                jogoFinalizado = true; 
                
                String mensagemVitoria = (activePlayerId == 0) ? 
                    "🎉👑 PARABÉNS! Você levou todos os 4 peões ao Centro e VENCEU O JOGO!" :
                    "🤖 FIM DE JOGO! A " + peao.getPlayerName() + " venceu a partida.";

                    emitirStatus(mensagemVitoria, COLOR_SUCCESS);
                return;
            }
        }

        if (ganhouTurnoExtra) {
            if (activePlayerId == 0) {
                emitirStatus("🎲 Turno Bônus! Você tirou um '6', jogue novamente.", COLOR_SUCCESS);
            } else {
                emitirStatus("🤖 " + getPlayerNameById(activePlayerId) + " tirou um '6' e ganhou turno extra!", COLOR_INFO);
            }
            passarVez = false; 
        }

        // =========================================================================
        // ULTIMA MENSAGEM ANTES DE PASSAR O TURNO (Ajustado via Constante)
        // =========================================================================
        if (passarVez && this.turnManager != null) {
            consecutiveSixesCounters[activePlayerId] = 0; 
            
            // Aguarda o tempo estipulado para o jogador ver a última alteração/mensagem na tela
            Timer delayTransicaoTurno = new Timer(DELAY_FINAL_TURNO, eFim -> {
                this.turnManager.nextTurn();
            });
            delayTransicaoTurno.setRepeats(false);
            delayTransicaoTurno.start();
            
        } else if (!passarVez && this.turnManager != null) {
            
            // Aguarda o tempo estipulado antes de processar o Turno Bônus
            Timer delayTransicaoExtra = new Timer(DELAY_FINAL_TURNO, eExtra -> {
                if (activePlayerId > 0) {
                    this.turnManager.processExtraTurn();
                }
            });
            delayTransicaoExtra.setRepeats(false);
            delayTransicaoExtra.start();
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
                    
                    String atacante = (activePlayerId == 0) ? "Você" : getPlayerNameById(activePlayerId);
                    String vitima = (p == 0) ? "seu peão" : "peão de " + getPlayerNameById(p);
                    
                    emitirStatus("⚔️ ATAQUE! " + atacante + " capturou o " + vitima + "!", COLOR_ACTION);
                    
                    enemyPawn.setPawnCurrentPos(i);
                    Point basePoint = boardScreen.getCaminhoCasas(p)[i];
                    enemyPawn.setPawnVisualCoordinates(basePoint);
                    boardScreen.repaint();
                }
            }
        }
    }

    public int escolherMelhorPeaoParaCPU(int cpuId, java.util.List<Integer> peoesDisponiveis, int cardValue, String personality) {
        int melhorPeao = -1;
        int maiorPontuacao = -9999;

        for (int peaoIndex : peoesDisponiveis) {
            int pontuacaoAtual = 0;
            
            PlayerPawn pawn = this.boardScreen.getPlayerPawn(cpuId, peaoIndex);
            if (pawn == null) continue;

            int posAtual = pawn.getPawnCurrentPos();
            int posDestino = posAtual + cardValue; 
            
            if (posDestino >= this.boardScreen.getCaminhoCasas(cpuId).length) {
                if (this.usarReboteCentro) {
                    pontuacaoAtual -= 500; 
                }
            } else {
                Point destPoint = this.boardScreen.getCaminhoCasas(cpuId)[posDestino];
                
                if (posDestino == this.boardScreen.getCaminhoCasas(cpuId).length - 1) {
                    pontuacaoAtual += 10000; 
                }
                
                boolean temInimigo = false;
                for (int p = 0; p < 4; p++) {
                    if (p == cpuId) continue; 
                    
                    for (int i = 0; i < 4; i++) {
                        PlayerPawn enemyPawn = this.boardScreen.getPlayerPawn(p, i);
                        if (enemyPawn != null) {
                            int enemyPos = enemyPawn.getPawnCurrentPos();
                            if (enemyPos >= 4 && enemyPos < this.boardScreen.getCaminhoCasas(p).length - 1) {
                                Point enemyPoint = this.boardScreen.getCaminhoCasas(p)[enemyPos];
                                if (destPoint.x == enemyPoint.x && destPoint.y == enemyPoint.y) {
                                    temInimigo = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                
                if (temInimigo) {
                    pontuacaoAtual += 1000;
                    if ("AGRESSIVA".equalsIgnoreCase(personality)) {
                        pontuacaoAtual += 3000; 
                    }
                }
                
                if ("CORREDORA".equalsIgnoreCase(personality)) {
                    pontuacaoAtual += (posAtual * 50); 
                }
            }
            
            pontuacaoAtual += (int)(Math.random() * 10);

            if (pontuacaoAtual > maiorPontuacao) {
                maiorPontuacao = pontuacaoAtual;
                melhorPeao = peaoIndex;
            }
        }
        
        return melhorPeao != -1 ? melhorPeao : peoesDisponiveis.get(0);
    }

    public int getFurthestPawnIndex(int playerId) {
        int furthestPawn = -1;
        int maxPosition = -1;

        for (int i = 0; i < 4; i++) {
            PlayerPawn pawn = boardScreen.getPlayerPawn(playerId, i);
            if (pawn != null) {
                int pos = pawn.getPawnCurrentPos();
                if (pos >= 4 && pos < boardScreen.getCaminhoCasas(playerId).length - 1) {
                    if (pos > maxPosition) {
                        maxPosition = pos;
                        furthestPawn = i;
                    }
                }
            }
        }
        return furthestPawn; 
    }
}
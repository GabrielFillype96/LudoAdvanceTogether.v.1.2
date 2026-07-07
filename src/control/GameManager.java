// Classe responsável por gerenciar as regras de movimentação dos peões de acordo com as cartas

// Packages
package control;

// Imports interno
import gui.windows.BoardScreen;
import gui.components.PlayerPawn;

// Imports externos
import java.awt.Point;
import javax.swing.Timer;
import javax.swing.JOptionPane;

public class GameManager {
    // VARIÁVEIS DE INSTÂNCIA
    private BoardScreen boardScreen;
    private Timer timerAnimation; 
    private PawnControlManager pawnControlManager;

    /** ** Construtor da classe "GameManager"
    */
    public GameManager(BoardScreen boardScreen) {
        this.boardScreen = boardScreen;
    }

    /** ** Método setter para vinculação
    */
    public void setPawnControlManager(PawnControlManager pawnControlManager) {
        this.pawnControlManager = pawnControlManager;
    }

    // Método para processar o resultado das cartas
    public void cardResultVerification(boolean correct, String cardValue, String cardEffect) {
        if (boardScreen == null) {
            System.err.println("[GameManager] Erro: O tabuleiro não foi registrado!");
            return;
        }

        if (timerAnimation != null && timerAnimation.isRunning()) {
            return;
        }
 
        PlayerPawn p1 = boardScreen.getPlayer1Pawn(0);
        Point[] mapaCasas = boardScreen.getCaminhoCasas();

        if (p1 == null || mapaCasas == null) {
            System.err.println("[GameManager] Erro: Componentes do tabuleiro não inicializados.");
            return;
        }

        if (!correct) {
            System.out.println("[GameManager] Resposta incorreta. O peão permaneceu na casa " + p1.getPawnCurrentPos());
            return;
        }

        if ("AVANÇAR".equalsIgnoreCase(cardEffect)) {
            try {
                String cardValueTreated = cardValue.trim();
                if (cardValueTreated.contains("/")) {
                    cardValueTreated = cardValueTreated.split("/")[0].trim();
                }

                int valorDado = Integer.parseInt(cardValueTreated);

                if (this.pawnControlManager != null) {
                    System.out.println("[GameManager] Interceptado! Mandando " + valorDado + " passos para a memória...");
                    updatePlayablePawns(valorDado);
                    this.pawnControlManager.preparePendingMovement(valorDado, cardEffect);
                } else {
                    System.err.println("[GameManager] Erro: PawnControlManager não injetado!");
                }

            } catch (NumberFormatException e) {
                System.err.println("[GameManager] Erro de conversão: " + cardValue);
            }
        }
    }

    /**
     * Avalia todos os peões a cada turno para decidir quem fica NORMAL ou DESABILITADO
     */
    private void updatePlayablePawns(int cardValue) {
        for (int i = 0; i < 4; i++) {
            PlayerPawn pawn = boardScreen.getPlayer1Pawn(i);
            if (pawn == null) continue;
            
            String currentState = this.pawnControlManager.getPawnState(i);
            
            // Ignora peões que já venceram (DOURADOS nunca mudam de estado)
            if ("DOURADO".equalsIgnoreCase(currentState)) {
                continue;
            }
            
            int pos = pawn.getPawnCurrentPos();
            
            // Regra da Base (Posições 0, 1, 2 e 3 correspondem aos 4 peões na base)
            if (pos < 4) {
                if (Math.abs(cardValue) == 1 || Math.abs(cardValue) == 6) {
                    this.pawnControlManager.updatePawnVisualState(i, "NORMAL");
                } else {
                    this.pawnControlManager.updatePawnVisualState(i, "DESABILITADO"); // Fica rosa!
                }
            } else {
                // Peões que já estão no tabuleiro estão sempre habilitados para andar
                this.pawnControlManager.updatePawnVisualState(i, "NORMAL");
            }
        }
    }
    

    /**
    * Animação do peão, agora controlando qual índice exato está se movendo para virar DOURADO no fim
    * */
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

        timerAnimation = new Timer(15, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                
                if (STEP_INDEX[0] >= pawnPathList.size() - 1) {
                    timerAnimation.stop();
                    System.out.println("[Animação] Movimento concluído suavemente.");
                    
                    if (boardScreen != null) {
                        boardScreen.clearPreview();
                    }

                    // Repassa o índice correto para avaliar as condições dinâmicas e ver se virou Dourado
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
                    
                    if (boardScreen != null) {
                        boardScreen.consumePreviewDot();
                    }
                    boardScreen.repaint();
                    STEP_INDEX[0]++;  
                } else {
                    VISUAL_POS_X[0] += (dx / remainingDistance) * SPEED; 
                    VISUAL_POS_Y[0] += (dy / remainingDistance) * SPEED;
                    playerPawn.setPawnVisualCoordinates(new Point((int) VISUAL_POS_X[0], (int) VISUAL_POS_Y[0]));
                }
            }
        });

        timerAnimation.start();
    }

    /**
     ** Método acionado pelo PawnControlManager 
    */
    public boolean moveChosenPawn(int pawnIndex, int cardValue, String cardEffect) {
        PlayerPawn chosenPawn = boardScreen.getPlayer1Pawn(pawnIndex);
        
        if (chosenPawn == null) {
            System.err.println("[GameManager] Erro: Peão índice " + pawnIndex + " não encontrado!");
            return false;
        }

        Point[] pawnPath = boardScreen.getCaminhoCasas();
        int pawnActualPosition = chosenPawn.getPawnCurrentPos();
        
        boolean isBackwards = "VOLTAR".equalsIgnoreCase(cardEffect) || "RETRÓGRADO".equalsIgnoreCase(cardEffect);
        if (isBackwards && cardValue > 0) {
            cardValue = -cardValue; 
        }
        
        boolean ganhouTurnoExtra = (Math.abs(cardValue) == 6);
        boolean exitBase = false;

        // Regra de Saída da Base
        if (pawnActualPosition < 4) {
            if (Math.abs(cardValue) == 1 || Math.abs(cardValue) == 6) {
                int pawnStarterPath = 4; 
                chosenPawn.setPawnCurrentPos(pawnStarterPath);
                exitBase = true; 
                
               
                
                // Animação repassando o Index
                pawnMovement(chosenPawn, pawnIndex, pawnActualPosition, pawnStarterPath, pawnPath, ganhouTurnoExtra, exitBase);
                return true; 
            } else {
                JOptionPane.showMessageDialog(boardScreen, 
                    "Este peão específico precisa de um 1 ou 6 para sair da base!", 
                    "Movimento Inválido", JOptionPane.WARNING_MESSAGE);
                return false; 
            }
        }

        // Movimentação normal pelo tabuleiro
        int pawnStarterPath = pawnActualPosition + cardValue; 

        if (pawnStarterPath >= pawnPath.length) {
            pawnStarterPath = pawnPath.length - 1;
        }
        if (pawnStarterPath < 4) {
            pawnStarterPath = 4;
        }

        chosenPawn.setPawnCurrentPos(pawnStarterPath);
        
        
        // Animação repassando o Index
        pawnMovement(chosenPawn, pawnIndex, pawnActualPosition, pawnStarterPath, pawnPath, ganhouTurnoExtra, exitBase);
        return true; 
    }

    public void showMovementPreview(int pawnIndex, int cardValue, String cardEffect) {
        PlayerPawn chosenPawn = boardScreen.getPlayer1Pawn(pawnIndex);
        if (chosenPawn == null) return;

        Point[] pawnPath = boardScreen.getCaminhoCasas();
        int pawnActualPosition = chosenPawn.getPawnCurrentPos();

        boolean isBackwards = "VOLTAR".equalsIgnoreCase(cardEffect) || "RETRÓGRADO".equalsIgnoreCase(cardEffect);
        int localCardValue = cardValue;
        if (isBackwards && localCardValue > 0) {
            localCardValue = -localCardValue;
        }

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

        if (destIndex >= pawnPath.length) {
            destIndex = pawnPath.length - 1;
        }
        if (destIndex < 4 && !exitBase) {
            destIndex = 4;
        }

        java.util.List<Point> previewPathList = new java.util.ArrayList<>();
        if (exitBase) {
            previewPathList.add(pawnPath[4]);
        } else {
            if (pawnActualPosition < destIndex) {
                for (int i = pawnActualPosition + 1; i <= destIndex; i++) {
                    previewPathList.add(pawnPath[i]);
                }
            } else if (pawnActualPosition > destIndex) {
                for (int i = pawnActualPosition - 1; i >= destIndex; i--) {
                    previewPathList.add(pawnPath[i]);
                }
            }
        }

        boardScreen.setPreviewData(pawnIndex, destIndex, previewPathList);
    }

    /**
     * DINÂMICO: Modificado para receber o índice do peão e transformá-lo em DOURADO automaticamente
     */
    private void verificarCondicoesFinais(PlayerPawn peao, int pawnIndex, int posicaoAlcancada, boolean ganhouTurnoExtra) {
        // Se a posição alcançada for a última casa do array (o centro do tabuleiro)
        if (posicaoAlcancada >= boardScreen.getCaminhoCasas().length - 1) {
            
            // TRANSFORMAÇÃO DINÂMICA: Comunica a mudança ao painel lateral e tabuleiro
            if (this.pawnControlManager != null) {
                this.pawnControlManager.updatePawnVisualState(pawnIndex, "DOURADO");
            }

            // NOVA LINHA: TRANSFORMAÇÃO DINÂMICA (No Tabuleiro!)
            peao.updatePawnVisual("/assets/peaoAmarelo_90x90.png");

            JOptionPane.showMessageDialog(boardScreen, 
                "🏆 INCRÍVEL! O peão " + (pawnIndex + 1) + " de " + peao.getPlayerName() + " alcançou o Centro do Tabuleiro e tornou-se DOURADO!", 
                "Peão Vitorioso", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (ganhouTurnoExtra) {
            JOptionPane.showMessageDialog(boardScreen, 
                "Incrível! Você tirou um efeito de valor '6'!\nVocê ganhou o direito de jogar novamente.", 
                "Turno Bônus", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
package control;

import gui.windows.CardsContainer;
import java.awt.Color;
import javax.swing.Timer;

public class TurnManager {
    
    private int currentTurn; 
    private GameManager gameManager;
    private CPUIManager cpuManager;
    private CardsContainer cardsContainer;

    // Definição de cores para manter o padrão visual do status
    private static final Color COLOR_INFO = Color.WHITE;
    private static final Color COLOR_WARNING = new Color(241, 196, 15); // Amarelo

    public TurnManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.currentTurn = 0; 
    }

    public void sortearPrimeiroJogador() {
        if (this.gameManager == null) return;

        Color corAlerta = new Color(241, 196, 15);  
        Color corSucesso = new Color(46, 204, 113); 

        javax.swing.SwingUtilities.invokeLater(() -> {
            this.gameManager.emitirStatus("LUDO: ADVANCE TOGETHER", COLOR_INFO);
        });

        Timer timerAviso = new Timer(2000, eAviso -> {
            this.gameManager.emitirStatus("Sorteando o jogador inicial...", corAlerta);

            Timer timerDelayRoleta = new Timer(1500, eRoleta -> {
                
                final int[] delayAtual = {50}; 
                Timer timerSorteio = new Timer(delayAtual[0], null);
                
                timerSorteio.addActionListener(e -> {
                    int jogadorFalso = (int) (Math.random() * 4);
                    String nomeSorteio = (jogadorFalso == 0) ? "VOCÊ" : this.gameManager.getPlayerNameById(jogadorFalso).toUpperCase();
                    
                    this.gameManager.emitirStatusSorteio("🎰 " + nomeSorteio + " 🎰", corAlerta);

                    delayAtual[0] += 40; 
                    timerSorteio.setDelay(delayAtual[0]);

                    if (delayAtual[0] >= 500) {
                        timerSorteio.stop();

                        this.gameManager.setSorteioInicialAtivo(false);
                        
                        this.currentTurn = (int) (Math.random() * 4);
                        String nomeVencedor = (this.currentTurn == 0) ? "Você" : this.gameManager.getPlayerNameById(this.currentTurn);
                        
                        this.gameManager.emitirStatus("🎉 Começa com " + nomeVencedor + "!", corSucesso);

                        if (this.currentTurn == 0) {
                            Timer delayHumano = new Timer(1500, ev -> {
                                startHumanTurn(); 
                            });
                            delayHumano.setRepeats(false);
                            delayHumano.start();
                        } else {
                            // MODIFICADO: Passa 'false' pois é o primeiro turno normal do jogo
                            Timer delayCPU = new Timer(1500, ev -> startCPUTurn(false));
                            delayCPU.setRepeats(false);
                            delayCPU.start();
                        }
                    }
                });
                
                timerSorteio.start();
            });
            timerDelayRoleta.setRepeats(false);
            timerDelayRoleta.start();
            
        });
        timerAviso.setRepeats(false);
        timerAviso.start();
    }

    public void setCardsContainer(CardsContainer cardsContainer) {
        this.cardsContainer = cardsContainer;
    }
    
    public void setCPUIManager(CPUIManager cpuManager) {
        this.cpuManager = cpuManager;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public boolean isHumanTurn() {
        return this.currentTurn == 0;
    }

    public void nextTurn() {
        if (this.currentTurn == 0 && this.gameManager != null) {
            this.gameManager.resetHumanPawnsVisuals();
        }

        currentTurn++;
        if (currentTurn > 3) {
            currentTurn = 0;
        }

        System.out.println("\n=================================");
        System.out.println("[TurnManager] Fim de turno. A vez agora é do Jogador: " + currentTurn);
        System.out.println("=================================");
        
        if (currentTurn == 0) {
            startHumanTurn();
        } else {
            // MODIFICADO: Passa 'false' porque é uma transição normal de turnos
            startCPUTurn(false);
        }
    }

    private void startHumanTurn() {
        System.out.println("[TurnManager] Vez do humano. Aguardando interação...");
        
        if (this.gameManager != null) {
            this.gameManager.setJogadaEmAndamento(false);
            this.gameManager.resetHumanPawnsVisuals();
            this.gameManager.emitirStatus("🎲 Sua vez! Clique no deck para revelar sua carta.", COLOR_INFO);
        }
        
        if (this.cardsContainer != null) {
            this.cardsContainer.startDeckHighlight();
        }
    }

    // MODIFICADO: Agora aceita o parâmetro booleano para identificar jogadas bônus
    private void startCPUTurn(boolean ehTurnoExtra) {
        if (this.gameManager == null) {
            executarAcaoCPU();
            return;
        }

        String nomeCPU = this.gameManager.getPlayerNameById(this.currentTurn);
        System.out.println("[TurnManager] Iniciando sequência de turno para: " + nomeCPU);
        
        // CORREÇÃO AQUI: Só exibe "Turno de CPU X" se NÃO for uma jogada bônus (6)
        if (!ehTurnoExtra) {
            this.gameManager.emitirStatus("Turno de " + nomeCPU, COLOR_INFO);
        }

        // ETAPA 2: Após 1,8 segundos, atualiza para o status "Pensando..."
        Timer timerPensando = new Timer(1800, ePensando -> {
            this.gameManager.emitirStatus("🤖 " + nomeCPU + " está pensando...", COLOR_WARNING);

            // ETAPA 3: Após mais 1,5 segundos pensando, a CPU finalmente joga
            Timer timerPuxarCarta = new Timer(1500, ePuxar -> {
                executarAcaoCPU();
            });
            timerPuxarCarta.setRepeats(false);
            timerPuxarCarta.start();
        });
        timerPensando.setRepeats(false);
        timerPensando.start();
    }

    private void executarAcaoCPU() {
        if (this.cpuManager != null) {
            this.cpuManager.playTurn(this.currentTurn);
        } else {
            System.err.println("[TurnManager] Erro: CPUIManager não encontrado!");
            nextTurn();
        }
    }

    public void processExtraTurn() {
        System.out.println("\n=================================");
        System.out.println("[TurnManager] TURNO EXTRA! A vez continua com o Jogador: " + currentTurn);
        System.out.println("=================================");

        // Limpa o visual para a nova jogada bônus do humano
        if (this.currentTurn == 0 && this.gameManager != null) {
            this.gameManager.setJogadaEmAndamento(false);
            this.gameManager.resetHumanPawnsVisuals();
        }
        
        if (currentTurn == 0) {
            startHumanTurn();
        } else {
            // MODIFICADO: Passa 'true' para pular a mensagem genérica de turno
            startCPUTurn(true);
        }
    }

    public CardsContainer getCardsContainer() {
        return this.cardsContainer; 
    }
}
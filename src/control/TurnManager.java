package control;

import gui.windows.CardsContainer;
import java.awt.Color;
import javax.swing.Timer;
import network.GameClient;
import network.NetworkMessage;

public class TurnManager {
    
    private int currentTurn; 
    private GameManager gameManager;
    private CPUIManager cpuManager;
    private CardsContainer cardsContainer;
    private GameClient gameClient;

    public TurnManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.currentTurn = 0; 
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public boolean isMyTurn() {
        if (this.gameClient == null) {
            return this.currentTurn == 0;
        }
        return this.gameClient.getMyPlayerId() == this.currentTurn;
    }

    public void setTurn(int playerTurn) {
        this.currentTurn = playerTurn;
        System.out.println("[TurnManager] Turno atualizado via rede para Jogador: " + currentTurn);
        
        if (this.gameManager != null) {
            this.gameManager.resetHumanPawnsVisuals();
            this.gameManager.setJogadaEmAndamento(false);
        }

        if (isMyTurn()) {
            startHumanTurn();
        } else if (this.gameManager != null) {
            String nome = this.gameManager.getPlayerNameById(this.currentTurn);
            this.gameManager.emitirStatus(GameStatusManager.VEZ_DE_JOGADOR, nome);
        }
    }

    public void sortearPrimeiroJogador() {
        if (this.gameManager == null) return;

        Color corAlerta = new Color(241, 196, 15);  

        javax.swing.SwingUtilities.invokeLater(() -> {
            this.gameManager.emitirStatus(GameStatusManager.TITULO_JOGO);
        });

        Timer timerAviso = new Timer(2000, eAviso -> {
            this.gameManager.emitirStatus(GameStatusManager.SORTEANDO_JOGADOR);

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
                        
                        this.gameManager.emitirStatus(GameStatusManager.INICIO_VENCEDOR, nomeVencedor);

                        if (this.currentTurn == 0) {
                            Timer delayHumano = new Timer(1500, ev -> startHumanTurn());
                            delayHumano.setRepeats(false);
                            delayHumano.start();
                        } else if (this.gameClient == null) {
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
        return isMyTurn();
    }

    public void nextTurn() {
        if (this.currentTurn == 0 && this.gameManager != null) {
            this.gameManager.resetHumanPawnsVisuals();
        }

        int previousTurn = this.currentTurn;

        if (this.gameManager != null) {
            this.gameManager.decrementAzarCooldown(previousTurn);
        }

        this.currentTurn = (this.currentTurn + 1) % 4;

        System.out.println("\n=================================");
        System.out.println("[TurnManager] Fim de turno. A vez agora é do Jogador: " + currentTurn);
        System.out.println("=================================");
        
        if (gameClient != null && gameClient.getMyPlayerId() == previousTurn) {
            gameClient.send(new NetworkMessage("NEXT_TURN", gameClient.getMyPlayerId(), String.valueOf(this.currentTurn)));
        }

        if (isMyTurn()) {
            startHumanTurn();
        } else if (this.gameClient == null) {
            startCPUTurn(false);
        }
    }

    private void startHumanTurn() {
        System.out.println("[TurnManager] Vez do humano. Aguardando interação...");
        
        if (this.gameManager != null) {
            this.gameManager.setJogadaEmAndamento(false);
            this.gameManager.resetHumanPawnsVisuals();
            this.gameManager.emitirStatus(GameStatusManager.SUA_VEZ_COMPRA);
        }
        
        if (this.cardsContainer != null) {
            this.cardsContainer.startDeckHighlight();
        }
    }

    private void startCPUTurn(boolean ehTurnoExtra) {
        if (this.gameManager == null) {
            executarAcaoCPU();
            return;
        }

        String nomeCPU = this.gameManager.getPlayerNameById(this.currentTurn);
        System.out.println("[TurnManager] Iniciando sequência de turno para: " + nomeCPU);
        
        if (!ehTurnoExtra) {
            this.gameManager.emitirStatus(GameStatusManager.TURNO_CPU_INICIO, nomeCPU);
        }

        Timer timerPensando = new Timer(1800, ePensando -> {
            this.gameManager.emitirStatus(GameStatusManager.CPU_PENSANDO, nomeCPU);

            Timer timerPuxarCarta = new Timer(1500, ePuxar -> executarAcaoCPU());
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

        if (this.currentTurn == 0 && this.gameManager != null) {
            this.gameManager.setJogadaEmAndamento(false);
            this.gameManager.resetHumanPawnsVisuals();
        }
        
        if (isMyTurn()) {
            startHumanTurn();
        } else if (this.gameClient == null) {
            startCPUTurn(true);
        }
    }

    public CardsContainer getCardsContainer() {
        return this.cardsContainer; 
    }
}
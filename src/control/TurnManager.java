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

    // Definição de cores para manter o padrão visual do status
    private static final Color COLOR_INFO = Color.WHITE;
    private static final Color COLOR_WARNING = new Color(241, 196, 15); // Amarelo

    public TurnManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.currentTurn = 0; 
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public boolean isMyTurn() {
        if (this.gameClient == null) {
            return this.currentTurn == 0; // Modo offline: Jogador local é o ID 0
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
            this.gameManager.emitirStatus("Vez de " + nome + "...", COLOR_INFO);
        }
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
                            Timer delayHumano = new Timer(1500, ev -> startHumanTurn());
                            delayHumano.setRepeats(false);
                            delayHumano.start();
                        } else {
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

        int previousTurn = this.currentTurn;
        this.currentTurn = (this.currentTurn + 1) % 4;

        System.out.println("\n=================================");
        System.out.println("[TurnManager] Fim de turno. A vez agora é do Jogador: " + currentTurn);
        System.out.println("=================================");
        
        // Sincroniza a troca de turno via rede se a jogada pertencia ao jogador local
        if (gameClient != null && gameClient.getMyPlayerId() == previousTurn) {
            gameClient.send(new NetworkMessage("NEXT_TURN", gameClient.getMyPlayerId(), String.valueOf(this.currentTurn)));
        }

        if (isMyTurn()) {
            startHumanTurn();
        } else {
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

    private void startCPUTurn(boolean ehTurnoExtra) {
        if (this.gameManager == null) {
            executarAcaoCPU();
            return;
        }

        String nomeCPU = this.gameManager.getPlayerNameById(this.currentTurn);
        System.out.println("[TurnManager] Iniciando sequência de turno para: " + nomeCPU);
        
        if (!ehTurnoExtra) {
            this.gameManager.emitirStatus("Turno de " + nomeCPU, COLOR_INFO);
        }

        Timer timerPensando = new Timer(1800, ePensando -> {
            this.gameManager.emitirStatus("🤖 " + nomeCPU + " está pensando...", COLOR_WARNING);

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
        } else {
            startCPUTurn(true);
        }
    }

    public CardsContainer getCardsContainer() {
        return this.cardsContainer; 
    }
}
package control;

import gui.windows.CardsContainer;
import javax.swing.Timer;
import network.GameClient;
import network.NetworkMessage;

public class TurnManager {
    
    private int currentTurn = -1; 
    private int localPlayerId = 0; // ID do jogador local (offline)
    private GameManager gameManager;
    private CPUIManager cpuManager;
    private CardsContainer cardsContainer;
    private GameClient gameClient;

    public TurnManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setLocalPlayerId(int id) {
        this.localPlayerId = id;
        System.out.println("[TurnManager] ID Local configurado para: " + this.localPlayerId);
    }

    public void setGameClient(GameClient gameClient) {
        this.gameClient = gameClient;
        System.out.println("[TurnManager] GameClient vinculado. ID de rede: " + getMyPlayerId());
    }

    public int getMyPlayerId() {
        if (this.gameClient != null && this.gameClient.isConnected()) {
            return this.gameClient.getMyPlayerId();
        }
        return this.localPlayerId; 
    }

    public boolean isMyTurn() {
        if (currentTurn == -1) return false;
        return getMyPlayerId() == this.currentTurn;
    }

    public void setTurn(int playerTurn) {
        this.currentTurn = playerTurn;
        int meuId = getMyPlayerId();
        
        System.out.println("[TurnManager] Turno alterado para: Jogador " + currentTurn + " | Meu ID: " + meuId);
        
        if (this.gameManager != null) {
            this.gameManager.resetHumanPawnsVisuals();
            this.gameManager.setJogadaEmAndamento(false);
            
            if (this.gameManager.getBoardScreen() != null) {
                this.gameManager.getBoardScreen().updateActivePlayerSlot(this.currentTurn);
            }
        }

        boolean ehCpu = (this.gameManager != null && this.gameManager.isCPU(this.currentTurn));

        // 1. SE FOR A SUA VEZ (Humano Local)
        if (isMyTurn()) {
            startHumanTurn();
        } 
        // 2. SE FOR A VEZ DE UMA CPU
        else if (ehCpu) {
            // No modo Offline OU sendo o Host (Player 0) no Online, inicia a jogada da CPU
            if (this.gameClient == null || meuId == 0) {
                startCPUTurn(false);
            } else {
                String nome = this.gameManager.getPlayerNameById(this.currentTurn);
                this.gameManager.emitirStatus(GameStatusManager.TURNO_CPU_INICIO, nome);
            }
        } 
        // 3. SE FOR A VEZ DE OUTRO JOGADOR HUMANO (Online)
        else {
            String nome = this.gameManager.getPlayerNameById(this.currentTurn);
            this.gameManager.emitirStatus(GameStatusManager.VEZ_DE_JOGADOR, nome);
        }
    }

    public void sortearPrimeiroJogador() {
        if (this.gameManager == null) return;

        if (this.gameManager.getDeckManager() != null) {
            this.gameManager.getDeckManager().initializeDecksAsync(() -> {
                System.out.println("[TurnManager] Baralhos prontos para o início.");
            });
        }

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
                    String nomeSorteio = (jogadorFalso == getMyPlayerId()) ? "VOCÊ" : this.gameManager.getPlayerNameById(jogadorFalso).toUpperCase();
                    
                    this.gameManager.emitirStatus(GameStatusManager.SORTEIO_GIRO, nomeSorteio);

                    delayAtual[0] += 40; 
                    timerSorteio.setDelay(delayAtual[0]);

                    if (delayAtual[0] >= 500) {
                        timerSorteio.stop();
                        this.gameManager.setSorteioInicialAtivo(false);

                        // No offline ou sendo Host online, sorteia e aplica o turno inicial
                        if (this.gameClient == null || getMyPlayerId() == 0) {
                            int sorteado = (int) (Math.random() * 4);
                            
                            if (this.gameClient != null && this.gameClient.isConnected()) {
                                this.gameClient.send(new NetworkMessage("SET_TURN", getMyPlayerId(), String.valueOf(sorteado)));
                            }
                            
                            setTurn(sorteado);
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
        if (isMyTurn() && this.gameManager != null) {
            this.gameManager.resetHumanPawnsVisuals();
        }

        int previousTurn = this.currentTurn;

        if (this.gameManager != null) {
            this.gameManager.decrementAzarCooldown(previousTurn);
        }

        int next = (this.currentTurn + 1) % 4;
        
        if (gameClient != null && gameClient.isConnected() && gameClient.getMyPlayerId() == previousTurn) {
            gameClient.send(new NetworkMessage("NEXT_TURN", gameClient.getMyPlayerId(), String.valueOf(next)));
        } else {
            setTurn(next);
        }
    }

    private void startHumanTurn() {
        System.out.println("[TurnManager] Sua vez (ID " + getMyPlayerId() + "). Controles liberados.");
        
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
        System.out.println("[TurnManager] Vez da CPU " + currentTurn + " (" + nomeCPU + ")");
        
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
            System.err.println("[TurnManager] CPUIManager ausente! Passando turno...");
            nextTurn();
        }
    }

    public void processExtraTurn() {
        if (this.gameManager != null && this.gameManager.getBoardScreen() != null) {
            this.gameManager.getBoardScreen().updateActivePlayerSlot(this.currentTurn);
        }

        if (isMyTurn() && this.gameManager != null) {
            this.gameManager.setJogadaEmAndamento(false);
            this.gameManager.resetHumanPawnsVisuals();
        }
        
        boolean ehCpu = (this.gameManager != null && this.gameManager.isCPU(this.currentTurn));

        if (isMyTurn()) {
            startHumanTurn();
        } else if (ehCpu) {
            if (this.gameClient == null || getMyPlayerId() == 0) {
                startCPUTurn(true);
            } else {
                String nome = this.gameManager.getPlayerNameById(this.currentTurn);
                this.gameManager.emitirStatus(GameStatusManager.TURNO_CPU_INICIO, nome);
            }
        } else {
            String nome = this.gameManager.getPlayerNameById(this.currentTurn);
            this.gameManager.emitirStatus(GameStatusManager.VEZ_DE_JOGADOR, nome);
        }
    }

    public CardsContainer getCardsContainer() {
        return this.cardsContainer; 
    }
}
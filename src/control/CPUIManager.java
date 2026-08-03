package control;

import cards.CustomCards;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CPUIManager {
    private GameManager gameManager;
    private DeckManager deckManager; 
    
    private String jogoDificuldade = "MÉDIO";
    private String[] personalidadesCPUs = new String[4];
    private Random random = new Random();

    public CPUIManager(GameManager gameManager) {
        this.gameManager = gameManager;
        inicializarPersonalidadesAleatorias();
    }

    /**
     * Verifica se o slot especificado é controlado por uma CPU.
     */
    public boolean isCPUSlot(int slot) {
        if (slot < 0 || slot >= 4) {
            return false;
        }

        // No modo Online conectado, assume-se que os outros slots são jogadores humanos
        if (gameManager != null && gameManager.getGameClient() != null && gameManager.getGameClient().isConnected()) {
            return false;
        }

        // No modo Offline, qualquer slot diferente do jogador local é uma CPU
        int meuId = (gameManager != null && gameManager.getTurnManager() != null) 
                    ? gameManager.getTurnManager().getMyPlayerId() : 0;

        return slot != meuId;
    }

    public void setJogoDificuldade(String dificuldade) {
        if (dificuldade != null && !dificuldade.trim().isEmpty()) {
            String difNormalizada = dificuldade.trim().toUpperCase();
            // Normaliza entradas sem acento para garantir compatibilidade no switch
            if (difNormalizada.equals("FACIL")) difNormalizada = "FÁCIL";
            if (difNormalizada.equals("MEDIO")) difNormalizada = "MÉDIO";
            if (difNormalizada.equals("DIFICIL")) difNormalizada = "DIFÍCIL";

            this.jogoDificuldade = difNormalizada;
        }
        System.out.println("[CPUIManager] Dificuldade do Jogo configurada para: " + this.jogoDificuldade);
    }

    public String getJogoDificuldade() {
        return this.jogoDificuldade;
    }

    public void inicializarPersonalidadesAleatorias() {
        List<String> pool = new ArrayList<>();
        pool.add("AGRESSIVA");
        pool.add("DEFENSIVA");
        pool.add("CORREDORA");
        
        Collections.shuffle(pool);
        
        this.personalidadesCPUs[1] = pool.get(0);
        this.personalidadesCPUs[2] = pool.get(1);
        this.personalidadesCPUs[3] = pool.get(2);
        
        System.out.println("\n[CPUIManager] ========== PERSONALIDADES DA RODADA ==========");
        System.out.println("[CPUIManager] CPU 1: " + this.personalidadesCPUs[1]);
        System.out.println("[CPUIManager] CPU 2: " + this.personalidadesCPUs[2]);
        System.out.println("[CPUIManager] CPU 3: " + this.personalidadesCPUs[3]);
        System.out.println("[CPUIManager] ==============================================\n");
    }

    public void setDeckManager(DeckManager deckManager) {
        this.deckManager = deckManager;
    }

    public void playTurn(int cpuId) {
        System.out.println("[CPUIManager] Iniciando o raciocínio da CPU " + cpuId + "...");

        Timer cpuTimer = new Timer(1800, e -> {
            // Tratamento para evitar congelar o jogo caso deckManager seja nulo
            if (this.deckManager == null) {
                System.err.println("[CPUIManager] Erro Crítico: DeckManager não foi inicializado!");
                if (this.gameManager != null && this.gameManager.getTurnManager() != null) {
                    this.gameManager.getTurnManager().nextTurn();
                }
                return;
            }

            boolean todosNaBase = (gameManager.getFurthestPawnIndex(cpuId) == -1);
            CustomCards carta = this.deckManager.drawCard(cpuId, todosNaBase);
            
            if (carta != null) {
                System.out.println("[CPUIManager] A CPU " + cpuId + " puxou a carta ID: " + carta.getCardID());
                boolean acertou;
                
                if ("SORTE".equalsIgnoreCase(carta.getCardType()) || "AZAR".equalsIgnoreCase(carta.getCardType())) {
                    System.out.println("[CPUIManager] A CPU tirou uma carta de " + carta.getCardType() + "!");
                    acertou = true; 
                } else {
                    acertou = calcularAcertoCPU(carta.getDificuldade()); 
                    System.out.println("[CPUIManager] CPU puxou pergunta (" + carta.getDificuldade() + ") no modo [" + jogoDificuldade + "] e... " + (acertou ? "ACERTOU!" : "ERROU!"));
                }
                
                try {
                    this.gameManager.cardResultVerification(acertou, carta.getCardValueText(), carta.getCardEffect(), carta.getCardType());
                } catch (Exception ex) {
                    System.err.println("[CPUIManager] Erro inesperado na carta: " + ex.getMessage());
                    this.gameManager.getTurnManager().nextTurn(); 
                } finally {
                    this.deckManager.discardCard(cpuId, carta);
                }
                
            } else {
                System.err.println("[CPUIManager] Erro: A CPU não encontrou cartas no deck.");
                this.gameManager.getTurnManager().nextTurn();
            }
        });
        
        cpuTimer.setRepeats(false);
        cpuTimer.start();
    }

    public void iniciarJogadaCPU(int cpuId, java.util.List<Integer> peoesDisponiveis, int cardValue, String cardEffect) {
        System.out.println("[CPUIManager] Analisando jogada para a CPU " + cpuId + "...");

        if (peoesDisponiveis == null || peoesDisponiveis.isEmpty()) {
            System.out.println("[CPUIManager] A CPU " + cpuId + " não tem peões válidos para mover.");
            this.gameManager.getTurnManager().nextTurn();
            return;
        }

        String personalidade = getCPUPersonality(cpuId);
        int peaoEscolhido;

        // Na dificuldade FÁCIL do jogo, a CPU tem 35% de chance de fazer uma escolha aleatória
        if ("FÁCIL".equalsIgnoreCase(this.jogoDificuldade) && random.nextDouble() <= 0.35) {
            peaoEscolhido = peoesDisponiveis.get(random.nextInt(peoesDisponiveis.size()));
            System.out.println("[CPUIManager] CPU " + cpuId + " cometeu um descuido tático (Modo Fácil) e escolheu o peão: " + peaoEscolhido);
        } else {
            peaoEscolhido = this.gameManager.escolherMelhorPeaoParaCPU(cpuId, peoesDisponiveis, cardValue, personalidade);
            System.out.println("[CPUIManager] A CPU " + cpuId + " (" + personalidade + ") calculou e decidiu mover o peão índice: " + peaoEscolhido);
        }

        Timer movimentoTimer = new Timer(1000, e -> {
            try {
                boolean moveuComSucesso = this.gameManager.moveChosenPawn(peaoEscolhido, cardValue, cardEffect);
                if (!moveuComSucesso) {
                    this.gameManager.getTurnManager().nextTurn();
                }
            } catch (Exception ex) {
                System.err.println("[CPUIManager] Erro ao mover o peão: " + ex.getMessage());
                this.gameManager.getTurnManager().nextTurn();
            }
        });
        
        movimentoTimer.setRepeats(false);
        movimentoTimer.start();
    }

    private boolean calcularAcertoCPU(String dificuldadeCarta) {
        double chance = random.nextDouble(); 
        String difCarta = (dificuldadeCarta != null) ? dificuldadeCarta.toUpperCase() : "MÉDIO";

        switch (this.jogoDificuldade) {
            case "FÁCIL":
                if (difCarta.contains("FÁCIL") || difCarta.contains("FACIL")) return chance <= 0.75;
                if (difCarta.contains("MÉDIO") || difCarta.contains("MEDIO")) return chance <= 0.50;
                return chance <= 0.25;

            case "DIFÍCIL":
                if (difCarta.contains("FÁCIL") || difCarta.contains("FACIL")) return chance <= 0.98;
                if (difCarta.contains("MÉDIO") || difCarta.contains("MEDIO")) return chance <= 0.90;
                return chance <= 0.70;

            case "MÉDIO":
            default:
                if (difCarta.contains("FÁCIL") || difCarta.contains("FACIL")) return chance <= 0.90;
                if (difCarta.contains("MÉDIO") || difCarta.contains("MEDIO")) return chance <= 0.75;
                return chance <= 0.50;
        }
    }

    public String getCPUPersonality(int cpuId) {
        if (cpuId >= 1 && cpuId < this.personalidadesCPUs.length && this.personalidadesCPUs[cpuId] != null) {
            return this.personalidadesCPUs[cpuId];
        }
        return "PADRAO";
    }
}
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
    
    // Armazena a dificuldade global escolhida pelo jogador no menu ("FÁCIL", "MÉDIO", "DIFÍCIL")
    private String jogoDificuldade = "MÉDIO";
    
    // Vetor que guardará a personalidade fixa sorteada para cada CPU (índices 1, 2 e 3)
    private String[] personalidadesCPUs = new String[4];
    private Random random = new Random();

    public CPUIManager(GameManager gameManager) {
        this.gameManager = gameManager;
        inicializarPersonalidadesAleatorias();
    }

    public void setJogoDificuldade(String dificuldade) {
        if (dificuldade != null && !dificuldade.trim().isEmpty()) {
            this.jogoDificuldade = dificuldade.trim().toUpperCase();
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
            if (this.deckManager != null) {

                boolean todosNaBase = (gameManager.getFurthestPawnIndex(cpuId) == -1);
                CustomCards carta = this.deckManager.drawCard(cpuId, todosNaBase);
                
                if (carta != null) {
                    System.out.println("[CPUIManager] A CPU " + cpuId + " puxou a carta ID: " + carta.getCardID());
                    boolean acertou = true;
                    
                    if ("SORTE".equalsIgnoreCase(carta.getCardType()) || "AZAR".equalsIgnoreCase(carta.getCardType())) {
                        System.out.println("[CPUIManager] A CPU tirou uma carta de " + carta.getCardType() + "!");
                        acertou = true; 
                    } else {
                        // === CORREÇÃO: Passando a DIFICULDADE da carta e não o TIPO ===
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

        String personalidade = "PADRAO";
        if (cpuId >= 1 && cpuId < this.personalidadesCPUs.length) {
            personalidade = this.personalidadesCPUs[cpuId];
        }
        
        int peaoEscolhido;

        // Na dificuldade FÁCIL do jogo, a CPU tem 35% de chance de fazer uma escolha aleatória (erro tático)
        if ("FÁCIL".equalsIgnoreCase(this.jogoDificuldade) && random.nextDouble() <= 0.35) {
            peaoEscolhido = peoesDisponiveis.get(random.nextInt(peoesDisponiveis.size()));
            System.out.println("[CPUIManager] CPU " + cpuId + " cometeu um descuido tático (Modo Fácil) e escolheu o peão: " + peaoEscolhido);
        } else {
            // Em dificuldades normais/altas, calcula a melhor jogada via GameManager
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

    /**
     * Calcula se a CPU acertou a pergunta combinando a dificuldade da CARTA
     * com a dificuldade escolhida para o JOGO no menu.
     */
    private boolean calcularAcertoCPU(String dificuldadeCarta) {
        double chance = random.nextDouble(); 
        String difCarta = (dificuldadeCarta != null) ? dificuldadeCarta.toUpperCase() : "MÉDIO";

        switch (this.jogoDificuldade) {
            case "FÁCIL":
                // Jogo Fácil: CPU erra mais
                if (difCarta.contains("FÁCIL") || difCarta.contains("FACIL")) return chance <= 0.70;
                if (difCarta.contains("MÉDIO") || difCarta.contains("MEDIO")) return chance <= 0.40;
                return chance <= 0.15; // Difícil

            case "DIFÍCIL":
                // Jogo Difícil: CPU erra raramente
                if (difCarta.contains("FÁCIL") || difCarta.contains("FACIL")) return chance <= 0.98;
                if (difCarta.contains("MÉDIO") || difCarta.contains("MEDIO")) return chance <= 0.80;
                return chance <= 0.50; // Difícil

            case "MÉDIO":
            default:
                // Jogo Médio: Equilibrado
                if (difCarta.contains("FÁCIL") || difCarta.contains("FACIL")) return chance <= 0.85;
                if (difCarta.contains("MÉDIO") || difCarta.contains("MEDIO")) return chance <= 0.60;
                return chance <= 0.30; // Difícil
        }
    }

    public String getCPUPersonality(int cpuId) {
        if (cpuId >= 0 && cpuId < this.personalidadesCPUs.length) {
            return this.personalidadesCPUs[cpuId];
        }
        return "PADRAO";
    }
}
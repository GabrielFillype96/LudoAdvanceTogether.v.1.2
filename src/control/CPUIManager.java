package control;

import cards.CustomCards;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CPUIManager {
    private GameManager gameManager;
    private DeckManager deckManager; 
    
    // Vetor que guardará a personalidade fixa sorteada para cada CPU (índices 1, 2 e 3)
    private String[] personalidadesCPUs = new String[4];

    public CPUIManager(GameManager gameManager) {
        this.gameManager = gameManager;
        // Realiza o sorteio das personalidades assim que o gerenciador da CPU é criado
        inicializarPersonalidadesAleatorias();
    }

    /**
     * Distribui as personalidades (AGRESSIVA, DEFENSIVA, CORREDORA) de forma aleatória
     * entre as CPUs 1, 2 e 3 no início do jogo, garantindo que não haja repetições.
     */
    public void inicializarPersonalidadesAleatorias() {
        List<String> pool = new ArrayList<>();
        pool.add("AGRESSIVA");
        pool.add("DEFENSIVA");
        pool.add("CORREDORA");
        
        // Embaralha a lista aleatoriamente
        Collections.shuffle(pool);
        
        // Atribui uma personalidade única para cada uma das 3 CPUs
        this.personalidadesCPUs[1] = pool.get(0);
        this.personalidadesCPUs[2] = pool.get(1);
        this.personalidadesCPUs[3] = pool.get(2);
        
        // Log para você acompanhar no console quem é quem nesta rodada!
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

        Timer cpuTimer = new Timer(2000, e -> {
            if (this.deckManager != null) {
                CustomCards carta = this.deckManager.drawCard(cpuId);
                
                if (carta != null) {
                    System.out.println("[CPUIManager] A CPU " + cpuId + " puxou a carta ID: " + carta.getCardID());
                    boolean acertou = true;
                    
                    if ("SORTE".equalsIgnoreCase(carta.getCardType()) || "AZAR".equalsIgnoreCase(carta.getCardType())) {
                        System.out.println("[CPUIManager] A CPU tirou uma carta de " + carta.getCardType() + "!");
                        acertou = true; 
                    } else {
                        // === CHAMA O NOSSO SISTEMA DE PROBABILIDADE ===
                        acertou = calcularAcertoCPU(carta.getCardType()); 
                        System.out.println("[CPUIManager] A CPU puxou uma carta " + carta.getCardType() + " e... " + (acertou ? "ACERTOU!" : "ERROU!"));
                    }
                    
                    // === BLINDAGEM MÁXIMA RESTAURADA AQUI ===
                    try {
                        this.gameManager.cardResultVerification(acertou, carta.getCardValueText(), carta.getCardEffect());
                    } catch (Exception ex) {
                        System.err.println("[CPUIManager] Erro inesperado na carta: " + ex.getMessage());
                        this.gameManager.getTurnManager().nextTurn(); // Passa a vez para não travar
                    } finally {
                        // O 'finally' garante que a carta VAI para o descarte, independente de qualquer erro!
                        this.deckManager.discardCard(cpuId, carta);
                    }
                    // ==============================
                    
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

        // === RECUPERA A PERSONALIDADE QUE FOI SORTEADA NO INÍCIO DO JOGO ===
        String personalidade = "PADRAO";
        if (cpuId >= 1 && cpuId < this.personalidadesCPUs.length) {
            personalidade = this.personalidadesCPUs[cpuId];
        }
        
        // CHAMA O ORÁCULO DO GAMEMANAGER PARA DECIDIR A JOGADA
        int peaoEscolhido = this.gameManager.escolherMelhorPeaoParaCPU(cpuId, peoesDisponiveis, cardValue, personalidade);
        
        System.out.println("[CPUIManager] A CPU " + cpuId + " (" + personalidade + ") calculou e decidiu mover o peão índice: " + peaoEscolhido);

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
     * Calcula se a CPU acertou a pergunta com base na dificuldade da carta.
     * @param dificuldade O tipo da carta (FÁCIL, MÉDIO, DIFÍCIL)
     * @return true se acertou, false se errou.
     */
    private boolean calcularAcertoCPU(String dificuldade) {
        // Math.random() gera um número decimal aleatório entre 0.0 e 0.99...
        double chance = Math.random(); 
        
        switch (dificuldade.toUpperCase()) {
            case "FÁCIL":
                return chance <= 0.90; // 90% de chance de acerto
            case "MÉDIO":
                return chance <= 0.65; // 65% de chance de acerto
            case "DIFÍCIL":
                return chance <= 0.30; // Apenas 30% de chance de acerto!
            default:
                return chance <= 0.50; // Fallback: 50% de chance se algo der errado
        }
    }
}
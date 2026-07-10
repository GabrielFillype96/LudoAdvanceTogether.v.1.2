package control;

import cards.CustomCards;
import javax.swing.Timer;

public class CPUIManager {
    private GameManager gameManager;
    private DeckManager deckManager; 

    public CPUIManager(GameManager gameManager) {
        this.gameManager = gameManager;
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
                    } else {
                        acertou = (Math.random() < 0.8); 
                        System.out.println("[CPUIManager] A CPU tentou responder e... " + (acertou ? "ACERTOU!" : "ERROU!"));
                    }
                    
                    // === BLINDAGEM MÁXIMA AQUI ===
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

        int indiceSorteado = (int) (Math.random() * peoesDisponiveis.size());
        int peaoEscolhido = peoesDisponiveis.get(indiceSorteado);
        System.out.println("[CPUIManager] A CPU " + cpuId + " decidiu mover o peão índice: " + peaoEscolhido);

        Timer movimentoTimer = new Timer(1000, e -> {
            // === BLINDAGEM NO MOVIMENTO ===
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
}
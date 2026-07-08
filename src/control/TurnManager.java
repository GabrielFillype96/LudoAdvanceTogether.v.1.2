package control;

import gui.windows.CardsContainer;
import cards.CustomCards;
import java.util.List;
import javax.swing.Timer;

public class TurnManager {
    
    private int currentTurn; 
    private GameManager gameManager;
    private CardsContainer cardsContainer; // Referência às cartas

    public TurnManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.currentTurn = 0; // 0 = Humano, 1 a 3 = CPUs
    }

    // Injeção de dependência para a CPU conseguir "ler" a carta
    public void setCardsContainer(CardsContainer cardsContainer) {
        this.cardsContainer = cardsContainer;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public boolean isHumanTurn() {
        return this.currentTurn == 0;
    }

    public void nextTurn() {
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
            startCPUTurn();
        }
    }

    private void startHumanTurn() {
        System.out.println("[TurnManager] Turno do Humano iniciado. Virando carta de FRENTE.");
        
        // 🔄 SEU ADENDO AQUI: Vira a carta de frente para o jogador humano interagir
        if (cardsContainer != null) {
            // cardsContainer.setCardFaceUp(true);    // <- Método visual que você implementará futuramente
            // cardsContainer.setButtonsEnabled(true); // <- Ativa os botões de escolha
        }
    }

    private void startCPUTurn() {
        System.out.println("[TurnManager] Turno da CPU " + currentTurn + " iniciado. Virando carta de COSTAS.");
        
        // 🔄 SEU ADENDO AQUI: Vira a carta de costas imediatamente para o Humano não interferir
        if (cardsContainer != null) {
            // cardsContainer.setCardFaceUp(false);    // <- Método visual que você implementará futuramente
            // cardsContainer.setButtonsEnabled(false); // <- Bloqueia cliques por segurança
        }
        
        // Simula o tempo da CPU processando a jogada (2000ms = 2 segundos)
        Timer cpuTimer = new Timer(2000, e -> {
            if (cardsContainer != null && cardsContainer.getActiveCard() != null) {
                CustomCards carta = cardsContainer.getActiveCard();
                System.out.println("[TurnManager] A CPU " + currentTurn + " processou a carta e fará a jogada!");
                
                // 1. Manda a jogada direto para o GameManager
                gameManager.cardResultVerification(true, carta.getCardValueText(), carta.getCardEffect());
                
                // 2. Avança a carta internamente no painel (ela continuará de costas até voltar ao humano)
                avancarCarta(carta);
            }
        });
        cpuTimer.setRepeats(false);
        cpuTimer.start();
    }

    /**
     * Método auxiliar para a CPU trocar a carta visualmente de forma silenciosa
     */
    private void avancarCarta(CustomCards cartaAtual) {
        List<CustomCards> lista = cardsContainer.getCardList();
        int nextIndex = lista.indexOf(cartaAtual) + 1;

        if (nextIndex < lista.size()) {
            cardsContainer.displayActiveCard(lista.get(nextIndex));
        } else {
            if (!lista.isEmpty()) {
                cardsContainer.displayActiveCard(lista.get(0)); // Reinicia o deck se acabarem
            }
        }
    }

    /**
     * Acionado quando um jogador tira 6 e ganha o direito de jogar novamente.
     */
    public void processExtraTurn() {
        System.out.println("\n=================================");
        System.out.println("[TurnManager] TURNO EXTRA! A vez continua com o Jogador: " + currentTurn);
        System.out.println("=================================");
        
        if (currentTurn == 0) {
            startHumanTurn();
        } else {
            startCPUTurn(); // Isso recria o Timer e faz a CPU ler a próxima carta!
        }
    }
}
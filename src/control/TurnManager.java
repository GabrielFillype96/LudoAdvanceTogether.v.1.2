package control;

// Imports internos
import gui.windows.CardsContainer;

public class TurnManager {
    
    private int currentTurn; 
    private GameManager gameManager;
    private CPUIManager cpuManager;
    private CardsContainer cardsContainer;

    public TurnManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.currentTurn = 0; 
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
        System.out.println("[TurnManager] Vez do humano. Aguardando interação...");
        if (this.cardsContainer != null) {
            this.cardsContainer.startDeckHighlight();
        }
    }

    private void startCPUTurn() {
        System.out.println("[TurnManager] Vez da CPU " + currentTurn + " puxar a carta...");
        
        // Agora o TurnManager apenas delega a responsabilidade! (Ficou muito mais limpo)
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
        
        if (currentTurn == 0) {
            startHumanTurn();
        } else {
            startCPUTurn();
        }
    }
}
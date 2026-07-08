// Classe responsável por gerenciar os turnos e o fluxo da partida

package control;

public class TurnManager {
    
    // VARIÁVEIS DE INSTÂNCIA
    private int currentTurn; // 0 = Jogador 1 (Humano), 1 = CPU 1, 2 = CPU 2, 3 = CPU 3
    private GameManager gameManager;

    /**
     * Construtor do Gestor de Turnos
     */
    public TurnManager(GameManager gameManager) {
        this.gameManager = gameManager;
        this.currentTurn = 0; // O jogo começa sempre com o Jogador 1 (Humano)
    }

    /**
     * Retorna de quem é a vez no momento
     */
    public int getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Passa o bastão para o próximo jogador. 
     * Roda de 0 a 3, e depois volta para 0.
     */
    public void nextTurn() {
        currentTurn++;
        
        if (currentTurn > 3) {
            currentTurn = 0; // Volta para o Humano
        }

        System.out.println("\n=================================");
        System.out.println("[TurnManager] Fim de turno. A vez agora é do Jogador: " + currentTurn);
        System.out.println("=================================");
        
        // Verifica de quem é a nova vez para tomar as decisões de interface
        if (currentTurn == 0) {
            startHumanTurn();
        } else {
            startCPUTurn();
        }
    }

    /**
     * Prepara a vez do Jogador Humano
     */
    private void startHumanTurn() {
        System.out.println("[TurnManager] Vez do Humano. Desbloqueando interface para puxar carta...");
        // TODO: Chamar o método que desbloqueia o botão de tirar carta
    }

    /**
     * Prepara a vez da Inteligência Artificial
     */
    private void startCPUTurn() {
        System.out.println("[TurnManager] Vez da CPU " + currentTurn + ". Bloqueando interface...");
        // TODO: Chamar o método que bloqueia o botão de tirar carta para o humano não roubar
        
        // TODO: Iniciar o "cérebro" da CPU (Fase 3)
    }
}
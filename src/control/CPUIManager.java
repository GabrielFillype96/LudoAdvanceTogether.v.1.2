package control;

import java.util.List;
import javax.swing.Timer;

public class CPUIManager {
    
    private GameManager gameManager;

    public CPUIManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Inicia o pensamento da IA para decidir qual peão mover.
     */
    public void iniciarJogadaCPU(int cpuId, List<Integer> peoesDisponiveis, int valorDado, String cardEffect) {
        // Medida de segurança: se não houver peões válidos, aborta (o GameManager já tratou de passar o turno)
        if (peoesDisponiveis == null || peoesDisponiveis.isEmpty()) {
            return; 
        }

        System.out.println("[CPUIManager] CPU " + cpuId + " analisando opções de movimento...");
        
        // CÉREBRO DA IA: Por enquanto, apenas escolhe o primeiro peão da lista elegível.
        // Futuramente, podemos adicionar lógica aqui (ex: preferir tirar peão da base antes de avançar)
        int peaoEscolhido = peoesDisponiveis.get(0);
        
        System.out.println("[CPUIManager] CPU " + cpuId + " decidiu mover o peão índice " + peaoEscolhido);

        // Simula o tempo de "pensamento" (1.5 segundos) antes de mover a peça no tabuleiro
        Timer atrasoPensamento = new Timer(1500, e -> {
            
            // Move a peça SILENCIOSAMENTE direto no motor do jogo (sem clicar na interface visual)
            boolean sucesso = gameManager.moveChosenPawn(peaoEscolhido, valorDado, cardEffect);
            
            if (!sucesso) {
                System.err.println("[CPUIManager] Aviso: Falha ao tentar mover o peão " + peaoEscolhido + " da CPU " + cpuId);
            }
            
        });
        atrasoPensamento.setRepeats(false);
        atrasoPensamento.start();
    }
}
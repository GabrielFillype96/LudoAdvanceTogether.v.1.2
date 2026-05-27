package actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gui.windows.NewGameMenuInterface;
import gui.windows.WindowManager;

public class StartGameAction implements ActionListener {

    private NewGameMenuInterface offlineMenu;
    private WindowManager windowManager;

    // O construtor recebe o menu offline para poder ler os inputs e o gerenciador para mudar a tela
    public StartGameAction(NewGameMenuInterface offlineMenu, WindowManager windowManager) {
        this.offlineMenu = offlineMenu;
        this.windowManager = windowManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[Action] Botão JOGAR detectado! Processando dados da partida...");

        if (offlineMenu != null && windowManager != null) {
            // 1. Extrai o nome do jogador do JTextField (vamos criar esses métodos no menu já já)
            String player1Name = offlineMenu.getPlayerName();

            // 2. Extrai a dificuldade selecionada nos RadioButtons
            String difficulty = offlineMenu.getCPUDifficulty();

            System.out.println("[Action] Dados Coletados -> Jogador: " + player1Name + " | Dificuldade: " + difficulty);

            // 3. Ordena ao WindowManager para carregar o jogo com esses parâmetros
            windowManager.startOfflineGameMode(player1Name, difficulty);
        }
    }
}
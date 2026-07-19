package actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gui.windows.NewGameMenuScreen;
import gui.windows.WindowManager;

public class StartGameAction implements ActionListener {

    private NewGameMenuScreen offlineMenu;
    private WindowManager windowManager;

    public StartGameAction(NewGameMenuScreen offlineMenu, WindowManager windowManager) {
        this.offlineMenu = offlineMenu;
        this.windowManager = windowManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[Action] Botão JOGAR detectado! Processando dados da partida...");

        if (offlineMenu != null && windowManager != null) {
            // 1. Extrai todos os nomes do menu (usando os getters que adicionamos no menu)
            String p1Name = offlineMenu.getPlayer1Name();
            String p2Name = offlineMenu.getCPU1Name(); // Slot 2 (Computador 2)
            String p3Name = offlineMenu.getCPU2Name(); // Slot 3 (Computador 1)
            String p4Name = offlineMenu.getCPU3Name(); // Slot 4 (Computador 3)

            // 2. Extrai todas as cores selecionadas
            String p1Color = offlineMenu.getPlayer1Color();
            String p2Color = offlineMenu.getCPU1Color();
            String p3Color = offlineMenu.getCPU2Color();
            String p4Color = offlineMenu.getCPU3Color();

            // 3. Extrai a dificuldade
            String difficulty = offlineMenu.getCPUDifficulty();

            System.out.println("[Action] Enviando dados para o WindowManager...");

            // 4. Envia tudo para o WindowManager iniciar a tela do jogo
            windowManager.startOfflineGameMode(
                p1Name, p1Color,
                p2Name, p2Color,
                p3Name, p3Color,
                p4Name, p4Color,
                difficulty
            );
        }
    }
}
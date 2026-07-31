package actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
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
            // 1. Resposta visual INSTANTÂNEA: altera o cursor e desabilita o botão temporariamente
            if (e.getSource() instanceof JComponent) {
                JComponent sourceComp = (JComponent) e.getSource();
                sourceComp.setEnabled(false);
                sourceComp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }

            // 2. Extrai todos os nomes do menu
            String p1Name = offlineMenu.getPlayer1Name();
            String p2Name = offlineMenu.getCPU1Name(); // Slot 2 (Computador 2)
            String p3Name = offlineMenu.getCPU2Name(); // Slot 3 (Computador 1)
            String p4Name = offlineMenu.getCPU3Name(); // Slot 4 (Computador 3)

            // 3. Extrai todas as cores selecionadas
            String p1Color = offlineMenu.getPlayer1Color();
            String p2Color = offlineMenu.getCPU1Color();
            String p3Color = offlineMenu.getCPU2Color();
            String p4Color = offlineMenu.getCPU3Color();

            // 4. Extrai a dificuldade
            String difficulty = offlineMenu.getCPUDifficulty();

            System.out.println("[Action] Agendando criação da tela de jogo...");

            // 5. Adia a troca pesada da tela para o próximo ciclo de renderização do Swing.
            // Isso permite que o botão registre o clique na tela sem travar a interface.
            SwingUtilities.invokeLater(() -> {
                windowManager.startOfflineGameMode(
                    p1Name, p1Color,
                    p2Name, p2Color,
                    p3Name, p3Color,
                    p4Name, p4Color,
                    difficulty
                );
            });
        }
    }
}
package actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gui.windows.WindowManager; // Certifique-se de importar o seu WindowManager corretamente

public class NewGameMenuAction implements ActionListener {

    private WindowManager windowManager;

    // O construtor recebe o gerenciador para sabermos onde dar a ordem de abertura
    public NewGameMenuAction(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[Action] Botão detectado! Solicitando ao WindowManager a abertura do menu offline...");
        
        // Executa o método no seu gerenciador de janelas
        if (windowManager != null) {
            windowManager.abrirMenuOffline();
        } else {
            System.out.println("[Erro] WindowManager está nulo na Action!");
        }
    }
}
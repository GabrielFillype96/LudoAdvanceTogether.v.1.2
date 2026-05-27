
// Classe responsável pela ação de abrir o menu do modo de jogo offline
//Packages
package actions;
// Imports internos
import gui.windows.WindowManager; 
// Imports externos
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
            windowManager.openMenuOffline();
        } else {
            System.out.println("[Erro] WindowManager está nulo na Action!");
        }
    }
}
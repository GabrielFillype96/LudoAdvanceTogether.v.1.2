import gui.windows.MainScreenInterface;
import gui.windows.MenuInterface;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            JFrame janelaTeste = new JFrame("Teste de Fundo - Ludo");
            janelaTeste.setSize(900, 600);
            janelaTeste.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janelaTeste.setLocationRelativeTo(null);

            // Instancia o painel puxando ele do pacote correto
            MainScreenInterface telaFundo = new MainScreenInterface();
            telaFundo.setLayout(null);


            MenuInterface menuInterface = new MenuInterface();
            telaFundo.add(menuInterface);
            
            janelaTeste.add(telaFundo);
            janelaTeste.setVisible(true);

        });
    }
}

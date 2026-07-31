import control.ImageLoaderManager;
import gui.windows.MainScreenContainer;
import gui.windows.MainMenuScreen;
import gui.windows.WindowManager;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Main { 
    public static void main(String[] args) throws Exception {
        System.setProperty("sun.java2d.uiScale", "1");
            
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("Jogo de Ludo - Advance Together");

            ImageIcon appIcon = ImageLoaderManager.loadIcon("/assets/img/gameIcon.png");
            if (appIcon != null) {
                janela.setIconImage(appIcon.getImage());
            }

            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setUndecorated(true);
            janela.setResizable(false);
            janela.setExtendedState(JFrame.MAXIMIZED_BOTH);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            janela.setSize(screenSize);

            MainScreenContainer telaFundo = new MainScreenContainer();
            telaFundo.setLayout(null);
            telaFundo.setPreferredSize(screenSize);
            telaFundo.setBounds(0, 0, screenSize.width, screenSize.height);

            WindowManager gerenciadorJanelas = new WindowManager(telaFundo);

            MainMenuScreen menuPrincipal = new MainMenuScreen(gerenciadorJanelas);
            
            telaFundo.add(menuPrincipal);
            janela.add(telaFundo);
            
            janela.setLocationRelativeTo(null);
            janela.setVisible(true);
        });
    }
}
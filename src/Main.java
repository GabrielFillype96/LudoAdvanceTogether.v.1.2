import gui.windows.MainScreenInterface;
import gui.windows.MenuInterface;
import gui.windows.WindowManager;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            // Instancia a janela principal do jogo
            JFrame janela = new JFrame("Jogo de Ludo - Advance Together");
            janela.setSize(900, 600); 
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
            janela.setLocationRelativeTo(null); 
            janela.setResizable(false); 

            // 1. Instancia o painel base de fundo que carrega a imagem de 900x600
            MainScreenInterface telaFundo = new MainScreenInterface();
            telaFundo.setLayout(null);

            // 2. Cria o WindowManager passando a tela de fundo para gerir as transições
            WindowManager gerenciadorJanelas = new WindowManager(telaFundo);

            // 3. Instancia a MenuInterface principal (com os botões da esquerda)
            MenuInterface menuPrincipal = new MenuInterface(gerenciadorJanelas);
            
            // 4. Adiciona o menu principal à tela de fundo
            telaFundo.add(menuPrincipal);

            // 5. Adiciona tudo na janela e exibe
            janela.add(telaFundo);
            janela.setVisible(true);
        });
    }   
}
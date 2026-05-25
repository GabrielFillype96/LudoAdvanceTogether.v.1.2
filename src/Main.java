// Importações
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
            janela.setSize(900, 600); // Define o tamanho da janela para 900x600
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Garante que o programa feche quando a janela for fechada
            janela.setLocationRelativeTo(null); // Centraliza a janela na tela do usuário
            janela.setResizable(false); // Mantém o tamanho travado para os layouts absolutos

            // Instancia o painel base de fundo que carrega a imagem de 900x600
            MainScreenInterface telaFundo = new MainScreenInterface();
            telaFundo.setLayout(null);

            // Cria o WindowManager passando a tela de fundo
            WindowManager gerenciadorJanelas = new WindowManager(telaFundo);

            // Instancia a MenuInterface principal (com os botões da esquerda)
            MenuInterface menuPrincipal = new MenuInterface(gerenciadorJanelas);
            
            // Adiciona o menu principal à tela de fundo
            telaFundo.add(menuPrincipal);

            // Adiciona tudo na janela e exibe
            janela.add(telaFundo);
            janela.setVisible(true);
        });
    }   
}

// Importações internos
import gui.windows.MainScreenContainer;
import gui.windows.MainMenuScreen;
import gui.windows.WindowManager;

// Imports externos
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            // Instancia a janela principal do jogo
            JFrame janela = new JFrame("Jogo de Ludo - Advance Together");
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
            janela.setResizable(false);
            
            
            // 1. Instancia o painel base de fundo que carrega a imagem de 900x600
            MainScreenContainer telaFundo = new MainScreenContainer();
            telaFundo.setLayout(null);
            telaFundo.setPreferredSize(new java.awt.Dimension(1350, 900));

            // 2. Cria o WindowManager passando a tela de fundo para gerir as transições
            WindowManager gerenciadorJanelas = new WindowManager(telaFundo);

            // 3. Instancia a MainMenuScreen principal (com os botões da esquerda)
            MainMenuScreen menuPrincipal = new MainMenuScreen(gerenciadorJanelas);
            
            // 4. Adiciona o menu principal à tela de fundo
            telaFundo.add(menuPrincipal);

            // 5. Adiciona tudo na janela e exibe
            janela.add(telaFundo);
            janela.pack();
            java.awt.GraphicsConfiguration gc = janela.getGraphicsConfiguration();
            java.awt.Rectangle limitesTela = gc.getBounds();
            
            // 2. Pega as "rebarbas" da tela (tamanho exato da barra de tarefas do Windows)
            java.awt.Insets rebarbasTaskbar = java.awt.Toolkit.getDefaultToolkit().getScreenInsets(gc);

            // 3. Calcula o espaço útil real que sobrou na tela
            int larguraUtil = limitesTela.width - rebarbasTaskbar.left - rebarbasTaskbar.right;
            int alturaUtil = limitesTela.height - rebarbasTaskbar.top - rebarbasTaskbar.bottom;

            // 4. Calcula a posição centralizada dentro desse espaço útil
            int x = rebarbasTaskbar.left + (larguraUtil - janela.getWidth()) / 2;
            int y = rebarbasTaskbar.top + (alturaUtil - janela.getHeight()) / 2;

            // 5. SE o jogo for alto demais e o rodapé encostar na barra de tarefas, 
            // força a janela a subir e colar no topo da tela útil
            if (y + janela.getHeight() > limitesTela.height - rebarbasTaskbar.bottom) {
                y = rebarbasTaskbar.top; 
            }

            // Define a nova posição corrigida da janela
            janela.setLocation(x, y);
            janela.setVisible(true);
        });
    }   
}
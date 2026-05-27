// Classe responsável por criar um layer para receber os "mini-menu's" de opções do menu principal
//Packages
package gui.windows;
// Imports intenos

//Imports externos
import javax.swing.JPanel;


public class SubMenuContainer extends JPanel {
    // Variáveis
    // Variável para armazenar a referência ao WindowManager, que é o responsável por controlar as telas do jogo
    private WindowManager windowManager; 
    private int menuOptionsPanelWidth = 420; // Define a largura do painel de opções do menu
    private int menuOptionsPanelHeight = 280; // Define a altura do painel de opções
    
    public SubMenuContainer(WindowManager windowManager) {
        this.windowManager = windowManager;
        // Configurações iniciais do painel que irá receber os mini-menu's de opções do menu principal
        setOpaque(true); // Deixa o layer transparente para mostrar o fundo
        setLayout(null); // Layout absoluto para controle total dos pixels de espaçamento
        setBounds(0, 0, menuOptionsPanelWidth, menuOptionsPanelHeight); // Define o tamanho do painel de opções do menu para cobrir toda a área do menu principal
        

    }


}

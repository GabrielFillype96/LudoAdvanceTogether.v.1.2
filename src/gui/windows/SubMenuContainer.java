// Classe responsável por criar um layer para receber os "mini-menu's" de opções do menu principal
//Packages
package gui.windows;
// Imports internos
//Imports externos
import javax.swing.JPanel;

public class SubMenuContainer extends JPanel {
    // Variáveis
    // Variável para armazenar a referência ao WindowManager, que é o responsável por controlar as telas do jogo       
    private static final int menuOptionsPanelWidth = 535; // Define a largura do painel de opções do menu
    private static final int menuOptionsPanelHeight = 420; // Define a altura do painel de opções
    
    // Construtor da classe SubMenuContainer, que recebe o WindowManager para poder acessar e realizar a troca de telas
    public SubMenuContainer(WindowManager windowManager) {
        // Configurações iniciais do painel que irá receber os mini-menu's de opções do menu principal
        setOpaque(false); // Deixa o layer transparente para mostrar o fundo
        setLayout(null); // Layout absoluto para controle total dos pixels de espaçamento
        setBounds(0, 0, menuOptionsPanelWidth, menuOptionsPanelHeight); // Define o tamanho do painel de opções do menu para cobrir toda a área do menu principal
    }

    // Método para exibir um mini-menu específico dentro do painel de opções do menu principal
    public void displaySubMenu(JPanel miniMenu) {
        this.removeAll(); // Remove qualquer mini-menu que já esteja aberto para garantir que apenas um mini-menu seja exibido por vez
        miniMenu.setBounds(0, 0, menuOptionsPanelWidth, menuOptionsPanelHeight); // Define o tamanho do mini-menu para cobrir toda a área do painel de opções
        this.add(miniMenu); // Adiciona o mini-menu ao painel de opções
        this.revalidate(); // Atualiza o layout do painel para garantir que o novo mini-menu seja exibido corretamente
        this.repaint(); // Repaint para garantir que o painel seja redesenhado com o novo mini-menu visível
    }

    // Métodos getters para obter as dimensões do painel de opções do menu
    public int getMenuOptionsPanelWidth() {
        return menuOptionsPanelWidth;
    }
    public int getMenuOptionsPanelHeight() {
        return menuOptionsPanelHeight;  
    }   

}




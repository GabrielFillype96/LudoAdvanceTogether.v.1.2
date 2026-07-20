// Classe responsável por criar um layer para receber os "mini-menu's" de opções do menu principal

// Packages
package gui.windows;

// Imports externos
import java.awt.Rectangle;
import javax.swing.JPanel;

public class SubMenuContainer extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private static final double SCALE = 1.5;   
    private static final Rectangle SUB_MENU_CONTAINER_BOUNDS = new Rectangle( // Dimensões fixas para o painel de opções do menu principal onde os mini-menu's são exibidos
        (int) (0 * SCALE), 
        (int) (0 * SCALE), 
        (int) (560 * SCALE),
        (int) (460 * SCALE)
    );

    /**
    * @param WindowManager Gerenciador da troca de telas
    * Construtor da classe "SubMenuContainer" que recebe o "WindowManager" para poder acessar e realizar a troca de telas
    */ 
    public SubMenuContainer(WindowManager windowManager) {
        // Configurações iniciais do painel que irá receber os mini-menu's de opções do menu principal
        setOpaque(false); // Deixa o layer transparente para mostrar o fundo
        setLayout(null); // Layout absoluto para controle total dos pixels de espaçamento
        setBounds(SUB_MENU_CONTAINER_BOUNDS); // Define o tamanho do painel de opções do menu para cobrir toda a área do menu principal
    }

    // Método para exibir um mini-menu específico dentro do painel de opções do menu principal
    public void displaySubMenu(JPanel miniMenu) {
        this.removeAll(); // Remove qualquer mini-menu que já esteja aberto para garantir que apenas um mini-menu seja exibido por vez
        miniMenu.setBounds(SUB_MENU_CONTAINER_BOUNDS); // Define o tamanho do mini-menu para cobrir toda a área do painel de opções
        this.add(miniMenu); // Adiciona o mini-menu ao painel de opções
        this.revalidate(); // Atualiza o layout do painel para garantir que o novo mini-menu seja exibido corretamente
        this.repaint(); // Repaint para garantir que o painel seja redesenhado com o novo mini-menu visível
    }

    public void exibirSobreMenu() {
        displaySubMenu(new AboutSubMenuPanel());
    }

    // Método getter para acessar as dimensões da tela principal
    public static Rectangle getSubMenuContainerBounds() {
        return SUB_MENU_CONTAINER_BOUNDS;
    }  

}




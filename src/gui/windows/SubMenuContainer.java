// Packages
package gui.windows;

// Imports externos
import java.awt.Rectangle;
import javax.swing.JPanel;

public class SubMenuContainer extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private static final double SCALE = 1.5;   
    // Dimensões expandidas para acompanhar o novo tamanho do menu "Sobre"
    private static final Rectangle SUB_MENU_CONTAINER_BOUNDS = new Rectangle(
        0, 
        0, 
        (int) (600 * SCALE),
        (int) (530 * SCALE)
    );

    public SubMenuContainer(WindowManager windowManager) {
        setOpaque(false);
        setLayout(null);
        setBounds(SUB_MENU_CONTAINER_BOUNDS);
    }

    public void displaySubMenu(JPanel miniMenu) {
        this.removeAll();
        miniMenu.setBounds(SUB_MENU_CONTAINER_BOUNDS); // Agora aplica o tamanho correto de 600x530
        this.add(miniMenu);
        this.revalidate();
        this.repaint();
    }

    public void exibirSobreMenu() {
        displaySubMenu(new AboutSubMenuPanel());
    }

    public static Rectangle getSubMenuContainerBounds() {
        return SUB_MENU_CONTAINER_BOUNDS;
    }  
}
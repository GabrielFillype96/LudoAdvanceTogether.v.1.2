// Classe responsável por criar a interface do menu principal do jogo

package gui.windows;

// Import internos
import gui.components.buttons.*;
import actions.NewGameMenuAction;

// Imports externos
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JPanel;

public class MainMenuScreen extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private SubMenuContainer subMenuContainer; 
    private static final double SCALE = 1.5; 
    private static final Rectangle BUTTON_NAVIGATION_CONTAINER_BOUNDS = new Rectangle( 
        (int) (0 * SCALE), 
        (int) (35 * SCALE), 
        (int) (300 * SCALE), 
        (int) (280 * SCALE)
    ); 

    // Construtor da classe MainMenuScreen
    public MainMenuScreen(WindowManager windowManager) {
        setOpaque(false);  
        setLayout(null); 
        setBounds(MainScreenContainer.getMainScreenContainerBounds()); 
        
        // Cria o layer transparente das opções do menu
        JPanel buttonNavigationPanel = new JPanel(new GridBagLayout());
        buttonNavigationPanel.setOpaque(false); 
        buttonNavigationPanel.setBounds(BUTTON_NAVIGATION_CONTAINER_BOUNDS); 

        // Configuração do grid
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.gridx = 0; 
        gbc.gridy = GridBagConstraints.RELATIVE; 
        gbc.fill = GridBagConstraints.NONE; 
        gbc.insets = new Insets( 
            (int) (7 * SCALE), 
            (int) (0 * SCALE), 
            (int) (7 * SCALE), 
            (int) (0 * SCALE)
        ); 

        // Instância dos botões
        NewGameButton btnNewGame = new NewGameButton(); 
        ConnectionButton btnConnection = new ConnectionButton(); 
        AboutButton btnAbout = new AboutButton(); 
        ExitButton btnExit = new ExitButton(); 

        // Adiciona os botões ao grid
        buttonNavigationPanel.add(btnNewGame, gbc);
        buttonNavigationPanel.add(btnConnection, gbc);
        buttonNavigationPanel.add(btnAbout, gbc);
        buttonNavigationPanel.add(btnExit, gbc);

        add(buttonNavigationPanel);

        // Ações de clique dos botões
        btnNewGame.addActionListener(new NewGameMenuAction(windowManager));
        btnConnection.addActionListener(e -> windowManager.openLobbyMultiplayer());
        btnAbout.addActionListener(e -> subMenuContainer.exibirSobreMenu());
        btnExit.addActionListener(e -> System.exit(0)); // Encerra a aplicação ao clicar em SAIR

        // SubMenuContainer
        subMenuContainer = new SubMenuContainer(windowManager);
        subMenuContainer.setBounds(
            (int) (320 * SCALE), 
            (int) (50 * SCALE), 
            (int) (SubMenuContainer.getSubMenuContainerBounds().width * SCALE), 
            (int) (SubMenuContainer.getSubMenuContainerBounds().height * SCALE)
        );
        
        add(subMenuContainer); 
    }

    public SubMenuContainer getSubMenuContainer() {
        return subMenuContainer;
    }
}
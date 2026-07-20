// Classe responsável por criar a interface do menu principal do jogo

// Packages
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
    private SubMenuContainer subMenuContainer; // Painel para conter os mini-menu's abertos pelos botões do menu principal
    private static final double SCALE = 1.5; // Fator de escala para ajustar o tamanho dos componentes
    private static final Rectangle BUTTON_NAVIGATION_CONTAINER_BOUNDS = new Rectangle( 
        (int) (0 * SCALE), 
        (int) (35 * SCALE), 
        (int) (300 * SCALE), 
        (int) (280 * SCALE)
    ); 

    // Construtor da classe MainMenuScreen
    public MainMenuScreen(WindowManager windowManager) {
        setOpaque(false);  // Mantém o fundo transparente para a imagem de fundo do jogo aparecer
        setLayout(null); // Layout nulo torna o painel absoluto 
        setBounds(MainScreenContainer.getMainScreenContainerBounds()); 
        
        // Cria o layer transparente onde ficarão alocadas as opções do menu principal
        JPanel buttonNavigationPanel = new JPanel(new GridBagLayout());
        
        buttonNavigationPanel.setOpaque(false); 
        buttonNavigationPanel.setBounds(BUTTON_NAVIGATION_CONTAINER_BOUNDS); 

        // Configuração do grid das opções do menu (GridBagConstraints)
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.gridx = 0; // Tudo na mesma coluna
        gbc.gridy = GridBagConstraints.RELATIVE; // Próximo botão vai para a linha de baixo
        gbc.fill = GridBagConstraints.NONE; 
        gbc.insets = new Insets( 
            (int) (7 * SCALE), 
            (int) (0 * SCALE), 
            (int) (7 * SCALE), 
            (int) (0 * SCALE)
        ); 

        // Criação dos botões (sem o ConfigButton)
        NewGameButton btnNewGame = new NewGameButton(); 
        ConnectionButton btnConnection = new ConnectionButton(); 
        AboutButton btnAbout = new AboutButton(); 
        ExitButton btnExit = new ExitButton(); 

        // Adiciona os botões ao grid
        buttonNavigationPanel.add(btnNewGame, gbc);
        buttonNavigationPanel.add(btnConnection, gbc);
        buttonNavigationPanel.add(btnAbout, gbc);
        buttonNavigationPanel.add(btnExit, gbc);

        // Adiciona o painel de opções ao painel principal
        add(buttonNavigationPanel);

        // Ações de clique
        btnNewGame.addActionListener(new NewGameMenuAction(windowManager));
        btnConnection.addActionListener(e -> windowManager.openLobbyMultiplayer());

        // Instancia o painel de sub-menus
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
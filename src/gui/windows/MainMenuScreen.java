package gui.windows;

// Imports internos
import gui.components.buttons.*;
import actions.NewGameMenuAction;

// Imports externos
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JPanel;

public class MainMenuScreen extends JPanel {
    private SubMenuContainer subMenuContainer; 
    private static final double SCALE = 1.5; 
    private static final Rectangle BUTTON_NAVIGATION_CONTAINER_BOUNDS = new Rectangle( 
        (int) (10 * SCALE), 
        (int) (35 * SCALE), 
        (int) (240 * SCALE), 
        (int) (280 * SCALE)
    ); 

    // Referências dos botões promovidas a atributos da classe para gerenciar o estado
    private final NewGameButton btnNewGame; 
    private final ConnectionButton btnConnection; 
    private final AboutButton btnAbout; 
    private final ExitButton btnExit; 

    public MainMenuScreen(WindowManager windowManager) {
        setOpaque(false);  
        setLayout(null); 
        setBounds(MainScreenContainer.getMainScreenContainerBounds()); 
        
        // Layer das opções do menu principal
        JPanel buttonNavigationPanel = new JPanel(new GridBagLayout());
        buttonNavigationPanel.setOpaque(false); 
        buttonNavigationPanel.setBounds(BUTTON_NAVIGATION_CONTAINER_BOUNDS); 

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
        btnNewGame = new NewGameButton(); 
        btnConnection = new ConnectionButton(); 
        btnAbout = new AboutButton(); 
        btnExit = new ExitButton(); 

        buttonNavigationPanel.add(btnNewGame, gbc);
        buttonNavigationPanel.add(btnConnection, gbc);
        buttonNavigationPanel.add(btnAbout, gbc);
        buttonNavigationPanel.add(btnExit, gbc);

        add(buttonNavigationPanel);

        // SubMenuContainer posicionamento ajustado
        subMenuContainer = new SubMenuContainer(windowManager);
        Rectangle bounds = SubMenuContainer.getSubMenuContainerBounds();
        
        subMenuContainer.setBounds(
            (int) (260 * SCALE), 
            (int) (25 * SCALE), 
            bounds.width, 
            bounds.height
        );
        
        add(subMenuContainer); 

        // --- AÇÕES DOS BOTÕES COM SINCRO DE SELEÇÃO VISUAL ---
        NewGameMenuAction newGameAction = new NewGameMenuAction(windowManager);
        
        btnNewGame.addActionListener(e -> {
            selecionarBotao(btnNewGame);
            newGameAction.actionPerformed(e);
        });

        btnConnection.addActionListener(e -> {
            selecionarBotao(btnConnection);
            windowManager.openLobbyMultiplayer();
        });

        btnAbout.addActionListener(e -> {
            selecionarBotao(btnAbout);
            subMenuContainer.exibirSobreMenu();
        });

        btnExit.addActionListener(e -> System.exit(0));
    }

    /**
     * Garante que apenas o botão do submenu ativo permaneça com o destaque dourado.
     */
    private void selecionarBotao(CustomButton botaoAlvo) {
        btnNewGame.setSelecionado(btnNewGame == botaoAlvo);
        btnConnection.setSelecionado(btnConnection == botaoAlvo);
        btnAbout.setSelecionado(btnAbout == botaoAlvo);
        btnExit.setSelecionado(btnExit == botaoAlvo);
    }

    public SubMenuContainer getSubMenuContainer() {
        return subMenuContainer;
    }
}
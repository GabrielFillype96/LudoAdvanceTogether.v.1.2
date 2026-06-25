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
    private static final double SCALE = 1.5; // Fator de escala para ajustar o tamanho dos componentes (pode ser ajustado conforme necessário)
    private static final Rectangle BUTTON_NAVIGATION_CONTAINER_BOUNDS = new Rectangle( 
        // Dimensões fixas para o painel dos botões do menu principal
        (int) (20 * SCALE), 
        (int) (35 * SCALE), 
        (int) (300 * SCALE), 
        (int) (280 * SCALE)
    ); 


    // Construtor da classe MainMenuScreen, que recebe o WindowManager para gerenciar as transições entre telas
    public MainMenuScreen(WindowManager windowManager) {
        setOpaque(false);  // Mantém o fundo transparente para a imagem de fundo do jogo aparecer
        setLayout(null); // Layout nulo torna o painel absoluto 
        setBounds(MainScreenContainer.getMainScreenContainerBounds()); // Define o tamanho do painel do menu para cobrir toda a área da janela
        
        // Cria o layer transparente onde ficará alocado as opções do menu principal (botões)
        JPanel buttonNavigationPanel = new JPanel(new GridBagLayout()); // Instancia o objeto "buttonNavigationPanel"
        
        // Configuração do painel
        buttonNavigationPanel.setOpaque(false); // Mantém transparente para o fundo do jogo aparecer atrás
        buttonNavigationPanel.setBounds(BUTTON_NAVIGATION_CONTAINER_BOUNDS); // Define a posição (canto esquerdo) e o tamanho da box que irá conter os botões

        // Configuração do grid das opções do menu (GridBagConstraints)
        GridBagConstraints gbc = new GridBagConstraints(); 
        gbc.gridx = 0; // Tudo na mesma coluna (coluna 0)
        gbc.gridy = GridBagConstraints.RELATIVE; // O Java joga o próximo botão automaticamente na linha de baixo
        gbc.fill = GridBagConstraints.NONE; // Não estica a imagem do botão
        gbc.insets = new Insets( // Espaçamento de 7 pixels acima e abaixo de cada botão
            (int) (7 * SCALE), 
            (int) (0 * SCALE), 
            (int) (7 * SCALE), 
            (int) (0 * SCALE)
        ); 

        // Criação dos botões
        NewGameButton btnNewGame = new NewGameButton(); // Instancia o botão "btnNewGame" através da classe "NewGameButton"
        ConfigButton btnConfig = new ConfigButton(); // Instancia o botão "btnConfig" através da classe "ConfigButton"
        ConnectionButton btnConnection = new ConnectionButton(); // Instancia o botão "btnConnection" através da classe "ConnectionButton"
        AboutButton btnAbout = new AboutButton(); // Instancia o botão "btnAbout" através da classe "AboutButton"
        ExitButton btnExit = new ExitButton(); // Instancia o botão "btnExit" através da classe "ExitButton"

        // Adiciona os botões ao grid criado.
        buttonNavigationPanel.add(btnNewGame, gbc);
        buttonNavigationPanel.add(btnConfig, gbc);
        buttonNavigationPanel.add(btnConnection, gbc);
        buttonNavigationPanel.add(btnAbout, gbc);
        buttonNavigationPanel.add(btnExit, gbc);

        // Adiciona o painel de opções do menu (com os botões) ao painel principal do menu
        add(buttonNavigationPanel);

        // Adiciona a ação de clique para o botão "Novo Jogo", que irá abrir o menu de configuração do modo de jogo offline
        btnNewGame.addActionListener(new NewGameMenuAction(windowManager));

        // Instancia o painel de mini-menu's (SubMenuContainer) e o adiciona ao painel principal do menu
        subMenuContainer = new SubMenuContainer(windowManager);
        // Define a posição e o tamanho do painel de mini-menu's (SubMenuContainer) para que ele apareça ao lado direito dos botões do menu principal
        subMenuContainer.setBounds(
            (int) (320 * SCALE), 
            (int) (50 * SCALE), 
            (int) (SubMenuContainer.getSubMenuContainerBounds().width * SCALE), 
            (int) (SubMenuContainer.getSubMenuContainerBounds().height * SCALE)
        );
        
        add(subMenuContainer); // Adiciona o painel de mini-menu's ao painel principal do menu
    }

    // Método para obter a referência ao painel de opções do menu principal, onde os mini-menu's são exibidos
    public SubMenuContainer getSubMenuContainer() {
        return subMenuContainer;
    }
}
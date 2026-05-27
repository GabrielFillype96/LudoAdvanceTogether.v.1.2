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
import javax.swing.JPanel;

public class MenuInterface extends JPanel {
    // Variáveis de instância para os componentes do menu principal
    private JPanel buttonNavigationPanel; // Painel para conter os botões do menu principal
    private GridBagConstraints gbc; // Variável para armazenar as configurações do GridBagLayout para os botões do menu

    // Variáveis para os botões do menu principal
    private NewGameButton btnNewGame;
    private ConfigButton btnConfig;
    private ConnectionButton btnConnection;
    private AboutButton btnAbout;
    private ExitButton btnExit;
    // Variáveis para os mini-menu's abertos pelos botões do menu principal
    private SubMenuContainer subMenuContainer; // Painel para conter os mini-menu's abertos pelos botões do menu principal

    // Variáveis para as dimensões e posicionamento do painel do menu e do painel de navegação dos botões
    private int menuPanelWidth = 900; // Define a largura do painel do menu para cobrir toda a área da janela
    private int menuPanelHeight = 600; // Define a altura do painel do menu para cobrir toda a área da janela
    private int buttonNavigationPanelWidth = 300; // Define a largura do painel de opções do menu para conter os botões
    private int buttonNavigationPanelHeight = 280; // Define a altura do painel de opções do menu para conter os botões
    private int buttonNavigationPanelX = 20; // Define a posição X do painel de opções do menu 
    private int buttonNavigationPanelY = 35; // Define a posição Y do painel de opções do menu

    // Construtor da classe MenuInterface, que recebe o WindowManager para gerenciar as transições entre telas
    public MenuInterface(WindowManager windowManager) {
        // --- CONFIGURAÇÕES REFERENTES AO PAINEL DO MENU PRINCIPAL --- //
        // Configurações iniciais do painel do menu
        setOpaque(false);  // Mantém o fundo transparente para a imagem de fundo do jogo aparecer
        setLayout(null); // Define o layout como absoluto para posicionar os componentes manualmente
        setBounds(0, 0, menuPanelWidth, menuPanelHeight); // Define o tamanho do painel do menu para cobrir toda a área da janela
        
        // Cria o layer transparente onde ficará alocado as opções do menu principal (botões)
        buttonNavigationPanel = new JPanel(new GridBagLayout()); // Instancia o objeto "buttonNavigationPanel"
        
        // Configuração do painel
        buttonNavigationPanel.setOpaque(false); // Mantém transparente para o fundo do jogo aparecer atrás
        buttonNavigationPanel.setBounds(buttonNavigationPanelX, buttonNavigationPanelY, buttonNavigationPanelWidth, buttonNavigationPanelHeight); // Define a posição (canto esquerdo) e o tamanho da box que irá conter os botões

        // Configuração do grid das opções do menu (GridBagConstraints)
        gbc = new GridBagConstraints(); 
        gbc.gridx = 0; // Tudo na mesma coluna (coluna 0)
        gbc.gridy = GridBagConstraints.RELATIVE; // O Java joga o próximo botão automaticamente na linha de baixo
        gbc.fill = GridBagConstraints.NONE; // Não estica a imagem do botão
        gbc.insets = new Insets(7, 0, 7, 0); // Espaçamento de 10 pixels acima e abaixo de cada botão

        // Criação dos botões
        btnNewGame = new NewGameButton(); // Instancia o botão "btnNewGame" através da classe "NewGameButton"
        btnConfig = new ConfigButton(); // Instancia o botão "btnConfig" através da classe "ConfigButton"
        btnConnection = new ConnectionButton(); // Instancia o botão "btnConnection" através da classe "ConnectionButton"
        btnAbout = new AboutButton(); // Instancia o botão "btnAbout" através da classe "AboutButton"
        btnExit = new ExitButton();  // Instancia o botão "btnExit" através da classe "ExitButton"

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

        // --- CONFIGURAÇÕES REFERENTES AO PAINEL DOS MINI-MENUS ABERTOS PELO MENU PRINCIPAL --- //
        subMenuContainer = new SubMenuContainer(windowManager);
        subMenuContainer.setBounds(350, 260, 420, 280);
        add(subMenuContainer); // Adiciona o painel de mini-menu's ao painel principal do menu

    }





}
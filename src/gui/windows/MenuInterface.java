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
    // Variáveis dos botões
    private JPanel menuOptionsPanel;
    public static NewGameButton btnNewGame;
    private ConfigButton btnConfig;
    private ConnectionButton btnConnection;
    private AboutButton btnAbout;
    private ExitButton btnExit;
    private int menuPanelWidth = 900;
    private int menuPanelHeight = 600;
    private int menuOptionsPanelWidth = 300;
    private int menuOptionsPanelHeight = 280;
    private int menuOptionsPanelX = 20;
    private int menuOptionsPanelY = 35;

    // Construtor da classe MenuInterface, que recebe o WindowManager para gerenciar as transições entre telas
    public MenuInterface(WindowManager windowManager) {
        // Configurações iniciais do painel do menu
        setOpaque(false);  // Mantém o fundo transparente para a imagem de fundo do jogo aparecer
        setLayout(null); // Define o layout como absoluto para posicionar os componentes manualmente
        setBounds(0, 0, menuPanelWidth, menuPanelHeight); // Define o tamanho do painel do menu para cobrir toda a área da janela
        
        // Cria o layer transparente onde ficará alocado o menu
        menuOptionsPanel = new JPanel(new GridBagLayout()); // Instancia o objeto "menuOptionsPanel"
        
        // Configuração do painel
        setLayout(null); // Define o layout do layer do menu como absoluto para sobrepor a primeira camada de layer criada ("MainScreenInterface")
        setSize(menuPanelWidth, menuPanelHeight); // Define o tamanho do layer do menu
        menuOptionsPanel.setOpaque(false); // Mantém transparente para o fundo do jogo aparecer atrás
        menuOptionsPanel.setBounds(menuOptionsPanelX, menuOptionsPanelY, menuOptionsPanelWidth, menuOptionsPanelHeight); // Define a posição (canto esquerdo) e o tamanho da box que irá conter os botões

        // Configuração do grid das opções do menu (GridBagConstraints)
        GridBagConstraints gbc = new GridBagConstraints();
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
        menuOptionsPanel.add(btnNewGame, gbc);
        menuOptionsPanel.add(btnConfig, gbc);
        menuOptionsPanel.add(btnConnection, gbc);
        menuOptionsPanel.add(btnAbout, gbc);
        menuOptionsPanel.add(btnExit, gbc);

        // Adiciona o painel de opções do menu (com os botões) ao painel principal do menu
        add(menuOptionsPanel);

        // Adiciona a ação de clique para o botão "Novo Jogo", que irá abrir o menu de configuração do modo de jogo offline
        btnNewGame.addActionListener(new NewGameMenuAction(windowManager));

    }





}
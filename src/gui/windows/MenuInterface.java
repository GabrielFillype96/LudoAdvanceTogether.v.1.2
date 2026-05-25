package gui.windows;


import gui.components.buttons.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

import actions.NewGameMenuAction;

public class MenuInterface extends JPanel {
    // Variáveis dos botões
    private JPanel menuOptionsPanel;
    private WindowManager windowManager;
    private NewGameButton btnNewGame;
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
    


    public MenuInterface(WindowManager windowManager) {

        setOpaque(false); 
        setLayout(null);
        setBounds(0, 0, 900, 600);
        
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
        btnConfig = new ConfigButton();
        btnConnection = new ConnectionButton();
        btnAbout = new AboutButton();
        btnExit = new ExitButton(); 

        // Adiciona os botões ao grid criado.
        menuOptionsPanel.add(btnNewGame, gbc);
        menuOptionsPanel.add(btnConfig, gbc);
        menuOptionsPanel.add(btnConnection, gbc);
        menuOptionsPanel.add(btnAbout, gbc);
        menuOptionsPanel.add(btnExit, gbc);

        add(menuOptionsPanel);

        btnNewGame.addActionListener(new NewGameMenuAction(windowManager));

    }





}
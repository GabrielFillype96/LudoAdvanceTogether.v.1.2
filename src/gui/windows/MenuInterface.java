package gui.windows;

import gui.components.buttons.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MenuInterface extends JPanel {
    // Variáveis dos botões
    private JPanel menuOptionsPanel;
    private NewGameButton btnNewGame;
    private ConfigButton btnConfig;
    private ConnectionButton btnConnection;
    private AboutButton btnAbout;
    private ExitButton btnExit;
    private int menuPanelWidth = 900;
    private int menuPanelHeight = 600;

    public MenuInterface() {

        setOpaque(false); 
        setLayout(null);
        setBounds(0, 0, 900, 600);
        
        // Cria o layer transparente onde ficará alocado o menu
        menuOptionsPanel = new JPanel(new GridBagLayout()); // Instancia o objeto "menuOptionsPanel"
        
        // Configuração do painel
        setLayout(null); // Define o layout do layer do menu como absoluto para sobrepor a primeira camada de layer criada ("MainScreenInterface")
        setSize(menuPanelWidth, menuPanelHeight); // Define o tamanho do layer do menu
        menuOptionsPanel.setOpaque(false); // Mantém transparente para o fundo do jogo aparecer atrás
        menuOptionsPanel.setBounds(25, 50, 300, 450); // Define a posição (canto esquerdo) e o tamanho da box que irá conter os botões.

        // Configuração do grid das opções do menu (GridBagConstraints)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Tudo na mesma coluna (coluna 0)
        gbc.gridy = GridBagConstraints.RELATIVE; // O Java joga o próximo botão automaticamente na linha de baixo
        gbc.fill = GridBagConstraints.NONE; // Não estica a imagem do botão
        gbc.insets = new Insets(10, 0, 10, 0); // Espaçamento de 10 pixels acima e abaixo de cada botão

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

    }





}
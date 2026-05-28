// Classe responsável por criar a interface do menu do modo de jogo offline
// Packages
package gui.windows;
// Imports internos
import gui.components.SlotsIcon;
import gui.components.PlayerIdentifier;
import gui.components.SlotsName;
import gui.components.buttons.CustomButton;
import gui.components.buttons.PlayButton;
import actions.StartGameAction;
//Imports externos
import java.awt.*;
import javax.swing.*;

public class NewGameMenuInterface extends JPanel {
    // Variável para armazenar a referência ao WindowManager, que é o responsável por controlar as telas do jogo
    private WindowManager windowManager;
    // Variável para armazenar a referência à ação de iniciar o jogo, que é acionada ao clicar no botão "playBtn"
    private StartGameAction startGameAction; 
    // Variável para armazenar a referência ao painel de opções do menu principal, onde os mini-menu's são exibidos
    private SubMenuContainer subMenuContainer;

    // Variáveis de instância para as dimensões do menu
    private int offlineMenuWidth = 520; // Define a largura do menu do modo de jogo offline
    private int offlineMenuHeight = 420; // Define a altura do menu do modo de jogo offline

    // Variáveis para os componentes interativos do menu offline
    private JRadioButton rbEasy, rbMedium, rbHard; // Botões do tipo radio para a dificuldade
    private JTextField txtP1, txtCPU1, txtCPU2, txtCPU3; // Campos de texto para os nomes dos jogadores
    private JLabel lblDif; // Label para a seção de dificuldade
    private JLabel title; // Label para o título do menu offline
    private JLabel subTitle; // Label para a seção de inserção dos nomes dos jogadores
    
    // Variável para o botão de iniciar o jogo herdados da classe "CustomButton"
    private CustomButton playBtn; // Botão principal para iniciar o jogo;

    // Construtor que recebe o WindowManager para poder chamar as transições de tela
    public NewGameMenuInterface(WindowManager windowManager, SubMenuContainer subMenuContainer) {
        this.windowManager = windowManager;
        this.subMenuContainer = subMenuContainer;
        // Dimensões exatas para o GridBagLayout centralizar perfeitamente
        // Instancia (cria o objeto) da classe Dimension do Java Awt com as dimensões do menu offline
        Dimension offlineMenuSize = new Dimension(offlineMenuWidth, offlineMenuHeight); 
        //setSize(offlineMenuSize); // Define o tamanho do painel para as dimensões do menu offline
        setPreferredSize(offlineMenuSize); //
        setMinimumSize(offlineMenuSize);
        setMaximumSize(offlineMenuSize);
        
        setOpaque(false); // Deixa o layer transparente para mostrar o fundo 
        setLayout(null); // Layout absoluto para controle total dos pixels de espaçamento

        // Título do Menu
        // Instancia um JLabel com o texto "MODO JOGO OFFLINE" e centraliza o texto horizontalmente
        title = new JLabel("MODO JOGO OFFLINE", SwingConstants.CENTER); 
        // Define a fonte, cor e posição do título
        title.setFont(new Font("Serif", Font.BOLD, 22));
        title.setForeground(gui.theme.GameColors.GOLD_ACCENT);
        title.setBounds(0, 25, 520, 30); 
        add(title); // Adiciona o título ao painel

        // Seleção da dificuldade da CPU
        // Instancia um JLabel para a seção de dificuldade, alinhado à direita
        lblDif = new JLabel("Dificuldade da CPU:", SwingConstants.RIGHT);
        // Define a fonte, cor e posição do label de dificuldade
        lblDif.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblDif.setForeground(gui.theme.GameColors.GOLD_ACCENT);
        lblDif.setBounds(20, 70, 140, 25);
        add(lblDif); // Adiciona o label de dificuldade ao painel

        // Instancia os botões de rádio para as opções de dificuldade
        rbEasy = gui.components.buttons.DifficultyRadioButton.goldenBtnRd("Fácil");
        rbEasy.setBounds(180, 70, 70, 25);
        rbMedium = gui.components.buttons.DifficultyRadioButton.goldenBtnRd("Médio");
        rbMedium.setBounds(260, 70, 80, 25);
        rbMedium.setSelected(true);
        rbHard = gui.components.buttons.DifficultyRadioButton.goldenBtnRd("Difícil");
        rbHard.setBounds(350, 70, 80, 25);

        // O método ButtonGroup é utilizado para agrupar os botões de rádio, garantindo que apenas um possa ser selecionado por vez.
        ButtonGroup group = new ButtonGroup();
        // Adiciona os botões de rádio ao grupo para garantir a exclusividade da seleção
        group.add(rbEasy); 
        group.add(rbMedium); 
        group.add(rbHard);
        // Adiciona os botões de rádio ao painel para que sejam exibidos na interface
        add(rbEasy); 
        add(rbMedium); 
        add(rbHard);

        // Seção de slots para os nomes dos jogadores
        // Slots para os nomes dos jogadores (Preparados para receber imagens e com espaçamento adequado)
        subTitle = new JLabel("INSERIR NOMES DOS JOGADORES", SwingConstants.CENTER);
        subTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        subTitle.setForeground(gui.theme.GameColors.GOLD_ACCENT);
        subTitle.setBounds(0, 120, 520, 20);
        add(subTitle);

        /* * EXPLICAÇÃO DOS ESPAÇAMENTOS (X):
         * Número começa em X. Mede 35px de largura.
         * Damos +8px de espaço vazio (respiro para sua arte).
         * Campo de texto começa em X + 35 + 8.
         */
        
        // Adiciona ao menu o componente de identificação do jogador criado pela classe "PlayerIdentifier"
        add(PlayerIdentifier.squarePlayerIdentifier("1", 25, 155)); // ID 1
        add(PlayerIdentifier.squarePlayerIdentifier("2", 280, 155)); // ID 2
        add(PlayerIdentifier.squarePlayerIdentifier("3", 25, 210)); // ID 3
        add(PlayerIdentifier.squarePlayerIdentifier("4", 280, 210)); // ID 4

        // Cria o campo de texto para os jogadores utilizando a classe "SlotsName" e armazena a referência na variável.
        // Não é preciso criar um novo objeto pois a classe "SlotsName" já tem um método estático que retorna o JTextField pronto para uso.
        txtP1 = SlotsName.slotName(68, 155, 155, 35, true); // 45 + 35 + 8 = 88 --> P1
        txtCPU1 = SlotsName.slotName(328, 155, 155, 35, false); // 305 + 35 + 8 = 348 --> CPU 1
        txtCPU2 = SlotsName.slotName(68, 210, 155, 35, false); // 45 + 35 + 8 = 88 --> CPU 2
        txtCPU3 = SlotsName.slotName(328, 210, 155, 35, false); // 305 + 35 + 8 = 348 --> CPU 3
        
        // Define o texto padrão dos campos
        txtP1.setText("Digite seu nome"); // Player 1
        txtCPU1.setText("Computador 1"); // CPU 1
        txtCPU2.setText("Computador 2"); // CPU 2
        txtCPU3.setText("Computador 3"); // CPU 3

        // Adiciona os campos de texto ao painel para que sejam exibidos na interface
        add(txtP1);
        add(txtCPU1);
        add(txtCPU2);
        add(txtCPU3);
        
        // Adiciona os ícones laterais
        add(SlotsIcon.slotIconLabel("👤", 225, 155)); // Aqui entrará sua arte de Pessoa
        add(SlotsIcon.slotIconLabel("💻", 485, 155)); // Aqui entrará sua arte de Engrenagem
        add(SlotsIcon.slotIconLabel("💻", 225, 210));
        add(SlotsIcon.slotIconLabel("💻", 485, 210));

        // Cria o botão de iniciar o jogo utilizando a classe "PlayButton" e armazena a referência na variável "playBtn"
        playBtn = new PlayButton();
        playBtn.setBounds(180, 300, 200, 45); // Define a posição e o tamanho do botão
        startGameAction = new StartGameAction(this, this.windowManager); // Cria a ação de iniciar o jogo
        playBtn.addActionListener(startGameAction); // Adiciona a ação de iniciar o jogo ao botão "playBtn"
        add(playBtn);
    }

    // Método para desenhar o fundo personalizado do menu offline
    // Sobrescreve o método "paintComponent" para desenhar o fundo personalizado do menu offline
    // @Override indica que o método "paintComponent" está sendo sobrescrito da classe pai (JPanel). Serve como uma espécie de "guarda-costas" para garantir que estamos realmente sobrescrevendo um método existente e não criando um novo método por engano.
    @Override
    // O método "paintComponent" é chamado sempre que o painel precisa ser redesenhado, permitindo que personalizemos a aparência do fundo do menu offline.
    // O método "paintComponent" é "protected" para impedir que ele seja chamado por engano em outras classes.
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Placa roxa de fundo
        g2.setColor(gui.theme.GameColors.PURPLE_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
        
        // Moldura dourada externa
        g2.setColor(gui.theme.GameColors.GOLD_ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 24, 24);
    }

    // Método para obter a referência ao painel de opções do menu principal, onde os mini-menu's são exibidos
    public SubMenuContainer getSubMenuContainer() {
        return subMenuContainer;
    }
    // Método para obter o nome do jogador inserido no campo de texto "txtP1"
    public String getPlayerName() {
        // Retorna o texto do campo do P1 (certifique-se de que a variável txtP1 seja visível aqui)
        return txtP1.getText();
    }
    // Método para obter a dificuldade selecionada pelos botões de rádio
    public String getCPUDifficulty() {
        // Verifica qual rádio botão está marcado no momento do clique
        if (rbEasy.isSelected()) return "FÁCIL";
        if (rbHard.isSelected()) return "DIFÍCIL";
        return "MÉDIO"; // Caso padrão
    }
}
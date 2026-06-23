// Classe responsável por criar a interface do menu do modo de jogo offline

// Packages
package gui.windows;

// Imports internos
import gui.components.SlotsIcon;
import gui.components.PlayerIdentifier;
import gui.components.SlotsName;
import gui.components.buttons.CustomButton;
import gui.components.buttons.PlayButton;
import gui.components.buttons.DifficultyRadioButton;
import actions.StartGameAction;
import gui.theme.GameColors;

//Imports externos
import java.awt.*;
import javax.swing.*;

public class NewGameMenuScreen extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    // Variável para armazenar a referência ao WindowManager, que é o responsável por controlar as telas do jogo
    private WindowManager windowManager;
    // Variável para armazenar a referência ao painel de opções do menu principal, onde os mini-menu's são exibidos
    private SubMenuContainer subMenuContainer;

    // Variável de escala para qualquer medida
    private static final double SCALE = 1.5; // Fator de escala para ajustar o tamanho dos componentes (pode ser ajustado conforme necessário)

    // Instancia (cria o objeto) da classe Dimension do Java Awt com as dimensões do menu offline
    private static final Dimension OFFLINE_MENU_DIMENSION = new Dimension( // Dimensões fixas para o menu offline
        (int) (520 * SCALE), 
        (int) (420 * SCALE)
    );

    // Variáveis para os componentes interativos do menu offline
    private JRadioButton rbEasy, rbMedium, rbHard; // Botões do tipo radio para a dificuldade
    // Depois irá precisar de um método getter para pegar o nome dos demais players 
    private JTextField txtP1, txtCPU1, txtCPU2, txtCPU3; // Campos de texto para os nomes dos jogadores
    
    
    /** 
    * @param WindowManager Gerenciador responsável pela troca de tela
    * @param SubMenuContainer Container que irá guardar o subMenu
    */
    // Construtor que recebe o WindowManager para poder chamar as transições de tela
    public NewGameMenuScreen(WindowManager windowManager, SubMenuContainer subMenuContainer) {
        this.windowManager = windowManager;
        this.subMenuContainer = subMenuContainer;
        // Dimensões exatas para o GridBagLayout centralizar perfeitamente
        //setSize(offlineMenuSize); // Define o tamanho do painel para as dimensões do menu offline
        setPreferredSize(OFFLINE_MENU_DIMENSION); // 
        setMinimumSize(OFFLINE_MENU_DIMENSION);
        setMaximumSize(OFFLINE_MENU_DIMENSION);
        
        setOpaque(false); // Deixa o layer transparente para mostrar o fundo 
        setLayout(null); // Layout absoluto para controle total dos pixels de espaçamento

        // Título do sub-menu
        // Instancia um JLabel com o texto "MODO JOGO OFFLINE" e centraliza o texto horizontalmente
        JLabel title = new JLabel(
            "MODO JOGO OFFLINE", 
            SwingConstants.CENTER
        ); 

        // Define a fonte, cor e posição do título
        title.setFont(new Font(
            "Serif", // Fonte Serif
            Font.BOLD, // Estilo da fonte em negrito
            (int) (22 * SCALE) // Tamanho da fonte 22
        ));
        title.setForeground(GameColors.GOLD_ACCENT); // Cor do título em dourado fosco
        title.setBounds(
            0, 
            (int) (25 * SCALE), 
            (int) (520 * SCALE), 
            (int) (30 * SCALE)
        ); 
        add(title); // Adiciona o título ao painel

        // Seleção da dificuldade da CPU
        // Instancia um JLabel para a seção de dificuldade, alinhado à direita
        JLabel lblDif = new JLabel(
            "Dificuldade da CPU:", 
            SwingConstants.RIGHT
        );

        // Define a fonte, cor e posição do label de dificuldade
        lblDif.setFont(new Font(
            "SansSerif", // Fonte SansSerif
            Font.BOLD, // Estilo da fonte em negrito
            (int) (13 * SCALE)// Tamanho da fonte 13
        ));
        lblDif.setForeground(GameColors.GOLD_ACCENT);
        lblDif.setBounds(
            (int) (20 * SCALE), 
            (int) (70 * SCALE), 
            (int) (140 * SCALE), 
            (int) (25 * SCALE)
        );
        add(lblDif); // Adiciona o label de dificuldade ao painel

        // Instancia os botões de rádio para as opções de dificuldade e define a posição e tamanho
        rbEasy = DifficultyRadioButton.goldenBtnRd("Fácil");
        rbEasy.setBounds(
            (int) (180 * SCALE), 
            (int) (70 * SCALE), 
            (int) (70 * SCALE), 
            (int) (25 *SCALE)
        );
        rbMedium = gui.components.buttons.DifficultyRadioButton.goldenBtnRd("Médio");
        rbMedium.setBounds(
            (int) (260 * SCALE), 
            (int) (70 * SCALE), 
            (int) (80 * SCALE), 
            (int) (25 * SCALE)
        );
        rbMedium.setSelected(true);
        rbHard = gui.components.buttons.DifficultyRadioButton.goldenBtnRd("Difícil");
        rbHard.setBounds(
            (int) (350 * SCALE), 
            (int) (70 * SCALE), 
            (int) (80 * SCALE), 
            (int) (25 * SCALE)
        );

        // O método ButtonGroup é utilizado para agrupar os botões de rádio, garantindo que apenas um possa ser selecionado por vez.
        ButtonGroup buttonGroup = new ButtonGroup();
        // Adiciona os botões de rádio ao grupo para garantir a exclusividade da seleção
        buttonGroup.add(rbEasy); 
        buttonGroup.add(rbMedium); 
        buttonGroup.add(rbHard);
        // Adiciona os botões de rádio ao painel para que sejam exibidos na interface
        add(rbEasy); 
        add(rbMedium); 
        add(rbHard);

        // Seção de slots para os nomes dos jogadores
        // Slots para os nomes dos jogadores (Preparados para receber imagens e com espaçamento adequado)
        JLabel subTitle = new JLabel("INSERIR NOMES DOS JOGADORES", SwingConstants.CENTER);
        subTitle.setFont(new Font(
            "SansSerif", // Fonte SansSerif
            Font.BOLD, // Estilo da fonte em negrito
            (int) (14 * SCALE) // Tamanho da fonte 14
        ));
        subTitle.setForeground(GameColors.GOLD_ACCENT);
        subTitle.setBounds(
            (int) (0 * SCALE), 
            (int) (120 * SCALE), 
            (int) (520 * SCALE), 
            (int) (20 * SCALE)
        );
        add(subTitle);

        /* * EXPLICAÇÃO DOS ESPAÇAMENTOS (X):
         * Número começa em X. Mede 35px de largura.
         * Damos +8px de espaço vazio (respiro para sua arte).
         * Campo de texto começa em X + 35 + 8.
         */
        int i = 0;
        for (int x = 25; x <= 280; x +=255) {
            for (int y = 155; y <= 210; y += 55) {

                add(PlayerIdentifier.squarePlayerIdentifier(
                    String.valueOf(i + 1),
                    (int) (x * SCALE),
                    (int) (y * SCALE)
                ));
                i++;
            }
        }

        // Cria o campo de texto para os jogadores utilizando a classe "SlotsName" e armazena a referência na variável.
        // Não é preciso criar um novo objeto pois a classe "SlotsName" já tem um método estático que retorna o JTextField pronto para uso.
        txtP1 = SlotsName.slotName( // 45 + 35 + 8 = 88 --> P1
            68, 
            155, 
            155, 
            35, 
            true
        );
        txtCPU1 = SlotsName.slotName( // 305 + 35 + 8 = 348 --> CPU 1
            328,
            155, 
            155, 
            35, 
            false
        ); 
        txtCPU2 = SlotsName.slotName( // 45 + 35 + 8 = 88 --> CPU 2
            68, 
            210, 
            155, 
            35, 
            false
        ); 
        txtCPU3 = SlotsName.slotName( // 305 + 35 + 8 = 348 --> CPU 3
            328, 
            210, 
            155, 
            35, 
            false
        ); 
        
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
        add(SlotsIcon.slotIconLabel( 
            "👤", 
            225, 
            155
        )); 
        add(SlotsIcon.slotIconLabel(
            "💻", 
            485, 
            155
        )); 
        add(SlotsIcon.slotIconLabel(
            "💻", 
            225, 
            210
        ));
        add(SlotsIcon.slotIconLabel(
            "💻", 
            485, 
            210
        ));

        // Cria o botão de iniciar o jogo utilizando a classe "PlayButton" e armazena a referência na variável "playBtn"
        CustomButton playBtn = new PlayButton();
        playBtn.setBounds(
            180, 
            300, 
            200, 
            45
        ); // Define a posição e o tamanho do botão
        
        // Cria a ação de iniciar o jogo
        StartGameAction startGameAction = new StartGameAction(this, this.windowManager); 
        playBtn.addActionListener(startGameAction); // Adiciona a ação de iniciar o jogo ao botão "playBtn"
        add(playBtn); // Adiciona o botão "playBtn" ao painel para que seja exibido na interface
    }

    // Método para desenhar o fundo personalizado do menu offline
    // @Override indica que o método "paintComponent" está sendo sobrescrito da classe pai (JPanel). Serve como uma espécie de "guarda-costas" para garantir que estamos realmente sobrescrevendo um método existente e não criando um novo método por engano.
    @Override
    // O método "paintComponent" é chamado sempre que o painel precisa ser redesenhado, permitindo que personalizemos a aparência do fundo do menu offline.
    // Visibilidade "protected" para que apenas classes dentro do mesmo pacote ou subclasses possam acessar este método
    protected void paintComponent(Graphics g) {
        // Estrutura padrão do "paintComponent" para garantir que o fundo seja desenhado corretamente
        super.paintComponent(g);
        // Cria um contexto gráfico 2D para aplicar renderizações avançadas (como anti-aliasing)
        Graphics2D g2 = (Graphics2D) g;
        // Habilita o anti-aliasing para suavizar as bordas das imagens desenhadas
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Placa roxa de fundo
        g2.setColor(GameColors.PURPLE_BG);
        g2.fillRoundRect(
            0, 
            0, 
            getWidth(), 
            getHeight(), 
            24, 
            24
        );
        
        // Moldura dourada externa
        g2.setColor(GameColors.GOLD_ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(
            2, 
            2, 
            getWidth() - 5, 
            getHeight() - 5, 
            24, 
            24
        );
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
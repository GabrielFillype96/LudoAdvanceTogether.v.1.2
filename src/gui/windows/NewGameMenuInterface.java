package gui.windows;

import gui.components.buttons.DifficultyRadioButton;
import gui.components.PlayerIdentifier;
import gui.components.SlotsName;
import gui.components.buttons.CustomButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import actions.StartGameAction;

/*
 * NewGameMenuInterface: Versão Preparada para Artes Customizadas
 * Adiciona espaçamento entre números e campos, e deixa os slots prontos para receber imagens.
 */
public class NewGameMenuInterface extends JPanel {
    // Variável para armazenar a referência ao WindowManager, que é o responsável por controlar as telas do jogo
    private WindowManager windowManager;

    // Variáveis de instância para as dimensões do menu e as cores baseadas na sua arte real
    private int offlineMenuWidth = 560; // Define a largura do menu do modo de jogo offline
    private int offlineMenuHeight = 420; // Define a altura do menu do modo de jogo offline
    
    // Cores baseadas na sua imagem real
    private final Color PURPLE_BG = new Color(52, 34, 64); // Roxo profundo de fundo
    private final Color GOLD_ACCENT = new Color(222, 179, 102); // Dourado fosco dos textos/bordas
    private final static Color INPUT_BG = new Color(25, 14, 33); // Escuro das caixas de texto

    // Componentes
    private JRadioButton rbEasy, rbMedium, rbHard; // Botões do tipo radio para a dificuldade
    private JTextField txtP1, txtCPU1, txtCPU2, txtCPU3;
    
    // Componente do botão customizado que utiliza a classe "CustomButton"
    private CustomButton btnJogarCustom; // Botão principal para iniciar o jogo;
    private JButton btnJogarFallback; // Botão provisório para o teste atual
    private DifficultyRadioButton DifficultyRadioButton; // Instância da classe auxiliar para criar os botões de rádio de dificuldade 


    public NewGameMenuInterface(WindowManager windowManager) {
        this.windowManager = windowManager;
        // Dimensões exatas para o GridBagLayout centralizar perfeitamente
        // Instancia (cria o objeto) da classe Dimension do Java Awt com as dimensões do menu offline
        Dimension offlineMenuSize = new Dimension(offlineMenuWidth, offlineMenuHeight); 
        setSize(offlineMenuSize); // Define o tamanho do painel para as dimensões do menu offline
        setPreferredSize(offlineMenuSize); //
        setMinimumSize(offlineMenuSize);
        setMaximumSize(offlineMenuSize);
        
        setOpaque(false); // Deixa o layer transparente para mostrar o fundo 
        setLayout(null); // Layout absoluto para controle total dos pixels de espaçamento

        // Título do Menu
        // Instancia um JLabel com o texto "MODO JOGO OFFLINE" e centraliza o texto horizontalmente
        JLabel title = new JLabel("MODO JOGO OFFLINE", SwingConstants.CENTER); 
        // Define a fonte, cor e posição do título
        title.setFont(new Font("Serif", Font.BOLD, 22));
        title.setForeground(GOLD_ACCENT);
        title.setBounds(0, 25, 560, 30); 
        add(title); // Adiciona o título ao painel

        // Seleção da dificuldade da CPU
        // Instancia um JLabel para a seção de dificuldade, alinhado à direita
        JLabel lblDif = new JLabel("Dificuldade da CPU:", SwingConstants.RIGHT);
        // Define a fonte, cor e posição do label de dificuldade
        lblDif.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblDif.setForeground(GOLD_ACCENT);
        lblDif.setBounds(20, 70, 140, 25);
        add(lblDif); // Adiciona o label de dificuldade ao painel

        
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
        JLabel subTitle = new JLabel("INSERIR NOMES DOS JOGADORES", SwingConstants.CENTER);
        subTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        subTitle.setForeground(GOLD_ACCENT);
        subTitle.setBounds(0, 120, 560, 20);
        add(subTitle);

        /* * EXPLICAÇÃO DOS ESPAÇAMENTOS (X):
         * Número começa em X. Mede 35px de largura.
         * Damos +8px de espaço vazio (respiro para sua arte).
         * Campo de texto começa em X + 35 + 8.
         */
        
        // --- LINHA 1 ---
        // Slot 1 (Você)
        add(PlayerIdentifier.squarePlayerIdentifier("1", 45, 155));
        txtP1 = SlotsName.slotsName(88, 155, 155, 35, true); // 45 + 35 + 8 = 88 (Espaço perfeito)

        txtP1.setText("Digite seu nome");
        add(txtP1);
        add(criarSlotIcone("👤", 250, 155)); // Aqui entrará sua arte de Pessoa .png

        // Slot 2 (CPU 1)
        add(PlayerIdentifier.squarePlayerIdentifier("2", 305, 155));
        txtCPU1 = SlotsName.slotsName(348, 155, 155, 35, false); // 305 + 35 + 8 = 348
        txtCPU1.setText("Computador 1");

        add(txtCPU1);
        add(criarSlotIcone("⚙️", 510, 155)); // Aqui entrará sua arte de Engrenagem .png

        // --- LINHA 2 ---
        // Slot 3 (CPU 2)
        add(PlayerIdentifier.squarePlayerIdentifier("3", 45, 210));
        txtCPU2 = SlotsName.slotsName(88, 210, 155, 35, false);
        txtCPU2.setText("Computador 2");

        add(txtCPU2);
        add(criarSlotIcone("⚙️", 250, 210));

        // Slot 4 (CPU 3)
        add(PlayerIdentifier.squarePlayerIdentifier("4", 305, 210));
        txtCPU3 = SlotsName.slotsName(348, 210, 155, 35, false);
        txtCPU3.setText("Computador 3");
        
        add(txtCPU3);
        add(criarSlotIcone("⚙️", 510, 210));


        // --- SEÇÃO DO BOTÃO JOGAR (PREPARADO PARA SUA ARTE) ---
        try {
            // Quando suas imagens estiverem prontas na pasta de recursos, descomente as linhas abaixo:
            ImageIcon imgNormal = new ImageIcon(getClass().getResource("/assets/playButton.png"));
            ImageIcon imgHover = new ImageIcon(getClass().getResource("/assets/playButtonSelected.png"));
            btnJogarCustom = new CustomButton(imgNormal, imgHover, true);
            btnJogarCustom.setBounds(205, 295, 145, 45);
            btnJogarCustom.addActionListener(new StartGameAction(this, this.windowManager));
            add(btnJogarCustom);
            
            
            // Forçando o erro para rodar o botão provisório enquanto você não tem o arquivo .png
            throw new Exception("Aguardando imagens do usuário");
        } catch (Exception e) {
            // Botão reserva elegante de teste (Simula o tamanho real da sua futura arte)
            btnJogarFallback = new JButton("JOGAR");
            btnJogarFallback.setBounds(205, 295, 145, 45);
            btnJogarFallback.setBackground(GOLD_ACCENT);
            btnJogarFallback.setForeground(INPUT_BG);
            btnJogarFallback.setFont(new Font("Arial", Font.BOLD, 18));
            btnJogarFallback.setFocusPainted(false);
            btnJogarFallback.setBorder(new LineBorder(GOLD_ACCENT.darker(), 2));
            add(btnJogarFallback);
        }
        StartGameAction acaoJogar = new StartGameAction(this, this.windowManager);
        btnJogarFallback.addActionListener(acaoJogar);
    }

    // Auxiliar para o ícone lateral (Pessoa/Engrenagem) - Também preparado para ImageIcon
    private JLabel criarSlotIcone(String unicodePadrao, int x, int y) {
        JLabel lbl = new JLabel(unicodePadrao, SwingConstants.CENTER);
        lbl.setBounds(x, y, 25, 35);
        lbl.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        lbl.setForeground(GOLD_ACCENT);
        
        // Quando tiver os arquivos .png prontos, a lógica será:
        // if(unicodePadrao.equals("👤")) lbl.setIcon(new ImageIcon(getClass().getResource("/images/icone_humano.png")));
        // else lbl.setIcon(new ImageIcon(getClass().getResource("/images/icone_engrenagem.png")));
        // lbl.setText(""); 

        return lbl;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Placa roxa de fundo
        g2.setColor(PURPLE_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
        
        // Moldura dourada externa
        g2.setColor(GOLD_ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 24, 24);

    }

    // --- MÉTODOS AUXILIARES PARA A AÇÃO LER OS DADOS ---

    public String getNomeJogador() {
        // Retorna o texto do campo do P1 (certifique-se de que a variável txtP1 seja visível aqui)
        return txtP1.getText();
    }

    public String getDificuldadeSelecionada() {
        // Verifica qual rádio botão está marcado no momento do clique
        if (rbEasy.isSelected()) return "FÁCIL";
        if (rbHard.isSelected()) return "DIFÍCIL";
        return "MÉDIO"; // Caso padrão
    }








    // // ==========================================
    // // MÉTODO MAIN INTEGRADO PARA SEU TESTE
    // // ==========================================
    // public static void main(String[] args) {
    //     JFrame frame = new JFrame("Teste do Mini-Menu Offline com Espaçamento");
    //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //     frame.setSize(900, 600);
    //     frame.setLocationRelativeTo(null);

    //     JPanel fundoMesa = new JPanel(new GridBagLayout());
    //     fundoMesa.setBackground(new Color(38, 24, 16)); 

    //     NewGameMenuInterface miniMenu = new NewGameMenuInterface();
    //     fundoMesa.add(miniMenu);

    //     frame.add(fundoMesa);
    //     frame.setVisible(true);
    // }
}
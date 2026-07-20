// Classe responsável por criar a interface do menu do modo de jogo offline
package gui.windows;

import gui.components.SlotsIcon;
import gui.components.PlayerIdentifier;
import gui.components.SlotsName;
import gui.components.buttons.CustomButton;
import gui.components.buttons.PlayButton;
import gui.components.buttons.DifficultyRadioButton;
import actions.StartGameAction;
import gui.theme.GameColors;

import java.awt.*;
import javax.swing.*;

public class NewGameMenuScreen extends JPanel {
    private WindowManager windowManager;
    private SubMenuContainer subMenuContainer;
    private static final double SCALE = 1.5; 

    private static final Dimension OFFLINE_MENU_DIMENSION = new Dimension( 
        (int) (560 * SCALE), 
        (int) (460 * SCALE)
    );

    private JRadioButton rbEasy, rbMedium, rbHard; 
    private JTextField txtP1, txtCPU1, txtCPU2, txtCPU3; 
    private JComboBox<String> cbColorP1, cbColorCPU1, cbColorCPU2, cbColorCPU3;
    
    // NOVO: Declaração das variáveis dos ícones para podermos alterar a cor deles depois
    private JLabel lblIconP1, lblIconCPU1, lblIconCPU2, lblIconCPU3;
    
    private boolean updatingColors = false; 

    public NewGameMenuScreen(WindowManager windowManager, SubMenuContainer subMenuContainer) {
        this.windowManager = windowManager;
        this.subMenuContainer = subMenuContainer;
        
        setPreferredSize(OFFLINE_MENU_DIMENSION); 
        setMinimumSize(OFFLINE_MENU_DIMENSION);
        setMaximumSize(OFFLINE_MENU_DIMENSION);
        
        setOpaque(false); 
        setLayout(null); 

        // Título do sub-menu
        JLabel title = new JLabel("MODO JOGO OFFLINE", SwingConstants.CENTER); 
        title.setFont(new Font("Serif", Font.BOLD, (int) (22 * SCALE)));
        title.setForeground(GameColors.GOLD_ACCENT);
        title.setBounds((int) (0 * SCALE), (int) (25 * SCALE), (int) (560 * SCALE), (int) (30 * SCALE)); 
        add(title); 

        // Seleção da dificuldade
        JLabel lblDif = new JLabel("Dificuldade da CPU:", SwingConstants.RIGHT);
        lblDif.setFont(new Font("SansSerif", Font.BOLD, (int) (13 * SCALE)));
        lblDif.setForeground(GameColors.GOLD_ACCENT);
        lblDif.setBounds((int) (20 * SCALE), (int) (70 * SCALE), (int) (140 * SCALE), (int) (25 * SCALE));
        add(lblDif); 

        rbEasy = DifficultyRadioButton.goldenBtnRd("Fácil", SCALE);
        rbEasy.setBounds((int) (180 * SCALE), (int) (70 * SCALE), (int) (70 * SCALE), (int) (25 * SCALE));
        rbMedium = DifficultyRadioButton.goldenBtnRd("Médio", SCALE);
        rbMedium.setBounds((int) (260 * SCALE), (int) (70 * SCALE), (int) (80 * SCALE), (int) (25 * SCALE));
        rbMedium.setSelected(true);
        rbHard = DifficultyRadioButton.goldenBtnRd("Difícil", SCALE);
        rbHard.setBounds((int) (350 * SCALE), (int) (70 * SCALE), (int) (80 * SCALE), (int) (25 * SCALE));

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(rbEasy); 
        buttonGroup.add(rbMedium); 
        buttonGroup.add(rbHard);
        add(rbEasy); 
        add(rbMedium); 
        add(rbHard);

        // Subtítulo
        JLabel subTitle = new JLabel("INSERIR NOMES DOS JOGADORES", SwingConstants.CENTER);
        subTitle.setFont(new Font("SansSerif", Font.BOLD, (int) (14 * SCALE)));
        subTitle.setForeground(GameColors.GOLD_ACCENT);
        subTitle.setBounds((int) (0 * SCALE), (int) (560 * SCALE), (int) (560 * SCALE), (int) (20 * SCALE));
        
        // Correção visual do bound do subtítulo para o novo tamanho da caixa
        subTitle.setBounds((int) (0 * SCALE), (int) (120 * SCALE), (int) (560 * SCALE), (int) (20 * SCALE));
        add(subTitle);

        // --- LINHA 1 DE JOGADORES ---
        // (As linhas do PlayerIdentifier "1" e "3" foram removidas daqui)

        txtP1 = SlotsName.slotName((int) (68 * SCALE), (int) (155 * SCALE), (int) (155 * SCALE), (int) (35 * SCALE), true, SCALE);
        txtCPU1 = SlotsName.slotName((int) (328 * SCALE), (int) (155 * SCALE), (int) (155 * SCALE), (int) (35 * SCALE), true, SCALE); 
        txtP1.setText("Digite seu nome");
        txtCPU1.setText("Computador 1");
        
        setupPlaceholder(txtP1, "Digite seu nome");
        setupPlaceholder(txtCPU1, "Computador 1");
        
        add(txtP1);
        add(txtCPU1);
        
        lblIconP1 = SlotsIcon.slotIconLabel("👤", (int) (225 * SCALE), (int) (155 * SCALE), SCALE); 
        lblIconCPU1 = SlotsIcon.slotIconLabel("💻", (int) (485 * SCALE), (int) (155 * SCALE), SCALE); 
        add(lblIconP1);
        add(lblIconCPU1);

        cbColorP1 = createColorComboBox((int) (68 * SCALE), (int) (195 * SCALE), (int) (155 * SCALE), (int) (25 * SCALE));
        cbColorCPU1 = createColorComboBox((int) (328 * SCALE), (int) (195 * SCALE), (int) (155 * SCALE), (int) (25 * SCALE));
        cbColorP1.setSelectedIndex(0);   
        cbColorCPU1.setSelectedIndex(1); 
        add(cbColorP1);
        add(cbColorCPU1);

        // --- LINHA 2 DE JOGADORES ---
        // (As linhas do PlayerIdentifier "2" e "4" foram removidas daqui)

        txtCPU2 = SlotsName.slotName((int) (68 * SCALE), (int) (245 * SCALE), (int) (155 * SCALE), (int) (35 * SCALE), true, SCALE); 
        txtCPU3 = SlotsName.slotName((int) (328 * SCALE), (int) (245 * SCALE), (int) (155 * SCALE), (int) (35 * SCALE), true, SCALE); 
        txtCPU2.setText("Computador 2");
        txtCPU3.setText("Computador 3");
        
        setupPlaceholder(txtCPU2, "Computador 2");
        setupPlaceholder(txtCPU3, "Computador 3");
        
        add(txtCPU2);
        add(txtCPU3);

        lblIconCPU2 = SlotsIcon.slotIconLabel("💻", (int) (225 * SCALE), (int) (245 * SCALE), SCALE);
        lblIconCPU3 = SlotsIcon.slotIconLabel("💻", (int) (485 * SCALE), (int) (245 * SCALE), SCALE);
        add(lblIconCPU2);
        add(lblIconCPU3);

        cbColorCPU2 = createColorComboBox((int) (68 * SCALE), (int) (285 * SCALE), (int) (155 * SCALE), (int) (25 * SCALE));
        cbColorCPU3 = createColorComboBox((int) (328 * SCALE), (int) (285 * SCALE), (int) (155 * SCALE), (int) (25 * SCALE));
        cbColorCPU2.setSelectedIndex(2); 
        cbColorCPU3.setSelectedIndex(3); 
        add(cbColorCPU2);
        add(cbColorCPU3);

        // Ativa a lógica inteligente que gerencia as cores e os ícones
        setupColorSelectionLogic();

        // Botão Jogar
        CustomButton playBtn = new PlayButton();
        playBtn.setBounds((int) (180 * SCALE), (int) (355 * SCALE), (int) (200 * SCALE), (int) (45 * SCALE)); 
        
        StartGameAction startGameAction = new StartGameAction(this, this.windowManager); 
        playBtn.addActionListener(startGameAction); 
        add(playBtn); 
    }

    private JComboBox<String> createColorComboBox(int x, int y, int w, int h) {
        String[] cores = {"Roxo", "Azul", "Amarelo", "Rosa"};
        JComboBox<String> cb = new JComboBox<>(cores);
        cb.setBounds(x, y, w, h);
        cb.setBackground(new Color(25, 14, 33)); 
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("SansSerif", Font.BOLD, (int) (12 * SCALE)));
        cb.setBorder(new javax.swing.border.LineBorder(GameColors.GOLD_ACCENT, 1));
        return cb;
    }

    // NOVO: Método para traduzir o texto da seleção em um objeto Color real para os ícones
    private Color getColorByString(String nomeCor) {
        if (nomeCor == null) return GameColors.GOLD_ACCENT;
        switch (nomeCor) {
            case "Roxo": return new Color(155, 89, 182);   // Roxo vivo
            case "Azul": return new Color(52, 152, 219);   // Azul claro/médio
            case "Amarelo": return new Color(241, 196, 15); // Amarelo clássico
            case "Rosa": return new Color(232, 67, 147);    // Rosa destacado
            default: return GameColors.GOLD_ACCENT;
        }
    }

    // MODIFICADO: Atualiza a troca automática e aplica as novas cores nos ícones correspondentes
    private void setupColorSelectionLogic() {
        @SuppressWarnings("unchecked")
        JComboBox<String>[] boxes = new JComboBox[]{cbColorP1, cbColorCPU1, cbColorCPU2, cbColorCPU3};
        JLabel[] icons = new JLabel[]{lblIconP1, lblIconCPU1, lblIconCPU2, lblIconCPU3};
        
        for (int i = 0; i < boxes.length; i++) {
            final int currentIndex = i;
            boxes[i].addActionListener(e -> {
                if (updatingColors) return; 
                
                updatingColors = true;
                
                int duplicateIndex = -1;
                for (int j = 0; j < boxes.length; j++) {
                    if (j != currentIndex && boxes[j].getSelectedIndex() == boxes[currentIndex].getSelectedIndex()) {
                        duplicateIndex = j;
                        break;
                    }
                }
                
                if (duplicateIndex != -1) {
                    boolean[] colorUsed = new boolean[4];
                    for (int j = 0; j < boxes.length; j++) {
                        if (j != duplicateIndex) {
                            colorUsed[boxes[j].getSelectedIndex()] = true;
                        }
                    }
                    
                    int missingColorIndex = 0;
                    for (int k = 0; k < 4; k++) {
                        if (!colorUsed[k]) {
                            missingColorIndex = k;
                            break;
                        }
                    }
                    
                    boxes[duplicateIndex].setSelectedIndex(missingColorIndex);
                }
                
                // NOVO: Aplica dinamicamente a cor certa no ícone de cada jogador após a definição das cores
                for (int j = 0; j < boxes.length; j++) {
                    String corSelecionada = (String) boxes[j].getSelectedItem();
                    icons[j].setForeground(getColorByString(corSelecionada));
                }
                
                updatingColors = false;
            });
        }
        
        // Força os ícones a iniciarem com as cores corretas logo na abertura da tela
        for (int j = 0; j < boxes.length; j++) {
            String corSelecionada = (String) boxes[j].getSelectedItem();
            icons[j].setForeground(getColorByString(corSelecionada));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(GameColors.PURPLE_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int) (24 * SCALE), (int) (24 * SCALE));
        
        g2.setColor(GameColors.GOLD_ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect((int) (2 * SCALE), (int) (2 * SCALE), getWidth() - (int) (5 * SCALE), getHeight() - (int) (5 * SCALE), (int) (24 * SCALE), (int)(24 * SCALE));
    }

    public SubMenuContainer getSubMenuContainer() { return subMenuContainer; }
    public String getPlayerName() { return txtP1.getText(); }
    public String getCPUDifficulty() {
        if (rbEasy.isSelected()) return "FÁCIL";
        if (rbHard.isSelected()) return "DIFÍCIL";
        return "MÉDIO";
    }

    // Retorna os nomes de todos os slots
    public String getPlayer1Name() { return txtP1.getText().trim(); }
    public String getCPU1Name() { return txtCPU1.getText().trim(); }
    public String getCPU2Name() { return txtCPU2.getText().trim(); }
    public String getCPU3Name() { return txtCPU3.getText().trim(); }

    // Retorna as cores selecionadas em formato de texto (ex: "Roxo", "Azul")
    public String getPlayer1Color() { return (String) cbColorP1.getSelectedItem(); }
    public String getCPU1Color() { return (String) cbColorCPU1.getSelectedItem(); }
    public String getCPU2Color() { return (String) cbColorCPU2.getSelectedItem(); }
    public String getCPU3Color() { return (String) cbColorCPU3.getSelectedItem(); }

    // Método auxiliar para adicionar o efeito de placeholder inteligente nos campos
    private void setupPlaceholder(JTextField textField, String placeholder) {
        // Inicializa o texto padrão com a cor cinza de dica
        textField.setForeground(Color.GRAY);
        
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                // Se o usuário clicar e o texto for o padrão, limpa o campo e muda para branco
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                // Se o usuário clicar fora e não tiver digitado nada, restaura o padrão em cinza
                if (textField.getText().trim().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }
}
// Classe responsável por criar a interface do menu do modo de jogo offline
package gui.windows;

import gui.components.SlotsIcon;
import gui.components.buttons.CustomButton;
import gui.components.buttons.PlayButton;
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

    private JToggleButton rbEasy, rbMedium, rbHard; 
    private JTextField txtP1, txtCPU1, txtCPU2, txtCPU3; 
    private JComboBox<String> cbColorP1, cbColorCPU1, cbColorCPU2, cbColorCPU3;
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
        lblDif.setBounds((int) (20 * SCALE), (int) (70 * SCALE), (int) (140 * SCALE), (int) (28 * SCALE));
        add(lblDif); 

        rbEasy = createChipButton("Fácil", false);
        rbEasy.setBounds((int) (175 * SCALE), (int) (70 * SCALE), (int) (85 * SCALE), (int) (28 * SCALE));

        rbMedium = createChipButton("Médio", true);
        rbMedium.setBounds((int) (270 * SCALE), (int) (70 * SCALE), (int) (85 * SCALE), (int) (28 * SCALE));

        rbHard = createChipButton("Difícil", false);
        rbHard.setBounds((int) (365 * SCALE), (int) (70 * SCALE), (int) (85 * SCALE), (int) (28 * SCALE));

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
        subTitle.setBounds((int) (0 * SCALE), (int) (120 * SCALE), (int) (560 * SCALE), (int) (20 * SCALE));
        add(subTitle);

        // --- LINHA 1 DE JOGADORES ---
        lblIconP1 = SlotsIcon.slotIconLabel("👤", (int) (55 * SCALE), (int) (155 * SCALE), SCALE); 
        lblIconCPU1 = SlotsIcon.slotIconLabel("💻", (int) (315 * SCALE), (int) (155 * SCALE), SCALE); 
        add(lblIconP1);
        add(lblIconCPU1);

        // Posição x alterada de 82 para 90 para dar o "respiro", largura ajustada para 150
        txtP1 = createPlayerTextField((int) (90 * SCALE), (int) (155 * SCALE), (int) (150 * SCALE), (int) (32 * SCALE), "Digite seu nome");
        txtCPU1 = createPlayerTextField((int) (350 * SCALE), (int) (155 * SCALE), (int) (150 * SCALE), (int) (32 * SCALE), "Computador 1"); 
        add(txtP1);
        add(txtCPU1);

        cbColorP1 = createColorComboBox((int) (90 * SCALE), (int) (193 * SCALE), (int) (150 * SCALE), (int) (25 * SCALE));
        cbColorCPU1 = createColorComboBox((int) (350 * SCALE), (int) (193 * SCALE), (int) (150 * SCALE), (int) (25 * SCALE));
        cbColorP1.setSelectedIndex(0);   
        cbColorCPU1.setSelectedIndex(1); 
        add(cbColorP1);
        add(cbColorCPU1);

        // --- LINHA 2 DE JOGADORES ---
        lblIconCPU2 = SlotsIcon.slotIconLabel("💻", (int) (55 * SCALE), (int) (245 * SCALE), SCALE);
        lblIconCPU3 = SlotsIcon.slotIconLabel("💻", (int) (315 * SCALE), (int) (245 * SCALE), SCALE);
        add(lblIconCPU2);
        add(lblIconCPU3);

        txtCPU2 = createPlayerTextField((int) (90 * SCALE), (int) (245 * SCALE), (int) (150 * SCALE), (int) (32 * SCALE), "Computador 2"); 
        txtCPU3 = createPlayerTextField((int) (350 * SCALE), (int) (245 * SCALE), (int) (150 * SCALE), (int) (32 * SCALE), "Computador 3"); 
        add(txtCPU2);
        add(txtCPU3);

        cbColorCPU2 = createColorComboBox((int) (90 * SCALE), (int) (283 * SCALE), (int) (150 * SCALE), (int) (25 * SCALE));
        cbColorCPU3 = createColorComboBox((int) (350 * SCALE), (int) (283 * SCALE), (int) (150 * SCALE), (int) (25 * SCALE));
        cbColorCPU2.setSelectedIndex(2); 
        cbColorCPU3.setSelectedIndex(3); 
        add(cbColorCPU2);
        add(cbColorCPU3);

        setupColorSelectionLogic();

        // Botão Jogar
        CustomButton playBtn = new PlayButton();
        playBtn.setBounds((int) (180 * SCALE), (int) (355 * SCALE), (int) (200 * SCALE), (int) (45 * SCALE)); 
        
        StartGameAction startGameAction = new StartGameAction(this, this.windowManager); 
        playBtn.addActionListener(startGameAction); 
        add(playBtn); 
    }

    // Campo de texto customizado com quinas arredondadas
    private JTextField createPlayerTextField(int x, int y, int w, int h, String placeholder) {
        JTextField tf = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hasFocus()) {
                    g2.setColor(GameColors.GOLD_ACCENT);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(new Color(222, 179, 102, 100));
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };

        tf.setOpaque(false);
        tf.setBounds(x, y, w, h);
        tf.setFont(new Font("SansSerif", Font.PLAIN, (int) (12 * SCALE)));
        tf.setBackground(new Color(25, 14, 33));
        tf.setForeground(Color.GRAY);
        tf.setCaretColor(GameColors.GOLD_ACCENT);
        tf.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                tf.repaint();
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                tf.repaint();
                if (tf.getText().trim().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(Color.GRAY);
                }
            }
        });

        return tf;
    }

    private JToggleButton createChipButton(String text, boolean selected) {
        JToggleButton btn = new JToggleButton(text);
        btn.setSelected(selected);
        btn.setFont(new Font("SansSerif", Font.BOLD, (int) (12 * SCALE)));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.setUI(new javax.swing.plaf.basic.BasicToggleButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = b.getWidth();
                int h = b.getHeight();
                
                if (b.isSelected()) {
                    g2.setColor(GameColors.GOLD_ACCENT);
                    g2.fillRoundRect(0, 0, w, h, h, h);
                    g2.setColor(new Color(25, 14, 33));
                } else {
                    g2.setColor(new Color(42, 24, 54));
                    g2.fillRoundRect(0, 0, w, h, h, h);
                    g2.setColor(GameColors.GOLD_ACCENT);
                    g2.setStroke(new BasicStroke(1));
                    g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);
                    g2.setColor(Color.WHITE);
                }
                
                FontMetrics fm = g2.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(b.getText(), g2).getBounds();
                int textX = (w - stringBounds.width) / 2;
                int textY = (h - stringBounds.height) / 2 + fm.getAscent();
                
                g2.drawString(b.getText(), textX, textY);
                g2.dispose();
            }
        });
        return btn;
    }

    // Caixa de seleção com quinas arredondadas
    private JComboBox<String> createColorComboBox(int x, int y, int w, int h) {
        String[] cores = {"Roxo", "Azul", "Amarelo", "Rosa"};
        JComboBox<String> cb = new JComboBox<String>(cores) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GameColors.GOLD_ACCENT);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };

        cb.setOpaque(false);
        cb.setBounds(x, y, w, h);
        cb.setBackground(new Color(25, 14, 33)); 
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("SansSerif", Font.BOLD, (int) (12 * SCALE)));
        cb.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        cb.setFocusable(false);

        cb.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        g2.setColor(new Color(25, 14, 33));
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        
                        g2.setColor(GameColors.GOLD_ACCENT);
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        int[] xPoints = {cx - 4, cx + 4, cx};
                        int[] yPoints = {cy - 2, cy - 2, cy + 3};
                        g2.fillPolygon(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                };
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(25, 14, 33));
                g2.fillRoundRect(0, 0, comboBox.getWidth(), comboBox.getHeight(), 8, 8);
                g2.dispose();
            }
        });

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("SansSerif", Font.BOLD, (int) (12 * SCALE)));
                lbl.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                lbl.setOpaque(true);
                
                if (index == -1) {
                    lbl.setBackground(new Color(25, 14, 33));
                    lbl.setForeground(Color.WHITE);
                } else if (isSelected) {
                    lbl.setBackground(GameColors.GOLD_ACCENT);
                    lbl.setForeground(new Color(25, 14, 33));
                } else {
                    lbl.setBackground(new Color(35, 20, 45));
                    lbl.setForeground(Color.WHITE);
                }
                return lbl;
            }
        });

        return cb;
    }

    private Color getColorByString(String nomeCor) {
        if (nomeCor == null) return GameColors.GOLD_ACCENT;
        switch (nomeCor) {
            case "Roxo": return new Color(107, 86, 165);
            case "Azul": return new Color(80, 163, 213);
            case "Amarelo": return new Color(243, 177, 28);
            case "Rosa": return new Color(218, 99, 127);
            default: return GameColors.GOLD_ACCENT;
        }
    }

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
                
                for (int j = 0; j < boxes.length; j++) {
                    String corSelecionada = (String) boxes[j].getSelectedItem();
                    icons[j].setForeground(getColorByString(corSelecionada));
                }
                
                updatingColors = false;
            });
        }
        
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

        // Fundo principal
        g2.setColor(GameColors.PURPLE_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int) (24 * SCALE), (int) (24 * SCALE));

        // Cards dos Jogadores
        Color cardBg = new Color(42, 24, 54); 
        Color cardBorder = new Color(222, 179, 102, 60);

        int[][] cardPositions = {
            {(int) (45 * SCALE), (int) (148 * SCALE)},
            {(int) (305 * SCALE), (int) (148 * SCALE)},
            {(int) (45 * SCALE), (int) (238 * SCALE)},
            {(int) (305 * SCALE), (int) (238 * SCALE)}
        };

        int cardW = (int) (210 * SCALE);
        int cardH = (int) (78 * SCALE);

        for (int[] pos : cardPositions) {
            g2.setColor(cardBg);
            g2.fillRoundRect(pos[0], pos[1], cardW, cardH, (int) (12 * SCALE), (int) (12 * SCALE));
            
            g2.setColor(cardBorder);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(pos[0], pos[1], cardW, cardH, (int) (12 * SCALE), (int) (12 * SCALE));
        }

        // Borda externa principal
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

    public String getPlayer1Name() { return txtP1.getText().trim(); }
    public String getCPU1Name() { return txtCPU1.getText().trim(); }
    public String getCPU2Name() { return txtCPU2.getText().trim(); }
    public String getCPU3Name() { return txtCPU3.getText().trim(); }

    public String getPlayer1Color() { return (String) cbColorP1.getSelectedItem(); }
    public String getCPU1Color() { return (String) cbColorCPU1.getSelectedItem(); }
    public String getCPU2Color() { return (String) cbColorCPU2.getSelectedItem(); }
    public String getCPU3Color() { return (String) cbColorCPU3.getSelectedItem(); }
}
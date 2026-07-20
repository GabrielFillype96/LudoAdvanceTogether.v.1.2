package gui.windows;

import gui.components.SlotsIcon;
import gui.components.buttons.CustomButton;
import gui.components.buttons.PlayButton;
import gui.theme.GameColors;
import network.GameClient;
import network.GameServer;

import javax.swing.*;
import java.awt.*;

public class LobbyScreen extends JPanel {

    private WindowManager windowManager;
    private GameServer server;
    private GameClient client;

    private static final double SCALE = 1.5;
    private static final Dimension LOBBY_MENU_DIMENSION = new Dimension(
        (int) (560 * SCALE),
        (int) (460 * SCALE)
    );

    // Elementos da Interface
    private JTextField txtIpServer;
    private JButton btnCriarSala;
    private JButton btnConectar;
    private JButton btnIniciarJogo;
    private JButton btnVoltar;

    private JLabel[] slotLabels = new JLabel[4];
    private JLabel[] slotIcons = new JLabel[4];

    public LobbyScreen(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.client = new GameClient();
        this.client.setWindowManager(windowManager);

        setPreferredSize(LOBBY_MENU_DIMENSION);
        setMinimumSize(LOBBY_MENU_DIMENSION);
        setMaximumSize(LOBBY_MENU_DIMENSION);

        setOpaque(false);
        setLayout(null);

        initUI();
    }

    private void initUI() {
        // Título Principal
        JLabel title = new JLabel("SALA MULTIPLAYER", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, (int) (22 * SCALE)));
        title.setForeground(GameColors.GOLD_ACCENT);
        title.setBounds(0, (int) (20 * SCALE), (int) (560 * SCALE), (int) (30 * SCALE));
        add(title);

        // --- PAINEL DE CONEXÃO (IP E BOTÕES DE SALA) ---
        JLabel lblIp = new JLabel("IP Host:", SwingConstants.RIGHT);
        lblIp.setFont(new Font("SansSerif", Font.BOLD, (int) (12 * SCALE)));
        lblIp.setForeground(GameColors.GOLD_ACCENT);
        lblIp.setBounds((int) (20 * SCALE), (int) (65 * SCALE), (int) (70 * SCALE), (int) (28 * SCALE));
        add(lblIp);

        txtIpServer = createStyledTextField((int) (95 * SCALE), (int) (65 * SCALE), (int) (130 * SCALE), (int) (28 * SCALE), "localhost");
        add(txtIpServer);

        btnCriarSala = createActionButton("Criar Sala", (int) (235 * SCALE), (int) (65 * SCALE), (int) (130 * SCALE), (int) (28 * SCALE));
        btnConectar = createActionButton("Entrar em Sala", (int) (375 * SCALE), (int) (65 * SCALE), (int) (140 * SCALE), (int) (28 * SCALE));
        add(btnCriarSala);
        add(btnConectar);

        // Subtítulo dos Slots
        JLabel subTitle = new JLabel("JOGADORES CONECTADOS", SwingConstants.CENTER);
        subTitle.setFont(new Font("SansSerif", Font.BOLD, (int) (13 * SCALE)));
        subTitle.setForeground(GameColors.GOLD_ACCENT);
        subTitle.setBounds(0, (int) (115 * SCALE), (int) (560 * SCALE), (int) (20 * SCALE));
        add(subTitle);

        // --- CARDS DOS SLOTS (2x2) ---
        int[][] positions = {
            {(int) (45 * SCALE), (int) (145 * SCALE)},  // Slot 1
            {(int) (305 * SCALE), (int) (145 * SCALE)}, // Slot 2
            {(int) (45 * SCALE), (int) (235 * SCALE)},  // Slot 3
            {(int) (305 * SCALE), (int) (235 * SCALE)}  // Slot 4
        };

        for (int i = 0; i < 4; i++) {
            int posX = positions[i][0];
            int posY = positions[i][1];

            // Ícone do Slot
            slotIcons[i] = SlotsIcon.slotIconLabel("💻", posX + (int) (10 * SCALE), posY + (int) (15 * SCALE), SCALE);
            add(slotIcons[i]);

            // Texto do Slot
            slotLabels[i] = new JLabel("Slot " + (i + 1) + ": [ VAZIO ]", SwingConstants.LEFT);
            slotLabels[i].setFont(new Font("SansSerif", Font.BOLD, (int) (11 * SCALE)));
            slotLabels[i].setForeground(Color.LIGHT_GRAY);
            slotLabels[i].setBounds(posX + (int) (45 * SCALE), posY + (int) (10 * SCALE), (int) (155 * SCALE), (int) (55 * SCALE));
            add(slotLabels[i]);
        }

        // --- PAINEL INFERIOR: AÇÕES DO LOBBY ---
        btnVoltar = createActionButton("Voltar", (int) (100 * SCALE), (int) (360 * SCALE), (int) (140 * SCALE), (int) (40 * SCALE));
        add(btnVoltar);

        // Botão Iniciar Jogo estilizado como PlayButton
        CustomButton playBtn = new PlayButton();
        playBtn.setBounds((int) (260 * SCALE), (int) (355 * SCALE), (int) (200 * SCALE), (int) (45 * SCALE));
        playBtn.setEnabled(false);
        this.btnIniciarJogo = playBtn;
        add(btnIniciarJogo);

        configurarAcoes();
    }

    private void configurarAcoes() {
        btnCriarSala.addActionListener(e -> {
            server = new GameServer();
            server.start();

            client.setLobbyScreen(this);
            client.connect("localhost", 12345);

            btnCriarSala.setEnabled(false);
            btnConectar.setEnabled(false);
            txtIpServer.setEnabled(false);
            btnIniciarJogo.setEnabled(true);
        });

        btnConectar.addActionListener(e -> {
            String ip = txtIpServer.getText().trim();
            if (ip.isEmpty()) ip = "localhost";

            client.setLobbyScreen(this);
            client.connect(ip, 12345);

            btnCriarSala.setEnabled(false);
            btnConectar.setEnabled(false);
            txtIpServer.setEnabled(false);
        });

        btnVoltar.addActionListener(e -> {
            if (windowManager != null) {
                // Adicione a ação de voltar aqui se necessário
            }
        });

        btnIniciarJogo.addActionListener(e -> {
            if (server != null) {
                server.startGame();
            }
        });
    }

    public void atualizarSlotUI(int slotIndex, String status) {
        if (slotIndex >= 0 && slotIndex < 4) {
            slotLabels[slotIndex].setText("<html>Slot " + (slotIndex + 1) + ":<br><font color='#DEB366'>" + status + "</font></html>");
        }
    }

    public void sincronizarLobby(boolean[] slotIsCPU, int meuId) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 4; i++) {
                if (i == meuId) {
                    slotIcons[i].setText("👤");
                    atualizarSlotUI(i, "Você (Jogador " + (i + 1) + ")");
                } else if (slotIsCPU[i]) {
                    slotIcons[i].setText("💻");
                    atualizarSlotUI(i, "CPU");
                } else {
                    slotIcons[i].setText("👤");
                    atualizarSlotUI(i, "Jogador " + (i + 1) + " (Conectado)");
                }
            }
        });
    }

    // --- COMPONENTES VISUAIS PERSONALIZADOS ---

    private JTextField createStyledTextField(int x, int y, int w, int h, String defaultText) {
        JTextField tf = new JTextField(defaultText) {
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
                g2.setColor(hasFocus() ? GameColors.GOLD_ACCENT : new Color(222, 179, 102, 100));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };

        tf.setOpaque(false);
        tf.setBounds(x, y, w, h);
        tf.setFont(new Font("SansSerif", Font.PLAIN, (int) (12 * SCALE)));
        tf.setBackground(new Color(25, 14, 33));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(GameColors.GOLD_ACCENT);
        tf.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        return tf;
    }

    private JButton createActionButton(String text, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("SansSerif", Font.BOLD, (int) (11 * SCALE)));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = b.getWidth();
                int height = b.getHeight();

                if (b.isEnabled()) {
                    g2.setColor(b.getModel().isPressed() ? GameColors.GOLD_ACCENT.darker() : new Color(42, 24, 54));
                } else {
                    g2.setColor(new Color(30, 18, 38));
                }

                g2.fillRoundRect(0, 0, width, height, 10, 10);

                g2.setColor(b.isEnabled() ? GameColors.GOLD_ACCENT : Color.GRAY);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, width - 1, height - 1, 10, 10);

                FontMetrics fm = g2.getFontMetrics();
                Rectangle bounds = fm.getStringBounds(b.getText(), g2).getBounds();
                int textX = (width - bounds.width) / 2;
                int textY = (height - bounds.height) / 2 + fm.getAscent();

                g2.drawString(b.getText(), textX, textY);
                g2.dispose();
            }
        });

        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fundo principal roxo
        g2.setColor(GameColors.PURPLE_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int) (24 * SCALE), (int) (24 * SCALE));

        // Cards dos Slots (2x2)
        Color cardBg = new Color(42, 24, 54);
        Color cardBorder = new Color(222, 179, 102, 60);

        int[][] cardPositions = {
            {(int) (45 * SCALE), (int) (145 * SCALE)},
            {(int) (305 * SCALE), (int) (145 * SCALE)},
            {(int) (45 * SCALE), (int) (235 * SCALE)},
            {(int) (305 * SCALE), (int) (235 * SCALE)}
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

        // Borda externa dourada
        g2.setColor(GameColors.GOLD_ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect((int) (2 * SCALE), (int) (2 * SCALE), getWidth() - (int) (5 * SCALE), getHeight() - (int) (5 * SCALE), (int) (24 * SCALE), (int) (24 * SCALE));
    }
}
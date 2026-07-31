package gui.windows;

import gui.components.SlotsIcon;
import gui.components.buttons.CustomButton;
import gui.components.buttons.PlayButton;
import gui.theme.GameColors;
import network.GameClient;
import network.GameServer;
import network.PlayerInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class LobbyScreen extends JPanel {

    private WindowManager windowManager;
    private GameServer server;
    private GameClient client;

    private static final double SCALE = 1.5;
    
    // Dimensões expandidas (600x530 * SCALE)
    private static final Dimension LOBBY_MENU_DIMENSION = new Dimension(
        (int) (600 * SCALE),
        (int) (530 * SCALE)
    );

    private static final String[] CORES_NOME = {"Roxo", "Azul", "Amarelo", "Rosa"};

    // Elementos da Interface Topo (Conexão e Perfil Pré-Jogo)
    private JTextField txtNomeJogador;
    private JTextField txtIpServer;
    private JButton btnCriarSala;
    private JButton btnConectar;
    private JButton btnIniciarJogo;

    // Componentes dos 4 Slots de Jogadores
    private JLabel[] slotIcons = new JLabel[4];
    private JTextField[] slotNames = new JTextField[4];
    private JComboBox<String>[] slotColors = new JComboBox[4];

    private boolean updatingFromNetwork = false;
    private boolean primeiraSincronizacaoFeita = false;

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
        // --- TÍTULO PRINCIPAL ---
        JLabel title = new JLabel("SALA MULTIPLAYER", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, (int) (20 * SCALE)));
        title.setForeground(GameColors.GOLD_ACCENT);
        title.setBounds(0, (int) (15 * SCALE), (int) (600 * SCALE), (int) (28 * SCALE));
        add(title);

        // --- PAINEL DE CONEXÃO E NOME DO JOGADOR ---
        JLabel lblNome = new JLabel("Seu Nome:", SwingConstants.RIGHT);
        lblNome.setFont(new Font("SansSerif", Font.BOLD, (int) (11 * SCALE)));
        lblNome.setForeground(GameColors.GOLD_ACCENT);
        lblNome.setBounds((int) (20 * SCALE), (int) (52 * SCALE), (int) (80 * SCALE), (int) (28 * SCALE));
        add(lblNome);

        txtNomeJogador = createStyledTextField((int) (105 * SCALE), (int) (52 * SCALE), (int) (160 * SCALE), (int) (28 * SCALE), "Jogador");
        add(txtNomeJogador);

        JLabel lblIp = new JLabel("IP Host:", SwingConstants.RIGHT);
        lblIp.setFont(new Font("SansSerif", Font.BOLD, (int) (11 * SCALE)));
        lblIp.setForeground(GameColors.GOLD_ACCENT);
        lblIp.setBounds((int) (280 * SCALE), (int) (52 * SCALE), (int) (65 * SCALE), (int) (28 * SCALE));
        add(lblIp);

        txtIpServer = createStyledTextField((int) (350 * SCALE), (int) (52 * SCALE), (int) (180 * SCALE), (int) (28 * SCALE), "127.0.0.1");
        add(txtIpServer);

        btnCriarSala = createActionButton("Criar Sala", (int) (105 * SCALE), (int) (88 * SCALE), (int) (180 * SCALE), (int) (28 * SCALE));
        btnConectar = createActionButton("Entrar em Sala", (int) (315 * SCALE), (int) (88 * SCALE), (int) (180 * SCALE), (int) (28 * SCALE));
        add(btnCriarSala);
        add(btnConectar);

        // --- SUBTÍTULO ---
        JLabel subTitle = new JLabel("JOGADORES CONECTADOS", SwingConstants.CENTER);
        subTitle.setFont(new Font("SansSerif", Font.BOLD, (int) (13 * SCALE)));
        subTitle.setForeground(GameColors.GOLD_ACCENT);
        subTitle.setBounds(0, (int) (124 * SCALE), (int) (600 * SCALE), (int) (22 * SCALE));
        add(subTitle);

        // --- CARDS DOS JOGADORES ---
        int[][] cardPositions = {
            {(int) (40 * SCALE), (int) (152 * SCALE)},
            {(int) (320 * SCALE), (int) (152 * SCALE)},
            {(int) (40 * SCALE), (int) (265 * SCALE)},
            {(int) (320 * SCALE), (int) (265 * SCALE)}
        };

        for (int i = 0; i < 4; i++) {
            int posX = cardPositions[i][0];
            int posY = cardPositions[i][1];
            final int slotIndex = i;

            // Ícone do Slot
            String iconSymbol = (i == 0) ? "👑" : "💻";
            slotIcons[i] = SlotsIcon.slotIconLabel(iconSymbol, posX + (int) (10 * SCALE), posY + (int) (10 * SCALE), SCALE);
            add(slotIcons[i]);

            // Campo de Nome nos Cards
            slotNames[i] = createPlayerTextField(posX + (int) (52 * SCALE), posY + (int) (10 * SCALE), (int) (173 * SCALE), (int) (32 * SCALE), "Slot " + (i + 1) + ": [ VAZIO ]");
            slotNames[i].setEnabled(false);
            add(slotNames[i]);

            // ComboBox de Cores
            slotColors[i] = createColorComboBox(posX + (int) (52 * SCALE), posY + (int) (50 * SCALE), (int) (173 * SCALE), (int) (28 * SCALE));
            slotColors[i].setSelectedIndex(i);
            slotColors[i].setEnabled(false);
            add(slotColors[i]);

            // Listener de troca de cor
            slotColors[i].addActionListener(e -> {
                String corNome = (String) slotColors[slotIndex].getSelectedItem();
                slotIcons[slotIndex].setForeground(getColorByString(corNome));

                if (!updatingFromNetwork && client != null && client.getMyPlayerId() == slotIndex) {
                    enviarAtualizacaoPerfil();
                }
            });

            // Listener de edição de nome
            slotNames[i].addActionListener(e -> {
                if (!updatingFromNetwork && client != null && client.getMyPlayerId() == slotIndex) {
                    enviarAtualizacaoPerfil();
                }
            });
        }

        // --- BOTÃO JOGAR ---
        CustomButton playBtn = new PlayButton();
        playBtn.setBounds((int) (200 * SCALE), (int) (435 * SCALE), (int) (200 * SCALE), (int) (45 * SCALE));
        
        // Invisível e desabilitado por padrão até confirmar se é o Host
        playBtn.setVisible(false);
        playBtn.setEnabled(false);
        
        this.btnIniciarJogo = playBtn;
        add(btnIniciarJogo);

        configurarAcoes();
    }

    /**
     * Valida e confirma a saída do jogador da sala com um diálogo estilizado.
     * @return true se o jogador aceitou sair ou se não estava conectado.
     */
    public boolean confirmarSaidaDoLobby() {
        if (client == null || !client.isConnected()) {
            return true;
        }

        boolean isHost = (client.getMyPlayerId() == 0);
        String mensagem;
        String titulo;

        if (isHost) {
            mensagem = "Você é o Host da sala.<br>Se sair agora, a sala será <b>DESFEITA</b> para todos os jogadores.<br><br>Deseja realmente sair?";
            titulo = "DESFAZER SALA?";
        } else {
            mensagem = "Você está conectado em uma sala.<br><br>Deseja realmente sair da sala?";
            titulo = "SAIR DA SALA?";
        }

        boolean confirmado = mostrarConfirmacaoCustomizada(titulo, mensagem);

        if (confirmado) {
            executarSaidaLobby(isHost);
            return true;
        }

        return false;
    }

    /**
     * Exibe a janela de diálogo customizada combinando com a identidade gráfica do jogo.
     */
    private boolean mostrarConfirmacaoCustomizada(String titulo, String mensagem) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        int width = (int) (380 * SCALE);
        int height = (int) (200 * SCALE);

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fundo Roxo Escuro
                g2.setColor(new Color(30, 18, 38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int) (18 * SCALE), (int) (18 * SCALE));
                
                // Borda Dourada
                g2.setColor(GameColors.GOLD_ACCENT);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, (int) (18 * SCALE), (int) (18 * SCALE));
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setLayout(null);
        content.setPreferredSize(new Dimension(width, height));

        // Título estilizado
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, (int) (15 * SCALE)));
        lblTitulo.setForeground(GameColors.GOLD_ACCENT);
        lblTitulo.setBounds(0, (int) (16 * SCALE), width, (int) (24 * SCALE));
        content.add(lblTitulo);

        // Mensagem com suporte a HTML
        String htmlMsg = "<html><div style='text-align: center; color: #FFFFFF; font-family: sans-serif; font-size: " 
                + (int) (10 * SCALE) + "px;'>" + mensagem + "</div></html>";
        JLabel lblMsg = new JLabel(htmlMsg, SwingConstants.CENTER);
        lblMsg.setBounds((int) (20 * SCALE), (int) (45 * SCALE), width - (int) (40 * SCALE), (int) (85 * SCALE));
        content.add(lblMsg);

        final boolean[] resposta = {false};

        // Botão SIM
        JButton btnSim = createActionButton("SIM", (int) (55 * SCALE), (int) (142 * SCALE), (int) (120 * SCALE), (int) (34 * SCALE));
        btnSim.addActionListener(e -> {
            resposta[0] = true;
            dialog.dispose();
        });

        // Botão NÃO
        JButton btnNao = createActionButton("NÃO", (int) (205 * SCALE), (int) (142 * SCALE), (int) (120 * SCALE), (int) (34 * SCALE));
        btnNao.addActionListener(e -> {
            resposta[0] = false;
            dialog.dispose();
        });

        content.add(btnSim);
        content.add(btnNao);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setVisible(true);

        return resposta[0];
    }

    private void executarSaidaLobby(boolean isHost) {
        if (isHost) {
            if (server != null) {
                server.stopServer();
                server = null;
            }
        }
        if (client != null) {
            client.disconnect();
        }
        desbloquearCamposConexao();
    }

    private void desbloquearCamposConexao() {
        txtNomeJogador.setEnabled(true);
        txtIpServer.setEnabled(true);
        btnCriarSala.setEnabled(true);
        btnConectar.setEnabled(true);
        btnIniciarJogo.setVisible(false);
        btnIniciarJogo.setEnabled(false);
        primeiraSincronizacaoFeita = false;
    }

    private void enviarAtualizacaoPerfil() {
        int meuId = client.getMyPlayerId();
        if (client != null && meuId != -1) {
            String nome = slotNames[meuId].getText().trim();
            if (nome.isEmpty() || nome.startsWith("Slot ")) {
                nome = getNomeDigitadoOuPadrao(meuId);
            }
            int corIndex = slotColors[meuId].getSelectedIndex();
            client.sendPlayerInfoUpdate(nome, corIndex);
        }
    }

    private String getNomeDigitadoOuPadrao(int slotIndex) {
        String nome = txtNomeJogador.getText().trim();
        if (nome.isEmpty() || nome.equalsIgnoreCase("Jogador")) {
            return "Jogador " + (slotIndex + 1);
        }
        return nome;
    }

    private void configurarAcoes() {
        btnCriarSala.addActionListener(e -> {
            primeiraSincronizacaoFeita = false;
            server = new GameServer();
            server.start();

            client.setLobbyScreen(this);
            client.setWindowManager(windowManager);
            client.connect("127.0.0.1", 12345);

            bloquearCamposConexao();
        });

        btnConectar.addActionListener(e -> {
            primeiraSincronizacaoFeita = false;
            String ip = txtIpServer.getText().trim();
            if (ip.isEmpty()) ip = "127.0.0.1";

            client.setLobbyScreen(this);
            client.setWindowManager(windowManager);
            client.connect(ip, 12345);

            bloquearCamposConexao();
        });

        // Ação de clique do Host
        btnIniciarJogo.addActionListener(e -> {
            if (client != null && client.getMyPlayerId() == 0) {
                client.requestStartGame();
            }
        });
    }

    private void bloquearCamposConexao() {
        txtNomeJogador.setEnabled(false);
        txtIpServer.setEnabled(false);
        btnCriarSala.setEnabled(false);
        btnConectar.setEnabled(false);
    }

    /**
     * Sincroniza o estado dos slots via rede
     */
    public void sincronizarLobby(PlayerInfo[] players, int meuId) {
        SwingUtilities.invokeLater(() -> {
            updatingFromNetwork = true;

            // Envia o nome digitado no topo assim que entra na sala
            if (!primeiraSincronizacaoFeita && meuId != -1) {
                primeiraSincronizacaoFeita = true;
                String nomeInicial = getNomeDigitadoOuPadrao(meuId);
                int corInicial = slotColors[meuId].getSelectedIndex();
                client.sendPlayerInfoUpdate(nomeInicial, corInicial);
            }

            for (int i = 0; i < 4; i++) {
                PlayerInfo p = players[i];
                boolean isMe = (i == meuId);
                boolean isHost = (i == 0);

                // Ícones dos Slots (👑 = Host Humano, 👤 = Jogador Humano, 💻 = CPU)
                if (p.isCPU()) {
                    slotIcons[i].setText("💻");
                } else if (isHost) {
                    slotIcons[i].setText("👑");
                } else {
                    slotIcons[i].setText("👤");
                }

                // Atualiza cor
                int corIndex = p.getColorIndex();
                if (corIndex >= 0 && corIndex < CORES_NOME.length) {
                    slotColors[i].setSelectedIndex(corIndex);
                    slotIcons[i].setForeground(getColorByString(CORES_NOME[corIndex]));
                }

                // Configurações de Edição por Slot
                if (isMe) {
                    slotNames[i].setEnabled(true);
                    slotColors[i].setEnabled(true);

                    if (!slotNames[i].hasFocus()) {
                        slotNames[i].setText(p.getName());
                        slotNames[i].setForeground(Color.WHITE);
                    }
                } else {
                    slotNames[i].setEnabled(false);
                    slotColors[i].setEnabled(false);

                    String hostTag = isHost ? " (Host)" : "";
                    if (p.isCPU()) {
                        slotNames[i].setText(p.getName());
                    } else {
                        slotNames[i].setText(p.getName() + " (Conectado)" + hostTag);
                    }
                    slotNames[i].setForeground(Color.LIGHT_GRAY);
                }
            }

            // VISIBILIDADE E PERMISSÃO: Exibido EXCLUSIVAMENTE para o Host (Slot 0)
            boolean souHost = (meuId == 0);
            btnIniciarJogo.setVisible(souHost);
            btnIniciarJogo.setEnabled(souHost);

            updatingFromNetwork = false;
            repaint();
        });
    }

    // --- MÉTODOS AUXILIARES DE DESIGN DA INTERFACE ---

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

        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.repaint();
                if (tf.getText().equals(placeholder) || tf.getText().startsWith("Slot ")) {
                    tf.setText("");
                    tf.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                tf.repaint();
                if (tf.getText().trim().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(Color.GRAY);
                } else {
                    enviarAtualizacaoPerfil();
                }
            }
        });

        return tf;
    }

    private JComboBox<String> createColorComboBox(int x, int y, int w, int h) {
        JComboBox<String> cb = new JComboBox<String>(CORES_NOME) {
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(GameColors.GOLD_ACCENT);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };

        cb.setOpaque(false);
        cb.setBounds(x, y, w, h);
        cb.setBackground(new Color(25, 14, 33));
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("SansSerif", Font.BOLD, (int) (12 * SCALE)));
        cb.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        cb.setFocusable(false);

        cb.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(GameColors.GOLD_ACCENT);
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        int[] xPoints = {cx - 4, cx + 4, cx};
                        int[] yPoints = {cy - 2, cy - 2, cy + 3};
                        g2.fillPolygon(xPoints, yPoints, 3);
                        g2.dispose();
                    }
                };
                btn.setOpaque(false);
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
                g2.fillRoundRect(0, 0, comboBox.getWidth() - 1, comboBox.getHeight() - 1, 8, 8);
                g2.dispose();
            }

            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                ListCellRenderer<Object> renderer = comboBox.getRenderer();
                Component c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
                c.setFont(comboBox.getFont());
                c.setBackground(new Color(25, 14, 33));
                c.setForeground(comboBox.isEnabled() ? Color.WHITE : new Color(150, 160, 180));

                currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, false);
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
                    lbl.setForeground(cb.isEnabled() ? Color.WHITE : new Color(150, 160, 180));
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
        tf.setFont(new Font("SansSerif", Font.PLAIN, (int) (11 * SCALE)));
        tf.setBackground(new Color(25, 14, 33));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(GameColors.GOLD_ACCENT);
        tf.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.repaint();
                if (tf.getText().equals(defaultText)) {
                    tf.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                tf.repaint();
                if (tf.getText().trim().isEmpty()) {
                    tf.setText(defaultText);
                }
            }
        });

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

                g2.fillRoundRect(0, 0, width, height, 8, 8);
                g2.setColor(b.isEnabled() ? GameColors.GOLD_ACCENT : Color.GRAY);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, width - 1, height - 1, 8, 8);

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

        // Fundo principal (Roxo)
        g2.setColor(GameColors.PURPLE_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int) (24 * SCALE), (int) (24 * SCALE));

        // Cards dos Jogadores
        Color cardBg = new Color(42, 24, 54);
        Color cardBorder = new Color(222, 179, 102, 60);

        int[][] cardPositions = {
            {(int) (40 * SCALE), (int) (152 * SCALE)},
            {(int) (320 * SCALE), (int) (152 * SCALE)},
            {(int) (40 * SCALE), (int) (265 * SCALE)},
            {(int) (320 * SCALE), (int) (265 * SCALE)}
        };

        int cardW = (int) (240 * SCALE);
        int cardH = (int) (95 * SCALE);

        for (int[] pos : cardPositions) {
            g2.setColor(cardBg);
            g2.fillRoundRect(pos[0], pos[1], cardW, cardH, (int) (12 * SCALE), (int) (12 * SCALE));

            g2.setColor(cardBorder);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(pos[0], pos[1], cardW, cardH, (int) (12 * SCALE), (int) (12 * SCALE));
        }

        // Borda externa (Dourada)
        g2.setColor(GameColors.GOLD_ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect((int) (2 * SCALE), (int) (2 * SCALE), getWidth() - (int) (5 * SCALE), getHeight() - (int) (5 * SCALE), (int) (24 * SCALE), (int) (24 * SCALE));
    }
}
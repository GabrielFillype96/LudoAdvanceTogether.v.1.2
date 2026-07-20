package gui.windows;

import network.GameClient;
import network.GameServer;

import javax.swing.*;
import java.awt.*;

public class LobbyScreen extends JPanel {

    private WindowManager windowManager;
    private GameServer server;
    private GameClient client;

    // Elementos da Interface
    private JTextField txtIpServer;
    private JButton btnCriarSala;
    private JButton btnConectar;
    private JButton btnIniciarJogo;
    private JButton btnVoltar;
    private JLabel[] slotLabels = new JLabel[4];

    public LobbyScreen(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.client = new GameClient();
        
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.client.setWindowManager(windowManager);

        initUI();
    }

    private void initUI() {
        // Painel Superior: Conexão
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.add(new JLabel("IP do Host:"));
        txtIpServer = new JTextField("localhost", 10);
        topPanel.add(txtIpServer);

        btnCriarSala = new JButton("Criar Sala (Host)");
        btnConectar = new JButton("Entrar em Sala");
        topPanel.add(btnCriarSala);
        topPanel.add(btnConectar);

        add(topPanel, BorderLayout.NORTH);

        // Painel Central: 4 Slots de Jogadores
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Jogadores na Sala"));

        for (int i = 0; i < 4; i++) {
            slotLabels[i] = new JLabel("Slot " + (i + 1) + ": [ VAZIO / CPU ]", SwingConstants.CENTER);
            slotLabels[i].setFont(new Font("Arial", Font.BOLD, 14));
            slotLabels[i].setOpaque(true);
            slotLabels[i].setBackground(new Color(230, 230, 230));
            centerPanel.add(slotLabels[i]);
        }

        add(centerPanel, BorderLayout.CENTER);

        // Painel Inferior: Ações do Lobby
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnIniciarJogo = new JButton("Iniciar Jogo");
        btnIniciarJogo.setEnabled(false); // Só o Host pode clicar quando pronto

        btnVoltar = new JButton("Voltar ao Menu");

        bottomPanel.add(btnVoltar);
        bottomPanel.add(btnIniciarJogo);

        add(bottomPanel, BorderLayout.SOUTH);

        // Eventos dos Botões
        configurarAcoes();
    }

    private void configurarAcoes() {
        // Ação: Criar Sala (Virar Host)
        btnCriarSala.addActionListener(e -> {
            server = new GameServer();
            server.startServer();

            // O Host se conecta no próprio servidor local
            client.setLobbyScreen(this);
            client.connect("localhost", 12345);

            btnCriarSala.setEnabled(false);
            btnConectar.setEnabled(false);
            txtIpServer.setEnabled(false);
            btnIniciarJogo.setEnabled(true);

            atualizarSlotUI(0, "Você (Host)");
            for (int i = 1; i < 4; i++) {
                atualizarSlotUI(i, "CPU (Aguardando Jogador)");
            }
        });

        // Ação: Entrar em Sala
        btnConectar.addActionListener(e -> {
            String ip = txtIpServer.getText().trim();
            if (ip.isEmpty()) ip = "localhost";

            client.setLobbyScreen(this);
            client.connect(ip, 12345);

            btnCriarSala.setEnabled(false);
            btnConectar.setEnabled(false);
            txtIpServer.setEnabled(false);
        });

        // Ação: Voltar
        btnVoltar.addActionListener(e -> {
            // Se for preciso, feche conexões ativas aqui no futuro
            if (windowManager != null) {
                // Chama o retorno ao menu principal
                // windowManager.openMainMenu(); (Ajuste para o seu método de voltar)
            }
        });

        btnIniciarJogo.addActionListener(e -> {
            if (server != null) {
                server.startGame(); // Dispara o início para todos
            }
        });
    }

    // Método para atualizar o texto visual dos slots na tela
    public void atualizarSlotUI(int slotIndex, String status) {
        if (slotIndex >= 0 && slotIndex < 4) {
            slotLabels[slotIndex].setText("Slot " + (slotIndex + 1) + ": " + status);
        }
    }

    /**
     * Atualiza os rótulos de todos os slots com base na resposta do servidor.
     */
    public void sincronizarLobby(boolean[] slotIsCPU, int meuId) {
        for (int i = 0; i < 4; i++) {
            if (i == meuId) {
                atualizarSlotUI(i, "Você (Jogador " + (i + 1) + ")");
            } else if (slotIsCPU[i]) {
                atualizarSlotUI(i, "CPU");
            } else {
                atualizarSlotUI(i, "Jogador " + (i + 1) + " (Conectado)");
            }
        }
    }
}
package network;

import com.google.gson.Gson;
import gui.theme.GameColors;
import gui.windows.LobbyScreen;
import gui.windows.WindowManager;
import control.GameManager;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int myPlayerId = -1;
    private final Gson gson = new Gson();
    
    private LobbyScreen lobbyScreen;
    private WindowManager windowManager;
    private GameManager gameManager;

    // Guarda a lista anterior de jogadores para detectar entradas e saídas
    private PlayerInfo[] previousPlayers = null;
    
    // Referência do aviso ativo para substituir pop-ups antigos sem empilhar
    private JDialog avisoAtualDialog = null;

    public void setLobbyScreen(LobbyScreen lobbyScreen) { this.lobbyScreen = lobbyScreen; }
    public void setWindowManager(WindowManager windowManager) { this.windowManager = windowManager; }
    public void setGameManager(GameManager gameManager) { this.gameManager = gameManager; }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public void connect(String ip, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("ASSIGN_ID:")) {
                        myPlayerId = Integer.parseInt(line.split(":")[1]);
                        System.out.println("[Cliente] Conectado! Meu ID: " + myPlayerId);
                    } else {
                        NetworkMessage msg = gson.fromJson(line, NetworkMessage.class);
                        processIncomingMessage(msg);
                    }
                }
            } catch (Exception e) {
                System.err.println("[Cliente] Conexão encerrada: " + e.getMessage());
            } finally {
                disconnect();
            }
        }).start();
    }

    public void send(NetworkMessage msg) {
        if (out != null) {
            out.println(gson.toJson(msg));
        }
    }

    public void sendPlayerInfoUpdate(String name, int colorIndex) {
        PlayerInfo info = new PlayerInfo(myPlayerId, name, colorIndex, false);
        send(new NetworkMessage("UPDATE_PLAYER_INFO", myPlayerId, gson.toJson(info)));
    }

    public void requestStartGame() {
        if (myPlayerId == 0) {
            send(new NetworkMessage("REQUEST_START_GAME", myPlayerId, ""));
        }
    }

    private void processIncomingMessage(NetworkMessage msg) {
        SwingUtilities.invokeLater(() -> {
            if (msg == null || msg.getType() == null) return;

            switch (msg.getType()) {
                case "LOBBY_UPDATE":
                    PlayerInfo[] players = gson.fromJson(msg.getPayload(), PlayerInfo[].class);

                    // Se eu for o HOST, verifica se algum jogador realmente entrou ou saiu
                    if (myPlayerId == 0 && previousPlayers != null) {
                        verificarEntradaSaidaJogadores(previousPlayers, players);
                    }
                    previousPlayers = players;

                    if (lobbyScreen != null) {
                        lobbyScreen.sincronizarLobby(players, myPlayerId);
                    }
                    break;

                case "START_GAME":
                    PlayerInfo[] finalPlayers = gson.fromJson(msg.getPayload(), PlayerInfo[].class);
                    boolean[] slotIsCPU = new boolean[finalPlayers.length];
                    for (int i = 0; i < finalPlayers.length; i++) {
                        slotIsCPU[i] = finalPlayers[i].isCPU();
                    }
                    if (windowManager != null) {
                        windowManager.startOnlineGame(this, myPlayerId, slotIsCPU);
                    }
                    break;

                case "DISBAND":
                    String mensagemAviso = (msg.getPayload() != null && !msg.getPayload().trim().isEmpty())
                            ? msg.getPayload()
                            : "A sala foi desfeita pelo Host.";

                    disconnect();

                    boolean estavaNoLobby = (lobbyScreen != null && lobbyScreen.isShowing());

                    if (estavaNoLobby) {
                        if (windowManager != null) {
                            windowManager.openLobbyMultiplayer();
                        }
                        mostrarAvisoCustomizado("SALA ENCERRADA", mensagemAviso);
                    }
                    break;

                case "MOVE_PAWN":
                    if (gameManager != null) {
                        String[] parts = msg.getPayload().split(":");
                        int pawnIndex = Integer.parseInt(parts[0]);
                        int steps = Integer.parseInt(parts[1]);
                        String effect = parts.length > 2 ? parts[2] : "";

                        if (msg.getPlayerId() != myPlayerId) {
                            gameManager.moveChosenPawn(pawnIndex, steps, effect);
                        }
                    }
                    break;

                case "NEXT_TURN":
                    if (gameManager != null && gameManager.getTurnManager() != null) {
                        int nextPlayerId = Integer.parseInt(msg.getPayload());
                        gameManager.getTurnManager().setTurn(nextPlayerId);
                    }
                    break;
            }
        });
    }

    /**
     * Compara a lista anterior com a nova para notificar o Host com o nome correto do jogador.
     */
    private void verificarEntradaSaidaJogadores(PlayerInfo[] antigos, PlayerInfo[] novos) {
        if (antigos == null || novos == null) return;

        int len = Math.min(antigos.length, novos.length);

        for (int i = 0; i < len; i++) {
            if (i == myPlayerId) continue; // Ignora o próprio Host

            PlayerInfo pAntigo = antigos[i];
            PlayerInfo pNovo = novos[i];

            boolean antigoEraHumano = (pAntigo != null && !pAntigo.isCPU());
            boolean novoEHumano = (pNovo != null && !pNovo.isCPU());

            String nomeAntigo = (pAntigo != null && pAntigo.getName() != null) ? pAntigo.getName().trim() : "";
            String nomeNovo = (pNovo != null && pNovo.getName() != null) ? pNovo.getName().trim() : "";
            String nomePadraoSlot = "Jogador " + (i + 1);

            // 1. Um jogador humano acabou de entrar no slot
            if (!antigoEraHumano && novoEHumano) {
                String nomeExibicao = !nomeNovo.isEmpty() ? nomeNovo : nomePadraoSlot;
                mostrarAvisoCustomizado("JOGADOR CONECTADO", nomeExibicao + " entrou na sala!");
            }
            // 2. O jogador já conectado atualizou o nome temporário ("Jogador X") para o nome real escolhido
            else if (antigoEraHumano && novoEHumano) {
                boolean eraNomePadrao = nomeAntigo.equalsIgnoreCase(nomePadraoSlot);
                boolean agoraEhNomeCustomizado = !nomeNovo.isEmpty() && !nomeNovo.equalsIgnoreCase(nomePadraoSlot);

                if (eraNomePadrao && agoraEhNomeCustomizado) {
                    mostrarAvisoCustomizado("JOGADOR CONECTADO", nomeNovo + " entrou na sala!");
                }
            }
            // 3. Um jogador humano saiu da sala
            else if (antigoEraHumano && !novoEHumano) {
                String nomeExibicao = (!nomeAntigo.isEmpty() && !nomeAntigo.equalsIgnoreCase(nomePadraoSlot)) 
                        ? nomeAntigo : nomePadraoSlot;
                mostrarAvisoCustomizado("JOGADOR DESCONECTADO", nomeExibicao + " saiu da sala!");
            }
        }
    }

    public int getMyPlayerId() { return myPlayerId; }

    public void disconnect() {
        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (in != null) {
                in.close();
                in = null;
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                socket = null;
            }
            myPlayerId = -1;
            previousPlayers = null;
        } catch (Exception e) {
            System.err.println("[Cliente] Erro ao fechar: " + e.getMessage());
        }
    }

    /**
     * Exibe o diálogo de aviso com a identidade visual exata das telas do jogo.
     */
    private void mostrarAvisoCustomizado(String titulo, String mensagem) {
        // Se já houver um aviso na tela (ex: o temporário "Jogador 2 entrou"), fecha-o antes de exibir o novo
        if (avisoAtualDialog != null && avisoAtualDialog.isDisplayable()) {
            avisoAtualDialog.dispose();
        }

        Window parentWindow = lobbyScreen != null ? SwingUtilities.getWindowAncestor(lobbyScreen) : null;
        JDialog dialog = new JDialog(parentWindow, Dialog.ModalityType.MODELESS);
        avisoAtualDialog = dialog;

        dialog.setUndecorated(true);
        dialog.setBackground(new Color(0, 0, 0, 0));

        double scale = 1.5;
        int width = (int) (380 * scale);
        int height = (int) (180 * scale);

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fundo Roxo Escuro
                g2.setColor(new Color(30, 18, 38));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int) (18 * scale), (int) (18 * scale));
                
                // Borda Dourada
                g2.setColor(GameColors.GOLD_ACCENT);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, (int) (18 * scale), (int) (18 * scale));
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setLayout(null);
        content.setPreferredSize(new Dimension(width, height));

        // Título estilizado
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, (int) (15 * scale)));
        lblTitulo.setForeground(GameColors.GOLD_ACCENT);
        lblTitulo.setBounds(0, (int) (16 * scale), width, (int) (24 * scale));
        content.add(lblTitulo);

        // Mensagem estilizada em HTML
        String htmlMsg = "<html><div style='text-align: center; color: #FFFFFF; font-family: sans-serif; font-size: " 
                + (int) (10 * scale) + "px;'>" + mensagem + "</div></html>";
        JLabel lblMsg = new JLabel(htmlMsg, SwingConstants.CENTER);
        lblMsg.setBounds((int) (20 * scale), (int) (45 * scale), width - (int) (40 * scale), (int) (65 * scale));
        content.add(lblMsg);

        // Botão OK centralizado
        int btnW = (int) (120 * scale);
        int btnH = (int) (34 * scale);
        int btnX = (width - btnW) / 2;
        int btnY = (int) (125 * scale);

        JButton btnOk = createActionButton("OK", btnX, btnY, btnW, btnH, scale);
        btnOk.addActionListener(e -> dialog.dispose());
        content.add(btnOk);

        dialog.setContentPane(content);
        dialog.pack();

        if (parentWindow != null) {
            dialog.setLocationRelativeTo(parentWindow);
        } else {
            dialog.setLocationRelativeTo(null);
        }
        dialog.setVisible(true);
    }

    private JButton createActionButton(String text, int x, int y, int w, int h, double scale) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setFont(new Font("SansSerif", Font.BOLD, (int) (11 * scale)));
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
}
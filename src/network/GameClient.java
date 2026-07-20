package network;

import com.google.gson.Gson;
import gui.windows.LobbyScreen;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int myPlayerId = -1;
    private Gson gson = new Gson();
    private LobbyScreen lobbyScreen;
    private gui.windows.WindowManager windowManager;

    public void setLobbyScreen(LobbyScreen lobbyScreen) {
        this.lobbyScreen = lobbyScreen;
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
                        System.out.println("[Cliente] Conectado com ID: " + myPlayerId);
                    } else {
                        NetworkMessage msg = gson.fromJson(line, NetworkMessage.class);
                        processIncomingMessage(msg);
                    }
                }
            } catch (Exception e) {
                System.err.println("[Cliente] Erro ao conectar: " + e.getMessage());
            }
        }).start();
    }

    private void processIncomingMessage(NetworkMessage msg) {
        SwingUtilities.invokeLater(() -> {
            switch (msg.getType()) {
                case "LOBBY_UPDATE":
                    boolean[] slotIsCPU = gson.fromJson(msg.getPayload(), boolean[].class);
                    if (lobbyScreen != null) {
                        lobbyScreen.sincronizarLobby(slotIsCPU, myPlayerId);
                    }
                    break;

                case "START_GAME":
                    boolean[] finalSlotIsCPU = gson.fromJson(msg.getPayload(), boolean[].class);
                    if (windowManager != null) {
                        // Chama o WindowManager para trocar a tela para o tabuleiro online
                        windowManager.startOnlineGame(this, myPlayerId, finalSlotIsCPU);
                    }
                    break;
            }
        });
    }

    public int getMyPlayerId() { return myPlayerId; }

    public void setWindowManager(gui.windows.WindowManager windowManager) {
        this.windowManager = windowManager;
    }
}
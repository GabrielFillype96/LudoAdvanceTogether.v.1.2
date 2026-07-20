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
    private control.GameManager gameManager;

    public void setLobbyScreen(LobbyScreen lobbyScreen) {
        this.lobbyScreen = lobbyScreen;
    }

    public void setGameManager(control.GameManager gameManager) {
        this.gameManager = gameManager;
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

    public void send(NetworkMessage msg) {
        if (out != null) {
            out.println(gson.toJson(msg));
        }
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
                        windowManager.startOnlineGame(this, myPlayerId, finalSlotIsCPU);
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

    public int getMyPlayerId() { return myPlayerId; }

    public void setWindowManager(gui.windows.WindowManager windowManager) {
        this.windowManager = windowManager;
    }
}
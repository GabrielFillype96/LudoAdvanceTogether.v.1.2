package network;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameServer {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;
    
    // CopyOnWriteArrayList para evitar ConcurrentModificationException durante iterações de broadcast/encerramento
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    
    // Lista de informações dos 4 jogadores
    private final PlayerInfo[] players = new PlayerInfo[]{
        new PlayerInfo(0, "Jogador 1 (CPU)", 0, true),
        new PlayerInfo(1, "Jogador 2 (CPU)", 1, true),
        new PlayerInfo(2, "Jogador 3 (CPU)", 2, true),
        new PlayerInfo(3, "Jogador 4 (CPU)", 3, true)
    };
    
    private final Gson gson = new Gson();

    public void start() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("[Servidor] Servidor rodando na porta " + PORT);

                while (!serverSocket.isClosed()) {
                    Socket socket = serverSocket.accept();
                    int assignedId = getNextAvailableSlot();

                    if (assignedId != -1) {
                        players[assignedId].setCPU(false);
                        players[assignedId].setName("Jogador " + (assignedId + 1));
                        
                        ClientHandler handler = new ClientHandler(socket, assignedId);
                        clients.add(handler);
                        new Thread(handler).start();

                        handler.sendRaw("ASSIGN_ID:" + assignedId);
                        broadcastLobbyUpdate();
                        
                        System.out.println("[Servidor] Jogador conectado no Slot " + assignedId);
                    } else {
                        socket.close();
                    }
                }
            } catch (Exception e) {
                System.out.println("[Servidor] Encerrado.");
            }
        }).start();
    }

    /**
     * Notifica todos os clientes que a sala foi desfeita e encerra o servidor.
     */
    public void stopServer() {
        try {
            // Notifica os clientes que a sala foi encerrada pelo Host
            NetworkMessage disbandMsg = new NetworkMessage("DISBAND", 0, "A sala foi desfeita pelo Host.");
            broadcast(disbandMsg);

            // Encerra a conexão de todos os clientes
            for (ClientHandler client : clients) {
                client.closeConnection();
            }
            clients.clear();

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("[Servidor] Servidor e sala encerrados.");
        } catch (Exception e) {
            System.err.println("[Servidor] Erro ao fechar: " + e.getMessage());
        }
    }

    private int getNextAvailableSlot() {
        for (int i = 0; i < players.length; i++) {
            if (players[i].isCPU()) {
                return i;
            }
        }
        return -1;
    }

    public void broadcastLobbyUpdate() {
        String payload = gson.toJson(players);
        NetworkMessage msg = new NetworkMessage("LOBBY_UPDATE", -1, payload);
        broadcast(msg);
    }

    public void startGame() {
        String payload = gson.toJson(players);
        NetworkMessage msg = new NetworkMessage("START_GAME", -1, payload);
        broadcast(msg);
    }

    public void broadcast(NetworkMessage msg) {
        String json = gson.toJson(msg);
        for (ClientHandler client : clients) {
            client.sendRaw(json);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final int playerId;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, int playerId) {
            this.socket = socket;
            this.playerId = playerId;
            try {
                this.out = new PrintWriter(socket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void sendRaw(String text) {
            if (out != null) out.println(text);
        }

        public void closeConnection() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (Exception e) {
                // Silencia exceções ao fechar conexões
            }
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    NetworkMessage msg = gson.fromJson(line, NetworkMessage.class);
                    
                    if ("REQUEST_START_GAME".equals(msg.getType())) {
                        startGame();
                    } else if ("UPDATE_PLAYER_INFO".equals(msg.getType())) {
                        PlayerInfo updatedInfo = gson.fromJson(msg.getPayload(), PlayerInfo.class);
                        players[playerId].setName(updatedInfo.getName());
                        players[playerId].setColorIndex(updatedInfo.getColorIndex());
                        broadcastLobbyUpdate();
                    } else {
                        broadcast(msg);
                    }
                }
            } catch (Exception e) {
                System.out.println("[Servidor] Cliente " + playerId + " desconectou.");
            } finally {
                disconnect();
            }
        }

        private void disconnect() {
            try {
                clients.remove(this);
                players[playerId].setCPU(true);
                players[playerId].setName("Jogador " + (playerId + 1) + " (CPU)");
                closeConnection();

                // Se quem desconectou foi o Host (Slot 0), encerra a sala para todos os demais
                if (playerId == 0) {
                    System.out.println("[Servidor] Host desconectou. Encerrando a sala...");
                    stopServer();
                } else {
                    broadcastLobbyUpdate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
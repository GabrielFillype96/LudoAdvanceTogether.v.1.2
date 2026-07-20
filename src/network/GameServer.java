package network;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    
    // true = Vazio/CPU, false = Jogador Humano Conectado
    private final boolean[] slotIsCPU = new boolean[]{true, true, true, true}; 
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
                        slotIsCPU[assignedId] = false; // Marca o slot como ocupado por um humano
                        
                        ClientHandler handler = new ClientHandler(socket, assignedId);
                        clients.add(handler);
                        new Thread(handler).start();

                        // 1. Envia o ID individual para o cliente recém-conectado
                        handler.sendRaw("ASSIGN_ID:" + assignedId);

                        // 2. Transmite o estado atualizado do Lobby para TODOS os clientes
                        broadcastLobbyUpdate();
                        
                        System.out.println("[Servidor] Jogador conectado no Slot " + assignedId);
                    } else {
                        System.out.println("[Servidor] Tentativa de conexão recusada: Sala cheia.");
                        socket.close();
                    }
                }
            } catch (Exception e) {
                System.err.println("[Servidor] Erro no servidor: " + e.getMessage());
            }
        }).start();
    }

    private int getNextAvailableSlot() {
        for (int i = 0; i < slotIsCPU.length; i++) {
            if (slotIsCPU[i]) {
                return i;
            }
        }
        return -1;
    }

    // --- MÉTODOS DO SERVIDOR ---

    public void startGame() {
        String payload = gson.toJson(slotIsCPU);
        NetworkMessage msg = new NetworkMessage("START_GAME", -1, payload);
        broadcast(msg);
    }

    public void broadcastLobbyUpdate() {
        String payload = gson.toJson(slotIsCPU);
        NetworkMessage msg = new NetworkMessage("LOBBY_UPDATE", -1, payload);
        broadcast(msg);
    }

    public void broadcast(NetworkMessage msg) {
        String json = gson.toJson(msg);
        for (ClientHandler client : clients) {
            client.sendRaw(json);
        }
    }

    // --- CLASSE INTERNA PARA TRATAR CLIENTES ---

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
            if (out != null) {
                out.println(text);
            }
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    NetworkMessage msg = gson.fromJson(line, NetworkMessage.class);
                    
                    // Repassa as mensagens do jogo para todos
                    broadcast(msg);
                }
            } catch (Exception e) {
                System.out.println("[Servidor] Conexão encerrada com o Jogador " + playerId);
            } finally {
                disconnect();
            }
        }

        private void disconnect() {
            try {
                clients.remove(this);
                slotIsCPU[playerId] = true; // Libera a vaga no servidor
                socket.close();
                
                // Notifica os jogadores restantes sobre a desconexão
                broadcastLobbyUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
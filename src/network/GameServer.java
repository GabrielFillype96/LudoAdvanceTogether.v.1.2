package network;

import com.google.gson.Gson;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    // CORREÇÃO 1: Todos os slots começam como CPU (true). O primeiro a conectar (Host) vai ocupar o slot 0.
    private final boolean[] slotIsCPU = new boolean[]{true, true, true, true};
    private final Gson gson = new Gson();
    private final int PORT = 12345;

    public void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("[Servidor] Sala criada na porta " + PORT);

                // CORREÇÃO 2: Aceita até 4 conexões (Slot 0 = Host, Slots 1, 2 e 3 = Convidados)
                while (clients.size() < 4) {
                    Socket socket = serverSocket.accept();
                    int assignedSlot = getNextAvailableSlot();

                    if (assignedSlot != -1) {
                        slotIsCPU[assignedSlot] = false; // O slot deixa de ser CPU e vira humano
                        ClientHandler client = new ClientHandler(socket, assignedSlot, this);
                        clients.add(client);
                        new Thread(client).start();
                        
                        broadcastLobbyStatus();
                    }
                }
            } catch (Exception e) {
                System.err.println("[Servidor] Erro no servidor: " + e.getMessage());
            }
        }).start();
    }

    // CORREÇÃO 3: O laço agora começa do índice 0
    private int getNextAvailableSlot() {
        for (int i = 0; i < 4; i++) {
            if (slotIsCPU[i]) return i;
        }
        return -1;
    }

    public void broadcast(NetworkMessage msg) {
        String json = gson.toJson(msg);
        for (ClientHandler client : clients) {
            client.send(json);
        }
    }

    public void broadcastLobbyStatus() {
        broadcast(new NetworkMessage("LOBBY_UPDATE", 0, gson.toJson(slotIsCPU)));
    }

    public void startGame() {
        broadcast(new NetworkMessage("START_GAME", 0, gson.toJson(slotIsCPU)));
    }
}
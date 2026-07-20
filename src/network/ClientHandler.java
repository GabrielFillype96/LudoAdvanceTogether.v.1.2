package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private int playerId;
    private GameServer server;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, int playerId, GameServer server) {
        this.socket = socket;
        this.playerId = playerId;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Informa ao cliente recém-conectado qual é o ID dele (1, 2 ou 3)
            out.println("ASSIGN_ID:" + playerId);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Quando o cliente envia uma jogada, o servidor retransmite para todo mundo
                server.broadcast(new NetworkMessage("GAME_ACTION", playerId, inputLine));
            }
        } catch (Exception e) {
            System.out.println("[Servidor] Cliente " + playerId + " desconectou.");
        }
    }

    public void send(String msg) {
        if (out != null) out.println(msg);
    }
}
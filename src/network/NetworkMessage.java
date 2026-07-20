package network;

public class NetworkMessage {
    private String type;      // Ex: "LOBBY_UPDATE", "START_GAME", "MOVE_PAWN", "DRAW_CARD"
    private int playerId;     // ID do jogador (0 a 3)
    private String payload;   // Dados extras em JSON ou Texto (ex: nome, peão movido, carta)

    public NetworkMessage(String type, int playerId, String payload) {
        this.type = type;
        this.playerId = playerId;
        this.payload = payload;
    }

    public String getType() { return type; }
    public int getPlayerId() { return playerId; }
    public String getPayload() { return payload; }
}
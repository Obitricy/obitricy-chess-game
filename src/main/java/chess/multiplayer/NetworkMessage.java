package chess.multiplayer;

public class NetworkMessage {

    public enum Type {
        REGISTER_NAME,
        ONLINE_PLAYERS,
        PLAY_REQUEST,
        PLAY_REQUEST_SENT,
        PLAY_REQUEST_RECEIVED,
        PLAY_REQUEST_ACCEPT,
        PLAY_REQUEST_DECLINE,
        CREATE_GAME,
        JOIN_GAME,
        GAME_CREATED,
        GAME_JOINED,
        WAITING,
        START_GAME,
        MOVE,
        MOVE_REJECTED,
        UNDO,
        REDO,
        RESIGN,
        REMATCH,
        ERROR,
        DISCONNECT
    }

    private final Type type;
    private final String data;

    public NetworkMessage(Type type, String data) {
        this.type = type;
        this.data = data;
    }

    public Type getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String serialize() {
        return type.name() + "|" + (data == null ? "" : data);
    }

    public static NetworkMessage deserialize(String message) {

        if (message == null || message.isBlank()) {
            return null;
        }

        String[] parts = message.split("\\|", 2);

        try {
            Type type = Type.valueOf(parts[0].trim());

            String data =
                    parts.length > 1
                            ? parts[1]
                            : "";

            return new NetworkMessage(type, data);

        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}

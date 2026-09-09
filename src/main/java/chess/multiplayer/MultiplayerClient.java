package chess.multiplayer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

/**
 * Internet multiplayer client using WebSockets.
 *
 * The public server can be reached with wss:// while local development can
 * use ws://. The existing chess message protocol is unchanged.
 */
public class MultiplayerClient {

    private WebSocketClient socket;
    private volatile boolean connected;
    private volatile String playerName = "Player";
    private Consumer<NetworkMessage> messageListener;

    public void connect(String host, int port, Consumer<NetworkMessage> listener) throws Exception {
        this.messageListener = listener;
        String raw = host == null ? "" : host.trim();
        if (raw.isBlank()) throw new IllegalArgumentException("Server host is required.");

        String scheme;
        if (raw.startsWith("ws://") || raw.startsWith("wss://")) {
            scheme = raw.startsWith("wss://") ? "wss" : "ws";
        } else {
            scheme = Boolean.parseBoolean(System.getProperty("obitricy.websocket.secure", "false")) ? "wss" : "ws";
            raw = raw.replaceFirst("^https?://", "");
        }

        URI uri = new URI(scheme + "://" + raw + ":" + port + "/");
        socket = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                connected = true;
                MultiplayerClient.this.send(NetworkMessage.Type.REGISTER_NAME, playerName);
            }
            @Override public void onMessage(String message) {
                NetworkMessage parsed = NetworkMessage.deserialize(message);
                if (parsed != null && messageListener != null) messageListener.accept(parsed);
            }
            @Override public void onClose(int code, String reason, boolean remote) {
                connected = false;
            }
            @Override public void onError(Exception ex) {
                connected = false;
                notifyError("Connection error: " + ex.getMessage());
            }
        };
        socket.connectBlocking();
        if (!socket.isOpen()) {
            throw new IllegalStateException("Unable to establish WebSocket connection.");
        }
    }

    public synchronized void send(NetworkMessage.Type type, String data) {
        if (!connected || socket == null || !socket.isOpen()) return;
        socket.send(new NetworkMessage(type, data).serialize());
    }

    private void notifyError(String message) {
        if (messageListener != null) messageListener.accept(new NetworkMessage(NetworkMessage.Type.ERROR, message));
    }

    public void createGame() { send(NetworkMessage.Type.CREATE_GAME, ""); }
    public void joinGame(String gameId) { send(NetworkMessage.Type.JOIN_GAME, gameId); }
    public void sendMove(String moveData) { send(NetworkMessage.Type.MOVE, moveData); }
    public void sendMove(int fromRow, int fromCol, int toRow, int toCol) { sendMove(fromRow, fromCol, toRow, toCol, ""); }
    public void sendMove(int fromRow, int fromCol, int toRow, int toCol, String promotion) {
        String safePromotion = promotion == null ? "" : promotion.trim();
        send(NetworkMessage.Type.MOVE, fromRow + "," + fromCol + "," + toRow + "," + toCol + "," + safePromotion);
    }
    public void sendUndo() { send(NetworkMessage.Type.UNDO, ""); }
    public void sendRedo() { send(NetworkMessage.Type.REDO, ""); }
    public void sendResign() { send(NetworkMessage.Type.RESIGN, ""); }
    public void sendRematch() { send(NetworkMessage.Type.REMATCH, ""); }
    public void resign() { sendResign(); }
    public void rematch() { sendRematch(); }

    public synchronized void setPlayerName(String playerName) {
        if (playerName == null || playerName.isBlank()) this.playerName = "Player";
        else this.playerName = playerName.trim().substring(0, Math.min(24, playerName.trim().length()));
        if (connected) send(NetworkMessage.Type.REGISTER_NAME, this.playerName);
    }
    public String getPlayerName() { return playerName; }
    public void requestPlay(String playerName) { send(NetworkMessage.Type.PLAY_REQUEST, playerName); }
    public void acceptPlayRequest(String playerName) { send(NetworkMessage.Type.PLAY_REQUEST_ACCEPT, playerName); }
    public void declinePlayRequest(String playerName) { send(NetworkMessage.Type.PLAY_REQUEST_DECLINE, playerName); }
    public boolean isConnected() { return connected; }

    public void disconnect() {
        connected = false;
        if (socket != null) {
            try { socket.close(); } catch (Exception ignored) { }
            socket = null;
        }
    }

    public void setMessageListener(Consumer<NetworkMessage> listener) { this.messageListener = listener; }
}

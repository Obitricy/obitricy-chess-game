package chess.multiplayer;

import org.java_websocket.WebSocket;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Represents one connected player. It can be backed by the original TCP
 * socket or by a WebSocket connection used for Internet play.
 */
public class ClientConnection implements Runnable {

    private final Socket socket;
    private final WebSocket webSocket;
    private final ChessServer server;
    private BufferedReader reader;
    private PrintWriter writer;

    private volatile GameRoom gameRoom;
    private volatile boolean white;
    private volatile boolean running = true;
    private volatile String playerName = "Player";
    private final String connectionId;

    public ClientConnection(Socket socket, ChessServer server) {
        this.socket = socket;
        this.webSocket = null;
        this.server = server;
        this.connectionId = socket == null ? "unknown" : String.valueOf(socket.getPort());
    }

    public static ClientConnection forWebSocket(WebSocket webSocket, ChessServer server) {
        return new ClientConnection(webSocket, server, true);
    }

    private ClientConnection(WebSocket webSocket, ChessServer server, boolean webSocketConnection) {
        this.socket = null;
        this.webSocket = webSocket;
        this.server = server;
        this.connectionId = webSocket == null || webSocket.getRemoteSocketAddress() == null
                ? "websocket"
                : String.valueOf(webSocket.getRemoteSocketAddress());
    }

    @Override
    public void run() {
        if (socket == null) return;
        try {
            socket.setTcpNoDelay(true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            send(new NetworkMessage(NetworkMessage.Type.WAITING, "Connected to chess server."));

            String line;
            while (running && (line = reader.readLine()) != null) {
                receive(line);
            }
        } catch (IOException ex) {
            if (running) System.out.println("Client disconnected: " + ex.getMessage());
        } finally {
            disconnect();
        }
    }

    public void receive(String line) {
        NetworkMessage message = NetworkMessage.deserialize(line);
        if (message == null) {
            send(new NetworkMessage(NetworkMessage.Type.ERROR, "Invalid network message."));
            return;
        }
        server.handleMessage(this, message);
    }

    public synchronized void send(NetworkMessage message) {
        if (message == null || !running) return;
        String serialized = message.serialize();
        if (webSocket != null) {
            if (webSocket.isOpen()) webSocket.send(serialized);
            return;
        }
        if (writer != null) writer.println(serialized);
    }

    public synchronized void disconnect() {
        if (!running) return;
        running = false;
        server.removeClient(this);
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) { }
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.close();
        }
    }

    public GameRoom getGameRoom() { return gameRoom; }
    public void setGameRoom(GameRoom gameRoom) { this.gameRoom = gameRoom; }
    public boolean isWhite() { return white; }
    public void setWhite(boolean white) { this.white = white; }
    public Socket getSocket() { return socket; }
    public String getConnectionId() { return connectionId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) {
        if (playerName != null && !playerName.isBlank()) this.playerName = playerName.trim();
    }
}

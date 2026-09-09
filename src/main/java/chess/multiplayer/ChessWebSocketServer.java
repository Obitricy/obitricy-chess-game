package chess.multiplayer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Internet-facing WebSocket transport. The chess rules and lobby remain in
 * ChessServer so local TCP development and hosted WebSocket play share the
 * same authoritative game logic.
 */
public class ChessWebSocketServer extends WebSocketServer {

    private final ChessServer gameServer = new ChessServer();

    public ChessWebSocketServer(int port) {
        super(new InetSocketAddress("0.0.0.0", port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        ClientConnection client = ClientConnection.forWebSocket(conn, gameServer);
        conn.setAttachment(client);
        gameServer.addClient(client);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        ClientConnection client = conn.getAttachment();
        if (client != null) client.receive(message);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        ClientConnection client = conn.getAttachment();
        if (client != null) client.disconnect();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
        if (conn != null) {
            ClientConnection client = conn.getAttachment();
            if (client != null) client.disconnect();
        }
    }

    @Override
    public void onStart() {
        System.out.println("================================");
        System.out.println("OBITRICY CHESS WEBSOCKET SERVER");
        System.out.println("Server started on port " + getPort());
        System.out.println("Waiting for Internet players...");
        System.out.println("================================");
    }

    public static void main(String[] args) {
        int port = readPort(args);
        ChessWebSocketServer server = new ChessWebSocketServer(port);
        server.start();
    }

    private static int readPort(String[] args) {
        String value = System.getenv("PORT");
        if (value == null || value.isBlank()) value = System.getProperty("server.port");
        if ((value == null || value.isBlank()) && args.length > 0) value = args[0];
        if (value == null || value.isBlank()) value = "5000";
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            System.out.println("Invalid port. Using 5000.");
            return 5000;
        }
    }
}

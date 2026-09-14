package chess.multiplayer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.Consumer;

/**
 * Internet multiplayer client using WebSockets.
 *
 * Supports:
 *
 * Internet:
 *     wss://obitricy-chess-game.onrender.com/
 *
 * Local development:
 *     ws://localhost:5000/
 *
 * The existing chess message protocol remains unchanged.
 */
public class MultiplayerClient {

    private WebSocketClient socket;
    private volatile boolean connected;
    private volatile String playerName = "Player";
    private Consumer<NetworkMessage> messageListener;

    /**
     * Connect to the configured WebSocket server.
     *
     * If host already contains ws:// or wss://, that complete URL
     * is used without adding another port.
     *
     * This is important for Render because the public server URL is:
     *
     *     wss://obitricy-chess-game.onrender.com/
     *
     * Render handles the public HTTPS/WSS port for us.
     */
    public void connect(
            String host,
            int port,
            Consumer<NetworkMessage> listener
    ) throws Exception {

        this.messageListener = listener;

        String raw = host == null ? "" : host.trim();

        if (raw.isBlank()) {
            throw new IllegalArgumentException(
                    "Server host is required."
            );
        }

        URI uri;

        /*
         * Full WebSocket URL supplied.
         *
         * Example:
         *
         * wss://obitricy-chess-game.onrender.com
         *
         * Do NOT append :443 here.
         */
        if (raw.startsWith("ws://") || raw.startsWith("wss://")) {

            if (raw.endsWith("/")) {
                uri = new URI(raw);
            } else {
                uri = new URI(raw + "/");
            }

        } else {

            /*
             * Local/development host.
             *
             * Example:
             *
             * localhost + 5000
             *
             * becomes:
             *
             * ws://localhost:5000/
             */
            String scheme = OnlineServerConfig.secure()
                    ? "wss"
                    : "ws";

            /*
             * Remove accidental HTTP/HTTPS prefixes.
             */
            raw = raw.replaceFirst(
                    "^https?://",
                    ""
            );

            uri = new URI(
                    scheme
                            + "://"
                            + raw
                            + ":"
                            + port
                            + "/"
            );
        }

        System.out.println(
                "Connecting to WebSocket server: " + uri
        );

        socket = new WebSocketClient(uri) {

            @Override
            public void onOpen(ServerHandshake handshake) {

                connected = true;

                System.out.println(
                        "ONLINE MULTIPLAYER CONNECTED: "
                                + uri
                );

                /*
                 * Register the player's name immediately after
                 * the WebSocket connection is established.
                 */
                MultiplayerClient.this.send(
                        NetworkMessage.Type.REGISTER_NAME,
                        playerName
                );
            }

            @Override
            public void onMessage(String message) {

                NetworkMessage parsed =
                        NetworkMessage.deserialize(message);

                if (parsed != null
                        && messageListener != null) {

                    messageListener.accept(parsed);
                }
            }

            @Override
            public void onClose(
                    int code,
                    String reason,
                    boolean remote
            ) {

                connected = false;

                System.out.println(
                        "ONLINE MULTIPLAYER DISCONNECTED: "
                                + code
                                + " "
                                + reason
                );
            }

            @Override
            public void onError(Exception ex) {

                connected = false;

                System.err.println(
                        "ONLINE MULTIPLAYER ERROR: "
                                + ex.getMessage()
                );

                notifyError(
                        "Connection error: "
                                + ex.getMessage()
                );
            }
        };

        /*
         * Wait until the connection is established.
         */
        socket.connectBlocking();

        if (!socket.isOpen()) {

            throw new IllegalStateException(
                    "Unable to establish WebSocket connection."
            );
        }
    }

    public synchronized void send(
            NetworkMessage.Type type,
            String data
    ) {

        if (!connected
                || socket == null
                || !socket.isOpen()) {

            return;
        }

        socket.send(
                new NetworkMessage(
                        type,
                        data
                ).serialize()
        );
    }

    private void notifyError(String message) {

        if (messageListener != null) {

            messageListener.accept(
                    new NetworkMessage(
                            NetworkMessage.Type.ERROR,
                            message
                    )
            );
        }
    }

    public void createGame() {

        send(
                NetworkMessage.Type.CREATE_GAME,
                ""
        );
    }

    public void joinGame(String gameId) {

        send(
                NetworkMessage.Type.JOIN_GAME,
                gameId
        );
    }

    public void sendMove(String moveData) {

        send(
                NetworkMessage.Type.MOVE,
                moveData
        );
    }

    public void sendMove(
            int fromRow,
            int fromCol,
            int toRow,
            int toCol
    ) {

        sendMove(
                fromRow,
                fromCol,
                toRow,
                toCol,
                ""
        );
    }

    public void sendMove(
            int fromRow,
            int fromCol,
            int toRow,
            int toCol,
            String promotion
    ) {

        String safePromotion =
                promotion == null
                        ? ""
                        : promotion.trim();

        send(
                NetworkMessage.Type.MOVE,
                fromRow
                        + ","
                        + fromCol
                        + ","
                        + toRow
                        + ","
                        + toCol
                        + ","
                        + safePromotion
        );
    }

    public void sendUndo() {

        send(
                NetworkMessage.Type.UNDO,
                ""
        );
    }

    public void sendRedo() {

        send(
                NetworkMessage.Type.REDO,
                ""
        );
    }

    public void sendResign() {

        send(
                NetworkMessage.Type.RESIGN,
                ""
        );
    }

    public void sendRematch() {

        send(
                NetworkMessage.Type.REMATCH,
                ""
        );
    }

    public void resign() {

        sendResign();
    }

    public void rematch() {

        sendRematch();
    }

    public synchronized void setPlayerName(
            String playerName
    ) {

        if (playerName == null
                || playerName.isBlank()) {

            this.playerName = "Player";

        } else {

            String clean =
                    playerName.trim();

            this.playerName =
                    clean.substring(
                            0,
                            Math.min(
                                    24,
                                    clean.length()
                            )
                    );
        }

        /*
         * If already connected, immediately update the
         * name registered on the server.
         */
        if (connected) {

            send(
                    NetworkMessage.Type.REGISTER_NAME,
                    this.playerName
            );
        }
    }

    public String getPlayerName() {

        return playerName;
    }

    public void requestPlay(
            String playerName
    ) {

        send(
                NetworkMessage.Type.PLAY_REQUEST,
                playerName
        );
    }

    public void acceptPlayRequest(
            String playerName
    ) {

        send(
                NetworkMessage.Type.PLAY_REQUEST_ACCEPT,
                playerName
        );
    }

    public void declinePlayRequest(
            String playerName
    ) {

        send(
                NetworkMessage.Type.PLAY_REQUEST_DECLINE,
                playerName
        );
    }

    public boolean isConnected() {

        return connected;
    }

    public void disconnect() {

        connected = false;

        if (socket != null) {

            try {

                socket.close();

            } catch (Exception ignored) {
            }

            socket = null;
        }
    }

    public void setMessageListener(
            Consumer<NetworkMessage> listener
    ) {

        this.messageListener = listener;
    }
}
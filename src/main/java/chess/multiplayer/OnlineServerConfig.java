package chess.multiplayer;

/**
 * Central configuration for the Internet multiplayer endpoint.
 *
 * The default configuration connects the installed chess game
 * directly to the public Obitricy Chess WebSocket server on Render.
 *
 * Local development can still override these values with JVM
 * system properties when needed.
 */
public final class OnlineServerConfig {

    private OnlineServerConfig() { }

    /**
     * Public Internet WebSocket server.
     *
     * Render provides HTTPS for the service, therefore the WebSocket
     * connection uses the secure WSS protocol.
     */
    public static final String HOST = System.getProperty(
            "obitricy.server.host",
            "wss://obitricy-chess-game.onrender.com"
    );

    /**
     * The port is only used when a plain host such as localhost
     * is supplied. The public Render URL does not need :443 appended.
     */
    public static final int PORT = Integer.getInteger(
            "obitricy.server.port",
            443
    );

    /**
     * Secure WebSocket connection for Internet play.
     */
    public static final boolean SECURE = Boolean.parseBoolean(
            System.getProperty(
                    "obitricy.websocket.secure",
                    "true"
            )
    );

    public static String host() {
        return HOST;
    }

    public static int port() {
        return PORT;
    }

    public static boolean secure() {
        return SECURE;
    }
}
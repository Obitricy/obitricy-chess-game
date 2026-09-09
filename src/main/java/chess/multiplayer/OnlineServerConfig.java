package chess.multiplayer;

/** Central configuration for the Internet multiplayer endpoint. */
public final class OnlineServerConfig {
    private OnlineServerConfig() { }

    // Development default. Replace these values when the Render service exists.
    public static final String HOST = System.getProperty("obitricy.server.host", "localhost");
    public static final int PORT = Integer.getInteger("obitricy.server.port", 5000);
    public static final boolean SECURE = Boolean.parseBoolean(
            System.getProperty("obitricy.websocket.secure", "false"));

    public static String host() { return HOST; }
    public static int port() { return PORT; }
    public static boolean secure() { return SECURE; }
}

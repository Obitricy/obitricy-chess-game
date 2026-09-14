package chess.multiplayer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central socket server for Obitricy Chess.
 *
 * The server owns the authoritative chess position for every GameRoom and
 * also maintains the online-player lobby.  Clients never decide whether a
 * network move is valid and clients do not need to know one another's IP
 * addresses.
 */
public class ChessServer {

    public static final int DEFAULT_PORT = 5000;

    private final int port;
    private ServerSocket serverSocket;

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();
    private final Map<ClientConnection, Boolean> clients = new ConcurrentHashMap<>();

    public ChessServer() {
        this(DEFAULT_PORT);
    }

    public ChessServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("================================");
            System.out.println("OBITRICY CHESS SERVER");
            System.out.println("Server started on port " + port);
            System.out.println("Waiting for players...");
            System.out.println("================================");

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);

                System.out.println("New client connected: " + socket.getInetAddress());

                ClientConnection client = new ClientConnection(socket, this);
                addClient(client);

                Thread thread = new Thread(client, "ChessClient-" + socket.getPort());
                thread.start();
            }
        } catch (IOException ex) {
            if (serverSocket == null || !serverSocket.isClosed()) {
                System.err.println("Chess server error: " + ex.getMessage());
            }
        }
    }

    public void addClient(ClientConnection client) {
        if (client == null) return;
        clients.put(client, Boolean.TRUE);
        client.send(new NetworkMessage(NetworkMessage.Type.WAITING, "Connected to chess server."));
        System.out.println("New client connected: " + client.getConnectionId());
    }

    public void handleMessage(ClientConnection client, NetworkMessage message) {
        if (client == null || message == null) return;

        switch (message.getType()) {
            case REGISTER_NAME -> registerName(client, message.getData());
            case PLAY_REQUEST -> handlePlayRequest(client, message.getData());
            case PLAY_REQUEST_ACCEPT -> handlePlayRequestAccept(client, message.getData());
            case PLAY_REQUEST_DECLINE -> handlePlayRequestDecline(client, message.getData());
            case CREATE_GAME -> createGame(client);
            case JOIN_GAME -> joinGame(client, message.getData());
            case MOVE -> handleMove(client, message.getData());
            case UNDO -> handleUndo(client);
            case REDO -> handleRedo(client);
            case RESIGN -> handleResign(client);
            case REMATCH -> handleRematch(client);
            default -> sendError(client, "Unsupported message.");
        }
    }

    private void registerName(ClientConnection client, String requestedName) {
        if (client.getGameRoom() != null) {
            sendError(client, "You cannot change your name during a game.");
            return;
        }

        String name = sanitizeName(requestedName);
        System.out.println("REGISTER_NAME received: [" + requestedName + "] from " + client.getConnectionId());

        if (name.isBlank()) {
            sendError(client, "A valid player name is required. Please log in again.");
            return;
        }

        if (!isNameAvailable(name, client)) {
            sendError(client, "The player name '" + name + "' is already online.");
            return;
        }

        client.setPlayerName(name);
        client.send(new NetworkMessage(NetworkMessage.Type.WAITING, "Welcome, " + name + "."));
        broadcastOnlinePlayers();

        System.out.println("Player registered: " + name);
    }

    private String sanitizeName(String value) {
        if (value == null) return "";
        String name = value.trim().replace('|', ' ').replace(',', ' ');
        if (name.length() > 24) name = name.substring(0, 24).trim();
        return name;
    }

    private boolean isNameAvailable(String name, ClientConnection except) {
        for (ClientConnection client : clients.keySet()) {
            if (client == except) continue;
            if (name.equalsIgnoreCase(client.getPlayerName())) return false;
        }
        return true;
    }

    private boolean isLobbyAvailable(ClientConnection client) {
        return client != null
                && client.getGameRoom() == null
                && clients.containsKey(client);
    }

    private void handlePlayRequest(ClientConnection requester, String targetName) {
        if (!isLobbyAvailable(requester)) {
            sendError(requester, "You are already in a game.");
            return;
        }

        String target = sanitizeName(targetName);
        ClientConnection targetClient = findClientByName(target);

        if (targetClient == null || !isLobbyAvailable(targetClient)) {
            sendError(requester, "That player is no longer available.");
            broadcastOnlinePlayers();
            return;
        }

        if (targetClient == requester) {
            sendError(requester, "You cannot challenge yourself.");
            return;
        }

        targetClient.send(new NetworkMessage(
                NetworkMessage.Type.PLAY_REQUEST_RECEIVED,
                requester.getPlayerName()
        ));

        requester.send(new NetworkMessage(
                NetworkMessage.Type.PLAY_REQUEST_SENT,
                targetClient.getPlayerName()
        ));

        System.out.println("Play request: " + requester.getPlayerName()
                + " -> " + targetClient.getPlayerName());
    }

    private void handlePlayRequestAccept(ClientConnection accepter, String requesterName) {
        if (!isLobbyAvailable(accepter)) {
            sendError(accepter, "You are not available for a new game.");
            return;
        }

        ClientConnection requester = findClientByName(sanitizeName(requesterName));
        if (requester == null || !isLobbyAvailable(requester)) {
            sendError(accepter, "That play request has expired.");
            broadcastOnlinePlayers();
            return;
        }

        // The player who originally sent the request becomes White.
        GameRoom room = new GameRoom();
        if (!room.addPlayer(requester) || !room.addPlayer(accepter)) {
            sendError(accepter, "Unable to create the online game.");
            return;
        }

        requester.setGameRoom(room);
        requester.setWhite(true);
        accepter.setGameRoom(room);
        accepter.setWhite(false);
        rooms.put(room.getGameId(), room);

        String gameDataWhite = room.getGameId() + "|WHITE|" + accepter.getPlayerName();
        String gameDataBlack = room.getGameId() + "|BLACK|" + requester.getPlayerName();

        requester.send(new NetworkMessage(NetworkMessage.Type.START_GAME, gameDataWhite));
        accepter.send(new NetworkMessage(NetworkMessage.Type.START_GAME, gameDataBlack));

        broadcastOnlinePlayers();

        System.out.println("Game started from request: " + room.getGameId()
                + " (" + requester.getPlayerName() + " vs " + accepter.getPlayerName() + ")");
    }

    private void handlePlayRequestDecline(ClientConnection decliner, String requesterName) {
        ClientConnection requester = findClientByName(sanitizeName(requesterName));
        if (requester != null) {
            requester.send(new NetworkMessage(
                    NetworkMessage.Type.PLAY_REQUEST_DECLINE,
                    decliner.getPlayerName()
            ));
        }
    }

    private ClientConnection findClientByName(String name) {
        if (name == null || name.isBlank()) return null;
        for (ClientConnection client : clients.keySet()) {
            if (name.equalsIgnoreCase(client.getPlayerName())) return client;
        }
        return null;
    }

    private void broadcastOnlinePlayers() {
        List<String> names = new ArrayList<>();
        for (ClientConnection client : clients.keySet()) {
            if (isLobbyAvailable(client)) {
                names.add(client.getPlayerName());
            }
        }

        names.sort(String.CASE_INSENSITIVE_ORDER);
        String data = String.join(",", names);
        NetworkMessage message = new NetworkMessage(NetworkMessage.Type.ONLINE_PLAYERS, data);

        for (ClientConnection client : clients.keySet()) {
            if (client.getGameRoom() == null) {
                client.send(message);
            }
        }
    }

    private void createGame(ClientConnection client) {
        if (client.getGameRoom() != null) {
            sendError(client, "You are already in a game.");
            return;
        }

        GameRoom room = new GameRoom();
        if (!room.addPlayer(client)) {
            sendError(client, "Unable to create game.");
            return;
        }

        client.setGameRoom(room);
        client.setWhite(true);
        rooms.put(room.getGameId(), room);

        client.send(new NetworkMessage(
                NetworkMessage.Type.GAME_CREATED,
                room.getGameId() + "|WHITE"
        ));
        client.send(new NetworkMessage(
                NetworkMessage.Type.WAITING,
                "Game created. Waiting for opponent..."
        ));
        broadcastOnlinePlayers();

        System.out.println("Game created: " + room.getGameId());
    }

    private void joinGame(ClientConnection client, String gameId) {
        if (client.getGameRoom() != null) {
            sendError(client, "You are already in a game.");
            return;
        }
        if (gameId == null || gameId.isBlank()) {
            sendError(client, "Game ID is required.");
            return;
        }

        GameRoom room = rooms.get(gameId.trim().toUpperCase());
        if (room == null) {
            sendError(client, "Game not found.");
            return;
        }

        synchronized (room) {
            if (room.isFull() || !room.addPlayer(client)) {
                sendError(client, room.isFull() ? "Game is already full." : "Unable to join game.");
                return;
            }

            client.setGameRoom(room);
            client.setWhite(false);
            client.send(new NetworkMessage(NetworkMessage.Type.GAME_JOINED,
                    room.getGameId() + "|BLACK"));

            ClientConnection white = room.getWhitePlayer();
            if (white != null) {
                white.send(new NetworkMessage(NetworkMessage.Type.START_GAME,
                        room.getGameId() + "|WHITE"));
            }
            client.send(new NetworkMessage(NetworkMessage.Type.START_GAME,
                    room.getGameId() + "|BLACK"));
        }
        broadcastOnlinePlayers();
        System.out.println("Game started: " + room.getGameId());
    }

    private void handleMove(ClientConnection client, String moveData) {
        GameRoom room = client.getGameRoom();
        if (room == null) {
            sendMoveRejected(client, "You are not in a game.");
            return;
        }

        GameRoom.MoveResult result = room.applyMove(client, moveData);
        if (result != GameRoom.MoveResult.ACCEPTED) {
            sendMoveRejected(client, getMoveError(result));
            System.out.println("Rejected move from " + (client.isWhite() ? "WHITE" : "BLACK")
                    + " in game " + room.getGameId() + ": " + result);
            return;
        }

        ClientConnection opponent = room.getOpponent(client);
        if (opponent == null) {
            sendMoveRejected(client, "Opponent is not connected.");
            return;
        }

        String canonicalMove = room.normalizeMoveData(moveData);
        opponent.send(new NetworkMessage(NetworkMessage.Type.MOVE,
                canonicalMove + "|" + (client.isWhite() ? "WHITE" : "BLACK")));
    }

    private void handleUndo(ClientConnection client) {

        GameRoom room =
                client.getGameRoom();

        if (room == null) {

            sendError(
                    client,
                    "You are not in a game."
            );

            return;
        }

        if (!room.undoMove(client)) {

            if (room.isGameOver()) {

                sendError(
                        client,
                        "Undo is not available after the game has ended."
                );

            } else {

                sendError(
                        client,
                        "Nothing to undo."
                );
            }

            return;
        }

        /*
         * The server has successfully changed the
         * authoritative position.
         *
         * Both clients must now perform exactly one
         * matching undo operation locally.
         */
        broadcastToRoom(
                room,
                new NetworkMessage(
                        NetworkMessage.Type.UNDO,
                        ""
                )
        );
    }

    private void handleRedo(ClientConnection client) {

        GameRoom room =
                client.getGameRoom();

        if (room == null) {

            sendError(
                    client,
                    "You are not in a game."
            );

            return;
        }

        if (!room.redoMove(client)) {

            if (room.isGameOver()) {

                sendError(
                        client,
                        "Redo is not available after the game has ended."
                );

            } else {

                sendError(
                        client,
                        "Nothing to redo."
                );
            }

            return;
        }

        /*
         * The server has successfully changed the
         * authoritative position.
         *
         * Both clients must now perform exactly one
         * matching redo operation locally.
         */
        broadcastToRoom(
                room,
                new NetworkMessage(
                        NetworkMessage.Type.REDO,
                        ""
                )
        );
    }

    private String getMoveError(GameRoom.MoveResult result) {
        return switch (result) {
            case NOT_IN_ROOM -> "You are not in a game.";
            case NOT_YOUR_TURN -> "It is not your turn.";
            case NO_PIECE -> "There is no piece on the selected square.";
            case WRONG_COLOUR -> "You cannot move the opponent's piece.";
            case ILLEGAL_MOVE -> "Illegal chess move.";
            case INVALID_PROMOTION -> "Invalid pawn promotion.";
            case GAME_OVER -> "This game has already ended.";
            case INVALID_DATA -> "Invalid move data.";
            case ACCEPTED -> "Move accepted.";
        };
    }

    private void sendMoveRejected(ClientConnection client, String reason) {
        client.send(new NetworkMessage(NetworkMessage.Type.MOVE_REJECTED, reason));
    }

    private void sendError(ClientConnection client, String message) {
        client.send(new NetworkMessage(NetworkMessage.Type.ERROR, message));
    }

    private void broadcastToRoom(GameRoom room, NetworkMessage message) {
        if (room == null || message == null) return;
        ClientConnection white = room.getWhitePlayer();
        ClientConnection black = room.getBlackPlayer();
        if (white != null) white.send(message);
        if (black != null) black.send(message);
    }

    private void handleResign(ClientConnection client) {
        GameRoom room = client.getGameRoom();
        if (room == null) { sendError(client, "You are not in a game."); return; }
        if (!room.resignGame(client)) { sendError(client, "Unable to resign this game."); return; }

        broadcastToRoom(room, new NetworkMessage(NetworkMessage.Type.RESIGN,
                client.isWhite() ? "WHITE" : "BLACK"));
    }

    private void handleRematch(ClientConnection client) {
        GameRoom room = client.getGameRoom();
        if (room == null) { sendError(client, "You are not in a game."); return; }
        if (!room.requestRematch(client)) { sendError(client, "Rematch is not available."); return; }

        String colour = client.isWhite() ? "WHITE" : "BLACK";
        if (!room.isRematchReady()) {
            ClientConnection opponent = room.getOpponent(client);
            if (opponent != null) {
                opponent.send(new NetworkMessage(NetworkMessage.Type.REMATCH, colour));
            }
            return;
        }

        if (!room.startRematch()) {
            sendError(client, "Unable to start the rematch.");
            return;
        }
        broadcastToRoom(room, new NetworkMessage(NetworkMessage.Type.REMATCH, "START"));
    }

    public void removeClient(ClientConnection client) {
        if (client == null) return;
        clients.remove(client);

        GameRoom room = client.getGameRoom();
        if (room != null) {
            ClientConnection opponent = room.getOpponent(client);
            if (opponent != null) {
                opponent.send(new NetworkMessage(NetworkMessage.Type.DISCONNECT,
                        "Opponent disconnected."));
                opponent.setGameRoom(null);
            }
            room.removePlayer(client);
            client.setGameRoom(null);
            rooms.remove(room.getGameId(), room);
        }

        broadcastOnlinePlayers();
        System.out.println("Client removed: " + client.getPlayerName());
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid port. Using " + DEFAULT_PORT);
            }
        }

        ChessServer server = new ChessServer(port);
        server.start();
    }
}

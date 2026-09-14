package chess.multiplayer;

import chess.board.GameState;
import chess.game.GameMode;
import chess.game.Move;
import chess.pieces.Bishop;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Piece;
import chess.pieces.Queen;
import chess.pieces.Rook;
import chess.rules.MoveValidator;
import chess.rules.CheckDetector;

import java.util.UUID;

/**
 * A single online chess game.
 *
 * The server owns one authoritative GameState for the room.
 * Clients send move coordinates; the server validates and
 * applies the move before forwarding it to the opponent.
 *
 * IMPORTANT:
 * Normal chess moves are blocked after game over.
 *
 * Undo and Redo are intentionally allowed after game over
 * so players can navigate through their move history.
 */
public class GameRoom {

    public enum MoveResult {
        ACCEPTED,
        INVALID_DATA,
        NOT_IN_ROOM,
        NOT_YOUR_TURN,
        NO_PIECE,
        WRONG_COLOUR,
        ILLEGAL_MOVE,
        INVALID_PROMOTION,
        GAME_OVER
    }

    private final String gameId;

    private ClientConnection whitePlayer;
    private ClientConnection blackPlayer;

    /*
     * IMPORTANT:
     * This GameState is server-side only. It contains no Swing/UI
     * objects and therefore can safely be used as the authoritative
     * chess position for this room.
     */
    private GameState gameState;

    /*
     * True when the CURRENT authoritative position is a
     * game-over position.
     *
     * This does NOT prevent Undo/Redo.
     *
     * It only prevents making a NEW chess move.
     */
    private boolean gameOver;

    /*
     * Rematch state.
     *
     * A rematch only starts when BOTH players agree.
     */
    private boolean whiteRematchRequested;
    private boolean blackRematchRequested;


    public GameRoom() {

        gameId =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 6)
                        .toUpperCase();

        gameState =
                new GameState(
                        "White",
                        "Black",
                        GameMode.ONLINE,
                        null
                );
    }


    public String getGameId() {
        return gameId;
    }


    public synchronized boolean addPlayer(
            ClientConnection client) {

        if (client == null) {
            return false;
        }

        if (whitePlayer == null) {
            whitePlayer = client;
            return true;
        }

        if (blackPlayer == null) {
            blackPlayer = client;
            return true;
        }

        return false;
    }


    public synchronized boolean isFull() {

        return whitePlayer != null
                && blackPlayer != null;
    }


    public synchronized ClientConnection getWhitePlayer() {

        return whitePlayer;
    }


    public synchronized ClientConnection getBlackPlayer() {

        return blackPlayer;
    }


    public synchronized ClientConnection getOpponent(
            ClientConnection client) {

        if (client == whitePlayer) {
            return blackPlayer;
        }

        if (client == blackPlayer) {
            return whitePlayer;
        }

        return null;
    }


    public synchronized boolean removePlayer(
            ClientConnection client) {

        boolean removed = false;

        if (client == whitePlayer) {
            whitePlayer = null;
            removed = true;
        }

        if (client == blackPlayer) {
            blackPlayer = null;
            removed = true;
        }

        return removed;
    }


    public synchronized boolean isGameOver() {

        return gameOver;
    }


    public synchronized boolean isPlayersTurn(
            ClientConnection client) {

        if (client == null) {
            return false;
        }

        if (!isFull()) {
            return false;
        }

        boolean playerIsWhite =
                client == whitePlayer;

        return gameState.isWhiteTurn()
                == playerIsWhite;
    }


    /**
     * Validates and applies a client move to the authoritative
     * server-side chess position.
     *
     * Accepted input:
     *
     * fromRow,fromCol,toRow,toCol,promotion
     *
     * promotion may be blank, Queen, Rook, Bishop or Knight.
     */
    public synchronized MoveResult applyMove(
            ClientConnection client,
            String moveData) {

        if (client == null) {
            return MoveResult.NOT_IN_ROOM;
        }

        if (client != whitePlayer
                && client != blackPlayer) {

            return MoveResult.NOT_IN_ROOM;
        }

        if (!isFull()) {
            return MoveResult.GAME_OVER;
        }

        /*
         * IMPORTANT:
         *
         * A NEW chess move is never allowed after
         * checkmate/stalemate.
         *
         * Undo/Redo are handled separately below
         * and remain available.
         */
        if (gameOver) {
            return MoveResult.GAME_OVER;
        }

        if (!isPlayersTurn(client)) {
            return MoveResult.NOT_YOUR_TURN;
        }

        if (moveData == null) {
            return MoveResult.INVALID_DATA;
        }

        String[] parts =
                moveData.split(",", -1);

        if (parts.length < 4 || parts.length > 5) {
            return MoveResult.INVALID_DATA;
        }

        int fromRow;
        int fromCol;
        int toRow;
        int toCol;

        try {

            fromRow =
                    Integer.parseInt(parts[0].trim());

            fromCol =
                    Integer.parseInt(parts[1].trim());

            toRow =
                    Integer.parseInt(parts[2].trim());

            toCol =
                    Integer.parseInt(parts[3].trim());

        } catch (NumberFormatException ex) {

            return MoveResult.INVALID_DATA;
        }

        if (!inBounds(fromRow, fromCol)
                || !inBounds(toRow, toCol)) {

            return MoveResult.INVALID_DATA;
        }

        Piece piece =
                gameState.getPiece(
                        fromRow,
                        fromCol
                );

        if (piece == null) {
            return MoveResult.NO_PIECE;
        }

        boolean moverIsWhite =
                client == whitePlayer;

        if (piece.isWhite() != moverIsWhite) {
            return MoveResult.WRONG_COLOUR;
        }

        Piece captured =
                gameState.getPiece(
                        toRow,
                        toCol
                );

        Move move =
                new Move(
                        piece,
                        fromRow,
                        fromCol,
                        toRow,
                        toCol,
                        captured
                );

        /*
         * Handle promotion without opening the desktop
         * PromotionDialog on the server.
         */
        String promotion =
                parts.length == 5
                        ? parts[4].trim()
                        : "";

        if (isPromotionMove(piece, toRow)) {

            if (promotion.isBlank()) {
                return MoveResult.INVALID_PROMOTION;
            }

            Piece promoted =
                    createPromotionPiece(
                            promotion,
                            piece.isWhite(),
                            toRow,
                            toCol
                    );

            if (promoted == null) {
                return MoveResult.INVALID_PROMOTION;
            }

            move.setPromotion(promoted);

        } else if (!promotion.isBlank()) {

            /*
             * A promotion type is only legal when the move
             * actually reaches the promotion rank.
             */
            return MoveResult.INVALID_PROMOTION;
        }

        /*
         * GameState.makeMove() performs the same chess
         * legality checks used by the desktop game.
         */
        if (!MoveValidator.isLegalMove(
                move,
                gameState)) {

            return MoveResult.ILLEGAL_MOVE;
        }

        if (!gameState.makeMove(move)) {
            return MoveResult.ILLEGAL_MOVE;
        }

        /*
         * Check whether the resulting position has ended
         * the game.
         *
         * We do not send a special game-over packet here.
         * The clients already evaluate the resulting
         * position locally after receiving MOVE.
         */
        updateGameOverStatus();

        return MoveResult.ACCEPTED;
    }


    /**
     * Undoes the most recent move on the authoritative
     * server-side GameState.
     *
     * IMPORTANT:
     *
     * Undo is allowed even when the current position is
     * CHECKMATE, STALEMATE or another server-side game-over
     * position.
     *
     * After undoing, the game-over status is recalculated.
     */
    public synchronized boolean undoMove(
            ClientConnection client) {

        /*
         * The requester must be a player in this room.
         */
        if (client == null
                || (client != whitePlayer
                && client != blackPlayer)) {

            return false;
        }

        /*
         * Both players must be connected before
         * an online undo can be performed.
         */
        if (!isFull()) {
            return false;
        }

        /*
         * DO NOT reject undo merely because gameOver == true.
         *
         * This is the key fix.
         */
        if (gameState.getMoveHistory().size() == 0) {
            return false;
        }

        /*
         * Undo the most recent move on the
         * authoritative GameState.
         */
        gameState.undoMove();

        /*
         * Recalculate the resulting position.
         *
         * If the undo moves us away from checkmate,
         * gameOver becomes false.
         */
        updateGameOverStatus();

        return true;
    }


    /**
     * Redoes the most recently undone move on the
     * authoritative server-side GameState.
     *
     * IMPORTANT:
     *
     * Redo is also allowed when the current position is
     * game-over.
     *
     * This allows:
     *
     * CHECKMATE
     *    -> UNDO
     *    -> normal position
     *    -> REDO
     *    -> CHECKMATE
     */
    public synchronized boolean redoMove(
            ClientConnection client) {

        /*
         * The requester must be a player in this room.
         */
        if (client == null
                || (client != whitePlayer
                && client != blackPlayer)) {

            return false;
        }

        /*
         * Both players must be connected before
         * an online redo can be performed.
         */
        if (!isFull()) {
            return false;
        }

        /*
         * DO NOT reject redo merely because gameOver == true.
         *
         * This is also intentional.
         */
        int historyBefore =
                gameState.getMoveHistory().size();

        /*
         * Redo the most recently undone move.
         */
        gameState.redoMove();

        int historyAfter =
                gameState.getMoveHistory().size();

        /*
         * No change means there was nothing to redo.
         */
        if (historyAfter <= historyBefore) {
            return false;
        }

        /*
         * Recalculate game-over status after redo.
         *
         * If the redone move is the mating move,
         * gameOver becomes true again.
         */
        updateGameOverStatus();

        return true;
    }


    /**
     * Recalculates whether the CURRENT authoritative
     * position is a game-over position.
     */
    private void updateGameOverStatus() {

        gameOver =
                CheckDetector.isCheckmate(gameState)
                        || gameState.isStalemate();
    }


    /**
     * Ends the online game because a player resigned.
     *
     * @return true if the game was successfully marked as over.
     */
    public synchronized boolean resignGame(
            ClientConnection client) {

        if (client == null) {
            return false;
        }

        if (client != whitePlayer
                && client != blackPlayer) {

            return false;
        }

        if (!isFull()) {
            return false;
        }

        if (gameOver) {
            return false;
        }

        gameOver = true;

        return true;
    }


    /**
     * Records a player's request for a rematch.
     *
     * The rematch does NOT start until both players
     * have requested/accepted it.
     */
    public synchronized boolean requestRematch(
            ClientConnection client) {

        if (client == null) {
            return false;
        }

        if (client != whitePlayer
                && client != blackPlayer) {

            return false;
        }

        if (!isFull()) {
            return false;
        }

        /*
         * A rematch is only available after the game
         * has ended.
         */
        if (!gameOver) {
            return false;
        }

        if (client == whitePlayer) {

            whiteRematchRequested = true;

        } else {

            blackRematchRequested = true;
        }

        return true;
    }


    /**
     * Returns true when both players have agreed
     * to the rematch.
     */
    public synchronized boolean isRematchReady() {

        return whiteRematchRequested
                && blackRematchRequested;
    }


    /**
     * Starts a completely new online chess game
     * after both players have agreed to a rematch.
     */
    public synchronized boolean startRematch() {

        if (!isFull()) {
            return false;
        }

        if (!isRematchReady()) {
            return false;
        }

        /*
         * Create a completely fresh authoritative
         * chess position.
         */
        gameState =
                new GameState(
                        "White",
                        "Black",
                        GameMode.ONLINE,
                        null
                );

        /*
         * New game starts with White.
         */
        gameOver = false;

        /*
         * Clear the rematch agreement so another
         * rematch can be requested after this game.
         */
        whiteRematchRequested = false;
        blackRematchRequested = false;

        return true;
    }


    /**
     * Returns whether this player has already
     * requested the rematch.
     */
    public synchronized boolean hasRequestedRematch(
            ClientConnection client) {

        if (client == whitePlayer) {
            return whiteRematchRequested;
        }

        if (client == blackPlayer) {
            return blackRematchRequested;
        }

        return false;
    }


    /**
     * Returns the canonical move string after
     * successful validation.
     */
    public String normalizeMoveData(
            String moveData) {

        String[] parts =
                moveData.split(",", -1);

        String promotion =
                parts.length >= 5
                        ? parts[4].trim()
                        : "";

        return parts[0].trim()
                + ","
                + parts[1].trim()
                + ","
                + parts[2].trim()
                + ","
                + parts[3].trim()
                + ","
                + promotion;
    }


    private boolean isPromotionMove(
            Piece piece,
            int toRow) {

        return piece instanceof Pawn
                && ((piece.isWhite() && toRow == 0)
                || (!piece.isWhite() && toRow == 7));
    }


    private Piece createPromotionPiece(
            String type,
            boolean white,
            int row,
            int col) {

        return switch (type.toLowerCase()) {

            case "queen" ->
                    new Queen(white, row, col);

            case "rook" ->
                    new Rook(white, row, col);

            case "bishop" ->
                    new Bishop(white, row, col);

            case "knight" ->
                    new Knight(white, row, col);

            default ->
                    null;
        };
    }


    private boolean inBounds(
            int row,
            int col) {

        return row >= 0
                && row < 8
                && col >= 0
                && col < 8;
    }
}
package chess.game;

import chess.board.GameState;
import chess.rules.Detector;

/**
 * Manages the current status of a chess game.
 *
 * This class determines whether the game is still being played,
 * whether a player is in check, or whether the game has ended.
 */
public class GameStatusManager {

    public enum Status {

        PLAYING,

        CHECK,

        CHECKMATE,

        STALEMATE,

        DRAW_INSUFFICIENT_MATERIAL
    }

    private final GameState gameState;

    private Status status = Status.PLAYING;

    private boolean gameOver;

    private String winner;

    public GameStatusManager(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Evaluates the current position and updates the game status.
     */
    public void update() {

        boolean whiteTurn = gameState.isWhiteTurn();

        winner = null;
        gameOver = false;

        // ================= CHECKMATE =================

        if (Detector.isCheckmate(whiteTurn, gameState)) {

            status = Status.CHECKMATE;

            winner = whiteTurn
                    ? "Black"
                    : "White";

            gameOver = true;

            return;
        }

        // ================= INSUFFICIENT MATERIAL =================

        if (Detector.isInsufficientMaterial(gameState)) {

            status = Status.DRAW_INSUFFICIENT_MATERIAL;

            gameOver = true;

            return;
        }

        // ================= STALEMATE =================

        if (Detector.isStalemate(gameState)) {

            status = Status.STALEMATE;

            gameOver = true;

            return;
        }

        // ================= CHECK =================

        if (Detector.isCheck(whiteTurn, gameState)) {

            status = Status.CHECK;

            return;
        }

        // ================= PLAYING =================

        status = Status.PLAYING;
    }

    /**
     * Returns the current game status.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns true if the game has ended.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Returns the winner.
     *
     * Returns null when the game is a draw or
     * the game is still being played.
     */
    public String getWinner() {
        return winner;
    }

    /**
     * Returns true if the current position is a draw.
     */
    public boolean isDraw() {

        return status == Status.STALEMATE
                || status == Status.DRAW_INSUFFICIENT_MATERIAL;
    }

    /**
     * Returns true if the current player is in check.
     */
    public boolean isCheck() {
        return status == Status.CHECK;
    }

    /**
     * Returns a human-readable title for the current status.
     */
    public String getTitle() {

        return switch (status) {

            case PLAYING ->
                    "GAME IN PROGRESS";

            case CHECK ->
                    "CHECK!";

            case CHECKMATE ->
                    "CHECKMATE!";

            case STALEMATE ->
                    "STALEMATE!";

            case DRAW_INSUFFICIENT_MATERIAL ->
                    "DRAW!";
        };
    }

    /**
     * Returns a human-readable message for the current status.
     */
    public String getMessage() {

        return switch (status) {

            case PLAYING ->
                    "The game is still in progress.";

            case CHECK -> {

                String player =
                        gameState.isWhiteTurn()
                                ? "White"
                                : "Black";

                yield player + " is in check.";
            }

            case CHECKMATE -> {

                String loser =
                        gameState.isWhiteTurn()
                                ? "White"
                                : "Black";

                yield winner + " Wins!\n"
                        + loser + " has been checkmated.";
            }

            case STALEMATE ->
                    "The game ends in a draw.\n"
                            + "No legal moves remain.";

            case DRAW_INSUFFICIENT_MATERIAL ->
                    "The game ends in a draw.\n"
                            + "There is insufficient material "
                            + "to checkmate.";
        };
    }
}
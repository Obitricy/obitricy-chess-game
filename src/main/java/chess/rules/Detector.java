package chess.rules;

import chess.board.GameState;

/**
 * Central chess position detector.
 *
 * This class provides a single entry point for detecting
 * important game conditions.
 */
public final class Detector {

    private Detector() {
        // Utility class - do not instantiate.
    }

    /**
     * Returns true if the specified side's king is in check.
     */
    public static boolean isCheck(
            boolean white,
            GameState gameState) {

        return CheckDetector.isKingInCheck(
                white,
                gameState
        );
    }

    /**
     * Returns true if the current player is in check.
     */
    public static boolean isCurrentPlayerInCheck(
            GameState gameState) {

        return CheckDetector.isCurrentPlayerInCheck(
                gameState
        );
    }

    /**
     * Returns true if the specified side is checkmated.
     */
    public static boolean isCheckmate(
            boolean white,
            GameState gameState) {

        return CheckMateDetector.isCheckMate(
                white,
                gameState
        );
    }

    /**
     * Returns true if the current player is checkmated.
     */
    public static boolean isCurrentPlayerCheckmate(
            GameState gameState) {

        return CheckMateDetector.isCurrentPlayerCheckMated(
                gameState
        );
    }

    /**
     * Returns true if the current position is a stalemate.
     */
    public static boolean isStalemate(
            GameState gameState) {

        return gameState.isStalemate();
    }

    /**
     * Returns true if the current position has insufficient
     * material to checkmate.
     */
    public static boolean isInsufficientMaterial(
            GameState gameState) {

        return gameState.isInsufficientMaterial();
    }

    /**
     * Returns true if the current position is a draw
     * because of insufficient material or stalemate.
     */
    public static boolean isDraw(
            GameState gameState) {

        return isInsufficientMaterial(gameState)
                || isStalemate(gameState);
    }

    /**
     * Returns true if the game has ended.
     */
    public static boolean isGameOver(
            GameState gameState) {

        boolean whiteTurn = gameState.isWhiteTurn();

        return isCheckmate(whiteTurn, gameState)
                || isStalemate(gameState)
                || isInsufficientMaterial(gameState);
    }
}
package chess.rules;

import chess.board.GameState;
import chess.game.Move;
import chess.pieces.Piece;

import java.util.List;

public class CheckMateDetector {

    /**
     * Returns true if the specified side is checkmated.
     *
     * @param white true = White, false = Black
     */
    public static boolean isCheckMate(boolean white, GameState gameState) {

        // Must already be in check
        if (!CheckDetector.isKingInCheck(white, gameState)) {
            return false;
        }

        // Search all pieces belonging to that side
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Piece piece = gameState.getPiece(row, col);

                if (piece == null) {
                    continue;
                }

                if (piece.isWhite() != white) {
                    continue;
                }

                List<Move> moves =
                        MoveValidator.getValidMoves(piece, row, col, gameState);

                // If any legal move exists, it's not checkmate
                if (!moves.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns true if the current player is checkmated.
     */
    public static boolean isCurrentPlayerCheckMated(GameState gameState) {
        return isCheckMate(gameState.isWhiteTurn(), gameState);
    }
}
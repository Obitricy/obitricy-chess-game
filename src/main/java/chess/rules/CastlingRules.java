package chess.rules;

import chess.board.GameState;
import chess.game.Move;
import chess.pieces.King;
import chess.pieces.Piece;
import chess.pieces.Rook;

public class CastlingRules {

    // =====================================================
    // PUBLIC ENTRY POINT
    // =====================================================
    public static boolean isLegalCastle(
            Move move,
            GameState gameState) {

        Piece piece = move.getPiece();

        if (!(piece instanceof King king)) {
            return false;
        }

        int row = move.getFromRow();
        int col = move.getFromCol();

        // King must remain on the same rank
        if (move.getToRow() != row) {
            return false;
        }

        int dc = move.getToCol() - col;

        if (dc == 2) {
            return canCastleKingSide(
                    king,
                    row,
                    col,
                    gameState
            );
        }

        if (dc == -2) {
            return canCastleQueenSide(
                    king,
                    row,
                    col,
                    gameState
            );
        }

        return false;
    }

    // =====================================================
    // PRIVATE HELPERS
    // =====================================================
    private static boolean canCastleKingSide(
            King king,
            int row,
            int col,
            GameState gameState) {

        if (king.hasMoved()) {
            return false;
        }

        if (CheckDetector.isKingInCheck(
                king.isWhite(),
                gameState)) {
            return false;
        }

        Piece rookPiece = gameState.getPiece(row, 7);

        if (!(rookPiece instanceof Rook rook)) {
            return false;
        }

        if (rook.hasMoved()) {
            return false;
        }

        // Squares between king and rook must be empty
        if (gameState.getPiece(row, 5) != null ||
                gameState.getPiece(row, 6) != null) {
            return false;
        }

        // King cannot pass through check
        Move step1 = new Move(
                king,
                row,
                col,
                row,
                5,
                null
        );

        if (!CheckDetector.wouldLeaveKingInCheck(step1, gameState)) {

            Move step2 = new Move(
                    king,
                    row,
                    col,
                    row,
                    6,
                    null
            );

            return !CheckDetector.wouldLeaveKingInCheck(
                    step2,
                    gameState
            );
        }

        return false;
    }

    private static boolean canCastleQueenSide(
            King king,
            int row,
            int col,
            GameState gameState) {

        if (king.hasMoved()) {
            return false;
        }

        if (CheckDetector.isKingInCheck(
                king.isWhite(),
                gameState)) {
            return false;
        }

        Piece rookPiece = gameState.getPiece(row, 0);

        if (!(rookPiece instanceof Rook rook)) {
            return false;
        }

        if (rook.hasMoved()) {
            return false;
        }

        // Squares between king and rook must be empty
        if (gameState.getPiece(row, 1) != null ||
                gameState.getPiece(row, 2) != null ||
                gameState.getPiece(row, 3) != null) {
            return false;
        }

        Move step1 = new Move(
                king,
                row,
                col,
                row,
                3,
                null
        );

        if (!CheckDetector.wouldLeaveKingInCheck(step1, gameState)) {

            Move step2 = new Move(
                    king,
                    row,
                    col,
                    row,
                    2,
                    null
            );

            return !CheckDetector.wouldLeaveKingInCheck(
                    step2,
                    gameState
            );
        }

        return false;
    }
}
package chess.rules;

import chess.board.GameState;
import chess.game.Move;
import chess.rules.MoveValidator;
import chess.pieces.King;
import chess.pieces.Piece;
import chess.pieces.Rook;

public class CheckDetector {


    /**
     * Returns true if the specified side's king is in check.
     *
     * @param white true to check the White king, false for the Black king.
     */
    public static boolean isKingInCheck(boolean white, GameState gameState) {

        Piece king = null;
        int kingRow = -1;
        int kingCol = -1;

        // Find the king
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Piece piece = gameState.getPiece(row, col);

                if (piece instanceof King && piece.isWhite() == white) {
                    king = piece;
                    kingRow = row;
                    kingCol = col;
                    break;

                }
            }

            if (king != null)
                break;

        }

        if (king == null) {
            return false;
        }

        // Can any opponent piece attack the king?
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Piece piece = gameState.getPiece(row, col);

                if (piece == null)
                    continue;

                if (piece.isWhite() == white)
                    continue;

                Move move = new Move(
                        piece,
                        row,
                        col,
                        kingRow,
                        kingCol,
                        king
                );

                if (piece.canMove(move, gameState)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true if the current player is in check.
     */
    public static boolean isCurrentPlayerInCheck(GameState gameState) {
        return isKingInCheck(gameState.isWhiteTurn(), gameState);
    }

    public static boolean wouldLeaveKingInCheck(Move move,
                                                GameState gameState) {

        gameState.makeTemporaryMove(move);

        boolean inCheck = isKingInCheck(
                move.getPiece().isWhite(),
                gameState
        );

        gameState.undoTemporaryMove(move);

        return inCheck;
    }


    public static King getCheckedKing(GameState gameState) {

        boolean whiteInCheck = isKingInCheck(true, gameState);
        boolean blackInCheck = isKingInCheck(false, gameState);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Piece piece = gameState.getPiece(row, col);

                if (piece instanceof King king) {

                    if (whiteInCheck && king.isWhite()) {
                        return king;
                    }

                    if (blackInCheck && !king.isWhite()) {
                        return king;
                    }
                }
            }
        }

        return null;
    }

    public static boolean canCastle(Move move, GameState gameState) {

        Piece piece = move.getPiece();

        if (!(piece instanceof King king)) {
            return false;
        }


        int row = move.getFromRow();
        int fromCol = move.getFromCol();
        int toCol = move.getToCol();


        // Must move exactly two squares
        if (Math.abs(toCol - fromCol) != 2) {
            return false;
        }


        // King cannot already be moved
        if (king.hasMoved()) {
            return false;
        }


        // King cannot castle while in check
        if (isKingInCheck(piece.isWhite(), gameState)) {
            return false;
        }


        int direction = toCol > fromCol ? 1 : -1;


        // ================= CHECK ROOK =================

        int rookCol = direction == 1 ? 7 : 0;

        Piece rookPiece = gameState.getPiece(row, rookCol);


        if (!(rookPiece instanceof Rook rook)) {
            return false;
        }


        if (rook.hasMoved()) {
            return false;
        }


        if (rook.isWhite() != king.isWhite()) {
            return false;
        }


        // ================= CHECK EMPTY PATH =================

        for (int c = fromCol + direction;
             c != rookCol;
             c += direction) {

            if (gameState.getPiece(row, c) != null) {
                return false;
            }
        }


        // ================= CHECK ATTACKED SQUARES =================

        for (int c = fromCol + direction;
             c != toCol + direction;
             c += direction) {


            Piece captured = gameState.getPiece(row, c);


            // Temporarily move king

            gameState.getBoard()[row][fromCol].clear();

            gameState.getBoard()[row][c].setPiece(king);


            int oldRow = king.getRow();
            int oldCol = king.getCol();


            king.setPositionSilently(row, c);


            boolean attacked =
                    isKingInCheck(
                            king.isWhite(),
                            gameState
                    );


            // Restore

            gameState.getBoard()[row][c].setPiece(captured);

            gameState.getBoard()[row][fromCol].setPiece(king);


            king.setPositionSilently(oldRow, oldCol);


            if (attacked) {
                return false;
            }
        }


        return true;
    }

    /**
     * Returns true if the current player is checkmated.
     * A player is checkmated when:
     * 1. Their king is in check.
     * 2. They have no legal move that removes the check.
     */
    public static boolean isCheckmate(GameState gameState) {

        // The current player must first be in check.
        if (!isCurrentPlayerInCheck(gameState)) {
            return false;
        }

        boolean white = gameState.isWhiteTurn();

        // Check every piece belonging to the current player.
        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece = gameState.getPiece(row, col);

                if (piece == null) {
                    continue;
                }

                if (piece.isWhite() != white) {
                    continue;
                }

                // Try every possible destination.
                for (int toRow = 0; toRow < 8; toRow++) {

                    for (int toCol = 0; toCol < 8; toCol++) {

                        Piece capturedPiece =
                                gameState.getPiece(toRow, toCol);

                        Move move = new Move(
                                piece,
                                row,
                                col,
                                toRow,
                                toCol,
                                capturedPiece
                        );

                        // Use the central validator so candidate moves are
                        // bounds-safe and obey all current chess rules.
                        if (MoveValidator.isLegalMove(move, gameState)) {
                            return false;
                        }
                    }
                }
            }
        }

        // In check and no legal escape move = checkmate.
        return true;
    }


    /**
     * Returns true if the current player is stalemated.
     * A player is stalemated when:
     * 1. Their king is NOT in check.
     * 2. They have no legal move.
     */
    public static boolean isStalemate(GameState gameState) {

        // Stalemate cannot occur while in check.
        if (isCurrentPlayerInCheck(gameState)) {
            return false;
        }

        boolean white = gameState.isWhiteTurn();

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece = gameState.getPiece(row, col);

                if (piece == null) {
                    continue;
                }

                if (piece.isWhite() != white) {
                    continue;
                }

                for (int toRow = 0; toRow < 8; toRow++) {

                    for (int toCol = 0; toCol < 8; toCol++) {

                        Piece capturedPiece =
                                gameState.getPiece(toRow, toCol);

                        Move move = new Move(
                                piece,
                                row,
                                col,
                                toRow,
                                toCol,
                                capturedPiece
                        );

                        if (MoveValidator.isLegalMove(move, gameState)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }


}

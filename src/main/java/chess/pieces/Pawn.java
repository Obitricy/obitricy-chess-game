package chess.pieces;

import chess.board.Tile;
import java.io.Serializable;

public class Pawn extends Piece implements Serializable {

    private static final long serialVersionUID = 1L;

    public Pawn(boolean white, int row, int col) {
        super(white, row, col);


    }

    @Override
    public boolean canMove(int toRow, int toCol, Tile[][] board) {

        // Prevent array index errors
        if (toRow < 0 || toRow >= 8 ||
                toCol < 0 || toCol >= 8) {
            return false;
        }

        int direction = white ? -1 : 1;

        Tile targetTile = board[toRow][toCol];
        Piece targetPiece = targetTile.getPiece();

        // ─────────────────────────────
        // 1. MOVE FORWARD (1 STEP)
        // ─────────────────────────────
        if (toCol == col && toRow == row + direction) {

            return targetPiece == null;
        }

        // ─────────────────────────────
        // 2. FIRST MOVE (2 STEPS)
        // ─────────────────────────────
        if (!hasMoved &&
                toCol == col &&
                toRow == row + 2 * direction &&
                board[row + direction][col].getPiece() == null &&
                targetPiece == null) {

            return true;
        }
        // ─────────────────────────────
        // 3. DIAGONAL CAPTURE
        // ─────────────────────────────
        if (Math.abs(toCol - col) == 1 &&
                toRow == row + direction) {

            return targetPiece != null &&
                    targetPiece.isWhite() != white;
        }

        return false;
    }

}
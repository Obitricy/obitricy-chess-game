package chess.pieces;

import chess.board.Tile;
import java.io.Serializable;

public class Queen extends Piece implements Serializable {

    private static final long serialVersionUID = 1L;

    public Queen(boolean white, int row, int col) {
            super(white, row, col);

    }

    @Override
    public boolean canMove(int toRow, int toCol, Tile[][] board) {

        // Queen moves like rook OR bishop
        boolean straight = row == toRow || col == toCol;
        boolean diagonal = Math.abs(row - toRow) == Math.abs(col - toCol);

        if (!straight && !diagonal) return false;

        int dRow = Integer.compare(toRow, row);
        int dCol = Integer.compare(toCol, col);

        int r = row + dRow;
        int c = col + dCol;

        // Check path blocking
        while (r != toRow || c != toCol) {

            if (board[r][c].getPiece() != null) {
                return false;
            }

            r += dRow;
            c += dCol;
        }

        // Destination tile
        Piece target = board[toRow][toCol].getPiece();

        // Cannot capture own piece
        return target == null || target.isWhite() != this.isWhite();
    }
}
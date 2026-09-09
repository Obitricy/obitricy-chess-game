package chess.pieces;

import chess.board.Tile;
import java.io.Serializable;


public class Rook extends Piece implements Serializable {
    private static final long serialVersionUID = 1L;


    public Rook(boolean white, int row, int col) {
        super(white, row, col);

    }

    @Override
    public boolean canMove(int toRow, int toCol, Tile[][] board) {

        // Can't stay on same square
        if (row == toRow && col == toCol) {
            return false;
        }

        // Must move in straight line
        if (row != toRow && col != toCol) {
            return false;
        }

        // ----------------------------
        // CHECK PATH OBSTRUCTION
        // ----------------------------

        int stepRow = Integer.compare(toRow, row); // -1, 0, or 1
        int stepCol = Integer.compare(toCol, col);

        int currentRow = row + stepRow;
        int currentCol = col + stepCol;

        while (currentRow != toRow || currentCol != toCol) {

            if (board[currentRow][currentCol].getPiece() != null) {
                return false; // blocked path
            }

            currentRow += stepRow;
            currentCol += stepCol;
        }

        // destination square can be empty or enemy piece
        Piece target = board[toRow][toCol].getPiece();

        return target == null || target.isWhite() != this.white;
    }

}
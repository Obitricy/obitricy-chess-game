package chess.pieces;

import chess.board.Tile;
import java.io.Serializable;
import chess.utils.ImageLoader;
import chess.utils.GameConfig;

import java.awt.Image;

public class Bishop extends Piece implements Serializable {

    private static final long serialVersionUID = 1L;

    public Bishop(boolean white, int row, int col) {
        super(white, row, col);

    }

    @Override
    public boolean canMove(int toRow, int toCol, Tile[][] board) {

        // 1. bounds check for both source and destination.
        // Checkmate analysis can test many candidate moves, so never
        // allow an invalid coordinate to reach the board array.
        if (row < 0 || row > 7 || col < 0 || col > 7
                || toRow < 0 || toRow > 7 || toCol < 0 || toCol > 7) {
            return false;
        }

        // 2. must move diagonally
        if (Math.abs(toRow - row) != Math.abs(toCol - col)) {
            return false;
        }

        int rowDir = (toRow > row) ? 1 : -1;
        int colDir = (toCol > col) ? 1 : -1;

        int r = row + rowDir;
        int c = col + colDir;

        // 3. check path is clear
        while (r != toRow && c != toCol) {

            if (board[r][c].getPiece() != null) {
                return false;
            }

            r += rowDir;
            c += colDir;
        }

        // 4. final square logic
        Piece target = board[toRow][toCol].getPiece();

        if (target == null) {
            return true;
        }

        return target.isWhite() != white;
    }
}
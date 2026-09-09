package chess.pieces;

import chess.board.Tile;
import java.io.Serializable;
import chess.utils.ImageLoader;
import chess.utils.GameConfig;

import java.awt.Image;

public class Knight extends Piece implements Serializable {

    private static final long serialVersionUID = 1L;

    public Knight(boolean white, int row, int col) {
        super(white, row, col);

    }

    @Override
    public boolean canMove(int toRow, int toCol, Tile[][] board) {

        int dr = Math.abs(toRow - row);
        int dc = Math.abs(toCol - col);

        // L-shaped movement
        boolean validMove = (dr == 2 && dc == 1) || (dr == 1 && dc == 2);

        if (!validMove) return false;

        // Check destination tile
        Piece target = board[toRow][toCol].getPiece();

        // Cannot capture own piece
        return target == null || target.isWhite() != this.isWhite();
    }
}
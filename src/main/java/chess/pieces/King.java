package chess.pieces;

import chess.board.Tile;

import chess.game.Move;
import java.io.Serializable;
import chess.board.GameState;


public class King extends Piece implements Serializable {

    private static final long serialVersionUID = 1L;



    public King(boolean white, int row, int col) {
        super(white, row, col);

    }

    @Override
    public boolean canMove(int toRow, int toCol, Tile[][] board) {

        if (toRow == row && toCol == col) {
            return false;
        }

        int dr = Math.abs(toRow - row);
        int dc = Math.abs(toCol - col);

        // ================= NORMAL KING MOVE =================

        if (dr <= 1 && dc <= 1) {

            Piece target = board[toRow][toCol].getPiece();

            return target == null
                    || target.isWhite() != this.isWhite();
        }

        // ================= NOT A NORMAL KING MOVE =================
        //
        // Castling must NOT be accepted here.
        // Castling is handled by MoveValidator/CastlingRules.
        //
        return false;
    }

    @Override
    public boolean canMove(Move move, GameState gameState) {


        // ================= NORMAL MOVE =================

        boolean result = canMove(
                move.getToRow(),
                move.getToCol(),
                gameState.getBoard()
        );


        if (result) {
            System.out.printf(
                    "KING.canMove() ACCEPTED (%d,%d)->(%d,%d)%n",
                    move.getFromRow(),
                    move.getFromCol(),
                    move.getToRow(),
                    move.getToCol()
            );
        }


        return result;
    }


}
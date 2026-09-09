package chess.pieces;

import chess.board.Tile;
import chess.utils.ImageLoader;
import chess.game.Move;
import chess.board.GameState;
import chess.player.Player;
import java.io.Serializable;

import java.awt.Image;

public abstract class Piece implements Serializable {

    private static final long serialVersionUID = 1L;

    protected int row;
    protected int col;
    protected boolean white;
    protected boolean hasMoved = false;

    protected transient Image image;

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }


    public Piece(boolean white, int row, int col) {

        this.white = white;
        this.row = row;
        this.col = col;

        this.image = ImageLoader.loadPiece(this);
    }


    // -------------------------
    // BASIC GETTERS
    // -------------------------

    public boolean isWhite() {
        return white;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }


    // -------------------------
    // POSITION UPDATE
    // -------------------------

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
        this.hasMoved = true;
    }

    public void setPositionSilently(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // -------------------------
    // MOVEMENT RULES
    // -------------------------

    public abstract boolean canMove(int toRow, int toCol, Tile[][] board);

    public boolean canMove(Move move, GameState gameState) {
        return canMove(
                move.getToRow(),
                move.getToCol(),
                gameState.getBoard()
        );
    }

    // -------------------------
    // OPTIONAL HELPERS
    // -------------------------


    public Player.Color getColor() {
        return white ? Player.Color.WHITE : Player.Color.BLACK;
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    public Image getImage() {
        return image;
    }

    public void reloadImage() {
        image = ImageLoader.loadPiece(this);
    }

    @Override
    public String toString() {
        return getColor() + " " + getType()
                + " (" + row + "," + col + ")";
    }
}
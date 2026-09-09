package chess.board;

import chess.pieces.Piece;

import java.io.Serializable;

public class Tile implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int row;
    private final int column;
    private Piece piece;

    public Tile(int row, int column) {
        this.row = row;
        this.column = column;
        this.piece = null;
    }

    // ---------------- LOCATION ----------------

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    // ---------------- PIECE ----------------

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public void clear() {
        piece = null;
    }

    public Piece removePiece() {
        Piece removed = piece;
        piece = null;
        return removed;
    }


    public boolean isEmpty() {
        return piece == null;
    }

    // ---------------- HELPERS ----------------

    @Override
    public String toString() {
        if (piece == null) {
            return ".";
        }
        return piece.toString();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Tile other)) {
            return false;
        }

        return row == other.row && column == other.column;
    }

    @Override
    public int hashCode() {
        return 31 * row + column;
    }
}
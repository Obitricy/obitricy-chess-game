package chess.player;

import chess.pieces.Piece;
import chess.board.Tile;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Color {
        WHITE,
        BLACK
    }

    private final String name;
    private final Color color;

    // Captured pieces
    private final List<Piece> capturedPieces;

    // Optional: track tiles this player controls or last move origin/destination
    private Tile selectedTile;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.capturedPieces = new ArrayList<>();
    }



    // ---------------- BASIC INFO ----------------

    public String getName() {

        return name;
    }

    public Color getColor() {

        return color;
    }

    public String getDisplayName() {
        return name;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
    // ---------------- CAPTURED PIECES ----------------

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public void addCapturedPiece(Piece piece) {
        if (piece != null) {
            capturedPieces.add(piece);
        }
    }

    public void resetCapturedPieces() {
        capturedPieces.clear();
    }

    // ---------------- TILE SUPPORT ----------------

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public void setSelectedTile(Tile selectedTile) {
        this.selectedTile = selectedTile;
    }
}

package chess.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

public class MoveHistory implements Serializable {

    private static final long serialVersionUID = 1L;


    private final List<Move> moves = new ArrayList<>();

    /**
     * Add a move to the history.
     */
    public void addMove(Move move) {
        if (move != null) {
            moves.add(move);
        }
    }

    /**
     * Remove the last move (useful for Undo).
     */
    public Move removeLastMove() {
        if (moves.isEmpty()) {
            return null;
        }

        return moves.remove(moves.size() - 1);
    }

    /**
     * Get the last move.
     */
    public Move getLastMove() {
        if (moves.isEmpty()) {
            return null;
        }

        return moves.get(moves.size() - 1);
    }

    /**
     * Get all moves.
     */
    public List<Move> getMoves() {
        return Collections.unmodifiableList(moves);
    }

    /**
     * Number of moves played.
     */
    public int size() {
        return moves.size();
    }

    /**
     * Clear history.
     */
    public void clear() {
        moves.clear();
    }
}
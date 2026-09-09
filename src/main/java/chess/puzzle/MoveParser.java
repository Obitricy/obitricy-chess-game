package chess.puzzle;

public final class MoveParser {

    private MoveParser() {
    }

    public static PuzzleMove parse(String move) {

        if (move == null || move.length() < 4) {
            throw new IllegalArgumentException("Invalid move: " + move);
        }

        int fromCol = move.charAt(0) - 'a';
        int fromRow = 8 - Character.getNumericValue(move.charAt(1));

        int toCol = move.charAt(2) - 'a';
        int toRow = 8 - Character.getNumericValue(move.charAt(3));

        return new PuzzleMove(
                fromRow,
                fromCol,
                toRow,
                toCol
        );
    }
}
package chess.puzzle;

import java.util.ArrayList;
import java.util.List;

import chess.board.GameState;
import chess.game.Move;
import chess.pieces.Piece;

public class PuzzleManager {

    private final List<Puzzle> puzzles = new ArrayList<>();

    private int currentPuzzle = 0;
    private int solutionIndex = 0;

    public Puzzle getCurrentPuzzle() {
        return puzzles.get(currentPuzzle);
    }

    public boolean hasNextPuzzle() {
        return currentPuzzle < puzzles.size() - 1;
    }

    public void nextPuzzle() {
        if (hasNextPuzzle()) {
            currentPuzzle++;
            solutionIndex = 0;
        }
    }

    public void addPuzzle(Puzzle puzzle) {
        puzzles.add(puzzle);
    }

    public void resetPuzzle() {
        solutionIndex = 0;
    }

    /**
     * Loads the current puzzle position.
     *
     * In a Lichess puzzle, the first move in the solution
     * is the opponent's move. We therefore play that move
     * automatically and start the player at solution move 1.
     */
    public void loadCurrentPuzzle(GameState gameState) {

        FenLoader.load(
                gameState,
                getCurrentPuzzle().getFen()
        );

        solutionIndex = 0;

        /*
         * Lichess puzzle:
         *
         * FEN
         *  ↓
         * solution[0] = opponent's move
         *  ↓
         * player must find solution[1]
         */
        PuzzleMove opponentMove = getNextSolutionMove();

        if (opponentMove != null) {

            Move move = createMove(gameState, opponentMove);

            if (move != null) {

                System.out.println(
                        "PUZZLE AUTO MOVE BEFORE: "
                                + (gameState.isWhiteTurn()
                                ? "WHITE"
                                : "BLACK")
                );

                boolean success = gameState.makeMove(move);

                System.out.println(
                        "PUZZLE AUTO MOVE RESULT: "
                                + success
                );

                System.out.println(
                        "PUZZLE AUTO MOVE AFTER: "
                                + (gameState.isWhiteTurn()
                                ? "WHITE"
                                : "BLACK")
                );

                if (success) {
                    solutionIndex++;
                }
            }
        }
    }

    /**
     * Checks whether the player's move matches
     * the next expected puzzle move.
     */
    public boolean isCorrectMove(Move move) {

        PuzzleMove expected = getNextSolutionMove();

        System.out.println("========== PUZZLE DEBUG ==========");
        System.out.println("Solution index: " + solutionIndex);

        if (expected == null) {
            System.out.println("Expected move: NULL");
            return false;
        }

        System.out.println(
                "Expected: "
                        + expected.getFromRow()
                        + ","
                        + expected.getFromCol()
                        + " -> "
                        + expected.getToRow()
                        + ","
                        + expected.getToCol()
        );

        System.out.println(
                "Actual:   "
                        + move.getFromRow()
                        + ","
                        + move.getFromCol()
                        + " -> "
                        + move.getToRow()
                        + ","
                        + move.getToCol()
        );

        boolean correct =
                move.getFromRow() == expected.getFromRow()
                        && move.getFromCol() == expected.getFromCol()
                        && move.getToRow() == expected.getToRow()
                        && move.getToCol() == expected.getToCol();

        System.out.println("Correct: " + correct);
        System.out.println("===================================");

        return correct;
    }

    public PuzzleMove getNextSolutionMove() {

        Puzzle puzzle = getCurrentPuzzle();

        System.out.println("========== SOLUTION DEBUG ==========");
        System.out.println("Puzzle ID: " + puzzle.getId());
        System.out.println("Solution size: " + puzzle.getSolution().size());
        System.out.println("Solution index: " + solutionIndex);

        for (int i = 0; i < puzzle.getSolution().size(); i++) {

            PuzzleMove move = puzzle.getSolution().get(i);

            System.out.println(
                    "solution[" + i + "]: "
                            + move.getFromRow()
                            + ","
                            + move.getFromCol()
                            + " -> "
                            + move.getToRow()
                            + ","
                            + move.getToCol()
            );
        }

        System.out.println("===================================");

        if (puzzleSolved()) {
            return null;
        }

        return puzzle.getSolution().get(solutionIndex);
    }

    public Move createMove(
            GameState gameState,
            PuzzleMove puzzleMove) {

        Piece piece = gameState.getPiece(
                puzzleMove.getFromRow(),
                puzzleMove.getFromCol()
        );

        if (piece == null) {
            return null;
        }

        Piece captured = gameState.getPiece(
                puzzleMove.getToRow(),
                puzzleMove.getToCol()
        );

        return new Move(
                piece,
                puzzleMove.getFromRow(),
                puzzleMove.getFromCol(),
                puzzleMove.getToRow(),
                puzzleMove.getToCol(),
                captured
        );
    }

    public void advanceSolution() {
        solutionIndex++;
    }

    public boolean puzzleSolved() {

        return solutionIndex >= getCurrentPuzzle()
                .getSolution()
                .size();
    }

    public int getPuzzleIndex() {
        return currentPuzzle;
    }

    public int getSolutionIndex() {
        return solutionIndex;
    }
}
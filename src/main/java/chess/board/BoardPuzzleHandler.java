package chess.board;

import chess.game.Move;
import chess.puzzle.PuzzleManager;
import chess.audio.SoundManager;
import chess.ui.PuzzleSolvedDialog;
import chess.ui.IncorrectMoveDialog;

import javax.swing.*;

public class BoardPuzzleHandler {

    private final ChessBoard board;
    private final GameState gameState;
    private final BoardUIState ui;

    private boolean puzzleSolvedShown = false;
    private boolean incorrectMoveShown = false;

    public BoardPuzzleHandler(
            ChessBoard board,
            GameState gameState,
            BoardUIState ui) {

        this.board = board;
        this.gameState = gameState;
        this.ui = ui;
    }

    public boolean isCorrectMove(Move move) {

        PuzzleManager manager =
                gameState.getPuzzleManager();

        if (manager.isCorrectMove(move)) {

            SoundManager.playPuzzleCorrect();

            return true;
        }

        SoundManager.playPuzzleWrong();

        /*
         * Show the polished incorrect-move dialog
         * instead of the default JOptionPane.
         */
        if (!incorrectMoveShown) {

            incorrectMoveShown = true;

            IncorrectMoveDialog.show(
                    board,
                    () -> {
                        incorrectMoveShown = false;

                        ui.clearSelection();
                        ui.clearLegalMoves();

                        board.repaint();
                    }
            );
        }

        ui.clearSelection();
        ui.clearLegalMoves();

        board.repaint();

        return false;
    }

    /**
     * Advances to the next puzzle and loads its position.
     */
    public boolean nextPuzzle() {

        PuzzleManager manager =
                gameState.getPuzzleManager();

        if (!manager.hasNextPuzzle()) {

            JOptionPane.showMessageDialog(
                    board,
                    "Congratulations! You completed all puzzles! 🎉",
                    "Puzzle Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return false;
        }

        // Reset dialog state for the new puzzle.
        puzzleSolvedShown = false;
        incorrectMoveShown = false;

        // Move from current puzzle to next puzzle
        manager.nextPuzzle();

        // Clear the old board state and load the new puzzle.
        manager.loadCurrentPuzzle(gameState);

        // Clear any old selection/highlight information.
        ui.clearSelection();
        ui.clearLegalMoves();

        board.repaint();

        System.out.println(
                "Loaded Puzzle "
                        + (manager.getPuzzleIndex() + 1)
        );

        return true;
    }

    /**
     * Kept for cases where an opponent move needs
     * to be played manually.
     */
    public boolean playOpponentMove() {

        PuzzleManager manager =
                gameState.getPuzzleManager();

        var puzzleMove =
                manager.getNextSolutionMove();

        if (puzzleMove == null) {
            return false;
        }

        Move move =
                manager.createMove(
                        gameState,
                        puzzleMove
                );

        if (move == null) {

            System.out.println(
                    "Puzzle error: unable to create opponent move."
            );

            return false;
        }

        if (!gameState.makeMove(move)) {

            System.out.println(
                    "Puzzle error: unable to execute opponent move."
            );

            return false;
        }

        manager.advanceSolution();

        ui.clearSelection();
        ui.clearLegalMoves();

        board.repaint();

        return true;
    }

    public void showSolved() {

        // Prevent the solved dialog from appearing more than once
        if (puzzleSolvedShown) {
            return;
        }

        puzzleSolvedShown = true;
        incorrectMoveShown = false;

        SoundManager.playPuzzleComplete();

        PuzzleSolvedDialog.show(
                board,
                () -> {
                    resetSolvedState();
                    board.nextPuzzle();
                }
        );

        board.enableNextPuzzleButton();
    }

    public void resetSolvedState() {
        puzzleSolvedShown = false;
        incorrectMoveShown = false;
    }
}
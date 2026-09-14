package chess.board;

import chess.audio.SoundManager;
import chess.game.GameStatusManager;
import chess.puzzle.PuzzleManager;
import chess.save.SaveManager;
import chess.ui.CheckDialog;

public class BoardGameFlow {

    private final ChessBoard board;
    private final GameState gameState;
    private final BoardUIState ui;
    private final BoardLayout layout;
    private final GameStatusManager statusManager;

    public BoardGameFlow(
            ChessBoard board,
            GameState gameState,
            BoardUIState ui,
            BoardLayout layout) {

        this.board = board;
        this.gameState = gameState;
        this.ui = ui;
        this.layout = layout;

        this.statusManager =
                new GameStatusManager(gameState);
    }

    // ================= UNDO =================

    public void undoLastMove() {

        ui.clearAnimations();

        ui.clearSelection();
        ui.clearLegalMoves();
        ui.clearHint();

        gameState.undoMove();

        board.getTimer().restart();

        SoundManager.playUndo();

        board.repaint();
    }

// ================= REDO =================

    public void redoLastMove() {

        ui.clearAnimations();

        ui.clearSelection();
        ui.clearLegalMoves();
        ui.clearHint();

        gameState.redoMove();

        statusManager.update();

        GameStatusManager.Status status =
                statusManager.getStatus();

        if (status == GameStatusManager.Status.CHECKMATE
                || status == GameStatusManager.Status.STALEMATE
                || status == GameStatusManager.Status.DRAW_INSUFFICIENT_MATERIAL) {

            board.getTimer().stop();

            ui.setGameOverTitle(
                    statusManager.getTitle()
            );

            ui.setGameOverMessage(
                    statusManager.getMessage()
            );

        } else {

            board.getTimer().restart();
        }

        SoundManager.playRedo();

        board.repaint();
    }

    // ================= RESET =================

    public void resetBoard() {

        SaveManager.deleteSave();

        ui.clearAnimations();

        gameState.reset();

        ui.clearSelection();
        ui.clearLegalMoves();
        ui.clearLastMove();
        ui.clearHint();

        board.getTimer().restart();

        board.repaint();
    }

    // ================= NEXT PUZZLE =================

    public void nextPuzzle() {

        PuzzleManager manager =
                gameState.getPuzzleManager();

        if (manager == null) {
            System.out.println("NEXT PUZZLE ERROR: PuzzleManager is null");
            return;
        }

        if (!manager.hasNextPuzzle()) {

            board.getDialogs().showMessage(
                    "Puzzles",
                    "No more puzzles."
            );

            return;
        }

        System.out.println("=================================");
        System.out.println("MOVING TO NEXT PUZZLE");
        System.out.println("Current puzzle BEFORE: "
                + manager.getPuzzleIndex());

        /*
         * Move PuzzleManager to the next puzzle.
         */
        manager.nextPuzzle();
        board.resetPuzzleSolvedState();

        System.out.println("Current puzzle AFTER: "
                + manager.getPuzzleIndex());

        /*
         * Clear the old board/UI state.
         */
        ui.clearAnimations();
        ui.clearSelection();
        ui.clearLegalMoves();
        ui.clearHint();
        ui.clearLastMove();

        /*
         * Load the new FEN and automatically
         * play the first solution move.
         */
        manager.loadCurrentPuzzle(gameState);

        /*
         * IMPORTANT:
         * After the automatic opponent move,
         * it must be the player's turn.
         */
        System.out.println(
                "NEXT PUZZLE TURN = "
                        + (gameState.isWhiteTurn()
                        ? "WHITE"
                        : "BLACK")
        );

        System.out.println(
                "NEXT PUZZLE SOLUTION INDEX = "
                        + manager.getSolutionIndex()
        );

        System.out.println("=================================");

        board.repaint();
    }
    // ================= CHECK GAME FINISHED =================

    public void checkGameFinished() {

        /*
         * Evaluate the current position.
         */
        statusManager.update();

        GameStatusManager.Status status =
                statusManager.getStatus();

        /*
         * ================= PLAYING =================
         *
         * No special game status.
         */
        if (status == GameStatusManager.Status.PLAYING) {
            return;
        }

        /*
         * ================= CHECK =================
         *
         * The game continues, but the current player
         * is in check.
         */
        if (status == GameStatusManager.Status.CHECK) {

            SoundManager.playCheck();

            CheckDialog.show(
                    board,
                    statusManager.getMessage()
            );

            return;
        }

        /*
         * ================= CHECKMATE =================
         */
        if (status == GameStatusManager.Status.CHECKMATE) {

            SoundManager.playCheckmate();

            ui.setGameOverTitle(
                    statusManager.getTitle()
            );

            ui.setGameOverMessage(
                    statusManager.getMessage()
            );

            board.getTimer().stop();

            board.fireGameOver();

            return;
        }

        /*
         * ================= STALEMATE =================
         */
        if (status == GameStatusManager.Status.STALEMATE) {

            SoundManager.playStalemate();

            ui.setGameOverTitle(
                    statusManager.getTitle()
            );

            ui.setGameOverMessage(
                    statusManager.getMessage()
            );

            board.getTimer().stop();

            board.fireGameOver();

            return;
        }

        /*
         * ================= INSUFFICIENT MATERIAL =================
         */
        if (status ==
                GameStatusManager.Status.DRAW_INSUFFICIENT_MATERIAL) {

            ui.setGameOverTitle(
                    statusManager.getTitle()
            );

            ui.setGameOverMessage(
                    statusManager.getMessage()
            );

            board.getTimer().stop();

            board.fireGameOver();
        }
    }

    // ================= STATUS ACCESS =================

    public GameStatusManager getStatusManager() {
        return statusManager;
    }
}
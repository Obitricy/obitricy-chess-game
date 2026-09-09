package chess.board;

import chess.game.GameMode;
import chess.game.Move;
import chess.puzzle.PuzzleManager;

public class BoardInputHandler {

    private final ChessBoard board;
    private final GameState gameState;
    private final BoardUIState ui;
    private final BoardMoveHandler moveHandler;
    private final BoardPuzzleHandler puzzleHandler;

    public BoardInputHandler(
            ChessBoard board,
            GameState gameState,
            BoardUIState ui,
            BoardMoveHandler moveHandler,
            BoardPuzzleHandler puzzleHandler) {

        this.board = board;
        this.gameState = gameState;
        this.ui = ui;
        this.moveHandler = moveHandler;
        this.puzzleHandler = puzzleHandler;
    }

    // ---------------- CLICK ----------------

    void handleClick(int row, int col) {

        /*
         * ==========================================
         * ONLINE MULTIPLAYER TURN CONTROL
         * ==========================================
         */

        if (gameState.getGameMode() == GameMode.ONLINE) {

            if (gameState.isWhiteTurn() !=
                    board.isPlayerIsWhite()) {

                System.out.println(
                        "ONLINE: Waiting for opponent."
                );

                return;
            }
        }

        System.out.println(
                "CLICK: row=" + row
                        + " col=" + col
                        + " piece=" + gameState.getPiece(row, col)
                        + " whiteTurn=" + gameState.isWhiteTurn()
                        + " selected=" + ui.getSelected()
        );

        /*
         * ==========================================
         * FIRST CLICK - SELECT A PIECE
         * ==========================================
         */
        if (ui.getSelected() == null) {

            if (moveHandler.selectPiece(row, col)) {

                System.out.println(
                        "PIECE SELECTED: "
                                + row + "," + col
                );

                return;
            }

            System.out.println(
                    "NO PIECE SELECTED AT: "
                            + row + "," + col
            );

            return;
        }

        /*
         * ==========================================
         * SECOND CLICK - CREATE MOVE
         * ==========================================
         */
        Move move = moveHandler.createMove(row, col);

        if (move == null) {
            System.out.println(
                    "Could not create move from selected piece."
            );
            return;
        }

        /*
         * ==========================================
         * NORMAL CHESS LEGALITY
         * ==========================================
         */
        if (!moveHandler.isLegalMove(move)) {

            System.out.println(
                    "Illegal chess move."
            );

            moveHandler.resetSelection();

            return;
        }

        /*
         * ==========================================
         * PUZZLE MODE
         * ==========================================
         */
        if (gameState.getGameMode() == GameMode.PUZZLE) {

            PuzzleManager manager =
                    gameState.getPuzzleManager();

            /*
             * Check the player's move against
             * the expected puzzle solution.
             */
            if (!puzzleHandler.isCorrectMove(move)) {

                System.out.println(
                        "Incorrect puzzle move."
                );

                moveHandler.resetSelection();

                return;
            }

            /*
             * Execute the correct player's move.
             *
             * IMPORTANT:
             * finishMove() performs the actual board move.
             */
            if (!moveHandler.finishMove(move)) {
                return;
            }

            /*
             * Advance exactly ONCE.
             */
            manager.advanceSolution();

            /*
             * Was this the final puzzle move?
             */
            if (manager.puzzleSolved()) {

                puzzleHandler.showSolved();

                moveHandler.resetSelection();

                return;
            }

            /*
             * Play the opponent's response automatically.
             */
            if (!puzzleHandler.playOpponentMove()) {

                System.out.println(
                        "Unable to play puzzle opponent move."
                );

                return;
            }

            /*
             * Clear player's selection.
             */
            moveHandler.resetSelection();

            return;
        }

        /*
         * ==========================================
         * NORMAL GAME MODE
         * ==========================================
         */
        if (moveHandler.finishMove(move)) {
            moveHandler.resetSelection();
        }
    }
}
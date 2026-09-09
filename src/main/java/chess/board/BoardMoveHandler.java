package chess.board;
import java.awt.KeyboardFocusManager;
import java.awt.Window;

import chess.game.GameMode;
import chess.game.Move;
import chess.audio.SoundManager;
import chess.pieces.Piece;
import chess.pieces.Pawn;
import chess.rules.MoveValidator;
import chess.save.SaveManager;
import chess.ui.PromotionDialog;

public class BoardMoveHandler {

    private final ChessBoard board;
    private final GameState gameState;
    private final BoardUIState ui;
    private final BoardPuzzleHandler puzzleHandler;
    public BoardMoveHandler(
            ChessBoard board,
            GameState gameState,
            BoardUIState ui,
            BoardPuzzleHandler puzzleHandler) {

        this.board = board;
        this.gameState = gameState;
        this.ui = ui;
        this.puzzleHandler = puzzleHandler;
    }

    public boolean isLegalMove(Move move) {

        if (move == null) {
            return false;
        }

        return MoveValidator.isLegalMove(move, gameState);
    }

    public void updateLegalMoves() {

        // IMPORTANT:
        // Do NOT clear the selected piece here.
        // This method is called AFTER a piece has been selected.
        ui.clearLegalMoves();

        if (ui.getSelected() == null) {
            return;
        }

        int selectedRow = ui.getSelectedRow();
        int selectedCol = ui.getSelectedCol();

        if (selectedRow < 0 || selectedRow >= 8
                || selectedCol < 0 || selectedCol >= 8) {
            return;
        }

        Piece piece = gameState.getPiece(
                selectedRow,
                selectedCol
        );

        if (piece == null) {
            ui.clearSelection();
            return;
        }

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Move move = new Move(
                        piece,
                        selectedRow,
                        selectedCol,
                        row,
                        col,
                        gameState.getPiece(row, col)
                );

                if (MoveValidator.isLegalMove(move, gameState)) {
                    ui.getLegalMoves().add(move);
                }
            }
        }
    }

    public boolean selectPiece(int row, int col) {

        Piece clicked =
                gameState.getPiece(row, col);

        /*
         * Selecting a different friendly piece
         * while another piece is already selected.
         */
        if (ui.getSelected() != null
                && clicked != null
                && clicked.isWhite() == gameState.isWhiteTurn()) {

            ui.setSelected(clicked);
            ui.setSelectedRow(row);
            ui.setSelectedCol(col);

            updateLegalMoves();

            board.repaint();

            return true;
        }

        /*
         * Nothing is currently selected.
         */
        if (ui.getSelected() == null) {

            if (clicked != null
                    && clicked.isWhite()
                    == gameState.isWhiteTurn()) {

                ui.setSelected(clicked);
                ui.setSelectedRow(row);
                ui.setSelectedCol(col);

                updateLegalMoves();

                board.repaint();

                return true;
            }
        }

        return false;
    }


    public Move createMove(int row, int col) {

        Piece selected = ui.getSelected();

        if (selected == null) {
            System.out.println("Cannot create move: no selected piece");
            return null;
        }

        return new Move(
                selected,
                ui.getSelectedRow(),
                ui.getSelectedCol(),
                row,
                col,
                gameState.getPiece(row, col)
        );
    }

    public void resetSelection() {

        ui.clearSelection();
        ui.clearLegalMoves();

        board.repaint();
    }


    public boolean finishMove(Move move) {

        if (move == null) {
            return false;
        }

        /*
         * PUZZLE MODE
         *
         * Check the player's move BEFORE applying it.
         */
        if (gameState.getGameMode() == GameMode.PUZZLE) {

            gameState.getPuzzleManager().advanceSolution();

            if (gameState.getPuzzleManager().puzzleSolved()) {
                puzzleHandler.showSolved();
            }
        }

        /*
         * Remember whether this move captures a piece
         * BEFORE GameState changes the board.
         */
        boolean isCapture =
                move.getCapturedPiece() != null
                        || gameState.isEnPassantMove(move);

        /*
         * Execute the move.
         */
        // Ask for the promotion piece only after the destination square
        // has been clicked. GameState must remain UI-free.
        Piece movingPiece = move.getPiece();
        if (movingPiece instanceof Pawn
                && ((movingPiece.isWhite() && move.getToRow() == 0)
                || (!movingPiece.isWhite() && move.getToRow() == 7))
                && move.getPromotedPiece() == null) {

            Window owner = KeyboardFocusManager
                    .getCurrentKeyboardFocusManager()
                    .getActiveWindow();

            Piece promoted = PromotionDialog.choose(
                    owner,
                    movingPiece.isWhite(),
                    move.getToRow(),
                    move.getToCol());

            if (promoted == null) {
                promoted = new chess.pieces.Queen(movingPiece.isWhite(), move.getToRow(), move.getToCol());
            }

            move.setPromotion(promoted);
        }

        if (!gameState.makeMove(move)) {
            return false;
        }

        /*
         * Puzzle move was correct and has now
         * been successfully executed.
         */
        if (gameState.getGameMode() == GameMode.PUZZLE) {

            gameState.getPuzzleManager().advanceSolution();

            if (gameState.getPuzzleManager().puzzleSolved()) {

                puzzleHandler.showSolved();

            }
        }

        /*
         * Play move sound.
         */
        if (isCapture) {
            SoundManager.playCapture();
        } else {
            SoundManager.playMove();
        }

        /*
         * Animate the move.
         */
        board.animateMove(move);

        /*
         * Notify listeners that a move was successfully made.
         */
        board.fireMoveMade(move);


        /*
         * ONLINE MULTIPLAYER
         *
         * Send the successfully executed local move
         * to the opponent.
         */
        if (gameState.getGameMode() == GameMode.ONLINE) {

            board.fireOnlineMove(move);
        }

        /*
         * Clear UI state.
         */
        board.getHintManager().clearHint();
        ui.clearSelection();
        ui.clearLegalMoves();

        /*
         * Save the position.
         */
        SaveManager.save(gameState);

        board.repaint();

        /*
         * Let BoardGameFlow determine:
         *
         * CHECK
         * CHECKMATE
         * STALEMATE
         * DRAW
         * PLAYING
         */
        board.getGameFlow().checkGameFinished();

        return true;
    }

}
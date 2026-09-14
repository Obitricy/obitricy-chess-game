package chess.board;

import chess.ui.IncorrectMoveDialog;
import chess.ui.ObitricyDialog;
import chess.ui.PuzzleSolvedDialog;

/**
 * Handles dialogs displayed by the chess board.
 *
 * Uses the Obitricy premium dialog system instead of
 * the default Swing JOptionPane appearance.
 *
 * IMPORTANT:
 * This class only changes dialog presentation.
 * Chess/game logic remains unchanged.
 */
public final class BoardDialogs {

    private final ChessBoard board;

    public BoardDialogs(ChessBoard board) {
        this.board = board;
    }

    public void showMessage(
            String title,
            String message
    ) {
        ObitricyDialog.showMessage(
                board,
                title,
                message
        );
    }

    public void showWarning(
            String title,
            String message
    ) {
        ObitricyDialog.showWarning(
                board,
                title,
                message
        );
    }

    public void showError(
            String title,
            String message
    ) {
        ObitricyDialog.showError(
                board,
                title,
                message
        );
    }

    public boolean confirm(
            String title,
            String message
    ) {
        return ObitricyDialog.confirm(
                board,
                title,
                message
        );
    }

    public void showGameOver(
            String title,
            String message
    ) {
        ObitricyDialog.showMessage(
                board,
                title,
                message
        );
    }

    public void showPuzzleSolved() {
        PuzzleSolvedDialog.show(
                board,
                () -> board.nextPuzzle()
        );
    }

    public void showCheckmate(
            String winner
    ) {
        ObitricyDialog.showSuccess(
                board,
                "Checkmate",
                winner + " wins by checkmate!"
        );
    }

    public void showStalemate() {
        ObitricyDialog.showInfo(
                board,
                "Stalemate",
                "The game ends in a stalemate."
        );
    }

    public void showDraw(
            String reason
    ) {
        ObitricyDialog.showInfo(
                board,
                "Draw",
                reason
        );
    }

    public boolean confirmNewGame() {
        return confirm(
                "New Game",
                "Are you sure you want to start a new game?"
        );
    }

    public boolean confirmExit() {
        return confirm(
                "Exit",
                "Are you sure you want to exit the game?"
        );
    }

    public void showPuzzleIncorrect() {
        IncorrectMoveDialog.show(board);
    }
}
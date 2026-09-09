package chess.board;

import javax.swing.JOptionPane;
import chess.ui.PuzzleSolvedDialog;
import chess.ui.IncorrectMoveDialog;


/**
 * Handles dialogs displayed by the chess board.
 *
 * This class keeps JOptionPane usage out of ChessBoard
 * and other board-management classes.
 */
public final class BoardDialogs {

    private final ChessBoard board;

    public BoardDialogs(ChessBoard board) {
        this.board = board;
    }

    /**
     * Displays a general information message.
     */
    public void showMessage(
            String title,
            String message) {

        JOptionPane.showMessageDialog(
                board,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays a warning message.
     */
    public void showWarning(
            String title,
            String message) {

        JOptionPane.showMessageDialog(
                board,
                message,
                title,
                JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Displays an error message.
     */
    public void showError(
            String title,
            String message) {

        JOptionPane.showMessageDialog(
                board,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Displays a confirmation dialog.
     *
     * @return true when the user selects Yes.
     */
    public boolean confirm(
            String title,
            String message) {

        int result = JOptionPane.showConfirmDialog(
                board,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Displays a game-over dialog.
     */
    public void showGameOver(
            String title,
            String message) {

        JOptionPane.showMessageDialog(
                board,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays a puzzle-solved dialog.
     */
    public void showPuzzleSolved() {

        PuzzleSolvedDialog.show(
                board,
                () -> board.nextPuzzle()
        );
    }

    /**
     * Displays a checkmate dialog.
     */
    public void showCheckmate(
            String winner) {

        JOptionPane.showMessageDialog(
                board,
                winner + " wins by checkmate!",
                "Checkmate",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays a stalemate dialog.
     */
    public void showStalemate() {

        JOptionPane.showMessageDialog(
                board,
                "The game ends in a stalemate.",
                "Stalemate",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays a draw dialog.
     */
    public void showDraw(
            String reason) {

        JOptionPane.showMessageDialog(
                board,
                reason,
                "Draw",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Displays a confirmation before starting a new game.
     */
    public boolean confirmNewGame() {

        return confirm(
                "New Game",
                "Are you sure you want to start a new game?"
        );
    }

    /**
     * Displays a confirmation before quitting.
     */
    public boolean confirmExit() {

        return confirm(
                "Exit",
                "Are you sure you want to exit the game?"
        );
    }

    /**
     * Displays the custom premium dialog used when the player
     * makes an incorrect move in a puzzle.
     */
    public void showPuzzleIncorrect() {

        IncorrectMoveDialog.show(board);
    }
}

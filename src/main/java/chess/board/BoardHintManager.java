package chess.board;

import chess.ai.ComputerDifficulty;
import chess.game.Move;
import chess.player.ComputerPlayer;

/**
 * Manages chess hints and hint-related board UI state.
 *
 * BoardHintManager is responsible for calculating and displaying
 * hints. It does not execute the suggested move.
 */
public class BoardHintManager {

    private final ChessBoard board;
    private final GameState gameState;
    private final BoardUIState ui;

    public BoardHintManager(
            ChessBoard board,
            GameState gameState,
            BoardUIState ui) {

        this.board = board;
        this.gameState = gameState;
        this.ui = ui;
    }

    /**
     * Calculates and displays a hint for Training mode.
     */
    public void showHint() {

        if (gameState.getGameMode()
                != chess.game.GameMode.TRAINING) {

            return;
        }

        ComputerPlayer coach = new ComputerPlayer(

                gameState.isWhiteTurn()
                        ? chess.player.Player.Color.WHITE
                        : chess.player.Player.Color.BLACK,

                ComputerDifficulty.MEDIUM
        );

        Move bestMove = coach.chooseMove(gameState);

        if (bestMove == null) {
            clearHint();
            return;
        }

        showHint(bestMove);
    }

    /**
     * Displays a specific move as a hint.
     */
    public void showHint(Move move) {

        if (move == null) {
            clearHint();
            return;
        }

        ui.setHintMove(move);

        ui.setHintFromRow(move.getFromRow());
        ui.setHintFromCol(move.getFromCol());

        ui.setHintToRow(move.getToRow());
        ui.setHintToCol(move.getToCol());

        board.repaint();
    }

    /**
     * Clears the current hint.
     */
    public void clearHint() {

        ui.clearHint();

        board.repaint();
    }

    /**
     * Returns the current hint move.
     */
    public Move getHintMove() {
        return ui.getHintMove();
    }

    /**
     * Returns whether a hint is currently displayed.
     */
    public boolean hasHint() {
        return ui.getHintMove() != null;
    }

    /**
     * Returns the game state.
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Returns the UI state.
     */
    public BoardUIState getUIState() {
        return ui;
    }
}

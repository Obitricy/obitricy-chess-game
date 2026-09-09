package chess.board;

import chess.game.Move;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BoardAnimationController {

    private final ChessBoard board;
    private final GameState gameState;
    private final BoardUIState ui;
    private final BoardComputerMoveHandler computerMoveHandler;

    private Runnable onMoveCallback;

    private final Timer timer;

    public BoardAnimationController(
            ChessBoard board,
            GameState gameState,
            BoardUIState ui,
            BoardComputerMoveHandler computerMoveHandler) {

        this.board = board;
        this.gameState = gameState;
        this.ui = ui;
        this.computerMoveHandler = computerMoveHandler;

        timer = new Timer(16, e -> updateAnimations());
    }

    private void updateAnimations() {

        List<AnimatedPiece> finished = new ArrayList<>();

        for (AnimatedPiece ap : ui.getAnimations()) {

            ap.update();

            if (ap.isDone()) {
                finished.add(ap);
            }
        }

        ui.getAnimations().removeAll(finished);

        board.repaint();

        if (!finished.isEmpty()) {

            if (onMoveCallback != null) {
                onMoveCallback.run();
            }

            if (gameState.isVsComputer()
                    && gameState.getCurrentPlayer()
                    == gameState.getBlackPlayer()) {

                SwingUtilities.invokeLater(
                        computerMoveHandler::makeComputerMove
                );
            }
        }
    }

    public void animateMove(Move move, BoardLayout layout) {
        ui.getAnimations().add(new AnimatedPiece(move, layout));
    }

    public void setOnMoveCallback(Runnable callback) {
        this.onMoveCallback = callback;
    }

    public void start() {
        timer.start();
    }


    public Timer getTimer() {
        return timer;
    }
}
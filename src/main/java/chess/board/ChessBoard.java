package chess.board;

import chess.ai.ComputerDifficulty;
import chess.game.GameMode;
import chess.game.Move;
import chess.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ChessBoard extends JPanel {

    // =========================================================
    // RESPONSIVE BOARD
    // =========================================================

    private final BoardLayout layout = new BoardLayout();

    private BoardRenderer renderer;
    private BoardMoveHandler moveHandler;
    private BoardPuzzleHandler puzzleHandler;
    private BoardGameFlow gameFlow;
    private BoardComputerMoveHandler computerMoveHandler;
    private BoardHintManager hintManager;
    private BoardMouseHandler mouseHandler;
    private BoardInputHandler inputHandler;
    private BoardAnimationController animationController;
    private BoardDialogs dialogs;

    // =========================================================
    // CALLBACKS
    // =========================================================

    private Runnable onMoveCallback;
    private Runnable onGameOver;
    private Runnable onPuzzleSolved;

    private Consumer<Move> onMoveMade;
    private Consumer<Move> onOnlineMove;

    // =========================================================
    // GAME
    // =========================================================

    private final GameState gameState;

    private final BoardUIState ui =
            new BoardUIState();

    // =========================================================
    // LAST MOVE
    // =========================================================

    private Move lastMove;

    public Move getLastMove() {
        return lastMove;
    }

    public void setLastMove(Move move) {
        lastMove = move;
        repaint();
    }

    public void clearLastMoveHighlight() {
        lastMove = null;
        repaint();
    }

    // =========================================================
    // TIMER
    // =========================================================

    private Timer timer;

    public Timer getTimer() {
        return timer;
    }

    // =========================================================
    // BOARD VIEW
    // =========================================================

    /*
     * TWO_D  = normal flat board
     * THREE_D = perspective / 3D board
     *
     * Both views use the SAME GameState.
     */

    private BoardView boardView = BoardView.THREE_D;

    public BoardView getBoardView() {
        return boardView;
    }

    public void setBoardView(BoardView boardView) {

        if (boardView == null) {
            return;
        }

        if (this.boardView == boardView) {
            return;
        }

        this.boardView = boardView;

        /*
         * The renderer uses the same GameState,
         * so changing the view does NOT change
         * any chess data.
         */
        revalidate();
        repaint();
    }

    public void toggleBoardView() {

        if (boardView == BoardView.TWO_D) {
            boardView = BoardView.THREE_D;
        } else {
            boardView = BoardView.TWO_D;
        }

        revalidate();
        repaint();
    }

    // =========================================================
    // GETTERS
    // =========================================================

    public GameState getGameState() {
        return gameState;
    }

    public BoardLayout getBoardLayout() {
        return layout;
    }

    public BoardUIState getBoardUIState() {
        return ui;
    }

    public BoardHintManager getHintManager() {
        return hintManager;
    }

    public BoardMoveHandler getMoveHandler() {
        return moveHandler;
    }

    public BoardDialogs getDialogs() {
        return dialogs;
    }

    public BoardGameFlow getGameFlow() {
        return gameFlow;
    }

    /*
     * BoardRenderer owns the 3D projection.
     *
     * Returning the same projection here is important because
     * mouse/input code must use exactly the same geometry
     * that the renderer uses.
     */
    public Board3DProjection get3DProjection() {

        if (renderer == null) {
            return null;
        }

        return renderer.getProjection();
    }

    public String getGameOverTitle() {
        return ui.getGameOverTitle();
    }

    public String getGameOverMessage() {
        return ui.getGameOverMessage();
    }

    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    public ChessBoard(
            GameMode mode,
            String white,
            String black,
            ComputerDifficulty difficulty) {

        gameState =
                new GameState(
                        white,
                        black,
                        mode,
                        difficulty
                );

        initializeBoard();
    }

    public ChessBoard(GameState gameState) {

        if (gameState == null) {
            throw new IllegalArgumentException(
                    "GameState cannot be null"
            );
        }

        this.gameState = gameState;

        initializeBoard();
    }

    // =========================================================
    // INITIALIZATION
    // =========================================================

    private void initializeBoard() {

        // -----------------------------------------------------
        // PUZZLE
        // -----------------------------------------------------

        puzzleHandler =
                new BoardPuzzleHandler(
                        this,
                        gameState,
                        ui
                );

        // -----------------------------------------------------
        // MOVE HANDLER
        // -----------------------------------------------------

        moveHandler =
                new BoardMoveHandler(
                        this,
                        gameState,
                        ui,
                        puzzleHandler
                );

        // -----------------------------------------------------
        // GAME FLOW
        // -----------------------------------------------------

        gameFlow =
                new BoardGameFlow(
                        this,
                        gameState,
                        ui,
                        layout
                );

        // -----------------------------------------------------
        // RENDERER
        // -----------------------------------------------------

        renderer =
                new BoardRenderer(
                        this,
                        layout,
                        ui
                );

        // -----------------------------------------------------
        // COMPUTER PLAYER
        // -----------------------------------------------------

        computerMoveHandler =
                new BoardComputerMoveHandler(
                        this,
                        gameState,
                        moveHandler
                );

        // -----------------------------------------------------
        // ANIMATION
        // -----------------------------------------------------

        animationController =
                new BoardAnimationController(
                        this,
                        gameState,
                        ui,
                        computerMoveHandler
                );

        // -----------------------------------------------------
        // INPUT
        // -----------------------------------------------------

        inputHandler =
                new BoardInputHandler(
                        this,
                        gameState,
                        ui,
                        moveHandler,
                        puzzleHandler
                );

        // -----------------------------------------------------
        // MOUSE
        // -----------------------------------------------------

        mouseHandler =
                new BoardMouseHandler(
                        this,
                        layout,
                        ui,
                        inputHandler
                );

        // -----------------------------------------------------
        // HINTS
        // -----------------------------------------------------

        hintManager =
                new BoardHintManager(
                        this,
                        gameState,
                        ui
                );

        // -----------------------------------------------------
        // DIALOGS
        // -----------------------------------------------------

        dialogs =
                new BoardDialogs(this);

        // -----------------------------------------------------
        // PANEL
        // -----------------------------------------------------

        setBackground(new Color(0, 0, 0, 0));

        setPreferredSize(
                new Dimension(
                        900,
                        700
                )
        );

        /*
         * Make sure Swing does not paint the old board
         * underneath the renderer.
         */
        setOpaque(true);

        // -----------------------------------------------------
        // TIMER
        // -----------------------------------------------------

        timer =
                animationController.getTimer();

        animationController.start();
    }

    // =========================================================
    // PAINT
    // =========================================================

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        /*
         * Calculate the current board geometry before
         * drawing anything.
         */
        layout.update(
                getWidth(),
                getHeight()
        );

        /*
         * BoardRenderer decides whether the board is
         * rendered in 2D or 3D.
         */
        if (renderer != null) {
            renderer.paint(g);
        }
    }

    // =========================================================
    // MOVE ANIMATION
    // =========================================================

    public void animateMove(Move move) {

        if (move == null) {
            return;
        }

        /*
         * Remember the move so BoardRenderer can draw
         * the last-move highlight.
         */
        setLastMove(move);

        if (animationController != null) {

            animationController.animateMove(
                    move,
                    layout
            );
        }
    }

    // =========================================================
    // REFRESH APPEARANCE
    // =========================================================

    public void refreshAppearance() {

        gameState.reloadPieceImages();

        repaint();
    }

    // =========================================================
    // MOVE CALLBACK
    // =========================================================

    public void setOnMoveCallback(
            Runnable callback) {

        onMoveCallback = callback;

        if (animationController != null) {

            animationController.setOnMoveCallback(
                    callback
            );
        }
    }

    // =========================================================
    // UNDO / REDO
    // =========================================================

    public void undoLastMove() {

        if (gameFlow != null) {
            gameFlow.undoLastMove();
        }

        repaint();
    }

    public void redoLastMove() {

        if (gameFlow != null) {
            gameFlow.redoLastMove();
        }

        repaint();
    }

    // =========================================================
    // BOARD RESET
    // =========================================================

    public void resetBoard() {

        if (gameFlow != null) {
            gameFlow.resetBoard();
        }

        clearLastMoveHighlight();

        repaint();
    }

    // =========================================================
    // PUZZLES
    // =========================================================

    public void nextPuzzle() {

        if (puzzleHandler != null) {
            puzzleHandler.nextPuzzle();
        }

        clearLastMoveHighlight();

        repaint();
    }

    public void setOnPuzzleSolved(
            Runnable callback) {

        onPuzzleSolved = callback;
    }

    public void firePuzzleSolved() {

        if (onPuzzleSolved != null) {
            onPuzzleSolved.run();
        }
    }

    public void enableNextPuzzleButton() {
        firePuzzleSolved();
    }

    public void resetPuzzleSolvedState() {

        if (puzzleHandler != null) {
            puzzleHandler.resetSolvedState();
        }
    }

    // =========================================================
    // GAME OVER
    // =========================================================

    public void setOnGameOver(
            Runnable callback) {

        onGameOver = callback;
    }

    public void fireGameOver() {

        if (onGameOver != null) {
            onGameOver.run();
        }
    }

    // =========================================================
    // ANIMATION CONTROL
    // =========================================================

    public void stopAnimation() {

        if (timer != null) {
            timer.stop();
        }
    }

    // =========================================================
    // TRAINING HINT
    // =========================================================

    public void showHint() {

        if (gameState.getGameMode()
                != GameMode.TRAINING) {

            return;
        }

        if (hintManager != null) {
            hintManager.showHint();
        }
    }

    // =========================================================
    // ONLINE GAME
    // =========================================================

    private boolean onlineGame;
    private boolean playerIsWhite;

    public void setOnlineGame(
            boolean onlineGame,
            boolean playerIsWhite) {

        this.onlineGame = onlineGame;
        this.playerIsWhite = playerIsWhite;
    }

    public boolean isOnlineGame() {
        return onlineGame;
    }

    public boolean isPlayerIsWhite() {
        return playerIsWhite;
    }

    // =========================================================
    // LOCAL MOVE CALLBACK
    // =========================================================

    public void setOnMoveMade(
            Consumer<Move> callback) {

        onMoveMade = callback;
    }

    public void fireMoveMade(Move move) {

        if (onMoveMade != null
                && move != null) {

            onMoveMade.accept(move);
        }
    }

    // =========================================================
    // ONLINE MOVE CALLBACK
    // =========================================================

    public void setOnOnlineMove(
            Consumer<Move> callback) {

        onOnlineMove = callback;
    }

    public void fireOnlineMove(Move move) {

        if (onOnlineMove != null
                && move != null) {

            onOnlineMove.accept(move);
        }
    }

    // =========================================================
    // APPLY REMOTE MOVE
    // =========================================================

    public void applyRemoteMove(Move move) {

        if (move != null) {
            applyRemoteMove(move, move.getPiece().isWhite());
        }
    }

    public void applyRemoteMove(Move move, boolean moverIsWhite) {

        if (move == null) {
            return;
        }

        /*
         * Remote moves do not use local selection.
         */
        ui.clearSelection();
        ui.clearLegalMoves();

        boolean capture =
                move.getCapturedPiece() != null
                        || gameState.isEnPassantMove(move);

        /*
         * Apply the move directly to GameState.
         */
        if (!gameState.makeRemoteMove(move, moverIsWhite)) {

            System.err.println(
                    "REMOTE MOVE REJECTED: "
                            + move
            );

            return;
        }

        /*
         * Last move highlight.
         */
        setLastMove(move);

        /*
         * Sound.
         */
        if (capture) {

            chess.audio.SoundManager
                    .playCapture();

        } else {

            chess.audio.SoundManager
                    .playMove();
        }

        /*
         * Animate the opponent's move.
         */
        if (animationController != null) {

            animationController.animateMove(
                    move,
                    layout
            );
        }

        /*
         * Clear training hint.
         */
        if (hintManager != null) {
            hintManager.clearHint();
        }

        repaint();

        /*
         * Check for checkmate/stalemate/etc.
         */
        if (gameFlow != null) {
            gameFlow.checkGameFinished();
        }
    }

    // =========================================================
    // MAKE REMOTE MOVE
    // =========================================================

    public boolean makeRemoteMove(
            int fromRow,
            int fromCol,
            int toRow,
            int toCol,
            boolean moverIsWhite) {

        Piece piece =
                gameState.getPiece(
                        fromRow,
                        fromCol
                );

        if (piece == null) {

            System.err.println(
                    "REMOTE MOVE ERROR: "
                            + "No piece at "
                            + fromRow
                            + ","
                            + fromCol
            );

            return false;
        }

        /*
         * Basic protection against a malformed network
         * message claiming that the wrong colour moved.
         */
        if (piece.isWhite() != moverIsWhite) {

            System.err.println(
                    "REMOTE MOVE ERROR: "
                            + "Wrong piece colour."
            );

            return false;
        }

        Piece capturedPiece =
                gameState.getPiece(
                        toRow,
                        toCol
                );

        Move move =
                new Move(
                        piece,
                        fromRow,
                        fromCol,
                        toRow,
                        toCol,
                        capturedPiece
                );

        boolean success =
                gameState.makeRemoteMove(
                        move,
                        moverIsWhite
                );

        if (!success) {

            System.err.println(
                    "REMOTE MOVE REJECTED: "
                            + move
            );

            return false;
        }

        /*
         * Last move highlight.
         */
        setLastMove(move);

        /*
         * Animate.
         */
        if (animationController != null) {

            animationController.animateMove(
                    move,
                    layout
            );
        }

        /*
         * Clear hints.
         */
        if (hintManager != null) {
            hintManager.clearHint();
        }

        repaint();

        /*
         * Check game status.
         */
        if (gameFlow != null) {
            gameFlow.checkGameFinished();
        }

        return true;
    }
}
package chess.board;

import chess.player.ComputerPlayer;
import chess.game.Move;
import chess.rules.MoveValidator;
import chess.pieces.Piece;

public class BoardComputerMoveHandler {

    private final ChessBoard board;
    private final GameState gameState;
    private final BoardMoveHandler moveHandler;

    public BoardComputerMoveHandler(
            ChessBoard board,
            GameState gameState,
            BoardMoveHandler moveHandler) {

        this.board = board;
        this.gameState = gameState;
        this.moveHandler = moveHandler;
    }

    // ---------------- AI MOVE ----------------
    public void makeComputerMove() {

        if (!gameState.isVsComputer()) {
            return;
        }

        if (!(gameState.getCurrentPlayer()
                instanceof ComputerPlayer computer)) {
            return;
        }

        Move move = computer.chooseMove(gameState);

        if (move == null) {
            return;
        }

        System.out.println("AI chose "
                + move.getPiece().getClass().getSimpleName()
                + " from (" + move.getFromRow() + "," + move.getFromCol() + ")"
                + " to (" + move.getToRow() + "," + move.getToCol() + ")");

        Piece boardPiece = gameState.getPiece(
                move.getFromRow(),
                move.getFromCol()
        );

        System.out.println("Board contains: "
                + (boardPiece == null ? "null"
                : boardPiece.getClass().getSimpleName()));

        System.out.println("Move piece hash = "
                + System.identityHashCode(move.getPiece()));

        if (boardPiece != null) {
            System.out.println("Board piece hash = "
                    + System.identityHashCode(boardPiece));
        }

        if (!MoveValidator.isLegalMove(move, gameState)) {

            System.out.println("AI produced an illegal move!");

            System.out.printf(
                    "%s from (%d,%d) to (%d,%d)%n",
                    move.getPiece().getClass().getSimpleName(),
                    move.getFromRow(),
                    move.getFromCol(),
                    move.getToRow(),
                    move.getToCol()
            );

            return;
        }

        moveHandler.finishMove(move);

    }
}
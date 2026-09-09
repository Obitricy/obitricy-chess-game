package chess.rules;

import chess.board.GameState;
import chess.board.Tile;
import chess.game.Move;
import chess.pieces.Piece;
import chess.pieces.Pawn;
import chess.pieces.Pawn;
import java.util.ArrayList;
import java.util.List;
import chess.pieces.King;


public class MoveValidator {

    public static boolean isLegalMove(Move move, GameState gameState) {
        if (move == null)
            return false;

        Piece piece = move.getPiece();
        if (piece == null)
            return false;

        Piece boardPiece = gameState.getPiece(
                move.getFromRow(),
                move.getFromCol()
        );

        if (boardPiece != piece) {

            return false;
        }

        int toRow = move.getToRow();
        int toCol = move.getToCol();

        // bounds check
        if (toRow < 0 || toCol < 0 || toRow >= 8 || toCol >= 8)
            return false;

        // prevent capturing own piece
        Piece target = gameState.getPiece(toRow, toCol);
        if (target != null && target.isWhite() == piece.isWhite())
            return false;

        // prevent capturing a king
        if (target instanceof King)
            return false;


        Tile[][] board = gameState.getBoard();

// Special castling logic
        if (piece instanceof King
                && Math.abs(move.getToCol() - move.getFromCol()) == 2) {

            return CastlingRules.isLegalCastle(move, gameState);
        }



// En passant: the destination square is empty, so Pawn.canMove()
        // alone cannot recognize this special capture.
        if (piece instanceof Pawn && gameState.isEnPassantMove(move)) {
            return !CheckDetector.wouldLeaveKingInCheck(move, gameState);
        }

        // Normal move validation
        if (piece instanceof Pawn && gameState.isEnPassantMove(move)) {
            return !CheckDetector.wouldLeaveKingInCheck(move, gameState);
        }

        // Normal move validation
        boolean canMove = piece.canMove(toRow, toCol, board);

        if (!canMove) {
            return false;
        }

// Prevent moves that leave your own king in check
        return !CheckDetector.wouldLeaveKingInCheck(move, gameState);

    }

    public static List<Move> getValidMoves(Piece piece, int row, int col, GameState gameState) {

        List<Move> validMoves = new ArrayList<>();

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {


                Move move = new Move(
                        piece,
                        row,
                        col,
                        r,
                        c,
                        gameState.getPiece(r, c)
                );

                if (isLegalMove(move, gameState)) {
                    validMoves.add(move);
                }
            }
        }

        return validMoves;
    }
}
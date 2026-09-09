package chess.game;

import chess.pieces.*;

public class MoveNotation {

    public static String toNotation(Move move) {

        Piece piece = move.getPiece();

        String pieceLetter = "";

        if (piece instanceof King) pieceLetter = "K";
        else if (piece instanceof Queen) pieceLetter = "Q";
        else if (piece instanceof Rook) pieceLetter = "R";
        else if (piece instanceof Bishop) pieceLetter = "B";
        else if (piece instanceof Knight) pieceLetter = "N";

        boolean capture = move.getCapturedPiece() != null;

        String fromFile = String.valueOf((char) ('a' + move.getFromCol()));


        String toFile = String.valueOf((char) ('a' + move.getToCol()));
        String toRank = String.valueOf(8 - move.getToRow());

        // Pawns
        if (piece instanceof Pawn) {
            if (capture) {
                return fromFile + "x" + toFile + toRank;
            }
            return toFile + toRank;
        }

        return pieceLetter +
                (capture ? "x" : "") +
                toFile + toRank;
    }
}
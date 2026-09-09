package chess.puzzle;

import chess.board.GameState;
import chess.pieces.*;

public class FenLoader {

    public static void load(GameState gameState, String fen) {

        // Split the FEN
        String[] parts = fen.split(" ");

        String board = parts[0];

        String sideToMove = parts[1];

        // Clear board
        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                gameState.setPiece(row, col, null);

            }
        }

        String[] ranks = board.split("/");

        for (int row = 0; row < 8; row++) {

            int col = 0;

            for (char c : ranks[row].toCharArray()) {

                if (Character.isDigit(c)) {

                    col += c - '0';

                    continue;

                }

                boolean white = Character.isUpperCase(c);

                Piece piece = switch (Character.toLowerCase(c)) {

                    case 'k' -> new King(white, row, col);

                    case 'q' -> new Queen(white, row, col);

                    case 'r' -> new Rook(white, row, col);

                    case 'b' -> new Bishop(white, row, col);

                    case 'n' -> new Knight(white, row, col);

                    case 'p' -> new Pawn(white, row, col);

                    default -> null;

                };

                gameState.setPiece(row, col, piece);

                col++;

            }
        }

        // Set turn
        if (sideToMove.equals("w")) {

            gameState.setCurrentPlayerWhite();

        } else {

            gameState.setCurrentPlayerBlack();

        }
    }

}
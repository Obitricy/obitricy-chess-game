package chess.pieces;

import chess.board.GameState;

public final class PieceFactory {

    private PieceFactory() {
        // Prevent instantiation
    }

    public static Piece createPiece(
            String type,
            boolean white,
            int row,
            int col) {

        return switch (type.toUpperCase()) {

            case "PAWN" -> new Pawn(white, row, col);

            case "ROOK" -> new Rook(white, row, col);

            case "KNIGHT" -> new Knight(white, row, col);

            case "BISHOP" -> new Bishop(white, row, col);

            case "QUEEN" -> new Queen(white, row, col);

            case "KING" -> new King(white, row, col);

            default ->
                    throw new IllegalArgumentException(
                            "Unknown piece: " + type);
        };
    }

    /**
     * Creates the standard chess starting position.
     */
    public static void createInitialPosition(GameState gameState) {

        // Black pawns
        for (int col = 0; col < 8; col++) {
            gameState.setPiece(1, col, new Pawn(false, 1, col));
        }

        // White pawns
        for (int col = 0; col < 8; col++) {
            gameState.setPiece(6, col, new Pawn(true, 6, col));
        }

        // Black pieces
        gameState.setPiece(0, 0, new Rook(false, 0, 0));
        gameState.setPiece(0, 1, new Knight(false, 0, 1));
        gameState.setPiece(0, 2, new Bishop(false, 0, 2));
        gameState.setPiece(0, 3, new Queen(false, 0, 3));
        gameState.setPiece(0, 4, new King(false, 0, 4));
        gameState.setPiece(0, 5, new Bishop(false, 0, 5));
        gameState.setPiece(0, 6, new Knight(false, 0, 6));
        gameState.setPiece(0, 7, new Rook(false, 0, 7));

        // White pieces
        gameState.setPiece(7, 0, new Rook(true, 7, 0));
        gameState.setPiece(7, 1, new Knight(true, 7, 1));
        gameState.setPiece(7, 2, new Bishop(true, 7, 2));
        gameState.setPiece(7, 3, new Queen(true, 7, 3));
        gameState.setPiece(7, 4, new King(true, 7, 4));
        gameState.setPiece(7, 5, new Bishop(true, 7, 5));
        gameState.setPiece(7, 6, new Knight(true, 7, 6));
        gameState.setPiece(7, 7, new Rook(true, 7, 7));
    }
}
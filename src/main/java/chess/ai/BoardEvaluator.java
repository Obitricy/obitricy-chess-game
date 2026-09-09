package chess.ai;

import chess.board.GameState;
import chess.pieces.*;
import chess.rules.CheckMateDetector;
import chess.rules.CheckDetector;

import java.util.List;

public class BoardEvaluator {

    // ============================================================
    // PIECE VALUES
    // ============================================================

    private static final int PAWN_VALUE = 100;
    private static final int KNIGHT_VALUE = 320;
    private static final int BISHOP_VALUE = 330;
    private static final int ROOK_VALUE = 500;
    private static final int QUEEN_VALUE = 900;
    private static final int KING_VALUE = 20000;

    // ============================================================
    // POSITIONAL WEIGHTS
    // ============================================================

    private static final int MOBILITY_WEIGHT = 2;
    private static final int KING_SAFETY_WEIGHT = 12;
    private static final int DEVELOPMENT_WEIGHT = 8;
    private static final int PAWN_STRUCTURE_WEIGHT = 8;
    private static final int CENTER_WEIGHT = 6;

    /**
     * Positive score = White is better.
     * Negative score = Black is better.
     */
    public int evaluate(GameState gameState) {

        // --------------------------------------------------------
        // Checkmate
        // --------------------------------------------------------

        if (CheckMateDetector.isCheckMate(true, gameState)) {
            return -1000000;
        }

        if (CheckMateDetector.isCheckMate(false, gameState)) {
            return 1000000;
        }

        // --------------------------------------------------------
        // Stalemate / insufficient material
        // --------------------------------------------------------

        if (gameState.isStalemate()) {
            return 0;
        }

        if (gameState.isInsufficientMaterial()) {
            return 0;
        }

        int score = 0;

        // --------------------------------------------------------
        // 1. Material
        // --------------------------------------------------------

        score += evaluateMaterial(gameState);

        // --------------------------------------------------------
        // 2. Mobility
        // --------------------------------------------------------

        score += evaluateMobility(gameState);

        // --------------------------------------------------------
        // 3. Piece-square positioning
        // --------------------------------------------------------

        score += evaluatePiecePosition(gameState);

        // --------------------------------------------------------
        // 4. Center control
        // --------------------------------------------------------

        score += evaluateCenterControl(gameState);

        // --------------------------------------------------------
        // 5. Development
        // --------------------------------------------------------

        score += evaluateDevelopment(gameState);

        // --------------------------------------------------------
        // 6. Pawn structure
        // --------------------------------------------------------

        score += evaluatePawnStructure(gameState);

        // --------------------------------------------------------
        // 7. King safety
        // --------------------------------------------------------

        score += evaluateKingSafety(gameState);

        return score;
    }

    // ============================================================
    // MATERIAL
    // ============================================================

    private int evaluateMaterial(GameState gameState) {

        int score = 0;

        for (Piece piece : gameState.getPieces(true)) {
            score += getPieceValue(piece);
        }

        for (Piece piece : gameState.getPieces(false)) {
            score -= getPieceValue(piece);
        }

        return score;
    }

    // ============================================================
    // MOBILITY
    // ============================================================

    private int evaluateMobility(GameState gameState) {

        int whiteMoves =
                gameState.getAllLegalMoves(true).size();

        int blackMoves =
                gameState.getAllLegalMoves(false).size();

        return (whiteMoves - blackMoves)
                * MOBILITY_WEIGHT;
    }

    // ============================================================
    // PIECE POSITION
    // ============================================================

    private int evaluatePiecePosition(GameState gameState) {

        int score = 0;

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece =
                        gameState.getPiece(row, col);

                if (piece == null) {
                    continue;
                }

                int positionalValue =
                        getPositionalValue(
                                piece,
                                row,
                                col
                        );

                if (piece.isWhite()) {
                    score += positionalValue;
                } else {
                    score -= positionalValue;
                }
            }
        }

        return score;
    }

    private int getPositionalValue(
            Piece piece,
            int row,
            int col) {

        int[][] table;

        if (piece instanceof Pawn) {
            table = PAWN_TABLE;
        } else if (piece instanceof Knight) {
            table = KNIGHT_TABLE;
        } else if (piece instanceof Bishop) {
            table = BISHOP_TABLE;
        } else if (piece instanceof Rook) {
            table = ROOK_TABLE;
        } else if (piece instanceof Queen) {
            table = QUEEN_TABLE;
        } else if (piece instanceof King) {
            table = KING_TABLE;
        } else {
            return 0;
        }

        /*
         * White moves from row 6 toward row 0.
         * Black moves from row 1 toward row 7.
         */
        int tableRow =
                piece.isWhite()
                        ? row
                        : 7 - row;

        return table[tableRow][col];
    }

    // ============================================================
    // CENTER CONTROL
    // ============================================================

    private int evaluateCenterControl(GameState gameState) {

        int score = 0;

        int[][] centerSquares = {
                {3, 3},
                {3, 4},
                {4, 3},
                {4, 4}
        };

        for (int[] square : centerSquares) {

            int row = square[0];
            int col = square[1];

            Piece piece =
                    gameState.getPiece(row, col);

            if (piece != null) {

                if (piece.isWhite()) {
                    score += CENTER_WEIGHT;
                } else {
                    score -= CENTER_WEIGHT;
                }
            }
        }

        return score;
    }

    // ============================================================
    // DEVELOPMENT
    // ============================================================

    private int evaluateDevelopment(GameState gameState) {

        int score = 0;

        /*
         * Reward developed knights and bishops.
         */

        Piece whiteKnight1 =
                gameState.getPiece(7, 1);

        Piece whiteKnight2 =
                gameState.getPiece(7, 6);

        Piece whiteBishop1 =
                gameState.getPiece(7, 2);

        Piece whiteBishop2 =
                gameState.getPiece(7, 5);

        Piece blackKnight1 =
                gameState.getPiece(0, 1);

        Piece blackKnight2 =
                gameState.getPiece(0, 6);

        Piece blackBishop1 =
                gameState.getPiece(0, 2);

        Piece blackBishop2 =
                gameState.getPiece(0, 5);

        if (!(whiteKnight1 instanceof Knight)) {
            score += DEVELOPMENT_WEIGHT;
        }

        if (!(whiteKnight2 instanceof Knight)) {
            score += DEVELOPMENT_WEIGHT;
        }

        if (!(whiteBishop1 instanceof Bishop)) {
            score += DEVELOPMENT_WEIGHT;
        }

        if (!(whiteBishop2 instanceof Bishop)) {
            score += DEVELOPMENT_WEIGHT;
        }

        if (!(blackKnight1 instanceof Knight)) {
            score -= DEVELOPMENT_WEIGHT;
        }

        if (!(blackKnight2 instanceof Knight)) {
            score -= DEVELOPMENT_WEIGHT;
        }

        if (!(blackBishop1 instanceof Bishop)) {
            score -= DEVELOPMENT_WEIGHT;
        }

        if (!(blackBishop2 instanceof Bishop)) {
            score -= DEVELOPMENT_WEIGHT;
        }

        return score;
    }

    // ============================================================
    // PAWN STRUCTURE
    // ============================================================

    private int evaluatePawnStructure(GameState gameState) {

        int score = 0;

        score += evaluatePawnStructureForSide(
                gameState,
                true
        );

        score -= evaluatePawnStructureForSide(
                gameState,
                false
        );

        return score;
    }

    private int evaluatePawnStructureForSide(
            GameState gameState,
            boolean white) {

        int score = 0;

        int[] pawnCountByFile = new int[8];

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece =
                        gameState.getPiece(row, col);

                if (piece instanceof Pawn
                        && piece.isWhite() == white) {

                    pawnCountByFile[col]++;
                }
            }
        }

        for (int file = 0; file < 8; file++) {

            /*
             * Doubled pawns are usually a weakness.
             */
            if (pawnCountByFile[file] > 1) {

                score -=
                        (pawnCountByFile[file] - 1)
                                * PAWN_STRUCTURE_WEIGHT;
            }

            /*
             * Isolated pawn.
             */
            if (pawnCountByFile[file] > 0) {

                boolean left =
                        file > 0
                                && pawnCountByFile[file - 1] > 0;

                boolean right =
                        file < 7
                                && pawnCountByFile[file + 1] > 0;

                if (!left && !right) {
                    score -= PAWN_STRUCTURE_WEIGHT;
                }
            }
        }

        return score;
    }

    // ============================================================
    // KING SAFETY
    // ============================================================

    private int evaluateKingSafety(GameState gameState) {

        int score = 0;

        score +=
                evaluateKingSafetyForSide(
                        gameState,
                        true
                );

        score -=
                evaluateKingSafetyForSide(
                        gameState,
                        false
                );

        return score;
    }

    private int evaluateKingSafetyForSide(
            GameState gameState,
            boolean white) {

        int kingRow = -1;
        int kingCol = -1;

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece =
                        gameState.getPiece(row, col);

                if (piece instanceof King
                        && piece.isWhite() == white) {

                    kingRow = row;
                    kingCol = col;
                    break;
                }
            }

            if (kingRow != -1) {
                break;
            }
        }

        if (kingRow == -1) {
            return 0;
        }

        int score = 0;

        /*
         * Being in check is a serious weakness.
         */
        if (CheckDetector.isKingInCheck(
                white,
                gameState)) {

            score -= 50;
        }

        /*
         * Reward castling / moving the king away
         * from its vulnerable starting position.
         */
        if (white) {

            if (kingRow == 7
                    && (kingCol == 6 || kingCol == 2)) {

                score += 35;
            }

        } else {

            if (kingRow == 0
                    && (kingCol == 6 || kingCol == 2)) {

                score += 35;
            }
        }

        /*
         * Reward pawns protecting the king.
         */
        int pawnRow =
                white
                        ? kingRow - 1
                        : kingRow + 1;

        if (pawnRow >= 0 && pawnRow < 8) {

            for (int dc = -1; dc <= 1; dc++) {

                int pawnCol = kingCol + dc;

                if (pawnCol < 0 || pawnCol >= 8) {
                    continue;
                }

                Piece pawn =
                        gameState.getPiece(
                                pawnRow,
                                pawnCol
                        );

                if (pawn instanceof Pawn
                        && pawn.isWhite() == white) {

                    score += 8;
                }
            }
        }

        return score;
    }

    // ============================================================
    // PIECE VALUES
    // ============================================================

    private int getPieceValue(Piece piece) {

        if (piece instanceof Pawn) {
            return PAWN_VALUE;
        }

        if (piece instanceof Knight) {
            return KNIGHT_VALUE;
        }

        if (piece instanceof Bishop) {
            return BISHOP_VALUE;
        }

        if (piece instanceof Rook) {
            return ROOK_VALUE;
        }

        if (piece instanceof Queen) {
            return QUEEN_VALUE;
        }

        if (piece instanceof King) {
            return KING_VALUE;
        }

        return 0;
    }

    // ============================================================
    // PIECE-SQUARE TABLES
    // ============================================================

    private static final int[][] PAWN_TABLE = {

            { 0,  0,  0,  0,  0,  0,  0,  0 },

            {50, 50, 50, 50, 50, 50, 50, 50},

            {10, 10, 20, 30, 30, 20, 10, 10},

            { 5,  5, 10, 25, 25, 10,  5,  5},

            { 0,  0,  0, 20, 20,  0,  0,  0},

            { 5, -5,-10,  0,  0,-10, -5,  5},

            { 5, 10, 10,-20,-20, 10, 10,  5},

            { 0,  0,  0,  0,  0,  0,  0,  0}
    };

    private static final int[][] KNIGHT_TABLE = {

            {-50,-40,-30,-30,-30,-30,-40,-50},

            {-40,-20,  0,  5,  5,  0,-20,-40},

            {-30,  5, 10, 15, 15, 10,  5,-30},

            {-30,  0, 15, 20, 20, 15,  0,-30},

            {-30,  5, 15, 20, 20, 15,  5,-30},

            {-30,  0, 10, 15, 15, 10,  0,-30},

            {-40,-20,  0,  0,  0,  0,-20,-40},

            {-50,-40,-30,-30,-30,-30,-40,-50}
    };

    private static final int[][] BISHOP_TABLE = {

            {-20,-10,-10,-10,-10,-10,-10,-20},

            {-10,  5,  0,  0,  0,  0,  5,-10},

            {-10, 10, 10, 10, 10, 10, 10,-10},

            {-10,  0, 10, 10, 10, 10,  0,-10},

            {-10,  5,  5, 10, 10,  5,  5,-10},

            {-10,  0,  5, 10, 10,  5,  0,-10},

            {-10,  0,  0,  0,  0,  0,  0,-10},

            {-20,-10,-10,-10,-10,-10,-10,-20}
    };

    private static final int[][] ROOK_TABLE = {

            { 0,  0,  0,  5,  5,  0,  0,  0},

            {-5,  0,  0,  0,  0,  0,  0, -5},

            {-5,  0,  0,  0,  0,  0,  0, -5},

            {-5,  0,  0,  0,  0,  0, 0, -5},

            {-5,  0,  0,  0,  0,  0, 0, -5},

            {-5,  0,  0,  0,  0,  0, 0, -5},

            { 5, 10, 10, 10, 10, 10, 10,  5},

            { 0,  0,  0,  0,  0,  0,  0,  0}
    };

    private static final int[][] QUEEN_TABLE = {

            {-20,-10,-10, -5, -5,-10,-10,-20},

            {-10,  0,  0,  0,  0,  0,  0,-10},

            {-10,  0,  5,  5,  5,  5,  0,-10},

            { -5,  0,  5,  5,  5,  5,  0, -5},

            {  0,  0,  5,  5,  5,  5,  0, -5},

            {-10,  5,  5,  5,  5,  5,  5,-10},

            {-10,  0,  5,  0,  0,  0,  0,-10},

            {-20,-10,-10, -5, -5,-10,-10,-20}
    };

    private static final int[][] KING_TABLE = {

            {-30,-40,-40,-50,-50,-40,-40,-30},

            {-30,-40,-40,-50,-50,-40,-40,-30},

            {-30,-40,-40,-50,-50,-40,-40,-30},

            {-30,-40,-40,-50,-50,-40,-40,-30},

            {-20,-30,-30,-40,-40,-30,-30,-20},

            {-10,-20,-20,-20,-20,-20,-20,-10},

            { 20, 20,  0,  0,  0,  0, 20, 20},

            { 20, 30, 10,  0,  0, 10, 30, 20}
    };
}
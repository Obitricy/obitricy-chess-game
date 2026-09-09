package chess.ai;

import chess.board.GameState;
import chess.game.Move;
import chess.pieces.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinimaxAI implements ChessAI {

    private final BoardEvaluator evaluator = new BoardEvaluator();

    /*
     * Search depth supplied by ComputerDifficulty.
     */
    private final int searchDepth;

    /*
     * Keep both constructors.
     *
     * The no-argument constructor prevents compatibility problems
     * with ComputerPlayer if it currently uses new MinimaxAI().
     */
    public MinimaxAI() {
        this(5);
    }

    public MinimaxAI(int searchDepth) {
        this.searchDepth = Math.max(1, searchDepth);
    }

    /*
     * Transposition table.
     *
     * Stores positions already searched.
     */
    private final Map<String, TTEntry> transpositionTable =
            new HashMap<>();

    /*
     * Maximum time allowed for one computer move.
     */
    private long searchDeadline;

    private boolean timeUp;


    // ============================================================
    // THINKING TIME
    // ============================================================

    private long getTimeLimit() {

        if (searchDepth <= 1) return 200;
        if (searchDepth <= 2) return 350;
        if (searchDepth <= 3) return 600;
        if (searchDepth <= 5) return 1200;
        if (searchDepth <= 7) return 2500;

        /*
         * MASTER
         */
        return 4000;
    }


    // ============================================================
    // CHOOSE MOVE
    // ============================================================

    @Override
    public Move chooseMove(GameState gameState) {

        boolean maximizingPlayer =
                gameState.isWhiteTurn();

        List<Move> legalMoves =
                gameState.getAllLegalMoves(maximizingPlayer);

        if (legalMoves.isEmpty()) {
            return null;
        }

        /*
         * Initial move ordering.
         */
        orderMoves(legalMoves);

        /*
         * Start thinking timer.
         */
        searchDeadline =
                System.nanoTime()
                        + getTimeLimit() * 1_000_000L;

        timeUp = false;

        /*
         * Clear old positions.
         */
        transpositionTable.clear();

        /*
         * Always have a safe move available.
         */
        Move bestMove = legalMoves.get(0);

        /*
         * Iterative deepening.
         *
         * Example:
         *
         * depth 1
         * depth 2
         * depth 3
         * ...
         *
         * If the timer expires, the best result from the
         * previous completed depth is retained.
         */
        for (int depth = 1;
             depth <= searchDepth;
             depth++) {

            if (isTimeUp()) {
                break;
            }

            timeUp = false;

            RootResult result =
                    searchRoot(
                            gameState,
                            legalMoves,
                            depth,
                            maximizingPlayer
                    );

            /*
             * Do not use an incomplete search result.
             */
            if (timeUp) {
                break;
            }

            if (result.move != null) {
                bestMove = result.move;
            }

            /*
             * Put the best move first for the next iteration.
             *
             * IMPORTANT:
             * This is the corrected version of the broken lambda
             * that caused your compilation error.
             */
            if (bestMove != null) {

                final Move chosenMove = bestMove;

                legalMoves.sort(
                        Comparator.comparingInt(
                                (Move move) ->
                                        move == chosenMove
                                                ? Integer.MAX_VALUE
                                                : moveScore(move)
                        ).reversed()
                );
            }
        }

        return bestMove;
    }


    // ============================================================
    // ROOT SEARCH
    // ============================================================

    private RootResult searchRoot(
            GameState gameState,
            List<Move> legalMoves,
            int depth,
            boolean maximizingPlayer) {

        Move bestMove = null;

        int bestScore =
                maximizingPlayer
                        ? Integer.MIN_VALUE
                        : Integer.MAX_VALUE;

        int alpha = Integer.MIN_VALUE + 1;
        int beta = Integer.MAX_VALUE - 1;

        for (Move move : legalMoves) {

            if (isTimeUp()) {
                timeUp = true;
                break;
            }

            gameState.makeTemporaryMove(move);

            int score =
                    minimax(
                            gameState,
                            depth - 1,
                            alpha,
                            beta,
                            !maximizingPlayer
                    );

            gameState.undoTemporaryMove(move);

            if (timeUp) {
                break;
            }

            if (maximizingPlayer) {

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }

                alpha =
                        Math.max(alpha, bestScore);

            } else {

                if (score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }

                beta =
                        Math.min(beta, bestScore);
            }

            if (beta <= alpha) {
                break;
            }
        }

        return new RootResult(
                bestMove,
                bestScore
        );
    }


    // ============================================================
    // MINIMAX
    // ============================================================

    private int minimax(
            GameState gameState,
            int depth,
            int alpha,
            int beta,
            boolean maximizingPlayer) {

        /*
         * Stop immediately when the thinking time expires.
         */
        if (isTimeUp()) {
            timeUp = true;
            return 0;
        }

        /*
         * Static evaluation.
         */
        if (depth <= 0) {
            return evaluator.evaluate(gameState);
        }

        /*
         * Position key.
         */
        String key =
                createPositionKey(
                        gameState,
                        maximizingPlayer,
                        depth
                );

        /*
         * Transposition lookup.
         */
        TTEntry cached =
                transpositionTable.get(key);

        if (cached != null &&
                cached.depth >= depth) {

            return cached.score;
        }

        /*
         * Generate legal moves.
         */
        List<Move> legalMoves =
                gameState.getAllLegalMoves(
                        maximizingPlayer
                );

        /*
         * Checkmate / stalemate.
         */
        if (legalMoves.isEmpty()) {

            if (gameState.isCurrentPlayerInCheck()) {

                return maximizingPlayer
                        ? Integer.MIN_VALUE + depth
                        : Integer.MAX_VALUE - depth;
            }

            return 0;
        }

        /*
         * Move ordering.
         */
        orderMoves(legalMoves);

        int bestScore;

        // ========================================================
        // MAXIMIZING
        // ========================================================

        if (maximizingPlayer) {

            bestScore =
                    Integer.MIN_VALUE;

            for (Move move : legalMoves) {

                if (isTimeUp()) {
                    timeUp = true;
                    break;
                }

                gameState.makeTemporaryMove(move);

                int score =
                        minimax(
                                gameState,
                                depth - 1,
                                alpha,
                                beta,
                                false
                        );

                gameState.undoTemporaryMove(move);

                if (timeUp) {
                    break;
                }

                bestScore =
                        Math.max(
                                bestScore,
                                score
                        );

                alpha =
                        Math.max(
                                alpha,
                                bestScore
                        );

                /*
                 * Alpha-beta cutoff.
                 */
                if (beta <= alpha) {
                    break;
                }
            }

        }

        // ========================================================
        // MINIMIZING
        // ========================================================

        else {

            bestScore =
                    Integer.MAX_VALUE;

            for (Move move : legalMoves) {

                if (isTimeUp()) {
                    timeUp = true;
                    break;
                }

                gameState.makeTemporaryMove(move);

                int score =
                        minimax(
                                gameState,
                                depth - 1,
                                alpha,
                                beta,
                                true
                        );

                gameState.undoTemporaryMove(move);

                if (timeUp) {
                    break;
                }

                bestScore =
                        Math.min(
                                bestScore,
                                score
                        );

                beta =
                        Math.min(
                                beta,
                                bestScore
                        );

                /*
                 * Alpha-beta cutoff.
                 */
                if (beta <= alpha) {
                    break;
                }
            }
        }

        /*
         * Only store completed searches.
         */
        if (!timeUp) {

            transpositionTable.put(
                    key,
                    new TTEntry(
                            depth,
                            bestScore
                    )
            );
        }

        return bestScore;
    }


    // ============================================================
    // TIME CONTROL
    // ============================================================

    private boolean isTimeUp() {

        return System.nanoTime()
                >= searchDeadline;
    }


    // ============================================================
    // MOVE ORDERING
    // ============================================================

    private void orderMoves(List<Move> moves) {

        moves.sort(
                Comparator.comparingInt(
                        this::moveScore
                ).reversed()
        );
    }


    private int moveScore(Move move) {

        int score = 0;

        /*
         * Captures first.
         */
        if (move.getCapturedPiece() != null) {

            score += 10_000;

            score +=
                    pieceValue(
                            move.getCapturedPiece()
                    );

            /*
             * Prefer capturing with cheaper pieces.
             */
            score -=
                    pieceValue(
                            move.getPiece()
                    ) / 10;
        }

        /*
         * Promotions.
         */
        if (move.getPiece() instanceof Pawn) {

            if ((move.getPiece().isWhite()
                    && move.getToRow() == 0)
                    ||
                    (!move.getPiece().isWhite()
                            && move.getToRow() == 7)) {

                score += 9_000;
            }
        }

        /*
         * Center control.
         */
        int centerDistance =
                Math.abs(
                        3 - move.getToRow()
                )
                        +
                        Math.abs(
                                3 - move.getToCol()
                        );

        score +=
                Math.max(
                        0,
                        6 - centerDistance
                );

        return score;
    }


    // ============================================================
    // PIECE VALUES
    // ============================================================

    private int pieceValue(Piece piece) {

        if (piece instanceof Pawn) {
            return 100;
        }

        if (piece instanceof Knight) {
            return 320;
        }

        if (piece instanceof Bishop) {
            return 330;
        }

        if (piece instanceof Rook) {
            return 500;
        }

        if (piece instanceof Queen) {
            return 900;
        }

        if (piece instanceof King) {
            return 20_000;
        }

        return 0;
    }


    // ============================================================
    // POSITION KEY
    // ============================================================

    private String createPositionKey(
            GameState gameState,
            boolean whiteToMove,
            int depth) {

        StringBuilder key =
                new StringBuilder(70);

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece =
                        gameState.getPiece(
                                row,
                                col
                        );

                if (piece == null) {

                    key.append('.');

                } else {

                    char symbol;

                    if (piece instanceof Pawn) {
                        symbol = 'P';

                    } else if (piece instanceof Knight) {
                        symbol = 'N';

                    } else if (piece instanceof Bishop) {
                        symbol = 'B';

                    } else if (piece instanceof Rook) {
                        symbol = 'R';

                    } else if (piece instanceof Queen) {
                        symbol = 'Q';

                    } else if (piece instanceof King) {
                        symbol = 'K';

                    } else {
                        symbol = '?';
                    }

                    key.append(
                            piece.isWhite()
                                    ? symbol
                                    : Character.toLowerCase(
                                    symbol
                            )
                    );
                }
            }
        }

        /*
         * Side to move.
         */
        key.append(
                whiteToMove
                        ? 'w'
                        : 'b'
        );

        /*
         * Depth matters for this simple transposition table.
         */
        key.append(depth);

        return key.toString();
    }


    // ============================================================
    // RESULT CLASSES
    // ============================================================

    private static class RootResult {

        final Move move;
        final int score;

        RootResult(
                Move move,
                int score) {

            this.move = move;
            this.score = score;
        }
    }


    private static class TTEntry {

        final int depth;
        final int score;

        TTEntry(
                int depth,
                int score) {

            this.depth = depth;
            this.score = score;
        }
    }
}
package chess.board;

import chess.game.GameMode;
import chess.game.MoveNotation;
import chess.pieces.*;
import chess.game.Move;
import chess.player.Player;
import chess.player.ComputerPlayer;
import chess.rules.CheckDetector;
import chess.rules.MoveValidator;
import chess.game.MoveHistory;
import chess.pieces.PieceFactory;
import chess.ai.ComputerDifficulty;
import java.io.Serializable;
import chess.puzzle.PuzzleManager;
import chess.puzzle.PuzzleLibrary;


import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Tile[][] board;

    public Tile[][] getBoard() {
        return board;
    }

    private final Player whitePlayer;
    private final Player blackPlayer;
    private Player currentPlayer;

    private final GameMode gameMode;
    private transient PuzzleManager puzzleManager;

    public boolean isVsComputer() {
        return gameMode == GameMode.PLAYER_VS_COMPUTER;
    }

    private final List<Piece> capturedWhite = new ArrayList<>();
    private final List<Piece> capturedBlack = new ArrayList<>();

    // Move history
    private final MoveHistory  moveHistory = new MoveHistory();

    // Moves that have been undone
    private final List<Move> redoHistory = new ArrayList<>();


    // ================= CONSTRUCTOR =================
    public GameState(String whiteName,
                     String blackName,
                     GameMode gameMode,
                     ComputerDifficulty difficulty) {

        this.gameMode = gameMode;

        this.whitePlayer = new Player(whiteName, Player.Color.WHITE);

        if (gameMode == GameMode.PLAYER_VS_COMPUTER) {
            this.blackPlayer = new ComputerPlayer(Player.Color.BLACK, difficulty);
        } else {
            this.blackPlayer = new Player(blackName, Player.Color.BLACK);
        }

        this.currentPlayer = whitePlayer;

        board = new Tile[8][8];

        initTiles();

        if (gameMode == GameMode.PUZZLE) {

            puzzleManager = chess.puzzle.PuzzleLibrary.createDefaultLibrary();

            puzzleManager.loadCurrentPuzzle(this);


        } else {
            initBoard();
        }
    }

    public PuzzleManager getPuzzleManager() {
        return puzzleManager;
    }
    public GameMode getGameMode() {
        return gameMode;
    }

    public void makeTemporaryMove(Move move) {
        applyMove(move, false, false);
    }

    public void undoTemporaryMove(Move move) {
        revertMove(move, false);
    }

    public void initializePuzzleManager() {
        if (puzzleManager == null) {
            puzzleManager = PuzzleLibrary.createDefaultLibrary();
        }
    }


    // ================= BOARD SETUP =================
    private void initTiles() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = new Tile(r, c);
            }
        }
    }

    // ================= BOARD ACCESS =================
    public void setPiece(int row, int col, Piece piece) {

        board[row][col].setPiece(piece);
    }

    public Piece getPiece(int row, int col) {

        return board[row][col].getPiece();
    }

    // ================= MOVES =================
    /**
     * Returns true only for the en-passant capture available immediately
     * after the opponent advances a pawn two squares.
     */
    public boolean isEnPassantMove(Move move) {
        if (move == null || !(move.getPiece() instanceof Pawn)) return false;

        Piece pawn = move.getPiece();
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();
        int direction = pawn.isWhite() ? -1 : 1;

        if (toRow - fromRow != direction || Math.abs(toCol - fromCol) != 1) return false;
        if (getPiece(toRow, toCol) != null) return false;

        List<Move> moves = moveHistory.getMoves();
        if (moves.isEmpty()) return false;

        Move last = moves.get(moves.size() - 1);
        Piece lastPiece = last.getPiece();
        if (!(lastPiece instanceof Pawn) || lastPiece.isWhite() == pawn.isWhite()) return false;
        if (last.getFromCol() != toCol || last.getToCol() != toCol) return false;
        if (Math.abs(last.getToRow() - last.getFromRow()) != 2) return false;
        if (last.getToRow() != fromRow) return false;

        return getPiece(fromRow, toCol) == lastPiece;
    }

    public boolean makeMove(Move move) {

        return applyMove(move, true, true);

    }

    /**
     * Applies a move received from the online opponent.
     *
     * The local GameState may currently have the local player's
     * turn, so we temporarily synchronize the current player with
     * the player who actually made the remote move.
     */
    public boolean makeRemoteMove(Move move, boolean moverIsWhite) {
        if (move == null) {
            return false;
        }

        // Remember the current turn in case the remote move fails.
        boolean originalTurn = isWhiteTurn();

        // Temporarily set the turn to the player who made the remote move.
        if (moverIsWhite) {
            setCurrentPlayerWhite();
        } else {
            setCurrentPlayerBlack();
        }

        // Apply the move using the normal validated move pipeline.
        boolean success = applyMove(move, true, true);

        // If the remote move failed, restore the original turn.
        if (!success) {
            if (originalTurn) {
                setCurrentPlayerWhite();
            } else {
                setCurrentPlayerBlack();
            }
        }

        return success;
    }

    private boolean applyMove(
            Move move,
            boolean recordMove,
            boolean validate
    ) {

        if (move == null) {
            System.out.println("INVALID MOVE: move is null");
            return false;
        }

        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

        Piece piece = move.getPiece();

        Piece boardPiece = getPiece(fromRow, fromCol);

        if (boardPiece != piece) {
            System.out.printf(
                    "Source square mismatch! Board has %s, move has %s at (%d,%d)%n",
                    boardPiece == null ? "null" : boardPiece.getClass().getSimpleName(),
                    piece == null ? "null" : piece.getClass().getSimpleName(),
                    fromRow,
                    fromCol
            );
            return false;
        }

        if (piece == null) {
            System.out.println("INVALID MOVE: move has no piece");
            return false;
        }


        if (validate) {
            if (!MoveValidator.isLegalMove(move, this)) {
                return false;
            }
        }



// ================= CASTLING =================
        if (piece instanceof King &&
                Math.abs(toCol - fromCol) == 2) {

            move.setCastling(toCol > fromCol);

            // Kingside castling
            if (toCol > fromCol) {

                Piece rook = getPiece(fromRow, 7);

                move.setCastleRook(rook, 7, 5);

                board[fromRow][5].setPiece(rook);
                board[fromRow][7].clear();

                rook.setPosition(fromRow, 5);

                if (rook instanceof Rook r) {
                    r.setHasMoved(true);
                }


            }

            // Queenside castling
            else {

                Piece rook = getPiece(fromRow, 0);

                move.setCastleRook(rook, 0, 3);

                board[fromRow][3].setPiece(rook);
                board[fromRow][0].clear();

                rook.setPosition(fromRow, 3);

                if (rook instanceof Rook r) {
                    r.setHasMoved(true);
                }

            }
        }

// Move king (or any piece)

        Piece captured = board[toRow][toCol].getPiece();

        if (piece instanceof Pawn && isEnPassantMove(move)) {
            int capturedRow = fromRow;
            int capturedCol = toCol;
            captured = board[capturedRow][capturedCol].getPiece();

            if (!(captured instanceof Pawn) || captured.isWhite() == piece.isWhite()) {
                return false;
            }

            move.setEnPassant(captured, capturedRow, capturedCol);
            board[capturedRow][capturedCol].clear();
        }

        if (recordMove && captured != null) {
            addCaptured(captured);
        }

        board[fromRow][fromCol].clear();

        board[toRow][toCol].setPiece(piece);
        piece.setPosition(toRow, toCol);

        // Save move
        if (recordMove) {
            moveHistory.addMove(move);
            redoHistory.clear();
        }


        if (piece instanceof King king) {
            king.setHasMoved(true);
        }

        if (piece instanceof Rook rook) {
            rook.setHasMoved(true);
        }


        // ================= PAWN PROMOTION =================

        if (piece instanceof Pawn
                && ((piece.isWhite() && toRow == 0)
                || (!piece.isWhite() && toRow == 7))) {
            // Promotion choice is made by BoardMoveHandler after the
            // destination square is clicked. Never open UI from GameState.
            Piece promoted = move.getPromotedPiece();
            if (promoted == null) {
                // Silent fallback for simulations/safety only.
                promoted = new chess.pieces.Queen(piece.isWhite(), toRow, toCol);
            }
            promoted.setPosition(toRow, toCol);
            board[toRow][toCol].setPiece(promoted);
        }




        toggleTurn();
        return true;

    }


    private void revertMove(Move move, boolean recordedMove) {

        Piece movedPiece = move.getPiece();

        // Restore original square
        board[move.getFromRow()][move.getFromCol()].setPiece(movedPiece);

        movedPiece.setPositionSilently(
                move.getFromRow(),
                move.getFromCol()
        );

        movedPiece.setHasMoved(move.wasPieceMoved());

        // Restore destination square

        if (move.isPromotion()) {
            board[move.getToRow()][move.getToCol()].clear();
        }

        if (move.isEnPassant()) {
            board[move.getToRow()][move.getToCol()].clear();
            Piece epCaptured = move.getCapturedPiece();
            if (epCaptured != null) {
                int r = move.getEnPassantCapturedRow();
                int c = move.getEnPassantCapturedCol();
                board[r][c].setPiece(epCaptured);
                epCaptured.setPositionSilently(r, c);
                epCaptured.setHasMoved(move.capturedPieceHadMoved());
            }
        } else {
            board[move.getToRow()][move.getToCol()]
                    .setPiece(move.getCapturedPiece());

            if (move.getCapturedPiece() != null) {
                move.getCapturedPiece().setPositionSilently(
                        move.getToRow(),
                        move.getToCol());

                move.getCapturedPiece().setHasMoved(
                        move.capturedPieceHadMoved());
            }
        }


        Piece captured = move.getCapturedPiece();


        if (recordedMove && captured != null) {

            if (captured.isWhite()) {
                capturedWhite.remove(captured);
            } else {
                capturedBlack.remove(captured);
            }
        }


        // Restore rook if castling
        if (move.isCastling()) {

            int row = move.getFromRow();

            if (move.isKingSideCastle()) {

                Piece rook = board[row][5].getPiece();

                board[row][7].setPiece(rook);
                board[row][5].clear();

                rook.setPosition(row, 7);

                if (rook instanceof Rook r) {
                    r.setHasMoved(move.rookHadMoved());
                }

            } else {

                Piece rook = board[row][3].getPiece();

                board[row][0].setPiece(rook);
                board[row][3].clear();

                rook.setPosition(row, 0);

                if (rook instanceof Rook r) {
                    r.setHasMoved(move.rookHadMoved());
                }
            }
        }



        toggleTurn();





    }



    // ================= UNDO MOVE =================
    public void undoMove() {

        Move move = moveHistory.removeLastMove();

        if (move == null) {
            return;
        }

        redoHistory.add(move);

        revertMove(move, true);
    }


    public void redoMove() {

        if (redoHistory.isEmpty()) {
            return;
        }

        /*
         * The most recently undone move is at the end.
         */
        Move move =
                redoHistory.remove(
                        redoHistory.size() - 1
                );

        /*
         * Preserve the remaining redo history.
         *
         * makeMove() normally clears redoHistory because
         * a normal new move invalidates the redo chain.
         *
         * During REDO, however, we must keep the other
         * previously undone moves.
         */
        List<Move> remainingRedo =
                new ArrayList<>(redoHistory);

        /*
         * Re-apply the move.
         */
        boolean success =
                makeMove(move);

        /*
         * Restore the remaining redo moves.
         */
        if (success) {

            redoHistory.clear();

            redoHistory.addAll(
                    remainingRedo
            );

        } else {

            /*
             * If redo failed, put the move back so the
             * redo history is not lost.
             */
            redoHistory.clear();

            redoHistory.addAll(
                    remainingRedo
            );

            redoHistory.add(move);
        }
    }

    // ================= TURN SYSTEM =================
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isCurrentPlayerInCheck() {
        return CheckDetector.isKingInCheck(isWhiteTurn(), this);
    }

    public boolean isWhiteTurn() {
        return currentPlayer == whitePlayer;
    }

    public void toggleTurn() {
        currentPlayer = (currentPlayer == whitePlayer)
                ? blackPlayer
                : whitePlayer;
    }

    public void setCurrentPlayerWhite() {

        currentPlayer = whitePlayer;

    }

    public void setCurrentPlayerBlack() {

        currentPlayer = blackPlayer;

    }

    // ================= PLAYERS =================
    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    // ================= CAPTURES =================
    public List<Piece> getCapturedWhite() {

        return capturedWhite;
    }

    public List<Piece> getCapturedBlack() {

        return capturedBlack;
    }

    // ================= MOVE HISTORY =================
    public MoveHistory getMoveHistory() {
        return moveHistory;
    }

    public String getMoveHistoryText() {

        StringBuilder sb = new StringBuilder();

        int moveNumber = 1;

        List<Move> moves = moveHistory.getMoves();

        for (int i = 0; i < moves.size(); i++) {

            if (i % 2 == 0) {
                sb.append(moveNumber++).append(". ");
            }

            sb.append(MoveNotation.toNotation(moves.get(i)))
                    .append(" ");
        }

        return sb.toString();
    }

    public void addCaptured(Piece piece) {

        if (piece == null) {
            return;
        }

        if (piece.isWhite()) {
            capturedWhite.add(piece);
        } else {
            capturedBlack.add(piece);
        }
    }

    // ================= RESET =================
    public void reset() {

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c].setPiece(null);
            }
        }

        moveHistory.clear();
        redoHistory.clear();

        capturedWhite.clear();
        capturedBlack.clear();

        currentPlayer = whitePlayer;

        initBoard();
    }

    public List<Piece> getPieces(boolean white) {

        List<Piece> pieces = new ArrayList<>();

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece = getPiece(row, col);

                if (piece != null && piece.isWhite() == white) {
                    pieces.add(piece);
                }
            }
        }

        return pieces;
    }

    public List<Move> getAllLegalMoves(boolean white) {

        List<Move> moves = new ArrayList<>();

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece = getPiece(row, col);

                if (piece == null)
                    continue;

                if (piece.isWhite() != white)
                    continue;



                List<Move> pieceMoves =
                        MoveValidator.getValidMoves(
                                piece,
                                row,
                                col,
                                this
                        );



                moves.addAll(pieceMoves);
            }
        }

        return moves;
    }

    /**
     * Returns true if the current position has insufficient
     * material to checkmate either side.
     *
     * Currently handles:
     * - King vs King
     * - King + Bishop vs King
     * - King + Knight vs King
     */
    public boolean isInsufficientMaterial() {

        int whiteBishops = 0;
        int blackBishops = 0;

        int whiteKnights = 0;
        int blackKnights = 0;

        int otherPieces = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                Piece piece = getPiece(row, col);

                if (piece == null || piece instanceof King) {
                    continue;
                }

                if (piece instanceof Bishop) {

                    if (piece.isWhite()) {
                        whiteBishops++;
                    } else {
                        blackBishops++;
                    }

                } else if (piece instanceof Knight) {

                    if (piece.isWhite()) {
                        whiteKnights++;
                    } else {
                        blackKnights++;
                    }

                } else {

                    // Pawn, Rook or Queen means there is
                    // potentially enough material to checkmate.
                    otherPieces++;
                }
            }
        }

        // King vs King
        if (otherPieces == 0 &&
                whiteBishops == 0 &&
                blackBishops == 0 &&
                whiteKnights == 0 &&
                blackKnights == 0) {

            return true;
        }

        // King + Bishop vs King
        if (otherPieces == 0 &&
                whiteBishops == 1 &&
                blackBishops == 0 &&
                whiteKnights == 0 &&
                blackKnights == 0) {

            return true;
        }

        if (otherPieces == 0 &&
                blackBishops == 1 &&
                whiteBishops == 0 &&
                whiteKnights == 0 &&
                blackKnights == 0) {

            return true;
        }

        // King + Knight vs King
        if (otherPieces == 0 &&
                whiteKnights == 1 &&
                blackKnights == 0 &&
                whiteBishops == 0 &&
                blackBishops == 0) {

            return true;
        }

        if (otherPieces == 0 &&
                blackKnights == 1 &&
                whiteKnights == 0 &&
                whiteBishops == 0 &&
                blackBishops == 0) {

            return true;
        }

        return false;
    }

    public boolean isStalemate() {

        boolean whiteTurn = isWhiteTurn();

        // If the current player is in check, it's not stalemate.
        if (CheckDetector.isKingInCheck(whiteTurn, this)) {
            return false;
        }

        // Look for any legal move.
        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece = getPiece(row, col);

                if (piece != null && piece.isWhite() == whiteTurn) {

                    if (!MoveValidator.getValidMoves(piece, row, col, this).isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


    public void reloadPieceImages() {

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece = board[row][col].getPiece();

                if (piece != null) {
                    piece.reloadImage();
                }
            }
        }

        for (Piece piece : capturedWhite) {
            piece.reloadImage();
        }

        for (Piece piece : capturedBlack) {
            piece.reloadImage();
        }
    }

    // ================= INITIAL BOARD =================
    private void initBoard() {

        PieceFactory.createInitialPosition(this);
    }



}
package chess.game;

import chess.pieces.Piece;
import java.io.Serializable;

public class Move implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Piece piece;
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private Piece capturedPiece;

    // State before the move
    private final boolean pieceHadMoved;
    private boolean capturedPieceHadMoved;

    // En passant
    private boolean enPassant;
    private int enPassantCapturedRow = -1;
    private int enPassantCapturedCol = -1;

    // Castling
    private Piece castleRook;
    private int rookFromCol;
    private int rookToCol;
    private boolean rookHadMoved;

    private boolean castling;
    private boolean kingSideCastle;

    // Promotion
    private Piece promotedPiece;
    private boolean promotion;

    public Move(Piece piece,
                int fromRow,
                int fromCol,
                int toRow,
                int toCol,
                Piece capturedPiece) {

        this.piece = piece;
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.capturedPiece = capturedPiece;

        this.pieceHadMoved = piece != null && piece.hasMoved();

        if (capturedPiece != null) {
            this.capturedPieceHadMoved = capturedPiece.hasMoved();
        }
    }




    // GETTERS
    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }

    public Piece getPiece() {
        return piece;
    }

    public Piece getCapturedPiece() {

        return capturedPiece;
    }

    public void setCapturedPiece(Piece capturedPiece) {
        this.capturedPiece = capturedPiece;
        if (capturedPiece != null) {
            this.capturedPieceHadMoved = capturedPiece.hasMoved();
        }
    }

    public void setEnPassant(Piece capturedPiece, int row, int col) {
        this.enPassant = true;
        this.enPassantCapturedRow = row;
        this.enPassantCapturedCol = col;
        setCapturedPiece(capturedPiece);
    }

    public boolean isEnPassant() {
        return enPassant;
    }

    public int getEnPassantCapturedRow() {
        return enPassantCapturedRow;
    }

    public int getEnPassantCapturedCol() {
        return enPassantCapturedCol;
    }

    public void setPromotion(Piece promotedPiece) {
        this.promotedPiece = promotedPiece;
        this.promotion = true;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public Piece getPromotedPiece() {
        return promotedPiece;
    }

    public boolean wasPieceMoved() {
        return pieceHadMoved;
    }

    public boolean capturedPieceHadMoved() {
        return capturedPieceHadMoved;
    }


    public void setCastling(boolean kingSide) {
        castling = true;
        kingSideCastle = kingSide;
    }

    public boolean isCastling() {
        return castling;
    }

    public boolean isKingSideCastle() {
        return kingSideCastle;
    }

    public void setCastleRook(Piece rook, int fromCol, int toCol) {
        this.castleRook = rook;
        this.rookFromCol = fromCol;
        this.rookToCol = toCol;
        this.rookHadMoved = (rook != null) && rook.hasMoved();
    }

    public Piece getCastleRook() {
        return castleRook;
    }

    public int getRookFromCol() {
        return rookFromCol;
    }

    public int getRookToCol() {
        return rookToCol;
    }

    public boolean rookHadMoved() {
        return rookHadMoved;
    }

    @Override
    public String toString() {

        String pieceName = (piece == null)
                ? "null"
                : piece.getClass().getSimpleName();

        String color = (piece == null)
                ? ""
                : (piece.isWhite() ? "White" : "Black");

        return String.format(
                "%s %s (%d,%d) -> (%d,%d)",
                color,
                pieceName,
                fromRow,
                fromCol,
                toRow,
                toCol
        );
    }

}

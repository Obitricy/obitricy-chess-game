package chess.board;

import chess.game.Move;
import chess.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class BoardUIState {

    // ===========================
    // Selection
    // ===========================

    private Piece selected;

    private int selectedRow = -1;
    private int selectedCol = -1;

    // ===========================
    // Hover
    // ===========================

    private int hoverRow = -1;
    private int hoverCol = -1;

    // ===========================
// Last Move
// ===========================

    private int lastFromRow = -1;
    private int lastFromCol = -1;

    private int lastToRow = -1;
    private int lastToCol = -1;

    // ===========================
    // Last Move
    // ===========================

    public void clearLastMove() {
        lastFromRow = -1;
        lastFromCol = -1;
        lastToRow = -1;
        lastToCol = -1;
    }

    // ===========================
    // Hint
    // ===========================

    private Move hintMove;

    private int hintFromRow = -1;
    private int hintFromCol = -1;

    private int hintToRow = -1;
    private int hintToCol = -1;

    // ===========================
    // Game Over
    // ===========================

    private String gameOverTitle;
    private String gameOverMessage;

    // ===========================
    // Lists
    // ===========================

    private final List<AnimatedPiece> animations =
            new ArrayList<>();

    private final List<Move> legalMoves =
            new ArrayList<>();

    // ===========================
    // Getters / Setters
    // ===========================

    public Piece getSelected() {
        return selected;
    }

    public void setSelected(Piece selected) {
        this.selected = selected;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public void setSelectedCol(int selectedCol) {
        this.selectedCol = selectedCol;
    }

    public int getHoverRow() {
        return hoverRow;
    }

    public void setHoverRow(int hoverRow) {
        this.hoverRow = hoverRow;
    }

    public int getHoverCol() {
        return hoverCol;
    }

    public void setHoverCol(int hoverCol) {
        this.hoverCol = hoverCol;
    }

    public int getLastFromRow() {
        return lastFromRow;
    }

    public void setLastFromRow(int lastFromRow) {
        this.lastFromRow = lastFromRow;
    }

    public int getLastFromCol() {
        return lastFromCol;
    }

    public void setLastFromCol(int lastFromCol) {
        this.lastFromCol = lastFromCol;
    }

    public int getLastToRow() {
        return lastToRow;
    }

    public void setLastToRow(int lastToRow) {
        this.lastToRow = lastToRow;
    }

    public int getLastToCol() {
        return lastToCol;
    }

    public void setLastToCol(int lastToCol) {
        this.lastToCol = lastToCol;
    }

    public Move getHintMove() {
        return hintMove;
    }

    public void setHintMove(Move hintMove) {
        this.hintMove = hintMove;
    }

    public int getHintFromRow() {
        return hintFromRow;
    }

    public void setHintFromRow(int hintFromRow) {
        this.hintFromRow = hintFromRow;
    }

    public int getHintFromCol() {
        return hintFromCol;
    }

    public void setHintFromCol(int hintFromCol) {
        this.hintFromCol = hintFromCol;
    }

    public int getHintToRow() {
        return hintToRow;
    }

    public void setHintToRow(int hintToRow) {
        this.hintToRow = hintToRow;
    }

    public int getHintToCol() {
        return hintToCol;
    }

    public void setHintToCol(int hintToCol) {
        this.hintToCol = hintToCol;
    }

    public String getGameOverTitle() {
        return gameOverTitle;
    }

    public void setGameOverTitle(String gameOverTitle) {
        this.gameOverTitle = gameOverTitle;
    }

    public String getGameOverMessage() {
        return gameOverMessage;
    }

    public void setGameOverMessage(String gameOverMessage) {
        this.gameOverMessage = gameOverMessage;
    }

    public List<AnimatedPiece> getAnimations() {
        return animations;
    }

    public List<Move> getLegalMoves() {
        return legalMoves;
    }

    public void clearSelection() {
        selected = null;
        selectedRow = -1;
        selectedCol = -1;
    }

    public void clearHint() {

        hintMove = null;

        hintFromRow = -1;
        hintFromCol = -1;

        hintToRow = -1;
        hintToCol = -1;
    }

    public void clearLegalMoves() {
        legalMoves.clear();
    }

    public void clearAnimations() {
        animations.clear();
    }


    public void setLastMove(Move move) {

        lastFromRow = move.getFromRow();
        lastFromCol = move.getFromCol();

        lastToRow = move.getToRow();
        lastToCol = move.getToCol();
    }
}
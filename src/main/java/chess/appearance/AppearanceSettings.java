package chess.appearance;

import chess.board.BoardView;

public class AppearanceSettings {

    // =========================================================
    // DEFAULT APPEARANCE SETTINGS
    // =========================================================

    private BoardTheme boardTheme =
            BoardTheme.WALNUT;

    private PieceTheme pieceTheme =
            PieceTheme.CLASSIC;

    private HighlightTheme highlightTheme =
            HighlightTheme.CLASSIC;

    private boolean darkMode =
            false;

    private BoardView boardView =
            BoardView.THREE_D;


    // =========================================================
    // BOARD THEME
    // =========================================================

    public BoardTheme getBoardTheme() {
        return boardTheme;
    }

    public void setBoardTheme(BoardTheme boardTheme) {

        if (boardTheme != null) {
            this.boardTheme = boardTheme;
        }
    }


    // =========================================================
    // PIECE THEME
    // =========================================================

    public PieceTheme getPieceTheme() {
        return pieceTheme;
    }

    public void setPieceTheme(PieceTheme pieceTheme) {

        if (pieceTheme != null) {
            this.pieceTheme = pieceTheme;
        }
    }


    // =========================================================
    // HIGHLIGHT THEME
    // =========================================================

    public HighlightTheme getHighlightTheme() {
        return highlightTheme;
    }

    public void setHighlightTheme(
            HighlightTheme highlightTheme) {

        if (highlightTheme != null) {
            this.highlightTheme = highlightTheme;
        }
    }


    // =========================================================
    // DARK MODE
    // =========================================================

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }


    // =========================================================
    // BOARD VIEW
    // =========================================================

    public BoardView getBoardView() {
        return boardView;
    }

    public void setBoardView(BoardView boardView) {

        if (boardView != null) {
            this.boardView = boardView;
        }
    }
}
package chess.appearance;

import chess.board.BoardView;

public final class ThemeManager {

    private static final AppearanceSettings settings =
            new AppearanceSettings();

    private ThemeManager() {
        // Prevent instantiation
    }

    // =========================================================
    // SETTINGS
    // =========================================================

    public static AppearanceSettings getSettings() {
        return settings;
    }

    // =========================================================
    // BOARD THEME
    // =========================================================

    public static BoardTheme getBoardTheme() {

        BoardTheme theme = settings.getBoardTheme();

        if (theme == null) {
            theme = BoardTheme.CLASSIC;
            settings.setBoardTheme(theme);
        }

        return theme;
    }

    public static void setBoardTheme(BoardTheme theme) {

        if (theme != null) {
            settings.setBoardTheme(theme);
        }
    }

    // =========================================================
    // PIECE THEME
    // =========================================================

    public static PieceTheme getPieceTheme() {

        PieceTheme theme = settings.getPieceTheme();

        if (theme == null) {
            theme = PieceTheme.CLASSIC;
            settings.setPieceTheme(theme);
        }

        return theme;
    }

    public static void setPieceTheme(PieceTheme theme) {

        if (theme != null) {
            settings.setPieceTheme(theme);
        }
    }

    // =========================================================
    // HIGHLIGHT THEME
    // =========================================================

    public static HighlightTheme getHighlightTheme() {

        HighlightTheme theme = settings.getHighlightTheme();

        if (theme == null) {
            theme = HighlightTheme.CLASSIC;
            settings.setHighlightTheme(theme);
        }

        return theme;
    }

    public static void setHighlightTheme(
            HighlightTheme theme) {

        if (theme != null) {
            settings.setHighlightTheme(theme);
        }
    }

    // =========================================================
    // DARK MODE
    // =========================================================

    public static boolean isDarkMode() {
        return settings.isDarkMode();
    }

    public static void setDarkMode(boolean darkMode) {
        settings.setDarkMode(darkMode);
    }

    // =========================================================
    // BOARD VIEW
    // =========================================================

    public static BoardView getBoardView() {

        BoardView boardView = settings.getBoardView();

        if (boardView == null) {
            boardView = BoardView.class.getEnumConstants()[0];
            settings.setBoardView(boardView);
        }

        return boardView;
    }

    public static void setBoardView(BoardView boardView) {

        if (boardView != null) {
            settings.setBoardView(boardView);
        }
    }
}
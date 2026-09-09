package chess.appearance;

import java.awt.Color;

public enum HighlightTheme {

    CLASSIC(
            new Color(50, 205, 50, 140),   // Last move
            new Color(255, 0, 0, 120),      // Check
            new Color(255, 215, 0, 120),    // Selected piece
            new Color(30, 144, 255, 100)    // Legal moves
    ),

    BLUE(
            new Color(30, 144, 255, 140),
            new Color(220, 20, 60, 120),
            new Color(135, 206, 250, 120),
            new Color(0, 191, 255, 100)
    ),

    GOLD(
            new Color(255, 215, 0, 140),
            new Color(178, 34, 34, 120),
            new Color(255, 255, 102, 120),
            new Color(255, 165, 0, 100)
    ),

    RED(
            new Color(220, 20, 60, 140),
            new Color(255, 0, 0, 120),
            new Color(255, 99, 71, 120),
            new Color(255, 69, 0, 100)
    );

    private final Color lastMoveColor;
    private final Color checkColor;
    private final Color selectedColor;
    private final Color legalMoveColor;

    HighlightTheme(Color lastMoveColor,
                   Color checkColor,
                   Color selectedColor,
                   Color legalMoveColor) {

        this.lastMoveColor = lastMoveColor;
        this.checkColor = checkColor;
        this.selectedColor = selectedColor;
        this.legalMoveColor = legalMoveColor;
    }

    public Color getLastMoveColor() {
        return lastMoveColor;
    }

    public Color getCheckColor() {
        return checkColor;
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public Color getLegalMoveColor() {
        return legalMoveColor;
    }
}
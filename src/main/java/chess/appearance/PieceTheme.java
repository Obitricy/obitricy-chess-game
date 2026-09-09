package chess.appearance;

public enum PieceTheme {

    CLASSIC("Classic"),

    WOODEN("Wooden"),

    MARBLE("Marble"),

    GLASS("Glass"),

    FANTASY("Fantasy");

    private final String displayName;

    PieceTheme(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
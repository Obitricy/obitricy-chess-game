package chess.appearance;

import java.awt.Color;

public enum BoardTheme {

    CLASSIC("Classic",
            new Color(240,217,181),
            new Color(181,136,99)),

    GREEN("Green",
            new Color(238,238,210),
            new Color(118,150,86)),

    BLUE("Blue",
            new Color(222,227,230),
            new Color(70,130,180)),

    WALNUT("Walnut",
            new Color(226,195,151),
            new Color(118,69,34)),

    GREY("Grey",
            new Color(235,235,235),
            new Color(120,120,120)),

    PURPLE("Purple",
            new Color(230,220,245),
            new Color(110,70,150)),

    PINK("Pink",
            new Color(255,228,225),
            new Color(199,120,150)),

    MIDNIGHT("Midnight",
            new Color(100,100,120),
            new Color(40,40,60));

    private final String name;
    private final Color light;
    private final Color dark;

    BoardTheme(String name, Color light, Color dark) {

        this.name = name;
        this.light = light;
        this.dark = dark;
    }

    public String getName() {
        return name;
    }

    public Color getLight() {
        return light;
    }

    public Color getDark() {
        return dark;
    }
}
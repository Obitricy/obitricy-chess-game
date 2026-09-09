package chess.utils;

import chess.appearance.PieceTheme;
import chess.appearance.ThemeManager;
import chess.pieces.Piece;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageLoader {

    public static Image load(String imageName) {

        // Always guarantee a valid piece theme
        PieceTheme pieceTheme =
                ThemeManager.getPieceTheme();

        if (pieceTheme == null) {
            pieceTheme = PieceTheme.CLASSIC;
            ThemeManager.setPieceTheme(pieceTheme);
        }

        String folder =
                pieceTheme.name().toLowerCase();

        String path =
                "/pieces/" +
                        folder +
                        "/" +
                        imageName;

        try {

            URL url =
                    ImageLoader.class.getResource(path);

            if (url == null) {
                throw new RuntimeException(
                        "Image not found: " + path
                );
            }

            BufferedImage img =
                    ImageIO.read(url);

            if (img == null) {
                throw new RuntimeException(
                        "Could not read image: " + path
                );
            }

            BufferedImage transparent =
                    new BufferedImage(
                            img.getWidth(),
                            img.getHeight(),
                            BufferedImage.TYPE_INT_ARGB
                    );

            Graphics2D g2d =
                    transparent.createGraphics();

            g2d.drawImage(
                    img,
                    0,
                    0,
                    null
            );

            g2d.dispose();

            return transparent;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load image: " + path,
                    e
            );
        }
    }

    public static Image loadPiece(Piece piece) {

        if (piece == null) {
            throw new IllegalArgumentException(
                    "Cannot load image for a null piece."
            );
        }

        String color =
                piece.isWhite()
                        ? "white_"
                        : "black_";

        String type =
                piece.getType()
                        .toLowerCase();

        return load(
                color +
                        type +
                        ".png"
        );
    }
}
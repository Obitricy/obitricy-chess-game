package chess.board;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Path2D;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.util.Random;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import chess.appearance.BoardTheme;
import chess.appearance.ThemeManager;
import chess.game.GameMode;
import chess.game.Move;
import chess.pieces.King;
import chess.pieces.Piece;
import chess.rules.CheckDetector;

public class BoardRenderer {

    private final ChessBoard board;
    private final BoardUIState ui;
    private final BoardLayout layout;
    private final Board3DProjection projection;

    private final Map<java.awt.Image, PieceImageData> imageData =
            new IdentityHashMap<>();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public BoardRenderer(
            ChessBoard board,
            BoardLayout layout,
            BoardUIState ui) {

        this.board = board;
        this.layout = layout;
        this.ui = ui;

        this.projection =
                new Board3DProjection(layout);
    }


    // =========================================================
    // GET PROJECTION
    // =========================================================

    public Board3DProjection getProjection() {
        return projection;
    }



    // =========================================================
// WOOD MATERIALS
// =========================================================

    private BufferedImage tableWoodTexture;
    private BufferedImage lightWoodTexture;
    private BufferedImage darkWoodTexture;

    private int textureScale = -1;


    // =========================================================
    // MAIN PAINT
    // =========================================================

    public void paint(Graphics g) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        try {

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            // =====================================================
            // 3D WOODEN ENVIRONMENT
            // =====================================================

            if (board.getBoardView() ==
                    BoardView.THREE_D) {

                drawWoodenEnvironment(g2);

            } else {

                // Keep the normal 2D background simple.
                g2.setColor(
                        new Color(35, 24, 16)
                );

                g2.fillRect(
                        0,
                        0,
                        board.getWidth(),
                        board.getHeight()
                );
            }


            // =====================================================
            // BOARD
            // =====================================================

            if (board.getBoardView() ==
                    BoardView.THREE_D) {

                draw3DBoard(g2);

            } else {

                draw2DBoard(g2);
            }


            // =====================================================
            // COORDINATES
            // =====================================================

            drawCoordinates(g2);


            // =====================================================
            // LEGAL MOVE HIGHLIGHTS
            // =====================================================

            drawLegalMoves(g2);


            // =====================================================
            // PIECES
            // =====================================================

            drawPieces(g2);


            // =====================================================
            // ANIMATIONS
            // =====================================================

            drawAnimations(g2);

        } finally {

            g2.dispose();
        }
    }


    // =========================================================
    // DRAW PIECES
    // =========================================================

    private void drawPieces(Graphics2D g2) {

        if (board.getBoardView() ==
                BoardView.THREE_D) {

            draw3DPieces(g2);

        } else {

            draw2DPieces(g2);
        }
    }


    // =========================================================
    // 2D PIECES
    // =========================================================

    private void draw2DPieces(Graphics2D g2) {

        GameState gameState =
                board.getGameState();

        int tile =
                layout.getTileSize();

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece =
                        gameState.getPiece(
                                row,
                                col
                        );

                if (piece == null) {
                    continue;
                }

                if (isAnimated(piece)) {
                    continue;
                }

                int x =
                        layout.getBoardX()
                                + col * tile;

                int y =
                        layout.getBoardY()
                                + row * tile;

                drawPieceShadow(
                        g2,
                        x,
                        y,
                        tile
                );

                if (piece.getImage() != null) {

                    drawSeatedPiece(
                            g2,
                            piece.getImage(),
                            x + tile / 2.0,
                            y + tile,
                            tile * 0.84,
                            tile * 0.92
                    );
                }
            }
        }
    }


    // =========================================================
    // 3D PIECES
    // =========================================================


    // =========================================================
    // 3D PIECES
    // =========================================================

    /**
     * Draws pieces from back to front using the depth supplied by
     * Board3DProjection. This prevents front pieces from being
     * painted behind pieces that are physically farther away.
     */
    private void draw3DPieces(Graphics2D g2) {

        GameState gameState =
                board.getGameState();

        List<Point2D.Double> occupied =
                new java.util.ArrayList<>();

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                Piece piece =
                        gameState.getPiece(row, col);

                if (piece == null ||
                        isAnimated(piece) ||
                        piece.getImage() == null) {

                    continue;
                }

                occupied.add(
                        new Point2D.Double(col, row)
                );
            }
        }

        /*
         * Painter's order:
         * rear ranks first, front ranks last.
         *
         * Board3DProjection.getDepth() now agrees with the visual
         * camera direction, so a front piece is always painted later.
         */
        occupied.sort(
                java.util.Comparator.comparingDouble(
                        p -> projection.getDepth(
                                (int) p.y,
                                (int) p.x
                        )
                )
        );

        for (Point2D.Double square : occupied) {

            int col = (int) square.x;
            int row = (int) square.y;

            Piece piece =
                    gameState.getPiece(row, col);

            if (piece == null ||
                    piece.getImage() == null) {

                continue;
            }

            drawReal3DPiece(
                    g2,
                    piece,
                    piece.getImage(),
                    row,
                    col
            );
        }
    }


    // =========================================================
    // REAL 3D PIECE
    // =========================================================

    private void drawReal3DPiece(
            Graphics2D g2,
            Piece piece,
            java.awt.Image image,
            int row,
            int col) {

        Point2D.Double[] square =
                projection.getSquare(row, col);

        if (square == null ||
                square.length < 4) {

            return;
        }

        /*
         * Exact physical centre of the square.
         * drawSeatedPiece() uses this point as the contact point.
         */
        Point2D.Double base =
                projection.getPieceBase(row, col);

        if (base == null) {
            return;
        }

        /*
         * Measure the actual projected square.
         */
        double topWidth =
                distance(square[0], square[1]);

        double bottomWidth =
                distance(square[3], square[2]);

        double leftDepth =
                distance(square[0], square[3]);

        double rightDepth =
                distance(square[1], square[2]);

        double projectedWidth =
                (topWidth + bottomWidth) * 0.5;

        double projectedDepth =
                (leftDepth + rightDepth) * 0.5;

        /*
         * Perspective scale is used only as a controlled safety factor.
         *
         * The old renderer multiplied projectedDepth by perspectiveScale
         * again, effectively applying perspective twice.  That made the
         * front pieces grow too aggressively and encouraged rank overlap.
         */
        double perspectiveScale =
                projection.getPerspectiveScale(row, col);

        perspectiveScale =
                Math.max(
                        0.78,
                        Math.min(
                                1.12,
                                perspectiveScale
                        )
                );

        PieceScale pieceScale =
                getPieceScale(piece);

        /*
         * Keep the pieces substantial like the reference image while
         * maintaining a clear margin inside every projected square.
         */
        double maxWidth =
                projectedWidth
                        * pieceScale.width
                        * 0.90;

        /*
         * Use the actual projected depth only once.
         *
         * The front rank is allowed to be slightly larger, but not enough
         * to climb into the rank behind it.
         */
        double maxHeight =
                projectedDepth
                        * pieceScale.height
                        * 1.25;

        /*
         * Small controlled perspective adjustment:
         * front pieces may be slightly larger than rear pieces.
         */
        maxHeight *=
                0.94
                        + 0.06 * perspectiveScale;

        /*
         * Final hard limits prevent any individual piece from becoming
         * taller than the available visual space.
         */
        double heightLimit =
                layout.getTileSize() * 1.10;

        if (row >= 6) {
            heightLimit =
                    layout.getTileSize() * 1.14;
        }

        if (row <= 1) {
            heightLimit =
                    layout.getTileSize() * 1.08;
        }

        maxHeight =
                Math.min(
                        maxHeight,
                        heightLimit
                );

        /*
         * Never allow a piece footprint to become wider than its square.
         */
        maxWidth =
                Math.min(
                        maxWidth,
                        projectedWidth * 0.92
                );

        /*
         * Contact shadow sits directly under the physical base.
         */
        drawProjectedPieceShadow(
                g2,
                piece,
                base.x,
                base.y,
                projectedWidth,
                projectedDepth,
                pieceScale.shadow
        );

        /*
         * Draw from the exact centre of the square, with the visible
         * alpha bounds cropped so transparent image margins cannot shift
         * the piece sideways or downward.
         */
        drawSeatedPiece(
                g2,
                image,
                base.x,
                base.y,
                maxWidth,
                maxHeight
        );

        /*
         * Subtle base highlight reinforces physical contact with the
         * wooden playing surface.
         */
        int highlightWidth =
                Math.max(
                        1,
                        (int) Math.round(
                                projectedWidth
                                        * pieceScale.width
                                        * 0.82
                        )
                );

        int highlightHeight =
                Math.max(
                        1,
                        (int) Math.round(
                                projectedDepth * 0.045
                        )
                );

        drawBaseHighlight(
                g2,
                base.x,
                base.y,
                highlightWidth,
                highlightHeight,
                piece.isWhite()
        );
    }


    /** Piece-specific proportions make the set read as a substantial Staunton set. */
    private PieceScale getPieceScale(Piece piece) {

        String type =
                piece.getType();

        /*
         * These proportions are deliberately conservative.
         * The pieces remain large and elegant without touching the
         * neighbouring rank.
         */
        if ("Pawn".equals(type)) {
            return new PieceScale(
                    0.82,
                    0.88,
                    0.72
            );
        }

        if ("Knight".equals(type)) {
            return new PieceScale(
                    0.86,
                    0.96,
                    0.78
            );
        }

        if ("Bishop".equals(type)) {
            return new PieceScale(
                    0.84,
                    1.00,
                    0.80
            );
        }

        if ("Rook".equals(type)) {
            return new PieceScale(
                    0.84,
                    0.96,
                    0.80
            );
        }

        if ("Queen".equals(type)) {
            return new PieceScale(
                    0.85,
                    1.04,
                    0.82
            );
        }

        if ("King".equals(type)) {
            return new PieceScale(
                    0.86,
                    1.07,
                    0.84
            );
        }

        return new PieceScale(
                0.84,
                0.96,
                0.78
        );
    }


    private static final class PieceScale {
        final double width;
        final double height;
        final double shadow;

        PieceScale(double width, double height, double shadow) {
            this.width = width;
            this.height = height;
            this.shadow = shadow;
        }
    }

    /**
     * A perspective-aware contact shadow: a broad soft footprint plus a darker
     * core directly under the base. This makes pieces look seated rather than
     * pasted onto the square.
     */
    private void drawProjectedPieceShadow(Graphics2D g2, Piece piece,
                                          double centerX, double baselineY,
                                          double squareWidth, double squareDepth,
                                          double strength) {
        double width = squareWidth * 0.62 * strength;
        double height = Math.max(4.0, squareDepth * 0.10);
        double x = centerX - width * 0.5;
        double y = baselineY - height * 0.35;

        java.awt.Composite oldComposite = g2.getComposite();
        java.awt.Paint oldPaint = g2.getPaint();

        g2.setComposite(java.awt.AlphaComposite.SrcOver);
        g2.setPaint(new java.awt.RadialGradientPaint(
                new Point2D.Double(centerX, y + height * 0.5f),
                (float) Math.max(width * 0.5, 1.0),
                new float[] {0f, 0.58f, 1f},
                new Color[] {
                        new Color(0, 0, 0, 88),
                        new Color(0, 0, 0, 35),
                        new Color(0, 0, 0, 0)
                }));
        g2.fill(new Ellipse2D.Double(x, y, width, height));

        // Small warm reflected light directly around the base.
        double rimW = width * 0.54;
        double rimH = Math.max(2.0, height * 0.35);
        g2.setColor(piece.isWhite()
                ? new Color(255, 226, 160, 30)
                : new Color(230, 171, 83, 24));
        g2.fill(new Ellipse2D.Double(centerX - rimW * 0.5,
                baselineY - rimH * 0.55, rimW, rimH));

        g2.setComposite(oldComposite);
        g2.setPaint(oldPaint);
    }

    private Point2D.Double midpoint(Point2D.Double a, Point2D.Double b) {
        return new Point2D.Double(
                (a.x + b.x) * 0.5,
                (a.y + b.y) * 0.5
        );
    }

    // =========================================================
    // SEATED PIECE RENDERING
    // =========================================================

    private void drawSeatedPiece(
            Graphics2D g2,
            java.awt.Image image,
            double centerX,
            double baselineY,
            double maxWidth,
            double maxHeight) {

        if (image == null) {
            return;
        }


        PieceImageData data =
                imageData.get(image);


        if (data == null) {

            data =
                    createPieceImageData(
                            image
                    );

            imageData.put(
                    image,
                    data
            );
        }


        java.awt.Rectangle bounds =
                data.bounds;

        BufferedImage source =
                data.image;


        if (bounds.width <= 0 ||
                bounds.height <= 0) {

            return;
        }


        // =========================================================
        // PRESERVE PIECE ASPECT RATIO
        // =========================================================

        double scale =
                Math.min(
                        maxWidth / bounds.width,
                        maxHeight / bounds.height
                );


        int destW =
                Math.max(
                        1,
                        (int)
                                Math.round(
                                        bounds.width
                                                * scale
                                )
                );


        int destH =
                Math.max(
                        1,
                        (int)
                                Math.round(
                                        bounds.height
                                                * scale
                                )
                );


        // =========================================================
        // CENTER
        // =========================================================

        int destX =
                (int)
                        Math.round(
                                centerX
                                        - destW / 2.0
                        );


        int destY =
                (int)
                        Math.round(
                                baselineY
                                        - destH
                        );


        // =========================================================
        // DRAW
        // =========================================================

        g2.drawImage(
                source,
                destX,
                destY,
                destX + destW,
                destY + destH,
                bounds.x,
                bounds.y,
                bounds.x + bounds.width,
                bounds.y + bounds.height,
                board
        );
    }


    private void drawTopDownBoardThickness(
            Graphics2D g2,
            Point2D.Double[] outer,
            int depth) {

        if (outer == null ||
                outer.length < 4) {

            return;
        }


        // =========================================================
        // FRONT EDGE
        // =========================================================

        Polygon front =
                new Polygon();


        front.addPoint(
                (int)
                        Math.round(
                                outer[3].x
                        ),
                (int)
                        Math.round(
                                outer[3].y
                        )
        );


        front.addPoint(
                (int)
                        Math.round(
                                outer[2].x
                        ),
                (int)
                        Math.round(
                                outer[2].y
                        )
        );


        front.addPoint(
                (int)
                        Math.round(
                                outer[2].x
                        ),
                (int)
                        Math.round(
                                outer[2].y
                                        + depth
                        )
        );


        front.addPoint(
                (int)
                        Math.round(
                                outer[3].x
                        ),
                (int)
                        Math.round(
                                outer[3].y
                                        + depth
                        )
        );


        GradientPaint frontGradient =
                new GradientPaint(
                        0,
                        (float) outer[3].y,
                        new Color(
                                130,
                                72,
                                29
                        ),
                        0,
                        (float)
                                (
                                        outer[3].y
                                                + depth
                                ),
                        new Color(
                                53,
                                25,
                                10
                        )
                );


        g2.setPaint(
                frontGradient
        );

        g2.fillPolygon(front);


        // =========================================================
        // RIGHT EDGE
        // =========================================================

        Polygon right =
                new Polygon();


        right.addPoint(
                (int)
                        Math.round(
                                outer[2].x
                        ),
                (int)
                        Math.round(
                                outer[2].y
                        )
        );


        right.addPoint(
                (int)
                        Math.round(
                                outer[1].x
                        ),
                (int)
                        Math.round(
                                outer[1].y
                        )
        );


        right.addPoint(
                (int)
                        Math.round(
                                outer[1].x
                        ),
                (int)
                        Math.round(
                                outer[1].y
                                        + depth
                        )
        );


        right.addPoint(
                (int)
                        Math.round(
                                outer[2].x
                        ),
                (int)
                        Math.round(
                                outer[2].y
                                        + depth
                        )
        );


        GradientPaint rightGradient =
                new GradientPaint(
                        0,
                        0,
                        new Color(
                                105,
                                56,
                                22
                        ),
                        (float)
                                Math.max(
                                        1,
                                        layout.getBoardSize()
                                ),
                        0,
                        new Color(
                                43,
                                20,
                                8
                        )
                );


        g2.setPaint(
                rightGradient
        );

        g2.fillPolygon(right);
    }

    private void drawBaseHighlight(Graphics2D g2, double centerX, double baselineY,
                                   int pieceWidth, int pieceHeight, boolean white) {
        double w = pieceWidth * 0.56;
        double h = Math.max(2.0, pieceHeight * 0.025);
        double y = baselineY - h * 0.75;

        java.awt.Composite old = g2.getComposite();
        g2.setComposite(java.awt.AlphaComposite.SrcOver);
        g2.setPaint(new GradientPaint(
                (float) (centerX - w * 0.5), (float) y,
                white ? new Color(255, 246, 210, 62) : new Color(246, 201, 125, 38),
                (float) (centerX + w * 0.5), (float) (y + h),
                new Color(0, 0, 0, 0)));
        g2.fill(new Ellipse2D.Double(centerX - w * 0.5, y, w, h));
        g2.setComposite(old);
    }

    private void drawPieceSilhouette(
            Graphics2D g2, BufferedImage source, java.awt.Rectangle bounds,
            int x, int y, int w, int h, boolean white) {

        BufferedImage silhouette = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D sg = silhouette.createGraphics();
        sg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        sg.drawImage(source, 0, 0, w, h, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, null);
        sg.setComposite(java.awt.AlphaComposite.SrcIn);
        sg.setColor(new Color(0, 0, 0, white ? 34 : 58));
        sg.fillRect(0, 0, w, h);
        sg.dispose();

        // Two close passes fake a small soft blur using only Java2D.
        g2.drawImage(silhouette, x + 2, y + 3, null);
        g2.setComposite(java.awt.AlphaComposite.SrcOver);
        g2.drawImage(silhouette, x + 1, y + 2, null);
    }

    private void drawPieceSheen(
            Graphics2D g2, BufferedImage source, java.awt.Rectangle bounds,
            int x, int y, int w, int h, boolean white) {

        BufferedImage overlay = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D og = overlay.createGraphics();
        og.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        GradientPaint sheen = new GradientPaint(
                0, 0, new Color(255, 255, 255, white ? 34 : 22),
                Math.max(1, w), Math.max(1, h), new Color(255, 255, 255, 0));
        og.setPaint(sheen);
        og.fillRect(0, 0, w, h);
        og.setComposite(java.awt.AlphaComposite.DstIn);
        og.drawImage(source, 0, 0, w, h, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, null);
        og.dispose();

        g2.drawImage(overlay, x, y, null);
    }

    private void drawPieceRim(
            Graphics2D g2, BufferedImage source, java.awt.Rectangle bounds,
            int x, int y, int w, int h, boolean white) {

        BufferedImage rim = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D rg = rim.createGraphics();
        rg.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        rg.setComposite(java.awt.AlphaComposite.Src);
        rg.drawImage(source, 0, 0, w, h, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, null);
        rg.setComposite(java.awt.AlphaComposite.SrcIn);
        rg.setPaint(new GradientPaint(0, 0,
                white ? new Color(255, 248, 218, 25) : new Color(235, 198, 125, 20),
                w, h, new Color(0, 0, 0, 0)));
        rg.fillRect(0, 0, w, h);
        rg.dispose();
        g2.drawImage(rim, x, y, null);
    }

    private PieceImageData createPieceImageData(java.awt.Image image) {
        int width = image.getWidth(board);
        int height = image.getHeight(board);

        if (width <= 0 || height <= 0) {
            width = 600;
            height = 600;
        }

        BufferedImage buffer = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imageGraphics = buffer.createGraphics();
        imageGraphics.setComposite(java.awt.AlphaComposite.Src);
        imageGraphics.drawImage(image, 0, 0, board);
        imageGraphics.dispose();

        return new PieceImageData(buffer, findAlphaBounds(buffer));
    }

    private java.awt.Rectangle findAlphaBounds(BufferedImage image) {

        int minX = image.getWidth();
        int minY = image.getHeight();
        int maxX = -1;
        int maxY = -1;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) >>> 24) > 12) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        if (maxX < minX || maxY < minY) {
            return new java.awt.Rectangle(0, 0, image.getWidth(), image.getHeight());
        }

        return new java.awt.Rectangle(
                minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    private static final class PieceImageData {
        private final BufferedImage image;
        private final java.awt.Rectangle bounds;

        private PieceImageData(BufferedImage image, java.awt.Rectangle bounds) {
            this.image = image;
            this.bounds = bounds;
        }
    }


    // =========================================================
    // ANIMATION
    // =========================================================

    private void drawAnimations(Graphics2D g2) {

        List<AnimatedPiece> animations =
                ui.getAnimations();

        if (animations == null ||
                animations.isEmpty()) {

            return;
        }

        for (AnimatedPiece ap : animations) {

            if (ap == null ||
                    ap.getPiece() == null) {

                continue;
            }

            if (ap.getPiece().getImage() == null) {
                continue;
            }


            if (board.getBoardView() ==
                    BoardView.TWO_D) {

                int tile =
                        layout.getTileSize();

                g2.drawImage(
                        ap.getPiece().getImage(),
                        (int) Math.round(ap.getX()),
                        (int) Math.round(ap.getY()),
                        tile,
                        tile,
                        board
                );
            }
        }
    }


    // =========================================================
    // CHECK WHETHER PIECE IS ANIMATED
    // =========================================================

    private boolean isAnimated(Piece piece) {

        List<AnimatedPiece> animations =
                ui.getAnimations();

        if (animations == null) {
            return false;
        }

        for (AnimatedPiece ap : animations) {

            if (ap != null &&
                    ap.getPiece() == piece) {

                return true;
            }
        }

        return false;
    }


    // =========================================================
    // 2D BOARD
    // =========================================================

    private void draw2DBoard(Graphics2D g2) {

        GameState gameState =
                board.getGameState();

        King checkedKing =
                CheckDetector.getCheckedKing(
                        gameState
                );

        BoardTheme theme =
                ThemeManager.getBoardTheme();

        int tile =
                layout.getTileSize();

        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                int x =
                        layout.getBoardX()
                                + col * tile;

                int y =
                        layout.getBoardY()
                                + row * tile;

                boolean light =
                        (row + col) % 2 == 0;

                Color baseColor =
                        light
                                ? theme.getLight()
                                : theme.getDark();


                GradientPaint gradient =
                        new GradientPaint(
                                x,
                                y,
                                baseColor.brighter(),
                                x + tile,
                                y + tile,
                                baseColor.darker()
                        );

                g2.setPaint(gradient);

                g2.fillRect(
                        x,
                        y,
                        tile,
                        tile
                );


                int edge =
                        Math.max(
                                2,
                                tile / 20
                        );


                // Top highlight

                g2.setColor(
                        new Color(
                                255,
                                255,
                                255,
                                light ? 28 : 18
                        )
                );

                g2.fillRect(
                        x,
                        y,
                        tile,
                        edge
                );


                // Left highlight

                g2.setColor(
                        new Color(
                                255,
                                255,
                                255,
                                light ? 15 : 10
                        )
                );

                g2.fillRect(
                        x,
                        y,
                        edge,
                        tile
                );


                // Bottom shadow

                g2.setColor(
                        new Color(
                                0,
                                0,
                                0,
                                light ? 18 : 25
                        )
                );

                g2.fillRect(
                        x,
                        y + tile - edge,
                        tile,
                        edge
                );


                // Right shadow

                g2.setColor(
                        new Color(
                                0,
                                0,
                                0,
                                light ? 10 : 15
                        )
                );

                g2.fillRect(
                        x + tile - edge,
                        y,
                        edge,
                        tile
                );


                drawLastMoveHighlight(
                        g2,
                        row,
                        col,
                        x,
                        y
                );


                drawSelectedPieceHighlight(
                        g2,
                        row,
                        col,
                        x,
                        y
                );


                // Hover

                if (row == ui.getHoverRow()
                        && col == ui.getHoverCol()) {

                    g2.setColor(
                            new Color(
                                    255,
                                    255,
                                    255,
                                    40
                            )
                    );

                    g2.fillRect(
                            x,
                            y,
                            tile,
                            tile
                    );
                }


                // King in check

                if (checkedKing != null
                        && checkedKing.getRow() == row
                        && checkedKing.getCol() == col) {

                    g2.setColor(
                            new Color(
                                    255,
                                    0,
                                    0,
                                    120
                            )
                    );

                    g2.fillRect(
                            x,
                            y,
                            tile,
                            tile
                    );
                }


                drawTrainingHints(
                        g2,
                        row,
                        col,
                        x,
                        y,
                        tile
                );
            }
        }
    }


    // =========================================================
// 3D BOARD
// =========================================================


    // =========================================================
    // REAL 3D BOARD
    // =========================================================

    private void draw3DBoard(Graphics2D g2) {

        BoardTheme theme =
                ThemeManager.getBoardTheme();

        int tile =
                layout.getTileSize();

        Point2D.Double[] outer =
                projection.getBoardCorners();

        if (outer == null ||
                outer.length < 4) {

            return;
        }

        Polygon outerPolygon =
                createPolygon(outer);

        if (outerPolygon.npoints < 4) {
            return;
        }

        // =====================================================
        // 1. BOARD SHADOW
        // =====================================================

        drawRealBoardShadow(
                g2,
                outerPolygon,
                tile
        );

        // =====================================================
        // 2. PHYSICAL BOARD THICKNESS
        // =====================================================

        drawRealBoardThickness(
                g2,
                outer,
                tile
        );

        // =====================================================
        // 3. OUTER WOODEN FRAME
        // =====================================================

        drawReferenceWoodFrame(
                g2,
                outer,
                tile
        );

        // =========================================================
// CONTINUOUS PLAYING SURFACE BASE
// =========================================================

        Point2D.Double[] surfacePoints =
                insetPolygon(
                        outer,
                        Math.max(
                                10.0,
                                tile * 0.095
                        )
                );

        Polygon surfacePolygon =
                createPolygon(surfacePoints);

        if (surfacePolygon != null &&
                surfacePolygon.npoints >= 4) {

            /*
             * Dark wooden bed underneath all 64 squares.
             * This prevents visual holes between squares.
             */
            g2.setColor(
                    new Color(
                            54,
                            28,
                            12,
                            255
                    )
            );

            g2.fillPolygon(surfacePolygon);
        }

        // =========================================================
// POLISHED INNER LIP
// =========================================================

        Point2D.Double[] innerLipPoints =
                insetPolygon(
                        outer,
                        Math.max(
                                6.0,
                                tile * 0.055
                        )
                );

        Polygon innerLip =
                createPolygon(innerLipPoints);

        if (innerLip != null &&
                innerLip.npoints >= 4) {

            g2.setColor(
                    new Color(
                            232,
                            190,
                            118,
                            95
                    )
            );

            g2.setStroke(
                    new BasicStroke(
                            Math.max(
                                    1.2f,
                                    tile * 0.010f
                            )
                    )
            );

            g2.drawPolygon(innerLip);
        }

        // =====================================================
        // 4. INNER FRAME HIGHLIGHT
        // =====================================================

        Point2D.Double[] innerFramePoints =
                insetPolygon(
                        outer,
                        Math.max(
                                3.0,
                                tile * 0.035
                        )
                );

        Polygon innerFrame =
                createPolygon(
                        innerFramePoints
                );

        if (innerFrame.npoints >= 4) {

            g2.setColor(
                    new Color(
                            255,
                            215,
                            140,
                            100
                    )
            );

            g2.setStroke(
                    new BasicStroke(
                            Math.max(
                                    1.2f,
                                    tile * 0.010f
                            )
                    )
            );

            g2.drawPolygon(innerFrame);
        }

        // =====================================================
        // 5. PLAYING SURFACE
        // =====================================================

        /*
         * The reference board uses a continuous playing surface.
         *
         * Do NOT inset every individual square heavily.
         * Large per-square insets make the board look like
         * separate floating tiles.
         */
        double frameInset =
                Math.max(
                        1.0,
                        tile * 0.006
                );

        for (int row = 0;
             row < 8;
             row++) {

            for (int col = 0;
                 col < 8;
                 col++) {

                Point2D.Double[] points =
                        projection.getSquare(
                                row,
                                col
                        );

                if (points == null ||
                        points.length < 4) {

                    continue;
                }

                Point2D.Double[] insetPoints =
                        insetPolygon(
                                points,
                                frameInset
                        );

                Polygon square =
                        createPolygon(
                                insetPoints
                        );

                if (square.npoints < 4) {
                    continue;
                }

                boolean light =
                        (row + col) % 2 == 0;

                Color base =
                        light
                                ? theme.getLight()
                                : theme.getDark();

                /*
                 * Draw each square as a physical wooden surface.
                 */
                drawWoodSquare(
                        g2,
                        square,
                        base,
                        row,
                        col
                );

                /*
                 * Subtle engraved seam.
                 */
                /*
                 * Very subtle square seam.
                 *
                 * The squares should read as one wooden chessboard,
                 * not as 64 separate blocks.
                 */
                g2.setColor(
                        new Color(
                                45,
                                24,
                                12,
                                48
                        )
                );

                g2.setStroke(
                        new BasicStroke(
                                Math.max(
                                        0.7f,
                                        tile * 0.0045f
                                )
                        )
                );

                g2.drawPolygon(square);

                /*
                 * Real 3D square effects are deliberately kept
                 * on the square itself. No second board is drawn.
                 */
                draw3DSquareEffects(
                        g2,
                        row,
                        col,
                        insetPoints,
                        tile
                );
            }
        }

        // =====================================================
        // 6. FRAME EDGE
        // =====================================================

        g2.setColor(
                new Color(
                        238,
                        195,
                        125,
                        205
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.020f
                        )
                )
        );

        g2.drawPolygon(outerPolygon);

        /*
         * Dark lower/right edge gives the frame a crisp physical
         * separation from the table.
         */
        g2.setColor(
                new Color(
                        45,
                        22,
                        8,
                        180
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.018f
                        )
                )
        );

        g2.drawLine(
                outerPolygon.xpoints[3],
                outerPolygon.ypoints[3],
                outerPolygon.xpoints[2],
                outerPolygon.ypoints[2]
        );

        g2.drawLine(
                outerPolygon.xpoints[2],
                outerPolygon.ypoints[2],
                outerPolygon.xpoints[1],
                outerPolygon.ypoints[1]
        );
    }


    // =========================================================
    // BOARD SHADOW
    // =========================================================

    private void drawRealBoardShadow(
            Graphics2D g2,
            Polygon boardPolygon,
            int tile) {

        if (boardPolygon == null ||
                boardPolygon.npoints < 4) {

            return;
        }

        /*
         * Draw progressively smaller/stronger offsets from the
         * outside inward. This creates a soft contact shadow
         * without duplicating the board itself.
         */
        for (int step = 28;
             step >= 2;
             step -= 2) {

            Polygon shadow =
                    offsetPolygon(
                            boardPolygon,
                            Math.max(
                                    1,
                                    step / 4
                            ),
                            Math.max(
                                    3,
                                    tile / 12
                                            + step / 2
                            )
                    );

            int alpha =
                    Math.max(
                            2,
                            34 - step
                    );

            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            alpha
                    )
            );

            g2.fillPolygon(shadow);
        }
    }


    // =========================================================
    // PHYSICAL BOARD THICKNESS
    // =========================================================

    private void drawRealBoardThickness(
            Graphics2D g2,
            Point2D.Double[] top,
            int tile) {

        if (top == null ||
                top.length < 4) {

            return;
        }

        Point2D.Double[] bottom =
                projection.getBoardBottomCorners();

        if (bottom == null ||
                bottom.length < 4) {

            /*
             * Compatibility fallback.
             */
            drawTopDownBoardThickness(
                    g2,
                    top,
                    Math.max(
                            8,
                            (int) Math.round(
                                    tile * 0.08
                            )
                    )
            );

            return;
        }

        // =====================================================
        // FRONT FACE
        // =====================================================

        Polygon front =
                quad(
                        top[3],
                        top[2],
                        bottom[2],
                        bottom[3]
                );

        GradientPaint frontPaint =
                new GradientPaint(
                        0,
                        (float) top[3].y,
                        new Color(
                                145,
                                79,
                                30
                        ),
                        0,
                        (float) bottom[3].y,
                        new Color(
                                47,
                                20,
                                7
                        )
                );

        g2.setPaint(frontPaint);
        g2.fillPolygon(front);

        // =====================================================
        // RIGHT FACE
        // =====================================================

        Polygon right =
                quad(
                        top[2],
                        top[1],
                        bottom[1],
                        bottom[2]
                );

        GradientPaint rightPaint =
                new GradientPaint(
                        (float) top[1].x,
                        0,
                        new Color(
                                108,
                                56,
                                21
                        ),
                        (float) top[2].x,
                        0,
                        new Color(
                                37,
                                15,
                                5
                        )
                );

        g2.setPaint(rightPaint);
        g2.fillPolygon(right);

        // =====================================================
        // LEFT FACE
        // =====================================================

        Polygon left =
                quad(
                        top[0],
                        top[3],
                        bottom[3],
                        bottom[0]
                );

        g2.setColor(
                new Color(
                        75,
                        36,
                        13,
                        220
                )
        );

        g2.fillPolygon(left);

        // =====================================================
        // BOTTOM EDGE
        // =====================================================

        g2.setColor(
                new Color(
                        24,
                        10,
                        3,
                        230
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.018f
                        )
                )
        );

        g2.drawLine(
                (int) Math.round(bottom[3].x),
                (int) Math.round(bottom[3].y),
                (int) Math.round(bottom[2].x),
                (int) Math.round(bottom[2].y)
        );

        g2.drawLine(
                (int) Math.round(bottom[2].x),
                (int) Math.round(bottom[2].y),
                (int) Math.round(bottom[1].x),
                (int) Math.round(bottom[1].y)
        );
    }


    private Polygon quad(
            Point2D.Double a,
            Point2D.Double b,
            Point2D.Double c,
            Point2D.Double d) {

        Polygon p =
                new Polygon();

        if (a != null) {
            p.addPoint(
                    (int) Math.round(a.x),
                    (int) Math.round(a.y)
            );
        }

        if (b != null) {
            p.addPoint(
                    (int) Math.round(b.x),
                    (int) Math.round(b.y)
            );
        }

        if (c != null) {
            p.addPoint(
                    (int) Math.round(c.x),
                    (int) Math.round(c.y)
            );
        }

        if (d != null) {
            p.addPoint(
                    (int) Math.round(d.x),
                    (int) Math.round(d.y)
            );
        }

        return p;
    }

    // =========================================================
// WOOD CHESS SQUARE
// =========================================================

    private void drawWoodSquare(
            Graphics2D g2,
            Polygon square,
            Color base,
            int row,
            int col) {

        if (square == null ||
                square.npoints < 4) {

            return;
        }


        // =========================================================
        // BASE WOOD GRADIENT
        // =========================================================

        Rectangle bounds =
                square.getBounds();

        GradientPaint wood =
                new GradientPaint(
                        bounds.x,
                        bounds.y,
                        base.brighter(),
                        bounds.x + bounds.width,
                        bounds.y + bounds.height,
                        base.darker()
                );

        g2.setPaint(wood);

        g2.fillPolygon(square);


        // =========================================================
        // TOP LIGHT
        // =========================================================

        Polygon highlight =
                insetPolygonToPolygon(
                        square,
                        Math.max(
                                1,
                                (int)
                                        Math.round(
                                                bounds.width
                                                        * 0.025
                                        )
                        )
                );

        g2.setColor(
                new Color(
                        255,
                        255,
                        255,
                        18
                )
        );

        g2.fillPolygon(highlight);


        // =========================================================
        // WOOD GRAIN
        // =========================================================

        drawSquareWoodGrain(
                g2,
                square,
                row,
                col
        );

        // =========================================================
// POLISHED WOOD SURFACE
// =========================================================

        Rectangle surface =
                square.getBounds();

        GradientPaint polish =
                new GradientPaint(
                        surface.x,
                        surface.y,
                        new Color(
                                255,
                                245,
                                220,
                                28
                        ),
                        surface.x,
                        surface.y + surface.height,
                        new Color(
                                80,
                                35,
                                12,
                                10
                        )
                );

        g2.setPaint(polish);

        g2.fillPolygon(square);
    }

    // =========================================================
// SQUARE BEVEL
// =========================================================

    private void drawSquareBevel(
            Graphics2D g2,
            Point2D.Double[] points,
            boolean lightSquare) {

        if (points == null ||
                points.length < 4) {

            return;
        }


        // -----------------------------------------------------
        // TOP / LEFT HIGHLIGHT
        // -----------------------------------------------------

        Path2D.Double highlight =
                new Path2D.Double();

        highlight.moveTo(
                points[0].x,
                points[0].y
        );

        highlight.lineTo(
                points[1].x,
                points[1].y
        );

        highlight.lineTo(
                points[1].x - 1.5,
                points[1].y + 1.5
        );

        highlight.lineTo(
                points[0].x + 1.5,
                points[0].y + 1.5
        );

        highlight.closePath();

        g2.setColor(
                new Color(
                        255,
                        248,
                        230,
                        lightSquare ? 80 : 35
                )
        );

        g2.fill(highlight);


        // -----------------------------------------------------
        // BOTTOM / RIGHT SHADOW
        // -----------------------------------------------------

        Path2D.Double shadow =
                new Path2D.Double();

        shadow.moveTo(
                points[2].x,
                points[2].y
        );

        shadow.lineTo(
                points[3].x,
                points[3].y
        );

        shadow.lineTo(
                points[3].x + 1.5,
                points[3].y - 1.5
        );

        shadow.lineTo(
                points[2].x - 1.5,
                points[2].y - 1.5
        );

        shadow.closePath();

        g2.setColor(
                new Color(
                        25,
                        14,
                        8,
                        lightSquare ? 45 : 70
                )
        );

        g2.fill(shadow);
    }

    // =========================================================
// REALISTIC WOOD BOARD THICKNESS
// =========================================================

    private void draw3DBoardThickness(
            Graphics2D g2,
            Point2D.Double[] outer,
            int depth) {

        if (outer == null ||
                outer.length < 4) {

            return;
        }


        int tile =
                layout.getTileSize();


        // =====================================================
        // FRONT SIDE
        // =====================================================

        Polygon front =
                new Polygon();


        front.addPoint(
                (int) Math.round(outer[3].x),
                (int) Math.round(outer[3].y)
        );


        front.addPoint(
                (int) Math.round(outer[2].x),
                (int) Math.round(outer[2].y)
        );


        front.addPoint(
                (int) Math.round(outer[2].x),
                (int) Math.round(
                        outer[2].y + depth
                )
        );


        front.addPoint(
                (int) Math.round(outer[3].x),
                (int) Math.round(
                        outer[3].y + depth
                )
        );


        GradientPaint frontWood =
                new GradientPaint(
                        0,
                        (float) outer[3].y,
                        new Color(
                                157,
                                91,
                                38
                        ),
                        0,
                        (float)
                                (
                                        outer[3].y
                                                + depth
                                ),
                        new Color(
                                55,
                                25,
                                8
                        )
                );


        g2.setPaint(frontWood);

        g2.fillPolygon(front);


        // =====================================================
        // FRONT GRAIN
        // =====================================================

        g2.setClip(front);

        for (
                int y =
                (int) outer[3].y;
                y <
                        outer[3].y + depth;
                y += Math.max(3, tile / 12)) {

            g2.setColor(
                    new Color(
                            30,
                            12,
                            4,
                            55
                    )
            );


            g2.setStroke(
                    new BasicStroke(
                            Math.max(
                                    1f,
                                    tile * 0.006f
                            )
                    )
            );


            g2.drawLine(
                    (int) outer[3].x,
                    y,
                    (int) outer[2].x,
                    y
            );
        }


        g2.setClip(null);


        // =====================================================
        // RIGHT SIDE
        // =====================================================

        Polygon right =
                new Polygon();


        right.addPoint(
                (int) Math.round(outer[2].x),
                (int) Math.round(outer[2].y)
        );


        right.addPoint(
                (int) Math.round(outer[1].x),
                (int) Math.round(outer[1].y)
        );


        right.addPoint(
                (int) Math.round(outer[1].x),
                (int) Math.round(
                        outer[1].y + depth
                )
        );


        right.addPoint(
                (int) Math.round(outer[2].x),
                (int) Math.round(
                        outer[2].y + depth
                )
        );


        GradientPaint sideWood =
                new GradientPaint(
                        (float) outer[1].x,
                        0,
                        new Color(
                                125,
                                66,
                                27
                        ),
                        (float) outer[2].x,
                        0,
                        new Color(
                                43,
                                19,
                                6
                        )
                );


        g2.setPaint(sideWood);

        g2.fillPolygon(right);


        // =====================================================
        // SIDE GRAIN
        // =====================================================

        g2.setClip(right);

        for (
                int x =
                (int) outer[1].x;
                x <
                        outer[2].x;
                x += Math.max(4, tile / 9)) {

            g2.setColor(
                    new Color(
                            30,
                            12,
                            4,
                            42
                    )
            );


            g2.drawLine(
                    x,
                    (int) outer[1].y,
                    x,
                    (int)
                            (
                                    outer[1].y
                                            + depth
                            )
            );
        }


        g2.setClip(null);


        // =====================================================
        // FRONT BOTTOM EDGE
        // =====================================================

        g2.setColor(
                new Color(
                        35,
                        14,
                        4,
                        235
                )
        );


        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.025f
                        )
                )
        );


        g2.drawLine(
                (int) Math.round(outer[3].x),
                (int) Math.round(
                        outer[3].y + depth
                ),
                (int) Math.round(outer[2].x),
                (int) Math.round(
                        outer[2].y + depth
                )
        );


        // =====================================================
        // GOLDEN TOP EDGE
        // =====================================================

        g2.setColor(
                new Color(
                        235,
                        175,
                        92,
                        150
                )
        );


        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.015f
                        )
                )
        );


        g2.drawLine(
                (int) Math.round(outer[3].x),
                (int) Math.round(outer[3].y),
                (int) Math.round(outer[2].x),
                (int) Math.round(outer[2].y)
        );
    }

    // =========================================================
    // 3D SQUARE EFFECTS
    // =========================================================

    private void draw3DSquareEffects(
            Graphics2D g2,
            int row,
            int col,
            Point2D.Double[] points,
            int tile) {

        Polygon square =
                createPolygon(points);

        if (square.npoints < 4) {
            return;
        }


        // =====================================================
        // LAST MOVE
        // =====================================================

        if (isLastMoveSquare(row, col)) {

            boolean destination =
                    isLastMoveDestination(
                            row,
                            col
                    );

            g2.setColor(
                    new Color(
                            0,
                            220,
                            100,
                            destination ? 85 : 50
                    )
            );

            g2.fillPolygon(square);

            g2.setColor(
                    new Color(
                            80,
                            255,
                            140,
                            destination ? 220 : 120
                    )
            );

            g2.setStroke(
                    new BasicStroke(
                            Math.max(
                                    2f,
                                    tile * 0.03f
                            )
                    )
            );

            g2.drawPolygon(square);
        }


        // =====================================================
        // SELECTED PIECE
        // =====================================================

        if (ui.getSelected() != null
                && row == ui.getSelectedRow()
                && col == ui.getSelectedCol()) {

            g2.setColor(
                    new Color(
                            80,
                            220,
                            120,
                            80
                    )
            );

            g2.fillPolygon(square);

            g2.setColor(
                    new Color(
                            80,
                            255,
                            140,
                            210
                    )
            );

            g2.setStroke(
                    new BasicStroke(
                            Math.max(
                                    2f,
                                    tile * 0.03f
                            )
                    )
            );

            g2.drawPolygon(square);
        }


        // =====================================================
        // HOVER
        // =====================================================

        if (row == ui.getHoverRow()
                && col == ui.getHoverCol()) {

            g2.setColor(
                    new Color(
                            255,
                            255,
                            255,
                            40
                    )
            );

            g2.fillPolygon(square);
        }


        // =====================================================
        // KING IN CHECK
        // =====================================================

        GameState gameState =
                board.getGameState();

        King checkedKing =
                CheckDetector.getCheckedKing(
                        gameState
                );

        if (checkedKing != null
                && checkedKing.getRow() == row
                && checkedKing.getCol() == col) {

            g2.setColor(
                    new Color(
                            255,
                            0,
                            0,
                            125
                    )
            );

            g2.fillPolygon(square);
        }


        // =====================================================
        // TRAINING
        // =====================================================

        draw3DTrainingHints(
                g2,
                row,
                col,
                points
        );
    }


    // =========================================================
    // 3D TOP EDGE
    // =========================================================

    private void draw3DTopEdge(
            Graphics2D g2,
            Point2D.Double[] points,
            Color base) {

        if (points == null || points.length < 4) return;

        int width = Math.max(1, (int) Math.round(layout.getTileSize() * 0.018));
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(255, 235, 195, 42));
        g2.drawLine((int) Math.round(points[0].x), (int) Math.round(points[0].y),
                (int) Math.round(points[1].x), (int) Math.round(points[1].y));
    }


    // =========================================================
    // 3D BOTTOM EDGE
    // =========================================================

    private void draw3DBottomEdge(
            Graphics2D g2,
            Point2D.Double[] points,
            Color base) {

        if (points == null || points.length < 4) return;

        int width = Math.max(1, (int) Math.round(layout.getTileSize() * 0.018));
        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(35, 15, 5, 48));
        g2.drawLine((int) Math.round(points[3].x), (int) Math.round(points[3].y),
                (int) Math.round(points[2].x), (int) Math.round(points[2].y));
    }


    // =========================================================
    // PROCEDURAL WOOD GRAIN
    // =========================================================

    private void drawWoodGrain(
            Graphics2D g2,
            Polygon frame,
            int tile) {

        if (frame == null ||
                frame.npoints < 4) {

            return;
        }


        Rectangle bounds =
                frame.getBounds();


        g2.setClip(frame);


        /*
         * Deterministic grain.
         *
         * It does not move when the screen repaints.
         */
        long seed =
                918273L;


        for (int i = 0;
             i < 28;
             i++) {

            seed =
                    seed * 1103515245L
                            + 12345L;


            int offset =
                    (int)
                            (
                                    Math.abs(seed)
                                            % Math.max(
                                            1,
                                            bounds.height
                                    )
                            );


            int y =
                    bounds.y + offset;


            int wave =
                    Math.max(
                            4,
                            tile / 8
                    );


            Path2D.Double grain =
                    new Path2D.Double();


            grain.moveTo(
                    bounds.x,
                    y
            );


            for (int x =
                 bounds.x;
                 x <= bounds.x
                         + bounds.width;
                 x += wave) {

                double curve =
                        Math.sin(
                                x * 0.035
                                        + i
                        )
                                * tile
                                * 0.025;


                grain.lineTo(
                        x,
                        y + curve
                );
            }


            g2.setColor(
                    new Color(
                            75,
                            40,
                            18,
                            16
                    )
            );

            g2.setStroke(
                    new BasicStroke(
                            Math.max(
                                    1f,
                                    tile * 0.008f
                            )
                    )
            );

            g2.draw(grain);
        }


        g2.setClip(null);
    }

    private Polygon insetPolygonToPolygon(
            Polygon original,
            int amount) {

        if (original == null ||
                original.npoints < 3) {

            return new Polygon();
        }


        double centerX = 0;
        double centerY = 0;


        for (int i = 0;
             i < original.npoints;
             i++) {

            centerX +=
                    original.xpoints[i];

            centerY +=
                    original.ypoints[i];
        }


        centerX /=
                original.npoints;

        centerY /=
                original.npoints;


        Polygon result =
                new Polygon();


        for (int i = 0;
             i < original.npoints;
             i++) {

            double dx =
                    original.xpoints[i]
                            - centerX;

            double dy =
                    original.ypoints[i]
                            - centerY;


            double length =
                    Math.sqrt(
                            dx * dx
                                    + dy * dy
                    );


            if (length < 0.001) {

                result.addPoint(
                        original.xpoints[i],
                        original.ypoints[i]
                );

                continue;
            }


            double scale =
                    Math.max(
                            0.0,
                            (
                                    length
                                            - amount
                            )
                                    / length
                    );


            result.addPoint(
                    (int)
                            Math.round(
                                    centerX
                                            + dx * scale
                            ),
                    (int)
                            Math.round(
                                    centerY
                                            + dy * scale
                            )
            );
        }


        return result;
    }

    private void drawSquareWoodGrain(
            Graphics2D g2,
            Polygon square,
            int row,
            int col) {

        if (square == null ||
                square.npoints < 4) {

            return;
        }


        Rectangle bounds =
                square.getBounds();


        g2.setClip(square);


        /*
         * Different grain orientation per square prevents
         * the board from looking like a repeated texture.
         */
        double phase =
                (
                        row * 17.0
                                + col * 31.0
                );


        for (int i = 0;
             i < 5;
             i++) {

            int y =
                    bounds.y
                            + (i + 1)
                            * bounds.height
                            / 6;


            Path2D.Double grain =
                    new Path2D.Double();


            grain.moveTo(
                    bounds.x,
                    y
            );


            for (int x =
                 bounds.x;
                 x <= bounds.x + bounds.width;
                 x += Math.max(
                         5,
                         bounds.width / 8
                 )) {

                double curve =
                        Math.sin(
                                x * 0.04
                                        + phase
                                        + i
                        )
                                * bounds.height
                                * 0.035;


                grain.lineTo(
                        x,
                        y + curve
                );
            }


            g2.setColor(
                    new Color(
                            70,
                            38,
                            17,
                            18
                    )
            );

            g2.setStroke(
                    new BasicStroke(
                            Math.max(
                                    1f,
                                    bounds.width
                                            * 0.006f
                            )
                    )
            );

            g2.draw(grain);
        }


        g2.setClip(null);
    }

    // =========================================================
    // LEGAL MOVES
    // =========================================================

    private void drawLegalMoves(Graphics2D g2) {

        if (ui.getSelected() == null) {
            return;
        }

        List<Move> legalMoves =
                ui.getLegalMoves();

        if (legalMoves == null ||
                legalMoves.isEmpty()) {

            return;
        }


        int[][] squareType =
                new int[8][8];


        for (Move move : legalMoves) {

            if (move == null) {
                continue;
            }

            int row =
                    move.getToRow();

            int col =
                    move.getToCol();

            if (row < 0 || row >= 8 ||
                    col < 0 || col >= 8) {

                continue;
            }

            if (move.getCapturedPiece() != null) {

                squareType[row][col] = 2;

            } else if (squareType[row][col] == 0) {

                squareType[row][col] = 1;
            }
        }


        for (int row = 0; row < 8; row++) {

            for (int col = 0; col < 8; col++) {

                if (squareType[row][col] == 0) {
                    continue;
                }

                boolean capture =
                        squareType[row][col] == 2;


                if (board.getBoardView() ==
                        BoardView.THREE_D) {

                    Point2D.Double[] points =
                            projection.getSquare(
                                    row,
                                    col
                            );

                    if (points == null ||
                            points.length < 4) {

                        continue;
                    }

                    /*
                     * Match the visual playing surface.
                     */
                    double inset =
                            Math.max(
                                    2.0,
                                    layout.getTileSize()
                                            * 0.012
                            );

                    Point2D.Double[] insetPoints =
                            insetPolygon(
                                    points,
                                    inset
                            );

                    Polygon square =
                            createPolygon(
                                    insetPoints
                            );

                    draw3DLegalSquareGlow(
                            g2,
                            square,
                            capture
                    );

                } else {

                    int tile =
                            layout.getTileSize();

                    int x =
                            layout.getBoardX()
                                    + col * tile;

                    int y =
                            layout.getBoardY()
                                    + row * tile;

                    drawLegalSquareGlow(
                            g2,
                            x,
                            y,
                            tile,
                            capture
                    );
                }
            }
        }
    }


    // =========================================================
    // 2D LEGAL MOVE GLOW
    // =========================================================

    private void drawLegalSquareGlow(
            Graphics2D g2,
            int x,
            int y,
            int tile,
            boolean capture) {

        /*
         * Strong, clean 2D legal-move indicator.
         *
         * Green  = normal legal destination
         * Red    = capture
         */

        Color glow =
                capture
                        ? new Color(
                        255,
                        70,
                        90
                )
                        : new Color(
                        55,
                        235,
                        125
                );

        int margin =
                Math.max(
                        3,
                        tile / 14
                );

        int size =
                tile - margin * 2;


        // =====================================================
        // SOFT OUTER GLOW
        // =====================================================

        for (int i = 5; i >= 1; i--) {

            int alpha =
                    10 + (6 - i) * 7;

            g2.setColor(
                    new Color(
                            glow.getRed(),
                            glow.getGreen(),
                            glow.getBlue(),
                            alpha
                    )
            );

            g2.fillRect(
                    x + margin - i,
                    y + margin - i,
                    size + i * 2,
                    size + i * 2
            );
        }


        // =====================================================
        // MAIN HIGHLIGHT
        // =====================================================

        g2.setColor(
                new Color(
                        glow.getRed(),
                        glow.getGreen(),
                        glow.getBlue(),
                        105
                )
        );

        g2.fillRect(
                x + margin,
                y + margin,
                size,
                size
        );


        // =====================================================
        // INNER LIGHT
        // =====================================================

        int inner =
                Math.max(
                        7,
                        tile / 5
                );

        g2.setColor(
                new Color(
                        255,
                        255,
                        255,
                        38
                )
        );

        g2.fillRect(
                x + inner,
                y + inner,
                Math.max(
                        1,
                        tile - inner * 2
                ),
                Math.max(
                        1,
                        tile - inner * 2
                )
        );


        // =====================================================
        // CLEAR BORDER
        // =====================================================

        g2.setColor(
                new Color(
                        glow.getRed(),
                        glow.getGreen(),
                        glow.getBlue(),
                        225
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.025f
                        )
                )
        );

        g2.drawRect(
                x + margin,
                y + margin,
                Math.max(
                        1,
                        size
                ),
                Math.max(
                        1,
                        size
                )
        );


        // =====================================================
        // CENTER DOT
        // =====================================================

        int radius =
                Math.max(
                        5,
                        (int) (
                                tile * 0.075
                        )
                );

        int centerX =
                x + tile / 2;

        int centerY =
                y + tile / 2;

        g2.setColor(
                new Color(
                        glow.getRed(),
                        glow.getGreen(),
                        glow.getBlue(),
                        220
                )
        );

        g2.fillOval(
                centerX - radius,
                centerY - radius,
                radius * 2,
                radius * 2
        );

        g2.setColor(
                new Color(
                        255,
                        255,
                        255,
                        100
                )
        );

        int innerRadius =
                Math.max(
                        2,
                        radius / 2
                );

        g2.fillOval(
                centerX - innerRadius,
                centerY - innerRadius,
                innerRadius * 2,
                innerRadius * 2
        );
    }

    // =========================================================
    // 3D LEGAL MOVE GLOW
    // =========================================================

    private void draw3DLegalSquareGlow(
            Graphics2D g2,
            Polygon square,
            boolean capture) {

        if (square == null ||
                square.npoints < 4) {

            return;
        }

        Color glow =
                capture
                        ? new Color(
                        255,
                        70,
                        120
                )
                        : new Color(
                        70,
                        255,
                        145
                );


        for (int i = 6; i >= 1; i--) {

            int alpha =
                    Math.min(
                            5 + (6 - i) * 5,
                            35
                    );

            Polygon expanded =
                    expandPolygon(
                            square,
                            i
                    );

            g2.setColor(
                    new Color(
                            glow.getRed(),
                            glow.getGreen(),
                            glow.getBlue(),
                            alpha
                    )
            );

            g2.fillPolygon(expanded);
        }


        g2.setColor(
                new Color(
                        glow.getRed(),
                        glow.getGreen(),
                        glow.getBlue(),
                        70
                )
        );

        g2.fillPolygon(square);


        g2.setColor(
                new Color(
                        glow.getRed(),
                        glow.getGreen(),
                        glow.getBlue(),
                        210
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                layout.getTileSize()
                                        * 0.025f
                        )
                )
        );

        g2.drawPolygon(square);


        double centerX = 0;
        double centerY = 0;

        for (int i = 0;
             i < square.npoints;
             i++) {

            centerX += square.xpoints[i];
            centerY += square.ypoints[i];
        }

        centerX /=
                square.npoints;

        centerY /=
                square.npoints;


        int radius =
                Math.max(
                        5,
                        (int)
                                (layout.getTileSize()
                                        * 0.075)
                );


        g2.setColor(
                new Color(
                        glow.getRed(),
                        glow.getGreen(),
                        glow.getBlue(),
                        175
                )
        );

        g2.fillOval(
                (int) centerX - radius,
                (int) centerY - radius,
                radius * 2,
                radius * 2
        );


        int innerRadius =
                Math.max(
                        2,
                        radius / 2
                );

        g2.setColor(
                new Color(
                        255,
                        255,
                        255,
                        80
                )
        );

        g2.fillOval(
                (int) centerX - innerRadius,
                (int) centerY - innerRadius,
                innerRadius * 2,
                innerRadius * 2
        );
    }


    // =========================================================
    // LAST MOVE
    // =========================================================

    private boolean isLastMoveSquare(
            int row,
            int col) {

        Move move =
                board.getLastMove();

        if (move == null) {
            return false;
        }

        return
                (row == move.getFromRow()
                        && col == move.getFromCol())
                        ||
                        (row == move.getToRow()
                                && col == move.getToCol());
    }


    private boolean isLastMoveDestination(
            int row,
            int col) {

        Move move =
                board.getLastMove();

        return move != null
                && row == move.getToRow()
                && col == move.getToCol();
    }


    private void drawLastMoveHighlight(
            Graphics2D g2,
            int row,
            int col,
            int x,
            int y) {

        if (!isLastMoveSquare(row, col)) {
            return;
        }

        int tile =
                layout.getTileSize();

        boolean destination =
                isLastMoveDestination(
                        row,
                        col
                );


        g2.setColor(
                new Color(
                        0,
                        220,
                        100,
                        destination ? 75 : 45
                )
        );

        g2.fillRect(
                x,
                y,
                tile,
                tile
        );


        g2.setColor(
                new Color(
                        80,
                        255,
                        140,
                        destination ? 220 : 100
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.03f
                        )
                )
        );

        g2.drawRect(
                x + 2,
                y + 2,
                Math.max(1, tile - 4),
                Math.max(1, tile - 4)
        );
    }


    // =========================================================
    // SELECTED PIECE
    // =========================================================

    private void drawSelectedPieceHighlight(
            Graphics2D g2,
            int row,
            int col,
            int x,
            int y) {

        if (ui.getSelected() == null) {
            return;
        }

        if (row != ui.getSelectedRow()
                || col != ui.getSelectedCol()) {

            return;
        }

        int tile =
                layout.getTileSize();


        g2.setColor(
                new Color(
                        80,
                        180,
                        255,
                        70
                )
        );

        g2.fillRect(
                x,
                y,
                tile,
                tile
        );


        g2.setColor(
                new Color(
                        80,
                        200,
                        255,
                        220
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.04f
                        )
                )
        );

        g2.drawRect(
                x + 2,
                y + 2,
                Math.max(1, tile - 4),
                Math.max(1, tile - 4)
        );
    }


    // =========================================================
    // TRAINING HINTS 2D
    // =========================================================

    private void drawTrainingHints(
            Graphics2D g2,
            int row,
            int col,
            int x,
            int y,
            int tile) {

        if (board.getGameState().getGameMode()
                != GameMode.TRAINING) {

            return;
        }


        if (row == ui.getHintFromRow()
                && col == ui.getHintFromCol()) {

            g2.setColor(
                    new Color(
                            0,
                            100,
                            255,
                            120
                    )
            );

            g2.fillRect(
                    x,
                    y,
                    tile,
                    tile
            );
        }


        if (row == ui.getHintToRow()
                && col == ui.getHintToCol()) {

            g2.setColor(
                    new Color(
                            255,
                            215,
                            0,
                            140
                    )
            );

            g2.fillRect(
                    x,
                    y,
                    tile,
                    tile
            );
        }
    }


    // =========================================================
    // TRAINING HINTS 3D
    // =========================================================

    private void draw3DTrainingHints(
            Graphics2D g2,
            int row,
            int col,
            Point2D.Double[] points) {

        if (board.getGameState().getGameMode()
                != GameMode.TRAINING) {

            return;
        }

        Polygon square =
                createPolygon(points);

        if (square.npoints < 4) {
            return;
        }


        if (row == ui.getHintFromRow()
                && col == ui.getHintFromCol()) {

            g2.setColor(
                    new Color(
                            0,
                            100,
                            255,
                            120
                    )
            );

            g2.fillPolygon(square);
        }


        if (row == ui.getHintToRow()
                && col == ui.getHintToCol()) {

            g2.setColor(
                    new Color(
                            255,
                            215,
                            0,
                            140
                    )
            );

            g2.fillPolygon(square);
        }
    }


    // =========================================================
    // PIECE SHADOW 2D
    // =========================================================

    private void drawPieceShadow(
            Graphics2D g2,
            int x,
            int y,
            int tile) {

        int shadowWidth =
                (int) (tile * 0.58);

        int shadowHeight =
                Math.max(
                        6,
                        tile / 9
                );

        int shadowX =
                x + (tile - shadowWidth) / 2;

        int shadowY =
                y + tile
                        - shadowHeight
                        - tile / 12;


        for (int i = 6; i >= 1; i--) {

            int alpha =
                    Math.min(
                            6 + (6 - i) * 6,
                            40
                    );

            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            alpha
                    )
            );

            g2.fillOval(
                    shadowX - i,
                    shadowY - i / 2,
                    shadowWidth + i * 2,
                    shadowHeight + i
            );
        }
    }


    // =========================================================
    // PIECE SHADOW 3D
    // =========================================================

    private void draw3DPieceShadow(
            Graphics2D g2,
            double centerX,
            double baselineY,
            double squareWidth,
            double squareHeight) {

        int width =
                Math.max(
                        8,
                        (int) Math.round(
                                squareWidth * 0.52
                        )
                );

        int height =
                Math.max(
                        4,
                        (int) Math.round(
                                squareHeight * 0.075
                        )
                );


        int x =
                (int) Math.round(
                        centerX - width / 2.0
                );

        int y =
                (int) Math.round(
                        baselineY - height / 2.0
                );


        // Soft outer shadow

        for (int i = 11; i >= 1; i--) {

            int alpha =
                    Math.max(
                            2,
                            34 - i * 3
                    );

            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            alpha
                    )
            );

            g2.fillOval(
                    x - i,
                    y - i / 2,
                    width + i * 2,
                    height + i
            );
        }


        // Contact shadow

        g2.setColor(
                new Color(
                        0,
                        0,
                        0,
                        55
                )
        );

        g2.fillOval(
                x,
                y,
                width,
                height
        );
    }

    // =========================================================
    // COORDINATES
    // =========================================================

    private void drawCoordinates(Graphics2D g2) {

        int tile =
                layout.getTileSize();

        BoardTheme theme =
                ThemeManager.getBoardTheme();

        g2.setFont(
                g2.getFont().deriveFont(
                        Font.BOLD,
                        Math.max(
                                12f,
                                tile / 6f
                        )
                )
        );


        // =====================================================
        // 3D
        // =====================================================

        if (board.getBoardView() ==
                BoardView.THREE_D) {

            for (int col = 0; col < 8; col++) {

                Point2D.Double point =
                        projection.project(
                                8.02,
                                col + 0.5
                        );

                boolean light =
                        (7 + col) % 2 == 0;

                g2.setColor(
                        light
                                ? theme.getDark()
                                : theme.getLight()
                );


                char letter =
                        (char) ('a' + col);

                g2.drawString(
                        String.valueOf(letter),
                        (int) point.x - 4,
                        (int) point.y + tile / 6
                );
            }


            for (int row = 0; row < 8; row++) {

                Point2D.Double point =
                        projection.project(
                                row + 0.5,
                                -0.08
                        );

                boolean light =
                        row % 2 == 0;

                g2.setColor(
                        light
                                ? theme.getLight()
                                : theme.getDark()
                );


                String number =
                        String.valueOf(
                                8 - row
                        );

                g2.drawString(
                        number,
                        (int) point.x - tile / 5,
                        (int) point.y + 5
                );
            }

            return;
        }


        // =====================================================
        // 2D
        // =====================================================

        for (int col = 0; col < 8; col++) {

            boolean light =
                    (7 + col) % 2 == 0;

            g2.setColor(
                    light
                            ? theme.getDark()
                            : theme.getLight()
            );


            char letter =
                    (char) ('a' + col);

            int x =
                    layout.getBoardX()
                            + col * tile
                            + tile / 2
                            - 4;

            int y =
                    layout.getBoardY()
                            + layout.getBoardSize()
                            + tile / 5;


            g2.drawString(
                    String.valueOf(letter),
                    x,
                    y
            );
        }


        for (int row = 0; row < 8; row++) {

            boolean light =
                    row % 2 == 0;

            g2.setColor(
                    light
                            ? theme.getLight()
                            : theme.getDark()
            );


            String number =
                    String.valueOf(
                            8 - row
                    );


            int x =
                    layout.getBoardX()
                            - tile / 5;

            int y =
                    layout.getBoardY()
                            + row * tile
                            + tile / 2
                            + 5;


            g2.drawString(
                    number,
                    x,
                    y
            );
        }
    }


    // =========================================================
    // CREATE POLYGON
    // =========================================================

    private Polygon createPolygon(
            Point2D.Double[] points) {

        Polygon polygon =
                new Polygon();

        if (points == null) {
            return polygon;
        }

        for (Point2D.Double point : points) {

            if (point == null) {
                continue;
            }

            polygon.addPoint(
                    (int) Math.round(point.x),
                    (int) Math.round(point.y)
            );
        }

        return polygon;
    }


    // =========================================================
    // INSET POLYGON
    // =========================================================

    private Point2D.Double[] insetPolygon(
            Point2D.Double[] original,
            double amount) {

        if (original == null ||
                original.length < 3) {

            return new Point2D.Double[0];
        }


        double centerX = 0;
        double centerY = 0;

        int validPoints = 0;


        for (Point2D.Double point : original) {

            if (point == null) {
                continue;
            }

            centerX += point.x;
            centerY += point.y;
            validPoints++;
        }


        if (validPoints < 3) {
            return new Point2D.Double[0];
        }


        centerX /=
                validPoints;

        centerY /=
                validPoints;


        Point2D.Double[] result =
                new Point2D.Double[
                        original.length
                        ];


        for (int i = 0;
             i < original.length;
             i++) {

            Point2D.Double point =
                    original[i];

            if (point == null) {
                result[i] = null;
                continue;
            }


            double dx =
                    point.x - centerX;

            double dy =
                    point.y - centerY;


            double distance =
                    Math.sqrt(
                            dx * dx +
                                    dy * dy
                    );


            if (distance < 0.0001) {

                result[i] =
                        new Point2D.Double(
                                point.x,
                                point.y
                        );

                continue;
            }


            double scale =
                    Math.max(
                            0.0,
                            (distance - amount)
                                    / distance
                    );


            result[i] =
                    new Point2D.Double(
                            centerX + dx * scale,
                            centerY + dy * scale
                    );
        }


        return result;
    }


    // =========================================================
    // OUTER BOARD
    // =========================================================

    private Point2D.Double[] getOuterBoardPoints() {

        return projection.getBoardCorners();
    }


    // =========================================================
    // OFFSET POLYGON
    // =========================================================

    private Polygon offsetPolygon(
            Polygon original,
            int dx,
            int dy) {

        Polygon result =
                new Polygon();

        if (original == null) {
            return result;
        }


        for (int i = 0;
             i < original.npoints;
             i++) {

            result.addPoint(
                    original.xpoints[i] + dx,
                    original.ypoints[i] + dy
            );
        }

        return result;
    }


    // =========================================================
    // EXPAND POLYGON
    // =========================================================

    private Polygon expandPolygon(
            Polygon original,
            int amount) {

        Polygon result =
                new Polygon();

        if (original == null ||
                original.npoints < 3) {

            return result;
        }


        double centerX = 0;
        double centerY = 0;


        for (int i = 0;
             i < original.npoints;
             i++) {

            centerX +=
                    original.xpoints[i];

            centerY +=
                    original.ypoints[i];
        }


        centerX /=
                original.npoints;

        centerY /=
                original.npoints;


        for (int i = 0;
             i < original.npoints;
             i++) {

            double dx =
                    original.xpoints[i]
                            - centerX;

            double dy =
                    original.ypoints[i]
                            - centerY;


            double length =
                    Math.sqrt(
                            dx * dx +
                                    dy * dy
                    );


            if (length < 0.0001) {

                result.addPoint(
                        original.xpoints[i],
                        original.ypoints[i]
                );

                continue;
            }


            double scale =
                    (length + amount)
                            / length;


            result.addPoint(
                    (int) Math.round(
                            centerX
                                    + dx * scale
                    ),
                    (int) Math.round(
                            centerY
                                    + dy * scale
                    )
            );
        }


        return result;
    }


    // =========================================================
// REALISTIC WOODEN FRAME
// =========================================================

    private void drawWoodFrame(
            Graphics2D g2,
            Point2D.Double[] outer,
            int depth) {

        if (outer == null || outer.length < 4) {
            return;
        }

        Polygon board =
                createPolygon(outer);

        if (board.npoints < 4) {
            return;
        }

        int tile =
                layout.getTileSize();

        // =====================================================
        // BOARD DIMENSIONS
        // =====================================================

        int frameWidth =
                Math.max(
                        8,
                        (int) Math.round(tile * 0.115)
                );

        int outerBevel =
                Math.max(
                        3,
                        (int) Math.round(tile * 0.035)
                );

        int innerGroove =
                Math.max(
                        2,
                        (int) Math.round(tile * 0.018)
                );


        // =====================================================
        // BASE WOOD
        // =====================================================

        GradientPaint baseWood =
                new GradientPaint(
                        0,
                        (float) outer[0].y,
                        new Color(205, 151, 82),
                        0,
                        (float) outer[2].y,
                        new Color(108, 58, 24)
                );

        g2.setPaint(baseWood);
        g2.fillPolygon(board);


        // =====================================================
        // CLIP WOOD GRAIN TO BOARD
        // =====================================================

        java.awt.Shape oldClip =
                g2.getClip();

        g2.clip(board);


        drawWoodGrain(
                g2,
                outer,
                tile
        );


        // Restore previous clipping region.

        g2.setClip(oldClip);


        // =====================================================
        // OUTER DARK BEVEL
        // =====================================================

        g2.setColor(
                new Color(
                        55,
                        27,
                        10,
                        190
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                4f,
                                tile * 0.045f
                        )
                )
        );

        g2.drawPolygon(board);


        // =====================================================
        // OUTER GOLDEN HIGHLIGHT
        // =====================================================

        g2.setColor(
                new Color(
                        244,
                        192,
                        112,
                        210
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.018f
                        )
                )
        );

        drawInsetPolygonOutline(
                g2,
                outer,
                outerBevel,
                new Color(
                        245,
                        195,
                        115,
                        220
                )
        );


        // =====================================================
        // INNER FRAME GROOVE
        // =====================================================

        drawInsetPolygonOutline(
                g2,
                outer,
                frameWidth,
                new Color(
                        57,
                        28,
                        11,
                        220
                )
        );


        // =====================================================
        // INNER GOLDEN BEVEL
        // =====================================================

        drawInsetPolygonOutline(
                g2,
                outer,
                frameWidth + innerGroove,
                new Color(
                        235,
                        177,
                        95,
                        180
                )
        );


        // =====================================================
        // INNER DARK SHADOW
        // =====================================================

        drawInsetPolygonOutline(
                g2,
                outer,
                frameWidth + innerGroove + 3,
                new Color(
                        45,
                        23,
                        10,
                        110
                )
        );


        // =====================================================
        // WOODEN CORNER ACCENTS
        // =====================================================

        drawWoodCornerAccents(
                g2,
                outer,
                tile
        );
    }

    // =========================================================
// WOOD GRAIN
// =========================================================

    private void drawWoodGrain(
            Graphics2D g2,
            Point2D.Double[] outer,
            int tile) {

        if (outer == null || outer.length < 4) {
            return;
        }

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;

        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (Point2D.Double p : outer) {

            if (p == null) {
                continue;
            }

            minX =
                    Math.min(
                            minX,
                            p.x
                    );

            maxX =
                    Math.max(
                            maxX,
                            p.x
                    );

            minY =
                    Math.min(
                            minY,
                            p.y
                    );

            maxY =
                    Math.max(
                            maxY,
                            p.y
                    );
        }


        int grainSpacing =
                Math.max(
                        5,
                        tile / 10
                );


        /*
         * Deterministic pseudo-random grain.
         *
         * It deliberately does not use java.util.Random,
         * so the board looks identical every repaint.
         */
        for (
                int y = (int) minY - tile;
                y < maxY + tile;
                y += grainSpacing) {

            double wave =
                    Math.sin(
                            y * 0.035
                    ) * tile * 0.10;


            Path2D.Double grain =
                    new Path2D.Double();


            grain.moveTo(
                    minX - tile,
                    y
            );


            for (
                    int x = (int) minX - tile;
                    x <= maxX + tile;
                    x += Math.max(4, tile / 5)) {

                double yy =
                        y
                                + Math.sin(
                                x * 0.021
                                        + y * 0.014
                        ) * tile * 0.025
                                + wave;


                grain.lineTo(
                        x,
                        yy
                );
            }


            int darkness =
                    12
                            + Math.abs(
                            (y * 17) % 17
                    );


            g2.setColor(
                    new Color(
                            65,
                            31,
                            10,
                            Math.min(
                                    35,
                                    darkness
                            )
                    )
            );


            g2.setStroke(
                    new BasicStroke(
                            Math.max(
                                    0.6f,
                                    tile * 0.006f
                            )
                    )
            );


            g2.draw(grain);
        }


        // =====================================================
        // LIGHT GRAIN
        // =====================================================

        for (
                int y = (int) minY - tile;
                y < maxY + tile;
                y += grainSpacing * 3) {

            Path2D.Double grain =
                    new Path2D.Double();


            grain.moveTo(
                    minX - tile,
                    y
            );


            for (
                    int x = (int) minX - tile;
                    x <= maxX + tile;
                    x += Math.max(5, tile / 4)) {

                double yy =
                        y
                                + Math.sin(
                                x * 0.017
                                        + y * 0.009
                        ) * tile * 0.035;


                grain.lineTo(
                        x,
                        yy
                );
            }


            g2.setColor(
                    new Color(
                            255,
                            220,
                            150,
                            24
                    )
            );


            g2.setStroke(
                    new BasicStroke(
                            Math.max(
                                    0.5f,
                                    tile * 0.004f
                            )
                    )
            );


            g2.draw(grain);
        }


        // =====================================================
        // NATURAL WOOD KNOTS
        // =====================================================

        drawWoodKnot(
                g2,
                minX + (maxX - minX) * 0.23,
                minY + (maxY - minY) * 0.18,
                tile * 0.20
        );


        drawWoodKnot(
                g2,
                minX + (maxX - minX) * 0.73,
                minY + (maxY - minY) * 0.77,
                tile * 0.15
        );
    }

    // =========================================================
// WOOD KNOT
// =========================================================

    private void drawWoodKnot(
            Graphics2D g2,
            double x,
            double y,
            double size) {

        if (size <= 2) {
            return;
        }


        for (int i = 5; i >= 1; i--) {

            double width =
                    size
                            * (
                            1.0
                                    - i * 0.12
                    );

            double height =
                    width * 0.32;


            Path2D.Double knot =
                    new Path2D.Double();


            knot.moveTo(
                    x - width,
                    y
            );


            knot.curveTo(
                    x - width * 0.55,
                    y - height,
                    x + width * 0.55,
                    y - height,
                    x + width,
                    y
            );


            knot.curveTo(
                    x + width * 0.55,
                    y + height,
                    x - width * 0.55,
                    y + height,
                    x - width,
                    y
            );


            knot.closePath();


            g2.setColor(
                    new Color(
                            65,
                            31,
                            11,
                            12 + i * 4
                    )
            );


            g2.draw(knot);
        }
    }

    // =========================================================
// INSET POLYGON OUTLINE
// =========================================================

    private void drawInsetPolygonOutline(
            Graphics2D g2,
            Point2D.Double[] original,
            double amount,
            Color color) {

        if (original == null ||
                original.length < 4) {

            return;
        }


        Point2D.Double[] inset =
                insetPolygon(
                        original,
                        amount
                );


        Polygon polygon =
                createPolygon(inset);


        if (polygon.npoints < 4) {
            return;
        }


        g2.setColor(color);

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                1f,
                                layout.getTileSize()
                                        * 0.012f
                        )
                )
        );

        g2.drawPolygon(polygon);
    }

    // =========================================================
// WOOD CORNER ACCENTS
// =========================================================

    private void drawWoodCornerAccents(
            Graphics2D g2,
            Point2D.Double[] outer,
            int tile) {

        if (outer == null ||
                outer.length < 4) {

            return;
        }


        int radius =
                Math.max(
                        4,
                        (int) (
                                tile * 0.055
                        )
                );


        // =====================================================
        // CORNER HIGHLIGHTS
        // =====================================================

        for (int i = 0; i < 4; i++) {

            Point2D.Double p =
                    outer[i];


            if (p == null) {
                continue;
            }


            g2.setColor(
                    new Color(
                            255,
                            215,
                            145,
                            85
                    )
            );


            g2.fillOval(
                    (int) p.x - radius / 2,
                    (int) p.y - radius / 2,
                    radius,
                    radius
            );
        }


        // =====================================================
        // CORNER SHADOWS
        // =====================================================

        for (int i = 0; i < 4; i++) {

            Point2D.Double p =
                    outer[i];


            if (p == null) {
                continue;
            }


            g2.setColor(
                    new Color(
                            45,
                            20,
                            7,
                            80
                    )
            );


            g2.fillOval(
                    (int) p.x - radius / 3,
                    (int) p.y - radius / 3,
                    Math.max(
                            2,
                            radius / 2
                    ),
                    Math.max(
                            2,
                            radius / 2
                    )
            );
        }
    }

    // =========================================================
// POINT INTERPOLATION
// =========================================================

    private Point2D.Double interpolate(
            Point2D.Double a,
            Point2D.Double b,
            double t) {

        return new Point2D.Double(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t
        );
    }

    // =========================================================
// BOARD AMBIENT SHADOW
// =========================================================

    private void drawBoardAmbientShadow(
            Graphics2D g2,
            Polygon boardPolygon,
            int depth) {

        if (boardPolygon == null ||
                boardPolygon.npoints < 4) {

            return;
        }

        /*
         * Multiple progressively smaller shadows create
         * a soft physical shadow beneath the wooden board.
         *
         * Use a unique variable name so this method cannot
         * collide with other loops accidentally inserted
         * into the method.
         */
        for (int shadowStep = 24;
             shadowStep >= 2;
             shadowStep -= 2) {

            Polygon shadow =
                    offsetPolygon(
                            boardPolygon,
                            shadowStep / 3,
                            depth + shadowStep
                    );

            g2.setColor(
                    new Color(
                            0,
                            0,
                            0,
                            Math.max(
                                    2,
                                    42 - shadowStep
                            )
                    )
            );

            g2.fillPolygon(shadow);
        }
    }


    // =========================================================
// FINAL FRAME HIGHLIGHT
// =========================================================

    private void drawFinalWoodFrameHighlight(
            Graphics2D g2,
            Polygon outer,
            int tile) {

        if (outer == null ||
                outer.npoints < 4) {

            return;
        }


        // Outer golden edge

        g2.setColor(
                new Color(
                        255,
                        225,
                        180,
                        155
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                1.5f,
                                tile * 0.015f
                        )
                )
        );

        g2.drawPolygon(outer);


        // Dark underside

        g2.setColor(
                new Color(
                        45,
                        23,
                        12,
                        180
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.022f
                        )
                )
        );

        g2.drawLine(
                outer.xpoints[3],
                outer.ypoints[3],
                outer.xpoints[2],
                outer.ypoints[2]
        );

        g2.drawLine(
                outer.xpoints[2],
                outer.ypoints[2],
                outer.xpoints[1],
                outer.ypoints[1]
        );
    }

    // =========================================================
    // DISTANCE
    // =========================================================

    private double distance(
            Point2D.Double a,
            Point2D.Double b) {

        if (a == null || b == null) {
            return 1.0;
        }

        double dx =
                b.x - a.x;

        double dy =
                b.y - a.y;

        return Math.sqrt(
                dx * dx +
                        dy * dy
        );
    }
// =========================================================
// WOODEN GAME ENVIRONMENT
// =========================================================

    private void drawWoodenEnvironment(Graphics2D g2) {

        int width = board.getWidth();
        int height = board.getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        ensureWoodTextures();

        // =====================================================
        // WALNUT TABLE
        // =====================================================

        TexturePaint tablePaint =
                new TexturePaint(
                        tableWoodTexture,
                        new Rectangle(
                                0,
                                0,
                                tableWoodTexture.getWidth(),
                                tableWoodTexture.getHeight()
                        )
                );

        g2.setPaint(tablePaint);

        g2.fillRect(
                0,
                0,
                width,
                height
        );


        // =====================================================
        // LARGE WARM CENTER LIGHT
        // =====================================================

        RadialGradientPaint centerLight =
                new RadialGradientPaint(
                        new Point2D.Double(
                                width * 0.50,
                                height * 0.42
                        ),
                        Math.max(width, height) * 0.70f,
                        new float[]{
                                0.0f,
                                0.38f,
                                0.72f,
                                1.0f
                        },
                        new Color[]{
                                new Color(255, 220, 165, 48),
                                new Color(210, 140, 75, 24),
                                new Color(100, 50, 20, 10),
                                new Color(0, 0, 0, 95)
                        }
                );

        g2.setPaint(centerLight);

        g2.fillRect(
                0,
                0,
                width,
                height
        );


        // =====================================================
        // WOOD PLANK SEAMS
        // =====================================================

        int plankHeight = Math.max(
                70,
                height / 7
        );

        for (int y = 0; y < height; y += plankHeight) {

            g2.setColor(
                    new Color(
                            25,
                            11,
                            5,
                            75
                    )
            );

            g2.fillRect(
                    0,
                    y + plankHeight - 2,
                    width,
                    2
            );

            g2.setColor(
                    new Color(
                            215,
                            145,
                            78,
                            15
                    )
            );

            g2.fillRect(
                    0,
                    y + plankHeight - 4,
                    width,
                    1
            );
        }


        // =====================================================
        // SUBTLE TABLE HIGHLIGHT
        // =====================================================

        GradientPaint topHighlight =
                new GradientPaint(
                        0,
                        0,
                        new Color(255, 220, 170, 25),
                        0,
                        height * 0.45f,
                        new Color(255, 220, 170, 0)
                );

        g2.setPaint(topHighlight);

        g2.fillRect(
                0,
                0,
                width,
                height
        );


        // =====================================================
        // STRONG OUTER VIGNETTE
        // =====================================================

        drawWoodEnvironmentVignette(
                g2,
                width,
                height
        );
    }


    // =========================================================
// CREATE WOOD TEXTURES
// =========================================================

    private void ensureWoodTextures() {

        int scale =
                Math.max(
                        1,
                        Math.min(
                                board.getWidth(),
                                board.getHeight()
                        )
                );

        /*
         * Don't regenerate textures every repaint.
         */
        if (textureScale == scale
                && tableWoodTexture != null
                && lightWoodTexture != null
                && darkWoodTexture != null) {

            return;
        }

        textureScale = scale;

        tableWoodTexture =
                createWoodTexture(
                        420,
                        420,
                        new Color(132, 76, 37),
                        new Color(54, 27, 13),
                        26
                );

        lightWoodTexture =
                createWoodTexture(
                        220,
                        220,
                        new Color(239, 218, 185),
                        new Color(183, 153, 116),
                        12
                );

        darkWoodTexture =
                createWoodTexture(
                        220,
                        220,
                        new Color(105, 73, 48),
                        new Color(52, 34, 23),
                        15
                );
    }

    // =========================================================
// WOOD TEXTURE GENERATOR
// =========================================================

    private BufferedImage createWoodTexture(
            int width,
            int height,
            Color light,
            Color dark,
            int grainCount) {

        BufferedImage image =
                new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D tg =
                image.createGraphics();

        tg.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        Random random =
                new Random(
                        width * 31L
                                + height * 17L
                                + grainCount
                );


        // =====================================================
        // BASE
        // =====================================================

        GradientPaint base =
                new GradientPaint(
                        0,
                        0,
                        light,
                        width,
                        height,
                        dark
                );

        tg.setPaint(base);

        tg.fillRect(
                0,
                0,
                width,
                height
        );


        // =====================================================
        // LONG WOOD GRAIN
        // =====================================================

        for (int i = 0;
             i < grainCount;
             i++) {

            double y =
                    random.nextDouble()
                            * height;

            Path2D.Double grain =
                    new Path2D.Double();

            grain.moveTo(
                    -20,
                    y
            );

            for (int x = 0;
                 x <= width + 20;
                 x += 20) {

                double wave =
                        Math.sin(
                                x * 0.035
                                        + i * 1.7
                        )
                                * (4 + random.nextDouble() * 7);

                grain.lineTo(
                        x,
                        y + wave
                );
            }

            int alpha =
                    18 + random.nextInt(28);

            tg.setColor(
                    new Color(
                            dark.getRed(),
                            dark.getGreen(),
                            dark.getBlue(),
                            alpha
                    )
            );

            tg.setStroke(
                    new BasicStroke(
                            1.0f
                    )
            );

            tg.draw(grain);
        }


        // =====================================================
        // FINE GRAIN
        // =====================================================

        for (int i = 0; i < width / 5; i++) {

            int y =
                    random.nextInt(height);

            int alpha =
                    8 + random.nextInt(15);

            tg.setColor(
                    new Color(
                            dark.getRed(),
                            dark.getGreen(),
                            dark.getBlue(),
                            alpha
                    )
            );

            tg.drawLine(
                    0,
                    y,
                    width,
                    y + random.nextInt(5) - 2
            );
        }


        tg.dispose();

        return image;
    }

    // =========================================================
// WOOD ENVIRONMENT VIGNETTE
// =========================================================

    private void drawWoodEnvironmentVignette(
            Graphics2D g2,
            int width,
            int height) {

        int max =
                Math.max(width, height);

        for (int i = 0; i < 16; i++) {

            int margin =
                    i * Math.max(
                            5,
                            max / 100
                    );

            int alpha =
                    Math.min(
                            4 + i * 2,
                            32
                    );

            g2.setColor(
                    new Color(
                            15,
                            13,
                            11,
                            alpha
                    )
            );

            g2.drawRect(
                    margin,
                    margin,
                    Math.max(
                            1,
                            width - margin * 2
                    ),
                    Math.max(
                            1,
                            height - margin * 2
                    )
            );
        }
    }

    private void drawReferenceWoodFrame(
            Graphics2D g2,
            Point2D.Double[] outer,
            int tile) {

        Polygon frame =
                createPolygon(outer);

        if (frame.npoints < 4) {
            return;
        }


        // =========================================================
        // MAIN WOOD
        // =========================================================

        GradientPaint wood =
                new GradientPaint(
                        0,
                        (float) outer[0].y,
                        new Color(
                                205,
                                157,
                                91
                        ),
                        0,
                        (float) outer[2].y,
                        new Color(
                                116,
                                65,
                                29
                        )
                );

        g2.setPaint(wood);

        g2.fillPolygon(frame);


        // =========================================================
        // INNER DARK BORDER
        // =========================================================

        g2.setColor(
                new Color(
                        72,
                        37,
                        15,
                        190
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                4f,
                                tile * 0.055f
                        )
                )
        );

        g2.drawPolygon(frame);


        // =========================================================
        // GOLDEN WOOD HIGHLIGHT
        // =========================================================

        g2.setColor(
                new Color(
                        240,
                        194,
                        115,
                        170
                )
        );

        g2.setStroke(
                new BasicStroke(
                        Math.max(
                                2f,
                                tile * 0.018f
                        )
                )
        );

        g2.drawPolygon(frame);


        // =========================================================
        // WOOD GRAIN
        // =========================================================

        drawWoodGrain(
                g2,
                frame,
                tile
        );
    }
}
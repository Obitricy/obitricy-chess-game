package chess.board;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * Perspective projection used by the 3D chess board.
 *
 * The projection is deliberately mild so that all 64 squares remain
 * readable while the front of the board is slightly larger than the rear.
 *
 * Coordinate system:
 *   row 0 = rear edge
 *   row 8 = front edge
 *   col 0 = left edge
 *   col 8 = right edge
 *   z    = height above the playing surface
 */
public class Board3DProjection {

    private final BoardLayout layout;

    // =========================================================
// CAMERA
// =========================================================

    /*
     * Closer camera gives the board a stronger and more dramatic
     * 3D perspective.
     */
    private double cameraDistance = 13.5;

    /*
     * Lower elevation increases the visible depth of the board.
     * The rear ranks move farther away while the front ranks
     * become visually larger.
     */
    private double cameraElevation = 48.0;

    /*
     * Straight-on view.
     */
    private double cameraAzimuth = 0.0;

    /*
     * Slightly stronger perspective projection.
     */
    private double focalLength = 16.5;


// =========================================================
// BOARD
// =========================================================

    private double boardWorldSize = 8.0;

    /*
     * Increased thickness makes the board look like a physical
     * wooden chessboard instead of a flat surface.
     */
    private double boardThickness = 0.65;

    /*
     * Enlarges the board inside the available BoardLayout area.
     */
    private double boardScale = 1.05;


// =========================================================
// SCREEN OFFSET
// =========================================================

    private double screenOffsetX = 0.0;

    /*
     * Moves the board slightly upward so the whole 3D board,
     * including its thicker front edge, fits naturally.
     */
    private double screenOffsetY = -25.0;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Board3DProjection(BoardLayout layout) {
        if (layout == null) {
            throw new IllegalArgumentException(
                    "BoardLayout cannot be null"
            );
        }

        this.layout = layout;
    }

    // =========================================================
    // CAMERA DISTANCE
    // =========================================================

    public double getCameraDistance() {
        return cameraDistance;
    }

    public void setCameraDistance(double cameraDistance) {
        this.cameraDistance = Math.max(
                8.0,
                Math.min(60.0, cameraDistance)
        );
    }

    // =========================================================
    // CAMERA ELEVATION
    // =========================================================

    public double getCameraElevation() {
        return cameraElevation;
    }

    public void setCameraElevation(double cameraElevation) {
        this.cameraElevation = Math.max(
                35.0,
                Math.min(89.0, cameraElevation)
        );
    }

    // =========================================================
    // CAMERA AZIMUTH
    // =========================================================

    public double getCameraAzimuth() {
        return cameraAzimuth;
    }

    public void setCameraAzimuth(double cameraAzimuth) {
        this.cameraAzimuth = cameraAzimuth;
    }

    // =========================================================
    // FOCAL LENGTH
    // =========================================================

    public double getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(double focalLength) {
        this.focalLength = Math.max(
                8.0,
                Math.min(60.0, focalLength)
        );
    }

    // =========================================================
    // BOARD SCALE
    // =========================================================

    public double getBoardScale() {
        return boardScale;
    }

    public void setBoardScale(double boardScale) {
        this.boardScale = Math.max(
                0.50,
                Math.min(1.50, boardScale)
        );
    }

    // =========================================================
    // BOARD THICKNESS
    // =========================================================

    public double getBoardThickness() {
        return boardThickness;
    }

    public void setBoardThickness(double boardThickness) {
        this.boardThickness = Math.max(
                0.0,
                Math.min(1.50, boardThickness)
        );
    }

    // =========================================================
    // SCREEN OFFSET
    // =========================================================

    public double getScreenOffsetX() {
        return screenOffsetX;
    }

    public double getScreenOffsetY() {
        return screenOffsetY;
    }

    public void setScreenOffset(double x, double y) {
        this.screenOffsetX = x;
        this.screenOffsetY = y;
    }

    // =========================================================
    // BOARD PIXEL SIZE
    // =========================================================

    private double getBoardPixelSize() {
        return layout.getTileSize() * 8.0;
    }

    // =========================================================
    // SCREEN CENTER
    // =========================================================

    private double getScreenCenterX() {
        return layout.getBoardX()
                + getBoardPixelSize() / 2.0
                + screenOffsetX;
    }

    private double getScreenCenterY() {
        return layout.getBoardY()
                + getBoardPixelSize() / 2.0
                + screenOffsetY;
    }

    // =========================================================
    // WORLD CENTER
    // =========================================================

    private double worldCenter() {
        return boardWorldSize / 2.0;
    }

    // =========================================================
    // PROJECT
    // =========================================================

    /**
     * Projects a world-space board point onto the Swing component.
     *
     * The two sign choices below are intentional:
     *
     * 1. Front ranks are closer to the camera, so their perspective
     *    scale is larger.
     * 2. Increasing row moves downward on screen, so row 8 is the
     *    front edge and row 0 is the rear edge.
     */
    public Point2D.Double project(
            double row,
            double col,
            double z) {

        double x = col - worldCenter();
        double depth = row - worldCenter();

        double azimuth = Math.toRadians(cameraAzimuth);
        double elevation = Math.toRadians(cameraElevation);

        // Horizontal camera rotation.
        double rotatedX =
                x * Math.cos(azimuth)
                        - depth * Math.sin(azimuth);

        double rotatedDepth =
                x * Math.sin(azimuth)
                        + depth * Math.cos(azimuth);

        /*
         * Positive rotatedDepth is toward the player/front edge.
         */
        double cameraY =
                rotatedDepth * Math.cos(elevation)
                        + z * Math.sin(elevation);

        /*
         * Positive cameraZ is lower on screen.
         * Positive z therefore moves the piece upward.
         */
        double cameraZ =
                rotatedDepth * Math.sin(elevation)
                        - z * Math.cos(elevation);

        /*
         * FRONT = CLOSER.
         *
         * The previous implementation added cameraY here, which made
         * the rear ranks receive the larger perspective factor.  That
         * is the opposite of a real chessboard viewed from the front.
         */
        double depthFromCamera =
                cameraDistance - cameraY;

        depthFromCamera = Math.max(
                1.0,
                depthFromCamera
        );

        double perspectiveScale =
                focalLength / depthFromCamera;

        double pixelScale =
                getBoardPixelSize()
                        / boardWorldSize
                        * boardScale;

        double screenX =
                getScreenCenterX()
                        + rotatedX
                        * perspectiveScale
                        * pixelScale;

        /*
         * Row 0 is now above row 8, as it should be.
         * A positive z moves the piece upward.
         */
        double screenY =
                getScreenCenterY()
                        + cameraZ
                        * perspectiveScale
                        * pixelScale;

        return new Point2D.Double(
                screenX,
                screenY
        );
    }

    // =========================================================
    // BACKWARD COMPATIBILITY
    // =========================================================

    public Point2D.Double project(
            double row,
            double col) {

        return project(row, col, 0.0);
    }

    // =========================================================
    // SQUARE
    // =========================================================

    public Point2D.Double[] getSquare(
            int row,
            int col) {

        if (row < 0 || row >= 8 ||
                col < 0 || col >= 8) {

            return new Point2D.Double[0];
        }

        return new Point2D.Double[] {
                project(row,     col,     0.0),
                project(row,     col + 1, 0.0),
                project(row + 1, col + 1, 0.0),
                project(row + 1, col,     0.0)
        };
    }

    // =========================================================
    // SQUARE CENTER
    // =========================================================

    public Point2D.Double getSquareCenter(
            int row,
            int col) {

        Point2D.Double[] points =
                getSquare(row, col);

        if (points.length < 4) {
            return new Point2D.Double();
        }

        double x = 0.0;
        double y = 0.0;

        for (Point2D.Double point : points) {
            x += point.x;
            y += point.y;
        }

        return new Point2D.Double(
                x / 4.0,
                y / 4.0
        );
    }

    // =========================================================
    // BOARD CORNERS
    // =========================================================

    public Point2D.Double[] getBoardCorners() {

        return new Point2D.Double[] {
                project(0, 0, 0),
                project(0, 8, 0),
                project(8, 8, 0),
                project(8, 0, 0)
        };
    }

    // =========================================================
    // BOARD BOTTOM CORNERS
    // =========================================================

    public Point2D.Double[] getBoardBottomCorners() {

        double z = -boardThickness;

        return new Point2D.Double[] {
                project(0, 0, z),
                project(0, 8, z),
                project(8, 8, z),
                project(8, 0, z)
        };
    }

    // =========================================================
    // BOARD CENTER
    // =========================================================

    public Point2D.Double getBoardCenter() {
        return project(4.0, 4.0, 0.0);
    }

    // =========================================================
    // SCREEN -> BOARD
    // =========================================================

    public Point screenToBoard(
            int mouseX,
            int mouseY) {

        /*
         * Front squares first because they visually sit in front of
         * the rear ranks.
         */
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {

                Point2D.Double[] square =
                        getSquare(row, col);

                if (containsPoint(
                        square,
                        mouseX,
                        mouseY
                )) {
                    return new Point(col, row);
                }
            }
        }

        return null;
    }

    // =========================================================
    // POINT CONTAINMENT
    // =========================================================

    private boolean containsPoint(
            Point2D.Double[] points,
            double x,
            double y) {

        if (points == null || points.length < 4) {
            return false;
        }

        Path2D.Double path =
                new Path2D.Double();

        path.moveTo(points[0].x, points[0].y);

        for (int i = 1; i < points.length; i++) {
            path.lineTo(points[i].x, points[i].y);
        }

        path.closePath();

        return path.contains(x, y);
    }

    // =========================================================
// PIECE BASE
// =========================================================

    /**
     * Returns the visual contact point of a chess piece.
     *
     * Pieces should sit near the centre of their projected square.
     * A very small shift toward the front is used to compensate for
     * the perspective of upright piece images.
     */
    public Point2D.Double getPieceBase(
            int row,
            int col) {

        Point2D.Double[] square =
                getSquare(row, col);

        if (square == null || square.length < 4) {
            return new Point2D.Double();
        }

        // Exact centre of the projected square.
        Point2D.Double centre =
                getSquareCenter(row, col);

        // Centre of the front edge.
        double frontX =
                (square[2].x + square[3].x) * 0.5;

        double frontY =
                (square[2].y + square[3].y) * 0.5;

        /*
         * IMPORTANT:
         *
         * The old value was 0.42, which pushed the visual piece
         * contact point too far toward the front of the square.
         *
         * 0.15 keeps the piece almost at the true square centre
         * while giving a small natural perspective correction.
         */
        final double seating = 0.15;

        return new Point2D.Double(
                centre.x +
                        (frontX - centre.x) * seating,

                centre.y +
                        (frontY - centre.y) * seating
        );
    }

    // =========================================================
// PIECE TOP
// =========================================================

    /**
     * Returns the projected top position of a piece.
     *
     * The top and base are calculated from the same world-space
     * centre of the square, ensuring that the piece remains
     * vertically aligned in the 3D projection.
     */
    public Point2D.Double getPieceTop(
            int row,
            int col,
            double height) {

        return project(
                row + 0.5,
                col + 0.5,
                height
        );
    }

    // =========================================================
    // PIECE PERSPECTIVE SCALE
    // =========================================================

    /**
     * Returns the projected width of one square relative to the
     * unprojected Swing tile.  Front ranks are intentionally larger.
     */
    public double getPerspectiveScale(
            int row,
            int col) {

        Point2D.Double[] square =
                getSquare(row, col);

        if (square == null || square.length < 4) {
            return 1.0;
        }

        /*
         * Measure the actual projected width of the square.
         *
         * This is more reliable than measuring from the shifted
         * piece base to a reference point.
         */
        double topWidth = Point2D.distance(
                square[0].x,
                square[0].y,
                square[1].x,
                square[1].y
        );

        double bottomWidth = Point2D.distance(
                square[3].x,
                square[3].y,
                square[2].x,
                square[2].y
        );

        /*
         * Use the average projected width.
         */
        double projectedTile =
                (topWidth + bottomWidth) / 2.0;

        double originalTile =
                layout.getTileSize();

        if (originalTile <= 0.0) {
            return 1.0;
        }

        double scale =
                projectedTile / originalTile;

        /*
         * Prevent excessive scaling between the front and rear ranks.
         * This keeps the perspective natural and prevents pieces from
         * looking oversized on the front edge.
         */
        return Math.max(
                0.60,
                Math.min(1.30, scale)
        );
    }

    // =========================================================
    // DEPTH
    // =========================================================

    /**
     * Larger row means physically closer to the camera.
     */
    public double getDepth(
            int row,
            int col) {

        double x =
                col + 0.5 - worldCenter();

        double y =
                row + 0.5 - worldCenter();

        double azimuth =
                Math.toRadians(cameraAzimuth);

        return
                x * Math.sin(azimuth)
                        + y * Math.cos(azimuth);
    }
}

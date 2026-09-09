package chess.board;

public class BoardLayout {

    /*
     * Maximum square size.
     *
     * Increased so the board can become large enough for
     * the reference-style chess interface.
     */
    private static final int MAX_TILE_SIZE = 160;

    /*
     * Board should occupy most of the available game area.
     */
    private static final double BOARD_AREA_RATIO = 0.94;

    private int tileSize;
    private int boardSize;

    private int boardX;
    private int boardY;


    // =========================================================
    // UPDATE
    // =========================================================

    public void update(
            int panelWidth,
            int panelHeight) {

        if (panelWidth <= 0 ||
                panelHeight <= 0) {

            tileSize = 1;
            boardSize = 8;
            boardX = 0;
            boardY = 0;

            return;
        }


        // -----------------------------------------------------
        // AVAILABLE BOARD AREA
        // -----------------------------------------------------

        int targetWidth =
                (int)
                        (
                                panelWidth
                                        * BOARD_AREA_RATIO
                        );

        int targetHeight =
                (int)
                        (
                                panelHeight
                                        * BOARD_AREA_RATIO
                        );


        int targetBoardSize =
                Math.min(
                        targetWidth,
                        targetHeight
                );


        /*
         * Keep a little space around the board.
         */
        targetBoardSize =
                Math.max(
                        8,
                        targetBoardSize - 16
                );


        // -----------------------------------------------------
        // TILE
        // -----------------------------------------------------

        tileSize =
                targetBoardSize / 8;

        tileSize =
                Math.max(
                        1,
                        Math.min(
                                MAX_TILE_SIZE,
                                tileSize
                        )
                );


        boardSize =
                tileSize * 8;


        // -----------------------------------------------------
        // CENTER HORIZONTALLY
        // -----------------------------------------------------

        boardX =
                (panelWidth - boardSize) / 2;


        // -----------------------------------------------------
        // POSITION
        // -----------------------------------------------------

        /*
         * Slightly upward compared with the old renderer.
         *
         * This leaves room below the board for the UI while
         * keeping the chessboard visually dominant.
         */
        int verticalShift =
                (int)
                        (
                                panelHeight * 0.015
                        );


        boardY =
                (panelHeight - boardSize) / 2
                        + verticalShift;
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public int getTileSize() {
        return tileSize;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getBoardX() {
        return boardX;
    }

    public int getBoardY() {
        return boardY;
    }


    // =========================================================
    // COMPATIBILITY
    // =========================================================

    public int getCapturedHeight() {
        return 0;
    }
}
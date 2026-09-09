package chess.board;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class BoardMouseHandler {

    private final ChessBoard board;
    private final BoardLayout layout;
    private final BoardUIState ui;
    private final BoardInputHandler inputHandler;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public BoardMouseHandler(
            ChessBoard board,
            BoardLayout layout,
            BoardUIState ui,
            BoardInputHandler inputHandler) {

        this.board = board;
        this.layout = layout;
        this.ui = ui;
        this.inputHandler = inputHandler;


        // -----------------------------------------------------
        // MOUSE CLICK
        // -----------------------------------------------------

        board.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mousePressed(
                            MouseEvent e) {

                        handleMousePressed(e);
                    }
                }
        );


        // -----------------------------------------------------
        // MOUSE MOVE
        // -----------------------------------------------------

        board.addMouseMotionListener(
                new MouseMotionAdapter() {

                    @Override
                    public void mouseMoved(
                            MouseEvent e) {

                        handleMouseMoved(e);
                    }
                }
        );
    }


    // =========================================================
    // MOUSE PRESSED
    // =========================================================

    private void handleMousePressed(
            MouseEvent e) {

        if (layout.getTileSize() <= 0) {
            return;
        }


        int mouseX =
                e.getX();

        int mouseY =
                e.getY();


        Point square =
                getBoardSquare(
                        mouseX,
                        mouseY
                );


        if (square == null) {
            return;
        }


        int col =
                square.x;

        int row =
                square.y;


        inputHandler.handleClick(
                row,
                col
        );


        board.repaint();
    }


    // =========================================================
    // MOUSE MOVED
    // =========================================================

    private void handleMouseMoved(
            MouseEvent e) {

        if (layout.getTileSize() <= 0) {

            clearHover();

            return;
        }


        int mouseX =
                e.getX();

        int mouseY =
                e.getY();


        Point square =
                getBoardSquare(
                        mouseX,
                        mouseY
                );


        if (square == null) {

            clearHover();

        } else {

            ui.setHoverCol(
                    square.x
            );

            ui.setHoverRow(
                    square.y
            );
        }


        board.repaint();
    }


    // =========================================================
    // GET BOARD SQUARE
    // =========================================================

    private Point getBoardSquare(
            int mouseX,
            int mouseY) {

        /*
         * -----------------------------------------------------
         * 3D BOARD
         * -----------------------------------------------------
         */

        if (board.getBoardView()
                == BoardView.THREE_D) {

            Board3DProjection projection =
                    board.get3DProjection();

            if (projection == null) {
                return null;
            }

            return projection.screenToBoard(
                    mouseX,
                    mouseY
            );
        }


        /*
         * -----------------------------------------------------
         * 2D BOARD
         * -----------------------------------------------------
         */

        int boardX =
                layout.getBoardX();

        int boardY =
                layout.getBoardY();

        int boardSize =
                layout.getBoardSize();

        int tile =
                layout.getTileSize();


        /*
         * Outside normal 2D board.
         */
        if (mouseX < boardX
                || mouseX >= boardX + boardSize
                || mouseY < boardY
                || mouseY >= boardY + boardSize) {

            return null;
        }


        int col =
                (mouseX - boardX)
                        / tile;

        int row =
                (mouseY - boardY)
                        / tile;


        if (row < 0 || row >= 8
                || col < 0 || col >= 8) {

            return null;
        }


        return new Point(
                col,
                row
        );
    }


    // =========================================================
    // CLEAR HOVER
    // =========================================================

    private void clearHover() {

        ui.setHoverRow(-1);
        ui.setHoverCol(-1);
    }
}
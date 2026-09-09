package chess.board;

import chess.game.Move;
import chess.pieces.Piece;

public class AnimatedPiece {

    private static final long ANIMATION_DURATION_MS = 120;

    private final Piece piece;

    private final float startX;
    private final float startY;

    private final float targetX;
    private final float targetY;

    private float x;
    private float y;

    private final long startTime;

    public AnimatedPiece(Move move, BoardLayout layout) {

        piece = move.getPiece();

        startX =
                layout.getBoardX()
                        + move.getFromCol()
                        * layout.getTileSize();

        startY =
                layout.getBoardY()
                        + move.getFromRow()
                        * layout.getTileSize();

        targetX =
                layout.getBoardX()
                        + move.getToCol()
                        * layout.getTileSize();

        targetY =
                layout.getBoardY()
                        + move.getToRow()
                        * layout.getTileSize();

        x = startX;
        y = startY;

        startTime = System.currentTimeMillis();
    }

    public void update() {

        long elapsed =
                System.currentTimeMillis()
                        - startTime;

        float progress =
                Math.min(
                        1.0f,
                        elapsed
                                / (float) ANIMATION_DURATION_MS
                );

        /*
         * Fast, smooth ease-out.
         * The piece moves quickly and gently settles
         * into the destination square.
         */
        float eased =
                1.0f
                        - (1.0f - progress)
                        * (1.0f - progress);

        x =
                startX
                        + (targetX - startX)
                        * eased;

        y =
                startY
                        + (targetY - startY)
                        * eased;
    }

    public boolean isDone() {

        return System.currentTimeMillis()
                - startTime
                >= ANIMATION_DURATION_MS;
    }

    public Piece getPiece() {
        return piece;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
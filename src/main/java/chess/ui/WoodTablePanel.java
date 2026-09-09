package chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Premium wooden table surface used behind the chess board.
 *
 * Designed to give the chess game a warm, physical wooden-table appearance
 * without requiring external image assets.
 */
public class WoodTablePanel extends JPanel {

    private static final int PLANK_HEIGHT = 88;

    public WoodTablePanel() {
        setOpaque(true);
        setBackground(new Color(55, 30, 16));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

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
                    RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE
            );

            int w = getWidth();
            int h = getHeight();

            if (w <= 0 || h <= 0) {
                return;
            }

            /*
             * -------------------------------------------------------------
             * 1. Base walnut colour
             * -------------------------------------------------------------
             */
            GradientPaint base = new GradientPaint(
                    0,
                    0,
                    new Color(105, 61, 31),
                    0,
                    h,
                    new Color(48, 25, 14)
            );

            g2.setPaint(base);
            g2.fillRect(0, 0, w, h);

            /*
             * -------------------------------------------------------------
             * 2. Large vertical light variation
             *
             * This prevents the whole table from looking like one flat
             * rectangle.
             * -------------------------------------------------------------
             */
            LinearGradientPaint woodLight = new LinearGradientPaint(
                    0,
                    0,
                    w,
                    0,
                    new float[]{0.0f, 0.18f, 0.48f, 0.78f, 1.0f},
                    new Color[]{
                            new Color(38, 18, 9, 80),
                            new Color(125, 72, 35, 28),
                            new Color(160, 98, 48, 12),
                            new Color(95, 51, 25, 25),
                            new Color(30, 14, 7, 90)
                    }
            );

            g2.setPaint(woodLight);
            g2.fillRect(0, 0, w, h);

            /*
             * -------------------------------------------------------------
             * 3. Individual wooden planks
             * -------------------------------------------------------------
             */
            for (int y = 0; y < h; y += PLANK_HEIGHT) {

                int plankIndex = y / PLANK_HEIGHT;

                int variation = ((plankIndex * 17) % 13) - 6;

                Color plankColor = new Color(
                        clamp(88 + variation),
                        clamp(49 + variation),
                        clamp(25 + variation),
                        75
                );

                g2.setColor(plankColor);
                g2.fillRect(
                        0,
                        y,
                        w,
                        Math.min(PLANK_HEIGHT - 2, h - y)
                );

                /*
                 * Dark seam between planks.
                 */
                g2.setColor(new Color(25, 12, 6, 90));
                g2.fillRect(
                        0,
                        Math.min(y + PLANK_HEIGHT - 2, h),
                        w,
                        2
                );

                /*
                 * Small highlight beside each seam.
                 */
                if (y + 3 < h) {
                    g2.setColor(new Color(180, 112, 55, 20));
                    g2.fillRect(
                            0,
                            y + 1,
                            w,
                            1
                    );
                }
            }

            /*
             * -------------------------------------------------------------
             * 4. Long natural wood-grain lines
             * -------------------------------------------------------------
             */
            g2.setStroke(new BasicStroke(
                    1.0f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            for (int i = 0; i < 145; i++) {

                int y = Math.floorMod(
                        i * 71 + 23,
                        Math.max(1, h)
                );

                int startX = Math.floorMod(
                        i * 113,
                        Math.max(1, w)
                );

                int length = 80 + Math.floorMod(i * 137, 420);

                int endX = Math.min(
                        w + 40,
                        startX + length
                );

                int wave = 3 + Math.floorMod(i * 19, 9);

                int alpha = 12 + Math.floorMod(i * 7, 20);

                g2.setColor(
                        new Color(25, 11, 5, alpha)
                );

                for (int offset = -wave; offset <= wave; offset += 2) {

                    int yy = y + offset;

                    if (yy < 0 || yy >= h) {
                        continue;
                    }

                    g2.drawLine(
                            startX,
                            yy,
                            endX,
                            yy + ((i % 3) - 1)
                    );
                }
            }

            /*
             * -------------------------------------------------------------
             * 5. Curved grain patterns
             * -------------------------------------------------------------
             */
            for (int i = 0; i < 38; i++) {

                int x = Math.floorMod(
                        i * 211 + 40,
                        Math.max(1, w)
                );

                int y = Math.floorMod(
                        i * 137 + 30,
                        Math.max(1, h)
                );

                int width = 80 + Math.floorMod(i * 53, 230);
                int height = 15 + Math.floorMod(i * 31, 38);

                g2.setColor(
                        new Color(30, 14, 7, 24)
                );

                g2.drawArc(
                        x,
                        y,
                        width,
                        height,
                        160,
                        125
                );

                g2.setColor(
                        new Color(180, 108, 52, 12)
                );

                g2.drawArc(
                        x + 2,
                        y + 1,
                        width,
                        height,
                        160,
                        125
                );
            }

            /*
             * -------------------------------------------------------------
             * 6. Natural wood knots
             * -------------------------------------------------------------
             */
            drawKnot(g2, w, h, 0.14, 0.27, 48);
            drawKnot(g2, w, h, 0.83, 0.19, 36);
            drawKnot(g2, w, h, 0.23, 0.78, 30);
            drawKnot(g2, w, h, 0.89, 0.76, 42);

            /*
             * -------------------------------------------------------------
             * 7. Soft central table illumination
             *
             * Keeps the area around the board slightly brighter.
             * -------------------------------------------------------------
             */
            RadialGradientPaint centerGlow =
                    new RadialGradientPaint(
                            new Point2D.Double(
                                    w * 0.50,
                                    h * 0.48
                            ),
                            Math.max(w, h) * 0.58f,
                            new float[]{
                                    0.0f,
                                    0.42f,
                                    1.0f
                            },
                            new Color[]{
                                    new Color(255, 210, 140, 30),
                                    new Color(180, 110, 55, 12),
                                    new Color(0, 0, 0, 0)
                            }
                    );

            g2.setPaint(centerGlow);
            g2.fillRect(0, 0, w, h);

            /*
             * -------------------------------------------------------------
             * 8. Dark outer edge
             * -------------------------------------------------------------
             */
            LinearGradientPaint edgeShade =
                    new LinearGradientPaint(
                            0,
                            0,
                            w,
                            0,
                            new float[]{
                                    0.0f,
                                    0.06f,
                                    0.50f,
                                    0.94f,
                                    1.0f
                            },
                            new Color[]{
                                    new Color(15, 7, 3, 125),
                                    new Color(0, 0, 0, 15),
                                    new Color(0, 0, 0, 0),
                                    new Color(0, 0, 0, 20),
                                    new Color(15, 7, 3, 130)
                            }
                    );

            g2.setPaint(edgeShade);
            g2.fillRect(0, 0, w, h);

            /*
             * -------------------------------------------------------------
             * 9. Bottom table shadow
             * -------------------------------------------------------------
             */
            GradientPaint bottomShade =
                    new GradientPaint(
                            0,
                            h * 0.72f,
                            new Color(0, 0, 0, 0),
                            0,
                            h,
                            new Color(0, 0, 0, 95)
                    );

            g2.setPaint(bottomShade);
            g2.fillRect(0, 0, w, h);

        } finally {
            g2.dispose();
        }
    }

    /**
     * Draws a subtle natural-looking wood knot.
     */
    private void drawKnot(
            Graphics2D g2,
            int width,
            int height,
            double relativeX,
            double relativeY,
            int size
    ) {
        int x = (int) (width * relativeX);
        int y = (int) (height * relativeY);

        g2.setStroke(new BasicStroke(1.0f));

        /*
         * Outer grain rings.
         */
        for (int i = 0; i < 5; i++) {

            int ringSize = size + i * 8;

            g2.setColor(
                    new Color(
                            35,
                            15,
                            7,
                            18 + i * 4
                    )
            );

            g2.drawOval(
                    x - ringSize / 2,
                    y - ringSize / 4,
                    ringSize,
                    Math.max(8, ringSize / 2)
            );
        }

        /*
         * Dark center.
         */
        g2.setColor(new Color(28, 12, 5, 42));

        g2.fillOval(
                x - size / 5,
                y - size / 9,
                size / 2,
                Math.max(7, size / 5)
        );
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
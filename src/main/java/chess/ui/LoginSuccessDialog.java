package chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;
import java.util.Random;

public class LoginSuccessDialog extends JDialog {

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color BG =
            new Color(11, 18, 28);

    private static final Color BORDER =
            new Color(90, 101, 116);

    private static final Color GOLD =
            new Color(225, 184, 69);

    private static final Color GOLD_LIGHT =
            new Color(244, 211, 124);

    private static final Color GOLD_DARK =
            new Color(181, 136, 35);

    private static final Color GREEN =
            new Color(115, 225, 115);

    private static final Color GREEN_DARK =
            new Color(25, 65, 40);

    private static final Color TEXT =
            new Color(247, 248, 250);

    private static final Color TEXT_SECONDARY =
            new Color(190, 198, 210);

    private final SuccessPanel successPanel;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public LoginSuccessDialog(
            Window owner,
            String username) {

        super(
                owner,
                "Login Successful",
                ModalityType.APPLICATION_MODAL
        );

        setUndecorated(true);

        setBackground(
                new Color(0, 0, 0, 0)
        );

        successPanel =
                new SuccessPanel(username);

        setContentPane(successPanel);

        setSize(
                390,
                365
        );

        setLocationRelativeTo(owner);

        // -----------------------------------------------------
        // Proper cleanup when dialog closes
        // -----------------------------------------------------

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosed(
                            WindowEvent e) {

                        successPanel.stopAnimation();
                    }

                    @Override
                    public void windowClosing(
                            WindowEvent e) {

                        successPanel.stopAnimation();
                    }
                }
        );
    }

    // =========================================================
// SHOW DIALOG
// =========================================================

    public static void show(
            Component parent,
            String username) {

        Window owner =
                SwingUtilities.getWindowAncestor(parent);

        LoginSuccessDialog dialog =
                new LoginSuccessDialog(
                        owner,
                        username
                );

        dialog.setVisible(true);
    }


    // =========================================================
    // SUCCESS PANEL
    // =========================================================

    private class SuccessPanel extends JPanel {

        private final String username;

        private final Random random =
                new Random();

        private final Particle[] particles =
                new Particle[24];

        private float glowPhase = 0f;

        private Timer animationTimer;

        SuccessPanel(String username) {

            this.username = username;

            setOpaque(false);

            setLayout(
                    new BoxLayout(
                            this,
                            BoxLayout.Y_AXIS
                    )
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            25,
                            30,
                            25,
                            30
                    )
            );

            // -------------------------------------------------
            // PARTICLES
            // -------------------------------------------------

            for (int i = 0;
                 i < particles.length;
                 i++) {

                particles[i] =
                        new Particle();
            }

            // -------------------------------------------------
            // SUCCESS ICON
            // -------------------------------------------------

            SuccessIcon icon =
                    new SuccessIcon();

            icon.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            icon.setPreferredSize(
                    new Dimension(
                            110,
                            105
                    )
            );

            icon.setMaximumSize(
                    new Dimension(
                            110,
                            105
                    )
            );

            add(icon);

            // -------------------------------------------------
            // TITLE
            // -------------------------------------------------

            JLabel title =
                    new JLabel(
                            "Login Successful!",
                            SwingConstants.CENTER
                    );

            title.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            title.setForeground(TEXT);

            title.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            25
                    )
            );

            add(title);

            add(
                    Box.createVerticalStrut(7)
            );

            // -------------------------------------------------
            // WELCOME MESSAGE
            // -------------------------------------------------

            JLabel welcome =
                    new JLabel(
                            "Welcome back, "
                                    + username
                                    + "!",
                            SwingConstants.CENTER
                    );

            welcome.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            welcome.setForeground(
                    TEXT_SECONDARY
            );

            welcome.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            16
                    )
            );

            add(welcome);

            add(
                    Box.createVerticalStrut(15)
            );

            // -------------------------------------------------
            // DIVIDER
            // -------------------------------------------------

            Divider divider =
                    new Divider();

            divider.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            divider.setPreferredSize(
                    new Dimension(
                            330,
                            15
                    )
            );

            divider.setMaximumSize(
                    new Dimension(
                            Integer.MAX_VALUE,
                            15
                    )
            );

            add(divider);

            add(
                    Box.createVerticalStrut(14)
            );

            // -------------------------------------------------
            // GREAT BUTTON
            // -------------------------------------------------

            SuccessButton greatButton =
                    new SuccessButton(
                            "Great!"
                    );

            greatButton.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            greatButton.setPreferredSize(
                    new Dimension(
                            330,
                            52
                    )
            );

            greatButton.setMaximumSize(
                    new Dimension(
                            Integer.MAX_VALUE,
                            52
                    )
            );

            greatButton.addActionListener(
                    e -> dispose()
            );

            add(greatButton);
        }

        // =====================================================
        // START ANIMATION
        // =====================================================

        void startAnimation() {

            if (animationTimer != null
                    && animationTimer.isRunning()) {

                return;
            }

            animationTimer =
                    new Timer(
                            30,
                            e -> {

                                glowPhase += 0.08f;

                                for (Particle particle :
                                        particles) {

                                    particle.update();
                                }

                                repaint();
                            }
                    );

            animationTimer.start();
        }

        // =====================================================
        // STOP ANIMATION
        // =====================================================

        void stopAnimation() {

            if (animationTimer != null) {

                animationTimer.stop();

                animationTimer = null;
            }
        }

        // =====================================================
        // PANEL PAINT
        // =====================================================

        @Override
        protected void paintComponent(
                Graphics g) {

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

                // -------------------------------------------------
                // SHADOW
                // -------------------------------------------------

                for (int i = 14;
                     i >= 2;
                     i -= 2) {

                    int alpha =
                            4 + (14 - i);

                    g2.setColor(
                            new Color(
                                    0,
                                    0,
                                    0,
                                    alpha
                            )
                    );

                    g2.fillRoundRect(
                            -i / 2,
                            i / 2,
                            getWidth() + i,
                            getHeight() + i,
                            22 + i,
                            22 + i
                    );
                }

                // -------------------------------------------------
                // BACKGROUND
                // -------------------------------------------------

                GradientPaint background =
                        new GradientPaint(
                                0,
                                0,
                                new Color(
                                        15,
                                        23,
                                        35
                                ),
                                0,
                                getHeight(),
                                BG
                        );

                g2.setPaint(background);

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1,
                        22,
                        22
                );

                // -------------------------------------------------
                // PARTICLES
                // -------------------------------------------------

                for (Particle particle :
                        particles) {

                    g2.setColor(
                            particle.color
                    );

                    g2.fill(
                            particle.getShape()
                    );
                }

                // -------------------------------------------------
                // BORDER
                // -------------------------------------------------

                g2.setColor(BORDER);

                g2.setStroke(
                        new BasicStroke(1.2f)
                );

                g2.drawRoundRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1,
                        22,
                        22
                );

                // -------------------------------------------------
                // GOLD TOP ACCENT
                // -------------------------------------------------

                GradientPaint accent =
                        new GradientPaint(
                                65,
                                0,
                                new Color(
                                        GOLD.getRed(),
                                        GOLD.getGreen(),
                                        GOLD.getBlue(),
                                        30
                                ),
                                getWidth() / 2f,
                                0,
                                GOLD
                        );

                g2.setPaint(accent);

                g2.fillRoundRect(
                        65,
                        0,
                        getWidth() - 130,
                        2,
                        2,
                        2
                );

            } finally {

                g2.dispose();
            }

            super.paintComponent(g);
        }
    }

    // =========================================================
    // SUCCESS ICON
    // =========================================================

    private class SuccessIcon extends JPanel {

        SuccessIcon() {

            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                int size = 92;

                int x =
                        (getWidth() - size) / 2;

                int y = 5;

                // -------------------------------------------------
                // PULSING GLOW
                // -------------------------------------------------

                float pulse =
                        (float)
                                (
                                        (
                                                Math.sin(
                                                        successPanel.glowPhase
                                                )
                                                        + 1
                                        )
                                                / 2
                                );

                int alpha =
                        30
                                + (int)
                                (
                                        35 * pulse
                                );

                g2.setColor(
                        new Color(
                                80,
                                220,
                                100,
                                alpha
                        )
                );

                g2.fillOval(
                        x - 15,
                        y - 15,
                        size + 30,
                        size + 30
                );

                // -------------------------------------------------
                // OUTER RING
                // -------------------------------------------------

                g2.setColor(
                        new Color(
                                135,
                                235,
                                135
                        )
                );

                g2.setStroke(
                        new BasicStroke(
                                3f
                        )
                );

                g2.drawOval(
                        x,
                        y,
                        size,
                        size
                );

                // -------------------------------------------------
                // INNER CIRCLE
                // -------------------------------------------------

                g2.setColor(
                        GREEN_DARK
                );

                g2.fillOval(
                        x + 4,
                        y + 4,
                        size - 8,
                        size - 8
                );

                // -------------------------------------------------
                // CHECK MARK
                // -------------------------------------------------

                g2.setColor(
                        Color.WHITE
                );

                g2.setStroke(
                        new BasicStroke(
                                8f,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND
                        )
                );

                Path2D check =
                        new Path2D.Double();

                check.moveTo(
                        x + 25,
                        y + 47
                );

                check.lineTo(
                        x + 41,
                        y + 63
                );

                check.lineTo(
                        x + 69,
                        y + 31
                );

                g2.draw(check);

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================================================
    // DIVIDER
    // =========================================================

    private class Divider extends JPanel {

        Divider() {

            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                int center =
                        getWidth() / 2;

                int y =
                        getHeight() / 2;

                // Left line
                g2.setColor(
                        new Color(
                                GOLD.getRed(),
                                GOLD.getGreen(),
                                GOLD.getBlue(),
                                80
                        )
                );

                g2.drawLine(
                        0,
                        y,
                        center - 12,
                        y
                );

                // Right line
                g2.drawLine(
                        center + 12,
                        y,
                        getWidth(),
                        y
                );

                // Diamond
                Polygon diamond =
                        new Polygon();

                diamond.addPoint(
                        center,
                        y - 6
                );

                diamond.addPoint(
                        center + 6,
                        y
                );

                diamond.addPoint(
                        center,
                        y + 6
                );

                diamond.addPoint(
                        center - 6,
                        y
                );

                g2.setColor(GOLD);

                g2.fillPolygon(
                        diamond
                );

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================================================
    // SUCCESS BUTTON
    // =========================================================

    private class SuccessButton extends JButton {

        private boolean hover;

        SuccessButton(String text) {

            super(text);

            setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            17
                    )
            );

            setForeground(
                    new Color(
                            25,
                            20,
                            10
                    )
            );

            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);

            setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );

            addMouseListener(
                    new java.awt.event.MouseAdapter() {

                        @Override
                        public void mouseEntered(
                                java.awt.event.MouseEvent e) {

                            hover = true;
                            repaint();
                        }

                        @Override
                        public void mouseExited(
                                java.awt.event.MouseEvent e) {

                            hover = false;
                            repaint();
                        }
                    }
            );
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                Color top =
                        hover
                                ? GOLD_LIGHT
                                : GOLD;

                Color bottom =
                        hover
                                ? GOLD
                                : GOLD_DARK;

                GradientPaint gradient =
                        new GradientPaint(
                                0,
                                0,
                                top,
                                0,
                                getHeight(),
                                bottom
                        );

                g2.setPaint(gradient);

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        14,
                        14
                );

                // Subtle highlight
                if (hover) {

                    g2.setColor(
                            new Color(
                                    255,
                                    255,
                                    255,
                                    35
                            )
                    );

                    g2.fillRoundRect(
                            1,
                            1,
                            getWidth() - 2,
                            getHeight() / 2,
                            13,
                            13
                    );
                }

            } finally {

                g2.dispose();
            }

            super.paintComponent(g);
        }
    }

    // =========================================================
// PARTICLE
// =========================================================

    private class Particle {

        private final Random random = new Random();

        private double x;
        private double y;
        private double speed;

        private int size;

        private Color color;

        Particle() {
            reset(true);
        }

        // =====================================================
        // RESET PARTICLE
        // =====================================================

        private void reset(boolean initial) {

            int width =
                    Math.max(
                            330,
                            getWidth()
                    );

            x =
                    20
                            + random.nextInt(
                            Math.max(
                                    1,
                                    width - 40
                            )
                    );

            if (initial) {

                y =
                        25
                                + random.nextInt(110);

            } else {

                y = 10;
            }

            speed =
                    0.25
                            + random.nextDouble() * 0.65;

            size =
                    3
                            + random.nextInt(4);

            int type =
                    random.nextInt(3);

            if (type == 0) {

                color =
                        new Color(
                                230,
                                190,
                                70,
                                190
                        );

            } else if (type == 1) {

                color =
                        new Color(
                                45,
                                190,
                                240,
                                190
                        );

            } else {

                color =
                        new Color(
                                105,
                                220,
                                105,
                                190
                        );
            }
        }

        // =====================================================
        // UPDATE
        // =====================================================

        private void update() {

            y += speed;

            if (y > 185) {

                reset(false);
            }
        }

        // =====================================================
        // SHAPE
        // =====================================================

        private Shape getShape() {

            Polygon diamond =
                    new Polygon();

            diamond.addPoint(
                    (int) x,
                    (int) y - size
            );

            diamond.addPoint(
                    (int) x + size,
                    (int) y
            );

            diamond.addPoint(
                    (int) x,
                    (int) y + size
            );

            diamond.addPoint(
                    (int) x - size,
                    (int) y
            );

            return diamond;
        }
    }
    // =========================================================
    // DIALOG LIFECYCLE
    // =========================================================

    @Override
    public void setVisible(
            boolean visible) {

        if (visible) {

            successPanel.startAnimation();
        } else {

            successPanel.stopAnimation();
        }

        super.setVisible(visible);
    }
}
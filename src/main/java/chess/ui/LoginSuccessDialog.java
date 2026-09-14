package chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

/**
 * Premium login-success dialog for Obitricy Chess Game.
 *
 * Features:
 * - Premium dark/gold appearance
 * - Animated success icon
 * - Floating particles
 * - Rounded dialog with shadow
 * - Responsive text layout
 * - Long-username protection
 * - Hover animation on the confirmation button
 * - Proper animation cleanup
 *
 * Compatible with the existing LoginPanel call:
 *
 * LoginSuccessDialog.show(
 *         SwingUtilities.getWindowAncestor(LoginPanel.this),
 *         user
 * );
 */
public class LoginSuccessDialog extends JDialog {

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color BG =
            new Color(8, 13, 21);

    private static final Color BG_TOP =
            new Color(15, 23, 35);

    private static final Color BORDER =
            new Color(255, 255, 255, 30);

    private static final Color BORDER_GOLD =
            new Color(225, 184, 69, 120);

    private static final Color GOLD =
            new Color(225, 184, 69);

    private static final Color GOLD_LIGHT =
            new Color(244, 211, 124);

    private static final Color GOLD_DARK =
            new Color(181, 136, 35);

    private static final Color GREEN =
            new Color(120, 230, 125);

    private static final Color GREEN_LIGHT =
            new Color(155, 245, 160);

    private static final Color GREEN_DARK =
            new Color(24, 68, 39);

    private static final Color TEXT =
            new Color(247, 248, 250);

    private static final Color TEXT_SECONDARY =
            new Color(190, 198, 210);

    private static final Color TEXT_MUTED =
            new Color(112, 123, 140);

    // =========================================================
    // DIMENSIONS
    // =========================================================

    private static final int DIALOG_WIDTH = 430;
    private static final int DIALOG_HEIGHT = 390;

    private static final int CORNER_RADIUS = 24;

    // =========================================================
    // CONTENT
    // =========================================================

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

        // -----------------------------------------------------
        // WINDOW APPEARANCE
        // -----------------------------------------------------

        setUndecorated(true);

        setBackground(
                new Color(0, 0, 0, 0)
        );

        // -----------------------------------------------------
        // SUCCESS PANEL
        // -----------------------------------------------------

        successPanel =
                new SuccessPanel(
                        username == null
                                ? ""
                                : username.trim()
                );

        setContentPane(successPanel);

        // -----------------------------------------------------
        // SIZE
        // -----------------------------------------------------

        setSize(
                DIALOG_WIDTH,
                DIALOG_HEIGHT
        );

        setResizable(false);

        // -----------------------------------------------------
        // CENTER ON OWNER
        // -----------------------------------------------------

        setLocationRelativeTo(owner);

        // -----------------------------------------------------
        // CLEANUP
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
                parent instanceof Window
                        ? (Window) parent
                        : SwingUtilities.getWindowAncestor(parent);

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
                new Particle[28];

        private float glowPhase = 0f;

        private Timer animationTimer;

        SuccessPanel(String username) {

            this.username = username;

            setOpaque(false);

            setLayout(
                    new GridBagLayout()
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
            // CONTENT
            // -------------------------------------------------

            JPanel content =
                    new JPanel(
                            new GridBagLayout()
                    );

            content.setOpaque(false);

            GridBagConstraints c =
                    new GridBagConstraints();

            c.gridx = 0;
            c.weightx = 1.0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.CENTER;

            // -------------------------------------------------
            // SUCCESS ICON
            // -------------------------------------------------

            SuccessIcon icon =
                    new SuccessIcon();

            icon.setPreferredSize(
                    new Dimension(
                            112,
                            112
                    )
            );

            icon.setMinimumSize(
                    new Dimension(
                            112,
                            112
                    )
            );

            icon.setMaximumSize(
                    new Dimension(
                            112,
                            112
                    )
            );

            c.gridy = 0;
            c.insets =
                    new Insets(
                            4,
                            0,
                            0,
                            0
                    );

            content.add(icon, c);

            // -------------------------------------------------
            // TITLE
            // -------------------------------------------------

            JLabel title =
                    new JLabel(
                            "Login Successful!",
                            SwingConstants.CENTER
                    );

            title.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            25
                    )
            );

            title.setForeground(TEXT);

            c.gridy++;
            c.insets =
                    new Insets(
                            0,
                            0,
                            5,
                            0
                    );

            content.add(title, c);

            // -------------------------------------------------
            // WELCOME MESSAGE
            // -------------------------------------------------

            JLabel welcome =
                    new JLabel(
                            createWelcomeText(),
                            SwingConstants.CENTER
                    );

            welcome.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            16
                    )
            );

            welcome.setForeground(
                    TEXT_SECONDARY
            );

            welcome.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            c.gridy++;
            c.insets =
                    new Insets(
                            0,
                            8,
                            0,
                            8
                    );

            content.add(welcome, c);

            // -------------------------------------------------
            // DIVIDER
            // -------------------------------------------------

            Divider divider =
                    new Divider();

            divider.setPreferredSize(
                    new Dimension(
                            350,
                            18
                    )
            );

            divider.setMinimumSize(
                    new Dimension(
                            350,
                            18
                    )
            );

            divider.setMaximumSize(
                    new Dimension(
                            350,
                            18
                    )
            );

            c.gridy++;
            c.insets =
                    new Insets(
                            13,
                            0,
                            13,
                            0
                    );

            content.add(divider, c);

            // -------------------------------------------------
            // GREAT BUTTON
            // -------------------------------------------------

            SuccessButton greatButton =
                    new SuccessButton(
                            "Great!"
                    );

            greatButton.setPreferredSize(
                    new Dimension(
                            350,
                            52
                    )
            );

            greatButton.setMinimumSize(
                    new Dimension(
                            350,
                            52
                    )
            );

            greatButton.setMaximumSize(
                    new Dimension(
                            350,
                            52
                    )
            );

            greatButton.addActionListener(
                    e -> dispose()
            );

            c.gridy++;
            c.insets =
                    new Insets(
                            0,
                            0,
                            0,
                            0
                    );

            content.add(greatButton, c);

            // -------------------------------------------------
            // ADD CONTENT TO PANEL
            // -------------------------------------------------

            GridBagConstraints outer =
                    new GridBagConstraints();

            outer.gridx = 0;
            outer.gridy = 0;
            outer.weightx = 1.0;
            outer.weighty = 1.0;
            outer.anchor = GridBagConstraints.CENTER;

            add(content, outer);
        }

        // =====================================================
        // WELCOME TEXT
        // =====================================================

        private String createWelcomeText() {

            String safeUsername =
                    escapeHtml(username);

            if (safeUsername.isEmpty()) {

                return "<html>"
                        + "<div style='text-align:center;'>"
                        + "Welcome back!"
                        + "</div>"
                        + "</html>";
            }

            /*
             * Long usernames are wrapped rather than allowed
             * to extend beyond the dialog.
             */
            return "<html>"
                    + "<div style='width:330px;"
                    + "text-align:center;'>"
                    + "Welcome back, "
                    + "<b>"
                    + safeUsername
                    + "</b>!"
                    + "</div>"
                    + "</html>";
        }

        // =====================================================
        // HTML ESCAPE
        // =====================================================

        private String escapeHtml(String text) {

            if (text == null) {
                return "";
            }

            return text
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
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

            animationTimer.setCoalesce(true);

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
                // OUTER SHADOW
                // -------------------------------------------------

                for (int i = 18;
                     i >= 2;
                     i -= 2) {

                    int alpha =
                            Math.max(
                                    2,
                                    25 - i
                            );

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
                            i / 3,
                            getWidth() + i,
                            getHeight() + i,
                            CORNER_RADIUS + i,
                            CORNER_RADIUS + i
                    );
                }

                // -------------------------------------------------
                // MAIN BACKGROUND
                // -------------------------------------------------

                GradientPaint background =
                        new GradientPaint(
                                0,
                                0,
                                BG_TOP,
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
                        CORNER_RADIUS,
                        CORNER_RADIUS
                );

                // -------------------------------------------------
                // SOFT GOLD GLOW
                // -------------------------------------------------

                RadialGradientPaint glow =
                        new RadialGradientPaint(
                                new Point(
                                        getWidth() / 2,
                                        115
                                ),
                                175f,
                                new float[]{
                                        0.0f,
                                        0.55f,
                                        1.0f
                                },
                                new Color[]{
                                        new Color(
                                                225,
                                                184,
                                                69,
                                                14
                                        ),
                                        new Color(
                                                225,
                                                184,
                                                69,
                                                5
                                        ),
                                        new Color(
                                                0,
                                                0,
                                                0,
                                                0
                                        )
                                }
                        );

                g2.setPaint(glow);

                g2.fillRoundRect(
                        1,
                        1,
                        getWidth() - 2,
                        getHeight() - 2,
                        CORNER_RADIUS,
                        CORNER_RADIUS
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
                // OUTER BORDER
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
                        CORNER_RADIUS,
                        CORNER_RADIUS
                );

                // -------------------------------------------------
                // GOLD INNER BORDER
                // -------------------------------------------------

                g2.setColor(
                        new Color(
                                BORDER_GOLD.getRed(),
                                BORDER_GOLD.getGreen(),
                                BORDER_GOLD.getBlue(),
                                35
                        )
                );

                g2.drawRoundRect(
                        2,
                        2,
                        getWidth() - 5,
                        getHeight() - 5,
                        CORNER_RADIUS - 3,
                        CORNER_RADIUS - 3
                );

                // -------------------------------------------------
                // GOLD TOP ACCENT
                // -------------------------------------------------

                GradientPaint accent =
                        new GradientPaint(
                                75,
                                0,
                                new Color(
                                        GOLD.getRed(),
                                        GOLD.getGreen(),
                                        GOLD.getBlue(),
                                        15
                                ),
                                getWidth() / 2f,
                                0,
                                GOLD,
                                true
                        );

                g2.setPaint(accent);

                g2.fillRoundRect(
                        72,
                        0,
                        getWidth() - 144,
                        3,
                        3,
                        3
                );

            } finally {

                g2.dispose();
            }

            super.paintComponent(g);
        }

        // =====================================================
        // CHILDREN CLIPPING
        // =====================================================

        @Override
        protected void paintChildren(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                Shape clip =
                        new RoundRectangle2D.Double(
                                0,
                                0,
                                getWidth(),
                                getHeight(),
                                CORNER_RADIUS,
                                CORNER_RADIUS
                        );

                g2.clip(clip);

                super.paintChildren(g2);

            } finally {

                g2.dispose();
            }
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

                int size = 88;

                int x =
                        (getWidth() - size) / 2;

                int y =
                        (getHeight() - size) / 2;

                // -------------------------------------------------
                // ANIMATED GREEN GLOW
                // -------------------------------------------------

                float pulse =
                        (float)
                                (
                                        (
                                                Math.sin(
                                                        successPanel.glowPhase
                                                )
                                                        + 1.0
                                        )
                                                / 2.0
                                );

                int glowAlpha =
                        24
                                + (int)
                                (
                                        35 * pulse
                                );

                g2.setColor(
                        new Color(
                                100,
                                235,
                                120,
                                glowAlpha
                        )
                );

                g2.fillOval(
                        x - 18,
                        y - 18,
                        size + 36,
                        size + 36
                );

                // -------------------------------------------------
                // SECONDARY GLOW
                // -------------------------------------------------

                g2.setColor(
                        new Color(
                                100,
                                235,
                                120,
                                16
                        )
                );

                g2.fillOval(
                        x - 10,
                        y - 10,
                        size + 20,
                        size + 20
                );

                // -------------------------------------------------
                // OUTER RING
                // -------------------------------------------------

                g2.setColor(
                        GREEN_LIGHT
                );

                g2.setStroke(
                        new BasicStroke(
                                3.2f
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

                GradientPaint circleGradient =
                        new GradientPaint(
                                x,
                                y,
                                new Color(
                                        37,
                                        91,
                                        52
                                ),
                                x + size,
                                y + size,
                                GREEN_DARK
                        );

                g2.setPaint(
                        circleGradient
                );

                g2.fillOval(
                        x + 5,
                        y + 5,
                        size - 10,
                        size - 10
                );

                // -------------------------------------------------
                // INNER HIGHLIGHT RING
                // -------------------------------------------------

                g2.setColor(
                        new Color(
                                255,
                                255,
                                255,
                                35
                        )
                );

                g2.setStroke(
                        new BasicStroke(
                                1.2f
                        )
                );

                g2.drawOval(
                        x + 8,
                        y + 8,
                        size - 16,
                        size - 16
                );

                // -------------------------------------------------
                // CHECK MARK
                // -------------------------------------------------

                g2.setColor(
                        Color.WHITE
                );

                g2.setStroke(
                        new BasicStroke(
                                7f,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND
                        )
                );

                Path2D check =
                        new Path2D.Double();

                check.moveTo(
                        x + 24,
                        y + 45
                );

                check.lineTo(
                        x + 39,
                        y + 60
                );

                check.lineTo(
                        x + 66,
                        y + 29
                );

                g2.draw(check);

                // -------------------------------------------------
                // SMALL GOLD ACCENT
                // -------------------------------------------------

                g2.setColor(
                        new Color(
                                GOLD_LIGHT.getRed(),
                                GOLD_LIGHT.getGreen(),
                                GOLD_LIGHT.getBlue(),
                                160
                        )
                );

                g2.setStroke(
                        new BasicStroke(
                                1.5f
                        )
                );

                g2.drawArc(
                        x + 13,
                        y + 13,
                        size - 26,
                        size - 26,
                        205,
                        85
                );

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

                // -------------------------------------------------
                // LEFT LINE
                // -------------------------------------------------

                GradientPaint leftGradient =
                        new GradientPaint(
                                0,
                                y,
                                new Color(
                                        GOLD.getRed(),
                                        GOLD.getGreen(),
                                        GOLD.getBlue(),
                                        10
                                ),
                                center - 15,
                                y,
                                new Color(
                                        GOLD.getRed(),
                                        GOLD.getGreen(),
                                        GOLD.getBlue(),
                                        90
                                )
                        );

                g2.setPaint(
                        leftGradient
                );

                g2.drawLine(
                        0,
                        y,
                        center - 15,
                        y
                );

                // -------------------------------------------------
                // RIGHT LINE
                // -------------------------------------------------

                GradientPaint rightGradient =
                        new GradientPaint(
                                center + 15,
                                y,
                                new Color(
                                        GOLD.getRed(),
                                        GOLD.getGreen(),
                                        GOLD.getBlue(),
                                        90
                                ),
                                getWidth(),
                                y,
                                new Color(
                                        GOLD.getRed(),
                                        GOLD.getGreen(),
                                        GOLD.getBlue(),
                                        10
                                )
                        );

                g2.setPaint(
                        rightGradient
                );

                g2.drawLine(
                        center + 15,
                        y,
                        getWidth(),
                        y
                );

                // -------------------------------------------------
                // CENTER DIAMOND
                // -------------------------------------------------

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

                g2.setColor(
                        GOLD
                );

                g2.fillPolygon(
                        diamond
                );

                // -------------------------------------------------
                // DIAMOND HIGHLIGHT
                // -------------------------------------------------

                Polygon highlight =
                        new Polygon();

                highlight.addPoint(
                        center,
                        y - 5
                );

                highlight.addPoint(
                        center + 4,
                        y
                );

                highlight.addPoint(
                        center,
                        y
                );

                highlight.addPoint(
                        center - 4,
                        y
                );

                g2.setColor(
                        GOLD_LIGHT
                );

                g2.fillPolygon(
                        highlight
                );

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================================================
    // SUCCESS BUTTON
    // =========================================================

    private class SuccessButton
            extends JButton {

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

            setToolTipText(
                    "Continue to Obitricy Chess"
            );

            addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseEntered(
                                MouseEvent e) {

                            hover = true;

                            repaint();
                        }

                        @Override
                        public void mouseExited(
                                MouseEvent e) {

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

                // -------------------------------------------------
                // BUTTON SHADOW
                // -------------------------------------------------

                if (isEnabled()) {

                    g2.setColor(
                            new Color(
                                    0,
                                    0,
                                    0,
                                    hover ? 55 : 40
                            )
                    );

                    g2.fillRoundRect(
                            0,
                            3,
                            getWidth(),
                            getHeight(),
                            14,
                            14
                    );
                }

                // -------------------------------------------------
                // BUTTON GRADIENT
                // -------------------------------------------------

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

                g2.setPaint(
                        gradient
                );

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight() - 2,
                        14,
                        14
                );

                // -------------------------------------------------
                // HOVER HIGHLIGHT
                // -------------------------------------------------

                if (hover) {

                    g2.setColor(
                            new Color(
                                    255,
                                    255,
                                    255,
                                    38
                            )
                    );

                    g2.fillRoundRect(
                            1,
                            1,
                            getWidth() - 2,
                            (getHeight() - 2) / 2,
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

        private final Random random =
                new Random();

        private double x;
        private double y;
        private double speed;

        private int size;

        private Color color;

        Particle() {

            reset(true);
        }

        // =====================================================
        // RESET
        // =====================================================

        private void reset(
                boolean initial) {

            int width =
                    Math.max(
                            360,
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
                        15
                                + random.nextInt(
                                145
                        );

            } else {

                y = 8;
            }

            speed =
                    0.20
                            + random.nextDouble()
                            * 0.65;

            size =
                    2
                            + random.nextInt(
                            4
                    );

            int type =
                    random.nextInt(3);

            if (type == 0) {

                color =
                        new Color(
                                230,
                                190,
                                70,
                                145
                        );

            } else if (type == 1) {

                color =
                        new Color(
                                70,
                                190,
                                235,
                                120
                        );

            } else {

                color =
                        new Color(
                                105,
                                220,
                                105,
                                125
                        );
            }
        }

        // =====================================================
        // UPDATE
        // =====================================================

        private void update() {

            y += speed;

            if (y > 205) {

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

    // =========================================================
    // DISPOSE
    // =========================================================

    @Override
    public void dispose() {

        if (successPanel != null) {

            successPanel.stopAnimation();
        }

        super.dispose();
    }
}
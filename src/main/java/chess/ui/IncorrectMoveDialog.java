package chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class IncorrectMoveDialog extends JDialog {

    private static final Color BACKGROUND =
            new Color(10, 13, 17);

    private static final Color RED =
            new Color(205, 70, 70);

    private static final Color RED_LIGHT =
            new Color(245, 115, 115);

    private static final Color TEXT =
            new Color(245, 246, 248);

    private static final Color TEXT_SECONDARY =
            new Color(190, 198, 210);

    private final Runnable closeAction;

    private IncorrectMoveDialog(
            Window owner,
            Runnable closeAction) {

        super(
                owner,
                "Incorrect Move",
                ModalityType.APPLICATION_MODAL
        );

        this.closeAction = closeAction;

        setUndecorated(true);

        setBackground(
                new Color(0, 0, 0, 0)
        );

        setContentPane(
                new IncorrectMovePanel()
        );

        setSize(
                440,
                400
        );

        setLocationRelativeTo(owner);
    }

    public static void show(
            Component parent,
            Runnable closeAction) {

        Window owner =
                SwingUtilities.getWindowAncestor(parent);

        IncorrectMoveDialog dialog =
                new IncorrectMoveDialog(
                        owner,
                        closeAction
                );

        dialog.setVisible(true);
    }

    public static void show(
            Component parent) {

        show(
                parent,
                null
        );
    }

    private class IncorrectMovePanel
            extends JPanel {

        IncorrectMovePanel() {

            setOpaque(false);

            setLayout(
                    new BoxLayout(
                            this,
                            BoxLayout.Y_AXIS
                    )
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            28,
                            35,
                            28,
                            35
                    )
            );

            add(
                    Box.createVerticalStrut(4)
            );

            JLabel title =
                    new JLabel(
                            "KEEP GOING",
                            SwingConstants.CENTER
                    );

            title.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            title.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            25
                    )
            );

            title.setForeground(
                    RED_LIGHT
            );

            add(title);

            add(
                    Box.createVerticalStrut(10)
            );

            Divider divider =
                    new Divider();

            divider.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            divider.setPreferredSize(
                    new Dimension(
                            330,
                            18
                    )
            );

            divider.setMaximumSize(
                    new Dimension(
                            330,
                            18
                    )
            );

            add(divider);

            add(
                    Box.createVerticalStrut(10)
            );

            WarningIcon warning =
                    new WarningIcon();

            warning.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            warning.setPreferredSize(
                    new Dimension(
                            105,
                            105
                    )
            );

            warning.setMaximumSize(
                    new Dimension(
                            105,
                            105
                    )
            );

            add(warning);

            add(
                    Box.createVerticalStrut(8)
            );

            JLabel incorrect =
                    new JLabel(
                            "INCORRECT MOVE",
                            SwingConstants.CENTER
                    );

            incorrect.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            incorrect.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            24
                    )
            );

            incorrect.setForeground(
                    TEXT
            );

            add(incorrect);

            add(
                    Box.createVerticalStrut(8)
            );

            JLabel message =
                    new JLabel(
                            "That's not the puzzle move. Try again.",
                            SwingConstants.CENTER
                    );

            message.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            message.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            14
                    )
            );

            message.setForeground(
                    TEXT_SECONDARY
            );

            add(message);

            add(
                    Box.createVerticalStrut(25)
            );

            JButton ok =
                    createButton("TRY AGAIN");

            ok.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            ok.addActionListener(e -> {

                dispose();

                if (closeAction != null) {
                    closeAction.run();
                }
            });

            add(ok);

            add(
                    Box.createVerticalGlue()
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

                int w = getWidth();
                int h = getHeight();

                RoundRectangle2D card =
                        new RoundRectangle2D.Double(
                                1,
                                1,
                                w - 2,
                                h - 2,
                                26,
                                26
                        );

                g2.setColor(
                        BACKGROUND
                );

                g2.fill(card);

                g2.setColor(
                        new Color(
                                RED.getRed(),
                                RED.getGreen(),
                                RED.getBlue(),
                                190
                        )
                );

                g2.setStroke(
                        new BasicStroke(1.5f)
                );

                g2.draw(card);

            } finally {

                g2.dispose();
            }

            super.paintComponent(g);
        }
    }

    private JButton createButton(
            String text) {

        JButton button =
                new JButton(text) {

                    @Override
                    protected void paintComponent(
                            Graphics g) {

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        GradientPaint gradient =
                                new GradientPaint(
                                        0,
                                        0,
                                        RED_LIGHT,
                                        0,
                                        getHeight(),
                                        RED
                                );

                        g2.setPaint(
                                gradient
                        );

                        g2.fillRoundRect(
                                0,
                                0,
                                getWidth(),
                                getHeight(),
                                12,
                                12
                        );

                        g2.dispose();

                        super.paintComponent(g);
                    }
                };

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        button.setForeground(
                new Color(
                        30,
                        12,
                        12
                )
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.setPreferredSize(
                new Dimension(
                        330,
                        52
                )
        );

        button.setMaximumSize(
                new Dimension(
                        330,
                        52
                )
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    private class Divider
            extends JPanel {

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

                g2.setColor(
                        new Color(
                                RED.getRed(),
                                RED.getGreen(),
                                RED.getBlue(),
                                110
                        )
                );

                g2.drawLine(
                        0,
                        y,
                        center - 12,
                        y
                );

                g2.drawLine(
                        center + 12,
                        y,
                        getWidth(),
                        y
                );

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
                        RED
                );

                g2.fillPolygon(
                        diamond
                );

            } finally {

                g2.dispose();
            }
        }
    }

    private class WarningIcon
            extends JPanel {

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

                int cx =
                        getWidth() / 2;

                int cy =
                        getHeight() / 2;

                // Soft outer glow
                g2.setColor(
                        new Color(
                                RED.getRed(),
                                RED.getGreen(),
                                RED.getBlue(),
                                30
                        )
                );

                g2.fillOval(
                        cx - 48,
                        cy - 48,
                        96,
                        96
                );

                // Warning triangle
                Polygon triangle =
                        new Polygon();

                triangle.addPoint(
                        cx,
                        cy - 40
                );

                triangle.addPoint(
                        cx - 42,
                        cy + 36
                );

                triangle.addPoint(
                        cx + 42,
                        cy + 36
                );

                g2.setColor(
                        RED
                );

                g2.fillPolygon(
                        triangle
                );

                // Inner triangle
                Polygon inner =
                        new Polygon();

                inner.addPoint(
                        cx,
                        cy - 29
                );

                inner.addPoint(
                        cx - 31,
                        cy + 25
                );

                inner.addPoint(
                        cx + 31,
                        cy + 25
                );

                g2.setColor(
                        new Color(
                                10,
                                13,
                                17
                        )
                );

                g2.fillPolygon(
                        inner
                );

                // Exclamation mark
                g2.setColor(
                        RED_LIGHT
                );

                g2.setStroke(
                        new BasicStroke(
                                5f,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND
                        )
                );

                g2.drawLine(
                        cx,
                        cy - 15,
                        cx,
                        cy + 9
                );

                g2.fillOval(
                        cx - 3,
                        cy + 17,
                        6,
                        6
                );

            } finally {

                g2.dispose();
            }
        }
    }
}
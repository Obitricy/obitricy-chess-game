package chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PuzzleSolvedDialog extends JDialog {

    private static final Color BACKGROUND =
            new Color(7, 11, 17);

    private static final Color CARD =
            new Color(17, 24, 34);

    private static final Color BORDER =
            new Color(255, 255, 255, 30);

    private static final Color GOLD =
            new Color(225, 180, 65);

    private static final Color GOLD_LIGHT =
            new Color(250, 215, 115);

    private static final Color TEXT =
            new Color(245, 246, 248);

    private static final Color TEXT_SECONDARY =
            new Color(190, 198, 210);

    /*
     * Action to execute when the player presses OK.
     */
    private final Runnable nextPuzzleAction;

    private PuzzleSolvedDialog(
            Window owner,
            Runnable nextPuzzleAction) {

        super(
                owner,
                "Puzzle Solved",
                ModalityType.APPLICATION_MODAL
        );

        /*
         * Store the action so the OK button
         * can execute it later.
         */
        this.nextPuzzleAction = nextPuzzleAction;

        setUndecorated(true);

        setBackground(
                new Color(0, 0, 0, 0)
        );

        setContentPane(
                new PuzzleSolvedPanel()
        );

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        setSize(
                500,
                430
        );

        setLocationRelativeTo(owner);
    }

    public static void show(
            Component parent,
            Runnable nextPuzzleAction) {

        Window owner =
                SwingUtilities.getWindowAncestor(parent);

        PuzzleSolvedDialog dialog =
                new PuzzleSolvedDialog(
                        owner,
                        nextPuzzleAction
                );

        dialog.setVisible(true);
    }


    private class PuzzleSolvedPanel
            extends JPanel {

        PuzzleSolvedPanel() {

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

            add(Box.createVerticalStrut(4));

            JLabel congratulations =
                    new JLabel(
                            "CONGRATULATIONS!",
                            SwingConstants.CENTER
                    );

            congratulations.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            congratulations.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            25
                    )
            );

            congratulations.setForeground(
                    GOLD_LIGHT
            );

            add(congratulations);

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

            TrophyIcon trophy =
                    new TrophyIcon();

            trophy.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            trophy.setPreferredSize(
                    new Dimension(
                            100,
                            100
                    )
            );

            trophy.setMaximumSize(
                    new Dimension(
                            100,
                            100
                    )
            );

            add(trophy);

            add(
                    Box.createVerticalStrut(8)
            );

            JLabel solved =
                    new JLabel(
                            "PUZZLE SOLVED!",
                            SwingConstants.CENTER
                    );

            solved.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            solved.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            25
                    )
            );

            solved.setForeground(
                    TEXT
            );

            add(solved);

            add(
                    Box.createVerticalStrut(8)
            );

            JLabel message =
                    new JLabel(
                            "Excellent work! You solved the puzzle.",
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
                    createButton("OK");

            ok.setAlignmentX(
                    Component.CENTER_ALIGNMENT
            );

            /*
             * Close the dialog first,
             * then load the next puzzle.
             */
            ok.addActionListener(e -> {

                dispose();

                if (nextPuzzleAction != null) {
                    nextPuzzleAction.run();
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

                // Soft shadow
                g2.setColor(
                        new Color(
                                0,
                                0,
                                0,
                                120
                        )
                );

                g2.fillRoundRect(
                        6,
                        8,
                        w - 12,
                        h - 12,
                        24,
                        24
                );

                // Outer background
                g2.setColor(
                        BACKGROUND
                );

                g2.fillRoundRect(
                        0,
                        0,
                        w - 1,
                        h - 1,
                        24,
                        24
                );

                // Inner card
                g2.setColor(
                        CARD
                );

                g2.fillRoundRect(
                        2,
                        2,
                        w - 5,
                        h - 5,
                        22,
                        22
                );

                // Gold semantic accent
                g2.setColor(
                        GOLD
                );

                g2.fillRoundRect(
                        60,
                        0,
                        w - 120,
                        3,
                        3,
                        3
                );

                // Subtle border
                g2.setColor(
                        BORDER
                );

                g2.drawRoundRect(
                        1,
                        1,
                        w - 3,
                        h - 3,
                        24,
                        24
                );

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
                                        GOLD_LIGHT,
                                        0,
                                        getHeight(),
                                        GOLD
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
                        13
                )
        );

        button.setForeground(
                new Color(
                        25,
                        20,
                        10
                )
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.setPreferredSize(
                new Dimension(
                        300,
                        44
                )
        );

        button.setMaximumSize(
                new Dimension(
                        300,
                        44
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
                                GOLD.getRed(),
                                GOLD.getGreen(),
                                GOLD.getBlue(),
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

                g2.setColor(GOLD);

                g2.fillPolygon(
                        diamond
                );

            } finally {

                g2.dispose();
            }
        }
    }

    private class TrophyIcon
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

                g2.setColor(
                        new Color(
                                225,
                                180,
                                65,
                                30
                        )
                );

                g2.fillOval(
                        cx - 50,
                        cy - 50,
                        100,
                        100
                );

                g2.setColor(GOLD);

                g2.setStroke(
                        new BasicStroke(
                                3f
                        )
                );

                g2.drawOval(
                        cx - 45,
                        cy - 45,
                        90,
                        90
                );

                // Trophy cup

                g2.fillRoundRect(
                        cx - 19,
                        cy - 27,
                        38,
                        32,
                        7,
                        7
                );

                g2.drawLine(
                        cx - 19,
                        cy - 20,
                        cx - 32,
                        cy - 25
                );

                g2.drawLine(
                        cx - 32,
                        cy - 25,
                        cx - 28,
                        cy - 5
                );

                g2.drawLine(
                        cx + 19,
                        cy - 20,
                        cx + 32,
                        cy - 25
                );

                g2.drawLine(
                        cx + 32,
                        cy - 25,
                        cx + 28,
                        cy - 5
                );

                g2.fillRect(
                        cx - 4,
                        cy + 5,
                        8,
                        16
                );

                g2.fillRoundRect(
                        cx - 24,
                        cy + 20,
                        48,
                        8,
                        5,
                        5
                );

            } finally {

                g2.dispose();
            }
        }
    }
}
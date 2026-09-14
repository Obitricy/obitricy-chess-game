package chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CheckDialog extends JDialog {

    private static final Color BG = new Color(10, 16, 24);
    private static final Color CARD = new Color(17, 25, 36);
    private static final Color GOLD = new Color(218, 175, 70);
    private static final Color GOLD_LIGHT = new Color(246, 211, 118);
    private static final Color TEXT = new Color(246, 248, 251);
    private static final Color MUTED = new Color(169, 181, 197);
    private static final Color BORDER = new Color(255, 255, 255, 32);

    public CheckDialog(
            Component parent,
            String message) {

        super(
                SwingUtilities.getWindowAncestor(parent),
                "Check",
                ModalityType.APPLICATION_MODAL
        );

        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0));

        setSize(500, 350);
        setLocationRelativeTo(parent);

        DialogPanel root = new DialogPanel();

        root.setLayout(
                new BorderLayout(0, 10)
        );

        root.setBorder(
                BorderFactory.createEmptyBorder(
                        22, 34, 25, 34
                )
        );

        // =========================
        // HEADER
        // =========================

        JPanel header = new JPanel();

        header.setOpaque(false);

        header.setLayout(
                new BoxLayout(
                        header,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel brand = createLabel(
                "OBITRICY",
                13,
                Font.BOLD,
                GOLD
        );

        brand.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel subtitle = createLabel(
                "CHESS ALERT",
                9,
                Font.BOLD,
                MUTED
        );

        subtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        header.add(brand);
        header.add(
                Box.createVerticalStrut(2)
        );
        header.add(subtitle);

        root.add(
                header,
                BorderLayout.NORTH
        );

        // =========================
        // CENTER
        // =========================

        JPanel center = new JPanel();

        center.setOpaque(false);

        center.setLayout(
                new BoxLayout(
                        center,
                        BoxLayout.Y_AXIS
                )
        );

        KingIcon kingIcon = new KingIcon();

        kingIcon.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        center.add(kingIcon);

        center.add(
                Box.createVerticalStrut(5)
        );

        JLabel title = createLabel(
                "CHECK!",
                30,
                Font.BOLD,
                TEXT
        );

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        center.add(title);

        center.add(
                Box.createVerticalStrut(9)
        );

        JLabel messageLabel = createLabel(
                message == null
                        ? "A king is in check."
                        : message,
                16,
                Font.PLAIN,
                MUTED
        );

        messageLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        center.add(messageLabel);

        root.add(
                center,
                BorderLayout.CENTER
        );

        // =========================
        // BUTTON
        // =========================

        JButton okButton =
                new GoldButton("OK");

        JPanel bottom = new JPanel(
                new BorderLayout()
        );

        bottom.setOpaque(false);

        bottom.setPreferredSize(
                new Dimension(0, 44)
        );

        bottom.add(
                okButton,
                BorderLayout.CENTER
        );

        root.add(
                bottom,
                BorderLayout.SOUTH
        );

        okButton.addActionListener(
                e -> dispose()
        );

        getRootPane().setDefaultButton(
                okButton
        );

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        setContentPane(root);
    }

    // =========================
    // SHOW METHOD
    // =========================

    public static void show(
            Component parent,
            String message) {

        CheckDialog dialog =
                new CheckDialog(
                        parent,
                        message
                );

        dialog.setVisible(true);
    }

    // =========================
    // LABEL
    // =========================

    private static JLabel createLabel(
            String text,
            int size,
            int style,
            Color color) {

        JLabel label =
                new JLabel(
                        text,
                        SwingConstants.CENTER
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        style,
                        size
                )
        );

        label.setForeground(color);

        return label;
    }

    // =========================
    // DIALOG PANEL
    // =========================

    private static class DialogPanel
            extends JPanel {

        private final int radius = 22;

        DialogPanel() {
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

                // Shadow
                g2.setColor(
                        new Color(
                                0, 0, 0, 120
                        )
                );

                g2.fillRoundRect(
                        6,
                        8,
                        getWidth() - 12,
                        getHeight() - 12,
                        radius,
                        radius
                );

                // Outer background
                g2.setColor(BG);

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1,
                        radius,
                        radius
                );

                // Inner card
                g2.setColor(CARD);

                g2.fillRoundRect(
                        2,
                        2,
                        getWidth() - 5,
                        getHeight() - 5,
                        radius - 2,
                        radius - 2
                );

                // Gold top accent
                g2.setColor(GOLD);

                g2.fillRoundRect(
                        55,
                        0,
                        getWidth() - 110,
                        3,
                        3,
                        3
                );

                // Border
                g2.setColor(BORDER);

                g2.drawRoundRect(
                        1,
                        1,
                        getWidth() - 3,
                        getHeight() - 3,
                        radius,
                        radius
                );

            } finally {

                g2.dispose();
            }

            super.paintComponent(g);
        }

        @Override
        protected void paintChildren(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                g2.clip(
                        new RoundRectangle2D.Double(
                                0,
                                0,
                                getWidth(),
                                getHeight(),
                                radius,
                                radius
                        )
                );

                super.paintChildren(g2);

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================
    // KING ICON
    // =========================

    private static class KingIcon
            extends JComponent {

        KingIcon() {

            setPreferredSize(
                    new Dimension(68, 68)
            );

            setMaximumSize(
                    new Dimension(68, 68)
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

                int width = getWidth();
                int height = getHeight();

                int centerX = width / 2;

                // Glow
                g2.setColor(
                        new Color(
                                218, 175, 70, 30
                        )
                );

                g2.fillOval(
                        2,
                        2,
                        width - 4,
                        height - 4
                );

                // Circle
                g2.setColor(GOLD);

                g2.setStroke(
                        new BasicStroke(2.4f)
                );

                g2.drawOval(
                        6,
                        6,
                        width - 12,
                        height - 12
                );

                // Cross
                g2.setColor(GOLD_LIGHT);

                g2.fillRect(
                        centerX - 2,
                        13,
                        4,
                        12
                );

                g2.fillRect(
                        centerX - 7,
                        17,
                        14,
                        4
                );

                // King body
                Polygon king =
                        new Polygon();

                king.addPoint(
                        centerX - 11,
                        27
                );

                king.addPoint(
                        centerX + 11,
                        27
                );

                king.addPoint(
                        centerX + 7,
                        39
                );

                king.addPoint(
                        centerX + 13,
                        44
                );

                king.addPoint(
                        centerX - 13,
                        44
                );

                king.addPoint(
                        centerX - 7,
                        39
                );

                g2.fillPolygon(king);

                g2.fillRoundRect(
                        centerX - 15,
                        45,
                        30,
                        5,
                        3,
                        3
                );

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================
    // GOLD BUTTON
    // =========================

    private static class GoldButton
            extends JButton {

        GoldButton(String text) {

            super(text);

            setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            14
                    )
            );

            setForeground(
                    new Color(
                            15, 17, 20
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

                Color fill;

                if (getModel().isPressed()) {

                    fill = GOLD.darker();

                } else if (getModel().isRollover()) {

                    fill = GOLD_LIGHT;

                } else {

                    fill = GOLD;
                }

                g2.setColor(fill);

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        11,
                        11
                );

                g2.setColor(
                        new Color(
                                255,
                                235,
                                170,
                                130
                        )
                );

                g2.drawRoundRect(
                        1,
                        1,
                        getWidth() - 3,
                        getHeight() - 3,
                        10,
                        10
                );

            } finally {

                g2.dispose();
            }

            super.paintComponent(g);
        }
    }
}
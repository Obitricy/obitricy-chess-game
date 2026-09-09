package chess.ui;

import chess.pieces.Bishop;
import chess.pieces.Knight;
import chess.pieces.Piece;
import chess.pieces.Queen;
import chess.pieces.Rook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Pawn promotion dialog.
 *
 * Design:
 * - Dimmed chess board behind dialog
 * - Dark rounded header
 * - Gold promotion title
 * - 2 x 2 promotion choices
 * - Large chess pieces
 * - Alternating white / pale-blue tiles
 * - Gold hover selection
 * - Rounded outer dialog
 */
public class PromotionDialog extends JDialog {

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color HEADER =
            new Color(10, 9, 11);

    private static final Color GOLD =
            new Color(245, 190, 55);

    private static final Color TEXT =
            new Color(18, 18, 18);

    private static final Color WHITE_TILE =
            new Color(250, 250, 250);

    private static final Color BLUE_TILE =
            new Color(224, 237, 247);

    private static final Color HOVER_TILE =
            new Color(247, 214, 150);

    private static final Color BORDER =
            new Color(205, 205, 205);

    // =========================================================
    // SIZE
    // =========================================================

    private static final int DIALOG_WIDTH = 440;
    private static final int DIALOG_HEIGHT = 470;

    private static final int HEADER_HEIGHT = 68;

    private final boolean white;

    private Piece selectedPiece;

    private JPanel dimGlassPane;
    private Component previousGlassPane;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PromotionDialog(
            Window owner,
            boolean white,
            int row,
            int col) {

        super(
                owner,
                "Pawn Promotion",
                ModalityType.APPLICATION_MODAL
        );

        this.white = white;

        setUndecorated(true);
        setResizable(false);

        installDimOverlay();

        buildUI(row, col);

        setSize(
                DIALOG_WIDTH,
                DIALOG_HEIGHT
        );

        setLocationRelativeTo(owner);

        setDefaultCloseOperation(
                WindowConstants.DO_NOTHING_ON_CLOSE
        );
    }

    // =========================================================
    // SHOW
    // =========================================================

    public static Piece choose(
            Window owner,
            boolean white,
            int row,
            int col) {

        PromotionDialog dialog =
                new PromotionDialog(
                        owner,
                        white,
                        row,
                        col
                );

        dialog.setVisible(true);

        Piece result =
                dialog.selectedPiece;

        dialog.dispose();

        return result;
    }

    // =========================================================
    // BUILD UI
    // =========================================================

    private void buildUI(
            int row,
            int col) {

        RoundedPanel root =
                new RoundedPanel(
                        26,
                        Color.WHITE
                );

        root.setLayout(
                new BorderLayout()
        );

        // =====================================================
        // HEADER
        // =====================================================

        RoundedPanel header =
                new RoundedPanel(
                        26,
                        HEADER
                );

        header.setPreferredSize(
                new Dimension(
                        DIALOG_WIDTH,
                        HEADER_HEIGHT
                )
        );

        header.setLayout(
                new GridBagLayout()
        );

        JLabel title =
                new JLabel(
                        "Promote pawn to?"
                );

        title.setForeground(
                GOLD
        );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        27
                )
        );

        header.add(title);

        root.add(
                header,
                BorderLayout.NORTH
        );

        // =====================================================
        // PIECES
        // =====================================================

        Piece queen =
                new Queen(
                        white,
                        row,
                        col
                );

        Piece rook =
                new Rook(
                        white,
                        row,
                        col
                );

        Piece bishop =
                new Bishop(
                        white,
                        row,
                        col
                );

        Piece knight =
                new Knight(
                        white,
                        row,
                        col
                );

        // =====================================================
        // CHOICE GRID
        // =====================================================

        JPanel choices =
                new JPanel(
                        new GridLayout(
                                2,
                                2
                        )
                );

        choices.setOpaque(false);

        choices.add(
                createChoice(
                        "Queen",
                        queen,
                        WHITE_TILE
                )
        );

        choices.add(
                createChoice(
                        "Rook",
                        rook,
                        BLUE_TILE
                )
        );

        choices.add(
                createChoice(
                        "Bishop",
                        bishop,
                        BLUE_TILE
                )
        );

        choices.add(
                createChoice(
                        "Knight",
                        knight,
                        WHITE_TILE
                )
        );

        root.add(
                choices,
                BorderLayout.CENTER
        );

        setContentPane(root);
    }

    // =========================================================
    // CHOICE
    // =========================================================

    private JPanel createChoice(
            String name,
            Piece piece,
            Color normalColor) {

        JPanel tile =
                new JPanel(
                        new BorderLayout()
                ) {

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

                            g2.setColor(
                                    getBackground()
                            );

                            g2.fillRect(
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight()
                            );

                        } finally {

                            g2.dispose();
                        }

                        super.paintComponent(g);
                    }
                };

        tile.setOpaque(true);

        tile.setBackground(
                normalColor
        );

        tile.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1,
                                1,
                                1,
                                1,
                                BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                10,
                                8,
                                10
                        )
                )
        );

        // =====================================================
        // PIECE IMAGE
        // =====================================================

        JLabel imageLabel =
                new JLabel();

        imageLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        imageLabel.setVerticalAlignment(
                SwingConstants.CENTER
        );

        Image image =
                piece.getImage();

        if (image != null) {

            imageLabel.setIcon(
                    createPieceIcon(
                            image,
                            95,
                            112
                    )
            );
        }

        // =====================================================
        // NAME
        // =====================================================

        JLabel nameLabel =
                new JLabel(
                        name,
                        SwingConstants.CENTER
                );

        nameLabel.setForeground(
                TEXT
        );

        nameLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        nameLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        3,
                        0,
                        4,
                        0
                )
        );

        tile.add(
                imageLabel,
                BorderLayout.CENTER
        );

        tile.add(
                nameLabel,
                BorderLayout.SOUTH
        );

        // =====================================================
        // MOUSE
        // =====================================================

        MouseAdapter mouse =
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e) {

                        tile.setBackground(
                                HOVER_TILE
                        );

                        tile.repaint();
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e) {

                        Point point =
                                SwingUtilities.convertPoint(
                                        e.getComponent(),
                                        e.getPoint(),
                                        tile
                                );

                        if (!tile.contains(point)) {

                            tile.setBackground(
                                    normalColor
                            );

                            tile.repaint();
                        }
                    }

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        selectedPiece =
                                piece;

                        dispose();
                    }
                };

        tile.addMouseListener(mouse);
        imageLabel.addMouseListener(mouse);
        nameLabel.addMouseListener(mouse);

        return tile;
    }

    // =========================================================
    // PIECE IMAGE
    // =========================================================

    private ImageIcon createPieceIcon(
            Image image,
            int maxWidth,
            int maxHeight) {

        int originalWidth =
                image.getWidth(null);

        int originalHeight =
                image.getHeight(null);

        if (originalWidth <= 0 ||
                originalHeight <= 0) {

            return new ImageIcon(image);
        }

        double scale =
                Math.min(
                        (double) maxWidth
                                / originalWidth,

                        (double) maxHeight
                                / originalHeight
                );

        int width =
                Math.max(
                        1,
                        (int) Math.round(
                                originalWidth * scale
                        )
                );

        int height =
                Math.max(
                        1,
                        (int) Math.round(
                                originalHeight * scale
                        )
                );

        BufferedImage buffered =
                new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g2 =
                buffered.createGraphics();

        try {

            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_ALPHA_INTERPOLATION,
                    RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
            );

            g2.drawImage(
                    image,
                    0,
                    0,
                    width,
                    height,
                    null
            );

        } finally {

            g2.dispose();
        }

        return new ImageIcon(buffered);
    }

    // =========================================================
    // DIM BOARD
    // =========================================================

    private void installDimOverlay() {

        if (!(getOwner()
                instanceof RootPaneContainer root)) {

            return;
        }

        previousGlassPane =
                root.getGlassPane();

        dimGlassPane =
                new JPanel() {

                    @Override
                    protected void paintComponent(
                            Graphics g) {

                        Graphics2D g2 =
                                (Graphics2D)
                                        g.create();

                        try {

                            g2.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON
                            );

                            g2.setColor(
                                    new Color(
                                            0,
                                            0,
                                            0,
                                            120
                                    )
                            );

                            g2.fillRect(
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight()
                            );

                        } finally {

                            g2.dispose();
                        }
                    }
                };

        dimGlassPane.setOpaque(false);

        root.setGlassPane(
                dimGlassPane
        );

        dimGlassPane.setVisible(true);
    }

    // =========================================================
    // REMOVE DIM
    // =========================================================

    @Override
    public void dispose() {

        if (getOwner()
                instanceof RootPaneContainer root) {

            if (previousGlassPane != null) {

                root.setGlassPane(
                        previousGlassPane
                );
            }

            if (dimGlassPane != null) {

                dimGlassPane.setVisible(false);
            }
        }

        super.dispose();
    }

    // =========================================================
    // ROUNDED PANEL
    // =========================================================

    private static class RoundedPanel
            extends JPanel {

        private final int radius;
        private final Color background;

        RoundedPanel(
                int radius,
                Color background) {

            this.radius = radius;
            this.background = background;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D)
                            g.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(
                        background
                );

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        radius,
                        radius
                );

            } finally {

                g2.dispose();
            }

            super.paintComponent(g);
        }
    }
}
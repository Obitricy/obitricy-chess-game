package chess.ui;

import chess.board.GameState;
import chess.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Compact wooden tray that displays captured pieces of one colour. */
public class CapturedTrayPanel extends JPanel {

    public enum Side { WHITE, BLACK }

    private final GameState gameState;
    private final Side side;

    public CapturedTrayPanel(GameState gameState, Side side) {
        this.gameState = gameState;
        this.side = side;
        setOpaque(false);
        setPreferredSize(new Dimension(128, 480));
        setMinimumSize(new Dimension(92, 260));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            int w = getWidth();
            int h = getHeight();
            int x = 7, y = 18;
            int trayW = Math.max(60, w - 14);
            int trayH = Math.max(180, h - 36);

            // Shadow
            g2.setColor(new Color(0, 0, 0, 85));
            g2.fillRoundRect(x + 5, y + 7, trayW, trayH, 20, 20);

            // Outer wooden tray
            g2.setPaint(new GradientPaint(0, y, new Color(113, 69, 33),
                    0, y + trayH, new Color(53, 28, 14)));
            g2.fillRoundRect(x, y, trayW, trayH, 20, 20);

            g2.setColor(new Color(199, 130, 59, 150));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x + 1, y + 1, trayW - 2, trayH - 2, 20, 20);

            // Recessed center
            int innerX = x + 8;
            int innerY = y + 38;
            int innerW = trayW - 16;
            int innerH = trayH - 48;
            g2.setPaint(new GradientPaint(innerX, innerY, new Color(46, 25, 14),
                    innerX, innerY + innerH, new Color(25, 14, 8)));
            g2.fillRoundRect(innerX, innerY, innerW, innerH, 14, 14);

            String title = side == Side.BLACK ? "BLACK" : "WHITE";
            g2.setFont(new Font("Serif", Font.BOLD, 12));
            g2.setColor(new Color(244, 205, 124));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, x + (trayW - fm.stringWidth(title)) / 2, y + 24);

            List<Piece> captured = side == Side.BLACK
                    ? gameState.getCapturedBlack()
                    : gameState.getCapturedWhite();

            int piece = Math.max(24, Math.min(40, (innerW - 12) / 2));
            int gap = 4;
            int cols = Math.max(1, innerW / (piece + gap));
            int startX = innerX + 6;
            int startY = innerY + 10;

            int index = 0;
            for (Piece p : captured) {
                if (p == null || p.getImage() == null) continue;
                int col = index % cols;
                int row = index / cols;
                int px = startX + col * (piece + gap);
                int py = startY + row * (piece + gap);
                if (py + piece > innerY + innerH - 5) break;

                g2.setColor(new Color(0, 0, 0, 90));
                g2.fillOval(px + 4, py + piece - 8, piece - 8, 9);
                g2.drawImage(p.getImage(), px, py, piece, piece, this);
                index++;
            }
        } finally {
            g2.dispose();
        }
    }
}

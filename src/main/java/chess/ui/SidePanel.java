package chess.ui;

import chess.board.GameState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SidePanel extends JPanel {

    private final CapturedPiecesPanel capturedPanel;
    private final MoveHistoryPanel historyPanel;

    /*
     * Preferred width on larger screens.
     */
    private static final int PREFERRED_WIDTH = 300;

    /*
     * Smallest width we allow on a laptop.
     */
    private static final int MIN_WIDTH = 240;

    public SidePanel(GameState gameState) {

        setLayout(new BorderLayout());

        // Dark chess-style background
        setBackground(
                new Color(38, 29, 20)
        );

        capturedPanel =
                new CapturedPiecesPanel(gameState);

        historyPanel =
                new MoveHistoryPanel();

        capturedPanel.setBorder(
                new EmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );

        /*
         * Captured pieces occupy the upper section.
         */
        add(
                capturedPanel,
                BorderLayout.CENTER
        );

        /*
         * Move history occupies the bottom section.
         */
        add(
                historyPanel,
                BorderLayout.SOUTH
        );

        /*
         * Preferred size.
         */
        setPreferredSize(
                new Dimension(
                        PREFERRED_WIDTH,
                        600
                )
        );

        /*
         * Allow the panel to become narrower on
         * smaller screens.
         */
        setMinimumSize(
                new Dimension(
                        MIN_WIDTH,
                        0
                )
        );

        /*
         * Do not force a maximum width.
         */
        setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE
                )
        );
    }

    public void updateMoveHistory(String history) {

        historyPanel.setHistory(history);
    }

    public void refresh() {

        capturedPanel.repaint();
        historyPanel.repaint();
    }
}
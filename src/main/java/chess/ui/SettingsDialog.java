package chess.ui;

import chess.board.ChessBoard;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {

    public SettingsDialog(
            JFrame parent,
            ChessBoard board) {

        super(
                parent,
                "SETTINGS",
                true
        );

        setUndecorated(true);

        setSize(
                620,
                620
        );

        setLocationRelativeTo(parent);

        JPanel root =
                new JPanel(
                        new BorderLayout()
                );

        root.setBackground(
                new Color(
                        18,
                        17,
                        22
                )
        );

        /*
         * =====================================================
         * HEADER
         * =====================================================
         */

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setBackground(
                Color.BLACK
        );

        header.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        18,
                        12,
                        12
                )
        );

        JLabel title =
                new JLabel(
                        "⚙  SETTINGS"
                );

        title.setForeground(
                new Color(
                        245,
                        185,
                        55
                )
        );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        25
                )
        );

        header.add(
                title,
                BorderLayout.WEST
        );

        JButton close =
                new JButton("×");

        close.setFocusPainted(false);
        close.setBorderPainted(false);
        close.setContentAreaFilled(false);

        close.setForeground(
                Color.WHITE
        );

        close.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        close.addActionListener(
                e -> dispose()
        );

        header.add(
                close,
                BorderLayout.EAST
        );

        root.add(
                header,
                BorderLayout.NORTH
        );

        /*
         * =====================================================
         * CONTENT
         * =====================================================
         */

        SettingsPanel settingsPanel =
                new SettingsPanel(board);

        JScrollPane scroll =
                new JScrollPane(
                        settingsPanel
                );

        scroll.setBorder(null);

        scroll.setOpaque(false);
        scroll.getViewport()
                .setOpaque(false);

        root.add(
                scroll,
                BorderLayout.CENTER
        );

        /*
         * =====================================================
         * FOOTER
         * =====================================================
         */

        JPanel footer =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                12,
                                10
                        )
                );

        footer.setBackground(
                Color.BLACK
        );

        JButton apply =
                createButton(
                        "APPLY"
                );

        JButton cancel =
                createButton(
                        "CANCEL"
                );

        footer.add(cancel);
        footer.add(apply);

        cancel.addActionListener(
                e -> dispose()
        );

        apply.addActionListener(e -> {

            settingsPanel.applySettings();

            board.refreshAppearance();

            board.repaint();

            dispose();
        });

        root.add(
                footer,
                BorderLayout.SOUTH
        );

        setContentPane(root);
    }

    private JButton createButton(
            String text) {

        JButton button =
                new JButton(text);

        button.setFocusPainted(false);

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(
                Color.BLACK
        );

        button.setBackground(
                new Color(
                        230,
                        145,
                        35
                )
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        24,
                        10,
                        24
                )
        );

        return button;
    }
}
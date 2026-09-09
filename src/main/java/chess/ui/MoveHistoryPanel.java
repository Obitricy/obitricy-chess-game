package chess.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MoveHistoryPanel extends JPanel {

    private final JTextArea textArea;

    public MoveHistoryPanel() {

        setLayout(new BorderLayout());

        // Match the dark side-panel theme
        setBackground(new Color(38, 29, 20));

        // ---------------- TITLE ----------------

        JLabel title = new JLabel(
                "MOVE HISTORY",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        title.setForeground(Color.WHITE);

        title.setBorder(
                new EmptyBorder(6, 5, 6, 5)
        );

        add(title, BorderLayout.NORTH);

        // ---------------- HISTORY TEXT ----------------

        textArea = new JTextArea();

        textArea.setEditable(false);

        textArea.setLineWrap(true);

        textArea.setWrapStyleWord(true);

        textArea.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        textArea.setForeground(Color.WHITE);

        textArea.setBackground(
                new Color(48, 38, 28)
        );

        textArea.setCaretColor(Color.WHITE);

        textArea.setBorder(
                new EmptyBorder(8, 8, 8, 8)
        );

        // ---------------- SCROLL PANE ----------------

        JScrollPane scrollPane =
                new JScrollPane(textArea);

        scrollPane.setBorder(
                new LineBorder(
                        new Color(100, 85, 70)
                )
        );

        scrollPane.getViewport().setBackground(
                new Color(48, 38, 28)
        );

        /*
         * Keep the scrollbar visually compatible
         * with the dark panel.
         */
        scrollPane.getVerticalScrollBar()
                .setBackground(
                        new Color(38, 29, 20)
                );

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // ---------------- SIZE ----------------

        setPreferredSize(
                new Dimension(330, 240)
        );
    }

    public void setHistory(String text) {

        textArea.setText(text);

        textArea.setCaretPosition(
                textArea.getDocument().getLength()
        );
    }
}
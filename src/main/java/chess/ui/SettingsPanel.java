package chess.ui;

import chess.appearance.BoardTheme;
import chess.appearance.HighlightTheme;
import chess.appearance.PieceTheme;
import chess.appearance.ThemeManager;
import chess.audio.SoundManager;
import chess.board.BoardView;
import chess.board.ChessBoard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern settings panel for the Obitricy Chess Game.
 *
 * Layout:
 *
 *   ⚙ SETTINGS
 *
 *   Theme
 *   [ Board Theme ]
 *
 *   View             [ 3D ][ 2D ][ Auto ]
 *
 *   Play As          [ White ][ Black ][ By turns ]
 *
 *   Sound/Vibe       [ ON ][ OFF ]
 *
 *   Help             [ ON ][ OFF ]
 *
 *   Notification     [ ON ][ OFF ]
 *
 *              [ 👍 RATE US ]
 */
public class SettingsPanel extends JPanel {

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color BACKGROUND =
            new Color(39, 38, 47);

    private static final Color HEADER =
            new Color(18, 17, 21);

    private static final Color GOLD =
            new Color(245, 190, 60);

    private static final Color GOLD_DARK =
            new Color(142, 91, 28);

    private static final Color ROW =
            new Color(48, 47, 57);

    private static final Color ROW_ALT =
            new Color(43, 42, 51);

    private static final Color BUTTON_OFF =
            new Color(55, 43, 42);

    private static final Color BUTTON_ON =
            new Color(239, 137, 24);

    private static final Color BUTTON_HOVER =
            new Color(255, 157, 40);

    private static final Color TEXT =
            new Color(235, 235, 238);

    private static final Color MUTED =
            new Color(180, 180, 188);

    // =========================================================
    // GAME
    // =========================================================

    private final ChessBoard board;

    // =========================================================
    // EXISTING SETTINGS
    // =========================================================

    private JComboBox<BoardTheme> boardThemeBox;
    private JComboBox<PieceTheme> pieceThemeBox;
    private JComboBox<HighlightTheme> highlightThemeBox;
    private JComboBox<BoardView> boardViewBox;

    private JCheckBox darkModeBox;

    // =========================================================
    // NEW SETTINGS
    // =========================================================

    private boolean soundEnabled = true;
    private boolean helpEnabled = true;
    private boolean notificationEnabled = true;

    private String playAs = "White";

    private JButton soundOnButton;
    private JButton soundOffButton;

    private JButton helpOnButton;
    private JButton helpOffButton;

    private JButton notificationOnButton;
    private JButton notificationOffButton;

    private JButton whiteButton;
    private JButton blackButton;
    private JButton turnsButton;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SettingsPanel(ChessBoard board) {

        this.board = board;

        // =====================================================
        // BACKGROUND SOUND
        // =====================================================

        SoundManager.setEnabled(soundEnabled);

        setOpaque(false);

        setLayout(
                new BorderLayout()
        );

        setBorder(
                new EmptyBorder(
                        0,
                        0,
                        0,
                        0
                )
        );

        // =====================================================
        // MAIN CONTENT
        // =====================================================

        JPanel content =
                new JPanel();

        content.setOpaque(false);

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );

        // =====================================================
        // THEME
        // =====================================================

        content.add(
                createThemeSection()
        );

        // =====================================================
        // VIEW
        // =====================================================

        content.add(
                createViewSection()
        );

        // =====================================================
        // PLAY AS
        // =====================================================

        content.add(
                createPlayAsSection()
        );

        // =====================================================
        // SOUND
        // =====================================================

        content.add(
                createSoundSection()
        );

        // =====================================================
        // HELP
        // =====================================================

        content.add(
                createHelpSection()
        );

        // =====================================================
        // NOTIFICATION
        // =====================================================

        content.add(
                createNotificationSection()
        );

        // =====================================================
        // DARK MODE
        // =====================================================

        content.add(
                createDarkModeSection()
        );

        // =====================================================
        // RATE US
        // =====================================================

        content.add(
                createRateButton()
        );

        JScrollPane scroll =
                new JScrollPane(
                        content
                );

        scroll.setBorder(null);

        scroll.setOpaque(false);

        scroll.getViewport()
                .setOpaque(false);

        scroll.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        add(
                scroll,
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel createHeader() {

        JPanel header =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                18,
                                14
                        )
                );

        header.setOpaque(true);
        header.setBackground(HEADER);

        JLabel icon =
                new JLabel("⚙");

        icon.setForeground(GOLD);

        icon.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        30
                )
        );

        JLabel title =
                new JLabel("SETTINGS");

        title.setForeground(GOLD);

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        27
                )
        );

        header.add(icon);
        header.add(title);

        return header;
    }

    // =========================================================
    // THEME
    // =========================================================

    private JPanel createThemeSection() {

        JPanel section =
                new JPanel(
                        new GridBagLayout()
                );

        section.setOpaque(true);

        section.setBackground(
                BACKGROUND
        );

        section.setBorder(
                new EmptyBorder(
                        14,
                        16,
                        14,
                        16
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        4,
                        4,
                        4,
                        4
                );

        gbc.anchor =
                GridBagConstraints.WEST;

        // =====================================================
        // TITLE
        // =====================================================

        JLabel title =
                sectionTitle(
                        "Theme"
                );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        section.add(
                title,
                gbc
        );

        // =====================================================
        // BOARD THEME
        // =====================================================

        JLabel themeLabel =
                smallLabel(
                        "Theme"
                );

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;

        section.add(
                themeLabel,
                gbc
        );

        boardThemeBox =
                new JComboBox<>(
                        BoardTheme.values()
                );

        boardThemeBox.setSelectedItem(
                ThemeManager.getBoardTheme()
        );

        styleCombo(
                boardThemeBox
        );

        boardThemeBox.setPreferredSize(
                new Dimension(
                        300,
                        40
                )
        );

        gbc.gridx = 1;
        gbc.gridy = 1;

        section.add(
                boardThemeBox,
                gbc
        );

        // =====================================================
        // PIECES
        // =====================================================

        JLabel piecesLabel =
                smallLabel(
                        "Pieces"
                );

        gbc.gridx = 0;
        gbc.gridy = 2;

        section.add(
                piecesLabel,
                gbc
        );

        pieceThemeBox =
                new JComboBox<>(
                        PieceTheme.values()
                );

        pieceThemeBox.setSelectedItem(
                ThemeManager.getPieceTheme()
        );

        styleCombo(
                pieceThemeBox
        );

        pieceThemeBox.setPreferredSize(
                new Dimension(
                        300,
                        40
                )
        );

        gbc.gridx = 1;
        gbc.gridy = 2;

        section.add(
                pieceThemeBox,
                gbc
        );

        // =====================================================
        // HIGHLIGHT
        // =====================================================

        JLabel highlightLabel =
                smallLabel(
                        "Highlight"
                );

        gbc.gridx = 0;
        gbc.gridy = 3;

        section.add(
                highlightLabel,
                gbc
        );

        highlightThemeBox =
                new JComboBox<>(
                        HighlightTheme.values()
                );

        highlightThemeBox.setSelectedItem(
                ThemeManager.getHighlightTheme()
        );

        styleCombo(
                highlightThemeBox
        );

        highlightThemeBox.setPreferredSize(
                new Dimension(
                        300,
                        40
                )
        );

        gbc.gridx = 1;
        gbc.gridy = 3;

        section.add(
                highlightThemeBox,
                gbc
        );

        // =====================================================
        // FILL REMAINING SPACE
        // =====================================================

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        section.add(
                Box.createHorizontalGlue(),
                gbc
        );

        return section;
    }

    // =========================================================
    // VIEW
    // =========================================================

    private JPanel createViewSection() {

        JPanel row =
                createRow();

        row.add(
                rowLabel(
                        "▣",
                        "View"
                ),
                BorderLayout.WEST
        );

        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                2,
                                0
                        )
                );

        buttons.setOpaque(false);

        boardViewBox =
                new JComboBox<>(
                        BoardView.values()
                );

        boardViewBox.setSelectedItem(
                ThemeManager.getBoardView()
        );

        for (BoardView view :
                BoardView.values()) {

            JButton button =
                    createSegmentButton(
                            getBoardViewDisplayName(view)
                    );

            if (view ==
                    ThemeManager.getBoardView()) {

                button.setSelected(true);
                button.setBackground(BUTTON_ON);
            }

            button.addActionListener(
                    e -> {

                        boardViewBox.setSelectedItem(
                                view
                        );

                        updateSegmentSelection(
                                buttons,
                                button
                        );
                    }
            );

            buttons.add(button);
        }

        row.add(
                buttons,
                BorderLayout.CENTER
        );

        return row;
    }

    // =========================================================
    // PLAY AS
    // =========================================================

    private JPanel createPlayAsSection() {

        JPanel row =
                createRow();

        row.add(
                rowLabel(
                        "♟",
                        "Play As"
                ),
                BorderLayout.WEST
        );

        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                2,
                                0
                        )
                );

        buttons.setOpaque(false);

        whiteButton =
                createSegmentButton(
                        "White"
                );

        blackButton =
                createSegmentButton(
                        "Black"
                );

        turnsButton =
                createSegmentButton(
                        "By turns"
                );

        whiteButton.setSelected(true);

        whiteButton.addActionListener(
                e -> {

                    playAs = "White";

                    selectPlayButton(
                            whiteButton,
                            blackButton,
                            turnsButton
                    );
                }
        );

        blackButton.addActionListener(
                e -> {

                    playAs = "Black";

                    selectPlayButton(
                            blackButton,
                            whiteButton,
                            turnsButton
                    );
                }
        );

        turnsButton.addActionListener(
                e -> {

                    playAs = "By turns";

                    selectPlayButton(
                            turnsButton,
                            whiteButton,
                            blackButton
                    );
                }
        );

        buttons.add(whiteButton);
        buttons.add(blackButton);
        buttons.add(turnsButton);

        row.add(
                buttons,
                BorderLayout.CENTER
        );

        return row;
    }

    // =========================================================
    // SOUND
    // =========================================================

    private JPanel createSoundSection() {

        JPanel row =
                createRow();

        row.add(
                rowLabel(
                        "🔊",
                        "Sound/Vibe"
                ),
                BorderLayout.WEST
        );

        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                2,
                                0
                        )
                );

        buttons.setOpaque(false);

        soundOnButton =
                createSegmentButton(
                        "On"
                );

        soundOffButton =
                createSegmentButton(
                        "Off"
                );

        soundOnButton.setSelected(
                soundEnabled
        );

        soundOffButton.setSelected(
                !soundEnabled
        );

        updateSoundButtonAppearance();

        // =====================================================
        // SOUND ON
        // =====================================================

        soundOnButton.addActionListener(
                e -> {

                    soundEnabled = true;

                    SoundManager.setSoundEnabled(true);

                    soundOnButton.setSelected(true);
                    soundOffButton.setSelected(false);

                    updateSoundButtonAppearance();
                }
        );

        // =====================================================
        // SOUND OFF
        // =====================================================

        soundOffButton.addActionListener(
                e -> {

                    soundEnabled = false;

                    SoundManager.setSoundEnabled(false);

                    soundOnButton.setSelected(false);
                    soundOffButton.setSelected(true);

                    updateSoundButtonAppearance();
                }
        );

        buttons.add(
                soundOnButton
        );

        buttons.add(
                soundOffButton
        );

        row.add(
                buttons,
                BorderLayout.CENTER
        );

        return row;
    }

    private void updateSoundButtonAppearance() {

        if (soundOnButton == null ||
                soundOffButton == null) {

            return;
        }

        soundOnButton.setBackground(
                soundEnabled
                        ? BUTTON_ON
                        : BUTTON_OFF
        );

        soundOffButton.setBackground(
                soundEnabled
                        ? BUTTON_OFF
                        : BUTTON_ON
        );
    }

    // =========================================================
    // HELP
    // =========================================================

    private JPanel createHelpSection() {

        JPanel row =
                createRow();

        row.add(
                rowLabel(
                        "?",
                        "Help"
                ),
                BorderLayout.WEST
        );

        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                2,
                                0
                        )
                );

        buttons.setOpaque(false);

        helpOnButton =
                createSegmentButton(
                        "On"
                );

        helpOffButton =
                createSegmentButton(
                        "Off"
                );

        helpOnButton.setSelected(
                helpEnabled
        );

        helpOffButton.setSelected(
                !helpEnabled
        );

        helpOnButton.addActionListener(
                e -> {

                    helpEnabled = true;

                    helpOnButton.setSelected(true);
                    helpOffButton.setSelected(false);

                    helpOnButton.setBackground(BUTTON_ON);
                    helpOffButton.setBackground(BUTTON_OFF);
                }
        );

        helpOffButton.addActionListener(
                e -> {

                    helpEnabled = false;

                    helpOnButton.setSelected(false);
                    helpOffButton.setSelected(true);

                    helpOnButton.setBackground(BUTTON_OFF);
                    helpOffButton.setBackground(BUTTON_ON);
                }
        );

        helpOnButton.setBackground(
                helpEnabled
                        ? BUTTON_ON
                        : BUTTON_OFF
        );

        helpOffButton.setBackground(
                helpEnabled
                        ? BUTTON_OFF
                        : BUTTON_ON
        );

        buttons.add(helpOnButton);
        buttons.add(helpOffButton);

        row.add(
                buttons,
                BorderLayout.CENTER
        );

        return row;
    }

    // =========================================================
    // NOTIFICATION
    // =========================================================

    private JPanel createNotificationSection() {

        JPanel row =
                createRow();

        row.add(
                rowLabel(
                        "✉",
                        "Notification"
                ),
                BorderLayout.WEST
        );

        JPanel buttons =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                2,
                                0
                        )
                );

        buttons.setOpaque(false);

        notificationOnButton =
                createSegmentButton(
                        "On"
                );

        notificationOffButton =
                createSegmentButton(
                        "Off"
                );

        notificationOnButton.setSelected(
                notificationEnabled
        );

        notificationOffButton.setSelected(
                !notificationEnabled
        );

        notificationOnButton.addActionListener(
                e -> {

                    notificationEnabled = true;

                    notificationOnButton
                            .setSelected(true);

                    notificationOffButton
                            .setSelected(false);

                    notificationOnButton
                            .setBackground(BUTTON_ON);

                    notificationOffButton
                            .setBackground(BUTTON_OFF);
                }
        );

        notificationOffButton.addActionListener(
                e -> {

                    notificationEnabled = false;

                    notificationOnButton
                            .setSelected(false);

                    notificationOffButton
                            .setSelected(true);

                    notificationOnButton
                            .setBackground(BUTTON_OFF);

                    notificationOffButton
                            .setBackground(BUTTON_ON);
                }
        );

        notificationOnButton.setBackground(
                notificationEnabled
                        ? BUTTON_ON
                        : BUTTON_OFF
        );

        notificationOffButton.setBackground(
                notificationEnabled
                        ? BUTTON_OFF
                        : BUTTON_ON
        );

        buttons.add(
                notificationOnButton
        );

        buttons.add(
                notificationOffButton
        );

        row.add(
                buttons,
                BorderLayout.CENTER
        );

        return row;
    }

    // =========================================================
    // DARK MODE
    // =========================================================

    private JPanel createDarkModeSection() {

        JPanel row =
                createRow();

        row.add(
                rowLabel(
                        "☾",
                        "Dark Mode"
                ),
                BorderLayout.WEST
        );

        darkModeBox =
                new JCheckBox();

        darkModeBox.setSelected(
                ThemeManager.isDarkMode()
        );

        darkModeBox.setOpaque(false);

        darkModeBox.setFocusPainted(false);

        row.add(
                darkModeBox,
                BorderLayout.CENTER
        );

        return row;
    }

    // =========================================================
    // RATE US
    // =========================================================

    private JPanel createRateButton() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                0,
                                18
                        )
                );

        panel.setOpaque(false);

        JButton rate =
                new JButton(
                        "👍  Rate Us"
                );

        rate.setPreferredSize(
                new Dimension(
                        255,
                        52
                )
        );

        rate.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        rate.setForeground(
                Color.WHITE
        );

        rate.setBackground(
                new Color(
                        92,
                        125,
                        230
                )
        );

        rate.setFocusPainted(false);

        rate.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        20,
                        8,
                        20
                )
        );

        rate.addActionListener(e -> {

            int rating =
                    ObitricyDialog.rateUs(this);

            if (rating > 0) {

                ObitricyDialog.showSuccess(
                        this,
                        "Thank You!",
                        "Thank you for rating Obitricy Chess Game "
                                + rating
                                + " star"
                                + (rating == 1 ? "" : "s")
                                + "!"
                );
            }
        });

        panel.add(rate);

        return panel;
    }

    // =========================================================
    // SECTION
    // =========================================================

    private JPanel createSection() {

        JPanel panel =
                new JPanel();

        panel.setOpaque(true);

        panel.setBackground(
                BACKGROUND
        );

        panel.setBorder(
                new EmptyBorder(
                        14,
                        16,
                        12,
                        16
                )
        );

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        return panel;
    }

    // =========================================================
    // ROW
    // =========================================================

    private JPanel createRow() {

        JPanel row =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        row.setOpaque(true);

        row.setBackground(
                ROW
        );

        row.setBorder(
                new EmptyBorder(
                        10,
                        14,
                        10,
                        14
                )
        );

        row.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        66
                )
        );

        return row;
    }

    // =========================================================
    // ROW LABEL
    // =========================================================

    private JLabel rowLabel(
            String icon,
            String text) {

        JLabel label =
                new JLabel(
                        "<html>"
                                + "<font size='5'>"
                                + icon
                                + "</font>&nbsp;&nbsp;"
                                + text
                                + "</html>"
                );

        label.setForeground(
                TEXT
        );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        17
                )
        );

        label.setPreferredSize(
                new Dimension(
                        175,
                        40
                )
        );

        label.setMinimumSize(
                new Dimension(
                        175,
                        40
                )
        );

        label.setMaximumSize(
                new Dimension(
                        175,
                        40
                )
        );

        return label;
    }

    // =========================================================
    // SECTION TITLE
    // =========================================================

    private JLabel sectionTitle(
            String text) {

        JLabel label =
                new JLabel(
                        text
                );

        label.setForeground(
                GOLD
        );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return label;
    }

    // =========================================================
    // SMALL LABEL
    // =========================================================

    private JLabel smallLabel(
            String text) {

        JLabel label =
                new JLabel(
                        text
                );

        label.setForeground(
                MUTED
        );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return label;
    }

    // =========================================================
    // COMBO
    // =========================================================

    private void styleCombo(
            JComboBox<?> combo) {

        combo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        combo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        combo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        combo.setBackground(
                new Color(
                        49,
                        47,
                        57
                )
        );

        combo.setForeground(
                Color.WHITE
        );

        combo.setFocusable(false);
    }

    // =========================================================
    // SEGMENT BUTTON
    // =========================================================

    private JButton createSegmentButton(
            String text) {

        JButton button =
                new JButton(
                        text
                );

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(
                Color.WHITE
        );

        button.setBackground(
                BUTTON_OFF
        );

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        12,
                        8,
                        12
                )
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e) {

                        if (!button.isSelected()) {

                            button.setBackground(
                                    BUTTON_HOVER
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e) {

                        if (!button.isSelected()) {

                            button.setBackground(
                                    BUTTON_OFF
                            );
                        }
                    }
                }
        );

        return button;
    }

    // =========================================================
    // SEGMENT SELECTION
    // =========================================================

    private void updateSegmentSelection(
            JPanel panel,
            JButton selected) {

        for (Component component :
                panel.getComponents()) {

            if (component instanceof JButton button) {

                button.setSelected(
                        button == selected
                );

                button.setBackground(
                        button == selected
                                ? BUTTON_ON
                                : BUTTON_OFF
                );
            }
        }
    }

    // =========================================================
    // PLAY BUTTON SELECTION
    // =========================================================

    private void selectPlayButton(
            JButton selected,
            JButton other1,
            JButton other2) {

        selected.setSelected(true);
        selected.setBackground(BUTTON_ON);

        other1.setSelected(false);
        other1.setBackground(BUTTON_OFF);

        other2.setSelected(false);
        other2.setBackground(BUTTON_OFF);
    }

    private String getBoardViewDisplayName(BoardView view) {

        String name = view.toString()
                .toUpperCase();

        if (name.contains("THREE")) {
            return "3D";
        }

        if (name.contains("TWO")) {
            return "2D";
        }

        if (name.contains("AUTO")) {
            return "Auto";
        }

        return view.toString();
    }

    // =========================================================
    // APPLY SETTINGS
    // =========================================================

    public void applySettings() {

        // =====================================================
        // BOARD VIEW
        // =====================================================

        BoardView boardView =
                (BoardView)
                        boardViewBox
                                .getSelectedItem();

        if (boardView != null) {

            ThemeManager.setBoardView(
                    boardView
            );
        }

        // =====================================================
        // BOARD THEME
        // =====================================================

        BoardTheme boardTheme =
                (BoardTheme)
                        boardThemeBox
                                .getSelectedItem();

        if (boardTheme != null) {

            ThemeManager.setBoardTheme(
                    boardTheme
            );
        }

        // =====================================================
        // PIECE THEME
        // =====================================================

        PieceTheme pieceTheme =
                (PieceTheme)
                        pieceThemeBox
                                .getSelectedItem();

        if (pieceTheme != null) {

            ThemeManager.setPieceTheme(
                    pieceTheme
            );
        }

        // =====================================================
        // HIGHLIGHT
        // =====================================================

        HighlightTheme highlightTheme =
                (HighlightTheme)
                        highlightThemeBox
                                .getSelectedItem();

        if (highlightTheme != null) {

            ThemeManager.setHighlightTheme(
                    highlightTheme
            );
        }

        // =====================================================
        // DARK MODE
        // =====================================================

        ThemeManager.setDarkMode(
                darkModeBox.isSelected()
        );

        // =====================================================
        // SOUND
        // =====================================================

        SoundManager.setEnabled(
                soundEnabled
        );

        // =====================================================
        // REFRESH BOARD
        // =====================================================

        if (board != null) {

            board.setBoardView(
                    ThemeManager.getBoardView()
            );

            board.revalidate();
            board.repaint();
        }
    }

    // =========================================================
    // GETTERS FOR FUTURE GAME INTEGRATION
    // =========================================================

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean isHelpEnabled() {
        return helpEnabled;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public String getPlayAs() {
        return playAs;
    }
}
package chess.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import chess.ai.ComputerDifficulty;
import chess.game.GameMode;
import chess.save.SaveManager;


/**
 * Main menu for OBITRICY CHESS GAME.
 *
 * Presentation only:
 * - Player names
 * - Game mode
 * - Computer difficulty
 * - Start / Continue / Settings / Exit
 *
 * Navigation remains controlled by MainFrame.
 */
public class MenuPanel extends JPanel {

    // =========================================================
    // CONSTANTS
    // =========================================================

    private static final Color WOOD_TOP =
            new Color(92, 56, 30);

    private static final Color WOOD_BOTTOM =
            new Color(38, 21, 11);

    private static final Color PANEL =
            new Color(35, 20, 11, 245);

    private static final Color PANEL_LIGHT =
            new Color(53, 30, 16, 245);

    private static final Color GOLD =
            new Color(245, 190, 60);

    private static final Color GOLD_LIGHT =
            new Color(255, 215, 105);

    private static final Color GOLD_DARK =
            new Color(164, 101, 42);

    private static final Color TEXT =
            new Color(245, 240, 230);

    private static final Color MUTED_TEXT =
            new Color(205, 190, 165);

    private static final Color INPUT_BACKGROUND =
            new Color(18, 11, 7, 235);

    private static final Color BUTTON_BACKGROUND =
            new Color(24, 18, 13, 245);

    private static final Color BUTTON_HOVER =
            new Color(62, 42, 24, 250);


    // =========================================================
    // MAIN FRAME
    // =========================================================

    private final MainFrame frame;


    // =========================================================
    // PLAYER FIELDS
    // =========================================================

    private JTextField whiteField;
    private JTextField blackField;


    // =========================================================
    // GAME SETTINGS
    // =========================================================

    private JComboBox<String> modeBox;

    private JComboBox<ComputerDifficulty>
            difficultyBox;


    // =========================================================
    // BUTTONS
    // =========================================================

    private JButton startButton;
    private JButton continueButton;
    private JButton settingsButton;
    private JButton exitButton;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public MenuPanel(MainFrame frame) {

        this.frame = frame;


        // =====================================================
        // ROOT
        // =====================================================

        setLayout(
                new GridBagLayout()
        );

        setOpaque(false);


        // =====================================================
        // MAIN CARD
        // =====================================================

        JPanel mainCard =
                new JPanel(
                        new BorderLayout()
                );

        mainCard.setOpaque(true);

        mainCard.setBackground(
                PANEL
        );

        mainCard.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                GOLD_DARK,
                                2
                        ),

                        new EmptyBorder(
                                22,
                                30,
                                22,
                                30
                        )
                )
        );


        // =====================================================
        // HEADER
        // =====================================================

        JPanel header =
                createHeader();

        mainCard.add(
                header,
                BorderLayout.NORTH
        );


        // =====================================================
        // FORM
        // =====================================================

        JPanel form =
                createGameSetupPanel();

        mainCard.add(
                form,
                BorderLayout.CENTER
        );


        // =====================================================
        // ACTIONS
        // =====================================================

        JPanel actions =
                createActionPanel();

        mainCard.add(
                actions,
                BorderLayout.SOUTH
        );


        // =====================================================
        // ADD CARD
        // =====================================================

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.anchor =
                GridBagConstraints.CENTER;

        gbc.fill =
                GridBagConstraints.NONE;

        add(
                mainCard,
                gbc
        );
    }


    // =========================================================
    // HEADER
    // =========================================================

    private JPanel createHeader() {

        JPanel header =
                new JPanel();

        header.setOpaque(false);

        header.setLayout(
                new BoxLayout(
                        header,
                        BoxLayout.Y_AXIS
                )
        );


        // =====================================================
        // TITLE
        // =====================================================

        JLabel title =
                new JLabel(
                        "♟  OBITRICY CHESS"
                );

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        title.setForeground(
                GOLD_LIGHT
        );

        title.setFont(
                new Font(
                        "Georgia",
                        Font.BOLD,
                        30
                )
        );


        // =====================================================
        // SUBTITLE
        // =====================================================

        JLabel subtitle =
                new JLabel(
                        "THE ULTIMATE CHESS EXPERIENCE"
                );

        subtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        subtitle.setForeground(
                MUTED_TEXT
        );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );


        header.add(
                title
        );

        header.add(
                Box.createVerticalStrut(
                        5
                )
        );

        header.add(
                subtitle
        );

        header.add(
                Box.createVerticalStrut(
                        22
                )
        );


        return header;
    }


    // =========================================================
    // GAME SETUP PANEL
    // =========================================================

    private JPanel createGameSetupPanel() {

        JPanel panel =
                new JPanel();

        panel.setOpaque(false);

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );


        // =====================================================
        // WHITE PLAYER
        // =====================================================

        panel.add(
                createSectionLabel(
                        "WHITE PLAYER"
                )
        );

        panel.add(
                Box.createVerticalStrut(
                        6
                )
        );

        whiteField =
                createPlayerField(
                        ""
                );

        panel.add(
                whiteField
        );


        panel.add(
                Box.createVerticalStrut(
                        17
                )
        );


        // =====================================================
        // BLACK PLAYER
        // =====================================================

        panel.add(
                createSectionLabel(
                        "BLACK PLAYER"
                )
        );

        panel.add(
                Box.createVerticalStrut(
                        6
                )
        );

        blackField =
                createPlayerField(
                        ""
                );

        panel.add(
                blackField
        );


        panel.add(
                Box.createVerticalStrut(
                        17
                )
        );


        // =====================================================
        // GAME MODE
        // =====================================================

        panel.add(
                createSectionLabel(
                        "GAME MODE"
                )
        );

        panel.add(
                Box.createVerticalStrut(
                        6
                )
        );


        modeBox =
                new JComboBox<>(
                        new String[]{
                                "Player vs Player",
                                "Player vs Computer",
                                "Training Mode",
                                "Puzzle Mode",
                                "Online Multiplayer"
                        }
                );

        styleComboBox(
                modeBox
        );

        panel.add(
                modeBox
        );


        panel.add(
                Box.createVerticalStrut(
                        17
                )
        );


        // =====================================================
        // COMPUTER DIFFICULTY
        // =====================================================

        panel.add(
                createSectionLabel(
                        "COMPUTER DIFFICULTY"
                )
        );

        panel.add(
                Box.createVerticalStrut(
                        6
                )
        );


        difficultyBox =
                new JComboBox<>(
                        ComputerDifficulty.values()
                );

        difficultyBox.setSelectedItem(
                ComputerDifficulty.BEGINNER
        );

        difficultyBox.setEnabled(
                false
        );

        styleComboBox(
                difficultyBox
        );

        panel.add(
                difficultyBox
        );


        // =====================================================
        // MODE LISTENER
        // =====================================================

        modeBox.addActionListener(
                e -> updateDifficultyState()
        );


        return panel;
    }


    // =========================================================
    // SECTION LABEL
    // =========================================================

    private JLabel createSectionLabel(
            String text) {

        JLabel label =
                new JLabel(
                        text
                );

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        label.setForeground(
                GOLD
        );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );


        return label;
    }


    // =========================================================
    // PLAYER FIELD
    // =========================================================

    private JTextField createPlayerField(
            String text) {

        JTextField field =
                new JTextField(
                        text
                );

        field.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        field.setPreferredSize(
                new Dimension(
                        390,
                        42
                )
        );

        field.setMinimumSize(
                new Dimension(
                        390,
                        42
                )
        );

        field.setMaximumSize(
                new Dimension(
                        390,
                        42
                )
        );

        field.setBackground(
                INPUT_BACKGROUND
        );

        field.setForeground(
                TEXT
        );

        field.setCaretColor(
                GOLD
        );

        field.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                GOLD_DARK,
                                1
                        ),

                        BorderFactory.createEmptyBorder(
                                7,
                                12,
                                7,
                                12
                        )
                )
        );


        return field;
    }


    // =========================================================
    // COMBO BOX
    // =========================================================

    private void styleComboBox(
            JComboBox<?> comboBox) {

        comboBox.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        comboBox.setPreferredSize(
                new Dimension(
                        390,
                        42
                )
        );

        comboBox.setMinimumSize(
                new Dimension(
                        390,
                        42
                )
        );

        comboBox.setMaximumSize(
                new Dimension(
                        390,
                        42
                )
        );

        comboBox.setBackground(
                INPUT_BACKGROUND
        );

        comboBox.setForeground(
                TEXT
        );

        comboBox.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        comboBox.setBorder(
                BorderFactory.createLineBorder(
                        GOLD_DARK,
                        1
                )
        );

        comboBox.setFocusable(
                false
        );
    }


    // =========================================================
    // ACTION PANEL
    // =========================================================

    private JPanel createActionPanel() {

        JPanel panel =
                new JPanel();

        panel.setOpaque(false);

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );


        panel.add(
                Box.createVerticalStrut(
                        25
                )
        );


        // =====================================================
        // START
        // =====================================================

        startButton =
                createMainButton(
                        "START GAME",
                        true
                );

        startButton.addActionListener(
                e -> startGame()
        );

        panel.add(
                startButton
        );


        panel.add(
                Box.createVerticalStrut(
                        12
                )
        );


        // =====================================================
        // CONTINUE
        // =====================================================

        continueButton =
                createSecondaryButton(
                        "CONTINUE GAME"
                );

        continueButton.setEnabled(
                SaveManager.saveExists()
        );

        continueButton.addActionListener(
                e -> frame.continueGame()
        );

        panel.add(
                continueButton
        );


        // =====================================================
        // SETTINGS
        // =====================================================

        settingsButton =
                createSecondaryButton(
                        "SETTINGS"
                );

        settingsButton.addActionListener(
                e -> {

                    new GlobalSettingsDialog(
                            frame
                    ).setVisible(true);

                }
        );

        panel.add(
                settingsButton
        );


        // =====================================================
        // EXIT
        // =====================================================

        exitButton =
                createSecondaryButton(
                        "EXIT"
                );

        exitButton.addActionListener(
                e -> confirmExit()
        );

        panel.add(
                exitButton
        );


        panel.add(
                Box.createVerticalStrut(
                        14
                )
        );


        // =====================================================
        // FOOTER
        // =====================================================

        JLabel footer =
                new JLabel(
                        "♟  OBITRICY"
                );

        footer.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        footer.setForeground(
                GOLD_DARK
        );

        footer.setFont(
                new Font(
                        "Georgia",
                        Font.BOLD,
                        12
                )
        );

        panel.add(
                footer
        );


        return panel;
    }


    // =========================================================
    // MAIN BUTTON
    // =========================================================

    private JButton createMainButton(
            String text,
            boolean large) {

        JButton button =
                new JButton(
                        text
                );

        int width =
                large
                        ? 390
                        : 360;

        int height =
                large
                        ? 50
                        : 42;

        Dimension size =
                new Dimension(
                        width,
                        height
                );

        button.setPreferredSize(
                size
        );

        button.setMinimumSize(
                size
        );

        button.setMaximumSize(
                size
        );

        button.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        button.setFocusPainted(
                false
        );

        button.setContentAreaFilled(
                true
        );

        button.setOpaque(
                true
        );

        button.setBackground(
                new Color(
                        82,
                        52,
                        25
                )
        );

        button.setForeground(
                GOLD_LIGHT
        );

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                GOLD,
                                2
                        ),

                        BorderFactory.createEmptyBorder(
                                8,
                                20,
                                8,
                                20
                        )
                )
        );


        addHoverEffect(
                button,
                true
        );


        return button;
    }


    // =========================================================
    // SECONDARY BUTTON
    // =========================================================

    private JButton createSecondaryButton(
            String text) {

        JButton button =
                new JButton(
                        text
                );

        Dimension size =
                new Dimension(
                        360,
                        40
                );

        button.setPreferredSize(
                size
        );

        button.setMinimumSize(
                size
        );

        button.setMaximumSize(
                size
        );

        button.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        button.setFocusPainted(
                false
        );

        button.setContentAreaFilled(
                true
        );

        button.setOpaque(
                true
        );

        button.setBackground(
                BUTTON_BACKGROUND
        );

        button.setForeground(
                TEXT
        );

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        button.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                GOLD_DARK,
                                1
                        ),

                        BorderFactory.createEmptyBorder(
                                7,
                                20,
                                7,
                                20
                        )
                )
        );


        addHoverEffect(
                button,
                false
        );


        return button;
    }


    // =========================================================
    // HOVER EFFECT
    // =========================================================

    private void addHoverEffect(
            JButton button,
            boolean primary) {

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e) {

                        if (!button.isEnabled()) {
                            return;
                        }

                        button.setBackground(
                                BUTTON_HOVER
                        );

                        button.setForeground(
                                GOLD_LIGHT
                        );
                    }


                    @Override
                    public void mouseExited(
                            MouseEvent e) {

                        if (!button.isEnabled()) {
                            return;
                        }

                        if (primary) {

                            button.setBackground(
                                    new Color(
                                            82,
                                            52,
                                            25
                                    )
                            );

                        } else {

                            button.setBackground(
                                    BUTTON_BACKGROUND
                            );
                        }

                        button.setForeground(
                                primary
                                        ? GOLD_LIGHT
                                        : TEXT
                        );
                    }
                }
        );
    }


    // =========================================================
    // DIFFICULTY STATE
    // =========================================================

    private void updateDifficultyState() {

        int index =
                modeBox.getSelectedIndex();

        boolean vsComputer =
                index == 1;

        difficultyBox.setEnabled(
                vsComputer
        );

        difficultyBox.repaint();
    }


    // =========================================================
    // START GAME
    // =========================================================

    private void startGame() {

        // =====================================================
        // PLAYER NAMES
        // =====================================================

        String white =
                whiteField.getText();

        String black =
                blackField.getText();


        if (white == null ||
                white.isBlank()) {

            white = "White";
        } else {

            white = white.trim();
        }


        if (black == null ||
                black.isBlank()) {

            black = "Black";
        } else {

            black = black.trim();
        }


        // =====================================================
        // MODE
        // =====================================================

        GameMode mode =
                getSelectedGameMode();


        if (mode == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a valid game mode.",
                    "Game Mode",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        // =====================================================
        // DIFFICULTY
        // =====================================================

        ComputerDifficulty difficulty =
                (ComputerDifficulty)
                        difficultyBox.getSelectedItem();


        if (difficulty == null) {

            difficulty =
                    ComputerDifficulty.BEGINNER;
        }


        // =====================================================
        // START
        // =====================================================

        frame.startGame(
                mode,
                white,
                black,
                difficulty
        );
    }


    // =========================================================
    // GAME MODE MAPPING
    // =========================================================

    private GameMode getSelectedGameMode() {

        int index =
                modeBox.getSelectedIndex();


        return switch (index) {

            case 0 ->
                    GameMode.PLAYER_VS_PLAYER;

            case 1 ->
                    GameMode.PLAYER_VS_COMPUTER;

            case 2 ->
                    GameMode.TRAINING;

            case 3 ->
                    GameMode.PUZZLE;

            case 4 ->
                    GameMode.ONLINE;

            default ->
                    null;
        };
    }


    // =========================================================
    // EXIT
    // =========================================================

    private void confirmExit() {

        int option =
                JOptionPane.showConfirmDialog(

                        this,

                        "Exit Obitricy Chess?",

                        "Exit Game",

                        JOptionPane.YES_NO_OPTION,

                        JOptionPane.QUESTION_MESSAGE
                );


        if (option ==
                JOptionPane.YES_OPTION) {

            System.exit(0);
        }
    }


    // =========================================================
    // BACKGROUND
    // =========================================================

    @Override
    protected void paintComponent(
            Graphics g) {

        super.paintComponent(g);


        Graphics2D g2 =
                (Graphics2D)
                        g.create();


        try {

            int width =
                    getWidth();

            int height =
                    getHeight();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            // =================================================
            // WOOD GRADIENT
            // =================================================

            g2.setPaint(
                    new GradientPaint(
                            0,
                            0,
                            WOOD_TOP,
                            0,
                            height,
                            WOOD_BOTTOM
                    )
            );

            g2.fillRect(
                    0,
                    0,
                    width,
                    height
            );


            // =================================================
            // WOOD HORIZONTAL GRAIN
            // =================================================

            for (
                    int y = 45;
                    y < height;
                    y += 72
            ) {

                g2.setColor(
                        new Color(
                                20,
                                10,
                                5,
                                28
                        )
                );

                g2.fillRect(
                        0,
                        y,
                        width,
                        2
                );
            }


            // =================================================
            // SUBTLE WOOD GRAIN
            // =================================================

            for (
                    int i = 0;
                    i < 65;
                    i++
            ) {

                int x =
                        Math.floorMod(
                                i * 173,
                                Math.max(
                                        1,
                                        width
                                )
                        );

                int y =
                        Math.floorMod(
                                i * 97,
                                Math.max(
                                        1,
                                        height
                                )
                        );

                int length =
                        45
                                + Math.floorMod(
                                i * 67,
                                170
                        );

                g2.setColor(
                        new Color(
                                15,
                                8,
                                4,
                                18
                        )
                );

                g2.drawArc(
                        x,
                        y,
                        length,
                        16 + (i % 5) * 3,
                        160,
                        130
                );
            }


            // =================================================
            // CENTRAL LIGHT
            // =================================================

            float radius =
                    Math.max(
                            width,
                            height
                    ) * 0.65f;


            RadialGradientPaint glow =
                    new RadialGradientPaint(

                            new Point(
                                    width / 2,
                                    height / 2
                            ),

                            radius,

                            new float[]{
                                    0.0f,
                                    0.65f,
                                    1.0f
                            },

                            new Color[]{
                                    new Color(
                                            130,
                                            80,
                                            35,
                                            38
                                    ),

                                    new Color(
                                            80,
                                            45,
                                            20,
                                            18
                                    ),

                                    new Color(
                                            0,
                                            0,
                                            0,
                                            85
                                    )
                            }
                    );


            g2.setPaint(
                    glow
            );

            g2.fillRect(
                    0,
                    0,
                    width,
                    height
            );


            // =================================================
            // EDGE VIGNETTE
            // =================================================

            RadialGradientPaint vignette =
                    new RadialGradientPaint(

                            new Point(
                                    width / 2,
                                    height / 2
                            ),

                            Math.max(
                                    width,
                                    height
                            ) * 0.72f,

                            new float[]{
                                    0.55f,
                                    1.0f
                            },

                            new Color[]{
                                    new Color(
                                            0,
                                            0,
                                            0,
                                            0
                                    ),

                                    new Color(
                                            0,
                                            0,
                                            0,
                                            110
                                    )
                            }
                    );


            g2.setPaint(
                    vignette
            );

            g2.fillRect(
                    0,
                    0,
                    width,
                    height
            );

        } finally {

            g2.dispose();
        }
    }
}
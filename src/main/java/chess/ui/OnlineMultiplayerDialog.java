package chess.ui;

import chess.multiplayer.MultiplayerClient;
import chess.multiplayer.NetworkMessage;
import chess.multiplayer.OnlineServerConfig;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Premium Online Multiplayer dialog for Obitricy Chess Game.
 *
 * Visual/UI improvements only.
 * Networking, connection, create-game, join-game and START_GAME
 * behaviour are intentionally preserved.
 */
public class OnlineMultiplayerDialog extends JDialog {

    private static final Color BACKGROUND = new Color(8, 13, 20);
    private static final Color CARD = new Color(17, 25, 36);
    private static final Color CARD_LIGHT = new Color(22, 32, 45);

    private static final Color GOLD = new Color(218, 175, 70);
    private static final Color GOLD_LIGHT = new Color(246, 211, 118);

    private static final Color BLUE = new Color(74, 144, 226);
    private static final Color BLUE_LIGHT = new Color(125, 184, 246);

    private static final Color TEXT = new Color(246, 248, 251);
    private static final Color MUTED = new Color(166, 180, 198);

    private static final Color BORDER = new Color(255, 255, 255, 28);

    private final MainFrame mainFrame;

    private final MultiplayerClient client =
            new MultiplayerClient();

    private JTextField gameIdField;

    private JLabel statusLabel;

    private JButton createButton;
    private JButton joinButton;

    public OnlineMultiplayerDialog(MainFrame mainFrame) {

        super(
                mainFrame,
                "Online Multiplayer",
                true
        );

        this.mainFrame = mainFrame;

        setBackground(new Color(0, 0, 0, 0));

        initializeUI();

        setSize(520, 430);

        setLocationRelativeTo(mainFrame);

        setResizable(false);

        setDefaultCloseOperation(
                DO_NOTHING_ON_CLOSE
        );

        installKeyboardActions();
    }

    private void initializeUI() {

        JPanel backgroundPanel =
                new JPanel(new GridBagLayout()) {

                    @Override
                    protected void paintComponent(Graphics g) {

                        super.paintComponent(g);

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        try {

                            g2.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON
                            );

                            g2.setColor(BACKGROUND);

                            g2.fillRect(
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight()
                            );

                            /*
                             * Soft background glow.
                             */
                            g2.setColor(
                                    new Color(
                                            218,
                                            175,
                                            70,
                                            18
                                    )
                            );

                            g2.fillOval(
                                    getWidth() / 2 - 210,
                                    -170,
                                    420,
                                    300
                            );

                        } finally {

                            g2.dispose();
                        }
                    }
                };

        backgroundPanel.setOpaque(false);

        JPanel card =
                new JPanel(
                        new BorderLayout(
                                0,
                                0
                        )
                ) {

                    @Override
                    protected void paintComponent(Graphics g) {

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        try {

                            g2.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON
                            );

                            /*
                             * Shadow.
                             */
                            g2.setColor(
                                    new Color(
                                            0,
                                            0,
                                            0,
                                            90
                                    )
                            );

                            g2.fillRoundRect(
                                    7,
                                    9,
                                    getWidth() - 14,
                                    getHeight() - 14,
                                    28,
                                    28
                            );

                            /*
                             * Card.
                             */
                            g2.setColor(CARD);

                            g2.fillRoundRect(
                                    0,
                                    0,
                                    getWidth() - 1,
                                    getHeight() - 1,
                                    28,
                                    28
                            );

                            /*
                             * Gold top accent.
                             */
                            g2.setColor(GOLD);

                            g2.fillRoundRect(
                                    0,
                                    0,
                                    getWidth(),
                                    4,
                                    28,
                                    28
                            );

                            /*
                             * Border.
                             */
                            g2.setColor(BORDER);

                            g2.drawRoundRect(
                                    0,
                                    0,
                                    getWidth() - 1,
                                    getHeight() - 1,
                                    28,
                                    28
                            );

                        } finally {

                            g2.dispose();
                        }

                        super.paintComponent(g);
                    }
                };

        card.setOpaque(false);

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        26,
                        30,
                        28,
                        30
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;

        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.insets =
                new Insets(
                        12,
                        12,
                        12,
                        12
                );

        backgroundPanel.add(
                card,
                gbc
        );

        /*
         * ==============================
         * HEADER
         * ==============================
         */

        JPanel header =
                new JPanel();

        header.setOpaque(false);

        header.setLayout(
                new BoxLayout(
                        header,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel brand =
                new JLabel(
                        "OBITRICY",
                        SwingConstants.CENTER
                );

        brand.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        brand.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        brand.setForeground(
                GOLD
        );

        JLabel title =
                new JLabel(
                        "ONLINE MULTIPLAYER",
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
                TEXT
        );

        JLabel subtitle =
                new JLabel(
                        "Challenge another player and play online",
                        SwingConstants.CENTER
                );

        subtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        subtitle.setForeground(
                MUTED
        );

        header.add(brand);

        header.add(
                Box.createVerticalStrut(5)
        );

        header.add(title);

        header.add(
                Box.createVerticalStrut(7)
        );

        header.add(subtitle);

        card.add(
                header,
                BorderLayout.NORTH
        );

        /*
         * ==============================
         * CENTER CONTENT
         * ==============================
         */

        JPanel center =
                new JPanel();

        center.setOpaque(false);

        center.setLayout(
                new BoxLayout(
                        center,
                        BoxLayout.Y_AXIS
                )
        );

        center.setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        4,
                        16,
                        4
                )
        );

        JLabel gameIdLabel =
                new JLabel(
                        "GAME ID"
                );

        gameIdLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        gameIdLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        gameIdLabel.setForeground(
                GOLD_LIGHT
        );

        center.add(gameIdLabel);

        center.add(
                Box.createVerticalStrut(8)
        );

        gameIdField =
                new JTextField();

        gameIdField.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        gameIdField.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        gameIdField.setForeground(
                TEXT
        );

        gameIdField.setCaretColor(
                GOLD_LIGHT
        );

        gameIdField.setBackground(
                CARD_LIGHT
        );

        gameIdField.setBorder(
                new RoundedBorder(
                        new Color(
                                255,
                                255,
                                255,
                                32
                        ),
                        14
                )
        );

        gameIdField.setPreferredSize(
                new Dimension(
                        0,
                        48
                )
        );

        gameIdField.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        48
                )
        );

        gameIdField.setMargin(
                new Insets(
                        0,
                        14,
                        0,
                        14
                )
        );

        center.add(gameIdField);

        center.add(
                Box.createVerticalStrut(10)
        );

        JLabel hint =
                new JLabel(
                        "Create a new game, or enter an existing Game ID to join.",
                        SwingConstants.LEFT
                );

        hint.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        hint.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        hint.setForeground(
                MUTED
        );

        center.add(hint);

        center.add(
                Box.createVerticalStrut(20)
        );

        statusLabel =
                new JLabel(
                        "●  Not connected.",
                        SwingConstants.CENTER
                );

        statusLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        statusLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        statusLabel.setForeground(
                MUTED
        );

        center.add(statusLabel);

        card.add(
                center,
                BorderLayout.CENTER
        );

        /*
         * ==============================
         * BUTTONS
         * ==============================
         */

        JPanel buttonPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                10,
                                0
                        )
                );

        buttonPanel.setOpaque(false);

        createButton =
                createButton(
                        "CREATE GAME",
                        GOLD
                );

        joinButton =
                createButton(
                        "JOIN GAME",
                        BLUE
                );

        JButton cancelButton =
                createButton(
                        "CANCEL",
                        new Color(
                                90,
                                101,
                                116
                        )
                );

        buttonPanel.add(createButton);
        buttonPanel.add(joinButton);
        buttonPanel.add(cancelButton);

        card.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        setContentPane(
                backgroundPanel
        );

        /*
         * ==============================
         * ACTIONS
         * ==============================
         */

        createButton.addActionListener(
                e -> createGame()
        );

        joinButton.addActionListener(
                e -> joinGame()
        );

        cancelButton.addActionListener(
                e -> closeConnection()
        );

        gameIdField.addActionListener(
                e -> joinGame()
        );
    }

    private JButton createButton(
            String text,
            Color accent) {

        JButton button =
                new JButton(text) {

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

                            Color fill =
                                    getModel().isPressed()
                                            ? accent.darker()
                                            : getModel().isRollover()
                                              ? accent.brighter()
                                              : accent;

                            g2.setColor(fill);

                            g2.fillRoundRect(
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight(),
                                    14,
                                    14
                            );

                        } finally {

                            g2.dispose();
                        }

                        super.paintComponent(g);
                    }
                };

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        button.setForeground(
                Color.WHITE
        );

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setContentAreaFilled(false);

        button.setOpaque(false);

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setPreferredSize(
                new Dimension(
                        0,
                        44
                )
        );

        return button;
    }

    private void connect() throws Exception {

        if (client.isConnected()) {
            return;
        }

        String host =
                OnlineServerConfig.host();

        int port =
                OnlineServerConfig.port();

        statusLabel.setText(
                "●  Connecting to server..."
        );

        statusLabel.setForeground(
                BLUE_LIGHT
        );

        client.connect(
                host,
                port,
                this::handleNetworkMessage
        );

        statusLabel.setText(
                "●  Connected to server."
        );

        statusLabel.setForeground(
                new Color(
                        104,
                        205,
                        143
                )
        );
    }

    private void createGame() {

        try {

            connect();

            createButton.setEnabled(false);

            joinButton.setEnabled(false);

            statusLabel.setText(
                    "●  Creating game..."
            );

            statusLabel.setForeground(
                    GOLD_LIGHT
            );

            client.createGame();

        } catch (Exception ex) {

            showError(
                    ex.getMessage()
            );
        }
    }

    private void joinGame() {

        String gameId =
                gameIdField.getText().trim();

        if (gameId.isBlank()) {

            showError(
                    "Enter a Game ID."
            );

            return;
        }

        try {

            connect();

            createButton.setEnabled(false);

            joinButton.setEnabled(false);

            statusLabel.setText(
                    "●  Joining game "
                            + gameId.toUpperCase()
                            + "..."
            );

            statusLabel.setForeground(
                    BLUE_LIGHT
            );

            client.joinGame(
                    gameId
            );

        } catch (Exception ex) {

            showError(
                    ex.getMessage()
            );
        }
    }

    private void handleNetworkMessage(
            NetworkMessage message) {

        if (message == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {

            switch (message.getType()) {

                case WAITING:

                    statusLabel.setText(
                            "●  " + message.getData()
                    );

                    statusLabel.setForeground(
                            GOLD_LIGHT
                    );

                    break;

                case GAME_CREATED:

                    handleGameCreated(
                            message.getData()
                    );

                    break;

                case GAME_JOINED:

                    handleGameJoined(
                            message.getData()
                    );

                    break;

                case START_GAME:

                    handleStartGame(
                            message.getData()
                    );

                    break;

                case ERROR:

                    showError(
                            message.getData()
                    );

                    createButton.setEnabled(true);

                    joinButton.setEnabled(true);

                    break;

                default:
                    break;
            }
        });
    }

    private void handleGameCreated(
            String data) {

        String[] parts =
                data.split("\\|", 2);

        String gameId =
                parts.length > 0
                        ? parts[0]
                        : "";

        String color =
                parts.length > 1
                        ? parts[1]
                        : "WHITE";

        gameIdField.setText(
                gameId
        );

        statusLabel.setText(
                "●  Game created: "
                        + gameId
                        + " — You are "
                        + color
                        + ". Waiting for opponent..."
        );

        statusLabel.setForeground(
                GOLD_LIGHT
        );
    }

    private void handleGameJoined(
            String data) {

        String[] parts =
                data.split("\\|", 2);

        String gameId =
                parts.length > 0
                        ? parts[0]
                        : "";

        String color =
                parts.length > 1
                        ? parts[1]
                        : "BLACK";

        statusLabel.setText(
                "●  Joined "
                        + gameId
                        + " — You are "
                        + color
                        + "."
        );

        statusLabel.setForeground(
                new Color(
                        104,
                        205,
                        143
                )
        );
    }

    private void handleStartGame(
            String data) {

        String[] parts =
                data.split("\\|", 2);

        String gameId =
                parts.length > 0
                        ? parts[0]
                        : "";

        String color =
                parts.length > 1
                        ? parts[1]
                        : "WHITE";

        boolean white =
                "WHITE".equalsIgnoreCase(
                        color
                );

        statusLabel.setText(
                "●  Game started!"
        );

        statusLabel.setForeground(
                new Color(
                        104,
                        205,
                        143
                )
        );

        /*
         * Pass the network client into MainFrame.
         *
         * IMPORTANT:
         * This is unchanged.
         */
        mainFrame.startOnlineGame(
                client,
                gameId,
                white
        );

        dispose();
    }

    private void showError(
            String message) {

        String error =
                message == null
                        ? "Unknown error."
                        : message;

        /*
         * Premium Obitricy error dialog.
         */
        ObitricyDialog.showError(
                this,
                "Online Multiplayer",
                error
        );

        statusLabel.setText(
                "●  Error: " + error
        );

        statusLabel.setForeground(
                new Color(
                        245,
                        115,
                        115
                )
        );
    }

    private void closeConnection() {

        client.disconnect();

        dispose();
    }

    private void installKeyboardActions() {

        JRootPane rootPane =
                getRootPane();

        rootPane.registerKeyboardAction(
                e -> closeConnection(),
                KeyStroke.getKeyStroke(
                        "ESCAPE"
                ),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        rootPane.registerKeyboardAction(
                e -> {

                    if (gameIdField != null
                            && gameIdField.isFocusOwner()) {

                        joinGame();

                    }

                },
                KeyStroke.getKeyStroke(
                        "ENTER"
                ),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    /*
     * ==============================
     * ROUNDED BORDER
     * ==============================
     */

    private static final class RoundedBorder
            extends AbstractBorder {

        private final Color color;

        private final int radius;

        private RoundedBorder(
                Color color,
                int radius) {

            this.color = color;

            this.radius = radius;
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(color);

                g2.draw(
                        new RoundRectangle2D.Double(
                                x,
                                y,
                                width - 1,
                                height - 1,
                                radius,
                                radius
                        )
                );

            } finally {

                g2.dispose();
            }
        }

        @Override
        public Insets getBorderInsets(
                Component c) {

            return new Insets(
                    0,
                    14,
                    0,
                    14
            );
        }

        @Override
        public Insets getBorderInsets(
                Component c,
                Insets insets) {

            insets.top = 0;
            insets.left = 14;
            insets.bottom = 0;
            insets.right = 14;

            return insets;
        }
    }
}
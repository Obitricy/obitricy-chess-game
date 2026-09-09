package chess.ui;

import chess.multiplayer.MultiplayerClient;
import chess.multiplayer.NetworkMessage;
import chess.multiplayer.OnlineServerConfig;

import javax.swing.*;
import java.awt.*;

public class OnlineMultiplayerDialog extends JDialog {

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

        initializeUI();

        setSize(450, 350);

        setLocationRelativeTo(mainFrame);

        setResizable(false);

        setDefaultCloseOperation(
                DO_NOTHING_ON_CLOSE
        );
    }

    private void initializeUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout(10, 10));

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        /*
         * ==============================
         * TITLE
         * ==============================
         */

        JLabel title =
                new JLabel(
                        "ONLINE MULTIPLAYER",
                        SwingConstants.CENTER
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        /*
         * ==============================
         * CONNECTION PANEL
         * ==============================
         */

        JPanel connectionPanel =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                10,
                                10
                        )
                );

        connectionPanel.setBorder(
                BorderFactory.createTitledBorder("Online Game")
        );

        connectionPanel.add(
                new JLabel("Game ID:")
        );

        gameIdField =
                new JTextField();

        connectionPanel.add(
                gameIdField
        );

        mainPanel.add(
                connectionPanel,
                BorderLayout.CENTER
        );

        /*
         * ==============================
         * BUTTONS
         * ==============================
         */

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                5
                        )
                );

        createButton =
                new JButton("Create Game");

        joinButton =
                new JButton("Join Game");

        JButton cancelButton =
                new JButton("Cancel");

        buttonPanel.add(
                createButton
        );

        buttonPanel.add(
                joinButton
        );

        buttonPanel.add(
                cancelButton
        );

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        /*
         * ==============================
         * STATUS
         * ==============================
         */

        statusLabel =
                new JLabel(
                        "Not connected.",
                        SwingConstants.CENTER
                );

        statusLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        5,
                        5,
                        5
                )
        );

        mainPanel.add(
                statusLabel,
                BorderLayout.PAGE_END
        );

        setContentPane(mainPanel);

        /*
         * ==============================
         * ACTIONS
         * ==============================
         */

        createButton.addActionListener(e ->
                createGame()
        );

        joinButton.addActionListener(e ->
                joinGame()
        );

        cancelButton.addActionListener(e ->
                closeConnection()
        );
    }

    private void connect() throws Exception {

        if (client.isConnected()) {
            return;
        }

        String host = OnlineServerConfig.host();
        int port = OnlineServerConfig.port();

        statusLabel.setText(
                "Connecting to server..."
        );

        client.connect(
                host,
                port,
                this::handleNetworkMessage
        );

        statusLabel.setText(
                "Connected to server."
        );
    }

    private void createGame() {

        try {

            connect();

            createButton.setEnabled(false);
            joinButton.setEnabled(false);

            statusLabel.setText(
                    "Creating game..."
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
                    "Joining game " +
                            gameId.toUpperCase() +
                            "..."
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
                            message.getData()
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
                "Game created: "
                        + gameId
                        + " — You are "
                        + color
                        + ". Waiting for opponent..."
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
                "Joined "
                        + gameId
                        + " — You are "
                        + color
                        + "."
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
                "WHITE".equalsIgnoreCase(color);

        statusLabel.setText(
                "Game started!"
        );

        /*
         * Pass the network client into MainFrame.
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

        JOptionPane.showMessageDialog(
                this,
                message == null
                        ? "Unknown error."
                        : message,
                "Online Multiplayer",
                JOptionPane.ERROR_MESSAGE
        );

        statusLabel.setText(
                "Error: "
                        + message
        );
    }

    private void closeConnection() {

        client.disconnect();

        dispose();
    }
}
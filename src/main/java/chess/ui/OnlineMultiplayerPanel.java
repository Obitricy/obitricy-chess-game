package chess.ui;

import chess.multiplayer.MultiplayerClient;
import chess.multiplayer.NetworkMessage;
import chess.multiplayer.OnlineServerConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Online lobby for Obitricy Chess.
 *
 * The multiplayer/networking logic is intentionally preserved.
 * This version upgrades the lobby presentation and replaces
 * the default JOptionPane popups with Obitricy premium dialogs.
 */
public class OnlineMultiplayerPanel extends JPanel {

    private final MainFrame mainFrame;
    private final MultiplayerClient client = new MultiplayerClient();

    private JButton connectButton;
    private JButton mainMenuButton;
    private JButton refreshButton;

    private JLabel statusLabel;
    private JLabel identityLabel;
    private DefaultListModel<String> onlinePlayersModel;
    private JList<String> onlinePlayersList;
    private JButton challengeButton;

    private boolean connected;
    private String gameId;
    private boolean playerIsWhite;

    // Premium UI colors
    private static final Color BACKGROUND = new Color(9, 14, 21);
    private static final Color PANEL = new Color(17, 25, 36);
    private static final Color PANEL_2 = new Color(23, 32, 44);
    private static final Color GOLD = new Color(218, 175, 70);
    private static final Color GOLD_LIGHT = new Color(246, 211, 118);
    private static final Color TEXT = new Color(246, 248, 251);
    private static final Color MUTED = new Color(169, 181, 197);
    private static final Color BLUE = new Color(75, 145, 235);
    private static final Color BORDER = new Color(255, 255, 255, 28);

    public OnlineMultiplayerPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }

    private void initializeUI() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        JPanel content = new JPanel();
        content.setLayout(
                new BoxLayout(content, BoxLayout.Y_AXIS)
        );
        content.setBackground(BACKGROUND);
        content.setBorder(
                new EmptyBorder(30, 50, 30, 50)
        );

        // =====================================================
        // HEADER
        // =====================================================

        JLabel title =
                new JLabel("ONLINE MULTIPLAYER");

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        29
                )
        );

        title.setForeground(GOLD_LIGHT);

        content.add(title);

        content.add(
                Box.createVerticalStrut(7)
        );

        JLabel subtitle =
                new JLabel(
                        "PLAY CHESS WITH PLAYERS AROUND THE WORLD"
                );

        subtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        subtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        10
                )
        );

        subtitle.setForeground(
                new Color(115, 170, 235)
        );

        content.add(subtitle);

        content.add(
                Box.createVerticalStrut(12)
        );

        identityLabel = new JLabel();

        identityLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        identityLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        identityLabel.setForeground(TEXT);

        updateIdentity();

        content.add(identityLabel);

        content.add(
                Box.createVerticalStrut(22)
        );

        // =====================================================
        // CONNECTION
        // =====================================================

        JPanel connectionPanel =
                createSectionPanel();

        JLabel serverLabel =
                createLabel(
                        "OBITRICY ONLINE SERVER"
                );

        serverLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        connectionPanel.add(serverLabel);

        connectionPanel.add(
                Box.createVerticalStrut(5)
        );

        JLabel endpointLabel =
                new JLabel(
                        OnlineServerConfig.SECURE
                                ? "● Secure Internet connection"
                                : "● Development connection"
                );

        endpointLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        endpointLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        endpointLabel.setForeground(
                OnlineServerConfig.SECURE
                        ? new Color(105, 210, 135)
                        : MUTED
        );

        connectionPanel.add(endpointLabel);

        connectionPanel.add(
                Box.createVerticalStrut(13)
        );

        connectButton =
                createButton("Connect Online");

        connectButton.addActionListener(
                e -> connectToServer()
        );

        connectionPanel.add(
                centerComponent(connectButton)
        );

        content.add(connectionPanel);

        content.add(
                Box.createVerticalStrut(15)
        );

        // =====================================================
        // ONLINE PLAYERS
        // =====================================================

        JPanel playersPanel =
                createSectionPanel();

        JLabel playersTitle =
                createLabel("ONLINE PLAYERS");

        playersTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        playersPanel.add(playersTitle);

        playersPanel.add(
                Box.createVerticalStrut(10)
        );

        onlinePlayersModel =
                new DefaultListModel<>();

        onlinePlayersList =
                new JList<>(
                        onlinePlayersModel
                );

        onlinePlayersList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        onlinePlayersList.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        onlinePlayersList.setVisibleRowCount(7);

        onlinePlayersList.setBackground(
                new Color(12, 18, 27)
        );

        onlinePlayersList.setForeground(TEXT);

        onlinePlayersList.setSelectionBackground(
                new Color(53, 83, 119)
        );

        onlinePlayersList.setSelectionForeground(
                Color.WHITE
        );

        onlinePlayersList.setBorder(
                BorderFactory.createEmptyBorder(
                        6,
                        8,
                        6,
                        8
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        onlinePlayersList
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        scrollPane.getViewport().setBackground(
                new Color(12, 18, 27)
        );

        playersPanel.add(scrollPane);

        playersPanel.add(
                Box.createVerticalStrut(11)
        );

        JPanel playerButtons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                0
                        )
                );

        playerButtons.setOpaque(false);

        refreshButton =
                createButton("Refresh");

        refreshButton.setPreferredSize(
                new Dimension(130, 40)
        );

        refreshButton.addActionListener(
                e -> requestLobbyRefresh()
        );

        challengeButton =
                createButton("Play Selected");

        challengeButton.setPreferredSize(
                new Dimension(160, 40)
        );

        challengeButton.setEnabled(false);

        challengeButton.addActionListener(
                e -> challengeSelectedPlayer()
        );

        playerButtons.add(refreshButton);
        playerButtons.add(challengeButton);

        playersPanel.add(playerButtons);

        content.add(playersPanel);

        onlinePlayersList.addListSelectionListener(
                e -> {
                    if (!e.getValueIsAdjusting()) {
                        challengeButton.setEnabled(
                                connected
                                        && onlinePlayersList
                                        .getSelectedValue() != null
                        );
                    }
                }
        );

        content.add(
                Box.createVerticalStrut(15)
        );

        // =====================================================
        // STATUS
        // =====================================================

        statusLabel =
                new JLabel(
                        "Status: Not connected"
                );

        statusLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        statusLabel.setForeground(MUTED);

        statusLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        content.add(statusLabel);

        content.add(
                Box.createVerticalStrut(15)
        );

        // =====================================================
        // MAIN MENU
        // =====================================================

        mainMenuButton =
                createButton("Main Menu");

        mainMenuButton.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        mainMenuButton.addActionListener(
                e -> {
                    client.disconnect();
                    connected = false;
                    mainFrame.showMenu();
                }
        );

        content.add(mainMenuButton);

        add(
                content,
                BorderLayout.CENTER
        );
    }

    /**
     * Called when the online lobby is actually opened.
     *
     * The panel is constructed during MainFrame startup, before
     * the user has logged in. Connecting from the constructor
     * therefore registered the default name "Player" on the server.
     * We now wait until the lobby is opened, when the logged-in
     * username is available.
     */
    public void openLobby() {

        updateIdentity();

        if (connected) {
            client.setPlayerName(
                    mainFrame.getCurrentUsername()
            );
            return;
        }

        connectToServer();
    }

    private void connectToServer() {

        if (connected) {

            client.disconnect();

            connected = false;

            connectButton.setText(
                    "Connect to Server"
            );

            setStatus("Disconnected.");

            onlinePlayersModel.clear();

            challengeButton.setEnabled(false);

            return;
        }

        String host =
                OnlineServerConfig.host();

        int port =
                OnlineServerConfig.port();

        client.setPlayerName(
                mainFrame.getCurrentUsername()
        );

        setStatus("Connecting...");

        connectButton.setEnabled(false);

        Thread connectionThread =
                new Thread(
                        () -> {
                            try {

                                client.connect(
                                        host,
                                        port,
                                        this::handleServerMessage
                                );

                                SwingUtilities.invokeLater(
                                        () -> {

                                            connected = true;

                                            connectButton.setText(
                                                    "Disconnect"
                                            );

                                            connectButton.setEnabled(
                                                    true
                                            );

                                            setStatus(
                                                    "Connected. Looking for online players..."
                                            );
                                        }
                                );

                            } catch (Exception ex) {

                                SwingUtilities.invokeLater(
                                        () -> {

                                            connectButton.setEnabled(
                                                    true
                                            );

                                            setStatus(
                                                    "Connection failed."
                                            );

                                            showError(
                                                    "Unable to connect to server:\n"
                                                            + ex.getMessage()
                                            );
                                        }
                                );
                            }
                        },
                        "ChessServerConnection"
                );

        connectionThread.start();
    }

    private void requestLobbyRefresh() {

        if (!connected) {

            showError(
                    "Connect to the server first."
            );

            return;
        }

        client.setPlayerName(
                mainFrame.getCurrentUsername()
        );

        setStatus(
                "Refreshing online players..."
        );
    }

    private void challengeSelectedPlayer() {

        String selected =
                onlinePlayersList.getSelectedValue();

        if (!connected
                || selected == null
                || selected.isBlank()) {
            return;
        }

        client.requestPlay(selected);

        challengeButton.setEnabled(false);

        setStatus(
                "Play request sent to "
                        + selected
                        + ". Waiting for response..."
        );
    }

    private void handleServerMessage(
            NetworkMessage message
    ) {

        if (message == null) {
            return;
        }

        SwingUtilities.invokeLater(
                () -> {

                    switch (message.getType()) {

                        case WAITING ->
                                setStatus(
                                        message.getData()
                                );

                        case ONLINE_PLAYERS ->
                                updateOnlinePlayers(
                                        message.getData()
                                );

                        case PLAY_REQUEST_SENT ->
                                setStatus(
                                        "Play request sent to "
                                                + message.getData()
                                                + ". Waiting..."
                                );

                        case PLAY_REQUEST_RECEIVED ->
                                showPlayRequest(
                                        message.getData()
                                );

                        case PLAY_REQUEST_DECLINE -> {

                            setStatus(
                                    message.getData()
                                            + " declined the play request."
                            );

                            challengeButton.setEnabled(
                                    onlinePlayersList
                                            .getSelectedValue()
                                            != null
                            );
                        }

                        case GAME_CREATED ->
                                handleGameCreated(
                                        message.getData()
                                );

                        case GAME_JOINED ->
                                handleGameJoined(
                                        message.getData()
                                );

                        case START_GAME ->
                                handleStartGame(
                                        message.getData()
                                );

                        case ERROR, MOVE_REJECTED ->
                                handleError(
                                        message.getData()
                                );

                        case DISCONNECT ->
                                handleOpponentDisconnect(
                                        message.getData()
                                );

                        default -> {
                            // No action required.
                        }
                    }
                }
        );
    }

    private void updateOnlinePlayers(
            String data
    ) {

        String selected =
                onlinePlayersList.getSelectedValue();

        onlinePlayersModel.clear();

        if (data != null
                && !data.isBlank()) {

            for (String name :
                    data.split(",")) {

                String clean =
                        name.trim();

                if (!clean.isBlank()
                        && !clean.equalsIgnoreCase(
                        mainFrame.getCurrentUsername()
                )) {

                    onlinePlayersModel.addElement(
                            clean
                    );
                }
            }
        }

        if (selected != null) {

            onlinePlayersList.setSelectedValue(
                    selected,
                    true
            );
        }

        setStatus(
                onlinePlayersModel.isEmpty()
                        ? "No other players are currently online."
                        : "Online players updated."
        );
    }

    // =========================================================
    // PREMIUM PLAY REQUEST
    // =========================================================

    private void showPlayRequest(
            String requester
    ) {

        boolean accepted =
                ObitricyDialog.confirm(
                        this,
                        "Chess Challenge",
                        requester
                                + " wants to play a game with you."
                                + "\n\n"
                                + "Accept the challenge?"
                );

        if (accepted) {

            client.acceptPlayRequest(
                    requester
            );

            setStatus(
                    "Accepting challenge from "
                            + requester
                            + "..."
            );

        } else {

            client.declinePlayRequest(
                    requester
            );

            setStatus(
                    "Declined challenge from "
                            + requester
                            + "."
            );
        }
    }

    private void handleGameCreated(
            String data
    ) {

        String[] parts =
                data.split("\\|", 2);

        gameId =
                parts.length > 0
                        ? parts[0]
                        : "";

        playerIsWhite = true;

        setStatus(
                "Game "
                        + gameId
                        + " created. Waiting for opponent..."
        );
    }

    private void handleGameJoined(
            String data
    ) {

        String[] parts =
                data.split("\\|", 2);

        gameId =
                parts.length > 0
                        ? parts[0]
                        : "";

        playerIsWhite = false;

        setStatus(
                "Joined game "
                        + gameId
                        + ". Waiting for game to start..."
        );
    }

    // =========================================================
    // PREMIUM GAME READY POPUP
    // =========================================================

    private void handleStartGame(
            String data
    ) {

        String[] parts =
                data.split("\\|", -1);

        if (parts.length < 2) {
            return;
        }

        gameId =
                parts[0];

        playerIsWhite =
                "WHITE".equalsIgnoreCase(
                        parts[1]
                );

        String opponent =
                parts.length >= 3
                        ? parts[2]
                        : "Opponent";

        setStatus(
                "Game starting against "
                        + opponent
                        + "..."
        );

        /*
         * IMPORTANT:
         * The online game is started exactly as before.
         * The popup is only presentation.
         */
        mainFrame.startOnlineGame(
                client,
                gameId,
                playerIsWhite
        );

        ObitricyDialog.showOnline(
                this,
                "Online Game Ready",
                "Your online game is ready!"
                        + "\n\nOpponent: "
                        + opponent
                        + "\nYou are "
                        + (
                        playerIsWhite
                                ? "WHITE"
                                : "BLACK"
                )
        );
    }

    private void handleError(
            String message
    ) {

        setStatus(
                "Error: " + message
        );

        showError(message);

        challengeButton.setEnabled(
                connected
                        && onlinePlayersList
                        .getSelectedValue()
                        != null
        );
    }

    // =========================================================
    // PREMIUM DISCONNECT POPUP
    // =========================================================

    private void handleOpponentDisconnect(
            String message
    ) {

        setStatus(message);

        ObitricyDialog.showWarning(
                this,
                "Opponent Disconnected",
                message
        );
    }

    private void updateIdentity() {

        identityLabel.setText(
                "Logged in as: "
                        + mainFrame.getCurrentUsername()
        );
    }

    // =========================================================
    // LOBBY UI HELPERS
    // =========================================================

    private JPanel createSectionPanel() {

        JPanel panel =
                new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBackground(PANEL);

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                16,
                                16,
                                16,
                                16
                        )
                )
        );

        panel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        return panel;
    }

    private JLabel createLabel(
            String text
    ) {

        JLabel label =
                new JLabel(text);

        label.setForeground(TEXT);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return label;
    }

    private JButton createButton(
            String text
    ) {

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
                new Color(12, 15, 20)
        );

        button.setBackground(GOLD);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        18,
                        8,
                        18
                )
        );

        button.setPreferredSize(
                new Dimension(220, 42)
        );

        button.setMaximumSize(
                new Dimension(300, 42)
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    private void styleTextField(
            JTextField field
    ) {

        field.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        field.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        40
                )
        );
    }

    private JPanel centerComponent(
            JComponent component
    ) {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER
                        )
                );

        panel.setBackground(PANEL);

        panel.add(component);

        return panel;
    }

    private void setStatus(
            String status
    ) {

        statusLabel.setText(
                "Status: " + status
        );
    }

    // =========================================================
    // PREMIUM ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        ObitricyDialog.showError(
                this,
                "Online Multiplayer",
                message == null
                        ? "Unknown error."
                        : message
        );
    }

    // =========================================================
    // PUBLIC ACCESSORS
    // =========================================================

    public MultiplayerClient getClient() {
        return client;
    }

    public boolean isPlayerWhite() {
        return playerIsWhite;
    }

    public String getGameId() {
        return gameId;
    }
}
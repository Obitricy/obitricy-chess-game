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
 * Development builds still expose the server/port fields so two laptops can
 * be tested easily. Once the public server is deployed these can be replaced
 * by a single automatic connection to the Obitricy Chess server.
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

    public OnlineMultiplayerPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(35, 35, 35));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(35, 35, 35));
        content.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel title = new JLabel("ONLINE MULTIPLAYER");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        content.add(title);
        content.add(Box.createVerticalStrut(10));

        identityLabel = new JLabel();
        identityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        identityLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        identityLabel.setForeground(new Color(210, 210, 210));
        updateIdentity();
        content.add(identityLabel);
        content.add(Box.createVerticalStrut(20));

        JPanel connectionPanel = createSectionPanel();
        JLabel serverLabel = createLabel("OBITRICY ONLINE SERVER");
        serverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectionPanel.add(serverLabel);
        JLabel endpointLabel = new JLabel(OnlineServerConfig.SECURE ? "Secure Internet connection" : "Development connection");
        endpointLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        endpointLabel.setForeground(new Color(180, 180, 180));
        connectionPanel.add(endpointLabel);

        connectButton = createButton("Connect Online");
        connectButton.addActionListener(e -> connectToServer());
        connectionPanel.add(centerComponent(connectButton));
        content.add(connectionPanel);
        content.add(Box.createVerticalStrut(15));

        JPanel playersPanel = createSectionPanel();
        JLabel playersTitle = createLabel("ONLINE PLAYERS");
        playersPanel.add(playersTitle);
        playersPanel.add(Box.createVerticalStrut(8));

        onlinePlayersModel = new DefaultListModel<>();
        onlinePlayersList = new JList<>(onlinePlayersModel);
        onlinePlayersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        onlinePlayersList.setFont(new Font("Segoe UI", Font.BOLD, 15));
        onlinePlayersList.setVisibleRowCount(7);
        onlinePlayersList.setBackground(new Color(40, 40, 40));
        onlinePlayersList.setForeground(Color.WHITE);
        onlinePlayersList.setSelectionBackground(new Color(85, 105, 135));
        onlinePlayersList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        playersPanel.add(new JScrollPane(onlinePlayersList));
        playersPanel.add(Box.createVerticalStrut(10));

        JPanel playerButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        playerButtons.setOpaque(false);
        refreshButton = createButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(130, 40));
        refreshButton.addActionListener(e -> requestLobbyRefresh());

        challengeButton = createButton("Play Selected");
        challengeButton.setPreferredSize(new Dimension(160, 40));
        challengeButton.setEnabled(false);
        challengeButton.addActionListener(e -> challengeSelectedPlayer());

        playerButtons.add(refreshButton);
        playerButtons.add(challengeButton);
        playersPanel.add(playerButtons);
        content.add(playersPanel);

        onlinePlayersList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                challengeButton.setEnabled(connected && onlinePlayersList.getSelectedValue() != null);
            }
        });

        content.add(Box.createVerticalStrut(15));

        statusLabel = new JLabel("Status: Not connected");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(new Color(220, 220, 220));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        content.add(statusLabel);
        content.add(Box.createVerticalStrut(15));

        mainMenuButton = createButton("Main Menu");
        mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainMenuButton.addActionListener(e -> {
            client.disconnect();
            connected = false;
            mainFrame.showMenu();
        });
        content.add(mainMenuButton);

        add(content, BorderLayout.CENTER);
    }

    /**
     * Called when the online lobby is actually opened.
     *
     * The panel is constructed during MainFrame startup, before the user has
     * logged in. Connecting from the constructor therefore registered the
     * default name "Player" on the server. We now wait until the lobby is
     * opened, when the logged-in username is available.
     */
    public void openLobby() {
        updateIdentity();

        if (connected) {
            client.setPlayerName(mainFrame.getCurrentUsername());
            return;
        }

        connectToServer();
    }

    private void connectToServer() {
        if (connected) {
            client.disconnect();
            connected = false;
            connectButton.setText("Connect to Server");
            setStatus("Disconnected.");
            onlinePlayersModel.clear();
            challengeButton.setEnabled(false);
            return;
        }

        String host = OnlineServerConfig.host();
        int port = OnlineServerConfig.port();

        client.setPlayerName(mainFrame.getCurrentUsername());
        setStatus("Connecting...");
        connectButton.setEnabled(false);

        Thread connectionThread = new Thread(() -> {
            try {
                client.connect(host, port, this::handleServerMessage);
                SwingUtilities.invokeLater(() -> {
                    connected = true;
                    connectButton.setText("Disconnect");
                    connectButton.setEnabled(true);
                    setStatus("Connected. Looking for online players...");
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    connectButton.setEnabled(true);
                    setStatus("Connection failed.");
                    showError("Unable to connect to server:\n" + ex.getMessage());
                });
            }
        }, "ChessServerConnection");
        connectionThread.start();
    }

    private void requestLobbyRefresh() {
        if (!connected) {
            showError("Connect to the server first.");
            return;
        }
        client.setPlayerName(mainFrame.getCurrentUsername());
        setStatus("Refreshing online players...");
    }

    private void challengeSelectedPlayer() {
        String selected = onlinePlayersList.getSelectedValue();
        if (!connected || selected == null || selected.isBlank()) return;

        client.requestPlay(selected);
        challengeButton.setEnabled(false);
        setStatus("Play request sent to " + selected + ". Waiting for response...");
    }

    private void handleServerMessage(NetworkMessage message) {
        if (message == null) return;

        SwingUtilities.invokeLater(() -> {
            switch (message.getType()) {
                case WAITING -> setStatus(message.getData());
                case ONLINE_PLAYERS -> updateOnlinePlayers(message.getData());
                case PLAY_REQUEST_SENT -> setStatus("Play request sent to " + message.getData() + ". Waiting...");
                case PLAY_REQUEST_RECEIVED -> showPlayRequest(message.getData());
                case PLAY_REQUEST_DECLINE -> {
                    setStatus(message.getData() + " declined the play request.");
                    challengeButton.setEnabled(onlinePlayersList.getSelectedValue() != null);
                }
                case GAME_CREATED -> handleGameCreated(message.getData());
                case GAME_JOINED -> handleGameJoined(message.getData());
                case START_GAME -> handleStartGame(message.getData());
                case ERROR, MOVE_REJECTED -> handleError(message.getData());
                case DISCONNECT -> handleOpponentDisconnect(message.getData());
                default -> { }
            }
        });
    }

    private void updateOnlinePlayers(String data) {
        String selected = onlinePlayersList.getSelectedValue();
        onlinePlayersModel.clear();

        if (data != null && !data.isBlank()) {
            for (String name : data.split(",")) {
                String clean = name.trim();
                if (!clean.isBlank() && !clean.equalsIgnoreCase(mainFrame.getCurrentUsername())) {
                    onlinePlayersModel.addElement(clean);
                }
            }
        }

        if (selected != null) onlinePlayersList.setSelectedValue(selected, true);
        setStatus(onlinePlayersModel.isEmpty()
                ? "No other players are currently online."
                : "Online players updated.");
    }

    private void showPlayRequest(String requester) {
        int result = JOptionPane.showConfirmDialog(
                this,
                requester + " wants to play a game with you.\n\nAccept the challenge?",
                "Chess Game Request",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            client.acceptPlayRequest(requester);
            setStatus("Accepting challenge from " + requester + "...");
        } else {
            client.declinePlayRequest(requester);
            setStatus("Declined challenge from " + requester + ".");
        }
    }

    private void handleGameCreated(String data) {
        String[] parts = data.split("\\|", 2);
        gameId = parts.length > 0 ? parts[0] : "";
        playerIsWhite = true;
        setStatus("Game " + gameId + " created. Waiting for opponent...");
    }

    private void handleGameJoined(String data) {
        String[] parts = data.split("\\|", 2);
        gameId = parts.length > 0 ? parts[0] : "";
        playerIsWhite = false;
        setStatus("Joined game " + gameId + ". Waiting for game to start...");
    }

    private void handleStartGame(String data) {
        String[] parts = data.split("\\|", -1);
        if (parts.length < 2) return;

        gameId = parts[0];
        playerIsWhite = "WHITE".equalsIgnoreCase(parts[1]);
        String opponent = parts.length >= 3 ? parts[2] : "Opponent";

        setStatus("Game starting against " + opponent + "...");

        mainFrame.startOnlineGame(client, gameId, playerIsWhite);

        JOptionPane.showMessageDialog(
                this,
                "Online game ready!\n\nOpponent: " + opponent + "\nYou are "
                        + (playerIsWhite ? "WHITE" : "BLACK"),
                "Online Multiplayer",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleError(String message) {
        setStatus("Error: " + message);
        showError(message);
        challengeButton.setEnabled(connected && onlinePlayersList.getSelectedValue() != null);
    }

    private void handleOpponentDisconnect(String message) {
        setStatus(message);
        JOptionPane.showMessageDialog(this, message, "Online Multiplayer", JOptionPane.WARNING_MESSAGE);
    }

    private void updateIdentity() {
        identityLabel.setText("Logged in as: " + mainFrame.getCurrentUsername());
    }

    private JPanel createSectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(50, 50, 50));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(90, 90, 90)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(220, 42));
        button.setMaximumSize(new Dimension(300, 42));
        return button;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private JPanel centerComponent(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(50, 50, 50));
        panel.add(component);
        return panel;
    }

    private void setStatus(String status) {
        statusLabel.setText("Status: " + status);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message == null ? "Unknown error." : message,
                "Online Multiplayer",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public MultiplayerClient getClient() { return client; }
    public boolean isPlayerWhite() { return playerIsWhite; }
    public String getGameId() { return gameId; }
}

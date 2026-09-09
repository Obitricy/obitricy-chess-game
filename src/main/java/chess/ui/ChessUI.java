package chess.ui;

import chess.board.BoardView;
import chess.board.ChessBoard;
import chess.board.GameState;
import chess.game.GameMode;
import chess.game.Move;
import chess.pieces.Bishop;
import chess.pieces.Knight;
import chess.pieces.Piece;
import chess.pieces.Queen;
import chess.pieces.Rook;
import chess.multiplayer.MultiplayerClient;
import chess.multiplayer.NetworkMessage;
import chess.save.SaveManager;

import javax.swing.*;
import java.awt.*;


/**
 * Main in-game UI.
 * The chess engine remains inside ChessBoard/GameState.
 * This class is responsible only for the presentation layer
 * and connecting UI controls to the existing game engine.
 */
public class ChessUI extends JPanel {

    // =========================================================
    // FIXED LAYOUT DIMENSIONS
    // =========================================================

    /*
     * Both player panels use exactly the same width.
     *
     * The toolbar is separate from the BLACK player panel,
     * so the BLACK and WHITE collection areas remain equal.
     */
    private static final int PLAYER_CARD_WIDTH = 190;

    private static final int TOOLBAR_WIDTH = 78;


    // =========================================================
    // GAME
    // =========================================================

    private final MainFrame mainFrame;
    private final GameState gameState;

    private final MultiplayerClient multiplayerClient;
    private final boolean playerIsWhite;

    private ChessBoard chessBoard;

    private boolean onlineGame;
    /*
     * Online game-over/rematch state.
     *
     * onlineGameOver becomes true when the online game ends
     * by resignation.
     *
     * rematchRequested prevents the same player from sending
     * repeated rematch requests while waiting for the opponent.
     */
    private boolean onlineGameOver;
    private boolean rematchRequested;


    // =========================================================
    // ROOT UI
    // =========================================================

    private JPanel gameRoot;

    private JPanel leftToolbar;

    private JPanel bottomBar;

    private JPanel bottomLeftStatus;
    private JPanel bottomRightStatus;

    private CapturedTrayPanel blackCapturedTray;
    private CapturedTrayPanel whiteCapturedTray;


    // =========================================================
    // PLAYER DISPLAY
    // =========================================================

    private JLabel blackPlayerLabel;
    private JLabel whitePlayerLabel;

    private JPanel blackPlayerCard;
    private JPanel whitePlayerCard;


    // =========================================================
    // STATUS
    // =========================================================

    private JLabel moveStatusLabel;
    private JLabel difficultyLabel;


    // =========================================================
    // BUTTONS
    // =========================================================

    private JButton undoButton;
    private JButton redoButton;
    private JButton resetButton;
    private JButton hintButton;
    private JButton settingsButton;
    private JButton menuButton;
    private JButton nextPuzzleButton;


    // =========================================================
    // NORMAL / OFFLINE GAME
    // =========================================================

    public ChessUI(
            MainFrame mainFrame,
            GameState gameState) {

        this(
                mainFrame,
                gameState,
                BoardView.THREE_D
        );
    }


    // =========================================================
    // NORMAL / OFFLINE GAME + BOARD VIEW
    // =========================================================

    public ChessUI(
            MainFrame mainFrame,
            GameState gameState,
            BoardView boardView) {

        this.mainFrame = mainFrame;
        this.gameState = gameState;

        this.multiplayerClient = null;
        this.playerIsWhite = true;

        this.onlineGame = false;

        chessBoard =
                new ChessBoard(
                        gameState
                );

        if (boardView != null) {

            chessBoard.setBoardView(
                    boardView
            );
        }

        initializeUI();
    }


    // =========================================================
    // ONLINE / MULTIPLAYER GAME
    // =========================================================

    public ChessUI(
            MainFrame mainFrame,
            GameState gameState,
            MultiplayerClient multiplayerClient,
            boolean playerIsWhite,
            BoardView boardView) {

        this.mainFrame = mainFrame;
        this.gameState = gameState;

        this.multiplayerClient =
                multiplayerClient;

        this.playerIsWhite =
                playerIsWhite;

        this.onlineGame =
                multiplayerClient != null
                        && gameState.getGameMode()
                        == GameMode.ONLINE;

        this.onlineGameOver = false;
        this.rematchRequested = false;

        chessBoard =
                new ChessBoard(
                        gameState
                );

        if (boardView != null) {

            chessBoard.setBoardView(
                    boardView
            );
        }

        // =====================================================
        // CONFIGURE ONLINE PLAY
        // =====================================================

        if (onlineGame) {

            chessBoard.setOnlineGame(
                    true,
                    playerIsWhite
            );

            /*
             * Local move -> network.
             */
            chessBoard.setOnOnlineMove(
                    this::sendOnlineMove
            );

            /*
             * Network -> local board.
             */
            multiplayerClient.setMessageListener(
                    this::handleNetworkMessage
            );
        }

        initializeUI();
    }


    // =========================================================
    // ONLINE COMPATIBILITY CONSTRUCTOR
    // =========================================================

    public ChessUI(
            MainFrame mainFrame,
            GameState gameState,
            MultiplayerClient multiplayerClient,
            boolean playerIsWhite) {

        this(
                mainFrame,
                gameState,
                multiplayerClient,
                playerIsWhite,
                BoardView.THREE_D
        );
    }


    // =========================================================
    // INITIALIZE UI
    // =========================================================

    private void initializeUI() {

        setLayout(
                new BorderLayout()
        );

        setOpaque(false);

        setBackground(
                new Color(
                        55,
                        32,
                        18
                )
        );


        // =====================================================
        // BOARD CALLBACKS
        // =====================================================

        chessBoard.setOnGameOver(
                this::showGameOverDialog
        );


        chessBoard.setOnPuzzleSolved(() -> {

            if (nextPuzzleButton != null) {

                nextPuzzleButton.setEnabled(
                        true
                );
            }

            updateStatusBar();
        });


        chessBoard.setOnMoveCallback(
                this::handleBoardMove
        );


        // =====================================================
        // ROOT
        // =====================================================

        gameRoot =
                new JPanel(
                        new BorderLayout()
                );

        gameRoot.setOpaque(false);


        // =====================================================
        // CREATE LEFT TOOLBAR
        // =====================================================

        createLeftToolbar();


        // =====================================================
        // CREATE BLACK PLAYER CARD
        // =====================================================

        blackPlayerCard =
                createPlayerCard(
                        "BLACK",
                        gameState
                                .getBlackPlayer()
                                .getName()
                );

        blackPlayerLabel =
                (JLabel) blackPlayerCard.getClientProperty(
                        "playerNameLabel"
                );


        // =====================================================
        // BLACK CAPTURED PIECES
        // =====================================================

        blackCapturedTray =
                new CapturedTrayPanel(
                        gameState,
                        CapturedTrayPanel.Side.BLACK
                );

        blackPlayerCard.add(
                blackCapturedTray,
                BorderLayout.CENTER
        );


        // =====================================================
        // LEFT RAIL
        // =====================================================
        //
        // IMPORTANT:
        //
        // Toolbar has its own width.
        // Black player card has its own fixed width.
        //
        // This prevents the toolbar from shrinking the
        // BLACK collection area.
        //
        // BLACK CARD = 190 px
        // WHITE CARD = 190 px
        // TOOLBAR    = 78 px
        //
        // =====================================================

        JPanel leftRail =
                new JPanel(
                        new BorderLayout()
                );

        leftRail.setOpaque(false);

        leftRail.setPreferredSize(
                new Dimension(
                        TOOLBAR_WIDTH
                                + PLAYER_CARD_WIDTH,
                        0
                )
        );

        leftRail.setMinimumSize(
                new Dimension(
                        TOOLBAR_WIDTH
                                + PLAYER_CARD_WIDTH,
                        0
                )
        );

        leftRail.setMaximumSize(
                new Dimension(
                        TOOLBAR_WIDTH
                                + PLAYER_CARD_WIDTH,
                        Integer.MAX_VALUE
                )
        );


        // =====================================================
        // TOOLBAR FIXED WIDTH
        // =====================================================

        leftToolbar.setPreferredSize(
                new Dimension(
                        TOOLBAR_WIDTH,
                        0
                )
        );

        leftToolbar.setMinimumSize(
                new Dimension(
                        TOOLBAR_WIDTH,
                        0
                )
        );

        leftToolbar.setMaximumSize(
                new Dimension(
                        TOOLBAR_WIDTH,
                        Integer.MAX_VALUE
                )
        );


        leftRail.add(
                leftToolbar,
                BorderLayout.WEST
        );


        // =====================================================
        // BLACK PLAYER CARD FIXED WIDTH
        // =====================================================

        blackPlayerCard.setPreferredSize(
                new Dimension(
                        PLAYER_CARD_WIDTH,
                        0
                )
        );

        blackPlayerCard.setMinimumSize(
                new Dimension(
                        PLAYER_CARD_WIDTH,
                        0
                )
        );

        blackPlayerCard.setMaximumSize(
                new Dimension(
                        PLAYER_CARD_WIDTH,
                        Integer.MAX_VALUE
                )
        );


        leftRail.add(
                blackPlayerCard,
                BorderLayout.CENTER
        );


        gameRoot.add(
                leftRail,
                BorderLayout.WEST
        );


        // =====================================================
        // BOARD
        // =====================================================

        gameRoot.add(
                chessBoard,
                BorderLayout.CENTER
        );


        // =====================================================
        // CREATE WHITE PLAYER CARD
        // =====================================================

        whitePlayerCard =
                createPlayerCard(
                        "WHITE",
                        gameState
                                .getWhitePlayer()
                                .getName()
                );

        whitePlayerLabel =
                (JLabel) whitePlayerCard.getClientProperty(
                        "playerNameLabel"
                );


        // =====================================================
        // WHITE CAPTURED PIECES
        // =====================================================

        whiteCapturedTray =
                new CapturedTrayPanel(
                        gameState,
                        CapturedTrayPanel.Side.WHITE
                );

        whitePlayerCard.add(
                whiteCapturedTray,
                BorderLayout.CENTER
        );


        // =====================================================
        // RIGHT RAIL
        // =====================================================

        JPanel rightRail =
                new JPanel(
                        new BorderLayout()
                );

        rightRail.setOpaque(false);

        rightRail.setPreferredSize(
                new Dimension(
                        PLAYER_CARD_WIDTH,
                        0
                )
        );

        rightRail.setMinimumSize(
                new Dimension(
                        PLAYER_CARD_WIDTH,
                        0
                )
        );

        rightRail.setMaximumSize(
                new Dimension(
                        PLAYER_CARD_WIDTH,
                        Integer.MAX_VALUE
                )
        );


        // =====================================================
        // WHITE PLAYER CARD FIXED WIDTH
        // =====================================================

        whitePlayerCard.setPreferredSize(
                new Dimension(
                        PLAYER_CARD_WIDTH,
                        0
                )
        );

        whitePlayerCard.setMinimumSize(
                new Dimension(
                        PLAYER_CARD_WIDTH,
                        0
                )
        );

        whitePlayerCard.setMaximumSize(
                new Dimension(
                        PLAYER_CARD_WIDTH,
                        Integer.MAX_VALUE
                )
        );


        rightRail.add(
                whitePlayerCard,
                BorderLayout.CENTER
        );


        gameRoot.add(
                rightRail,
                BorderLayout.EAST
        );


        // =====================================================
        // BOTTOM STATUS BAR
        // =====================================================

        createBottomStatusBar();

        gameRoot.add(
                bottomBar,
                BorderLayout.SOUTH
        );


        // =====================================================
        // ADD ROOT
        // =====================================================

        add(
                gameRoot,
                BorderLayout.CENTER
        );


        // =====================================================
        // PUZZLE BUTTON
        // =====================================================

        nextPuzzleButton =
                new JButton(
                        "NEXT PUZZLE"
                );

        nextPuzzleButton.setEnabled(
                false
        );

        nextPuzzleButton.addActionListener(
                e -> {

                    chessBoard.nextPuzzle();

                    nextPuzzleButton.setEnabled(
                            false
                    );

                    updateStatusBar();
                }
        );


        // =====================================================
        // INITIAL STATE
        // =====================================================

        updateStatusBar();
    }


    // =========================================================
    // PLAYER CARD
    // =========================================================

    private JPanel createPlayerCard(
            String side,
            String playerName) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setOpaque(true);

        card.setBackground(
                new Color(
                        38,
                        22,
                        13,
                        235
                )
        );

        card.setBorder(
                BorderFactory.createCompoundBorder(

                        BorderFactory.createLineBorder(
                                new Color(
                                        164,
                                        101,
                                        42
                                ),
                                2
                        ),

                        BorderFactory.createEmptyBorder(
                                6,
                                6,
                                6,
                                6
                        )
                )
        );


        // =====================================================
        // HEADER
        // =====================================================

        JPanel header =
                new JPanel(
                        new GridBagLayout()
                );

        header.setOpaque(true);

        header.setBackground(
                new Color(
                        52,
                        30,
                        16,
                        245
                )
        );

        header.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        8,
                        10,
                        8
                )
        );


        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1.0;


        // =====================================================
        // SIDE
        // =====================================================

        JLabel sideLabel =
                new JLabel(
                        side,
                        SwingConstants.CENTER
                );

        sideLabel.setForeground(
                new Color(
                        245,
                        190,
                        60
                )
        );

        sideLabel.setFont(
                new Font(
                        "Georgia",
                        Font.BOLD,
                        14
                )
        );

        gbc.gridy = 0;

        header.add(
                sideLabel,
                gbc
        );


        // =====================================================
        // PLAYER NAME
        // =====================================================

        String safeName =
                playerName == null
                        || playerName.isBlank()
                        ? side
                        : playerName.trim();


        String displayName =
                safeName;


        if (displayName.length() > 18) {

            displayName =
                    displayName.substring(
                            0,
                            17
                    )
                            + "...";
        }


        JLabel nameLabel =
                new JLabel(
                        displayName,
                        SwingConstants.CENTER
                );

        nameLabel.setForeground(
                Color.WHITE
        );

        nameLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        nameLabel.setToolTipText(
                safeName
        );


        gbc.gridy = 1;

        gbc.insets =
                new Insets(
                        4,
                        0,
                        0,
                        0
                );


        header.add(
                nameLabel,
                gbc
        );


        // =====================================================
        // ADD HEADER
        // =====================================================

        card.add(
                header,
                BorderLayout.NORTH
        );


        // =====================================================
        // STORE NAME LABEL
        // =====================================================

        card.putClientProperty(
                "playerNameLabel",
                nameLabel
        );


        return card;
    }


    // =========================================================
    // BACKGROUND
    // =========================================================

    @Override
    protected void paintComponent(
            Graphics g) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        try {

            int w =
                    getWidth();

            int h =
                    getHeight();


            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );


            // =================================================
            // WOOD BASE
            // =================================================

            g2.setPaint(
                    new GradientPaint(
                            0,
                            0,
                            new Color(
                                    92,
                                    56,
                                    30
                            ),

                            0,
                            h,
                            new Color(
                                    43,
                                    24,
                                    14
                            )
                    )
            );

            g2.fillRect(
                    0,
                    0,
                    w,
                    h
            );


            // =================================================
            // WOOD GRAIN LINES
            // =================================================

            for (
                    int y = 0;
                    y < h;
                    y += 70
            ) {

                g2.setColor(
                        new Color(
                                25,
                                13,
                                7,
                                42
                        )
                );

                g2.fillRect(
                        0,
                        y + 67,
                        w,
                        3
                );
            }


            // =================================================
            // WOOD GRAIN ARCS
            // =================================================

            for (
                    int i = 0;
                    i < 90;
                    i++
            ) {

                int x =
                        Math.floorMod(
                                i * 149,
                                Math.max(
                                        1,
                                        w
                                )
                        );

                int y =
                        Math.floorMod(
                                i * 91,
                                Math.max(
                                        1,
                                        h
                                )
                        );

                int length =
                        35
                                + Math.floorMod(
                                i * 71,
                                160
                        );


                g2.setColor(
                        new Color(
                                20,
                                10,
                                5,
                                22
                        )
                );


                g2.drawArc(
                        x,
                        y,
                        length,
                        18 + (i % 6) * 3,
                        165,
                        130
                );
            }


            // =================================================
            // VIGNETTE
            // =================================================

            RadialGradientPaint vignette =
                    new RadialGradientPaint(

                            new Point(
                                    w / 2,
                                    h / 2
                            ),

                            Math.max(
                                    w,
                                    h
                            ) * 0.72f,

                            new float[]{
                                    0.50f,
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
                                            95
                                    )
                            }
                    );


            g2.setPaint(
                    vignette
            );

            g2.fillRect(
                    0,
                    0,
                    w,
                    h
            );

        } finally {

            g2.dispose();
        }
    }


    // =========================================================
    // LEFT TOOLBAR
    // =========================================================

    private void createLeftToolbar() {

        leftToolbar =
                new JPanel();


        leftToolbar.setLayout(
                new BoxLayout(
                        leftToolbar,
                        BoxLayout.Y_AXIS
                )
        );


        leftToolbar.setOpaque(
                false
        );


        leftToolbar.setPreferredSize(
                new Dimension(
                        TOOLBAR_WIDTH,
                        0
                )
        );


        leftToolbar.setBorder(
                BorderFactory.createEmptyBorder(
                        28,
                        7,
                        20,
                        7
                )
        );


        // =====================================================
        // BUTTONS
        // =====================================================

        resetButton =
                createIconButton(
                        "↩"
                );


        redoButton =
                createIconButton(
                        "⟳"
                );


        undoButton =
                createIconButton(
                        "↶"
                );


        hintButton =
                createIconButton(
                        "💡"
                );


        settingsButton =
                createIconButton(
                        "⚙"
                );


        menuButton =
                createIconButton(
                        "☰"
                );


        // =====================================================
        // UNDO
        // =====================================================

        undoButton.addActionListener(
                e -> {

                    if (onlineGame
                            && multiplayerClient != null
                            && multiplayerClient.isConnected()) {

                        System.out.println(
                                "ONLINE SEND UNDO"
                        );

                        /*
                         * Do NOT undo locally.
                         *
                         * The server is authoritative and will
                         * broadcast the accepted UNDO to both clients.
                         */
                        multiplayerClient.sendUndo();

                    } else {

                        /*
                         * Offline game:
                         * continue using the normal local undo.
                         */
                        chessBoard.undoLastMove();

                        updateStatusBar();
                        refreshCapturedTrays();
                    }
                }
        );


        // =====================================================
        // REDO
        // =====================================================

        redoButton.addActionListener(
                e -> {

                    if (onlineGame
                            && multiplayerClient != null
                            && multiplayerClient.isConnected()) {

                        System.out.println(
                                "ONLINE SEND REDO"
                        );

                        /*
                         * Do NOT redo locally.
                         *
                         * Wait for the server to authorize the
                         * operation and broadcast it to both clients.
                         */
                        multiplayerClient.sendRedo();

                    } else {

                        /*
                         * Offline game:
                         * continue using the normal local redo.
                         */
                        chessBoard.redoLastMove();

                        updateStatusBar();
                        refreshCapturedTrays();
                    }
                }
        );


        // =====================================================
        // RESET
        // =====================================================

        resetButton.addActionListener(
                e -> {

                    /*
                     * Online games are controlled by the server.
                     *
                     * Never perform a local reset during an active
                     * online game.
                     */
                    if (onlineGame) {

                        JOptionPane.showMessageDialog(

                                this,

                                "The board cannot be reset during an online game.\n\n"
                                        + "Use Rematch after the game ends.",

                                "Online Multiplayer",

                                JOptionPane.INFORMATION_MESSAGE
                        );

                        return;
                    }


                    chessBoard.stopAnimation();

                    chessBoard.resetBoard();

                    updateStatusBar();

                    refreshCapturedTrays();
                }
        );


        // =====================================================
        // HINT
        // =====================================================

        hintButton.addActionListener(
                e ->
                        chessBoard.showHint()
        );


        // =====================================================
        // SETTINGS
        // =====================================================

        settingsButton.addActionListener(
                e -> {

                    Window window =
                            SwingUtilities
                                    .getWindowAncestor(
                                            this
                                    );


                    if (
                            window
                                    instanceof JFrame frame
                    ) {

                        new SettingsDialog(
                                frame,
                                chessBoard
                        ).setVisible(
                                true
                        );
                    }
                }
        );


        // =====================================================
        // MENU
        // =====================================================

        menuButton.addActionListener(
                e -> {

                    chessBoard.stopAnimation();

                    if (onlineGame) {

                        showOnlineGameMenu();

                    } else {

                        mainFrame.showMenu();
                    }
                }
        );


        // =====================================================
        // ADD BUTTONS
        // =====================================================

        addToolbarButton(
                resetButton
        );


        addToolbarSpacer();


        addToolbarButton(
                redoButton
        );


        addToolbarSpacer();


        addToolbarButton(
                undoButton
        );


        // =====================================================
        // HINT FOR TRAINING
        // =====================================================

        GameMode mode =
                chessBoard
                        .getGameState()
                        .getGameMode();


        if (
                mode
                        == GameMode.TRAINING
        ) {

            addToolbarSpacer();

            addToolbarButton(
                    hintButton
            );
        }


        addToolbarSpacer();


        addToolbarButton(
                settingsButton
        );


        // =====================================================
        // PUSH MENU TO BOTTOM
        // =====================================================

        leftToolbar.add(
                Box.createVerticalGlue()
        );


        addToolbarButton(
                menuButton
        );
    }


    private void addToolbarButton(
            JButton button) {

        leftToolbar.add(
                button
        );
    }


    private void addToolbarSpacer() {

        leftToolbar.add(
                Box.createVerticalStrut(
                        12
                )
        );
    }


    // =========================================================
    // BOTTOM STATUS BAR
    // =========================================================

    private void createBottomStatusBar() {

        bottomBar =
                new JPanel(
                        new BorderLayout()
                );


        bottomBar.setOpaque(
                false
        );


        bottomBar.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        15,
                        15,
                        15
                )
        );


        // =====================================================
        // LEFT STATUS
        // =====================================================

        bottomLeftStatus =
                createStatusPanel();


        difficultyLabel =
                new JLabel(
                        getDifficultyText()
                );


        styleStatusLabel(
                difficultyLabel
        );


        bottomLeftStatus.add(
                difficultyLabel
        );


        // =====================================================
        // RIGHT STATUS
        // =====================================================

        bottomRightStatus =
                createStatusPanel();


        moveStatusLabel =
                new JLabel(
                        getMoveStatusText()
                );


        styleStatusLabel(
                moveStatusLabel
        );


        bottomRightStatus.add(
                moveStatusLabel
        );


        // =====================================================
        // ADD
        // =====================================================

        bottomBar.add(
                bottomLeftStatus,
                BorderLayout.WEST
        );


        bottomBar.add(
                bottomRightStatus,
                BorderLayout.EAST
        );
    }


    // =========================================================
    // STATUS UPDATE
    // =========================================================

    private void updateStatusBar() {

        if (
                difficultyLabel
                        != null
        ) {

            difficultyLabel.setText(
                    getDifficultyText()
            );
        }


        if (
                moveStatusLabel
                        != null
        ) {

            moveStatusLabel.setText(
                    getMoveStatusText()
            );
        }


        updatePlayerTurnDisplay();


        if (
                bottomBar
                        != null
        ) {

            bottomBar.revalidate();

            bottomBar.repaint();
        }
    }


    // =========================================================
    // REFRESH CAPTURED PIECES
    // =========================================================

    private void refreshCapturedTrays() {

        if (
                blackCapturedTray
                        != null
        ) {

            blackCapturedTray.revalidate();

            blackCapturedTray.repaint();
        }


        if (
                whiteCapturedTray
                        != null
        ) {

            whiteCapturedTray.revalidate();

            whiteCapturedTray.repaint();
        }
    }


    // =========================================================
    // BOARD MOVE CALLBACK
    // =========================================================

    private void handleBoardMove() {

        updateStatusBar();


        if (
                blackCapturedTray
                        != null
        ) {

            blackCapturedTray.repaint();
        }


        if (
                whiteCapturedTray
                        != null
        ) {

            whiteCapturedTray.repaint();
        }
    }


    // =========================================================
    // DIFFICULTY / MODE
    // =========================================================

    private String getDifficultyText() {

        GameMode mode =
                chessBoard
                        .getGameState()
                        .getGameMode();


        return switch (mode) {

            case PUZZLE ->
                    "Beginner";

            case TRAINING ->
                    "Training";

            case PLAYER_VS_COMPUTER ->
                    "Computer";

            case PLAYER_VS_PLAYER ->
                    "2 Players";

            case ONLINE ->
                    "Online";

            default ->
                    "Chess";
        };
    }


    // =========================================================
    // MOVE STATUS
    // =========================================================

    private String getMoveStatusText() {

        GameState state =
                chessBoard
                        .getGameState();


        int moveCount =
                state
                        .getMoveHistory()
                        .getMoves()
                        .size();


        /*
         * Chess move numbering:
         *
         * 0 -> 1. White's Move
         * 1 -> 1. Black's Move
         * 2 -> 2. White's Move
         */

        int moveNumber =
                (moveCount / 2) + 1;


        String playerName;


        if (
                state.isWhiteTurn()
        ) {

            playerName =
                    state
                            .getWhitePlayer()
                            .getName();

        } else {

            playerName =
                    state
                            .getBlackPlayer()
                            .getName();
        }


        if (
                playerName == null
                        || playerName.isBlank()
        ) {

            playerName =
                    state.isWhiteTurn()
                            ? "White"
                            : "Black";
        }


        return moveNumber
                + ". "
                + playerName.trim()
                + "'s Move";
    }


    // =========================================================
    // UPDATE PLAYER TURN DISPLAY
    // =========================================================

    private void updatePlayerTurnDisplay() {

        if (
                blackPlayerCard == null
                        || whitePlayerCard == null
        ) {

            return;
        }


        boolean whiteTurn =
                chessBoard
                        .getGameState()
                        .isWhiteTurn();


        Color active =
                new Color(
                        110,
                        65,
                        25,
                        250
                );


        Color inactive =
                new Color(
                        38,
                        22,
                        13,
                        235
                );


        blackPlayerCard.setBackground(
                whiteTurn
                        ? inactive
                        : active
        );


        whitePlayerCard.setBackground(
                whiteTurn
                        ? active
                        : inactive
        );


        blackPlayerCard.repaint();

        whitePlayerCard.repaint();
    }


    // =========================================================
    // ICON BUTTON
    // =========================================================

    private JButton createIconButton(
            String icon) {

        JButton button =
                new JButton(
                        icon
                );


        Dimension size =
                new Dimension(
                        62,
                        62
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


        button.setBorderPainted(
                false
        );


        button.setOpaque(
                true
        );


        button.setBackground(
                new Color(
                        20,
                        20,
                        20,
                        225
                )
        );


        button.setForeground(
                new Color(
                        245,
                        190,
                        60
                )
        );


        button.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        27
                )
        );


        // =====================================================
        // HOVER EFFECT
        // =====================================================

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e) {

                        button.setBackground(
                                new Color(
                                        50,
                                        46,
                                        40,
                                        240
                                )
                        );
                    }


                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e) {

                        button.setBackground(
                                new Color(
                                        20,
                                        20,
                                        20,
                                        225
                                )
                        );
                    }
                }
        );


        return button;
    }


    // =========================================================
    // STATUS PANEL
    // =========================================================

    private JPanel createStatusPanel() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                12,
                                8
                        )
                );


        panel.setOpaque(
                true
        );


        panel.setBackground(
                new Color(
                        36,
                        21,
                        12,
                        235
                )
        );


        return panel;
    }


    // =========================================================
    // STATUS LABEL
    // =========================================================

    private void styleStatusLabel(
            JLabel label) {

        label.setForeground(
                Color.WHITE
        );


        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );
    }


    // =========================================================
    // GAME OVER
    // =========================================================

    private void showGameOverDialog() {

        if (
                chessBoard
                        .getGameState()
                        .getGameMode()
                        == GameMode.PUZZLE
        ) {

            if (
                    nextPuzzleButton
                            != null
            ) {

                nextPuzzleButton.setEnabled(
                        true
                );
            }
        }


        updateStatusBar();


        GameOverDialog dialog =
                new GameOverDialog(

                        mainFrame,

                        chessBoard
                                .getGameOverTitle(),

                        chessBoard
                                .getGameOverMessage(),

                        () -> {

                            chessBoard.resetBoard();

                            updateStatusBar();

                            refreshCapturedTrays();
                        },

                        () -> {

                            chessBoard.stopAnimation();

                            mainFrame.showMenu();
                        }
                );


        dialog.setVisible(
                true
        );


        SaveManager.deleteSave();
    }


    // =========================================================
    // MOVE HISTORY
    // =========================================================
    //
    // We intentionally no longer update SidePanel here.
    //
    // The move history will become a slide-out/reference-style
    // panel later.
    // =========================================================

    public void updateMoveHistory() {

        updateStatusBar();

        repaint();
    }


    // =========================================================
    // SEND ONLINE MOVE
    // =========================================================

    private void sendOnlineMove(
            Move move) {

        if (
                !onlineGame
                        || multiplayerClient == null
                        || move == null
        ) {

            return;
        }


        String moveData =
                serializeMove(
                        move
                );


        System.out.println(
                "ONLINE SEND MOVE: "
                        + moveData
        );


        multiplayerClient.sendMove(
                moveData
        );
    }


    // =========================================================
    // SERIALIZE MOVE
    // =========================================================

    private String serializeMove(
            Move move) {

        String promotion =
                "";


        if (
                move.isPromotion()
                        && move.getPromotedPiece() != null
        ) {

            promotion =
                    move.getPromotedPiece()
                            .getClass()
                            .getSimpleName();
        }


        return move.getFromRow()
                + ","
                + move.getFromCol()
                + ","
                + move.getToRow()
                + ","
                + move.getToCol()
                + ","
                + promotion;
    }


    // =========================================================
    // HANDLE REMOTE MOVE
    // =========================================================

    private void handleRemoteMove(
            String data) {

        try {

            /*
             * The server may append sender metadata after the canonical
             * move using the "|" separator, for example:
             *
             *     6,0,7,0,Queen|WHITE
             *
             * The chess move payload ends before that separator.
             * Promotion must therefore be parsed from the move payload
             * only; otherwise "Queen|WHITE" is treated as a piece type.
             */
            String movePayload = data;

            /*
             * The server appends the mover colour:
             *
             *     6,0,7,0,Queen|WHITE
             *
             * Keep the metadata so the receiving GameState can restore
             * the correct side-to-move before applying the remote move.
             */
            boolean moverIsWhite = false;
            boolean moverColourKnown = false;

            int metadataSeparator =
                    movePayload.indexOf('|');

            if (metadataSeparator >= 0) {

                String metadata =
                        movePayload
                                .substring(metadataSeparator + 1)
                                .trim();

                if ("WHITE".equalsIgnoreCase(metadata)) {
                    moverIsWhite = true;
                    moverColourKnown = true;

                } else if ("BLACK".equalsIgnoreCase(metadata)) {
                    moverIsWhite = false;
                    moverColourKnown = true;
                }

                movePayload =
                        movePayload.substring(
                                0,
                                metadataSeparator
                        );
            }

            movePayload =
                    movePayload.trim();

            String[] parts =
                    movePayload.split(
                            ",",
                            -1
                    );


            if (
                    parts.length < 5
            ) {

                System.err.println(
                        "Invalid remote move: "
                                + data
                );

                return;
            }


            int fromRow =
                    Integer.parseInt(
                            parts[0]
                    );


            int fromCol =
                    Integer.parseInt(
                            parts[1]
                    );


            int toRow =
                    Integer.parseInt(
                            parts[2]
                    );


            int toCol =
                    Integer.parseInt(
                            parts[3]
                    );


            String promotion =
                    parts[4].trim();


            // =================================================
            // FIND SOURCE PIECE
            // =================================================

            Piece piece =
                    chessBoard
                            .getGameState()
                            .getPiece(
                                    fromRow,
                                    fromCol
                            );


            if (
                    piece == null
            ) {

                System.err.println(
                        "Remote move source piece not found."
                );

                return;
            }


            // =================================================
            // FIND CAPTURED PIECE
            // =================================================

            Piece captured =
                    chessBoard
                            .getGameState()
                            .getPiece(
                                    toRow,
                                    toCol
                            );


            // =================================================
            // CREATE MOVE
            // =================================================

            Move move =
                    new Move(
                            piece,
                            fromRow,
                            fromCol,
                            toRow,
                            toCol,
                            captured
                    );


            // =================================================
            // PROMOTION
            // =================================================

            if (
                    !promotion.isBlank()
            ) {

                Piece promoted =
                        createPromotionPiece(
                                promotion,
                                piece.isWhite(),
                                toRow,
                                toCol
                        );


                if (
                        promoted != null
                ) {

                    move.setPromotion(
                            promoted
                    );
                }
            }


            System.out.println(
                    "ONLINE RECEIVE MOVE: "
                            + move
            );


            // =================================================
            // APPLY REMOTE MOVE
            // =================================================

            /*
             * Prefer the authoritative mover colour supplied by the
             * server. Fall back to the piece colour only for compatibility
             * with older servers that did not append metadata.
             */
            if (!moverColourKnown) {
                moverIsWhite = piece.isWhite();
            }

            chessBoard.applyRemoteMove(
                    move,
                    moverIsWhite
            );


            updateMoveHistory();

            refreshCapturedTrays();

        } catch (
                Exception ex
        ) {

            ex.printStackTrace();


            JOptionPane.showMessageDialog(

                    this,

                    "Unable to apply opponent move:\n"
                            + ex.getMessage(),

                    "Online Game Error",

                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =========================================================
    // CREATE PROMOTION PIECE
    // =========================================================

    private Piece createPromotionPiece(
            String type,
            boolean white,
            int row,
            int col) {

        return switch (type) {

            case "Queen" ->
                    new Queen(
                            white,
                            row,
                            col
                    );

            case "Rook" ->
                    new Rook(
                            white,
                            row,
                            col
                    );

            case "Bishop" ->
                    new Bishop(
                            white,
                            row,
                            col
                    );

            case "Knight" ->
                    new Knight(
                            white,
                            row,
                            col
                    );

            default ->
                    null;
        };
    }


    // =========================================================
    // NETWORK MESSAGE HANDLER
    // =========================================================

    private void handleNetworkMessage(
            NetworkMessage message) {

        if (
                message == null
        ) {

            return;
        }


        SwingUtilities.invokeLater(
                () -> {

                    switch (
                            message.getType()
                    ) {

                        case MOVE:

                            handleRemoteMove(
                                    message.getData()
                            );

                            break;


                        case MOVE_REJECTED:

                            handleMoveRejected(
                                    message.getData()
                            );

                            break;

                        case UNDO:

                            System.out.println(
                                    "ONLINE RECEIVE UNDO"
                            );

                            chessBoard.undoLastMove();

                            updateStatusBar();
                            refreshCapturedTrays();

                            break;


                        case REDO:

                            System.out.println(
                                    "ONLINE RECEIVE REDO"
                            );

                            chessBoard.redoLastMove();

                            updateStatusBar();
                            refreshCapturedTrays();

                            break;


                        case RESIGN:

                            handleRemoteResignation(
                                    message.getData()
                            );

                            break;


                        case REMATCH:

                            handleRemoteRematch(
                                    message.getData()
                            );

                            break;


                        case DISCONNECT:

                            JOptionPane.showMessageDialog(

                                    this,

                                    "Your opponent has disconnected.",

                                    "Online Multiplayer",

                                    JOptionPane.INFORMATION_MESSAGE
                            );


                            mainFrame.showMenu();

                            break;


                        case ERROR:

                            JOptionPane.showMessageDialog(

                                    this,

                                    message.getData(),

                                    "Multiplayer Error",

                                    JOptionPane.ERROR_MESSAGE
                            );

                            break;


                        default:

                            break;
                    }
                }
        );
    }


    // =========================================================
    // SERVER MOVE REJECTION

    private void handleMoveRejected(
            String reason) {

        JOptionPane.showMessageDialog(
                this,
                "The chess server rejected your move.\n\n"
                        + (reason == null
                        ? "Unknown reason."
                        : reason),
                "Online Multiplayer",
                JOptionPane.WARNING_MESSAGE
        );

        System.err.println(
                "SERVER REJECTED LOCAL MOVE: "
                        + reason
        );
    }


    // =========================================================
// REMOTE RESIGNATION
// =========================================================

    private void handleRemoteResignation(
            String color) {

        boolean whiteResigned =
                color != null
                        && color.equalsIgnoreCase("WHITE");

        boolean blackResigned =
                color != null
                        && color.equalsIgnoreCase("BLACK");

        /*
         * Determine whether THIS player resigned.
         */
        boolean localPlayerResigned =
                (playerIsWhite && whiteResigned)
                        || (!playerIsWhite && blackResigned);


        String title =
                "Online Game";


        String message;


        if (localPlayerResigned) {

            String winner =
                    playerIsWhite
                            ? "Black"
                            : "White";

            message =
                    "You resigned.\n\n"
                            + winner
                            + " wins.";

        } else {

            String resignedPlayer =
                    whiteResigned
                            ? "White"
                            : "Black";

            message =
                    resignedPlayer
                            + " resigned.\n\n"
                            + "You win!";
        }


        /*
         * Mark the online game as finished.
         *
         * The players remain in the current screen so they
         * can use the Rematch option.
         */
        onlineGameOver = true;
        rematchRequested = false;


        JOptionPane.showMessageDialog(

                this,

                message,

                title,

                JOptionPane.INFORMATION_MESSAGE
        );


        updateStatusBar();
    }

    // =========================================================
// REMOTE REMATCH
// =========================================================

    private void handleRemoteRematch(
            String data) {

        /*
         * The server uses:
         *
         *     REMATCH|WHITE
         *     REMATCH|BLACK
         *
         * when the opponent REQUESTS a rematch.
         *
         * It uses:
         *
         *     REMATCH|START
         *
         * when BOTH players have agreed and the new
         * authoritative game is ready.
         */

        if (
                data != null
                        && data.equalsIgnoreCase("START")
        ) {

            /*
             * BOTH players accepted.
             *
             * The server has already created a fresh
             * authoritative GameState.
             *
             * Reset both client boards at the same time.
             */
            System.out.println(
                    "ONLINE REMATCH STARTED"
            );


            chessBoard.stopAnimation();

            chessBoard.resetBoard();


            onlineGameOver = false;
            rematchRequested = false;


            updateMoveHistory();

            refreshCapturedTrays();

            updateStatusBar();

            return;
        }


        /*
         * Otherwise this is an opponent rematch request.
         */
        String opponent =
                data == null
                        || data.isBlank()
                        ? "Your opponent"
                        : data.trim();


        /*
         * If we already requested a rematch ourselves,
         * the server will normally send START once the
         * opponent agrees. Therefore do not show a second
         * confirmation dialog unnecessarily.
         */
        if (rematchRequested) {

            return;
        }


        int result =
                JOptionPane.showConfirmDialog(

                        this,

                        opponent
                                + " wants a rematch.\n\n"
                                + "Start a new game?",

                        "Rematch",

                        JOptionPane.YES_NO_OPTION,

                        JOptionPane.QUESTION_MESSAGE
                );


        if (
                result
                        == JOptionPane.YES_OPTION
        ) {

            if (
                    multiplayerClient != null
                            && multiplayerClient.isConnected()
            ) {

                System.out.println(
                        "ONLINE SEND REMATCH"
                );


                rematchRequested = true;


                /*
                 * IMPORTANT:
                 *
                 * Do NOT reset the board here.
                 *
                 * The server must receive BOTH agreements
                 * first and then send REMATCH|START to both
                 * clients.
                 */
                multiplayerClient.sendRematch();


            } else {

                JOptionPane.showMessageDialog(

                        this,

                        "The online server connection is no longer available.",

                        "Online Multiplayer",

                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    // =========================================================
// ONLINE GAME MENU
// =========================================================

    private void showOnlineGameMenu() {

        String[] options;


        /*
         * Once the online game has ended, Resign is no longer
         * useful. Replace it with Rematch.
         */
        if (onlineGameOver) {

            options = new String[]{
                    "Rematch Game",
                    "Return to Main Menu",
                    "Cancel"
            };

        } else {

            options = new String[]{
                    "Resign Game",
                    "Return to Main Menu",
                    "Cancel"
            };
        }


        int choice =
                JOptionPane.showOptionDialog(

                        this,

                        onlineGameOver
                                ? "Online Game Over"
                                : "Online Game",

                        "Game Menu",

                        JOptionPane.DEFAULT_OPTION,

                        JOptionPane.PLAIN_MESSAGE,

                        null,

                        options,

                        options[options.length - 1]
                );


        // =====================================================
        // REMATCH
        // =====================================================

        if (
                onlineGameOver
                        && choice == 0
        ) {

            /*
             * Prevent duplicate rematch requests.
             */
            if (rematchRequested) {

                JOptionPane.showMessageDialog(

                        this,

                        "Your rematch request has already been sent.\n\n"
                                + "Waiting for your opponent.",

                        "Rematch",

                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }


            if (
                    multiplayerClient != null
                            && multiplayerClient.isConnected()
            ) {

                System.out.println(
                        "ONLINE SEND REMATCH"
                );


                rematchRequested = true;


                multiplayerClient.sendRematch();


                JOptionPane.showMessageDialog(

                        this,

                        "Rematch request sent.\n\n"
                                + "Waiting for your opponent to accept.",

                        "Rematch",

                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(

                        this,

                        "The online server connection is no longer available.",

                        "Online Multiplayer",

                        JOptionPane.ERROR_MESSAGE
                );
            }


            return;
        }


        // =====================================================
        // RESIGN
        // =====================================================

        if (
                !onlineGameOver
                        && choice == 0
        ) {

            int confirm =
                    JOptionPane.showConfirmDialog(

                            this,

                            "Are you sure you want to resign?\n\n"
                                    + "Your opponent will win the game.",

                            "Confirm Resignation",

                            JOptionPane.YES_NO_OPTION,

                            JOptionPane.WARNING_MESSAGE
                    );


            if (
                    confirm
                            == JOptionPane.YES_OPTION
            ) {

                if (
                        multiplayerClient != null
                                && multiplayerClient.isConnected()
                ) {

                    System.out.println(
                            "ONLINE SEND RESIGN"
                    );


                    multiplayerClient.sendResign();

                } else {

                    JOptionPane.showMessageDialog(

                            this,

                            "The online server connection is no longer available.",

                            "Online Multiplayer",

                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }


            return;
        }


        // =====================================================
        // RETURN TO MAIN MENU
        // =====================================================

        /*
         * For both menu states:
         *
         * index 1 = Return to Main Menu
         */
        if (choice == 1) {

            mainFrame.showMenu();
        }
    }
}
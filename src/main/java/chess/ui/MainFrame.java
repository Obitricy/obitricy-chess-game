package chess.ui;

import javax.swing.*;
import java.awt.*;

import chess.ai.ComputerDifficulty;
import chess.appearance.ThemeManager;
import chess.board.BoardView;
import chess.board.GameState;
import chess.save.SaveManager;
import chess.audio.SoundManager;
import chess.game.GameMode;
import chess.puzzle.PuzzleImporter;
import chess.puzzle.PuzzleManager;
import chess.multiplayer.MultiplayerClient;

public class MainFrame extends JFrame {

    public static final String LOGIN = "LOGIN";
    public static final String MENU = "MENU";
    public static final String GAME = "GAME";
    public static final String REGISTER = "REGISTER";
    public static final String ONLINE = "ONLINE";

    private final CardLayout layout =
            new CardLayout();

    private final JPanel container =
            new JPanel(layout);

    private final LoginPanel loginPanel;
    private final RegisterPanel registerPanel;
    private final MenuPanel menuPanel;
    private final OnlineMultiplayerPanel onlineMultiplayerPanel;

    private ChessUI chessUI;

    private String currentUsername = "Player";

    public MainFrame() {

        setTitle(
                "OBITRICY CHESS GAME"
        );

        setLayout(
                new BorderLayout()
        );

        loginPanel =
                new LoginPanel(this);

        registerPanel =
                new RegisterPanel(this);

        menuPanel =
                new MenuPanel(this);

        onlineMultiplayerPanel =
                new OnlineMultiplayerPanel(this);

        container.add(
                loginPanel,
                LOGIN
        );

        container.add(
                registerPanel,
                REGISTER
        );

        container.add(
                menuPanel,
                MENU
        );

        container.add(
                onlineMultiplayerPanel,
                ONLINE
        );

        add(
                container,
                BorderLayout.CENTER
        );

        setDefaultCloseOperation(
                EXIT_ON_CLOSE
        );

        setResizable(true);

        pack();

        setLocationRelativeTo(null);

        layout.show(
                container,
                LOGIN
        );

        // Start chess background music.
        SoundManager.startBackgroundMusic();
    }

    // =========================================================
    // START GAME
    // =========================================================

    public void startGame(
            GameMode mode,
            String white,
            String black,
            ComputerDifficulty difficulty
    ) {

        /*
         * Board View is now an application setting.
         *
         * The user selects 2D or 3D in Settings.
         */
        BoardView boardView =
                ThemeManager.getBoardView();

        /*
         * Safety fallback.
         */
        if (boardView == null) {

            boardView =
                    BoardView.THREE_D;
        }

        switch (mode) {

            case PLAYER_VS_PLAYER:
            case PLAYER_VS_COMPUTER:
            case TRAINING:

                GameState state =
                        new GameState(
                                white,
                                black,
                                mode,
                                difficulty
                        );

                chessUI =
                        new ChessUI(
                                this,
                                state,
                                boardView
                        );

                showChessUI();

                break;

            case PUZZLE:

                startPuzzleGame(
                        white,
                        black,
                        difficulty,
                        boardView
                );

                break;

            case ONLINE:

                showOnlineMultiplayer();

                break;
        }
    }

    // =========================================================
    // PUZZLE GAME
    // =========================================================

    private void startPuzzleGame(
            String white,
            String black,
            ComputerDifficulty difficulty,
            BoardView boardView
    ) {

        try {

            PuzzleManager puzzleManager =
                    PuzzleImporter.load(
                            "/puzzles/lichess_puzzles.csv"
                    );

            if (puzzleManager.getCurrentPuzzle() == null) {

                ObitricyDialog.showWarning(
                        this,
                        "Puzzle Mode",
                        "No puzzles were found."
                );

                return;
            }

            GameState puzzleState =
                    new GameState(
                            white,
                            black,
                            GameMode.PUZZLE,
                            difficulty
                    );

            puzzleManager.loadCurrentPuzzle(
                    puzzleState
            );

            chessUI =
                    new ChessUI(
                            this,
                            puzzleState,
                            boardView
                    );

            showChessUI();

        } catch (Exception ex) {

            ex.printStackTrace();

            ObitricyDialog.showError(
                    this,
                    "Puzzle Mode Error",
                    "Unable to load Puzzle Mode:\n"
                            + ex.getMessage()
            );
        }
    }

    // =========================================================
    // SHOW CHESS UI
    // =========================================================

    private void showChessUI() {

        container.add(
                chessUI,
                GAME
        );

        layout.show(
                container,
                GAME
        );

        revalidate();
        repaint();

        sizeForChessGame();
    }

    // =========================================================
    // CONTINUE SAVED GAME
    // =========================================================

    public void continueGame() {

        GameState state =
                SaveManager.load();

        if (state == null) {

            ObitricyDialog.showInfo(
                    this,
                    "Saved Game",
                    "No saved game found."
            );

            return;
        }

        state.reloadPieceImages();

        BoardView boardView =
                ThemeManager.getBoardView();

        if (boardView == null) {

            boardView =
                    BoardView.THREE_D;
        }

        chessUI =
                new ChessUI(
                        this,
                        state,
                        boardView
                );

        showChessUI();
    }

    // =========================================================
    // SIZE GAME
    // =========================================================

    private void sizeForChessGame() {

        GraphicsConfiguration gc =
                getGraphicsConfiguration();

        if (gc == null) {

            gc =
                    GraphicsEnvironment
                            .getLocalGraphicsEnvironment()
                            .getDefaultScreenDevice()
                            .getDefaultConfiguration();
        }

        Rectangle screenBounds =
                gc.getBounds();

        Insets insets =
                Toolkit.getDefaultToolkit()
                        .getScreenInsets(gc);

        int availableWidth =
                screenBounds.width
                        - insets.left
                        - insets.right;

        int availableHeight =
                screenBounds.height
                        - insets.top
                        - insets.bottom;

        int width =
                (int) (
                        availableWidth * 0.98
                );

        int height =
                (int) (
                        availableHeight * 0.98
                );

        width =
                Math.min(
                        width,
                        availableWidth
                );

        height =
                Math.min(
                        height,
                        availableHeight
                );

        width =
                Math.min(
                        width,
                        1600
                );

        height =
                Math.min(
                        height,
                        1000
                );

        if (availableWidth >= 950) {

            width =
                    Math.max(
                            width,
                            900
                    );
        }

        if (availableHeight >= 700) {

            height =
                    Math.max(
                            height,
                            650
                    );
        }

        setSize(
                width,
                height
        );

        setLocationRelativeTo(null);
    }

    // =========================================================
    // NAVIGATION
    // =========================================================

    public void showLogin() {

        layout.show(
                container,
                LOGIN
        );

        pack();

        setLocationRelativeTo(null);
    }

    public void showRegister() {

        layout.show(
                container,
                REGISTER
        );

        pack();

        setLocationRelativeTo(null);
    }

    public void setCurrentUsername(String username) {
        if (username != null && !username.isBlank()) {
            currentUsername = username.trim();
        }
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void showMenu() {

        layout.show(
                container,
                MENU
        );

        pack();

        setLocationRelativeTo(null);
    }

    public void showOnlineMultiplayer() {

        layout.show(
                container,
                ONLINE
        );

        setSize(
                700,
                800
        );

        setLocationRelativeTo(null);

        revalidate();
        repaint();

        // The online panel is created before login. Connect only after the
        // user opens the lobby, when currentUsername is already known.
        onlineMultiplayerPanel.openLobby();
    }

    // =========================================================
    // ONLINE GAME
    // =========================================================

    public void startOnlineGame(
            MultiplayerClient client,
            String gameId,
            boolean playerIsWhite
    ) {

        BoardView boardView =
                ThemeManager.getBoardView();

        if (boardView == null) {
            boardView = BoardView.THREE_D;
        }

        GameState state =
                new GameState(
                        playerIsWhite
                                ? "You"
                                : "Opponent",

                        playerIsWhite
                                ? "Opponent"
                                : "You",

                        GameMode.ONLINE,
                        null
                );

        chessUI =
                new ChessUI(
                        this,
                        state,
                        client,
                        playerIsWhite,
                        boardView
                );

        showChessUI();
    }
}
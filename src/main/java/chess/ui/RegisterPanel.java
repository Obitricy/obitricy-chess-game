package chess.ui;

import chess.auth.LoginManager;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Premium registration panel for Obitricy Chess Game.
 *
 * Commercial registration interface featuring:
 * - Premium dark/gold design
 * - Username validation
 * - Password validation
 * - Confirm password
 * - Password visibility controls
 * - Registration result handling
 * - Keyboard shortcuts
 */
public class RegisterPanel extends JPanel {

    // ============================================================
    // OBITRICY COLORS
    // ============================================================

    private static final Color BACKGROUND =
            new Color(7, 11, 17);

    private static final Color CARD =
            new Color(17, 24, 34);

    private static final Color CARD_BORDER =
            new Color(43, 52, 66);

    private static final Color GOLD =
            new Color(218, 175, 70);

    private static final Color GOLD_HOVER =
            new Color(235, 195, 90);

    private static final Color TEXT =
            new Color(240, 243, 248);

    private static final Color MUTED_TEXT =
            new Color(155, 165, 180);

    private static final Color FIELD_BACKGROUND =
            new Color(11, 17, 25);

    private static final Color FIELD_BORDER =
            new Color(55, 65, 80);

    private static final Color FIELD_FOCUS =
            new Color(218, 175, 70);

    // ============================================================
    // FIELDS
    // ============================================================

    private final MainFrame frame;

    private JTextField usernameField;

    private JPasswordField passwordField;

    private JPasswordField confirmPasswordField;

    private JButton createButton;

    private JButton backButton;

    // ============================================================
    // PASSWORD EYE BUTTONS
    // ============================================================

    private JButton passwordToggle;

    private JButton confirmPasswordToggle;

    private boolean passwordVisible = false;

    private boolean confirmPasswordVisible = false;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public RegisterPanel(MainFrame frame) {

        this.frame = frame;

        setLayout(new BorderLayout());

        setBackground(BACKGROUND);

        setBorder(
                new EmptyBorder(
                        25,
                        25,
                        25,
                        25
                )
        );

        buildUI();

        setupKeyboardActions();
    }

    // ============================================================
    // BUILD UI
    // ============================================================

    private void buildUI() {

        JPanel centerWrapper =
                new JPanel(new GridBagLayout());

        centerWrapper.setOpaque(false);

        JPanel card = createCard();

        centerWrapper.add(card);

        add(
                centerWrapper,
                BorderLayout.CENTER
        );
    }

    // ============================================================
    // REGISTRATION CARD
    // ============================================================

    private JPanel createCard() {

        JPanel card =
                new JPanel(new GridBagLayout());

        card.setBackground(CARD);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                CARD_BORDER,
                                1
                        ),
                        new EmptyBorder(
                                32,
                                42,
                                32,
                                42
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;

        gbc.weightx = 1.0;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.insets =
                new Insets(0, 0, 0, 0);

        // ========================================================
        // OBITRICY KNIGHT
        // ========================================================

        JLabel logo =
                new JLabel("♞");

        logo.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        logo.setFont(
                new Font(
                        "Serif",
                        Font.BOLD,
                        42
                )
        );

        logo.setForeground(GOLD);

        gbc.gridy = 0;

        gbc.insets =
                new Insets(0, 0, 5, 0);

        card.add(logo, gbc);

        // ========================================================
        // TITLE
        // ========================================================

        JLabel title =
                new JLabel(
                        "CREATE YOUR ACCOUNT"
                );

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        22
                )
        );

        title.setForeground(TEXT);

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 8, 0);

        card.add(title, gbc);

        // ========================================================
        // SUBTITLE
        // ========================================================

        JLabel subtitle =
                new JLabel(
                        "<html><div style='text-align:center;'>"
                                + "Join Obitricy Chess and begin your journey."
                                + "</div></html>"
                );

        subtitle.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        subtitle.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        subtitle.setForeground(MUTED_TEXT);

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 28, 0);

        card.add(subtitle, gbc);

        // ========================================================
        // USERNAME
        // ========================================================

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 7, 0);

        card.add(
                createLabel("USERNAME"),
                gbc
        );

        usernameField =
                createTextField();

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 18, 0);

        card.add(
                usernameField,
                gbc
        );

        // ========================================================
        // PASSWORD
        // ========================================================

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 7, 0);

        card.add(
                createLabel("PASSWORD"),
                gbc
        );

        JPanel passwordPanel =
                createPasswordPanel(false);

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 18, 0);

        card.add(
                passwordPanel,
                gbc
        );

        // ========================================================
        // CONFIRM PASSWORD
        // ========================================================

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 7, 0);

        card.add(
                createLabel("CONFIRM PASSWORD"),
                gbc
        );

        JPanel confirmPasswordPanel =
                createPasswordPanel(true);

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 22, 0);

        card.add(
                confirmPasswordPanel,
                gbc
        );

        // ========================================================
        // CREATE ACCOUNT
        // ========================================================

        createButton =
                createPrimaryButton(
                        "CREATE ACCOUNT"
                );

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 12, 0);

        card.add(
                createButton,
                gbc
        );

        createButton.addActionListener(
                e -> createAccount()
        );

        // ========================================================
        // BACK TO LOGIN
        // ========================================================

        backButton =
                createSecondaryButton(
                        "BACK TO SIGN IN"
                );

        gbc.gridy++;

        gbc.insets =
                new Insets(0, 0, 8, 0);

        card.add(
                backButton,
                gbc
        );

        backButton.addActionListener(
                e -> frame.showLogin()
        );

        // ========================================================
        // FOOTER
        // ========================================================

        JLabel footer =
                new JLabel(
                        "<html><div style='text-align:center;'>"
                                + "Your chess journey starts here."
                                + "</div></html>"
                );

        footer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        footer.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        11
                )
        );

        footer.setForeground(MUTED_TEXT);

        gbc.gridy++;

        gbc.insets =
                new Insets(8, 0, 0, 0);

        card.add(
                footer,
                gbc
        );

        return card;
    }

    // ============================================================
    // LABEL
    // ============================================================

    private JLabel createLabel(String text) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        11
                )
        );

        label.setForeground(MUTED_TEXT);

        return label;
    }

    // ============================================================
    // TEXT FIELD
    // ============================================================

    private JTextField createTextField() {

        JTextField field =
                new JTextField();

        field.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        field.setForeground(TEXT);

        field.setBackground(
                FIELD_BACKGROUND
        );

        field.setCaretColor(GOLD);

        field.setBorder(
                createFieldBorder(FIELD_BORDER)
        );

        field.setPreferredSize(
                new Dimension(
                        330,
                        44
                )
        );

        addFocusEffect(field);

        return field;
    }

    // ============================================================
    // PASSWORD PANEL
    // ============================================================

    private JPanel createPasswordPanel(
            boolean confirm
    ) {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setOpaque(false);

        JPasswordField field =
                createPasswordField();

        JButton toggle =
                createPasswordToggle();

        if (confirm) {

            confirmPasswordField =
                    field;

            confirmPasswordToggle =
                    toggle;

            toggle.addActionListener(
                    e -> toggleConfirmPassword()
            );

        } else {

            passwordField =
                    field;

            passwordToggle =
                    toggle;

            toggle.addActionListener(
                    e -> togglePassword()
            );
        }

        panel.add(
                field,
                BorderLayout.CENTER
        );

        panel.add(
                toggle,
                BorderLayout.EAST
        );

        return panel;
    }

    // ============================================================
    // PASSWORD FIELD
    // ============================================================

    private JPasswordField createPasswordField() {

        JPasswordField field =
                new JPasswordField();

        field.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        field.setForeground(TEXT);

        field.setBackground(
                FIELD_BACKGROUND
        );

        field.setCaretColor(GOLD);

        field.setBorder(
                createFieldBorder(FIELD_BORDER)
        );

        field.setPreferredSize(
                new Dimension(
                        270,
                        44
                )
        );

        addFocusEffect(field);

        return field;
    }

    // ============================================================
    // PASSWORD VISIBILITY BUTTON
    // ============================================================

    private JButton createPasswordToggle() {

        JButton button =
                new JButton("SHOW");

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        10
                )
        );

        button.setForeground(GOLD);

        button.setBackground(
                FIELD_BACKGROUND
        );

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                FIELD_BORDER,
                                1
                        ),
                        new EmptyBorder(
                                0,
                                10,
                                0,
                                10
                        )
                )
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setPreferredSize(
                new Dimension(
                        60,
                        44
                )
        );

        return button;
    }

    // ============================================================
    // TOGGLE PASSWORD
    // ============================================================

    private void togglePassword() {

        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            passwordField.setEchoChar((char) 0);
            passwordToggle.setText("HIDE");
        } else {
            passwordField.setEchoChar('•');
            passwordToggle.setText("SHOW");
        }

        passwordField.requestFocusInWindow();
    }

    // ============================================================
    // TOGGLE CONFIRM PASSWORD
    // ============================================================

    private void toggleConfirmPassword() {

        confirmPasswordVisible =
                !confirmPasswordVisible;

        if (confirmPasswordVisible) {
            confirmPasswordField.setEchoChar((char) 0);
            confirmPasswordToggle.setText("HIDE");
        } else {
            confirmPasswordField.setEchoChar('•');
            confirmPasswordToggle.setText("SHOW");
        }

        confirmPasswordField.requestFocusInWindow();
    }

    // ============================================================
    // FIELD BORDER
    // ============================================================

    private Border createFieldBorder(
            Color color
    ) {

        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        color,
                        1
                ),
                new EmptyBorder(
                        10,
                        12,
                        10,
                        12
                )
        );
    }

    // ============================================================
    // FOCUS EFFECT
    // ============================================================

    private void addFocusEffect(
            JComponent field
    ) {

        field.addFocusListener(
                new java.awt.event.FocusAdapter() {

                    @Override
                    public void focusGained(
                            java.awt.event.FocusEvent e
                    ) {

                        field.setBorder(
                                createFieldBorder(
                                        FIELD_FOCUS
                                )
                        );
                    }

                    @Override
                    public void focusLost(
                            java.awt.event.FocusEvent e
                    ) {

                        field.setBorder(
                                createFieldBorder(
                                        FIELD_BORDER
                                )
                        );
                    }
                }
        );
    }

    // ============================================================
    // PRIMARY BUTTON
    // ============================================================

    private JButton createPrimaryButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(BACKGROUND);

        button.setBackground(GOLD);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setOpaque(true);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setPreferredSize(
                new Dimension(
                        330,
                        46
                )
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        button.setBackground(
                                GOLD_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        button.setBackground(
                                GOLD
                        );
                    }
                }
        );

        return button;
    }

    // ============================================================
    // SECONDARY BUTTON
    // ============================================================

    private JButton createSecondaryButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        button.setForeground(GOLD);

        button.setBackground(CARD);

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setOpaque(true);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setPreferredSize(
                new Dimension(
                        330,
                        40
                )
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        button.setForeground(
                                GOLD_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        button.setForeground(
                                GOLD
                        );
                    }
                }
        );

        return button;
    }

    // ============================================================
    // ACCOUNT CREATION
    // ============================================================

    private void createAccount() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        String confirmPassword =
                new String(
                        confirmPasswordField.getPassword()
                );

        // ========================================================
        // USERNAME EMPTY
        // ========================================================

        if (username.isEmpty()) {

            showValidationError(
                    "Please enter a username."
            );

            usernameField.requestFocusInWindow();

            return;
        }

        // ========================================================
        // USERNAME FORMAT
        // ========================================================

        if (!username.matches(
                "[A-Za-z0-9_]{3,20}"
        )) {

            showValidationError(
                    "Username must be 3–20 characters "
                            + "and may contain only letters, "
                            + "numbers and underscores."
            );

            usernameField.requestFocusInWindow();

            return;
        }

        // ========================================================
        // PASSWORD EMPTY
        // ========================================================

        if (password.isEmpty()) {

            showValidationError(
                    "Please enter a password."
            );

            passwordField.requestFocusInWindow();

            return;
        }

        // ========================================================
        // PASSWORD LENGTH
        // ========================================================

        int minimumPasswordLength =
                LoginManager.getMinimumPasswordLength();

        if (password.length()
                < minimumPasswordLength) {

            showValidationError(
                    "Your password must contain at least "
                            + minimumPasswordLength
                            + " characters."
            );

            passwordField.requestFocusInWindow();

            return;
        }

        // ========================================================
        // CONFIRM PASSWORD
        // ========================================================

        if (confirmPassword.isEmpty()) {

            showValidationError(
                    "Please confirm your password."
            );

            confirmPasswordField.requestFocusInWindow();

            return;
        }

        // ========================================================
        // PASSWORD MATCH
        // ========================================================

        if (!password.equals(confirmPassword)) {

            showValidationError(
                    "The passwords do not match."
            );

            confirmPasswordField.setText("");

            confirmPasswordField.requestFocusInWindow();

            return;
        }

        // ========================================================
        // REGISTER
        // ========================================================

        boolean registered =
                LoginManager.register(
                        username,
                        password
                );

        // ========================================================
        // REGISTRATION FAILED
        // ========================================================

        if (!registered) {

            showValidationError(
                    "Registration could not be completed. "
                            + "The username may already be in use."
            );

            usernameField.requestFocusInWindow();

            return;
        }

        // ========================================================
        // CLEAR SENSITIVE DATA
        // ========================================================

        passwordField.setText("");

        confirmPasswordField.setText("");

        // ========================================================
        // SUCCESS
        // ========================================================

        ObitricyDialog.showSuccess(
                this,
                "Account Created",
                "Your Obitricy Chess account has been "
                        + "created successfully."
        );

        // ========================================================
        // RETURN TO LOGIN
        // ========================================================

        frame.showLogin();
    }

    // ============================================================
    // VALIDATION DIALOG
    // ============================================================

    private void showValidationError(
            String message
    ) {

        ObitricyDialog.showError(
                this,
                "Registration",
                message
        );
    }

    // ============================================================
    // KEYBOARD SUPPORT
    // ============================================================

    private void setupKeyboardActions() {

        // --------------------------------------------------------
        // ENTER = CREATE ACCOUNT
        // --------------------------------------------------------

        getInputMap(
                JComponent
                        .WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        ).put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_ENTER,
                        0
                ),
                "createAccount"
        );

        getActionMap().put(
                "createAccount",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            ActionEvent e
                    ) {

                        createAccount();
                    }
                }
        );

        // --------------------------------------------------------
        // ESCAPE = BACK TO LOGIN
        // --------------------------------------------------------

        getInputMap(
                JComponent
                        .WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        ).put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_ESCAPE,
                        0
                ),
                "backToLogin"
        );

        getActionMap().put(
                "backToLogin",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            ActionEvent e
                    ) {

                        frame.showLogin();
                    }
                }
        );
    }
}
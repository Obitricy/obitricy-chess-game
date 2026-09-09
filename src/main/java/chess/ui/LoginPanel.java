package chess.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import chess.auth.LoginManager;

/**
 * Premium Obitricy login screen.
 *
 * Keeps the existing login/registration behaviour while improving:
 * - logo rendering and transparency
 * - card proportions and shadows
 * - custom fields, checkbox and buttons
 * - password visibility toggle
 * - loading indicator
 * - subtle chess background
 * - focus/hover states
 */
public class LoginPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeCheckBox;
    private JCheckBox showPasswordCheckBox;
    private JButton forgotPasswordButton;
    private JProgressBar loadingBar;

    private char defaultEchoChar;

    // =========================================================
    // COLORS
    // =========================================================

    private static final Color BACKGROUND_TOP =
            new Color(6, 10, 17);

    private static final Color BACKGROUND_BOTTOM =
            new Color(2, 4, 9);

    private static final Color CARD_BACKGROUND =
            new Color(12, 18, 28, 252);

    private static final Color CARD_BORDER =
            new Color(255, 255, 255, 28);

    private static final Color CARD_INNER_BORDER =
            new Color(255, 255, 255, 10);

    private static final Color GOLD =
            new Color(212, 175, 82);

    private static final Color GOLD_LIGHT =
            new Color(244, 211, 124);

    private static final Color GOLD_DARK =
            new Color(166, 126, 39);

    private static final Color TEXT_PRIMARY =
            new Color(247, 248, 250);

    private static final Color TEXT_SECONDARY =
            new Color(165, 174, 188);

    private static final Color TEXT_MUTED =
            new Color(104, 115, 130);

    private static final Color FIELD_BACKGROUND =
            new Color(6, 11, 18);

    private static final Color FIELD_BORDER =
            new Color(255, 255, 255, 34);

    private static final Color FIELD_FOCUS =
            new Color(212, 175, 82, 205);

    private static final Color SECONDARY_BUTTON =
            new Color(23, 30, 42);

    private static final Color SECONDARY_BUTTON_HOVER =
            new Color(31, 40, 54);

    private static final Color CHESS_LIGHT =
            new Color(255, 255, 255, 5);

    private static final Color CHESS_DARK =
            new Color(0, 0, 0, 12);

    private static final Color PIECE_COLOR =
            new Color(255, 255, 255, 7);

    // =========================================================
    // FONTS
    // =========================================================

    private static final Font BRAND_FONT =
            new Font("Segoe UI", Font.BOLD, 25);

    private static final Font BRAND_SUBTITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 9);

    private static final Font WELCOME_FONT =
            new Font("Segoe UI", Font.BOLD, 23);

    private static final Font DESCRIPTION_FONT =
            new Font("Segoe UI", Font.PLAIN, 13);

    private static final Font LABEL_FONT =
            new Font("Segoe UI", Font.BOLD, 11);

    private static final Font FIELD_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    private static final Font BUTTON_FONT =
            new Font("Segoe UI", Font.BOLD, 14);

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public LoginPanel(MainFrame frame) {

        setOpaque(false);
        setLayout(new GridBagLayout());

        // -----------------------------------------------------
        // MAIN CARD
        // -----------------------------------------------------

        ShadowPanel card =
                new ShadowPanel(24, CARD_BACKGROUND);

        card.setLayout(new GridBagLayout());

        card.setPreferredSize(
                new Dimension(420, 600)
        );

        card.setBorder(
                new EmptyBorder(20, 42, 18, 42)
        );

        GridBagConstraints outer =
                new GridBagConstraints();

        outer.gridx = 0;
        outer.gridy = 0;
        outer.anchor = GridBagConstraints.CENTER;
        outer.insets = new Insets(0, 0, 12, 0);

        add(card, outer);

        // -----------------------------------------------------
        // CONTENT CONSTRAINTS
        // -----------------------------------------------------

        GridBagConstraints c =
                new GridBagConstraints();

        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

        c.gridy = 0;
        c.insets = new Insets(0, 0, 1, 0);

        card.add(createLogo(), c);

        // -----------------------------------------------------
        // BRAND
        // -----------------------------------------------------

        JLabel brandLabel =
                new JLabel("OBITRICY", SwingConstants.CENTER);

        brandLabel.setFont(BRAND_FONT);
        brandLabel.setForeground(GOLD_LIGHT);

        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);

        card.add(brandLabel, c);

        // -----------------------------------------------------
        // BRAND SUBTITLE
        // -----------------------------------------------------

        JLabel brandSubtitle =
                new JLabel(
                        "CHESS EXPERIENCE",
                        SwingConstants.CENTER
                );

        brandSubtitle.setFont(BRAND_SUBTITLE_FONT);
        brandSubtitle.setForeground(GOLD);

        c.gridy++;
        c.insets = new Insets(0, 0, 11, 0);

        card.add(brandSubtitle, c);

        // -----------------------------------------------------
        // DECORATIVE DIVIDER
        // -----------------------------------------------------

        c.gridy++;
        c.insets = new Insets(0, 0, 14, 0);

        card.add(createDivider(), c);

        // -----------------------------------------------------
        // WELCOME
        // -----------------------------------------------------

        JLabel welcome =
                new JLabel(
                        "Welcome Back",
                        SwingConstants.CENTER
                );

        welcome.setFont(WELCOME_FONT);
        welcome.setForeground(TEXT_PRIMARY);

        c.gridy++;
        c.insets = new Insets(0, 0, 3, 0);

        card.add(welcome, c);

        // -----------------------------------------------------
        // DESCRIPTION
        // -----------------------------------------------------

        JLabel description =
                new JLabel(
                        "Sign in to continue your chess journey",
                        SwingConstants.CENTER
                );

        description.setFont(DESCRIPTION_FONT);
        description.setForeground(TEXT_SECONDARY);

        c.gridy++;
        c.insets = new Insets(0, 0, 17, 0);

        card.add(description, c);

        // -----------------------------------------------------
        // USERNAME LABEL
        // -----------------------------------------------------

        c.gridy++;
        c.insets = new Insets(0, 0, 5, 0);

        card.add(createLabel("USERNAME"), c);

        // -----------------------------------------------------
        // USERNAME FIELD
        // -----------------------------------------------------

        usernameField =
                createTextField(
                        "Username or email",
                        new UserIcon()
                );

        usernameField.setToolTipText(
                "Enter your username or email"
        );

        c.gridy++;
        c.insets = new Insets(0, 0, 11, 0);

        card.add(usernameField, c);

        // -----------------------------------------------------
        // PASSWORD LABEL
        // -----------------------------------------------------

        c.gridy++;
        c.insets = new Insets(0, 0, 5, 0);

        card.add(createLabel("PASSWORD"), c);

        // -----------------------------------------------------
        // PASSWORD FIELD + EYE
        // -----------------------------------------------------

        PasswordFieldPanel passwordPanel =
                new PasswordFieldPanel();

        passwordField =
                passwordPanel.getPasswordField();

        defaultEchoChar =
                passwordField.getEchoChar();

        c.gridy++;
        c.insets = new Insets(0, 0, 10, 0);

        card.add(passwordPanel, c);

        // -----------------------------------------------------
        // OPTIONS
        // -----------------------------------------------------

        JPanel optionsPanel =
                new JPanel(new BorderLayout());

        optionsPanel.setOpaque(false);

        rememberMeCheckBox =
                createCheckBox("Remember me");

        optionsPanel.add(
                rememberMeCheckBox,
                BorderLayout.WEST
        );

        forgotPasswordButton =
                createLinkButton("Forgot password?");

        optionsPanel.add(
                forgotPasswordButton,
                BorderLayout.EAST
        );

        c.gridy++;
        c.insets = new Insets(0, 0, 15, 0);

        card.add(optionsPanel, c);

        // -----------------------------------------------------
        // SIGN IN
        // -----------------------------------------------------

        JButton loginButton =
                createPrimaryButton("SIGN IN");

        c.gridy++;
        c.insets = new Insets(0, 0, 9, 0);

        card.add(loginButton, c);

        // -----------------------------------------------------
        // CREATE ACCOUNT
        // -----------------------------------------------------

        JButton registerButton =
                createSecondaryButton("CREATE ACCOUNT");

        c.gridy++;
        c.insets = new Insets(0, 0, 8, 0);

        card.add(registerButton, c);

        // -----------------------------------------------------
        // LOADING INDICATOR
        // -----------------------------------------------------

        loadingBar =
                createLoadingBar();

        c.gridy++;
        c.insets = new Insets(0, 0, 7, 0);

        card.add(loadingBar, c);

        // -----------------------------------------------------
        // FOOTER
        // -----------------------------------------------------

        JLabel footer =
                new JLabel(
                        "OBITRICY  •  PLAY. THINK. CONQUER.",
                        SwingConstants.CENTER
                );

        footer.setFont(
                new Font("Segoe UI", Font.PLAIN, 9)
        );

        footer.setForeground(TEXT_MUTED);

        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);

        card.add(footer, c);

        // -----------------------------------------------------
        // HIDDEN COMPATIBILITY CHECKBOX
        // -----------------------------------------------------

        showPasswordCheckBox =
                new JCheckBox();

        showPasswordCheckBox.setVisible(false);

        showPasswordCheckBox.addActionListener(
                e -> updatePasswordVisibility()
        );

        // -----------------------------------------------------
        // PASSWORD ENTER KEY
        // -----------------------------------------------------

        passwordField.addActionListener(
                e -> loginButton.doClick()
        );

        // -----------------------------------------------------
        // LOGIN ACTION
        // -----------------------------------------------------

        loginButton.addActionListener(
                e -> {

                    String user =
                            usernameField.getText().trim();

                    String pass =
                            new String(
                                    passwordField.getPassword()
                            ).trim();

                    if (user.isEmpty() || pass.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Please enter your username and password.",
                                "Login Required",
                                JOptionPane.WARNING_MESSAGE
                        );

                        if (user.isEmpty()) {
                            usernameField.requestFocusInWindow();
                        } else {
                            passwordField.requestFocusInWindow();
                        }

                        return;
                    }

                    loginButton.setEnabled(false);
                    registerButton.setEnabled(false);
                    forgotPasswordButton.setEnabled(false);
                    rememberMeCheckBox.setEnabled(false);

                    setLoading(true);

                    SwingWorker<Boolean, Void> worker =
                            new SwingWorker<>() {

                                @Override
                                protected Boolean doInBackground()
                                        throws Exception {

                                    Thread.sleep(700);

                                    return LoginManager.login(
                                            user,
                                            pass
                                    );
                                }

                                @Override
                                protected void done() {

                                    setLoading(false);

                                    try {

                                        if (get()) {

                                            frame.setCurrentUsername(user);

                                            // -------------------------------------------------
                                            // OBITRICY SUCCESS DIALOG
                                            // -------------------------------------------------

                                            LoginSuccessDialog.show(
                                                    SwingUtilities.getWindowAncestor(
                                                            LoginPanel.this
                                                    ),
                                                    user
                                            );

                                            // -------------------------------------------------
                                            // REMEMBER USER
                                            // -------------------------------------------------

                                            if (rememberMeCheckBox.isSelected()) {

                                                System.out.println(
                                                        "Remembering user: "
                                                                + user
                                                );
                                            }

                                            // -------------------------------------------------
                                            // CONTINUE TO MAIN MENU
                                            // -------------------------------------------------

                                            frame.showMenu();

                                        } else {

                                            loginButton.setEnabled(true);
                                            registerButton.setEnabled(true);
                                            forgotPasswordButton.setEnabled(true);
                                            rememberMeCheckBox.setEnabled(true);

                                            JOptionPane.showMessageDialog(
                                                    LoginPanel.this,
                                                    "Invalid username or password.",
                                                    "Login Failed",
                                                    JOptionPane.ERROR_MESSAGE
                                            );

                                            passwordField.selectAll();
                                            passwordField.requestFocusInWindow();
                                        }

                                    } catch (Exception ex) {

                                        loginButton.setEnabled(true);
                                        registerButton.setEnabled(true);
                                        forgotPasswordButton.setEnabled(true);
                                        rememberMeCheckBox.setEnabled(true);

                                        JOptionPane.showMessageDialog(
                                                LoginPanel.this,
                                                "Login failed.",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE
                                        );

                                        ex.printStackTrace();
                                    }

                                    revalidate();
                                    repaint();
                                }
                            };

                    worker.execute();
                }
        );

        // -----------------------------------------------------
        // REGISTER ACTION
        // -----------------------------------------------------

        registerButton.addActionListener(
                e -> frame.showRegister()
        );

        // -----------------------------------------------------
        // FORGOT PASSWORD ACTION
        // -----------------------------------------------------

        forgotPasswordButton.addActionListener(
                e -> {

                    JOptionPane.showMessageDialog(
                            this,
                            "Password recovery is not implemented yet.\n"
                                    + "Please contact the administrator.",
                            "Password Recovery",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
        );

        // -----------------------------------------------------
        // ENTER = LOGIN
        // -----------------------------------------------------

        SwingUtilities.invokeLater(
                () -> {

                    JRootPane rootPane =
                            getRootPane();

                    if (rootPane != null) {
                        rootPane.setDefaultButton(loginButton);
                    }
                }
        );
    }

    // =========================================================
    // LOADING
    // =========================================================

    private JProgressBar createLoadingBar() {

        JProgressBar bar =
                new JProgressBar();

        bar.setIndeterminate(true);
        bar.setVisible(false);
        bar.setBorderPainted(false);
        bar.setOpaque(false);
        bar.setPreferredSize(
                new Dimension(330, 5)
        );

        return bar;
    }

    private void setLoading(boolean loading) {

        loadingBar.setVisible(loading);

        if (loading) {
            loadingBar.setIndeterminate(true);
        }

        revalidate();
        repaint();
    }

    // =========================================================
    // LOGO
    // =========================================================

    private JLabel createLogo() {

        JLabel logoLabel;

        try {

            BufferedImage original =
                    ImageIO.read(
                            getClass().getResource(
                                    "/images/chess_logo.png"
                            )
                    );

            BufferedImage transparent =
                    makeLogoTransparent(original);

            Image scaledImage =
                    transparent.getScaledInstance(
                            78,
                            78,
                            Image.SCALE_SMOOTH
                    );

            logoLabel =
                    new JLabel(
                            new ImageIcon(scaledImage)
                    );

        } catch (Exception ex) {

            System.err.println(
                    "Unable to load chess_logo.png"
            );

            logoLabel =
                    new JLabel("♔");

            logoLabel.setFont(
                    new Font(
                            "Serif",
                            Font.PLAIN,
                            60
                    )
            );

            logoLabel.setForeground(GOLD_LIGHT);
        }

        logoLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        logoLabel.setPreferredSize(
                new Dimension(330, 78)
        );

        return logoLabel;
    }

    // =========================================================
    // BETTER LOGO TRANSPARENCY
    // =========================================================

    private BufferedImage makeLogoTransparent(
            BufferedImage source) {

        BufferedImage result =
                new BufferedImage(
                        source.getWidth(),
                        source.getHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );

        for (int y = 0;
             y < source.getHeight();
             y++) {

            for (int x = 0;
                 x < source.getWidth();
                 x++) {

                int argb =
                        source.getRGB(x, y);

                int alpha =
                        (argb >>> 24) & 0xFF;

                int red =
                        (argb >>> 16) & 0xFF;

                int green =
                        (argb >>> 8) & 0xFF;

                int blue =
                        argb & 0xFF;

                int brightness =
                        (red + green + blue) / 3;

                int newAlpha = alpha;

                // Remove white/near-white background,
                // including anti-aliased edge pixels.
                if (brightness > 238) {
                    newAlpha = 0;
                } else if (brightness > 220) {
                    newAlpha =
                            Math.min(
                                    newAlpha,
                                    80
                            );
                } else if (brightness > 205) {
                    newAlpha =
                            Math.min(
                                    newAlpha,
                                    150
                            );
                }

                result.setRGB(
                        x,
                        y,
                        (newAlpha << 24)
                                | (red << 16)
                                | (green << 8)
                                | blue
                );
            }
        }

        return result;
    }

    // =========================================================
    // DIVIDER
    // =========================================================

    private JPanel createDivider() {

        JPanel panel =
                new JPanel() {

                    @Override
                    protected void paintComponent(Graphics g) {

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        try {

                            g2.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON
                            );

                            int center =
                                    getWidth() / 2;

                            int y =
                                    getHeight() / 2;

                            g2.setStroke(
                                    new BasicStroke(1f)
                            );

                            g2.setColor(
                                    new Color(
                                            GOLD.getRed(),
                                            GOLD.getGreen(),
                                            GOLD.getBlue(),
                                            80
                                    )
                            );

                            g2.drawLine(
                                    0,
                                    y,
                                    center - 12,
                                    y
                            );

                            g2.drawLine(
                                    center + 12,
                                    y,
                                    getWidth(),
                                    y
                            );

                            g2.setColor(GOLD);

                            Polygon diamond =
                                    new Polygon();

                            diamond.addPoint(
                                    center,
                                    y - 5
                            );

                            diamond.addPoint(
                                    center + 5,
                                    y
                            );

                            diamond.addPoint(
                                    center,
                                    y + 5
                            );

                            diamond.addPoint(
                                    center - 5,
                                    y
                            );

                            g2.fillPolygon(diamond);

                        } finally {

                            g2.dispose();
                        }
                    }
                };

        panel.setOpaque(false);
        panel.setPreferredSize(
                new Dimension(330, 12)
        );

        return panel;
    }

    // =========================================================
    // LABEL
    // =========================================================

    private JLabel createLabel(String text) {

        JLabel label =
                new JLabel(text);

        label.setFont(LABEL_FONT);
        label.setForeground(GOLD_LIGHT);

        return label;
    }

    // =========================================================
    // USERNAME FIELD
    // =========================================================

    private JTextField createTextField(
            String placeholder,
            Icon icon) {

        IconTextField field =
                new IconTextField(icon);

        field.setFont(FIELD_FONT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(FIELD_BACKGROUND);
        field.setCaretColor(GOLD_LIGHT);

        field.setPreferredSize(
                new Dimension(330, 46)
        );

        installFocusBorder(field);

        return field;
    }

    // =========================================================
    // PASSWORD FIELD
    // =========================================================

    private class PasswordFieldPanel extends JPanel {

        private final IconPasswordField field;
        private final JButton eyeButton;

        PasswordFieldPanel() {

            setOpaque(false);
            setLayout(new BorderLayout(0, 0));

            setPreferredSize(
                    new Dimension(330, 46)
            );

            field =
                    new IconPasswordField(
                            new LockIcon()
                    );

            field.setFont(FIELD_FONT);
            field.setForeground(TEXT_PRIMARY);
            field.setBackground(FIELD_BACKGROUND);
            field.setCaretColor(GOLD_LIGHT);

            field.setBorder(
                    BorderFactory.createCompoundBorder(
                            new RoundedLineBorder(
                                    FIELD_BORDER,
                                    8
                            ),
                            new EmptyBorder(
                                    8,
                                    40,
                                    8,
                                    42
                            )
                    )
            );

            eyeButton =
                    new JButton(
                            new EyeIcon(false)
                    );

            eyeButton.setBorderPainted(false);
            eyeButton.setContentAreaFilled(false);
            eyeButton.setFocusPainted(false);
            eyeButton.setOpaque(false);
            eyeButton.setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );

            eyeButton.setToolTipText(
                    "Show password"
            );

            eyeButton.setPreferredSize(
                    new Dimension(40, 40)
            );

            eyeButton.addActionListener(
                    e -> {

                        boolean showing =
                                showPasswordCheckBox.isSelected();

                        showPasswordCheckBox.setSelected(
                                !showing
                        );

                        updatePasswordVisibility();

                        eyeButton.setIcon(
                                new EyeIcon(!showing)
                        );

                        eyeButton.setToolTipText(
                                !showing
                                        ? "Hide password"
                                        : "Show password"
                        );

                        field.requestFocusInWindow();
                    }
            );

            add(field, BorderLayout.CENTER);
            add(eyeButton, BorderLayout.EAST);

            installPasswordFocusBorder(field);
        }

        JPasswordField getPasswordField() {
            return field;
        }
    }

    // =========================================================
    // PASSWORD VISIBILITY
    // =========================================================

    private void updatePasswordVisibility() {

        if (passwordField == null) {
            return;
        }

        if (showPasswordCheckBox.isSelected()) {

            passwordField.setEchoChar((char) 0);

        } else {

            passwordField.setEchoChar(defaultEchoChar);
        }
    }

    // =========================================================
    // FOCUS BORDER - USERNAME
    // =========================================================

    private void installFocusBorder(
            JComponent field) {

        field.addFocusListener(
                new FocusAdapter() {

                    @Override
                    public void focusGained(FocusEvent e) {

                        field.setBorder(
                                BorderFactory.createCompoundBorder(
                                        new RoundedLineBorder(
                                                FIELD_FOCUS,
                                                8
                                        ),
                                        new EmptyBorder(
                                                8,
                                                40,
                                                8,
                                                15
                                        )
                                )
                        );

                        field.repaint();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {

                        field.setBorder(
                                BorderFactory.createCompoundBorder(
                                        new RoundedLineBorder(
                                                FIELD_BORDER,
                                                8
                                        ),
                                        new EmptyBorder(
                                                8,
                                                40,
                                                8,
                                                15
                                        )
                                )
                        );

                        field.repaint();
                    }
                }
        );
    }

    // =========================================================
    // FOCUS BORDER - PASSWORD
    // =========================================================

    private void installPasswordFocusBorder(
            JComponent field) {

        field.addFocusListener(
                new FocusAdapter() {

                    @Override
                    public void focusGained(FocusEvent e) {

                        field.setBorder(
                                BorderFactory.createCompoundBorder(
                                        new RoundedLineBorder(
                                                FIELD_FOCUS,
                                                8
                                        ),
                                        new EmptyBorder(
                                                8,
                                                40,
                                                8,
                                                42
                                        )
                                )
                        );

                        field.repaint();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {

                        field.setBorder(
                                BorderFactory.createCompoundBorder(
                                        new RoundedLineBorder(
                                                FIELD_BORDER,
                                                8
                                        ),
                                        new EmptyBorder(
                                                8,
                                                40,
                                                8,
                                                42
                                        )
                                )
                        );

                        field.repaint();
                    }
                }
        );
    }

    // =========================================================
    // CHECKBOX
    // =========================================================

    private JCheckBox createCheckBox(String text) {

        JCheckBox checkBox =
                new JCheckBox(text) {

                    @Override
                    protected void paintComponent(Graphics g) {

                        Graphics2D g2 =
                                (Graphics2D) g.create();

                        try {

                            g2.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON
                            );

                            int box = 14;

                            int y =
                                    (getHeight() - box) / 2;

                            if (isSelected()) {

                                g2.setColor(GOLD);

                                g2.fillRoundRect(
                                        0,
                                        y,
                                        box,
                                        box,
                                        4,
                                        4
                                );

                                g2.setColor(
                                        new Color(30, 24, 12)
                                );

                                g2.setStroke(
                                        new BasicStroke(
                                                1.8f,
                                                BasicStroke.CAP_ROUND,
                                                BasicStroke.JOIN_ROUND
                                        )
                                );

                                g2.drawLine(
                                        3,
                                        y + 7,
                                        6,
                                        y + 10
                                );

                                g2.drawLine(
                                        6,
                                        y + 10,
                                        11,
                                        y + 4
                                );

                            } else {

                                g2.setColor(
                                        new Color(
                                                255,
                                                255,
                                                255,
                                                18
                                        )
                                );

                                g2.fillRoundRect(
                                        0,
                                        y,
                                        box,
                                        box,
                                        4,
                                        4
                                );

                                g2.setColor(
                                        new Color(
                                                255,
                                                255,
                                                255,
                                                65
                                        )
                                );

                                g2.drawRoundRect(
                                        0,
                                        y,
                                        box - 1,
                                        box - 1,
                                        4,
                                        4
                                );
                            }

                        } finally {

                            g2.dispose();
                        }

                        super.paintComponent(g);
                    }
                };

        checkBox.setIcon(null);
        checkBox.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        checkBox.setForeground(TEXT_SECONDARY);
        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
        checkBox.setBorderPainted(false);
        checkBox.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        checkBox.setIconTextGap(7);

        return checkBox;
    }

    // =========================================================
    // LINK BUTTON
    // =========================================================

    private JButton createLinkButton(String text) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font("Segoe UI", Font.PLAIN, 11)
        );

        button.setForeground(TEXT_SECONDARY);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(MouseEvent e) {

                        button.setForeground(GOLD_LIGHT);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                        if (button.isEnabled()) {
                            button.setForeground(TEXT_SECONDARY);
                        }
                    }
                }
        );

        return button;
    }

    // =========================================================
    // PRIMARY BUTTON
    // =========================================================

    private JButton createPrimaryButton(String text) {

        RoundedButton button =
                new RoundedButton(
                        text,
                        GOLD
                );

        button.setFont(BUTTON_FONT);
        button.setForeground(
                new Color(24, 19, 9)
        );

        button.setPreferredSize(
                new Dimension(330, 46)
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    // =========================================================
    // SECONDARY BUTTON
    // =========================================================

    private JButton createSecondaryButton(String text) {

        RoundedButton button =
                new RoundedButton(
                        text,
                        SECONDARY_BUTTON
                );

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );

        button.setForeground(GOLD_LIGHT);

        button.setPreferredSize(
                new Dimension(330, 42)
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    // =========================================================
    // ROUNDED BUTTON
    // =========================================================

    private static class RoundedButton extends JButton {

        private final Color normalColor;
        private boolean hover;

        RoundedButton(
                String text,
                Color color) {

            super(text);

            normalColor = color;
            setBackground(color);
            setOpaque(false);

            addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            hover = true;
                            repaint();
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            hover = false;
                            repaint();
                        }
                    }
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                Color background;

                if (!isEnabled()) {

                    background =
                            new Color(70, 75, 84);

                } else if (hover) {

                    background =
                            normalColor.equals(GOLD)
                                    ? GOLD_LIGHT
                                    : SECONDARY_BUTTON_HOVER;

                } else {

                    background = normalColor;
                }

                if (normalColor.equals(GOLD)) {

                    GradientPaint paint =
                            new GradientPaint(
                                    0,
                                    0,
                                    background,
                                    0,
                                    getHeight(),
                                    GOLD_DARK
                            );

                    g2.setPaint(paint);

                } else {

                    g2.setColor(background);
                }

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        12,
                        12
                );

            } finally {

                g2.dispose();
            }

            super.paintComponent(g);
        }
    }

    // =========================================================
    // ROUNDED BORDER
    // =========================================================

    private static class RoundedLineBorder
            extends javax.swing.border.LineBorder {

        private final int radius;

        RoundedLineBorder(
                Color color,
                int radius) {

            super(color, 1, true);

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

                g2.setColor(lineColor);
                g2.drawRoundRect(
                        x,
                        y,
                        width - 1,
                        height - 1,
                        radius,
                        radius
                );

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================================================
    // SHADOW CARD
    // =========================================================

    private static class ShadowPanel
            extends JPanel {

        private final int radius;
        private final Color background;

        ShadowPanel(
                int radius,
                Color background) {

            this.radius = radius;
            this.background = background;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                // Soft outer shadow.
                for (int i = 14; i >= 2; i -= 2) {

                    int alpha =
                            Math.max(
                                    2,
                                    24 - (i * 2)
                            );

                    g2.setColor(
                            new Color(
                                    0,
                                    0,
                                    0,
                                    alpha
                            )
                    );

                    g2.fillRoundRect(
                            -i / 2,
                            i / 3,
                            getWidth() + i,
                            getHeight() + i / 2,
                            radius + i,
                            radius + i
                    );
                }

                // Card.
                g2.setColor(background);

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        radius,
                        radius
                );

                // Subtle inner highlight.
                g2.setColor(CARD_INNER_BORDER);

                g2.drawRoundRect(
                        1,
                        1,
                        getWidth() - 3,
                        getHeight() - 3,
                        radius - 2,
                        radius - 2
                );

                // Gold top accent.
                GradientPaint goldAccent =
                        new GradientPaint(
                                70,
                                0,
                                new Color(
                                        GOLD.getRed(),
                                        GOLD.getGreen(),
                                        GOLD.getBlue(),
                                        25
                                ),
                                getWidth() / 2f,
                                0,
                                new Color(
                                        GOLD.getRed(),
                                        GOLD.getGreen(),
                                        GOLD.getBlue(),
                                        175
                                )
                        );

                g2.setPaint(goldAccent);

                g2.fillRoundRect(
                        72,
                        0,
                        getWidth() - 144,
                        2,
                        2,
                        2
                );

                // Outer border.
                g2.setColor(CARD_BORDER);

                g2.drawRoundRect(
                        0,
                        0,
                        getWidth() - 1,
                        getHeight() - 1,
                        radius,
                        radius
                );

            } finally {

                g2.dispose();
            }

            super.paintComponent(g);
        }

        @Override
        protected void paintChildren(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                Shape clip =
                        new RoundRectangle2D.Double(
                                0,
                                0,
                                getWidth(),
                                getHeight(),
                                radius,
                                radius
                        );

                g2.clip(clip);

                super.paintChildren(g2);

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================================================
    // ICON TEXT FIELD
    // =========================================================

    private static class IconTextField
            extends JTextField {

        private final Icon icon;

        IconTextField(Icon icon) {

            this.icon = icon;

            setBorder(
                    BorderFactory.createCompoundBorder(
                            new RoundedLineBorder(
                                    FIELD_BORDER,
                                    8
                            ),
                            new EmptyBorder(
                                    8,
                                    40,
                                    8,
                                    15
                            )
                    )
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                super.paintComponent(g);

                if (icon != null) {

                    int x = 11;

                    int y =
                            (getHeight()
                                    - icon.getIconHeight())
                                    / 2;

                    icon.paintIcon(
                            this,
                            g2,
                            x,
                            y
                    );
                }

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================================================
    // ICON PASSWORD FIELD
    // =========================================================

    private static class IconPasswordField
            extends JPasswordField {

        private final Icon icon;

        IconPasswordField(Icon icon) {

            this.icon = icon;
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                super.paintComponent(g);

                if (icon != null) {

                    int x = 11;

                    int y =
                            (getHeight()
                                    - icon.getIconHeight())
                                    / 2;

                    icon.paintIcon(
                            this,
                            g2,
                            x,
                            y
                    );
                }

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================================================
    // USER ICON
    // =========================================================

    private static class UserIcon implements Icon {

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(GOLD);

                g2.fillOval(
                        x + 6,
                        y + 2,
                        8,
                        8
                );

                g2.fillRoundRect(
                        x + 3,
                        y + 10,
                        14,
                        8,
                        7,
                        7
                );

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================================================
    // LOCK ICON
    // =========================================================

    private static class LockIcon implements Icon {

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(GOLD);

                g2.setStroke(
                        new BasicStroke(
                                2f,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND
                        )
                );

                g2.drawArc(
                        x + 5,
                        y + 2,
                        10,
                        11,
                        0,
                        180
                );

                g2.fillRoundRect(
                        x + 3,
                        y + 9,
                        14,
                        10,
                        3,
                        3
                );

                g2.setColor(
                        new Color(30, 25, 15)
                );

                g2.fillOval(
                        x + 9,
                        y + 12,
                        2,
                        4
                );

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================================================
    // EYE ICON
    // =========================================================

    private static class EyeIcon implements Icon {

        private final boolean visible;

        EyeIcon(boolean visible) {
            this.visible = visible;
        }

        @Override
        public int getIconWidth() {
            return 22;
        }

        @Override
        public int getIconHeight() {
            return 22;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            try {

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(
                        TEXT_MUTED
                );

                g2.setStroke(
                        new BasicStroke(
                                1.5f,
                                BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND
                        )
                );

                g2.drawOval(
                        x + 3,
                        y + 6,
                        16,
                        10
                );

                if (visible) {

                    g2.fillOval(
                            x + 9,
                            y + 9,
                            4,
                            4
                    );

                } else {

                    g2.drawLine(
                            x + 3,
                            y + 3,
                            x + 19,
                            y + 19
                    );
                }

            } finally {

                g2.dispose();
            }
        }
    }

    // =========================================================
    // BACKGROUND
    // =========================================================

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 =
                (Graphics2D) g.create();

        try {

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // -------------------------------------------------
            // DARK GRADIENT
            // -------------------------------------------------

            GradientPaint gradient =
                    new GradientPaint(
                            0,
                            0,
                            BACKGROUND_TOP,
                            getWidth(),
                            getHeight(),
                            BACKGROUND_BOTTOM
                    );

            g2.setPaint(gradient);

            g2.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

            // -------------------------------------------------
            // SUBTLE CHESSBOARD
            // -------------------------------------------------

            int square =
                    Math.max(
                            74,
                            Math.min(
                                    105,
                                    Math.max(
                                            1,
                                            getWidth() / 11
                                    )
                            )
                    );

            for (int row = 0;
                 row < getHeight() / square + 2;
                 row++) {

                for (int col = 0;
                     col < getWidth() / square + 2;
                     col++) {

                    g2.setColor(
                            ((row + col) % 2 == 0)
                                    ? CHESS_LIGHT
                                    : CHESS_DARK
                    );

                    g2.fillRect(
                            col * square,
                            row * square,
                            square,
                            square
                    );
                }
            }

            // -------------------------------------------------
            // DECORATIVE CHESS PIECES
            // -------------------------------------------------

            g2.setFont(
                    new Font(
                            "Serif",
                            Font.PLAIN,
                            210
                    )
            );

            g2.setColor(PIECE_COLOR);

            g2.drawString(
                    "♔",
                    -28,
                    275
            );

            g2.drawString(
                    "♕",
                    getWidth() - 220,
                    350
            );

            g2.drawString(
                    "♜",
                    30,
                    getHeight() - 65
            );

            g2.drawString(
                    "♞",
                    getWidth() - 185,
                    170
            );

            // -------------------------------------------------
            // SMALL PIECES
            // -------------------------------------------------

            g2.setFont(
                    new Font(
                            "Serif",
                            Font.PLAIN,
                            78
                    )
            );

            g2.setColor(
                    new Color(
                            255,
                            255,
                            255,
                            4
                    )
            );

            g2.drawString(
                    "♟",
                    34,
                    getHeight() / 2
            );

            g2.drawString(
                    "♝",
                    getWidth() - 86,
                    getHeight() - 165
            );

            // -------------------------------------------------
            // SOFT CENTER GLOW
            // -------------------------------------------------

            RadialGradientPaint glow =
                    new RadialGradientPaint(
                            new Point(
                                    getWidth() / 2,
                                    getHeight() / 2
                            ),
                            Math.max(
                                    getWidth(),
                                    getHeight()
                            ) * 0.70f,
                            new float[]{
                                    0.0f,
                                    0.42f,
                                    1.0f
                            },
                            new Color[]{
                                    new Color(
                                            212,
                                            175,
                                            82,
                                            10
                                    ),
                                    new Color(
                                            35,
                                            45,
                                            60,
                                            5
                                    ),
                                    new Color(
                                            0,
                                            0,
                                            0,
                                            115
                                    )
                            }
                    );

            g2.setPaint(glow);

            g2.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

        } finally {

            g2.dispose();
        }

        super.paintComponent(g);
    }
}

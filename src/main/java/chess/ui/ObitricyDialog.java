package chess.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Unified premium dialog system for Obitricy Chess Game.
 *
 * This class provides the master visual style for application dialogs.
 *
 * IMPORTANT:
 * This class contains UI only.
 * It does not modify chess, puzzle, login, or multiplayer logic.
 */
public final class ObitricyDialog {

    private ObitricyDialog() {
        // Utility class
    }

    // =========================================================
    // MASTER COLORS
    // =========================================================

    private static final Color BG =
            new Color(7, 11, 17);

    private static final Color CARD =
            new Color(16, 23, 33);

    private static final Color CARD_2 =
            new Color(23, 32, 44);

    private static final Color GOLD =
            new Color(218, 175, 70);

    private static final Color GOLD_LIGHT =
            new Color(247, 215, 125);

    private static final Color BLUE =
            new Color(75, 145, 235);

    private static final Color BLUE_LIGHT =
            new Color(125, 185, 255);

    private static final Color GREEN =
            new Color(76, 190, 115);

    private static final Color GREEN_LIGHT =
            new Color(125, 230, 155);

    private static final Color RED =
            new Color(205, 70, 70);

    private static final Color RED_LIGHT =
            new Color(245, 120, 120);

    private static final Color TEXT =
            new Color(246, 248, 251);

    private static final Color MUTED =
            new Color(170, 182, 198);

    private static final Color BORDER =
            new Color(255, 255, 255, 30);

    private static final Color STAR_EMPTY =
            new Color(82, 87, 98);

    // =========================================================
    // DIMENSIONS
    // =========================================================

    private static final int CARD_RADIUS = 26;

    private static final int BUTTON_HEIGHT = 44;

    private static final int MIN_BUTTON_WIDTH = 116;

    private static final int SINGLE_DIALOG_WIDTH = 480;

    private static final int MULTI_DIALOG_WIDTH = 560;

    // =========================================================
    // PUBLIC MESSAGE METHODS
    // =========================================================

    public static void showMessage(
            Component parent,
            String title,
            String message
    ) {
        show(
                parent,
                title,
                message,
                DialogType.INFO,
                new String[]{"OK"},
                0
        );
    }

    public static void showInfo(
            Component parent,
            String title,
            String message
    ) {
        show(
                parent,
                title,
                message,
                DialogType.INFO,
                new String[]{"OK"},
                0
        );
    }

    public static void showWarning(
            Component parent,
            String title,
            String message
    ) {
        show(
                parent,
                title,
                message,
                DialogType.WARNING,
                new String[]{"OK"},
                0
        );
    }

    public static void showError(
            Component parent,
            String title,
            String message
    ) {
        show(
                parent,
                title,
                message,
                DialogType.ERROR,
                new String[]{"OK"},
                0
        );
    }

    public static void showSuccess(
            Component parent,
            String title,
            String message
    ) {
        show(
                parent,
                title,
                message,
                DialogType.SUCCESS,
                new String[]{"OK"},
                0
        );
    }

    public static void showOnline(
            Component parent,
            String title,
            String message
    ) {
        show(
                parent,
                title,
                message,
                DialogType.ONLINE,
                new String[]{"OK"},
                0
        );
    }

    // =========================================================
    // CONFIRMATION
    // =========================================================

    /**
     * Displays a premium YES / NO dialog.
     *
     * @return true when YES is selected.
     */
    public static boolean confirm(
            Component parent,
            String title,
            String message
    ) {
        return show(
                parent,
                title,
                message,
                DialogType.QUESTION,
                new String[]{"YES", "NO"},
                0
        ) == 0;
    }

    /**
     * Displays a premium confirmation dialog with custom
     * button labels.
     *
     * @return index of selected button,
     *         or -1 when closed.
     */
    public static int showOptions(
            Component parent,
            String title,
            String message,
            String... options
    ) {

        if (options == null || options.length == 0) {
            return show(
                    parent,
                    title,
                    message,
                    DialogType.INFO,
                    new String[]{"OK"},
                    0
            );
        }

        return show(
                parent,
                title,
                message,
                DialogType.QUESTION,
                options,
                0
        );
    }

    // =========================================================
    // CORE DIALOG
    // =========================================================

    private static int show(
            Component parent,
            String title,
            String message,
            DialogType type,
            String[] buttons,
            int defaultButton
    ) {

        Window owner =
                parent == null
                        ? null
                        : SwingUtilities.getWindowAncestor(parent);

        final JDialog dialog =
                new JDialog(
                        owner,
                        title == null
                                ? "Obitricy"
                                : title,
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setUndecorated(true);
        dialog.setBackground(
                new Color(0, 0, 0, 0)
        );
        dialog.setResizable(false);

        DialogPanel panel =
                new DialogPanel(type);

        panel.setLayout(
                new BorderLayout()
        );

        // =====================================================
        // HEADER
        // =====================================================

        JPanel header =
                createHeader(type);

        panel.add(
                header,
                BorderLayout.NORTH
        );

        // =====================================================
        // CONTENT
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

        content.setBorder(
                new EmptyBorder(
                        10,
                        34,
                        10,
                        34
                )
        );

        JLabel titleLabel =
                new JLabel(
                        title == null
                                ? "Obitricy"
                                : title
                );

        titleLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        titleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        23
                )
        );

        titleLabel.setForeground(TEXT);

        content.add(titleLabel);

        content.add(
                Box.createVerticalStrut(12)
        );

        // -----------------------------------------------------
        // DIVIDER
        // -----------------------------------------------------

        AccentLine divider =
                new AccentLine(
                        getAccent(type)
                );

        divider.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        content.add(divider);

        content.add(
                Box.createVerticalStrut(16)
        );

        // -----------------------------------------------------
        // MESSAGE
        // -----------------------------------------------------

        JLabel messageLabel =
                new JLabel(
                        toHtml(
                                message,
                                370
                        )
                );

        messageLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        messageLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        messageLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        messageLabel.setForeground(MUTED);

        content.add(messageLabel);

        panel.add(
                content,
                BorderLayout.CENTER
        );

        // =====================================================
        // BUTTONS
        // =====================================================

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                12,
                                0
                        )
                );

        buttonPanel.setOpaque(false);

        buttonPanel.setBorder(
                new EmptyBorder(
                        8,
                        22,
                        26,
                        22
                )
        );

        final int[] result =
                {-1};

        for (int i = 0; i < buttons.length; i++) {

            final int index = i;

            boolean primary =
                    i == defaultButton;

            String text =
                    buttons[i] == null
                            ? ""
                            : buttons[i];

            PremiumButton button =
                    new PremiumButton(
                            text,
                            primary,
                            getAccent(type)
                    );

            button.addActionListener(
                    e -> {
                        result[0] = index;
                        dialog.dispose();
                    }
            );

            buttonPanel.add(button);
        }

        panel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // =====================================================
        // KEYBOARD
        // =====================================================

        installKeyboardActions(
                dialog,
                result
        );

        // =====================================================
        // SIZE
        // =====================================================

        int width =
                calculateDialogWidth(
                        buttons
                );

        int height =
                calculateDialogHeight(
                        message,
                        buttons
                );

        dialog.setContentPane(panel);

        dialog.setSize(
                width,
                height
        );

        dialog.setMinimumSize(
                new Dimension(
                        width,
                        height
                )
        );

        if (owner != null) {
            dialog.setLocationRelativeTo(owner);
        } else {
            dialog.setLocationRelativeTo(null);
        }

        dialog.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        dialog.setVisible(true);

        return result[0];
    }

    // =========================================================
    // HEADER CREATION
    // =========================================================

    private static JPanel createHeader(
            DialogType type
    ) {

        JPanel header =
                new JPanel();

        header.setOpaque(false);

        header.setLayout(
                new BorderLayout()
        );

        header.setBorder(
                new EmptyBorder(
                        22,
                        28,
                        8,
                        24
                )
        );

        // -----------------------------------------------------
        // BRAND
        // -----------------------------------------------------

        JPanel brandPanel =
                new JPanel();

        brandPanel.setOpaque(false);

        brandPanel.setLayout(
                new BoxLayout(
                        brandPanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel brand =
                new JLabel("OBITRICY");

        brand.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        17
                )
        );

        brand.setForeground(
                GOLD_LIGHT
        );

        JLabel category =
                new JLabel(
                        getCategoryText(type)
                );

        category.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        10
                )
        );

        category.setForeground(
                getAccent(type)
        );

        brandPanel.add(brand);

        brandPanel.add(
                Box.createVerticalStrut(3)
        );

        brandPanel.add(category);

        // -----------------------------------------------------
        // ICON
        // -----------------------------------------------------

        JLabel icon =
                new JLabel(
                        getIcon(type),
                        SwingConstants.CENTER
                );

        icon.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        27
                )
        );

        icon.setForeground(
                getAccent(type)
        );

        icon.setPreferredSize(
                new Dimension(
                        48,
                        48
                )
        );

        header.add(
                brandPanel,
                BorderLayout.WEST
        );

        header.add(
                icon,
                BorderLayout.EAST
        );

        return header;
    }

    // =========================================================
    // DIALOG SIZE
    // =========================================================

    private static int calculateDialogWidth(
            String[] buttons
    ) {

        if (buttons == null
                || buttons.length <= 1) {

            return SINGLE_DIALOG_WIDTH;
        }

        Font font =
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                );

        FontMetrics metrics =
                new JLabel().getFontMetrics(font);

        int total =
                0;

        for (String button : buttons) {

            String text =
                    button == null
                            ? ""
                            : button;

            int textWidth =
                    metrics.stringWidth(text);

            int buttonWidth =
                    Math.max(
                            MIN_BUTTON_WIDTH,
                            textWidth + 46
                    );

            total += buttonWidth;
        }

        total +=
                (buttons.length - 1) * 12;

        /*
         * The dialog has enough horizontal space for
         * long options such as:
         *
         * RETURN TO MAIN MENU
         * PLAY AGAIN
         * CONTINUE
         */

        return Math.max(
                MULTI_DIALOG_WIDTH,
                total + 70
        );
    }

    private static int calculateDialogHeight(
            String message,
            String[] buttons
    ) {

        if (message == null || message.isBlank()) {
            return 335;
        }

        int lineCount = calculateWrappedLineCount(message, 370);

        /*
         * Keep normal dialogs compact, but give wrapped
         * messages enough vertical space.
         */
        if (lineCount <= 2) {
            return 335;
        }

        if (lineCount <= 4) {
            return 365;
        }

        if (lineCount <= 6) {
            return 400;
        }

        if (lineCount <= 8) {
            return 435;
        }

        return 470;
    }

    // =========================================================
    // KEYBOARD ACTIONS
    // =========================================================

    private static void installKeyboardActions(
            JDialog dialog,
            int[] result
    ) {

        JRootPane root =
                dialog.getRootPane();

        InputMap inputMap =
                root.getInputMap(
                        JComponent.WHEN_IN_FOCUSED_WINDOW
                );

        ActionMap actionMap =
                root.getActionMap();

        // -----------------------------------------------------
        // ESCAPE
        // -----------------------------------------------------

        inputMap.put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_ESCAPE,
                        0
                ),
                "obitricy.escape"
        );

        actionMap.put(
                "obitricy.escape",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            ActionEvent e
                    ) {

                        result[0] = -1;

                        dialog.dispose();
                    }
                }
        );

        // -----------------------------------------------------
        // ENTER
        // -----------------------------------------------------

        inputMap.put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_ENTER,
                        0
                ),
                "obitricy.enter"
        );

        actionMap.put(
                "obitricy.enter",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            ActionEvent e
                    ) {

                        result[0] = 0;

                        dialog.dispose();
                    }
                }
        );
    }

    // =========================================================
// HTML / TEXT
// =========================================================

    private static String toHtml(
            String message,
            int width
    ) {

        if (message == null || message.isBlank()) {

            return
                    "<html>"
                            + "<div style='text-align:center;'>"
                            + "&nbsp;"
                            + "</div>"
                            + "</html>";
        }

        String wrapped =
                wrapText(
                        message,
                        width
                );

        String escaped =
                escapeHtml(wrapped)
                        .replace(
                                "\r\n",
                                "\n"
                        )
                        .replace(
                                "\n",
                                "<br>"
                        );

        return
                "<html>"
                        + "<div style='text-align:center;'>"
                        + escaped
                        + "</div>"
                        + "</html>";
    }


    /**
     * Wraps text according to the actual pixel width
     * available inside the dialog.
     *
     * This is more reliable than depending on Swing's
     * HTML width handling.
     */
    private static String wrapText(
            String text,
            int maxWidth
    ) {

        if (text == null || text.isBlank()) {
            return "";
        }

        Font font =
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                );

        FontMetrics metrics =
                new JLabel().getFontMetrics(font);

        StringBuilder result =
                new StringBuilder();

        String[] paragraphs =
                text.replace(
                        "\r\n",
                        "\n"
                ).split(
                        "\n",
                        -1
                );

        for (int p = 0; p < paragraphs.length; p++) {

            String paragraph =
                    paragraphs[p].trim();

            if (paragraph.isEmpty()) {

                result.append("\n");

                continue;
            }

            String[] words =
                    paragraph.split("\\s+");

            StringBuilder line =
                    new StringBuilder();

            for (String word : words) {

                if (line.length() == 0) {

                    line.append(word);

                    continue;
                }

                String candidate =
                        line
                                + " "
                                + word;

                int candidateWidth =
                        metrics.stringWidth(
                                candidate
                        );

                if (candidateWidth <= maxWidth) {

                    line.append(" ")
                            .append(word);

                } else {

                    result.append(
                            line
                    );

                    result.append("\n");

                    line.setLength(0);

                    line.append(word);
                }
            }

            if (line.length() > 0) {

                result.append(line);
            }

            if (p < paragraphs.length - 1) {

                result.append("\n");
            }
        }

        return result.toString();
    }


    /**
     * Calculates how many visual lines the wrapped
     * message will occupy.
     */
    private static int calculateWrappedLineCount(
            String message,
            int width
    ) {

        String wrapped =
                wrapText(
                        message,
                        width
                );

        if (wrapped.isEmpty()) {
            return 1;
        }

        return wrapped.split(
                "\n",
                -1
        ).length;
    }


    private static String escapeHtml(
            String text
    ) {

        return text
                .replace(
                        "&",
                        "&amp;"
                )
                .replace(
                        "<",
                        "&lt;"
                )
                .replace(
                        ">",
                        "&gt;"
                )
                .replace(
                        "\"",
                        "&quot;"
                );
    }

    // =========================================================
    // COLORS / TYPE
    // =========================================================

    private static Color getAccent(
            DialogType type
    ) {

        return switch (type) {

            case ERROR ->
                    RED;

            case SUCCESS ->
                    GREEN;

            case ONLINE ->
                    BLUE;

            default ->
                    GOLD;
        };
    }

    private static String getCategoryText(
            DialogType type
    ) {

        return switch (type) {

            case ERROR ->
                    "ERROR";

            case WARNING ->
                    "ATTENTION";

            case QUESTION ->
                    "CONFIRMATION";

            case SUCCESS ->
                    "SUCCESS";

            case ONLINE ->
                    "ONLINE";

            case INFO ->
                    "INFORMATION";
        };
    }

    private static String getIcon(
            DialogType type
    ) {

        return switch (type) {

            case ERROR ->
                    "!";

            case WARNING ->
                    "!";

            case QUESTION ->
                    "?";

            case SUCCESS ->
                    "✓";

            case ONLINE ->
                    "●";

            case INFO ->
                    "i";
        };
    }

    // =========================================================
    // DIALOG TYPES
    // =========================================================

    private enum DialogType {

        INFO,
        WARNING,
        ERROR,
        QUESTION,
        SUCCESS,
        ONLINE
    }

    // =========================================================
    // ACCENT LINE
    // =========================================================

    private static class AccentLine
            extends JComponent {

        private final Color color;

        AccentLine(
                Color color
        ) {

            this.color = color;

            setPreferredSize(
                    new Dimension(
                            86,
                            3
                    )
            );

            setMaximumSize(
                    new Dimension(
                            86,
                            3
                    )
            );
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);

            int y =
                    getHeight() / 2;

            g2.fillRoundRect(
                    0,
                    y - 1,
                    getWidth(),
                    2,
                    3,
                    3
            );

            g2.dispose();
        }
    }

    // =========================================================
    // DIALOG PANEL
    // =========================================================

    private static class DialogPanel
            extends JPanel {

        private final Color accent;

        DialogPanel(
                DialogType type
        ) {

            setOpaque(false);

            accent =
                    getAccent(type);
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w =
                    getWidth();

            int h =
                    getHeight();

            // =================================================
            // SOFT SHADOW
            // =================================================

            for (int i = 14; i >= 1; i--) {

                int alpha =
                        Math.max(
                                3,
                                27 - i
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
                        i,
                        i,
                        Math.max(
                                1,
                                w - (i * 2)
                        ),
                        Math.max(
                                1,
                                h - (i * 2)
                        ),
                        CARD_RADIUS,
                        CARD_RADIUS
                );
            }

            // =================================================
            // MAIN CARD
            // =================================================

            g2.setColor(CARD);

            g2.fillRoundRect(
                    7,
                    7,
                    Math.max(
                            1,
                            w - 14
                    ),
                    Math.max(
                            1,
                            h - 14
                    ),
                    CARD_RADIUS,
                    CARD_RADIUS
            );

            // =================================================
            // TOP GOLD / ACCENT LINE
            // =================================================

            g2.setColor(accent);

            g2.fillRoundRect(
                    7,
                    7,
                    Math.max(
                            1,
                            w - 14
                    ),
                    5,
                    CARD_RADIUS,
                    CARD_RADIUS
            );

            // =================================================
            // INNER CONTENT PANEL
            // =================================================

            g2.setColor(
                    new Color(
                            CARD_2.getRed(),
                            CARD_2.getGreen(),
                            CARD_2.getBlue(),
                            72
                    )
            );

            g2.fillRoundRect(
                    18,
                    92,
                    Math.max(
                            1,
                            w - 36
                    ),
                    Math.max(
                            45,
                            h - 164
                    ),
                    18,
                    18
            );

            // =================================================
            // BORDER
            // =================================================

            g2.setColor(BORDER);

            g2.setStroke(
                    new BasicStroke(
                            1f
                    )
            );

            g2.drawRoundRect(
                    7,
                    7,
                    Math.max(
                            1,
                            w - 15
                    ),
                    Math.max(
                            1,
                            h - 15
                    ),
                    CARD_RADIUS,
                    CARD_RADIUS
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }

    // =========================================================
    // PREMIUM BUTTON
    // =========================================================

    private static class PremiumButton
            extends JButton {

        private final boolean primary;

        private final Color accent;

        private boolean hover;

        PremiumButton(
                String text,
                boolean primary,
                Color accent
        ) {

            super(text);

            this.primary =
                    primary;

            this.accent =
                    accent;

            setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            12
                    )
            );

            setForeground(
                    primary
                            ? new Color(
                            12,
                            15,
                            20
                    )
                            : TEXT
            );

            setFocusPainted(false);

            setBorderPainted(false);

            setContentAreaFilled(false);

            setOpaque(false);

            setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );

            Font font =
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            12
                    );

            FontMetrics metrics =
                    getFontMetrics(font);

            int textWidth =
                    metrics.stringWidth(
                            text == null
                                    ? ""
                                    : text
                    );

            /*
             * Extra horizontal padding deliberately
             * increased so long premium labels remain
             * comfortable and readable.
             */

            int width =
                    Math.max(
                            MIN_BUTTON_WIDTH,
                            textWidth + 52
                    );

            setPreferredSize(
                    new Dimension(
                            width,
                            BUTTON_HEIGHT
                    )
            );

            setMinimumSize(
                    new Dimension(
                            width,
                            BUTTON_HEIGHT
                    )
            );

            setMaximumSize(
                    new Dimension(
                            width,
                            BUTTON_HEIGHT
                    )
            );

            addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseEntered(
                                MouseEvent e
                        ) {

                            hover = true;

                            repaint();
                        }

                        @Override
                        public void mouseExited(
                                MouseEvent e
                        ) {

                            hover = false;

                            repaint();
                        }
                    }
            );
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w =
                    getWidth();

            int h =
                    getHeight();

            // =================================================
            // PRIMARY BUTTON
            // =================================================

            if (primary) {

                Color top =
                        hover
                                ? brighten(
                                accent,
                                1.10f
                        )
                                : accent;

                Color bottom =
                        hover
                                ? brighten(
                                accent,
                                0.98f
                        )
                                : darken(
                                accent,
                                0.88f
                        );

                GradientPaint gradient =
                        new GradientPaint(
                                0,
                                0,
                                top,
                                0,
                                h,
                                bottom
                        );

                g2.setPaint(
                        gradient
                );

                g2.fillRoundRect(
                        0,
                        0,
                        w,
                        h,
                        14,
                        14
                );

                // subtle highlight

                g2.setColor(
                        new Color(
                                255,
                                255,
                                255,
                                hover
                                        ? 28
                                        : 18
                        )
                );

                g2.fillRoundRect(
                        1,
                        1,
                        Math.max(
                                1,
                                w - 2
                        ),
                        Math.max(
                                1,
                                h / 2
                        ),
                        13,
                        13
                );

            } else {

                // =================================================
                // SECONDARY BUTTON
                // =================================================

                g2.setColor(
                        hover
                                ? new Color(
                                255,
                                255,
                                255,
                                24
                        )
                                : new Color(
                                255,
                                255,
                                255,
                                10
                        )
                );

                g2.fillRoundRect(
                        0,
                        0,
                        w,
                        h,
                        14,
                        14
                );

                g2.setColor(
                        new Color(
                                255,
                                255,
                                255,
                                38
                        )
                );

                g2.drawRoundRect(
                        0,
                        0,
                        Math.max(
                                1,
                                w - 1
                        ),
                        Math.max(
                                1,
                                h - 1
                        ),
                        14,
                        14
                );
            }

            g2.dispose();

            super.paintComponent(g);
        }

        private static Color brighten(
                Color color,
                float factor
        ) {

            return new Color(
                    Math.min(
                            255,
                            Math.round(
                                    color.getRed()
                                            * factor
                            )
                    ),
                    Math.min(
                            255,
                            Math.round(
                                    color.getGreen()
                                            * factor
                            )
                    ),
                    Math.min(
                            255,
                            Math.round(
                                    color.getBlue()
                                            * factor
                            )
                    )
            );
        }

        private static Color darken(
                Color color,
                float factor
        ) {

            return new Color(
                    Math.max(
                            0,
                            Math.round(
                                    color.getRed()
                                            * factor
                            )
                    ),
                    Math.max(
                            0,
                            Math.round(
                                    color.getGreen()
                                            * factor
                            )
                    ),
                    Math.max(
                            0,
                            Math.round(
                                    color.getBlue()
                                            * factor
                            )
                    )
            );
        }
    }

    // =========================================================
    // RATE US
    // =========================================================

    /**
     * Displays the premium Obitricy 5-star rating dialog.
     *
     * @return selected rating from 1 to 5,
     *         or -1 when cancelled/closed.
     */
    public static int rateUs(
            Component parent
    ) {

        Window owner =
                parent == null
                        ? null
                        : SwingUtilities.getWindowAncestor(parent);

        final JDialog dialog =
                new JDialog(
                        owner,
                        "Rate Us",
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setUndecorated(true);

        dialog.setBackground(
                new Color(
                        0,
                        0,
                        0,
                        0
                )
        );

        dialog.setResizable(false);

        final int[] rating =
                {0};

        DialogPanel panel =
                new DialogPanel(
                        DialogType.SUCCESS
                );

        panel.setLayout(
                new BorderLayout()
        );

        // =====================================================
        // HEADER
        // =====================================================

        panel.add(
                createHeader(
                        DialogType.SUCCESS
                ),
                BorderLayout.NORTH
        );

        // =====================================================
        // CONTENT
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

        content.setBorder(
                new EmptyBorder(
                        8,
                        30,
                        12,
                        30
                )
        );

        JLabel title =
                new JLabel(
                        "Rate Obitricy Chess"
                );

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        title.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        23
                )
        );

        title.setForeground(TEXT);

        content.add(title);

        content.add(
                Box.createVerticalStrut(10)
        );

        AccentLine divider =
                new AccentLine(
                        GOLD
                );

        divider.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        content.add(divider);

        content.add(
                Box.createVerticalStrut(12)
        );

        JLabel message =
                new JLabel(
                        "How would you rate your experience?"
                );

        message.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        message.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        message.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        message.setForeground(MUTED);

        content.add(message);

        content.add(
                Box.createVerticalStrut(15)
        );

        // =====================================================
        // STARS
        // =====================================================

        JPanel stars =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                3,
                                0
                        )
                );

        stars.setOpaque(false);

        JLabel[] starLabels =
                new JLabel[5];

        JLabel ratingText =
                new JLabel(
                        "Select a rating"
                );

        ratingText.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        ratingText.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        ratingText.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        ratingText.setForeground(MUTED);

        for (int i = 0; i < 5; i++) {

            final int selectedRating =
                    i + 1;

            JLabel star =
                    new JLabel(
                            "★"
                    );

            star.setFont(
                    new Font(
                            "SansSerif",
                            Font.PLAIN,
                            38
                    )
            );

            star.setForeground(
                    STAR_EMPTY
            );

            star.setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR
                    )
            );

            star.setToolTipText(
                    selectedRating == 1
                            ? "1 Star"
                            : selectedRating
                              + " Stars"
            );

            starLabels[i] =
                    star;

            star.addMouseListener(
                    new MouseAdapter() {

                        @Override
                        public void mouseEntered(
                                MouseEvent e
                        ) {

                            updateStars(
                                    starLabels,
                                    selectedRating
                            );

                            updateRatingText(
                                    ratingText,
                                    selectedRating
                            );
                        }

                        @Override
                        public void mouseExited(
                                MouseEvent e
                        ) {

                            updateStars(
                                    starLabels,
                                    rating[0]
                            );

                            updateRatingText(
                                    ratingText,
                                    rating[0]
                            );
                        }

                        @Override
                        public void mouseClicked(
                                MouseEvent e
                        ) {

                            rating[0] =
                                    selectedRating;

                            updateStars(
                                    starLabels,
                                    rating[0]
                            );

                            updateRatingText(
                                    ratingText,
                                    rating[0]
                            );
                        }
                    }
            );

            stars.add(star);
        }

        content.add(stars);

        content.add(
                Box.createVerticalStrut(6)
        );

        content.add(ratingText);

        panel.add(
                content,
                BorderLayout.CENTER
        );

        // =====================================================
        // BUTTONS
        // =====================================================

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                12,
                                0
                        )
                );

        buttonPanel.setOpaque(false);

        buttonPanel.setBorder(
                new EmptyBorder(
                        8,
                        24,
                        25,
                        24
                )
        );

        PremiumButton cancelButton =
                new PremiumButton(
                        "CANCEL",
                        false,
                        GOLD
                );

        PremiumButton submitButton =
                new PremiumButton(
                        "SUBMIT RATING",
                        true,
                        GREEN
                );

        cancelButton.addActionListener(
                e -> {

                    rating[0] = -1;

                    dialog.dispose();
                }
        );

        submitButton.addActionListener(
                e -> {

                    if (rating[0] <= 0) {

                        ratingText.setText(
                                "Please select a star rating"
                        );

                        ratingText.setForeground(
                                RED_LIGHT
                        );

                        return;
                    }

                    dialog.dispose();
                }
        );

        buttonPanel.add(
                cancelButton
        );

        buttonPanel.add(
                submitButton
        );

        panel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        // =====================================================
        // KEYBOARD
        // =====================================================

        JRootPane root =
                dialog.getRootPane();

        InputMap inputMap =
                root.getInputMap(
                        JComponent.WHEN_IN_FOCUSED_WINDOW
                );

        ActionMap actionMap =
                root.getActionMap();

        // ESC

        inputMap.put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_ESCAPE,
                        0
                ),
                "rate.cancel"
        );

        actionMap.put(
                "rate.cancel",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            ActionEvent e
                    ) {

                        rating[0] = -1;

                        dialog.dispose();
                    }
                }
        );

        // ENTER

        inputMap.put(
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_ENTER,
                        0
                ),
                "rate.submit"
        );

        actionMap.put(
                "rate.submit",
                new AbstractAction() {

                    @Override
                    public void actionPerformed(
                            ActionEvent e
                    ) {

                        if (rating[0] <= 0) {

                            ratingText.setText(
                                    "Please select a star rating"
                            );

                            ratingText.setForeground(
                                    RED_LIGHT
                            );

                            return;
                        }

                        dialog.dispose();
                    }
                }
        );

        // =====================================================
        // SHOW
        // =====================================================

        dialog.setContentPane(panel);

        dialog.setSize(
                520,
                365
        );

        if (owner != null) {

            dialog.setLocationRelativeTo(
                    owner
            );

        } else {

            dialog.setLocationRelativeTo(
                    null
            );
        }

        dialog.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        dialog.setVisible(true);

        return rating[0];
    }

    // =========================================================
    // RATING HELPERS
    // =========================================================

    /**
     * Updates the visual state of the five stars.
     */
    private static void updateStars(
            JLabel[] stars,
            int rating
    ) {

        for (int i = 0;
             i < stars.length;
             i++) {

            if (i < rating) {

                stars[i].setForeground(
                        GOLD_LIGHT
                );

            } else {

                stars[i].setForeground(
                        STAR_EMPTY
                );
            }
        }
    }

    /**
     * Updates the rating description.
     */
    private static void updateRatingText(
            JLabel label,
            int rating
    ) {

        if (rating <= 0) {

            label.setText(
                    "Select a rating"
            );

            label.setForeground(
                    MUTED
            );

            return;
        }

        String text =
                rating == 1
                        ? "1 Star"
                        : rating + " Stars";

        label.setText(text);

        label.setForeground(
                GOLD_LIGHT
        );
    }
}
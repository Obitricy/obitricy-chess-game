package chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GameOverDialog extends JDialog {

    private static final Color BG = new Color(10,16,24);
    private static final Color CARD = new Color(17,25,36);
    private static final Color CARD2 = new Color(23,32,45);
    private static final Color GOLD = new Color(218,175,70);
    private static final Color GOLD_LIGHT = new Color(246,211,118);
    private static final Color TEXT = new Color(246,248,251);
    private static final Color MUTED = new Color(169,181,197);
    private static final Color BORDER = new Color(255,255,255,32);

    public GameOverDialog(JFrame parent, String title, String message,
                          Runnable newGameAction, Runnable menuAction) {
        super(parent, true);
        setTitle("Game Over");
        setUndecorated(true);
        setResizable(false);
        setBackground(new Color(0,0,0,0));
        setSize(520,380);
        setLocationRelativeTo(parent);

        ResultPanel root = new ResultPanel();
        root.setLayout(new BorderLayout(0,10));
        root.setBorder(BorderFactory.createEmptyBorder(22,34,28,34));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel brand = text("OBITRICY",13,Font.BOLD,GOLD);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = text("GAME OVER",9,Font.BOLD,MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(brand);
        header.add(Box.createVerticalStrut(2));
        header.add(sub);
        root.add(header,BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));

        KingIcon icon = new KingIcon();
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(icon);
        center.add(Box.createVerticalStrut(5));

        JLabel titleLabel = text(
                title == null || title.isBlank() ? "GAME OVER" : title,
                27,Font.BOLD,TEXT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(titleLabel);
        center.add(Box.createVerticalStrut(8));

        String[] lines = message == null ? new String[0] : message.split("\\r?\\n");
        JLabel winner = text(lines.length > 0 ? lines[0] : "",
                18,Font.BOLD,GOLD_LIGHT);
        winner.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(winner);

        if (lines.length > 1) {
            JLabel detail = text(lines[1],13,Font.PLAIN,MUTED);
            detail.setAlignmentX(Component.CENTER_ALIGNMENT);
            center.add(Box.createVerticalStrut(5));
            center.add(detail);
        }
        root.add(center,BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1,2,12,0));
        buttons.setOpaque(false);
        buttons.setPreferredSize(new Dimension(0,44));

        JButton newGame = new GoldButton("NEW GAME");
        JButton menu = new DarkButton("MAIN MENU");
        buttons.add(newGame);
        buttons.add(menu);
        root.add(buttons,BorderLayout.SOUTH);

        newGame.addActionListener(e -> {
            dispose();
            if (newGameAction != null) newGameAction.run();
        });
        menu.addActionListener(e -> {
            dispose();
            if (menuAction != null) menuAction.run();
        });

        getRootPane().setDefaultButton(newGame);
        getRootPane().registerKeyboardAction(
                e -> dispose(), KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        setContentPane(root);
    }

    private static JLabel text(String s,int size,int style,Color c) {
        JLabel l = new JLabel(s,SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI",style,size));
        l.setForeground(c);
        return l;
    }

    private static class ResultPanel extends JPanel {
        private final int radius=24;
        ResultPanel(){setOpaque(false);}
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,120));
                g2.fillRoundRect(6,8,getWidth()-12,getHeight()-12,radius,radius);
                g2.setColor(BG);
                g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,radius,radius);
                g2.setColor(CARD);
                g2.fillRoundRect(2,2,getWidth()-5,getHeight()-5,radius-2,radius-2);
                g2.setColor(GOLD);
                g2.fillRoundRect(55,0,getWidth()-110,3,3,3);
                g2.setColor(BORDER);
                g2.drawRoundRect(1,1,getWidth()-3,getHeight()-3,radius,radius);
            } finally { g2.dispose(); }
            super.paintComponent(g);
        }
        protected void paintChildren(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            try {
                g2.clip(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),radius,radius));
                super.paintChildren(g2);
            } finally { g2.dispose(); }
        }
    }

    private static class KingIcon extends JComponent {
        KingIcon(){
            setPreferredSize(new Dimension(68,68));
            setMaximumSize(new Dimension(68,68));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int w=getWidth(), h=getHeight(), cx=w/2;
                g2.setColor(new Color(218,175,70,30));
                g2.fillOval(2,2,w-4,h-4);
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(2.4f));
                g2.drawOval(6,6,w-12,h-12);
                g2.setColor(GOLD_LIGHT);
                g2.fillRect(cx-2,13,4,12);
                g2.fillRect(cx-7,17,14,4);
                Polygon p=new Polygon();
                p.addPoint(cx-11,27); p.addPoint(cx+11,27);
                p.addPoint(cx+7,39); p.addPoint(cx+13,44);
                p.addPoint(cx-13,44); p.addPoint(cx-7,39);
                g2.fillPolygon(p);
                g2.fillRoundRect(cx-15,45,30,5,3,3);
            } finally { g2.dispose(); }
        }
    }

    private static class BaseButton extends JButton {
        private final Color base;
        BaseButton(String s,Color c) {
            super(s); base=c;
            setFont(new Font("Segoe UI",Font.BOLD,13));
            setFocusPainted(false); setBorderPainted(false);
            setContentAreaFilled(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill=getModel().isPressed()?base.darker():
                        getModel().isRollover()?base.brighter():base;
                g2.setColor(fill);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),11,11);
            } finally { g2.dispose(); }
            super.paintComponent(g);
        }
    }
    private static class GoldButton extends BaseButton {
        GoldButton(String s){super(s,GOLD);setForeground(new Color(15,17,20));}
    }
    private static class DarkButton extends BaseButton {
        DarkButton(String s){super(s,CARD2);setForeground(GOLD_LIGHT);}
    }
}

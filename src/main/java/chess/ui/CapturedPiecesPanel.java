package chess.ui;

import chess.board.GameState;
import chess.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CapturedPiecesPanel extends JPanel {

    private final GameState gameState;

    private static final int PIECE_SIZE = 30;
    private static final int PADDING = 10;

    public CapturedPiecesPanel(GameState gameState) {

        this.gameState = gameState;

        setBackground(new Color(40,30,20));

        setPreferredSize(new Dimension(300,420));
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int panelWidth = getWidth();

        // Title

        g2.setColor(new Color(200,200,200));
        g2.setFont(new Font("Arial",Font.BOLD,13));

        drawCentered(g2,"CAPTURED PIECES",panelWidth,30);

        g2.setColor(new Color(80,80,80));
        g2.drawLine(PADDING,40,panelWidth-PADDING,40);

        // Black

        g2.setColor(new Color(180,180,180));
        g2.setFont(new Font("Arial",Font.BOLD,12));

        g2.drawString(
                "BLACK: " + gameState.getBlackPlayer(),
                PADDING,
                60
        );

        drawCapturedPieces(
                g2,
                gameState.getCapturedBlack(),
                70,
                200
        );

        g2.setColor(new Color(80,80,80));
        g2.drawLine(PADDING,190,panelWidth-PADDING,190);

        // White

        g2.setColor(new Color(210,210,210));

        g2.drawString(
                "WHITE: " + gameState.getWhitePlayer(),
                PADDING,
                210
        );

        drawCapturedPieces(
                g2,
                gameState.getCapturedWhite(),
                220,
                390
        );

        // Turn

        // Turn

        String turn =
                gameState.isWhiteTurn()
                        ? "WHITE'S TURN"
                        : "BLACK'S TURN";

        g2.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        g2.setColor(
                gameState.isWhiteTurn()
                        ? Color.WHITE
                        : Color.LIGHT_GRAY
        );

        drawCentered(
                g2,
                turn,
                panelWidth,
                getHeight() - 20
        );


    }

    private void drawCapturedPieces(Graphics2D g2,
                                    List<Piece> pieces,
                                    int startY,
                                    int maxY) {

        int x = PADDING;
        int y = startY;

        int cols =
                (getWidth()-PADDING*2)/(PIECE_SIZE+4);

        int count=0;

        for(Piece p:pieces){

            g2.drawImage(
                    p.getImage(),
                    x,
                    y,
                    PIECE_SIZE,
                    PIECE_SIZE,
                    this
            );

            count++;

            if(count%cols==0){

                x=PADDING;

                y+=PIECE_SIZE+4;

                if(y>maxY-PIECE_SIZE)
                    break;

            }else{

                x+=PIECE_SIZE+4;
            }
        }
    }

    private void drawCentered(Graphics2D g2,
                              String text,
                              int width,
                              int y){

        FontMetrics fm=g2.getFontMetrics();

        int x=(width-fm.stringWidth(text))/2;

        g2.drawString(text,x,y);
    }
}
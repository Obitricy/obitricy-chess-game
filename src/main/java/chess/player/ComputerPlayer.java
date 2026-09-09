package chess.player;

import chess.ai.ChessAI;
import chess.board.GameState;
import chess.game.Move;
import chess.ai.MinimaxAI;
import chess.ai.ComputerDifficulty;
import java.io.Serializable;

public class ComputerPlayer extends Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private ComputerDifficulty difficulty;

    private transient ChessAI ai;


    public ComputerDifficulty getDifficulty() {
        return difficulty;
    }

    private void createAI() {

        if (difficulty == null) {
            throw new IllegalStateException("Computer difficulty is null");
        }

        ai = new MinimaxAI(difficulty.getDepth());
    }

    public void setDifficulty(ComputerDifficulty difficulty) {
        this.difficulty = difficulty;
        createAI();
    }

    public ComputerPlayer(Color color) {
        this(color, ComputerDifficulty.MEDIUM);
    }

    public ComputerPlayer(Color color, ComputerDifficulty difficulty) {
        super("Computer", color);
        this.difficulty = difficulty;
        createAI();
    }



    public Move chooseMove(GameState gameState) {
        return ai.chooseMove(gameState);
    }

}
package chess.ai;

import chess.board.GameState;
import chess.game.Move;

public interface ChessAI {

    /**
     * Returns the move chosen by the AI.
     */
    Move chooseMove(GameState gameState);

}
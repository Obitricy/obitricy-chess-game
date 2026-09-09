package chess.ai;

import chess.board.GameState;
import chess.game.Move;

import java.util.List;
import java.util.Random;

public class RandomAI implements ChessAI {

    private final Random random = new Random();

    @Override
    public Move chooseMove(GameState gameState) {

        List<Move> legalMoves =
                gameState.getAllLegalMoves(
                        gameState.isWhiteTurn()
                );

        if (legalMoves.isEmpty()) {
            return null;
        }

        return legalMoves.get(
                random.nextInt(legalMoves.size())
        );
    }
}
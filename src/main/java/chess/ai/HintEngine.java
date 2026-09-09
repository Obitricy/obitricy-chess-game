package chess.ai;

import chess.board.GameState;
import chess.game.Move;
import chess.player.ComputerPlayer;

public final class HintEngine {

    private HintEngine() {
    }

    public static Move getBestMove(GameState gameState) {

        ComputerDifficulty difficulty = ComputerDifficulty.MEDIUM;

        ComputerPlayer coach =
                new ComputerPlayer(
                        gameState.isWhiteTurn()
                                ? chess.player.Player.Color.WHITE
                                : chess.player.Player.Color.BLACK,
                        difficulty
                );

        return coach.chooseMove(gameState);
    }
}
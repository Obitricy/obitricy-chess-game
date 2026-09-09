package chess.puzzle;

import java.util.List;

public class PuzzleLibrary {

    public static PuzzleManager createDefaultLibrary() {

        return PuzzleImporter.load(
                "/puzzles/lichess_puzzles.csv"
        );

    }

}
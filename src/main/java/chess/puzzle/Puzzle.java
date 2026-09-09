package chess.puzzle;

import java.io.Serializable;
import java.util.List;

public class Puzzle implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final int rating;
    private final String theme;

    private final String opening;


    private final String fen;

    private final List<PuzzleMove> solution;

    public Puzzle(String id,
                  int rating,
                  String theme,
                  String opening,
                  String fen,
                  List<PuzzleMove> solution) {

        this.id = id;
        this.rating = rating;
        this.theme = theme;
        this.opening = opening;
        this.fen = fen;
        this.solution = solution;
    }

    public String getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getTheme() {
        return theme;
    }


    public String getFen() {
        return fen;
    }

    public List<PuzzleMove> getSolution() {
        return solution;
    }
}
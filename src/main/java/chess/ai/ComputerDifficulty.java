package chess.ai;

public enum ComputerDifficulty {

    BEGINNER(1),
    EASY(2),
    MEDIUM(3),
    HARD(5),
    EXPERT(7),
    MASTER(9);

    private final int depth;

    ComputerDifficulty(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return name();
    }
}
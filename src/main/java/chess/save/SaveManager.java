package chess.save;

import chess.board.GameState;

import java.io.*;

public class SaveManager {

    private static final String FILE =
            "savegame.dat";

    public static void save(GameState game) {

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(FILE))) {

            out.writeObject(game);

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    public static GameState load() {

        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(FILE))) {

            GameState game = (GameState) in.readObject();

            game.initializePuzzleManager();

            return game;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveExists() {

        return new File(FILE).exists();

    }

    public static void deleteSave() {

        new File(FILE).delete();

    }

}
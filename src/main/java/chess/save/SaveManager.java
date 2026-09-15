package chess.save;

import chess.board.GameState;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages persistent chess game saves for Obitricy Chess Game.
 *
 * Save data is stored per Windows user in:
 *
 * %LOCALAPPDATA%\Obitricy\Chess Game\savegame.dat
 *
 * This prevents different Windows users from sharing the same
 * save-game file and avoids storing application data inside the
 * installation/project directory.
 */
public class SaveManager {

    private static final String APP_FOLDER =
            "Obitricy" + File.separator + "Chess Game";

    private static final String FILE_NAME =
            "savegame.dat";

    private static final File FILE =
            getDataFile();

    // ============================================================
    // DATA DIRECTORY
    // ============================================================

    private static File getDataDirectory() {

        String localAppData =
                System.getenv("LOCALAPPDATA");

        Path dataDirectory;

        if (localAppData != null &&
                !localAppData.trim().isEmpty()) {

            dataDirectory = Paths.get(
                    localAppData,
                    "Obitricy",
                    "Chess Game"
            );

        } else {

            /*
             * Fallback for systems where LOCALAPPDATA is not
             * available.
             */
            dataDirectory = Paths.get(
                    System.getProperty("user.home"),
                    ".obitricy",
                    "Chess Game"
            );
        }

        try {

            Files.createDirectories(dataDirectory);

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to create Obitricy application data directory: "
                            + dataDirectory,
                    e
            );
        }

        return dataDirectory.toFile();
    }

    // ============================================================
    // DATA FILE
    // ============================================================

    private static File getDataFile() {

        return new File(
                getDataDirectory(),
                FILE_NAME
        );
    }

    // ============================================================
    // SAVE
    // ============================================================

    public static void save(GameState game) {

        if (game == null) {
            return;
        }

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(FILE))) {

            out.writeObject(game);
            out.flush();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // ============================================================
    // LOAD
    // ============================================================

    public static GameState load() {

        if (!FILE.exists()) {
            return null;
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(FILE))) {

            GameState game =
                    (GameState) in.readObject();

            if (game != null) {
                game.initializePuzzleManager();
            }

            return game;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    // ============================================================
    // CHECK SAVE EXISTS
    // ============================================================

    public static boolean saveExists() {

        return FILE.exists();
    }

    // ============================================================
    // DELETE SAVE
    // ============================================================

    public static void deleteSave() {

        if (FILE.exists()) {
            FILE.delete();
        }
    }
}
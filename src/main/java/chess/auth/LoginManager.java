package chess.auth;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LoginManager {

    private static final File FILE = new File("users.dat");

    private static Map<String, String> users = new HashMap<>();

    static {
        load();
    }

    public static boolean login(String user, String pass) {

        if (user == null || pass == null) {
            return false;
        }

        user = user.trim();

        String storedPassword = users.get(user);

        return storedPassword != null && storedPassword.equals(pass);
    }

    public static boolean register(String user, String pass) {

        if (user == null || pass == null) {
            return false;
        }

        user = user.trim();

        if (user.isEmpty() || pass.isEmpty()) {
            return false;
        }

        if (users.containsKey(user)) {
            return false;
        }

        users.put(user, pass);
        save();

        return true;
    }

    private static void save() {

        try (ObjectOutputStream out =
                     new ObjectOutputStream(new FileOutputStream(FILE))) {

            out.writeObject(users);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void load() {

        if (!FILE.exists()) {
            return;
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(FILE))) {

            users = (HashMap<String, String>) in.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
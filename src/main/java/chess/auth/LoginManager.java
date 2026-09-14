package chess.auth;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication manager for Obitricy Chess Game.
 *
 * Handles:
 * - User registration
 * - User login
 * - Secure password hashing
 * - Backward compatibility with existing plain-text passwords
 * - Persistent user storage
 *
 * New passwords are stored using PBKDF2WithHmacSHA256.
 */
public class LoginManager {

    private static final File FILE = new File("users.dat");

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 256;
    private static final int ITERATIONS = 120_000;

    private static final int MIN_PASSWORD_LENGTH = 8;

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private static Map<String, String> users = new HashMap<>();

    static {
        load();
    }

    // ============================================================
    // LOGIN
    // ============================================================

    public static boolean login(String user, String pass) {

        if (user == null || pass == null) {
            return false;
        }

        user = user.trim();

        if (user.isEmpty() || pass.isEmpty()) {
            return false;
        }

        String storedPassword = users.get(user);

        if (storedPassword == null) {
            return false;
        }

        /*
         * New secure password format:
         *
         * HASHED:<salt>:<hash>
         */
        if (storedPassword.startsWith("HASHED:")) {

            return verifyPassword(
                    pass,
                    storedPassword
            );
        }

        /*
         * Backward compatibility:
         *
         * Older Obitricy accounts were stored as plain text.
         *
         * If the password matches, immediately upgrade
         * the account to the secure hashed format.
         */
        if (storedPassword.equals(pass)) {

            String securePassword =
                    hashPassword(pass);

            if (securePassword != null) {

                users.put(user, securePassword);
                save();
            }

            return true;
        }

        return false;
    }

    // ============================================================
    // REGISTER
    // ============================================================

    public static boolean register(String user, String pass) {

        if (user == null || pass == null) {
            return false;
        }

        user = user.trim();

        // --------------------------------------------------------
        // USERNAME VALIDATION
        // --------------------------------------------------------

        if (!isValidUsername(user)) {
            return false;
        }

        // --------------------------------------------------------
        // PASSWORD VALIDATION
        // --------------------------------------------------------

        if (pass.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }

        // --------------------------------------------------------
        // DUPLICATE USERNAME
        // --------------------------------------------------------

        if (users.containsKey(user)) {
            return false;
        }

        // --------------------------------------------------------
        // HASH PASSWORD
        // --------------------------------------------------------

        String hashedPassword =
                hashPassword(pass);

        if (hashedPassword == null) {
            return false;
        }

        // --------------------------------------------------------
        // ADD USER
        // --------------------------------------------------------

        users.put(user, hashedPassword);

        // --------------------------------------------------------
        // SAVE TO DISK
        // --------------------------------------------------------

        if (!save()) {

            /*
             * Saving failed.
             *
             * Remove the user from memory so the application
             * does not report a successful registration when
             * the account wasn't actually persisted.
             */
            users.remove(user);

            return false;
        }

        return true;
    }

    // ============================================================
    // USERNAME VALIDATION
    // ============================================================

    private static boolean isValidUsername(String user) {

        if (user == null || user.isEmpty()) {
            return false;
        }

        /*
         * Username rules:
         *
         * 3 - 20 characters
         * Letters
         * Numbers
         * Underscore
         *
         * No spaces or special characters.
         */
        return user.matches("[A-Za-z0-9_]{3,20}");
    }

    // ============================================================
    // PASSWORD HASHING
    // ============================================================

    private static String hashPassword(String password) {

        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                HASH_LENGTH
        );

        try {

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] hash = factory
                    .generateSecret(spec)
                    .getEncoded();

            return "HASHED:"
                    + Base64.getEncoder().encodeToString(salt)
                    + ":"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            spec.clearPassword();
        }
    }

    // ============================================================
    // PASSWORD VERIFICATION
    // ============================================================

    private static boolean verifyPassword(
            String password,
            String storedPassword) {

        try {

            String[] parts =
                    storedPassword.split(":");

            if (parts.length != 3) {
                return false;
            }

            byte[] salt =
                    Base64.getDecoder().decode(parts[1]);

            byte[] expectedHash =
                    Base64.getDecoder().decode(parts[2]);

            PBEKeySpec spec =
                    new PBEKeySpec(
                            password.toCharArray(),
                            salt,
                            ITERATIONS,
                            HASH_LENGTH
                    );

            try {

                SecretKeyFactory factory =
                        SecretKeyFactory.getInstance(
                                "PBKDF2WithHmacSHA256"
                        );

                byte[] actualHash =
                        factory
                                .generateSecret(spec)
                                .getEncoded();

                return constantTimeEquals(
                        expectedHash,
                        actualHash
                );

            } finally {

                spec.clearPassword();
            }

        } catch (
                IllegalArgumentException |
                NoSuchAlgorithmException |
                InvalidKeySpecException e) {

            e.printStackTrace();

            return false;
        }
    }

    // ============================================================
    // CONSTANT-TIME COMPARISON
    // ============================================================

    private static boolean constantTimeEquals(
            byte[] a,
            byte[] b) {

        if (a == null || b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        int result = 0;

        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }

        return result == 0;
    }

    // ============================================================
    // SAVE USERS
    // ============================================================

    private static boolean save() {

        try (
                ObjectOutputStream out =
                        new ObjectOutputStream(
                                new FileOutputStream(FILE)
                        )
        ) {

            out.writeObject(users);
            out.flush();

            return true;

        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }
    }

    // ============================================================
    // LOAD USERS
    // ============================================================

    @SuppressWarnings("unchecked")
    private static void load() {

        if (!FILE.exists()) {
            return;
        }

        try (
                ObjectInputStream in =
                        new ObjectInputStream(
                                new FileInputStream(FILE)
                        )
        ) {

            Object data = in.readObject();

            if (data instanceof HashMap<?, ?>) {

                users =
                        (HashMap<String, String>) data;
            }

        } catch (
                IOException |
                ClassNotFoundException |
                ClassCastException e) {

            e.printStackTrace();

            /*
             * Do not destroy the existing file automatically.
             * If users.dat is corrupted, the problem should be
             * investigated rather than silently overwritten.
             */
        }
    }

    // ============================================================
    // PUBLIC INFORMATION
    // ============================================================

    /**
     * Returns the minimum password length required for
     * new accounts.
     */
    public static int getMinimumPasswordLength() {
        return MIN_PASSWORD_LENGTH;
    }
}
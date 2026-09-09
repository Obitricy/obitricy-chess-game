package chess.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Central audio manager for Obitricy Chess Game.
 *
 * Supports:
 * - Chess move
 * - Capture
 * - Undo
 * - Redo
 * - Check
 * - Checkmate
 * - Stalemate
 * - Puzzle sounds
 * - Background music
 *
 * Audio resources are loaded from:
 *
 * src/main/resources/audio/
 */
public final class SoundManager {

    private SoundManager() {
    }

    // =========================================================
    // SETTINGS
    // =========================================================

    private static volatile boolean soundEnabled = true;

    private static volatile boolean musicEnabled = true;

    private static Clip backgroundMusic;

    // =========================================================
    // AUDIO RESOURCES
    // =========================================================

    private static final String MOVE =
            "/audio/move.wav";

    private static final String CAPTURE =
            "/audio/capture.wav";

    private static final String UNDO =
            "/audio/undo.wav";

    private static final String REDO =
            "/audio/redo.wav";

    private static final String CHECK =
            "/audio/check.wav";

    private static final String CHECKMATE =
            "/audio/checkmate.wav";

    private static final String STALEMATE =
            "/audio/stalemate.wav";

    private static final String PUZZLE_CHECK =
            "/audio/puzzle_check.wav";

    private static final String PUZZLE_CORRECT =
            "/audio/puzzle_correct.wav";

    private static final String PUZZLE_WRONG =
            "/audio/puzzle_wrong.wav";

    private static final String PUZZLE_COMPLETE =
            "/audio/puzzle_complete.wav";

    private static final String PUZZLE_NEXT =
            "/audio/puzzle_next.wav";

    private static final String PUZZLE_OPPONENT =
            "/audio/puzzle_opponent.wav";

    /*
     * IMPORTANT:
     * Your actual file is chess_background.wav
     */
    private static final String BACKGROUND =
            "/audio/chess_background.wav";


    // =========================================================
    // SOUND ENABLE / DISABLE
    // =========================================================

    public static synchronized void setSoundEnabled(
            boolean enabled) {

        soundEnabled = enabled;

        if (!enabled) {

            stopBackgroundMusic();

        } else {

            /*
             * When sound is switched back ON,
             * restart the background music if music
             * is also enabled.
             */
            if (musicEnabled) {

                startBackgroundMusic();
            }
        }
    }


    /**
     * Compatibility method used by SettingsPanel.
     */
    public static void setEnabled(
            boolean enabled) {

        setSoundEnabled(enabled);
    }


    public static boolean isSoundEnabled() {

        return soundEnabled;
    }


    // =========================================================
    // MUSIC ENABLE / DISABLE
    // =========================================================

    public static synchronized void setMusicEnabled(
            boolean enabled) {

        musicEnabled = enabled;

        if (enabled && soundEnabled) {

            startBackgroundMusic();

        } else {

            stopBackgroundMusic();
        }
    }


    public static boolean isMusicEnabled() {

        return musicEnabled;
    }


    // =========================================================
    // NORMAL CHESS SOUNDS
    // =========================================================

    public static void playMove() {

        play(MOVE);
    }


    public static void playCapture() {

        play(CAPTURE);
    }


    public static void playUndo() {

        play(UNDO);
    }


    public static void playRedo() {

        play(REDO);
    }


    // =========================================================
    // GAME STATUS SOUNDS
    // =========================================================

    public static void playCheck() {

        play(CHECK);
    }


    public static void playCheckmate() {

        play(CHECKMATE);
    }


    public static void playStalemate() {

        play(STALEMATE);
    }


    // =========================================================
    // PUZZLE SOUNDS
    // =========================================================

    public static void playPuzzleCheck() {

        play(PUZZLE_CHECK);
    }


    public static void playPuzzleCorrect() {

        play(PUZZLE_CORRECT);
    }


    public static void playPuzzleWrong() {

        play(PUZZLE_WRONG);
    }


    public static void playPuzzleComplete() {

        play(PUZZLE_COMPLETE);
    }


    public static void playPuzzleNext() {

        play(PUZZLE_NEXT);
    }


    public static void playPuzzleOpponent() {

        play(PUZZLE_OPPONENT);
    }


    // =========================================================
    // BACKGROUND MUSIC
    // =========================================================

    public static synchronized void startBackgroundMusic() {

        if (!soundEnabled || !musicEnabled) {
            return;
        }

        // Already playing
        if (backgroundMusic != null && backgroundMusic.isOpen()) {
            if (!backgroundMusic.isRunning()) {
                backgroundMusic.setFramePosition(0);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                backgroundMusic.start();
            }
            return;
        }

        try {
            InputStream input =
                    SoundManager.class.getResourceAsStream(BACKGROUND);

            if (input == null) {
                System.err.println(
                        "Background music NOT FOUND: " + BACKGROUND
                );
                return;
            }

            BufferedInputStream buffered =
                    new BufferedInputStream(input);

            AudioInputStream originalStream =
                    AudioSystem.getAudioInputStream(buffered);

            AudioFormat sourceFormat =
                    originalStream.getFormat();

            System.out.println(
                    "Background source format: " + sourceFormat
            );

            AudioFormat decodedFormat =
                    new AudioFormat(
                            AudioFormat.Encoding.PCM_SIGNED,
                            44100.0f,
                            16,
                            2,
                            4,
                            44100.0f,
                            false
                    );

            AudioInputStream decodedStream;

            if (AudioSystem.isConversionSupported(
                    decodedFormat,
                    sourceFormat)) {

                decodedStream =
                        AudioSystem.getAudioInputStream(
                                decodedFormat,
                                originalStream
                        );

            } else if (sourceFormat.getEncoding()
                    == AudioFormat.Encoding.PCM_SIGNED
                    && sourceFormat.getSampleSizeInBits() == 16) {

                decodedStream = originalStream;

            } else {

                System.err.println(
                        "Background audio format cannot be converted: "
                                + sourceFormat
                );

                originalStream.close();
                return;
            }

            Clip clip = AudioSystem.getClip();

            clip.open(decodedStream);

            backgroundMusic = clip;

            clip.setFramePosition(0);

            clip.loop(Clip.LOOP_CONTINUOUSLY);

            clip.start();

            System.out.println(
                    "========================================"
            );
            System.out.println(
                    "BACKGROUND MUSIC STARTED SUCCESSFULLY"
            );
            System.out.println(
                    "Resource: " + BACKGROUND
            );
            System.out.println(
                    "Format: " + decodedFormat
            );
            System.out.println(
                    "========================================"
            );

        } catch (UnsupportedAudioFileException ex) {

            System.err.println(
                    "BACKGROUND AUDIO FILE NOT SUPPORTED: "
                            + ex.getMessage()
            );

        } catch (LineUnavailableException ex) {

            System.err.println(
                    "BACKGROUND AUDIO LINE UNAVAILABLE: "
                            + ex.getMessage()
            );

        } catch (Exception ex) {

            System.err.println(
                    "BACKGROUND MUSIC ERROR: "
                            + ex.getMessage()
            );

            ex.printStackTrace();

            backgroundMusic = null;
        }
    }

    // =========================================================
    // STOP BACKGROUND MUSIC
    // =========================================================

    public static synchronized void stopBackgroundMusic() {

        if (backgroundMusic == null) {

            return;
        }


        try {

            backgroundMusic.stop();

            backgroundMusic.flush();

            backgroundMusic.close();

        } catch (Exception ignored) {

        } finally {

            backgroundMusic = null;
        }


        System.out.println(
                "Background music stopped."
        );
    }


    // =========================================================
    // PLAY SHORT SOUND
    // =========================================================

    private static void play(String resource) {

        if (!soundEnabled) {
            System.out.println(
                    "Sound disabled. Skipping: " + resource
            );
            return;
        }

        try {
            InputStream input =
                    SoundManager.class.getResourceAsStream(resource);

            if (input == null) {
                System.err.println(
                        "Sound not found: " + resource
                );
                return;
            }

            BufferedInputStream buffered =
                    new BufferedInputStream(input);

            AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(buffered);

            Clip clip = AudioSystem.getClip();

            clip.open(audioStream);

            clip.setFramePosition(0);

            clip.addLineListener(event -> {

                if (event.getType() == LineEvent.Type.STOP) {

                    try {
                        clip.close();
                    } catch (Exception ignored) {
                    }

                    try {
                        audioStream.close();
                    } catch (Exception ignored) {
                    }

                    try {
                        buffered.close();
                    } catch (Exception ignored) {
                    }

                    try {
                        input.close();
                    } catch (Exception ignored) {
                    }
                }
            });

            clip.start();

            System.out.println(
                    "Playing sound: " + resource
            );

        } catch (UnsupportedAudioFileException ex) {

            System.err.println(
                    "Unsupported audio file: "
                            + resource
                            + " - "
                            + ex.getMessage()
            );

        } catch (LineUnavailableException ex) {

            System.err.println(
                    "Audio line unavailable: "
                            + resource
                            + " - "
                            + ex.getMessage()
            );

        } catch (Exception ex) {

            System.err.println(
                    "Unable to play sound "
                            + resource
                            + ": "
                            + ex.getMessage()
            );

            ex.printStackTrace();
        }
    }
}
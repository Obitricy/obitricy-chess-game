package chess.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public final class BackgroundMusic {

    private static Clip clip;

    private BackgroundMusic() {
    }

    // =========================================================
    // START MUSIC
    // =========================================================

    public static void play() {

        stop();

        try {

            InputStream input =
                    BackgroundMusic.class
                            .getResourceAsStream(
                                    "/audio/chess_background.wav"
                            );

            if (input == null) {
                System.err.println(
                        "Background music not found: " +
                                "/audio/chess_background.wav"
                );
                return;
            }

            AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(
                            new BufferedInputStream(input)
                    );

            clip =
                    AudioSystem.getClip();

            clip.open(audioStream);

            // Loop continuously
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            // Start playback
            clip.start();

        } catch (Exception e) {

            System.err.println(
                    "Could not play background music."
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // STOP
    // =========================================================

    public static void stop() {

        if (clip != null) {

            if (clip.isRunning()) {
                clip.stop();
            }

            clip.close();
            clip = null;
        }
    }

    // =========================================================
    // PAUSE
    // =========================================================

    public static void pause() {

        if (clip != null &&
                clip.isRunning()) {

            clip.stop();
        }
    }

    // =========================================================
    // RESUME
    // =========================================================

    public static void resume() {

        if (clip != null &&
                !clip.isRunning()) {

            clip.start();
        }
    }

    // =========================================================
    // VOLUME
    // =========================================================

    public static void setVolume(
            float volume) {

        if (clip == null) {
            return;
        }

        try {

            FloatControl control =
                    (FloatControl)
                            clip.getControl(
                                    FloatControl.Type.MASTER_GAIN
                            );

            // volume: 0.0 = silent
            //         1.0 = maximum

            volume =
                    Math.max(
                            0.0f,
                            Math.min(
                                    1.0f,
                                    volume
                            )
                    );

            float min =
                    control.getMinimum();

            float max =
                    control.getMaximum();

            float gain;

            if (volume <= 0.0f) {

                gain = min;

            } else {

                gain =
                        (float)
                                (20.0 *
                                        Math.log10(
                                                volume
                                        ));

                gain =
                        Math.max(
                                min,
                                Math.min(
                                        max,
                                        gain
                                )
                        );
            }

            control.setValue(gain);

        } catch (IllegalArgumentException ignored) {

            // Device does not support volume control.
        }
    }
}
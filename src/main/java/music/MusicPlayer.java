import javax.sound.sampled.*;
import java.io.File;

public class MusicPlayer {

    private static final String[] TRACKS = {
        "../../resources/tracks/track0.wav",
        "../../resources/tracks/track1.wav",
        "../../resources/tracks/track2.wav",
        "../../resources/tracks/track3.wav",
        "../../resources/tracks/track4.wav"  // loops forever, ironic is it not
    };

    public static void start() {
        new Thread(() -> {
            try {
                // Play track0 through track3 once each
                for (int i = 0; i < TRACKS.length - 1; i++) {
                    playAndWait(TRACKS[i], false);
                }
                // Loop track4 forever
                playAndWait(TRACKS[TRACKS.length - 1], true);

            } catch (Exception e) {
                System.err.println("[MusicPlayer] Error: " + e.getMessage());
            }
        }, "music-thread").start();
    }

    private static void playAndWait(String path, boolean loop) throws Exception {
        File file = new File(path);
        AudioInputStream stream = AudioSystem.getAudioInputStream(file);

        Clip clip = AudioSystem.getClip();
        clip.open(stream);

        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            Thread.sleep(Long.MAX_VALUE);
        } else {
            clip.start();
            Thread.sleep(clip.getMicrosecondLength() / 1000);
            clip.close();
        }
    }
}

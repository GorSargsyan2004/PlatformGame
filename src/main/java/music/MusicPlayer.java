package music;

import javax.sound.sampled.*;
import java.net.URL;

public class MusicPlayer {

    private static final String[] TRACKS = {
        "/tracks/track0.wav",
        "/tracks/track1.wav",
        "/tracks/track2.wav",
        "/tracks/track3.wav",
        "/tracks/track4.wav"  // loops forever, ironic is it not
    };

    private static Thread musicThread;
    private static Clip currentClip;

    public static void start() {
        if (musicThread != null && musicThread.isAlive()) {
            return;
        }

        musicThread = new Thread(() -> {
            try {
                // Play track0 through track3 once each
                for (int i = 0; i < TRACKS.length - 1; i++) {
                    playAndWait(TRACKS[i], false);
                }
                // Loop track4 forever
                playAndWait(TRACKS[TRACKS.length - 1], true);

            } catch (InterruptedException e) {
                System.out.println("[MusicPlayer] Music thread interrupted.");
            } catch (Exception e) {
                System.err.println("[MusicPlayer] Error: " + e.getMessage());
                e.printStackTrace();
            }
        }, "music-thread");
        musicThread.start();
    }

    public static void stop() {
        if (musicThread != null) {
            musicThread.interrupt();
        }
        if (currentClip != null && currentClip.isOpen()) {
            currentClip.stop();
            currentClip.close();
        }
    }

    private static void playAndWait(String path, boolean loop) throws Exception {
        URL url = MusicPlayer.class.getResource(path);
        if (url == null) {
            throw new RuntimeException("Could not find music file: " + path);
        }

        AudioInputStream stream = AudioSystem.getAudioInputStream(url);
        currentClip = AudioSystem.getClip();
        currentClip.open(stream);

        if (loop) {
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);
            // Wait indefinitely for the looping clip
            Thread.sleep(Long.MAX_VALUE);
        } else {
            currentClip.start();
            // Wait for the clip to finish
            Thread.sleep(currentClip.getMicrosecondLength() / 1000);
            currentClip.close();
        }
    }
}

package music;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * The MusicPlayer class handles the background music for the game.
 * It plays a sequence of tracks and loops the final track continuously.
 * It also supports a specific menu track that can be paused and resumed.
 */
public class MusicPlayer {

    private static final String[] TRACKS = {
        "/tracks/track0.wav",
        "/tracks/track1.wav",
        "/tracks/track2.wav",
        "/tracks/track3.wav",
        "/tracks/track4.wav"
    };

    private static final String MENU_TRACK = "/tracks/menu_track.wav";

    private static Thread levelMusicThread;
    private static Clip levelClip;
    private static Clip menuClip;

    /**
     * Starts the level music player in a new thread if it's not already running.
     * It plays the first four tracks once and then loops the final track.
     */
    public static void startLevelMusic() {
        if (levelMusicThread != null && levelMusicThread.isAlive()) {
            return;
        }

        levelMusicThread = new Thread(() -> {
            try {
                // Play track0 through track3 once each
                for (int i = 0; i < TRACKS.length - 1; i++) {
                    playLevelTrackAndWait(TRACKS[i], false);
                }
                // Loop track4 forever
                playLevelTrackAndWait(TRACKS[TRACKS.length - 1], true);

            } catch (InterruptedException e) {
                System.out.println("[MusicPlayer] Level music thread interrupted.");
            } catch (Exception e) {
                System.err.println("[MusicPlayer] Error in level music: " + e.getMessage());
                e.printStackTrace();
            }
        }, "level-music-thread");
        levelMusicThread.start();
    }

    /**
     * Stops the level music player and closes the current audio clip.
     */
    public static void stopLevelMusic() {
        if (levelMusicThread != null) {
            levelMusicThread.interrupt();
        }
        if (levelClip != null && levelClip.isOpen()) {
            levelClip.stop();
            levelClip.close();
        }
    }

    /**
     * Plays the menu track and loops it. If it was paused, it resumes from the last position.
     */
    public static void playMenuMusic() {
        try {
            if (menuClip == null) {
                URL url = MusicPlayer.class.getResource(MENU_TRACK);
                if (url == null) {
                    System.err.println("Could not find menu music file: " + MENU_TRACK);
                    return;
                }
                AudioInputStream stream = AudioSystem.getAudioInputStream(url);
                menuClip = AudioSystem.getClip();
                menuClip.open(stream);
                menuClip.loop(Clip.LOOP_CONTINUOUSLY);
            }

            if (!menuClip.isRunning()) {
                menuClip.start();
            }
        } catch (Exception e) {
            System.err.println("[MusicPlayer] Error playing menu music: " + e.getMessage());
        }
    }

    /**
     * Pauses the menu music if it is currently playing.
     */
    public static void pauseMenuMusic() {
        if (menuClip != null && menuClip.isRunning()) {
            menuClip.stop();
        }
    }

    /**
     * Plays a level audio file from the specified path and waits for it to finish or loops it.
     */
    private static void playLevelTrackAndWait(String path, boolean loop) throws Exception {
        URL url = MusicPlayer.class.getResource(path);
        if (url == null) {
            throw new RuntimeException("Could not find level music file: " + path);
        }

        AudioInputStream stream = AudioSystem.getAudioInputStream(url);
        levelClip = AudioSystem.getClip();
        levelClip.open(stream);

        if (loop) {
            levelClip.loop(Clip.LOOP_CONTINUOUSLY);
            Thread.sleep(Long.MAX_VALUE);
        } else {
            levelClip.start();
            Thread.sleep(levelClip.getMicrosecondLength() / 1000);
            levelClip.close();
        }
    }
}

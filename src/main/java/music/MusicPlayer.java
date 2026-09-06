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

    private static volatile boolean isLevelPaused = false;

    /**
     * Starts the level music player in a new thread if it's not already running.
     * It plays the first four tracks once and then loops the final track.
     */
    public static void startLevelMusic() {
        if (levelMusicThread != null && levelMusicThread.isAlive()) {
            isLevelPaused = false;
            return;
        }

        isLevelPaused = false;
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
            levelMusicThread = null;
        }
        if (levelClip != null && levelClip.isOpen()) {
            levelClip.stop();
            levelClip.close();
        }
    }

    /**
     * Pauses the level music.
     */
    public static void pauseLevelMusic() {
        isLevelPaused = true;
    }

    /**
     * Resumes the level music.
     */
    public static void resumeLevelMusic() {
        isLevelPaused = false;
    }

    /**
     * Plays the menu track and loops it. It always restarts from the beginning.
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
                
                if (menuClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) menuClip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(-9.5f); // Reduce volume by approx 3 times
                }
            }

            if (!menuClip.isRunning()) {
                menuClip.setMicrosecondPosition(0);
                menuClip.loop(Clip.LOOP_CONTINUOUSLY);
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

        if (path.endsWith("track1.wav")) {
            if (levelClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) levelClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-4.43f);
            }
        }

        if (loop) {
            levelClip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            levelClip.start();
        }

        while (true) {
            if (isLevelPaused) {
                if (levelClip.isRunning()) {
                    levelClip.stop();
                }
            } else {
                if (!levelClip.isRunning()) {
                    if (!loop && levelClip.getMicrosecondPosition() >= levelClip.getMicrosecondLength()) {
                        break;
                    }
                    if (levelClip.getMicrosecondPosition() < levelClip.getMicrosecondLength()) {
                        if (loop) {
                            levelClip.loop(Clip.LOOP_CONTINUOUSLY);
                        } else {
                            levelClip.start();
                        }
                    }
                }
            }
            Thread.sleep(50);
        }
        levelClip.close();
    }
}

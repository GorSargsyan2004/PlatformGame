package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static levels.LevelManager.TILESET_HEIGHT;
import static levels.LevelManager.TILESET_WIDTH;
import static main.Game.TILES_IN_HEIGHT;
import static main.Game.TILES_IN_WIDTH;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

/**
 * The LoadSave class provides static utility methods for loading images
 * and level data from the project's resources.
 */
public class LoadSave {
    public static final String LEVEL_ATLAS = "/Level/oak_woods_tileset.png";
    public static final String LEVEL_DATA = "/Level/map.png";
    public static final int BLANK_TILE_ID = 4;
    public static final String BACKGROUND_LAYER_1 = "/Level/background/background_layer_1.png";
    public static final String BACKGROUND_LAYER_2 = "/Level/background/background_layer_2.png";
    public static final String BACKGROUND_LAYER_3 = "/Level/background/background_layer_3.png";
    public static final String MENU_BUTTONS = "/Menu/Buttons/";
    public static final String MENU_BACKGROUND = "/Menu/menu_background.png";
    public static final String BACKGROUND_MENU = "/Menu/background_menu.png";
    public static final String BACKGROUND_LOGIN = "/Demo/background_login.jpeg";
    public static final String STATUS_BAR = "/GUI/health_power_bar.png";

    // Sounds
    public static final String SOUND_HOVER = "/tracks/player/ui_hover.wav";
    public static final String SOUND_CLICK = "/tracks/player/ui_click.wav";
    public static final String SOUND_ATTACK = "/tracks/player/attack.wav";
    public static final String SOUND_DASH_ATTACK = "/tracks/player/dash_attack.mp3";
    public static final String SOUND_HURT = "/tracks/player/hurt.mp3";
    public static final String SOUND_JUMP = "/tracks/player/jump.wav";
    public static final String SOUND_DASH = "/tracks/player/dash.mp3";
    public static final String SOUND_SLIDE = "/tracks/player/slide.mp3";
    public static final String SOUND_DEATH = "/tracks/player/death.wav";

    /**
     * Loads a BufferedImage from the specified resource path.
     *
     * @param path The resource path to the image file.
     * @return The loaded BufferedImage, or null if the resource was not found.
     * @throws RuntimeException If an error occurs during image reading.
     */
    public static BufferedImage getSave(String path) {
        InputStream is = LoadSave.class.getResourceAsStream(path);
        BufferedImage img = null;
        if (is == null) return img;

        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image at: " + path + e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    /**
     * Plays a sound from the specified resource path.
     * @param path The resource path to the sound file.
     */
    public static void playSound(String path) {
        if (path == null || path.trim().isEmpty()) return;
        System.out.println("Triggering sound: " + path);
        try {
            URL url = LoadSave.class.getResource(path);
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                AudioFormat baseFormat = audioIn.getFormat();
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false
                );
                AudioInputStream decodedAudioIn;
                try {
                    decodedAudioIn = AudioSystem.getAudioInputStream(decodedFormat, audioIn);
                } catch (IllegalArgumentException e) {
                    if (baseFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
                        byte[] bytes = audioIn.readAllBytes();
                        int byteDepth = baseFormat.getSampleSizeInBits() / 8;
                        if (byteDepth == 3 || byteDepth == 4) {
                            byte[] outBytes = new byte[(bytes.length / byteDepth) * 2];
                            int j = 0;
                            for (int i = 0; i < bytes.length; i += byteDepth) {
                                // Little Endian: take the 2 most significant bytes
                                outBytes[j++] = bytes[i + byteDepth - 2];
                                outBytes[j++] = bytes[i + byteDepth - 1];
                            }
                            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(outBytes);
                            decodedAudioIn = new AudioInputStream(bais, decodedFormat, outBytes.length / decodedFormat.getFrameSize());
                        } else {
                            throw e;
                        }
                    } else {
                        throw e;
                    }
                }
                
                Clip clip = AudioSystem.getClip();
                clip.open(decodedAudioIn);
                
                if (clip.isControlSupported(javax.sound.sampled.FloatControl.Type.MASTER_GAIN)) {
                    javax.sound.sampled.FloatControl gainControl = 
                        (javax.sound.sampled.FloatControl) clip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
                    // Reduce volume by half (-6.0f decibels roughly halves amplitude)
                    gainControl.setValue(-6.0f);
                }
                
                clip.addLineListener(event -> {
                    if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                clip.start();
            } else {
                System.err.println("Sound file not found: " + path);
            }
        } catch (Exception e) {
            System.err.println("Could not play sound: " + path);
            e.printStackTrace();
        }
    }

    /**
     * Reads the level data from the map image.
     * Each pixel's red channel corresponds to a tile ID in the tileset.
     *
     * @return A 2D array representing the tile indices of the level.
     */
    public static int[][] getLevelData() {
        int[][] lvlData = new int[TILES_IN_HEIGHT][TILES_IN_WIDTH];
        BufferedImage img = getSave(LEVEL_DATA);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {

                Color color = new Color(img.getRGB(x, y));

                int value = color.getRed();
                if (value >= TILESET_WIDTH * TILESET_HEIGHT) {
                    value = BLANK_TILE_ID;
                }

                lvlData[y][x] = value;
            }
        }
        return lvlData;
    }
}

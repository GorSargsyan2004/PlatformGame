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

public class LoadSave {
    public static final String LEVEL_ATLAS = "/Level/oak_woods_tileset.png";
    public static final String LEVEL_DATA = "/Level/map.png";
    public static final int BLANK_TILE_ID = 4;
    public static final String BACKGROUND_LAYER_1 = "/Level/background/background_layer_1.png";
    public static final String BACKGROUND_LAYER_2 = "/Level/background/background_layer_2.png";
    public static final String BACKGROUND_LAYER_3 = "/Level/background/background_layer_3.png";

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

package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class LoadSave {
    public static final String LEVEL_ATLAS = "/Level/oak_woods_tileset.png";

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
}

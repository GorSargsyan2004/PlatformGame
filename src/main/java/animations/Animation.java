package animations;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import main.Game;

public class Animation {
    private int aniTick, aniIndex, aniSpeed;
    private int width, height, quantity, x, y, dist;
    private BufferedImage[] images, flippedImages;
    private String path;

    private BufferedImage source;

    public Animation(String path, int x, int y, int height, int width, int quantity, int dist) {
        this.path = path;
        this.x = x;
        this.y = y;
        this. height = height;
        this.width = width;
        this.quantity = quantity;
        this.dist = dist;
        aniTick = 0;
        aniSpeed = Game.UPS_SET / (quantity+2);
        loadAnimation();
        loadFlippedAnimation();
    }

    public Animation(String folderPath, String pattern) {
        ArrayList<BufferedImage> tempImages = new ArrayList<>();
        int index = 1;

        if (!folderPath.endsWith("/")) {
            folderPath += "/";
        }

        while (true) {
            String currentPath = folderPath + pattern + index + ".png";
            InputStream is = getClass().getResourceAsStream(currentPath);

            if (is == null) break;

            try {
                BufferedImage img = ImageIO.read(is);
                tempImages.add(img);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read image at: " + currentPath, e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            index++;
        }

        this.quantity = tempImages.size();

        if (this.quantity > 0) {
            this.images = tempImages.toArray(new BufferedImage[0]);

            this.width = images[0].getWidth();
            this.height = images[0].getHeight();

            this.aniTick = 0;
            this.aniSpeed = Game.UPS_SET / (quantity + 2);

            loadFlippedAnimation();
        } else {
            System.err.println("Warning: No images found at " + folderPath + " with pattern " + pattern);
        }
    }

    private void loadAnimation() {
        importImage(path);
        assert source != null;
        source = source.getSubimage(x, y, source.getWidth()-2*x, height);

        images = new BufferedImage[quantity];
        for (int i = 0; i < quantity; i++) {
            images[i] = source.getSubimage(i*(width+dist), 0, width, height);
        }
    }

    private void loadFlippedAnimation() {
        flippedImages = new BufferedImage[quantity];
        for (int i = 0; i < quantity; i++) {
            flippedImages[i] = flipImage(images[i]);
        }
    }

    private BufferedImage flipImage(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();

        BufferedImage flipped = new BufferedImage(width, height, original.getType());

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                flipped.setRGB((width - 1) - x, y, original.getRGB(x, y));

        return flipped;
    }

    private void importImage(String path) {
        InputStream is = getClass().getResourceAsStream(path);

        try {
            source = ImageIO.read(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= quantity)
                aniIndex = 0;
        }
    }

    public BufferedImage getAnimationImage(Direction dir) {
        if (dir == Direction.RIGHT) return images[aniIndex];
        return flippedImages[aniIndex];
    }

    public boolean isAnimationCompleted() {
        if (aniIndex == quantity-1) return true;
        return false;
    }

    public void reset() {
        aniTick = 0;
        aniIndex = 0;
    }

    public void modifySpeed(int value) {
        this.aniSpeed = Game.UPS_SET / (quantity + 2 + value);
    }
}

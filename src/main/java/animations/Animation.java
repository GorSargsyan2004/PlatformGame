package animations;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;

import main.Game;
import utils.Direction;

import static utils.LoadSave.getSave;

/**
 * The Animation class manages sequences of images to create sprite animations.
 * It handles loading frames from sprite sheets or individual files, and provides
 * functionality for flipped animations (left/right) and playback control.
 */
public class Animation {
    /** Animation tick counter, current frame index, and speed threshold. */
    private int aniTick, aniIndex, aniSpeed;
    /** Dimensions and layout properties for the animation frames. */
    private int width, height, quantity, x, y, dist;
    /** Arrays storing original and horizontally flipped animation frames. */
    private BufferedImage[] images, flippedImages;
    /** Resource path to the sprite sheet or image folder. */
    private String path;
    /** Flag indicating if the animation has completed at least one full cycle. */
    private boolean completed = false;

    /** Source image used for extracting individual frames. */
    private BufferedImage source;

    /**
     * Constructs an Animation by extracting frames from a single sprite sheet.
     * @param path Resource path to the sprite sheet image.
     * @param x Starting X coordinate on the sprite sheet.
     * @param y Starting Y coordinate on the sprite sheet.
     * @param height Height of each frame.
     * @param width Width of each frame.
     * @param quantity Number of frames in the animation.
     * @param dist Distance (padding) between frames on the sprite sheet.
     */
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

    /**
     * Constructs an Animation from an existing array of images.
     * @param frames Array of BufferedImages to use as animation frames.
     */
    public Animation(BufferedImage[] frames) {
        this.path = null;
        this.x = 0;
        this.y = 0;
        this. height = frames[0].getHeight();
        this.width = frames[0].getWidth();
        this.aniTick = 0;
        this.quantity = frames.length;
        aniSpeed = Game.UPS_SET / (quantity+2);
        images = frames.clone();
        loadFlippedAnimation();
    }

    /**
     * Constructs an Animation by loading individual image files from a folder.
     * Files are expected to follow a numeric pattern (e.g., "walk_1.png", "walk_2.png").
     * @param folderPath Path to the directory containing the images.
     * @param pattern Prefix pattern of the image filenames.
     */
    public Animation(String folderPath, String pattern) {
        ArrayList<BufferedImage> tempImages = new ArrayList<>();
        int index = 1;

        if (!folderPath.endsWith("/")) {
            folderPath += "/";
        }

        while (true) {
            String currentPath = folderPath + pattern + index + ".png";
            BufferedImage img = getSave(currentPath);

            if (img == null) break;

            tempImages.add(img);

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
        source = source.getSubimage(x, y, source.getWidth()-x, height);

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

        source = getSave(path);
    }

    /**
     * @return The height of the animation frames in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return The width of the animation frames in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Updates the animation tick counter. If the counter exceeds the animation speed,
     * it advances to the next frame. Resets to the first frame after reaching the end.
     */
    public void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= quantity) {
                aniIndex = 0;
                completed = true;
            }
        }
    }

    /**
     * Gets the current frame of the animation based on the given direction.
     * @param dir The direction the sprite is facing (RIGHT or LEFT).
     * @return The BufferedImage for the current frame.
     */
    public BufferedImage getAnimationImage(Direction dir) {
        if (dir == Direction.RIGHT) return images[aniIndex];
        return flippedImages[aniIndex];
    }

    /**
     * Gets a specific frame of the animation based on the given index and direction.
     * @param index The index of the frame to retrieve.
     * @param dir The direction the sprite is facing (RIGHT or LEFT).
     * @return The BufferedImage for the specified frame.
     */
    public BufferedImage getAnimationImageAt(int index, Direction dir) {
        if (dir == Direction.RIGHT) return images[index];
        return flippedImages[index];
    }

    /**
     * Checks if the animation has finished at least one full cycle.
     * @return true if the animation has completed, false otherwise.
     */
    public boolean isAnimationCompleted() {
        return completed;
    }

    /**
     * Resets the animation to the first frame and clears the completion flag.
     */
    public void reset() {
        aniTick = 0;
        aniIndex = 0;
        completed = false;
    }

    /**
     * Sets the current frame index to the last frame of the animation.
     */
    public void setIndexToLastFrame() {
        aniIndex = images.length-1;
    }

    /**
     * Modifies the playback speed of the animation.
     * @param value The value to adjust the speed by (positive decreases speed, negative increases it).
     */
    public void modifySpeed(int value) {
        this.aniSpeed = Game.UPS_SET / (quantity + 2 + value);
    }

    /**
     * Creates a new Animation object consisting of a sub-sequence of the current frames.
     * @param fromFrame The starting frame index (inclusive).
     * @param toFrame The ending frame index (inclusive).
     * @return A new Animation object containing the specified frames.
     */
    public Animation getSubAnimation(int fromFrame, int toFrame) {
        BufferedImage[] frames = new BufferedImage[toFrame - fromFrame + 1];
        for (int i = fromFrame, j = 0; i <= toFrame; i++, j++)
            frames[j] = images[i];

        return new Animation(frames);
    }
}

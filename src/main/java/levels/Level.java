package levels;

/**
 * The Level class represents a game level, containing the layout data
 * as a 2D array of tile indices.
 */
public class Level {

    private int[][] lvlData;

    /**
     * Constructs a new Level with the specified layout data.
     *
     * @param lvlData A 2D array representing the tile indices of the level.
     */
    public Level(int[][] lvlData) {
        this.lvlData = lvlData;
    }

    /**
     * Gets the sprite index at a specific tile coordinate.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return The sprite index at the specified coordinate.
     */
    public int getSpriteIndex(int x, int y) {
        return lvlData[y][x];
    }

    /**
     * Returns the full 2D array of level data.
     *
     * @return The 2D array representing the level.
     */
    public int[][] getLevelData() {
        return lvlData;
    }
}

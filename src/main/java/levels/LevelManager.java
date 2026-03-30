package levels;

import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Game.TILES_DEFAULT_SIZE;
import static main.Game.TILES_SIZE;
import static utils.LoadSave.LEVEL_ATLAS;
import static utils.LoadSave.getSave;

public class LevelManager {

    private BufferedImage[] levelSprite;
    private BufferedImage[] backgroundLayers;
    private Game game;
    private Level levelOne;

    public static final int TILESET_HEIGHT = 15;
    public static final int TILESET_WIDTH = 21;

    public LevelManager(Game game) {
        this.game = game;
        importSprites();
        loadBackgroundLayers();
        levelOne = new Level(LoadSave.getLevelData());
    }

    private void loadBackgroundLayers() {
        backgroundLayers = new BufferedImage[3];
        backgroundLayers[0] = getSave(LoadSave.BACKGROUND_LAYER_1);
        backgroundLayers[1] = getSave(LoadSave.BACKGROUND_LAYER_2);
        backgroundLayers[2] = getSave(LoadSave.BACKGROUND_LAYER_3);
    }

    private void importSprites() {
        BufferedImage img = getSave(LEVEL_ATLAS);
        levelSprite = new BufferedImage[TILESET_WIDTH * TILESET_HEIGHT];
        for (int j = 0; j < TILESET_HEIGHT; j++) {
            for (int i = 0; i < TILESET_WIDTH; i++) {
                int index = j * TILESET_WIDTH + i;
                levelSprite[index] = img.getSubimage(i * TILES_DEFAULT_SIZE, j * TILES_DEFAULT_SIZE, TILES_DEFAULT_SIZE, TILES_DEFAULT_SIZE);
            }
        }
    }

    public void draw(Graphics g) {
        // Draw Background Layers
        for (BufferedImage img : backgroundLayers) {
            if (img != null) {
                g.drawImage(img, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
            }
        }

        // Draw Level Tiles
        for (int j = 0; j < Game.TILES_IN_HEIGHT; j++) {
            for (int i = 0; i < Game.TILES_IN_WIDTH; i++) {
                int index = levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], i*TILES_SIZE, j*TILES_SIZE, TILES_SIZE, TILES_SIZE, null);
            }
        }

    }

    public void update() {

    }

    public Level getCurrentLevel() {
        return levelOne;
    }
}

package levels;

import main.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.LoadSave.LEVEL_ATLAS;
import static utils.LoadSave.getSave;

public class LevelManager {

    private BufferedImage[][] levelSprite;
    private Game game;

    public LevelManager(Game game) {
        this.game = game;
        importSprites();
    }

    private void importSprites() {
        BufferedImage img = getSave(LEVEL_ATLAS);
        levelSprite = new BufferedImage[21][15];
        for (int i = 0; i < levelSprite.length; i++) {
            for (int  j = 0; j < levelSprite[i].length; j++) {
                levelSprite[i][j] = img.getSubimage(i*24, j*24, 24, 24);
            }
        }
    }

    public void draw(Graphics g) {
        g.drawImage(levelSprite[1][1], 0, 0, null);
    }
    public void update() {

    }
}

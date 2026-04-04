package entities;

import animations.Direction;
import gamestates.Playing;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static main.Game.*;

public class EnemyManager {
    private Playing playing;
    private ArrayList<Skeleton> skeletons = new ArrayList<>();

    private Point2D.Double  leftPos = new Point2D.Double(-100.0, GAME_HEIGHT - 12*TILES_SIZE),
                            rightPos = new Point2D.Double(GAME_WIDTH + 100.0, GAME_HEIGHT - 10*TILES_SIZE);
    private int[][] lvlData;

    public EnemyManager(Playing playing) {
        this.playing = playing;

        lvlData = playing.getLevelData();
    }

    public void update() {
        for (Skeleton skeleton : skeletons) {
            skeleton.update();
            skeleton.chase(playing.getPlayer());
            if (skeleton.isDead) {
                skeletons.remove(skeleton);
                break;
            }
        }
    }

    public void draw(Graphics g) {
        for (Skeleton skeleton : skeletons)
            skeleton.draw(g);
    }

    public void summonSkeleton(Direction dir) {
        Skeleton skeleton;
        if (dir == Direction.LEFT) {
            skeleton = new Skeleton(100, 20, new Point2D.Double(leftPos.x, leftPos.y), SCALE/2, lvlData);
        } else {
            skeleton = new Skeleton(100, 20, new Point2D.Double(rightPos.x, rightPos.y), SCALE/2, lvlData);
        }
        skeletons.add(skeleton);
    }

    public ArrayList<Skeleton> getSkeletons() {
        return skeletons;
    }
}

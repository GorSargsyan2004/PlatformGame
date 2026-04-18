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
    private ArrayList<Goblin> goblins = new ArrayList<>();

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
        for (Goblin goblin : goblins) {
            goblin.update();
            goblin.chase(playing.getPlayer());
            if (goblin.isDead) {
                skeletons.remove(goblin);
                break;
            }
        }
    }

    public void draw(Graphics g) {
        for (Skeleton skeleton : skeletons)
            skeleton.draw(g);
        for (Goblin goblin : goblins)
            goblin.draw(g);
    }

    public void summonSkeleton(Direction dir) {
        Skeleton skeleton;
        if (dir == Direction.LEFT) {
            skeleton = new Skeleton(120, 15, new Point2D.Double(leftPos.x, leftPos.y), SCALE/2, lvlData);
        } else {
            skeleton = new Skeleton(120, 15, new Point2D.Double(rightPos.x, rightPos.y), SCALE/2, lvlData);
        }
        skeletons.add(skeleton);
    }

    public void summonGoblin(Direction dir) {
        Goblin goblin;
        if (dir == Direction.LEFT) {
            goblin = new Goblin(40, 3, new Point2D.Double(leftPos.x, leftPos.y), SCALE/2, lvlData);
        } else {
            goblin = new Goblin(40, 3, new Point2D.Double(rightPos.x, rightPos.y), SCALE/2, lvlData);
        }
        goblins.add(goblin);
    }

    public ArrayList<Skeleton> getSkeletons() {
        return skeletons;
    }
    public ArrayList<Goblin> getGoblins() {
        return goblins;
    }
}

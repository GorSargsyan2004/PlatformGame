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
    private ArrayList<Mushroom> mushrooms = new ArrayList<>();
    private ArrayList<FlyingEye> flyingEyes = new ArrayList<>();

    private Point2D.Double  leftPos = new Point2D.Double(-100.0, GAME_HEIGHT - 12*TILES_SIZE),
                            rightPos = new Point2D.Double(GAME_WIDTH + 100.0, GAME_HEIGHT - 10*TILES_SIZE);
    private int[][] lvlData;

    public EnemyManager(Playing playing) {
        this.playing = playing;

        lvlData = playing.getLevelData();
    }

    public void update() {
        skeletons.removeIf(skeleton -> {
            skeleton.update();
            skeleton.chase(playing.getPlayer());
            return skeleton.isDead;
        });
        goblins.removeIf(goblin -> {
            goblin.update();
            goblin.chase(playing.getPlayer());
            return goblin.isDead;
        });
        mushrooms.removeIf(mushroom -> {
            mushroom.update();
            mushroom.chase(playing.getPlayer());
            return mushroom.isDead;
        });
        flyingEyes.removeIf(flyingEye -> {
            flyingEye.update();
            flyingEye.chase(playing.getPlayer());
            return flyingEye.isDead;
        });
    }

    public void draw(Graphics g) {
        for (Skeleton skeleton : skeletons)
            skeleton.draw(g);
        for (Goblin goblin : goblins)
            goblin.draw(g);
        for (Mushroom mushroom : mushrooms)
            mushroom.draw(g);
        for (FlyingEye flyingEye : flyingEyes)
            flyingEye.draw(g);
    }

    public void summonSkeleton(Direction dir) {
        Skeleton skeleton;
        if (dir == Direction.LEFT) {
            skeleton = new Skeleton(120, 15, new Point2D.Double(leftPos.x, leftPos.y), SCALE/2.2, lvlData);
        } else {
            skeleton = new Skeleton(120, 15, new Point2D.Double(rightPos.x, rightPos.y), SCALE/2.2, lvlData);
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

    public void summonMushroom(Direction dir) {
        Mushroom mushroom;
        if (dir == Direction.LEFT) {
            mushroom = new Mushroom(70, 8, new Point2D.Double(leftPos.x, leftPos.y), SCALE/2.3, lvlData);
        } else {
            mushroom = new Mushroom(70, 8, new Point2D.Double(rightPos.x, rightPos.y), SCALE/2.3, lvlData);
        }
        mushrooms.add(mushroom);
    }

    public void summonFlyingEye(Direction dir) {
        FlyingEye flyingEye;
        if (dir == Direction.LEFT) {
            flyingEye = new FlyingEye(40, 6, new Point2D.Double(leftPos.x, leftPos.y - 100), SCALE/2, lvlData);
        } else {
            flyingEye = new FlyingEye(40, 6, new Point2D.Double(rightPos.x, rightPos.y - 100), SCALE/2, lvlData);
        }
        flyingEyes.add(flyingEye);
    }

    public ArrayList<Skeleton> getSkeletons() {
        return skeletons;
    }
    public ArrayList<Goblin> getGoblins() {
        return goblins;
    }
    public ArrayList<Mushroom> getMushrooms() {
        return mushrooms;
    }
    public ArrayList<FlyingEye> getFlyingEyes() {
        return flyingEyes;
    }
}

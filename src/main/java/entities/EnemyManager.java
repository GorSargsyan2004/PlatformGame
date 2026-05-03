package entities;

import utils.Direction;
import gamestates.Playing;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static main.Game.*;

public class EnemyManager {
    private Playing playing;
    private int[][] lvlData;
    private AllayManager am;

    private final ArrayList<Skeleton> skeletons = new ArrayList<>();
    private final ArrayList<Goblin> goblins = new ArrayList<>();
    private final ArrayList<Mushroom> mushrooms = new ArrayList<>();
    private final ArrayList<FlyingEye> flyingEyes = new ArrayList<>();

    private Point2D.Double  leftPos = new Point2D.Double(-100.0, GAME_HEIGHT - 12*TILES_SIZE),
                            rightPos = new Point2D.Double(GAME_WIDTH + 100.0, GAME_HEIGHT - 10*TILES_SIZE);

    public EnemyManager(Playing playing) {
        this.playing = playing;
        this.am = playing.getAllayManager();

        lvlData = playing.getLevelData();
    }

    public void update() {
        skeletons.removeIf(skeleton -> {
            skeleton.update();
            Entity target = am.getClosestAllayOrPlayer(skeleton.pos);
            skeleton.chase(target);
            if (skeleton.isDead) {
                playing.getPlayer().addScore(skeleton.deathScore);
                return true;
            }
            return false;
        });
        goblins.removeIf(goblin -> {
            goblin.update();
            Entity target = am.getClosestAllayOrPlayer(goblin.pos);
            goblin.chase(target);
            if (goblin.isDead) {
                playing.getPlayer().addScore(goblin.deathScore);
                return true;
            }
            return false;
        });
        mushrooms.removeIf(mushroom -> {
            mushroom.update();
            Entity target = am.getClosestAllayOrPlayer(mushroom.pos);
            mushroom.chase(target);
            if (mushroom.isDead) {
                playing.getPlayer().addScore(mushroom.deathScore);
                return true;
            }
            return false;
        });
        flyingEyes.removeIf(flyingEye -> {
            flyingEye.update();
            Entity target = am.getClosestAllayOrPlayer(flyingEye.pos);
            flyingEye.chase(target);
            if (flyingEye.isDead) {
                playing.getPlayer().addScore(flyingEye.deathScore);
                return true;
            }
            return false;
        });
    }

    public void draw(Graphics g) {
        for (int i = 0; i < skeletons.size(); i++)
            skeletons.get(i).draw(g);
        for (int i = 0; i < goblins.size(); i++)
            goblins.get(i).draw(g);
        for (int i = 0; i < mushrooms.size(); i++)
            mushrooms.get(i).draw(g);
        for (int i = 0; i < flyingEyes.size(); i++)
            flyingEyes.get(i).draw(g);
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

    public Enemy getClosestEnemy(Point2D.Double pos) {
        Enemy closest = null;
        double minDist = Double.MAX_VALUE;

        for (Skeleton skeleton : skeletons) {
            double dist = pos.distance(skeleton.getCenter());
            if (dist < minDist) {
                closest = skeleton;
                minDist = dist;
            }
        }
        for (Goblin goblin : goblins) {
            double dist = pos.distance(goblin.getCenter());
            if (dist < minDist) {
                closest = goblin;
                minDist = dist;
            }
        }
        for (Mushroom mushroom : mushrooms) {
            double dist = pos.distance(mushroom.getCenter());
            if (dist < minDist) {
                closest = mushroom;
                minDist = dist;
            }
        }
        for (FlyingEye flyingEye : flyingEyes) {
            double dist = pos.distance(flyingEye.getCenter());
            if (dist < minDist) {
                closest = flyingEye;
                minDist = dist;
            }
        }

        return closest;
    }
}

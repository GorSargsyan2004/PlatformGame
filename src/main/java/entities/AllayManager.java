package entities;

import animations.Direction;
import gamestates.Playing;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static main.Game.*;

public class AllayManager {
    private Playing playing;
    private int[][] lvlData;

    private ArrayList<Knight> knights = new ArrayList<>();

    public AllayManager(Playing playing) {
        this.playing = playing;

        lvlData = playing.getLevelData();
    }

    public void update() {
        knights.removeIf(knight -> {
            knight.update();
            knight.chase(playing.getEnemyManager());
            return knight.isDead;
        });
    }

    public void draw(Graphics g) {
        for (Knight knight : knights)
            knight.draw(g);
    }

    public void summonKnight() {
        Knight knight;
        knight = new Knight(100, 15, new Point2D.Double(400.0, GAME_HEIGHT - 12*TILES_SIZE), SCALE/2, lvlData);
        knights.add(knight);
    }

    public ArrayList<Knight> getKnights() {
        return knights;
    }

    public Entity getClosestAllayOrPlayer(Point2D.Double pos) {
        Entity closest = null;
        double minDist = Double.MAX_VALUE;

        for (Knight knight : knights) {
            double dist = pos.distance(knight.getCenter());
            if (dist < minDist) {
                closest = knight;
                minDist = dist;
            }
        }

        Player player = playing.getPlayer();
        if (player.getCenter().distance(pos) < minDist)
            closest = player;

        return closest;
    }
}

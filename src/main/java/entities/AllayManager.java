package entities;

import gamestates.Playing;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static main.Game.*;

public class AllayManager {
    private final Playing playing;
    private final int[][] lvlData;

    private final ArrayList<Knight> knights = new ArrayList<>();
    private final ArrayList<Archer> archers = new ArrayList<>();

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
        archers.removeIf(archer -> {
            archer.update();
            archer.chaseAsShooter(playing.getEnemyManager());
            return archer.isDead;
        });
    }

    public void draw(Graphics g) {
        for (Knight knight : knights)
            knight.draw(g);
        for (Archer archer : archers)
            archer.draw(g);
    }

    public void summonKnight() {
        Knight knight;
        knight = new Knight(100, 15, new Point2D.Double(400.0, GAME_HEIGHT - 12*TILES_SIZE), SCALE/2, lvlData);
        knights.add(knight);
    }

    public void summonArcher() {
        Archer archer;
        archer = new Archer(80, 10, new Point2D.Double(400.0, GAME_HEIGHT - 12*TILES_SIZE), SCALE/2, lvlData);
        archers.add(archer);
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
        for (Archer archer : archers) {
            double dist = pos.distance(archer.getCenter());
            if (dist < minDist) {
                closest = archer;
                minDist = dist;
            }
        }

        Player player = playing.getPlayer();
        if (player.getCenter().distance(pos) < minDist)
            closest = player;

        return closest;
    }
}

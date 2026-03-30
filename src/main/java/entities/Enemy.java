package entities;

import java.awt.geom.Point2D;

public class Enemy extends Entity{

    Enemy (int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);
    }

    public void chase(Player p) {}
}

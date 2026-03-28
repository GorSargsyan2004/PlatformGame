package entities;

import java.awt.geom.Point2D;

public class Enemy extends Entity{

    Enemy (int health, int damage, Point2D.Double pos, double movementSpeed) {
        super(health, damage, pos, movementSpeed);
    }

    public void chase(Player p) {}
}

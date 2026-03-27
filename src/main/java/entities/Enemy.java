package entities;

import java.awt.geom.Point2D;

public class Enemy extends Entity{

    Enemy (int health, int damage, Point2D.Double pos) {
        super(health, damage, pos);
    }

    public void chase(Player p) {}
}

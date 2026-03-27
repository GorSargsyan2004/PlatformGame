package entities;

import java.awt.geom.Point2D;

public class Entity {
    private int health;
    private int damage;
    public Point2D.Double pos;

    Entity(int health, int damage, Point2D.Double pos) {
        this.health = health;
        this.damage = damage;
        this.pos = pos;
    }
}

package entities;

import animations.Animation;

import java.awt.geom.Point2D;

public class Entity {
    private int health;
    private int damage;
    protected Animation[] animations;
    public Point2D.Double pos;
    public double movementSpeed;

    Entity(int health, int damage, Point2D.Double pos, double movementSpeed) {
        this.health = health;
        this.damage = damage;
        this.pos = pos;
        this.movementSpeed = movementSpeed;
    }
}

package entities;

import java.awt.geom.Point2D;

public class Player extends Entity {

    Player(int health, int damage, Point2D.Double pos) {
        super(health, damage, pos);
    }

    public void goRight() {
        pos.setLocation(pos.x + 0.05, pos.y);
    }
}

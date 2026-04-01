package entities;

import animations.Animation;
import animations.Direction;
import main.Game;

import java.awt.geom.Point2D;

public abstract class Enemy extends Entity{
    protected Animation currentAnim;
    protected Direction currentDir;

    Enemy (int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);
    }

    protected boolean isOutOfBorders(int spriteWidth) {
        return (pos.x < 0 || pos.x > Game.GAME_WIDTH - spriteWidth);
    }

    protected void updateFromCorners(Animation anim) {
        // Left Corner
        if (pos.x < 0) {
            pos.x += movementSpeed;
            currentAnim = anim;
            currentDir = Direction.RIGHT;
        }

        // Right Corner
        if (pos.x > Game.GAME_WIDTH - anim.getWidth()) {
            pos.x -= movementSpeed;
            currentAnim = anim;
            currentDir = Direction.LEFT;
        }
    }

    protected void chase(Player player) {
        if ( (hitBox.x > player.hitBox.x - 100 && hitBox.x < player.hitBox.x + 100) &&
                (hitBox.y > player.hitBox.y + 50 && hitBox.y < player.hitBox.y + 100) &&
                (!player.inAir)) {
            setJump(true);
        }

        if (hitBox.x > player.hitBox.x - 20 && hitBox.x < player.hitBox.x + 20) {
            setLeft(false);
            setRight(false);
            return;
        }

        if (player.hitBox.x > hitBox.x) {
            setRight(true);
            setLeft(false);
        } else {
            setRight(false);
            setLeft(true);
        }
    }
}

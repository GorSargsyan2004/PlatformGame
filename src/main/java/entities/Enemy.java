package entities;

import animations.Animation;
import utils.Direction;
import main.Game;

import java.awt.geom.Point2D;

public abstract class Enemy extends Entity{
    protected long timePlayerInRange = 0;
    int deathScore;

    Enemy (int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);
        this.canWalkOffScreen = false;
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

    protected void chase(Entity player) {
        if (player == null || player.isDead) return;

        if (isIdle || attack) {
            setLeft(false);
            setRight(false);
            return;
        }

        if (isInAttackRange(this, player)) {
            setLeft(false);
            setRight(false);
            if (timePlayerInRange == 0) {
                timePlayerInRange = System.currentTimeMillis();
                if (!player.isAttackedBy(this)) player.takeHit(this);
            } else if (System.currentTimeMillis() - timePlayerInRange >= 200) {
                setAttack(true);
                timePlayerInRange = 0;
            }
            return;
        } else {
            timePlayerInRange = 0;
        }

        if ( (hitBox.x > player.hitBox.x - 100 && hitBox.x < player.hitBox.x + 100) &&
                (hitBox.y > player.hitBox.y + 50 && hitBox.y < player.hitBox.y + 100) &&
                (!player.inAir)) {
            setJump(true);
        }

        if (player.hitBox.x > hitBox.x) {
            setRight(true);
            setLeft(false);
        } else {
            setRight(false);
            setLeft(true);
        }
    }

    @Override
    protected void attack(Animation attackAnimation) {
        currentAnim = attackAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            attack = false;
            isIdle = true;
            currentAnim.reset();
        }
    }

    @Override
    protected boolean checkForTakingHit() {
        if (!isBeingAttacked()) return false;

        for (int i = 0; i < attackers.size(); i++) {
            Entity attacker = attackers.get(i);
            if (System.currentTimeMillis() - attackersTimeAttackedInMillis.get(i) >= 100) {
                changeHealth(-attacker.damage);
                attackers.remove(i);
                attackersTimeAttackedInMillis.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the score for killing the enemy.
     * @return the score for killing the enemy.
     */
    public int getDeathScore() {
        return deathScore;
    }
}

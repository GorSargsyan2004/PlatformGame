package entities;

import animations.Animation;
import utils.Direction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.net.IDN;

/**
 * Represents an Allay entity, which is a friendly or allied entity that can assist the player.
 * <p>
 * Allays extend the {@link Entity} class and include behaviors for chasing and attacking enemies,
 * either through close-range combat or ranged shooting. They are restricted from walking off-screen.
 * </p>
 */
public abstract class Allay extends Entity {
    protected long timeEnemyInRange = 0;
    protected int shootingDistance;
    protected boolean dash = false;
    protected boolean shooting = false;
    protected boolean isEscaping = false;

    Allay(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);
        this.canWalkOffScreen = false;
    }

    /**
     * Specialized chasing behavior for ranged-capable Allays.
     * Manages distance to the closest enemy, switching between movement,
     * close-range attacking, and ranged shooting.
     * @param em The EnemyManager to query for targets.
     */
    protected void chaseAsShooter(EnemyManager em) {
        Enemy enemy = em.getClosestEnemy(pos);

        if (enemy == null || enemy.isDead) return;

        if (attack || shooting || isEscaping || dash) {
            if (!isEscaping && !dash) {
                setLeft(false);
                setRight(false);
            }
            return;
        }

        boolean inCloseRange = isInAttackRange(this, enemy);
        boolean inShootingRange = isInAttackRange(this, enemy, shootingDistance);

        if (inCloseRange || inShootingRange) {
            setLeft(false);
            setRight(false);
            if (timeEnemyInRange == 0) {
                timeEnemyInRange = System.currentTimeMillis();
                if (!enemy.isAttackedBy(this)) enemy.takeHit(this);
            } else if (System.currentTimeMillis() - timeEnemyInRange >= 200) {
                if (inCloseRange) {
                    setAttack(true);
                } else {
                    setShooting(true);
                }
                timeEnemyInRange = 0;
            }
            return;
        } else {
            timeEnemyInRange = 0;
        }

        if ( (hitBox.x > enemy.hitBox.x - 100 && hitBox.x < enemy.hitBox.x + 100) &&
                (hitBox.y > enemy.hitBox.y + 50 && hitBox.y < enemy.hitBox.y + 100) &&
                (!enemy.inAir)) {
            setJump(true);
        }

        if (enemy.hitBox.x > hitBox.x) {
            setRight(true);
            setLeft(false);
        } else {
            setRight(false);
            setLeft(true);
        }
    }

    /**
     * Basic chasing behavior for melee Allays.
     * Attempts to reach the closest enemy and initiates an attack when in range.
     * @param em The EnemyManager to query for targets.
     */
    protected void chase(EnemyManager em) {
        Enemy enemy = em.getClosestEnemy(pos);

        if (enemy == null || enemy.isDead) return;

        if (isIdle || attack) {
            setLeft(false);
            setRight(false);
            return;
        }

        if (isInAttackRange(this, enemy)) {
            setLeft(false);
            setRight(false);
            if (timeEnemyInRange == 0) {
                timeEnemyInRange = System.currentTimeMillis();
                if (!enemy.isAttackedBy(this)) enemy.takeHit(this);
            } else if (System.currentTimeMillis() - timeEnemyInRange >= 200) {
                setAttack(true);
                timeEnemyInRange = 0;
            }
            return;
        } else {
            timeEnemyInRange = 0;
        }

        if ( (hitBox.x > enemy.hitBox.x - 100 && hitBox.x < enemy.hitBox.x + 100) &&
                (hitBox.y > enemy.hitBox.y + 50 && hitBox.y < enemy.hitBox.y + 100) &&
                (!enemy.inAir)) {
            setJump(true);
        }

        if (enemy.hitBox.x > hitBox.x) {
            setRight(true);
            setLeft(false);
        } else {
            setRight(false);
            setLeft(true);
        }
    }

    /**
     * Executes the melee attack animation.
     * @param attackAnimation The animation to play.
     */
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

    /**
     * Executes the shooting animation.
     * @param attackAnimation The animation to play for shooting.
     */
    protected void shoot(Animation attackAnimation) {
        currentAnim = attackAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            shooting = false;
            isIdle = true;

            currentAnim.reset();
        }
    }

    /**
     * Checks for incoming hits. Specialized for Allays to process
     * hits from attackers.
     * @return True if a hit was taken.
     */
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

    /** @param b Sets whether the Allay is currently shooting. */
    public void setShooting(boolean b) {shooting = b;}
    /** @param b Sets whether the Allay is currently dashing. */
    public void setDash(boolean b) {dash = b;}

}

package entities;

import animations.Animation;
import utils.Direction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.net.IDN;

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

    protected void shoot(Animation attackAnimation) {
        currentAnim = attackAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            shooting = false;
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

    public void setShooting(boolean b) {shooting = b;}
    public void setDash(boolean b) {dash = b;}
}

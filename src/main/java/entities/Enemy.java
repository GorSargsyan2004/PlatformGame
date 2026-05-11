package entities;

import animations.Animation;
import utils.Direction;
import main.Game;

import java.awt.geom.Point2D;

/**
 * Represents the base abstract class for all enemy entities.
 * <p>
 * Enemies extend the {@link Entity} class and include common enemy behaviors such as
 * chasing the player, attacking when in range, and handling death scores.
 * They are generally restricted from walking off-screen.
 * </p>
 */
public abstract class Enemy extends Entity{
    protected long timePlayerInRange = 0;
    int deathScore;

    Enemy (int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);
        this.canWalkOffScreen = false;
    }

    /**
     * Checks if the enemy is outside the game world borders.
     * @param spriteWidth The width of the enemy's sprite for boundary checking.
     * @return True if the enemy is out of bounds.
     */
    protected boolean isOutOfBorders(int spriteWidth) {
        return (pos.x < 0 || pos.x > Game.GAME_WIDTH - spriteWidth);
    }

    /**
     * Handles behavior when the enemy hits the screen corners, forcing
     * them to turn around.
     * @param anim The animation to play while turning/moving away from corners.
     */
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

    /**
     * Common chasing behavior for ground enemies targeting the player.
     * Manages movement, jumping over obstacles, and initiating attacks.
     * @param player The player entity to chase.
     */
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

    /**
     * Executes the enemy attack animation.
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
     * Checks for incoming hits from the player or allies.
     * @return True if a hit was successfully taken.
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


    /**
     * Returns the score for killing the enemy.
     * @return the score for killing the enemy.
     */
    public int getDeathScore() {
        return deathScore;
    }
}

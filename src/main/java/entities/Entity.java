package entities;

import animations.Animation;
import utils.Direction;
import main.Game;
import utils.HelpMethods;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import io.arxila.javatuples.Pair;

/**
 * Represents the base abstract class for all game entities.
 * <p>
 * This class provides fundamental properties and behaviors shared by all entities,
 * such as health, damage, position, movement, animations, and hitboxes.
 * Subclasses must implement {@link #update()} and {@link #draw(Graphics)}.
 * </p>
 */
public abstract class Entity {
    protected int health;
    protected int damage;
    protected ArrayList<Entity> attackers;
    protected ArrayList<Long> attackersTimeAttackedInMillis;
    protected Animation[] animations;
    protected Rectangle2D.Float hitBox;
    protected int[][] lvlData;

    // Physics & State Variables
    protected boolean leftPressed, rightPressed, attack;
    protected boolean attackChecked = false;
    protected boolean inAir = false;
    protected boolean landing = false;
    protected boolean onSlope = false;
    protected boolean isIdle = false;
    protected boolean isHurt = false;
    protected boolean isDead = false;
    protected double ySpeed = 0;
    protected double gravity = 0.04 * Game.SCALE;
    protected double jumpSpeed = -3.3 * Game.SCALE;
    protected int attackDistance;

    protected static float scale = Game.SCALE;

    protected float xDrawOffset;
    protected float yDrawOffset;

    protected int entityWidth;
    protected int entityHeight;

    protected boolean canWalkOffScreen = true;

    public Point2D.Double pos;
    public double movementSpeed;

    protected Animation currentAnim;
    protected Direction currentDir;

    Entity(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        this.health = health;
        this.damage = damage;
        this.pos = pos;
        this.movementSpeed = movementSpeed;
        this.lvlData = lvlData;

        attackers = new ArrayList<>();
        attackersTimeAttackedInMillis = new ArrayList<>();

        // Initialize hitBox to avoid null pointer exceptions, though it will be properly sized in subclasses
        this.hitBox = new Rectangle2D.Float((float)pos.x, (float)pos.y, 0, 0);
    }

    /**
     * Draws the entity's hitbox for debugging purposes.
     * @param g The graphics context to draw on.
     */
    protected void drawHitbox(Graphics g) {
        // For debugging the hitbox
        if (hitBox != null) {
            g.setColor(Color.RED);
            g.drawRect((int) hitBox.x, (int) hitBox.y, (int) hitBox.width, (int) hitBox.height);
        }
    }

    /**
     * Initializes the entity's hitbox. This should be overridden by subclasses
     * to set the specific dimensions and offsets of the hitbox.
     */
    protected void initHitbox() {
        // To be overridden by subclasses
    }

    /**
     * Updates the entity's logic, including physics, state, and animation ticks.
     * Must be implemented by subclasses.
     */
    public abstract void update();

    /**
     * Draws the entity on the screen.
     * Must be implemented by subclasses.
     * @param g The graphics context to draw on.
     */
    public abstract void draw(Graphics g);

    /**
     * Synchronizes the hitbox position with the entity's current world position.
     */
    protected void updateHitbox() {
        if (hitBox != null) {
            hitBox.x = (float) pos.x;
            hitBox.y = (float) pos.y;
        }
    }

    /** @param left Sets whether the entity is trying to move left. */
    public void setLeft(boolean left) { this.leftPressed = left; }
    /** @param right Sets whether the entity is trying to move right. */
    public void setRight(boolean right) { this.rightPressed = right; }
    /** @param attack Sets whether the entity is initiating an attack. */
    public void setAttack(boolean attack) { this.attack = attack; }

    /**
     * Initiates a jump if the entity is currently on the ground.
     * @param jump True to trigger a jump.
     */
    public void setJump(boolean jump) {
        if (jump && !inAir && !landing) {
            this.inAir = true;
            this.ySpeed = this.jumpSpeed;
        }
    }

    /**
     * Registers an attacker and the time of the attack.
     * @param entity The entity that dealt the hit.
     */
    protected void takeHit(Entity entity) {
        attackersTimeAttackedInMillis.add(System.currentTimeMillis());
        attackers.add(entity);
    }

    /**
     * Modifies the entity's health.
     * @param value The amount to add to health (negative for damage).
     */
    public void changeHealth(int value) {
        health += value;
        if (health <= 0) health = 0;
    }

    /**
     * Processes pending hits from attackers. Checks if attackers are still in range
     * and if enough time has passed since the hit was registered.
     * @return True if a hit was successfully processed and health was reduced.
     */
    protected boolean checkForTakingHit() {
        if (!isBeingAttacked()) return false;

        boolean hitTaken = false;
        for (int i = attackers.size() - 1; i >= 0; i--) {
            Entity attacker = attackers.get(i);
            if (attacker.hitBox != null && this.hitBox != null && isInAttackRange(attacker, this) && !attacker.isDead) {
                if (System.currentTimeMillis() - attackersTimeAttackedInMillis.get(i) >= 1000) {
                    changeHealth(-attacker.damage);
                    attackers.remove(i);
                    attackersTimeAttackedInMillis.remove(i);
                    hitTaken = true;
                }
            } else if (System.currentTimeMillis() - attackersTimeAttackedInMillis.get(i) >= 1000) {
                attackers.remove(i);
                attackersTimeAttackedInMillis.remove(i);
            }
        }
        return hitTaken;
    }

    /**
     * Updates the entity's state to "hurt" and plays the provided animation.
     * @param takeHit The animation to play when taking a hit.
     */
    protected void takeHit(Animation takeHit) {
        if (currentAnim != takeHit) {
            attack = false;
            isIdle = false;
            attackChecked = false;
            
            // Reset all animations to ensure a clean start for the takeHit animation
            if (animations != null) {
                for (Animation anim : animations)
                    if (anim != null && !isDead) anim.reset();
            }
        }

        isHurt = true;
        currentAnim = takeHit;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            isHurt = false;
            currentAnim.reset();
        }
    }

    /**
     * Updates the attack animation and resets state once completed.
     * @param attackAnimation The animation to play for the attack.
     */
    protected void attack(Animation attackAnimation) {
        currentAnim = attackAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            attack = false;
            isIdle = true;
            attackChecked = false;
            currentAnim.reset();
        }
    }

    /**
     * Updates the idle animation.
     * @param idleAnimation The animation to play while idle.
     */
    protected void idle(Animation idleAnimation) {
        currentAnim = idleAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            isIdle = false;
            currentAnim.reset();
        }
    }

    /**
     * Plays the death animation and locks the entity in its final frame.
     * @param deadAnimation The animation to play upon death.
     */
    protected void dead(Animation deadAnimation) {
        currentAnim = deadAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            isDead = true;
            currentAnim.setIndexToLastFrame();
        }
    }

    /**
     * Utility to check if an attacked entity is within a certain distance of an attacker.
     * @param attacker The entity performing the attack.
     * @param attacked The entity being targeted.
     * @param attackDistance The maximum distance allowed for the attack.
     * @return True if within range.
     */
    protected boolean isInAttackRange(Entity attacker, Entity attacked, int attackDistance) {
        if (attacker.hitBox == null || attacked.hitBox == null) return false;
        Rectangle2D.Float a = attacker.hitBox;
        Rectangle2D.Float b = attacked.hitBox;

        float centerX_A = a.x + a.width / 2;
        float centerX_B = b.x + b.width / 2;
        float centerY_A = a.y + a.height / 2;
        float centerY_B = b.y + b.height / 2;

        float distX = Math.abs(centerX_A - centerX_B);
        float distY = Math.abs(centerY_A - centerY_B);

        // Distance between edges (negative if hitboxes overlap)
        float xDiff = distX - (a.width + b.width) / 2;
        float yDiff = distY - (a.height + b.height) / 2;

        // Use the attacker's reach (attacker.attackDistance).
        // xDiff/yDiff is the empty space between hitboxes.
        return (xDiff <= attackDistance && yDiff <= (float) attackDistance / 2);
    }

    /**
     * Checks if an entity is within the attacker's default attack range.
     * @param attacker The entity performing the attack.
     * @param attacked The entity being targeted.
     * @return True if within default range.
     */
    protected boolean isInAttackRange(Entity attacker, Entity attacked) {
        return isInAttackRange(attacker, attacked, attacker.attackDistance);
    }

    /** @return True if the entity has registered attackers waiting to be processed. */
    protected boolean isBeingAttacked() {
        return !attackers.isEmpty();
    }

    /**
     * Checks if this entity is currently being targeted by a specific entity.
     * @param entity The potential attacker.
     * @return True if that entity is in the attackers list.
     */
    protected boolean isAttackedBy(Entity entity) {
        return attackers.contains(entity);
    }

    /**
     * Updates the landing animation state.
     * @param landingAnimation The animation to play when hitting the ground.
     */
    protected void landing(Animation landingAnimation) {
        currentAnim = landingAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()) {
            landing = false;
            currentAnim.reset();
        }
    }

    /**
     * Handles vertical physics, gravity, and collision with floors and slopes.
     * @param crouchIndex Index of the crouch/landing animation to reset on impact.
     */
    protected void physicsUpdate(int crouchIndex) {
        if (hitBox == null) return;
        // SLOPE LOGIC (Check ground below feet)
        float xCheck = hitBox.x + hitBox.width / 2;
        float yCheck = hitBox.y + hitBox.height;

        int tileX = (int) (xCheck / main.Game.TILES_SIZE);
        int tileY = (int) (yCheck / main.Game.TILES_SIZE);

        boolean foundSlope = false;
        // Search in a small window to catch the slope accurately
        for (int offset = -1; offset <= 1; offset++) {
            int currentTileY = tileY + offset;
            if (tileX >= 0 && tileX < main.Game.TILES_IN_WIDTH && currentTileY >= 0 && currentTileY < main.Game.TILES_IN_HEIGHT) {
                int tileValue = lvlData[currentTileY][tileX];
                if (utils.HelpMethods.IsSlope(tileValue)) {
                    float slopeYInTile = utils.HelpMethods.GetSlopeY(xCheck, tileValue);
                    float slopeYWorld = currentTileY * main.Game.TILES_SIZE + slopeYInTile;

                    // If player is on or falling onto the slope (only snap if not moving upwards)
                    if (ySpeed >= 0 && hitBox.y + hitBox.height >= slopeYWorld - 12 && hitBox.y + hitBox.height <= slopeYWorld + 12) {
                        pos.y = slopeYWorld - hitBox.height - yDrawOffset;
                        inAir = false;
                        onSlope = true;
                        ySpeed = 0;
                        updateHitbox();
                        return;
                    }
                    foundSlope = true;
                }
            }
        }

        if (!foundSlope) onSlope = false;

        if (!inAir && !onSlope) {
            if (utils.HelpMethods.CanMoveHere(new Point2D.Double(hitBox.x, hitBox.y + 1), hitBox.width, hitBox.height, lvlData)) {
                inAir = true;
            }
        }

        if (inAir) {
            if (utils.HelpMethods.CanMoveHere(new Point2D.Double(hitBox.x, hitBox.y + (float)ySpeed), hitBox.width, hitBox.height, lvlData)) {
                pos.y += ySpeed;
                ySpeed += gravity;
                updateHitbox();
            } else {
                pos.y = utils.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor(hitBox, (float)ySpeed) - yDrawOffset;
                updateHitbox();
                if (ySpeed > 0) {
                    // Only trigger landing animation if fall was significant (impact speed > 2.5)
                    if (ySpeed > 2.5) {
                        landing = true;
                        if (animations != null && crouchIndex >= 0 && crouchIndex < animations.length && animations[crouchIndex] != null)
                            animations[crouchIndex].reset();
                    }
                    inAir = false;
                    ySpeed = 0;
                } else {
                    ySpeed = 0.5;
                }
            }
        }
    }

    /**
     * Handles horizontal movement logic, including direction switching, slope adjustments,
     * and collision detection.
     * @param currentAnim The current animation state.
     * @param currentDir The current facing direction.
     * @param RUN Index for the running animation.
     * @param IDLE Index for the idle animation.
     * @param SCALE Scale factor for drawing and hitbox calculations.
     * @return A Pair containing the updated Animation and Direction.
     */
    protected Pair<Animation, Direction> run(Animation currentAnim, Direction currentDir, int RUN, int IDLE, float SCALE) {
        float xSpeed = 0;
        Direction lastDir = currentDir;
        if (leftPressed && !rightPressed) {
            xSpeed = -(float)movementSpeed;
            currentDir = Direction.LEFT;
        } else if (rightPressed && !leftPressed) {
            xSpeed = (float)movementSpeed;
            currentDir = Direction.RIGHT;
        }

        // Keep hitbox position stable when turning
        if (lastDir != currentDir) {
            float oldHitboxX = hitBox.x;
            updateHitbox(currentDir, currentAnim, SCALE);
            float diff = hitBox.x - oldHitboxX;
            pos.x -= diff;
            updateHitbox(currentDir, currentAnim, SCALE);
        }

        if (xSpeed != 0) {
            float nextX = hitBox.x + xSpeed;
            float nextY = hitBox.y;

            // If on slope, calculate the new target Y to keep the player on the surface
            if (onSlope) {
                float xCheck = nextX + hitBox.width / 2;
                int tileX = (int) (xCheck / main.Game.TILES_SIZE);
                int tileY = (int) ((hitBox.y + hitBox.height) / main.Game.TILES_SIZE);

                boolean slopeFound = false;
                for (int offset = -1; offset <= 1; offset++) {
                    int currentTileY = tileY + offset;
                    if (tileX >= 0 && tileX < main.Game.TILES_IN_WIDTH && currentTileY >= 0 && currentTileY < main.Game.TILES_IN_HEIGHT) {
                        int tileValue = lvlData[currentTileY][tileX];
                        if (utils.HelpMethods.IsSlope(tileValue)) {
                            float slopeYInTile = utils.HelpMethods.GetSlopeY(xCheck, tileValue);
                            nextY = currentTileY * main.Game.TILES_SIZE + slopeYInTile - hitBox.height;
                            slopeFound = true;
                            break;
                        }
                    }
                }
            }

            // Check collision using specialized method when on a slope
            boolean canMove = false;
            if (onSlope) {
                canMove = utils.HelpMethods.CanMoveHereOnSlope(new Point2D.Double(nextX, nextY), hitBox.width, hitBox.height, lvlData);
            } else {
                canMove = utils.HelpMethods.CanMoveHere(new Point2D.Double(nextX, nextY), hitBox.width, hitBox.height, lvlData);
            }

            if (canMove && !canWalkOffScreen) {
                // If moving outside left and already at or left of border, block.
                // If moving outside right and already at or right of border, block.
                // This allows entities summoned outside to walk IN.
                if (xSpeed < 0 && nextX < 0 && hitBox.x <= 0)
                    canMove = false;
                else if (xSpeed > 0 && nextX + hitBox.width > Game.GAME_WIDTH && hitBox.x + hitBox.width >= Game.GAME_WIDTH)
                    canMove = false;
            }

            if (canMove) {
                pos.x += xSpeed;
                if (onSlope) pos.y = nextY - yDrawOffset;
                currentAnim = (animations != null) ? animations[RUN] : currentAnim;
            } else {
                // Blocked by a wall, snap to it
                float currentXOffset = (currentDir == Direction.RIGHT) ? xDrawOffset : (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
                pos.x = utils.HelpMethods.GetEntityXPosNextToWall(hitBox, xSpeed) - currentXOffset;
                currentAnim = (animations != null) ? animations[IDLE] : currentAnim;
            }
            updateHitbox();
        } else {
            currentAnim = (animations != null) ? animations[IDLE] : currentAnim;
        }
        return Pair.of(currentAnim, currentDir);
    }

    /**
     * Updates the hitbox position based on the entity's facing direction and animation frame.
     * @param currentDir Facing direction.
     * @param currentAnim Current animation.
     * @param SCALE Scale factor.
     */
    protected void updateHitbox(Direction currentDir, Animation currentAnim, float SCALE) {
        if (hitBox == null) return;
        if (currentDir == Direction.RIGHT) {
            hitBox.x = (float) pos.x + xDrawOffset;
        } else {
            hitBox.x = (float) pos.x + (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
        }
        hitBox.y = (float)pos.y + yDrawOffset;
    }

    /** @return The center point of the entity's hitbox in world coordinates. */
    public Point2D.Double getCenter() {
        if (hitBox == null) return new Point2D.Double(pos.x, pos.y);
        return new Point2D.Double(hitBox.x + hitBox.width / 2, hitBox.y + hitBox.height / 2);
    }

    /**
     * Handles movement and animation state while the entity is in the air.
     * @param currentAnim Current animation.
     * @param currentDir Facing direction.
     * @param JUMP Index for jump animation.
     * @param UP_TO_FALL Index for transition animation.
     * @param FALL Index for falling animation.
     * @param SCALE Scale factor.
     * @return Updated Animation and Direction.
     */
    protected Pair<Animation, Direction> jump(Animation currentAnim, Direction currentDir, int JUMP, int UP_TO_FALL, int FALL, float SCALE) {
        float xSpeed = 0;
        Direction lastDir = currentDir;
        if (leftPressed && !rightPressed) {
            xSpeed = -(float)movementSpeed;
            currentDir = Direction.LEFT;
        } else if (rightPressed && !leftPressed) {
            xSpeed = (float)movementSpeed;
            currentDir = Direction.RIGHT;
        }

        // Keep hitbox position stable when turning
        if (lastDir != currentDir) {
            float oldHitboxX = hitBox.x;
            updateHitbox(currentDir, currentAnim, SCALE);
            float diff = hitBox.x - oldHitboxX;
            pos.x -= diff;
            updateHitbox(currentDir, currentAnim, SCALE);
        }

        if (xSpeed != 0) {
            float nextX = hitBox.x + xSpeed;

            boolean canMove = false;
            if (onSlope) {
                canMove = utils.HelpMethods.CanMoveHereOnSlope(new Point2D.Double(nextX, hitBox.y), hitBox.width, hitBox.height, lvlData);
            } else {
                canMove = utils.HelpMethods.CanMoveHere(new Point2D.Double(nextX, hitBox.y), hitBox.width, hitBox.height, lvlData);
            }

            if (canMove && !canWalkOffScreen) {
                if (xSpeed < 0 && nextX < 0 && hitBox.x <= 0)
                    canMove = false;
                else if (xSpeed > 0 && nextX + hitBox.width > Game.GAME_WIDTH && hitBox.x + hitBox.width >= Game.GAME_WIDTH)
                    canMove = false;
            }

            if (canMove) {
                pos.x += xSpeed;
            } else {
                float currentXOffset = (currentDir == Direction.RIGHT) ? xDrawOffset : (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
                pos.x = HelpMethods.GetEntityXPosNextToWall(hitBox, xSpeed) - currentXOffset;
            }
            updateHitbox();
        }

        if (ySpeed < -0.5) {
            currentAnim = (animations != null) ? animations[JUMP] : currentAnim;
        } else if (ySpeed >= -0.5 && ySpeed < 0.5) {
            currentAnim = (animations != null) ? animations[UP_TO_FALL] : currentAnim;
        } else {
            currentAnim = (animations != null) ? animations[FALL] : currentAnim;
        }

        return Pair.of(currentAnim, currentDir);
    }

}

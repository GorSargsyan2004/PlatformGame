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
    protected double gravity = 0.04;
    protected double jumpSpeed = -3.5;
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

    protected void drawHitbox(Graphics g) {
        // For debugging the hitbox
        if (hitBox != null) {
            g.setColor(Color.RED);
            g.drawRect((int) hitBox.x, (int) hitBox.y, (int) hitBox.width, (int) hitBox.height);
        }
    }

    protected void initHitbox() {
        // To be overridden by subclasses
    }

    public void update() {}

    protected void updateHitbox() {
        if (hitBox != null) {
            hitBox.x = (float) pos.x;
            hitBox.y = (float) pos.y;
        }
    }

    public void setLeft(boolean left) { this.leftPressed = left; }
    public void setRight(boolean right) { this.rightPressed = right; }
    public void setAttack(boolean attack) { this.attack = attack; }
    public void setJump(boolean jump) {
        if (jump && !inAir && !landing) {
            this.inAir = true;
            this.ySpeed = this.jumpSpeed;
        }
    }

    protected void takeHit(Entity entity) {
        attackersTimeAttackedInMillis.add(System.currentTimeMillis());
        attackers.add(entity);
    }

    public void changeHealth(int value) {
        health += value;
        if (health <= 0) health = 0;
    }

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

    protected void idle(Animation idleAnimation) {
        currentAnim = idleAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            isIdle = false;
            currentAnim.reset();
        }
    }

    protected void dead(Animation deadAnimation) {
        currentAnim = deadAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            isDead = true;
            currentAnim.setIndexToLastFrame();
        }
    }

    protected boolean isInAttackRange(Entity attacker, Entity attacked) {
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
        return (xDiff <= attacker.attackDistance && yDiff <= attacker.attackDistance / 2);
    }

    protected boolean isBeingAttacked() {
        return !attackers.isEmpty();
    }

    protected boolean isAttackedBy(Entity entity) {
        return attackers.contains(entity);
    }

    protected void landing(Animation landingAnimation) {
        currentAnim = landingAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()) {
            landing = false;
            currentAnim.reset();
        }
    }

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

    protected void updateHitbox(Direction currentDir, Animation currentAnim, float SCALE) {
        if (hitBox == null) return;
        if (currentDir == Direction.RIGHT) {
            hitBox.x = (float) pos.x + xDrawOffset;
        } else {
            hitBox.x = (float) pos.x + (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
        }
        hitBox.y = (float)pos.y + yDrawOffset;
    }

    public Point2D.Double getCenter() {
        if (hitBox == null) return new Point2D.Double(pos.x, pos.y);
        return new Point2D.Double(hitBox.x + hitBox.width / 2, hitBox.y + hitBox.height / 2);
    }

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

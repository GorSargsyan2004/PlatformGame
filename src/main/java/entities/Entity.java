package entities;

import animations.Animation;
import animations.Direction;
import utils.HelpMethods;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import io.arxila.javatuples.Pair;

import static utils.Constants.PlayerConstants.*;

public class Entity {
    private int health;
    private int damage;
    protected Animation[] animations;
    protected Rectangle2D.Float hitBox;
    protected int[][] lvlData;

    // Physics & State Variables
    protected boolean leftPressed, rightPressed, attack;
    protected boolean inAir = false;
    protected boolean landing = false;
    protected boolean onSlope = false;
    protected double ySpeed = 0;
    protected double gravity = 0.04;
    protected double jumpSpeed = -3.5;

    protected float xDrawOffset;
    protected float yDrawOffset;

    protected int entityWidth;
    protected int entityHeight;

    public Point2D.Double pos;
    public double movementSpeed;

    Entity(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        this.health = health;
        this.damage = damage;
        this.pos = pos;
        this.movementSpeed = movementSpeed;
        this.lvlData = lvlData;

        this.hitBox = null;
    }

    protected void drawHitbox(Graphics g) {
        // For debugging the hitbox
        g.setColor(Color.RED);
        g.drawRect((int)hitBox.x, (int)hitBox.y, (int)hitBox.width, (int)hitBox.height);
    }

    protected void initHitbox() {
        hitBox = null;
    }

    protected void updateHitbox() {
        hitBox.x = (int)pos.x;
        hitBox.y = (int)pos.y;
    }

    protected void physicsUpdate() {
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
                        animations[utils.Constants.PlayerConstants.CROUCH].reset();
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
            // ... (rest of the method unchanged)

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

            if (canMove) {
                pos.x += xSpeed;
                if (onSlope) pos.y = nextY - yDrawOffset;
                currentAnim = animations[RUN];
            } else {
                // Blocked by a wall, snap to it
                float currentXOffset = (currentDir == Direction.RIGHT) ? xDrawOffset : (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
                pos.x = utils.HelpMethods.GetEntityXPosNextToWall(hitBox, xSpeed) - currentXOffset;
                currentAnim = animations[IDLE];
            }
            updateHitbox();
        } else {
            currentAnim = animations[IDLE];
        }
        return Pair.of(currentAnim, currentDir);
    }

    protected void updateHitbox(Direction currentDir, Animation currentAnim, float SCALE) {
        if (currentDir == Direction.RIGHT) {
            hitBox.x = (float) pos.x + xDrawOffset;
        } else {
            hitBox.x = (float) pos.x + (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
        }
        hitBox.y = (float)pos.y + yDrawOffset;
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

            if (canMove) {
                pos.x += xSpeed;
            } else {
                float currentXOffset = (currentDir == Direction.RIGHT) ? xDrawOffset : (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
                pos.x = HelpMethods.GetEntityXPosNextToWall(hitBox, xSpeed) - currentXOffset;
            }
            updateHitbox();
        }

        if (ySpeed < -0.5) {
            currentAnim = animations[JUMP];
        } else if (ySpeed >= -0.5 && ySpeed < 0.5) {
            currentAnim = animations[UP_TO_FALL];
        } else {
            currentAnim = animations[FALL];
        }

        return Pair.of(currentAnim, currentDir);
    }
}

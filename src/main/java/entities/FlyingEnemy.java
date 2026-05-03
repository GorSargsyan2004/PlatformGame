package entities;

import utils.Direction;
import main.Game;
import utils.HelpMethods;

import java.awt.geom.Point2D;
import java.util.Random;

public class FlyingEnemy extends Enemy {
    protected boolean isChasing = false;
    protected boolean isFlyingAround = true;
    protected boolean isEntering = true;
    private Random rnd;

    private long stateTimer;
    private long stateDuration;
    
    private double targetVelX;
    private double targetVelY;
    private long lastVelChange;
    private long lastAttackTime;

    FlyingEnemy(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);
        rnd = new Random();
        resetStateTimer();
    }

    private void resetStateTimer() {
        stateTimer = System.currentTimeMillis();
        stateDuration = (rnd.nextInt(11) + 5) * 1000; // 5 to 15 seconds
    }

    @Override
    protected void chase(Entity player) {
        if (player == null || player.isDead || isDead || health <= 0) return;

        if (attack) {
            // Face the player while attacking
            if (player.hitBox.x > hitBox.x) currentDir = Direction.RIGHT;
            else currentDir = Direction.LEFT;
            targetVelX = 0;
            targetVelY = 0;
            lastAttackTime = System.currentTimeMillis();
            return;
        }

        // Stay still for 1 second after attacking
        if (!isChasing && !isFlyingAround && !isEntering) {
            if (System.currentTimeMillis() - lastAttackTime >= 1000) {
                isFlyingAround = true;
                resetStateTimer();
            } else {
                targetVelX = 0;
                targetVelY = 0;
                return;
            }
        }

        if (isEntering) {
            // Fly into the screen towards the player's general area
            double targetX = (pos.x < 0) ? 100 * Game.SCALE : (pos.x > Game.GAME_WIDTH) ? Game.GAME_WIDTH - 150 * Game.SCALE : pos.x;
            double targetY = player.hitBox.y - 150 * Game.SCALE;

            double diffX = targetX - pos.x;
            double diffY = targetY - pos.y;
            double dist = Math.sqrt(diffX * diffX + diffY * diffY);

            if (dist > 5) {
                targetVelX = (diffX / dist) * movementSpeed;
                targetVelY = (diffY / dist) * movementSpeed;
            }

            if (pos.x >= 0 && pos.x + hitBox.width <= Game.GAME_WIDTH) {
                isEntering = false;
                isFlyingAround = true;
                resetStateTimer();
            }
        } else if (isFlyingAround) {
            flyAround(player);
            if (System.currentTimeMillis() - stateTimer >= stateDuration) {
                isFlyingAround = false;
                isChasing = true;
                resetStateTimer();
            }
        } else if (isChasing) {
            double diffX = (player.hitBox.x + player.hitBox.width / 2) - (hitBox.x + hitBox.width / 2);
            double diffY = (player.hitBox.y + player.hitBox.height / 2) - (hitBox.y + hitBox.height / 2);
            double dist = Math.sqrt(diffX * diffX + diffY * diffY);

            if (isInAttackRange(this, player)) {
                if (!player.isAttackedBy(this)) player.takeHit(this);
                setAttack(true);
                isChasing = false; 
            } else {
                if (dist != 0) {
                    targetVelX = (diffX / dist) * movementSpeed;
                    targetVelY = (diffY / dist) * movementSpeed;
                }
            }
        }

        physicsUpdate();
    }

    protected void flyAround(Entity player) {
        if (System.currentTimeMillis() - lastVelChange >= 1000 + rnd.nextInt(1000)) {
            targetVelX = (rnd.nextDouble() * 2 - 1) * movementSpeed;
            targetVelY = (rnd.nextDouble() * 2 - 1) * movementSpeed;
            lastVelChange = System.currentTimeMillis();
        }

        float preferredY = (float) (player.hitBox.y - 150 * Game.SCALE);
        if (hitBox.y > preferredY + 50 * Game.SCALE) targetVelY = -movementSpeed;
        else if (hitBox.y < preferredY - 50 * Game.SCALE) targetVelY = movementSpeed;

        float distToPlayerX = (float) Math.abs(hitBox.x - player.hitBox.x);
        if (distToPlayerX < 100 * Game.SCALE) {
            if (hitBox.x < player.hitBox.x) targetVelX = -movementSpeed;
            else targetVelX = movementSpeed;
        }
    }

    protected void physicsUpdate() {
        boolean wasInsideX = (hitBox.x >= 0 && hitBox.x + hitBox.width <= Game.GAME_WIDTH);
        boolean wasInsideY = (hitBox.y >= 0 && hitBox.y + hitBox.height <= Game.GAME_HEIGHT);

        // Move X
        if (HelpMethods.CanMoveHere(new Point2D.Double(hitBox.x + targetVelX, hitBox.y), hitBox.width, hitBox.height, lvlData)) {
            boolean canMoveX = true;
            if (wasInsideX) {
                if (hitBox.x + targetVelX < 0 || hitBox.x + targetVelX + hitBox.width > Game.GAME_WIDTH)
                    canMoveX = false;
            } else {
                // If outside, only allow moving towards the screen
                if (hitBox.x < 0 && targetVelX < 0) canMoveX = false;
                else if (hitBox.x + hitBox.width > Game.GAME_WIDTH && targetVelX > 0) canMoveX = false;
            }

            if (canMoveX) {
                pos.x += targetVelX;
            } else {
                targetVelX *= -1;
            }
        } else {
            targetVelX *= -1;
        }

        // Move Y
        if (HelpMethods.CanMoveHere(new Point2D.Double(hitBox.x, hitBox.y + targetVelY), hitBox.width, hitBox.height, lvlData)) {
            boolean canMoveY = true;
            if (wasInsideY) {
                if (hitBox.y + targetVelY < 0 || hitBox.y + targetVelY + hitBox.height > Game.GAME_HEIGHT)
                    canMoveY = false;
            } else {
                // If outside, only allow moving towards the screen
                if (hitBox.y < 0 && targetVelY < 0) canMoveY = false;
                else if (hitBox.y + hitBox.height > Game.GAME_HEIGHT && targetVelY > 0) canMoveY = false;
            }

            if (canMoveY) {
                pos.y += targetVelY;
            } else {
                targetVelY *= -1;
            }
        } else {
            targetVelY *= -1;
        }

        updateHitbox();

        if (targetVelX > 0) currentDir = Direction.RIGHT;
        else if (targetVelX < 0) currentDir = Direction.LEFT;
    }

    @Override
    protected void updateHitbox() {
        if (hitBox == null) return;
        
        if (currentDir == Direction.RIGHT) {
            hitBox.x = (float) pos.x + xDrawOffset;
        } else {
            hitBox.x = (float) pos.x + (currentAnim.getWidth() * Game.SCALE - xDrawOffset - hitBox.width);
        }
        hitBox.y = (float) pos.y + yDrawOffset;
    }
}

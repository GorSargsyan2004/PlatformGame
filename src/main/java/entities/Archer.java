package entities;

import animations.Animation;
import utils.Direction;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.ArcherConstants.*;
import static utils.HelpMethods.CanMoveHere;

public class Archer extends Allay {
    private static final float SCALE = scale + 0.2f;
    private static int animWidthDiff;

    Archer(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);

        this.attackDistance = (int) (30 * SCALE);
        this.shootingDistance = (int) (120 * SCALE);

        this.entityHeight = (int) (35 * SCALE);
        this.entityWidth = (int) (25 * SCALE);

        this.xDrawOffset = (int) (50 * SCALE);
        this.yDrawOffset = (int) (43 * SCALE);

        initAnimations();

        initHitbox();
    }

    private void initAnimations() {
        String[] anims = {"Death", "Dash", "Attack", "Close_Attack", "Idle", "JumpAndFall", "Death", "Run"};

        animations = new Animation[CROUCH+1];

        animations[DEATH] = new Animation("/Allay/Archer/Archer-"+anims[DEATH]+".png", 0, 0, 76, 128, 24, 0);
        animations[DASH] = new Animation("/Allay/Archer/Archer-"+anims[DASH]+".png", 0, 0, 76, 128, 14, 0);
        animations[ATTACK] = new Animation("/Allay/Archer/Archer-"+anims[ATTACK]+".png", 0, 0, 76, 180, 14, 0);
        animations[CLOSE_ATTACK] = new Animation("/Allay/Archer/Archer-"+anims[CLOSE_ATTACK]+".png", 0, 0, 76, 128, 28, 0);
        animations[IDLE] = new Animation("/Allay/Archer/Archer-"+anims[IDLE]+".png", 0, 0, 76, 128, 8, 0);
        animations[JUMP] = new Animation("/Allay/Archer/Archer-"+anims[JUMP]+".png", 0, 0, 76, 128, 12, 0);
        animations[HURT] = new Animation("/Allay/Archer/Archer-"+anims[HURT]+".png", 0, 0, 76, 128, 9, 0);
        animations[RUN] = new Animation("/Allay/Archer/Archer-"+anims[RUN]+".png", 0, 0, 76, 128, 8, 0);

        animations[CLOSE_ATTACK].modifySpeed(-10);
        animations[DEATH].modifySpeed(2);
        animations[ATTACK].modifySpeed(4);

        animations[JUMPING] = animations[JUMP].getSubAnimation(0, 1);
        animations[UP_TO_FALL] = animations[JUMP].getSubAnimation(2, 4);
        animations[FALL] = animations[JUMP].getSubAnimation(5, 6);
        animations[CROUCH] = animations[JUMP].getSubAnimation(7, 11);

        animWidthDiff = (int)((animations[ATTACK].getWidth() - animations[IDLE].getWidth()) * SCALE);
        currentAnim = animations[IDLE];
        currentDir = Direction.RIGHT;
    }

    @Override
    protected void initHitbox() {
        hitBox = new Rectangle2D.Float((float)pos.x + xDrawOffset, (float)pos.y + yDrawOffset, entityWidth, entityHeight);
    }

    @Override
    public void update() {
        if (health <= 0) {
            dead(animations[DEATH]);
            return;
        }

        if (dash) {
            escape(animations[DASH]);
            return;
        }

        if (isHurt && inAir)
            isHurt = false;

        // Reset escape state when landing
        if (isEscaping && !inAir && !landing) {
            isEscaping = false;
            setLeft(false);
            setRight(false);
        }

        // Action Locking & State Reset
        if ((attack || shooting) && (inAir || landing)) {
            attack = false;
            shooting = false;
        }

        // State Selection
        if (checkForTakingHit()) {
            takeHit(animations[HURT]);
            isEscaping = false; // Cancel escape if hit
        } else if (isHurt) {
            currentAnim.updateAnimationTick();
            if (currentAnim.isAnimationCompleted()) {
                isHurt = false;
                currentAnim.reset();
                
                // Trigger dash escape in opposite direction
                dash = true;
                currentDir = (currentDir == Direction.RIGHT) ? Direction.LEFT : Direction.RIGHT;
            }
        } else if (attack) {
            attack(animations[CLOSE_ATTACK]);
        } else if (shooting) {
            shoot(animations[ATTACK]);
        } else if (isIdle) {
            idle(animations[IDLE]);
        } else if (landing) {
            landing(animations[CROUCH]);
        } else if (inAir) {
            var pair = jump(currentAnim, currentDir, JUMPING, UP_TO_FALL, FALL, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        } else {
            var pair = run(currentAnim, currentDir, RUN, IDLE, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        }

        // Physics & Gravity Update
        if (!isIdle && !attack && !shooting && !isHurt) {
            physicsUpdate(HURT);
        }

        // Update Visuals
        boolean alreadyUpdated = (isIdle || attack || shooting || landing || isHurt);
        if (!alreadyUpdated) {
            currentAnim.updateAnimationTick();
        }

        updateHitbox();
    }

    @Override
    protected void attack(Animation attackAnimation) {
        currentAnim = attackAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            attack = false;
            currentAnim.reset();

            if (attackAnimation == animations[CLOSE_ATTACK]) {
                isEscaping = true;
                setJump(true);
                // Jump in opposite direction
                if (currentDir == Direction.RIGHT) {
                    setLeft(true);
                    setRight(false);
                } else {
                    setRight(true);
                    setLeft(false);
                }
            } else {
                isIdle = true;
            }
        }
    }

    private void escape(Animation animation) {
        currentAnim = animation;
        boolean escape = dash;
        float xSpeed = (float) (movementSpeed * 1.5);
        if (currentDir == Direction.LEFT)
            xSpeed *= -1;

        // 1. Check if we hit a wall OR a slope from the side
        boolean blocked = !CanMoveHere(new Point2D.Double(hitBox.x + xSpeed, hitBox.y), hitBox.width, hitBox.height, lvlData);

        if (!blocked) {
            // Check front tiles for slopes (which CanMoveHere ignores)
            float xCheck = (currentDir == Direction.RIGHT) ? hitBox.x + hitBox.width + xSpeed : hitBox.x + xSpeed;
            float yCheckLower = hitBox.y + hitBox.height - 5;
            float yCheckUpper = hitBox.y + 5;

            if (isTileBlocking(xCheck, yCheckLower) || isTileBlocking(xCheck, yCheckUpper)) {
                blocked = true;
            }
        }

        if (!blocked) {
            pos.x += xSpeed;
        } else {
            // Hit a wall or slope: Snap to it and terminate the slide or dash
            float currentXOffset = (currentDir == Direction.RIGHT) ? xDrawOffset : (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
            pos.x = utils.HelpMethods.GetEntityXPosNextToWall(hitBox, xSpeed) - currentXOffset;
            escape = false;
        }

        // 2. Keep the player on the ground/slopes and check for falling
        physicsUpdate(CROUCH);
        if (inAir && !onSlope) escape = false;

        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted() || !escape) {
            escape = false;
            currentAnim.reset();
        }

        if (!escape) dash = escape;
        updateHitbox();
    }

    private boolean isTileBlocking(float x, float y) {
        int xIndex = (int) (x / main.Game.TILES_SIZE);
        int yIndex = (int) (y / main.Game.TILES_SIZE);
        if (xIndex < 0 || xIndex >= main.Game.TILES_IN_WIDTH || yIndex < 0 || yIndex >= main.Game.TILES_IN_HEIGHT)
            return false;
        int tileValue = lvlData[yIndex][xIndex];
        return tileValue != LoadSave.BLANK_TILE_ID;
    }

    @Override
    protected void updateHitbox() {
        float x = (float) pos.x;
        if (currentDir == Direction.LEFT && currentAnim == animations[ATTACK]) {
            x -= animWidthDiff;
        }

        if (currentDir == Direction.RIGHT) {
            hitBox.x = x + xDrawOffset;
        } else {
            hitBox.x = x + (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
        }
        hitBox.y = (float)pos.y + yDrawOffset;
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imageToDraw = currentAnim.getAnimationImage(currentDir);
        int x = (int)pos.x;
        int y = (int)pos.y;
        if (currentDir == Direction.LEFT && currentAnim == animations[ATTACK])
            x -= animWidthDiff;

        g.drawImage(imageToDraw, x, y,
                (int)(currentAnim.getWidth()*SCALE), (int)(currentAnim.getHeight()*SCALE), null);

        // For debugging the hitBox
//        drawHitbox(g);
    }
}

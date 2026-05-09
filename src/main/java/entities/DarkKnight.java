package entities;

import animations.Animation;
import utils.Direction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.DarkKnightConstants.*;
import static utils.Constants.NightBorneConstants.ATTACK;

public class DarkKnight extends Enemy {
    private static final float SCALE = scale + 0.4f;

    DarkKnight(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);

        this.attackDistance = (int) (35 * SCALE);

        this.entityHeight = (int)(30 * SCALE);
        this.entityWidth = (int)(18 * SCALE);

        this.xDrawOffset = (int)(15 * SCALE);
        this.yDrawOffset = (int)(5 * SCALE);

        this.deathScore = (health + damage) / 2;

        initAnimations();

        initHitbox();
    }

    private void initAnimations() {
        String[] anims = {"Attack", "Death", "Idle", "Hurt", "Run", "Jump", "Up-To-Fall", "Fall", "Crouch"};

        animations = new Animation[anims.length];
        for (int i = 0; i < anims.length; i++)
            animations[i] = new Animation("/Enemy/DarkKnight/"+anims[i]+"/", "");

        animations[ATTACK].modifySpeed(-6);

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

        // Action Locking & State Reset
        if (attack && (inAir || landing)) attack = false;

        // State Selection
        if (checkForTakingHit()) {
            takeHit(animations[HURT]);
        } else if (isHurt) {
            currentAnim.updateAnimationTick();
            if (currentAnim.isAnimationCompleted()) {
                isHurt = false;
                currentAnim.reset();
            }
        } else if (isIdle) {
            idle(animations[IDLE]);
        } else if (attack) {
            attack(animations[ATTACK]);
        } else if (isOutOfBorders(currentAnim.getWidth())) {
            updateFromCorners(animations[RUN]);
            ySpeed = 0;
            inAir = false;
        } else if (landing) {
            landing(animations[CROUCH]);
        } else if (inAir) {
            var pair = jump(currentAnim, currentDir, JUMP, UP_TO_FALL, FALL, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        } else {
            var pair = run(currentAnim, currentDir, RUN, IDLE, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        }

        // Physics & Gravity Update
        if (!isOutOfBorders(currentAnim.getWidth()) && !isIdle && !attack && !isHurt) {
            physicsUpdate(HURT);
        }

        // Update Visuals
        boolean alreadyUpdated = (isIdle || attack || landing || isHurt);
        if (!alreadyUpdated) {
            currentAnim.updateAnimationTick();
        }

        updateHitbox();
    }

    @Override
    protected void updateHitbox() {
        if (currentDir == Direction.RIGHT) {
            hitBox.x = (float) pos.x + xDrawOffset;
        } else {
            hitBox.x = (float) pos.x + (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
        }
        hitBox.y = (float)pos.y + yDrawOffset;
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imageToDraw = currentAnim.getAnimationImage(currentDir);
        g.drawImage(imageToDraw, (int)pos.x, (int)pos.y,
                (int)(currentAnim.getWidth()*SCALE), (int)(currentAnim.getHeight()*SCALE), null);

        // For debugging the hitBox
//        drawHitbox(g);
    }
}

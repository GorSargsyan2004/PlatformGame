package entities;

import animations.Animation;
import animations.Direction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.SkeletonConstants.*;

public class Skeleton extends Enemy{
    private static final float SCALE = scale;

    public Skeleton(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);

        this.attackDistance = (int) (30 * SCALE);

        this.entityHeight = 50;
        this.entityWidth = 40;

        this.xDrawOffset = 20;
        this.yDrawOffset = 10;

        this.deathScore = (health + damage) / 2;

        initAnimations();

        initHitbox();
    }

    private void initAnimations() {
        String[] anims = {"Attack", "Death", "Idle", "Take_Hit", "Walk", "Shield"};

        animations = new Animation[anims.length];
        animations[ATTACK] = new Animation("/Enemy/Skeleton/"+anims[ATTACK]+".png", 40, 40, entityHeight+10, entityWidth + 60, 8, 50);
        for (int i = 1; i < animations.length; i++)
            animations[i] = new Animation("/Enemy/Skeleton/"+anims[i]+".png", 40, 40, entityHeight+10, entityWidth + 60, 4, 50);


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
            takeHit(animations[TAKE_HIT]);
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
            updateFromCorners(animations[WALK]);
            ySpeed = 0;
            inAir = false;
        } else if (landing) {
            landing(animations[SHIELD]);
        } else if (inAir) {
            var pair = jump(currentAnim, currentDir, WALK, WALK, WALK, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        } else {
            var pair = run(currentAnim, currentDir, WALK, IDLE, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        }

        // Physics & Gravity Update
        if (!isOutOfBorders(currentAnim.getWidth()) && !isIdle && !attack && !isHurt) {
            physicsUpdate(TAKE_HIT);
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

    public void draw(Graphics g) {
        BufferedImage imageToDraw = currentAnim.getAnimationImage(currentDir);
        g.drawImage(imageToDraw, (int)pos.x, (int)pos.y,
                (int)(currentAnim.getWidth()*SCALE), (int)(currentAnim.getHeight()*SCALE), null);

        // For debugging the hitBox
//        drawHitbox(g);
    }
}

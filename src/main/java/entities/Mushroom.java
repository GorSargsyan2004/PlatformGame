package entities;

import animations.Animation;
import animations.Direction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static main.Game.SCALE;
import static utils.Constants.GoblinAndMushroomConstants.*;

public class Mushroom extends Enemy {

    Mushroom(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);

        this.attackDistance = (int) (20 * SCALE);

        this.entityHeight = 35;
        this.entityWidth = 25;

        this.xDrawOffset = 15;
        this.yDrawOffset = 10;

        this.deathScore = (health + damage) / 2;

        initAnimations();

        initHitbox();
    }

    private void initAnimations() {
        String[] anims = {"Take_Hit", "Death", "Idle", "Run", "Attack"};

        animations = new Animation[anims.length];
        for (int i = 0; i < anims.length; i++) {
            if (i < 3)
                animations[i] = new Animation("/Enemy/Mushroom/"+anims[i]+".png", 45, 55, entityHeight + 15, entityWidth + 35, 4, 90);
            else animations[i] = new Animation("/Enemy/Mushroom/"+anims[i]+".png", 45, 55, entityHeight + 15, entityWidth + 35, 8, 90);
        }

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
            updateFromCorners(animations[RUN]);
            ySpeed = 0;
            inAir = false;
        } else if (landing) {
            landing(animations[RUN]);
        } else if (inAir) {
            var pair = jump(currentAnim, currentDir, RUN, RUN, RUN, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        } else {
            var pair = run(currentAnim, currentDir, RUN, IDLE, SCALE);
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

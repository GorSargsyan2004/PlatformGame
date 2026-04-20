package entities;

import animations.Animation;
import animations.Direction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import static utils.Constants.KnightConstants.*;

public class Knight extends Allay {
    private static final float SCALE = scale + 0.2f;
    private Random rnd;
    private int attack_index = 0;

    Knight(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);
        rnd = new Random();
        this.attackDistance = (int) (30 * SCALE);

        this.entityHeight = (int) (35 * SCALE);
        this.entityWidth = (int) (25 * SCALE);

        this.xDrawOffset = (int) (14 * SCALE);
        this.yDrawOffset = (int) (10 * SCALE);

        initAnimations();

        initHitbox();
    }

    private void initAnimations() {
        String[] anims = {"IDLE", "RUN", "ATTACK_1", "ATTACK_2", "ATTACK_3", "DEATH", "JUMP", "HURT"};

        animations = new Animation[anims.length];

        animations[ATTACK_1] = new Animation("/Allay/Knight/"+anims[ATTACK_1]+".png", 23, 16, 45, 50, 6, 46);
        animations[ATTACK_2] = new Animation("/Allay/Knight/"+anims[ATTACK_2]+".png", 23, 16, 45, 50, 5, 46);
        animations[ATTACK_3] = new Animation("/Allay/Knight/"+anims[ATTACK_3]+".png", 23, 16, 45, 50, 6, 46);
        animations[IDLE] = new Animation("/Allay/Knight/"+anims[IDLE]+".png", 23, 16, 45, 50, 7, 46);
        animations[RUN] = new Animation("/Allay/Knight/"+anims[RUN]+".png", 23, 16, 45, 50, 8, 46);
        animations[DEATH] = new Animation("/Allay/Knight/"+anims[DEATH]+".png", 23, 16, 45, 50, 12, 46);
        animations[JUMP] = new Animation("/Allay/Knight/"+anims[JUMP]+".png", 23, 16, 45, 50, 5, 46);
        animations[HURT] = new Animation("/Allay/Knight/"+anims[HURT]+".png", 23, 16, 45, 50, 4, 46);

        currentAnim = animations[IDLE];
        currentDir = Direction.RIGHT;
    }

    @Override
    protected void initHitbox() {
        hitBox = new Rectangle2D.Float((float)pos.x + xDrawOffset, (float)pos.y + yDrawOffset, entityWidth, entityHeight);
    }

    @Override
    public void update() {
        if (!attack) attack_index = 0;
        if (attack && attack_index == 0)
           attack_index = rnd.nextInt(ATTACK_1, ATTACK_3 + 1);

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
            attack(animations[attack_index]);
        } else if (landing) {
            landing(animations[RUN]);
        } else if (inAir) {
            var pair = jump(currentAnim, currentDir, JUMP, JUMP, JUMP, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        } else {
            var pair = run(currentAnim, currentDir, RUN, IDLE, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        }

        // Physics & Gravity Update
        if (!isIdle && !attack && !isHurt) {
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

    public void draw(Graphics g) {
        BufferedImage imageToDraw = currentAnim.getAnimationImage(currentDir);
        g.drawImage(imageToDraw, (int)pos.x, (int)pos.y,
                (int)(currentAnim.getWidth()*SCALE), (int)(currentAnim.getHeight()*SCALE), null);

        // For debugging the hitBox
//        drawHitbox(g);
    }
}

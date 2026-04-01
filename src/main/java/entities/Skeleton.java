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

        this.entityHeight = 50;
        this.entityWidth = 40;

        this.xDrawOffset = 20;
        this.yDrawOffset = 10;

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
        // 1. Movement Logic (Off-screen or AI)
        if (isOutOfBorders(currentAnim.getWidth())) {
            updateFromCorners(animations[WALK]);
            // RESET PHYSICS while out of borders to prevent falling
            ySpeed = 0;
            inAir = false;
        } else if (attack) {
            attack();
            return;
        } else if (landing) {
            landing();
        } else if (inAir) {
            var pair = jump(currentAnim, currentDir, WALK, WALK, WALK, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        } else {
            var pair = run(currentAnim, currentDir, WALK, IDLE, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        }

        // 2. Physics & Gravity Update (Only if not handled by updateFromCorners)
        if (!isOutOfBorders(currentAnim.getWidth())) {
            physicsUpdate(TAKE_HIT);
        }

        currentAnim.updateAnimationTick();
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

        // FOr debugging the hitBox
//        drawHitbox(g);
    }

    private void attack() {
        currentAnim = animations[ATTACK];
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            attack = false;
            currentAnim.reset();
        }
    }

    private void landing() {
        currentAnim = animations[SHIELD];
        if (currentAnim.isAnimationCompleted()) {
            landing = false;
            currentAnim.reset();
        }
    }
}

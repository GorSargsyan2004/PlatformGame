package entities;

import animations.Animation;
import animations.Direction;

import java.awt.geom.Point2D;

import static utils.Constants.SkeletonConstants.*;

public class Skeleton extends Entity {

    private static final float SCALE = scale - 0.5f;

    public Skeleton(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);

        this.entityHeight = (int)(50 * SCALE);
        this.entityWidth = (int)(50 * SCALE);

        initAnimations();

        initHitbox(0.0f, 0.0f);
    }

    private void initAnimations() {
        String[] anims = {"Walk", "Idle", "Death", "Take_Hit", "Attack"};

        animations = new Animation[anims.length];

        for (int i = 0; i < anims.length-1; i++)
            animations[i] = new Animation("/Enemy/Skeleton/"+anims[i]+".png", 58, 50, entityHeight, entityWidth, 4, 100);

        animations[ATTACK] = new Animation("/Enemy/Skeleton/"+anims[ATTACK]+".png", 58, 50, entityHeight, entityWidth, 8, 100);

        currentAnim = animations[IDLE];
        currentDir = Direction.RIGHT;
    }

    @Override
    public void update() {
        // 1. Attack Logic (Action Locking)
        if (attack && inAir) attack = false;
        if (landing && attack) attack = false;
        if (attack) {
            attack(ATTACK);
            return;
        }

        // 2. Physics & Gravity Update
        physicsUpdate();

        if (inAir) {
            // Skeleton is flying through the air
            var pair = jump(currentAnim, currentDir, WALK, WALK, WALK, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        }
        else {
            // Skeleton is safely on the ground
            var pair = run(currentAnim, currentDir, WALK, IDLE, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        }

        currentAnim.updateAnimationTick();
        updateHitbox_();
    }
}

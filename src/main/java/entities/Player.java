package entities;

import animations.Animation;
import animations.Direction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;

public class Player extends Entity {

    public Player(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);

        this.scale += 0.05f;
        
        initAnimations();

        this.entityHeight = (int)(32 * scale);
        this.entityWidth = (int)(16 * scale);

        this.xDrawOffset = (int)(18 * scale);
        this.yDrawOffset = (int)(10 * scale);

        initHitbox(xDrawOffset, yDrawOffset);
    }

    private void initAnimations() {
        String[] anims = {"Run", "Idle", "Jump", "UptoFall", "Fall", "Crouch", "Hurt-Effect", "Attack", "Dash-Attack"};

        animations = new Animation[anims.length];

        for (int i = 0; i < animations.length; i++)
            animations[i] = new Animation("/Player/"+anims[i]+"/","Warrior_"+anims[i]+"_");

        animations[JUMP].modifySpeed(1);
        animations[UP_TO_FALL].modifySpeed(1);
        animations[FALL].modifySpeed(1);
        animations[CROUCH].modifySpeed(5);

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

        // 3. Movement & Animation Selection
        if (landing) {
            // Player just hit the ground. Play crouch and freeze movement.
            landing();
        }
        else if (inAir) {
            // Player is flying through the air
            var pair = jump(currentAnim, currentDir, JUMP, UP_TO_FALL, FALL, scale);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        }
        else {
            // Player is safely on the ground (or slope) and not crouching
            var pair = run(currentAnim, currentDir, RUN, IDLE, scale);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        }

        currentAnim.updateAnimationTick();
        updateHitbox_();
    }
    

    private void landing() {
        currentAnim = animations[CROUCH];
        if (currentAnim.isAnimationCompleted()) {
            landing = false;
            currentAnim.reset();
        }
    }
}


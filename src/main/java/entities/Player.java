package entities;

import animations.Animation;
import animations.Direction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;

public class Player extends Entity {

    private Animation currentAnim;
    private Direction currentDir;

    private static final float SCALE = scale + 0.5f;

    public Player(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);

        initAnimations();

        this.entityHeight = (int)(32 * SCALE);
        this.entityWidth = (int)(16 * SCALE);

        this.xDrawOffset = (int)(18 * SCALE);
        this.yDrawOffset = (int)(10 * SCALE);

        initHitbox();
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
    protected void initHitbox() {
        hitBox = new Rectangle2D.Float((float)pos.x + xDrawOffset, (float)pos.y + yDrawOffset, entityWidth, entityHeight);
    }

    public void setLeft(boolean left) { this.leftPressed = left; }
    public void setRight(boolean right) { this.rightPressed = right; }
    public void setAttack(boolean attack) { this.attack = attack; }
    public void setJump(boolean jump) {
        if (jump && !inAir && !landing) {
            this.inAir = true;
            this.ySpeed = this.jumpSpeed;
        }
    }

    @Override
    public void update() {
        // 1. Attack Logic (Action Locking)
        if (attack && inAir) attack = false;
        if (landing && attack) attack = false;
        if (attack) {
            attack();
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
            var pair = jump(currentAnim, currentDir, JUMP, UP_TO_FALL, FALL, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
        }
        else {
            // Player is safely on the ground (or slope) and not crouching
            var pair = run(currentAnim, currentDir, RUN, IDLE, SCALE);
            currentAnim = pair.value0();
            currentDir = pair.value1();
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

    public void drawPlayer(Graphics g) {
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
        currentAnim = animations[CROUCH];
        if (currentAnim.isAnimationCompleted()) {
            landing = false;
            currentAnim.reset();
        }
    }
}


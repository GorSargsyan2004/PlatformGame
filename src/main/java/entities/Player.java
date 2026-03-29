package entities;

import animations.Animation;
import animations.Direction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;

public class Player extends Entity {

    private Animation[] animations;
    private Animation currentAnim;
    private Direction currentDir;

    // Physics & State Variables
    private boolean leftPressed, rightPressed, attack;
    private boolean inAir = false;
    private boolean landing = false;

    private double ySpeed = 0;
    private double gravity = 0.04;
    private double jumpSpeed = -3.5;
    private double floorY = 0.0;

    public Player(int health, int damage, Point2D.Double pos, double movementSpeed) {
        super(health, damage, pos, movementSpeed);


        String[] anims = {"Run", "Idle", "Jump", "UptoFall", "Fall", "Crouch", "Hurt-Effect", "Attack", "Dash-Attack"};
        animations = new Animation[anims.length];

        for (int i = 0; i < animations.length; i++)
            animations[i] = new Animation("/Player/"+anims[i]+"/","Warrior_"+anims[i]+"_");

//        animations[RUN].modifySpeed(-2);
        animations[JUMP].modifySpeed(1);
        animations[UP_TO_FALL].modifySpeed(1);
        animations[FALL].modifySpeed(1);
        animations[CROUCH].modifySpeed(5);

        currentAnim = animations[IDLE];
        currentDir = Direction.RIGHT;
    }

    public void setLeft(boolean left) { this.leftPressed = left; }
    public void setRight(boolean right) { this.rightPressed = right; }
    public void setAttack(boolean attack) { this.attack = attack; }
    public void setJump(boolean jump) {
        if (jump && !inAir && !landing) {
            this.inAir = true;
            this.ySpeed = this.jumpSpeed;
            this.floorY = pos.y;
        }
    }

    public void update() {
        // 1. Attack Logic (Action Locking)
        if (attack && inAir) attack = false;
        if (landing && attack) attack = false;
        if (attack) {
            currentAnim = animations[ATTACK];
            currentAnim.updateAnimationTick();
            if (currentAnim.isAnimationCompleted()){
                attack = false;
                currentAnim.reset();
            }
            return;
        }

        // 2. Physics & Gravity Update
        if (inAir) {
            pos.y += ySpeed;
            ySpeed += gravity;

            // Check if we hit the floor we jumped from
            if (pos.y >= floorY && ySpeed > 0) {
                pos.y = floorY;
                inAir = false;
                landing = true;
                ySpeed = 0;
                animations[CROUCH].reset();
            }
        }

        // 3. Movement & Animation Selection
        if (landing) {
            // Player just hit the ground. Play crouch and freeze movement.
            currentAnim = animations[CROUCH];
            if (currentAnim.isAnimationCompleted()) {
                landing = false;
                currentAnim.reset();
            }
        }
        else if (inAir) {
            // Player is flying through the air

            // Allow left/right movement while in the air
            if (leftPressed) { pos.x -= movementSpeed; currentDir = Direction.LEFT; }
            if (rightPressed) { pos.x += movementSpeed; currentDir = Direction.RIGHT; }

            // Pick the animation based strictly on the current velocity
            if (ySpeed < -0.5) {
                currentAnim = animations[JUMP];
            } else if (ySpeed >= -0.5 && ySpeed < 0.5) {
                currentAnim = animations[UP_TO_FALL];
            } else {
                currentAnim = animations[FALL];
            }
        }
        else {
            // Player is safely on the ground and not crouching
            if (leftPressed && !rightPressed) {
                pos.x -= movementSpeed;
                currentAnim = animations[RUN];
                currentDir = Direction.LEFT;
            } else if (rightPressed && !leftPressed) {
                pos.x += movementSpeed;
                currentAnim = animations[RUN];
                currentDir = Direction.RIGHT;
            } else {
                currentAnim = animations[IDLE];
            }
        }

        // Always tick the animation forward
        currentAnim.updateAnimationTick();
    }

    public void drawPlayer(Graphics g) {
        BufferedImage imageToDraw = currentAnim.getAnimationImage(currentDir);
        g.drawImage(imageToDraw, (int)pos.x, (int)pos.y,
                currentAnim.getWidth()*2, currentAnim.getHeight()*2, null);
    }
}

package entities;

import animations.Animation;
import animations.Direction;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static main.Game.SCALE;
import static utils.Constants.FlyingEyeConstants.*;

public class FlyingEye extends FlyingEnemy {
    FlyingEye(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData) {
        super(health, damage, pos, movementSpeed, lvlData);

        this.attackDistance = (int) (30 * SCALE);

        this.entityHeight = 35;
        this.entityWidth = 35;

        this.xDrawOffset = 12;
        this.yDrawOffset = 15;

        this.deathScore = (health + damage) / 2;

        initAnimations();

        initHitbox();
    }

    private void initAnimations() {
        String[] anims = {"Attack", "Flight", "Death", "Take_Hit"};

        animations = new Animation[anims.length];
        for (int i = 0; i < animations.length; i++) {
            if (i < 2) animations[i] = new Animation("/Enemy/Flying_eye/"+anims[i]+".png", 50, 57, entityHeight + 10, entityWidth + 15, 8, 100);
            else animations[i] =  new Animation("/Enemy/Flying_eye/"+anims[i]+".png", 50, 57, entityHeight + 10, entityWidth + 15, 4, 100);
        }


        currentAnim = animations[FLIGHT];
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

        if (checkForTakingHit()) {
            takeHit(animations[TAKE_HIT]);
        } else if (isHurt) {
            currentAnim.updateAnimationTick();
            if (currentAnim.isAnimationCompleted()) {
                isHurt = false;
                currentAnim.reset();
            }
        } else if (attack) {
            attack(animations[ATTACK]);
        } else {
            currentAnim = animations[FLIGHT];
            currentAnim.updateAnimationTick();
        }

        updateHitbox();
    }

    public void draw(Graphics g) {
        BufferedImage imageToDraw = currentAnim.getAnimationImage(currentDir);
        g.drawImage(imageToDraw, (int)pos.x, (int)pos.y,
                (int)(currentAnim.getWidth()*SCALE), (int)(currentAnim.getHeight()*SCALE), null);

        // For debugging the hitBox
//        drawHitbox(g);
    }
}

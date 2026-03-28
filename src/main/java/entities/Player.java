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

    private boolean leftPressed, rightPressed;

    public Player(int health, int damage, Point2D.Double pos) {
        super(health, damage, pos);

        String[] anims = {"Run", "Idle", "Jump", "UptoFall", "Fall", "Crouch", "Hurt-Effect", "Attack", "Dash-Attack"};
        animations = new Animation[anims.length];

        for (int i = 0; i < animations.length; i++)
            animations[i] = new Animation("/Player/"+anims[i]+"/","Warrior_"+anims[i]+"_");

        currentAnim = animations[IDLE];
        currentDir = Direction.RIGHT;
    }

    public void setLeft(boolean left) { this.leftPressed = left; }
    public void setRight(boolean right) { this.rightPressed = right; }

    public void update() {
        if (leftPressed && !rightPressed) {
            pos.x -= 2.0;
            currentAnim = animations[RUN];
            currentDir = Direction.LEFT;
        } else if (rightPressed && !leftPressed) {
            pos.x += 2.0;
            currentAnim = animations[RUN];
            currentDir = Direction.RIGHT;
        } else {
            currentAnim = animations[IDLE];
        }
        currentAnim.updateAnimationTick();
    }

    public void drawPlayer(Graphics g) {
        BufferedImage imageToDraw = currentAnim.getAnimationImage(currentDir);
        g.drawImage(imageToDraw, (int)pos.x, (int)pos.y,
                currentAnim.getWidth()*2, currentAnim.getHeight()*2, null);
    }
}

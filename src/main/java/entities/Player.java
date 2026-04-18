package entities;

import animations.Animation;
import animations.Direction;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;

public class Player extends Entity {
    private static final float SCALE = scale + 0.3f;
    private EnemyManager enemyManager;

    // Status Bar UI
    private BufferedImage statusBarImg;

    private int statusBarWidth = (int) (192 * scale);
    private int statusBarHeight = (int) (58 * scale);
    private int statusBarX = (int) (10 * scale);
    private int statusBarY = (int) (10 * scale);

    private int healthBarWidth = (int) (150 * scale);
    private int healthBarHeight = (int) (4 * scale);
    private int healthBarXStart = (int) (34 * scale);
    private int healthBarYStart = (int) (14 * scale);

    private int maxHealth;
    private int healthWidth = healthBarWidth;

    public Player(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData, EnemyManager enemyManager) {
        super(health, damage, pos, movementSpeed, lvlData);
        this.enemyManager = enemyManager;
        this.maxHealth = health;
        this.attackDistance = (int) (35 * SCALE);
        this.statusBarImg = LoadSave.getSave(LoadSave.STATUS_BAR);
        this.canWalkOffScreen = false;

        initAnimations();

        this.entityHeight = (int)(32 * SCALE);
        this.entityWidth = (int)(16 * SCALE);

        this.xDrawOffset = (int)(18 * SCALE);
        this.yDrawOffset = (int)(10 * SCALE);

        initHitbox();
    }

    private void initAnimations() {
        String[] anims = {"Run", "Idle", "Jump", "UptoFall", "Fall", "Crouch", "Hurt-Effect", "Attack", "Dash-Attack", "Death"};

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

    @Override
    public void update() {
        updateHealthBar();

        // Player is dead, no other updates are needed
        if (isDead) return;
        if (health <= 0) {
            dead(animations[DEATH]);
            return;
        }

        // Action Locking
        if (attack && inAir) {
            attack = false;
            attackChecked = false;
        }
        if (landing && attack) {
            attack = false;
            attackChecked = false;
        }
        
        // State Selection
        if (checkForTakingHit()) {
            takeHit(animations[HURT]);
            updateHitbox();
            return;
        }

        if (isHurt) {
            currentAnim.updateAnimationTick();
            if (currentAnim.isAnimationCompleted()) {
                isHurt = false;
                currentAnim.reset();
            }
            updateHitbox();
            return;
        }

        if (attack) {
            attack(animations[ATTACK]);
            updateHitbox();
            return;
        }

        // Physics & Gravity Update
        physicsUpdate(CROUCH);

        // Movement & Animation Selection
        if (landing) {
            // Player just hit the ground. Play crouch and freeze movement.
            landing(animations[CROUCH]);
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

    private void updateHealthBar() {
        healthWidth = (int) ((health / (float)maxHealth) * healthBarWidth);
    }

    @Override
    protected void attack(Animation attackAnimation) {
        if (!attackChecked) {
            checkAttack();
            attackChecked = true;
        }
        currentAnim = attackAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            attack = false;
            isIdle = true;
            attackChecked = false;
            currentAnim.reset();
        }
    }

    private void checkAttack() {
        for (Skeleton skeleton : enemyManager.getSkeletons()) {
            if (isInAttackRange(this, skeleton)) {
                if (!skeleton.isBeingAttacked()) skeleton.takeHit(this);
            }
        }
        for (Goblin goblin : enemyManager.getGoblins()) {
            if (isInAttackRange(this, goblin)) {
                if (!goblin.isBeingAttacked()) goblin.takeHit(this);
            }
        }
        for (Mushroom mushroom : enemyManager.getMushrooms()) {
            if (isInAttackRange(this, mushroom)) {
                if (!mushroom.isBeingAttacked()) mushroom.takeHit(this);
            }
        }
        for (FlyingEye flyingEye : enemyManager.getFlyingEyes()) {
            if (isInAttackRange(this, flyingEye)) {
                if (!flyingEye.isBeingAttacked()) flyingEye.takeHit(this);
            }
        }
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

        drawUI(g);

        // FOr debugging the hitBox
//        drawHitbox(g);
    }

    private void drawUI(Graphics g) {
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.RED);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
    }

    public boolean isDead() {
        return isDead;
    }
}


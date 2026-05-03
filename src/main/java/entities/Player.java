package entities;

import animations.Animation;
import gamestates.Login;
import long_term_memory.UserManager;
import utils.Direction;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.CanMoveHere;
import long_term_memory.UserManager.*;

public class Player extends Entity {
    private static final float SCALE = scale + 0.3f;
    private final EnemyManager enemyManager;
    private final UserManager userManager;

    // Status Bar UI
    private final BufferedImage statusBarImg;

    private final int statusBarWidth = (int) (192 * scale);
    private final int statusBarHeight = (int) (58 * scale);
    private final int statusBarX = (int) (10 * scale);
    private final int statusBarY = (int) (10 * scale);

    private final int healthBarWidth = (int) (150 * scale);
    private final int healthBarHeight = (int) (4 * scale);
    private final int healthBarXStart = (int) (34 * scale);
    private final int healthBarYStart = (int) (14 * scale);

    private final int dashAttackBarWidth = (int) (100 * scale);
    private final int dashAttackBarHeight = (int) (2 * scale);
    private final int dashAttackBarXStart = (int) (44 * scale);
    private final int dashAttackBarYStart = (int) (34 * scale);

    private final int scoreAndTimeBarX = (int) ((statusBarX + 10) * scale);
    private final int scoreAndTimeBarY = (int) ((statusBarY + statusBarHeight + 10) * scale);
    private final long gameStartTime;

    private final int maxHealth;
    private int healthWidth = healthBarWidth;
    private int dashAttackWidth = 0;
    private final int maxDashAttackWidth = (int) (101 * scale);
    private long dashAttackTimer = 0;

    private boolean dashAttack = false;
    private boolean slide = false;
    private boolean dash = false;

    public Player(int health, int damage, Point2D.Double pos, double movementSpeed, int[][] lvlData, EnemyManager enemyManager, UserManager userManager) {
        super(health, damage, pos, movementSpeed, lvlData);
        this.enemyManager = enemyManager;
        this.userManager = userManager;
        this.maxHealth = health;
        this.attackDistance = (int) (30 * SCALE);
        this.statusBarImg = LoadSave.getSave(LoadSave.STATUS_BAR);
        this.canWalkOffScreen = false;
        this.gameStartTime = System.currentTimeMillis();

        initAnimations();

        this.entityHeight = (int)(32 * SCALE);
        this.entityWidth = (int)(16 * SCALE);

        this.xDrawOffset = (int)(18 * SCALE);
        this.yDrawOffset = (int)(10 * SCALE);

        initHitbox();
    }

    private void initAnimations() {
        String[] anims = {"Run", "Idle", "Jump", "UptoFall", "Fall", "Crouch", "Hurt-Effect", "Attack", "Dash-Attack", "Death", "Slide", "Dash"};

        animations = new Animation[anims.length];

        for (int i = 0; i < animations.length; i++)
            animations[i] = new Animation("/Player/"+anims[i]+"/","Warrior_"+anims[i]+"_");

        animations[JUMP].modifySpeed(1);
        animations[UP_TO_FALL].modifySpeed(1);
        animations[FALL].modifySpeed(1);
        animations[CROUCH].modifySpeed(5);
        animations[DASH].modifySpeed(15);

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
        updateDashAttackBar(10);

        // Player is dead, no other updates are needed
        if (isDead) return;
        if (health <= 0) {
            dead(animations[DEATH]);
            return;
        }

        // Action Locking
        if ((attack || dashAttack) && inAir) {
            attack = false;
            attackChecked = false;
        }
        if (landing && (attack || dashAttack)) {
            attack = false;
            dashAttack = false;
            attackChecked = false;
        }

        if (slide) {
            escape(animations[SLIDE]);
            return;
        }

        if (dash) {
            escape(animations[DASH]);
            return;
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

        if (dashAttack) {
            attack = true;
            attack(animations[DASH_ATTACK]);
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

    private void updateDashAttackBar(int steps) {
        if (dashAttackWidth >= maxDashAttackWidth) {
            dashAttackWidth = maxDashAttackWidth;
            return;
        }
        if (System.currentTimeMillis() - dashAttackTimer >= 1000) {
            dashAttackWidth += (int)(((float)steps / maxDashAttackWidth) * dashAttackBarWidth);
            dashAttackTimer = System.currentTimeMillis();
        }
    }

    private void escape(Animation animation) {
        currentAnim = animation;
        boolean escape = slide || dash;
        float xSpeed = (float) (movementSpeed * ((dash) ? 2 : 1.5));
        if (currentDir == Direction.LEFT)
            xSpeed *= -1;

        // 1. Check if we hit a wall OR a slope from the side
        boolean blocked = !CanMoveHere(new Point2D.Double(hitBox.x + xSpeed, hitBox.y), hitBox.width, hitBox.height, lvlData);
        
        if (!blocked) {
            // Check front tiles for slopes (which CanMoveHere ignores)
            float xCheck = (currentDir == Direction.RIGHT) ? hitBox.x + hitBox.width + xSpeed : hitBox.x + xSpeed;
            float yCheckLower = hitBox.y + hitBox.height - 5;
            float yCheckUpper = hitBox.y + 5;
            
            if (isTileBlocking(xCheck, yCheckLower) || isTileBlocking(xCheck, yCheckUpper)) {
                blocked = true;
            }
        }

        if (!blocked) {
            pos.x += xSpeed;
        } else {
            // Hit a wall or slope: Snap to it and terminate the slide or dash
            float currentXOffset = (currentDir == Direction.RIGHT) ? xDrawOffset : (currentAnim.getWidth() * SCALE - xDrawOffset - hitBox.width);
            pos.x = utils.HelpMethods.GetEntityXPosNextToWall(hitBox, xSpeed) - currentXOffset;
            escape = false;
        }

        // 2. Keep the player on the ground/slopes and check for falling
        physicsUpdate(CROUCH);
        if (inAir && !onSlope) escape = false;

        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted() || !escape) {
            escape = false;
            currentAnim.reset();
        }

        if (!escape) slide = dash = escape;
        updateHitbox();
    }



    private boolean isTileBlocking(float x, float y) {
        int xIndex = (int) (x / main.Game.TILES_SIZE);
        int yIndex = (int) (y / main.Game.TILES_SIZE);
        if (xIndex < 0 || xIndex >= main.Game.TILES_IN_WIDTH || yIndex < 0 || yIndex >= main.Game.TILES_IN_HEIGHT)
            return false;
        int tileValue = lvlData[yIndex][xIndex];
        return tileValue != LoadSave.BLANK_TILE_ID;
    }

    @Override
    protected void attack(Animation attackAnimation) {
        if (!attackChecked) {
            if (dashAttack) {
                damage *= 2;
                attackDistance *= 2;
            }
            checkAttack();
            attackChecked = true;
        }
        currentAnim = attackAnimation;
        currentAnim.updateAnimationTick();
        if (currentAnim.isAnimationCompleted()){
            attack = false;
            if (dashAttack) {
                damage /= 2;
                attackDistance /= 2;
                dashAttack = false;
                dashAttackWidth = 0;
            }
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
        // Status bar
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.RED);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
        if (dashAttackWidth >= maxDashAttackWidth)
            g.setColor(Color.YELLOW);
        else g.setColor(Color.getHSBColor(50, 25, 50));
        g.fillRect(dashAttackBarXStart + statusBarX, dashAttackBarYStart + statusBarY, dashAttackWidth, dashAttackBarHeight);

        // Score and Time
        long timePassed = (System.currentTimeMillis() - gameStartTime) / 1000;
        g.setColor(Color.WHITE);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.drawString("Time: " + timePassed, scoreAndTimeBarX + 10, scoreAndTimeBarY + 10);
        if (userManager.isPassedBestScore())
            g.setColor(Color.YELLOW);
        g.drawString("Score: " + userManager.getCurrScore(), scoreAndTimeBarX + 10, scoreAndTimeBarY + 30);
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isDashAttack() {
        return dashAttack;
    }

    public void setDashAttack(boolean dashAttack) {
        if (dashAttack && dashAttackWidth >= maxDashAttackWidth)
            this.dashAttack = true;
    }

    public boolean isSlide() {
        return slide;
    }

    public void setSlide(boolean slide) {
        if (slide && !this.slide) {
            if (dashAttackWidth >= maxDashAttackWidth / 2) {
                this.slide = true;
                dashAttackWidth -= maxDashAttackWidth / 2;
            }
        } else if (!slide) {
            this.slide = false;
        }
    }

    public void setDash(boolean dash) {
        if (dash && !this.dash) {
            if (dashAttackWidth >= maxDashAttackWidth / 3) {
                this.dash = true;
                dashAttackWidth -= maxDashAttackWidth / 3;
            }
        } else if (!dash) {
            this.dash = false;
        }
    }

    public void addScore(int adder) {
        userManager.addToCurrScore(adder);
    }

    public void saveData() {
        if (userManager.isPassedBestScore()) userManager.setRecord();
    }
}

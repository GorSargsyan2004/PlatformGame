package main;

import entities.AllayManager;
import entities.EnemyManager;
import entities.Player;
import gamestates.Playing;
import utils.Direction;

import java.awt.*;
import java.util.Random;

import static utils.Direction.LEFT;
import static utils.Direction.RIGHT;

/**
 * The GameAlgorithm class manages the spawning of enemies and allies based on difficulty.
 * it also updates and draws the player, enemies, and allies.
 */
public class GameAlgorithm {
    private AllayManager allayManager;
    private EnemyManager enemyManager;
    private Player player;

    /**
     * Enum representing the possible difficulty levels.
     */
    public enum Difficulty {
        EASY, MODERATE, HARD;
    }

    // Spawn Timers (in ticks)
    private int spawnKnightTimer = 0;
    private int spawnArcherTimer = 0;
    private int spawnSkeletonTimer = 0;
    private int spawnGoblinTimer;
    private int spawnMushroomTimer;
    private int spawnFlyingEyeTimer = 0;
    private int spawnNightBorneTimer = 0;
    private int spawnDarkKnightTimer;

    // Spawn Frequency in Ticks
    private int spawnKnightFrequency;
    private int spawnArcherFrequency;
    private int spawnSkeletonFrequency;
    private int spawnGoblinFrequency;
    private int spawnMushroomFrequency;
    private int spawnFlyingEyeFrequency;
    private int spawnNightBorneFrequency;
    private int spawnDarkKnightFrequency;

    private final Random rnd = new Random();

    /**
     * Constructs a GameAlgorithm with the specified playing state and difficulty.
     *
     * @param playing    The current playing state.
     * @param difficulty The chosen difficulty level.
     */
    public GameAlgorithm(Playing playing, Difficulty difficulty) {
        enemyManager = playing.getEnemyManager();
        allayManager = playing.getAllayManager();
        player = playing.getPlayer();

        initFrequencies(difficulty);
        initTimers();
    }

    /**
     * Initializes the timers. Timers that should trigger immediately are set to their frequency.
     */
    private void initTimers() {
        spawnGoblinTimer = spawnGoblinFrequency;
        spawnMushroomTimer = spawnMushroomFrequency;
        spawnDarkKnightTimer = spawnDarkKnightFrequency;
    }

    /**
     * Initializes spawn frequencies based on the chosen difficulty.
     *
     * @param difficulty The difficulty level.
     */
    private void initFrequencies(Difficulty difficulty) {
        switch (difficulty) {
            case EASY -> {
                spawnSkeletonFrequency = 60;
                spawnGoblinFrequency = 15;
                spawnMushroomFrequency = 45;
                spawnFlyingEyeFrequency = 75;
                spawnNightBorneFrequency = 100;
                spawnDarkKnightFrequency = 80;

                spawnKnightFrequency = 45;
                spawnArcherFrequency = 60;
            }
            case MODERATE -> {
                spawnSkeletonFrequency = 45;
                spawnGoblinFrequency = 8;
                spawnMushroomFrequency = 35;
                spawnFlyingEyeFrequency = 60;
                spawnNightBorneFrequency = 85;
                spawnDarkKnightFrequency = 65;

                spawnKnightFrequency = 60;
                spawnArcherFrequency = 75;
            }
            case HARD -> {
                spawnSkeletonFrequency = 30;
                spawnGoblinFrequency = 5;
                spawnMushroomFrequency = 35;
                spawnFlyingEyeFrequency = 40;
                spawnNightBorneFrequency = 70;
                spawnDarkKnightFrequency = 45;

                spawnKnightFrequency = 90;
                spawnArcherFrequency = 105;
            }
        }

        // Convert to ticks (UPS_SET is the number of updates per second)
        spawnSkeletonFrequency *= Game.UPS_SET;
        spawnGoblinFrequency *= Game.UPS_SET;
        spawnMushroomFrequency *= Game.UPS_SET;
        spawnFlyingEyeFrequency *= Game.UPS_SET;
        spawnNightBorneFrequency *= Game.UPS_SET;
        spawnKnightFrequency *= Game.UPS_SET;
        spawnArcherFrequency *= Game.UPS_SET;
        spawnDarkKnightFrequency *= Game.UPS_SET;
    }

    /**
     * Handles the logic for spawning various types of enemies at timed intervals.
     */
    private void spawnEnemies() {
        // Goblin
        spawnGoblinTimer++;
        if (spawnGoblinTimer >= spawnGoblinFrequency) {
            spawnGoblinTimer = 0;
            enemyManager.summonGoblin(getRndDir());
            if (spawnGoblinFrequency > 6 * Game.UPS_SET) spawnGoblinFrequency -= 3 * Game.UPS_SET;
        }
        // Flying Eye
        spawnFlyingEyeTimer++;
        if (spawnFlyingEyeTimer >= spawnFlyingEyeFrequency) {
            spawnFlyingEyeTimer = 0;
            enemyManager.summonFlyingEye(LEFT);
            if (spawnFlyingEyeFrequency > 5 * Game.UPS_SET) spawnFlyingEyeFrequency -= 2 * Game.UPS_SET;
        }
        // Mushroom
        spawnMushroomTimer++;
        if (spawnMushroomTimer >= spawnMushroomFrequency) {
            spawnMushroomTimer = 0;
            enemyManager.summonMushroom(getRndDir());
            if (spawnMushroomFrequency > 6 * Game.UPS_SET) spawnMushroomFrequency -= 3 * Game.UPS_SET;
        }
        // Skeleton
        spawnSkeletonTimer++;
        if (spawnSkeletonTimer >= spawnSkeletonFrequency) {
            spawnSkeletonTimer = 0;
            enemyManager.summonSkeleton(getRndDir());
            if (spawnSkeletonFrequency > 6 * Game.UPS_SET) spawnSkeletonFrequency -= 3 * Game.UPS_SET;
        }
        // NightBorne
        spawnNightBorneTimer++;
        if (spawnNightBorneTimer >= spawnNightBorneFrequency) {
            spawnNightBorneTimer = 0;
            enemyManager.summonNightBorne(getRndDir());
            if (spawnNightBorneFrequency > 10 * Game.UPS_SET) spawnNightBorneFrequency -= 3 * Game.UPS_SET;
        }
        // DarkKnight
        spawnDarkKnightTimer++;
        if (spawnDarkKnightTimer >= spawnDarkKnightFrequency) {
            spawnDarkKnightTimer = 0;
            enemyManager.summonDarkKnight(getRndDir());
            if (spawnDarkKnightFrequency > 8 * Game.UPS_SET) spawnDarkKnightFrequency -= 3 * Game.UPS_SET;
        }
    }

    /**
     * Handles the logic for spawning various types of allies at timed intervals.
     */
    private void spawnAllays() {
        // Knight
        spawnKnightTimer++;
        if (spawnKnightTimer >= spawnKnightFrequency) {
            spawnKnightTimer = 0;
            allayManager.summonKnight();
            spawnKnightFrequency += 2 * Game.UPS_SET;
        }
        // Archer
        spawnArcherTimer++;
        if (spawnArcherTimer >= spawnArcherFrequency) {
            spawnArcherTimer = 0;
            allayManager.summonArcher();
            spawnArcherFrequency += 2 * Game.UPS_SET;
        }
    }

    /**
     * Updates player, enemy manager, allay manager, and handles spawns.
     */
    public void update() {
        if (!player.isDead())
            player.update();

        enemyManager.update();
        allayManager.update();

        spawnEnemies();
        spawnAllays();
    }

    /**
     * Draws the player, enemies, and allies.
     *
     * @param g The Graphics object used for drawing.
     */
    public void draw(Graphics g) {
        player.draw(g);
        enemyManager.draw(g);
        allayManager.draw(g);
    }

    /**
     * Returns a random direction (LEFT or RIGHT).
     *
     * @return A random Direction.
     */
    private Direction getRndDir() {
        if (rnd.nextInt(2) == 0) return LEFT;
        return RIGHT;
    }
}

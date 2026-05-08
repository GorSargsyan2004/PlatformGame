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

public class GameAlgorithm {
    private AllayManager allayManager;
    private EnemyManager enemyManager;
    private Player player;

    public enum Difficulty {
        EASY, MODERATE, HARD;
    }

    // Spawn Timers
    private long spawnKnightTimer = System.currentTimeMillis();
    private long spawnArcherTimer = System.currentTimeMillis();
    private long spawnSkeletonTimer = 0;
    private long spawnGoblinTimer = 0;
    private long spawnMushroomTimer = 0;
    private long spawnFlyingEyeTimer = 0;
    private long spawnNightBorneTimer = 0;

    // Spawn Frequency in Milli Seconds
    private int spawnKnightFrequency;
    private int spawnArcherFrequency;
    private int spawnSkeletonFrequency;
    private int spawnGoblinFrequency;
    private int spawnMushroomFrequency;
    private int spawnFlyingEyeFrequency;
    private int spawnNightBorneFrequency;

    private final Random rnd = new Random();

    public GameAlgorithm(Playing playing, Difficulty difficulty) {
        enemyManager = playing.getEnemyManager();
        allayManager = playing.getAllayManager();
        player = playing.getPlayer();

        initFrequencies(difficulty);
    }

    private void initFrequencies(Difficulty difficulty) {
        switch (difficulty) {
            case EASY -> {
                spawnSkeletonFrequency = 60;
                spawnGoblinFrequency = 15;
                spawnMushroomFrequency = 45;
                spawnFlyingEyeFrequency = 75;
                spawnNightBorneFrequency = 100;

                spawnKnightFrequency = 45;
                spawnArcherFrequency = 60;
            }
            case MODERATE -> {
                spawnSkeletonFrequency = 45;
                spawnGoblinFrequency = 8;
                spawnMushroomFrequency = 35;
                spawnFlyingEyeFrequency = 60;
                spawnNightBorneFrequency = 85;

                spawnKnightFrequency = 60;
                spawnArcherFrequency = 75;
            }
            case HARD -> {
                spawnSkeletonFrequency = 30;
                spawnGoblinFrequency = 5;
                spawnMushroomFrequency = 35;
                spawnFlyingEyeFrequency = 40;
                spawnNightBorneFrequency = 70;

                spawnKnightFrequency = 90;
                spawnArcherFrequency = 105;
            }
        }

        // Convert to milliseconds
        spawnSkeletonFrequency *= 1_000;
        spawnGoblinFrequency *= 1_000;
        spawnMushroomFrequency *= 1_000;
        spawnFlyingEyeFrequency *= 1_000;
        spawnNightBorneFrequency *= 1_000;
        spawnKnightFrequency *= 1_000;
        spawnArcherFrequency *= 1_000;
    }

    private void spawnEnemies() {
        long currentTime = System.currentTimeMillis();

        // Goblin
        if (currentTime - spawnGoblinTimer >= spawnGoblinFrequency) {
            spawnGoblinTimer = System.currentTimeMillis();
            enemyManager.summonGoblin(getRndDir());
            if (spawnGoblinFrequency > 6_000) spawnGoblinFrequency -= 3_000;
        }
        // Flying Eye
        if (currentTime - spawnFlyingEyeTimer >= spawnFlyingEyeFrequency) {
            spawnFlyingEyeTimer = System.currentTimeMillis();
            enemyManager.summonFlyingEye(LEFT);
            if (spawnFlyingEyeFrequency > 5_000) spawnFlyingEyeFrequency -= 2_000;
        }
        // Mushroom
        if (currentTime - spawnMushroomTimer >= spawnMushroomFrequency) {
            spawnMushroomTimer = System.currentTimeMillis();
            enemyManager.summonMushroom(getRndDir());
            if (spawnMushroomFrequency > 6_000) spawnMushroomFrequency -= 3_000;
        }
        // Skeleton
        if (currentTime - spawnSkeletonTimer >= spawnSkeletonFrequency) {
            spawnSkeletonTimer = System.currentTimeMillis();
            enemyManager.summonSkeleton(getRndDir());
            if (spawnSkeletonFrequency > 6_000) spawnSkeletonFrequency -= 3_000;
        }
        // NightBorne
        if (currentTime - spawnNightBorneTimer >= spawnNightBorneFrequency) {
            spawnNightBorneTimer = System.currentTimeMillis();
            enemyManager.summonNightBorne(getRndDir());
            if (spawnNightBorneFrequency > 10_000) spawnNightBorneFrequency -= 3_000;
        }
    }

    private void spawnAllays() {
        // Knight
        if (System.currentTimeMillis() - spawnKnightTimer >= spawnKnightFrequency) {
            spawnKnightTimer = System.currentTimeMillis();
            allayManager.summonKnight();
            spawnKnightFrequency += 2000;
        }
        // Archer
        if (System.currentTimeMillis() - spawnArcherTimer >= spawnArcherFrequency) {
            spawnArcherTimer = System.currentTimeMillis();
            allayManager.summonArcher();
            spawnArcherFrequency += 2000;
        }
    }

    public void update() {
        if (!player.isDead())
            player.update();

        enemyManager.update();
        allayManager.update();

        spawnEnemies();
        spawnAllays();
    }

    public void draw(Graphics g) {
        player.draw(g);
        enemyManager.draw(g);
        allayManager.draw(g);
    }

    private Direction getRndDir() {
        if (rnd.nextInt(2) == 0) return LEFT;
        return RIGHT;
    }
}

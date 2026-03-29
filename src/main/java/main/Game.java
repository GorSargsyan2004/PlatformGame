package main;

import entities.Player;
import levels.LevelManager;

import java.awt.geom.Point2D;

public class Game implements Runnable{

    // CLASSES
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    protected LevelManager levelManager;

    // ENTITIES
    protected Player player;

    // CONSTANTS
    public static final int FPS_SET = 120;
    public static final int UPS_SET = 200;

    public final static int TILES_DEFAULT_SIZE = 32;
    public final static float SCALE = 1.0f;
    public final static int TILES_IN_WIDTH = 24;
    public final static int TILES_IN_HEIGHT = 24;
    public final static int TILES_SIZE = (int)(TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * (TILES_IN_WIDTH * 2);
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;


    Game() {
        initClasses();

        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
        startGameLoop();
    }

    private void initClasses() {
        player = new Player(100, 20, new Point2D.Double(500.0, 500.0), 1.5);
        levelManager = new LevelManager(this);
    }

    private void update() {
        player.update();
        levelManager.update();
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            // Accumulate the time passed into our update and frame "buckets"
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            // 1. UPDATE: Catch up on any missed game logic ticks
            while (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }

            // 2. RENDER: Draw the frame only if enough time has passed
            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            // 3. FPS & UPS COUNTER (Resets every 1 second)
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }
}

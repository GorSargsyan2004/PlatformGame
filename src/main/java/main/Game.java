package main;

public class Game implements Runnable{

    private GameWindow gameWindow;
    private GamePanel gamePanel;

    private Thread gameThread;
    public static final int FPS_SET = 120;
    public static final int UPS_SET = 200;

    Game() {
        gamePanel = new GamePanel();
        gameWindow = new GameWindow(gamePanel);
        gamePanel.requestFocus();
        startGameLoop();
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
                gamePanel.updateGame();
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

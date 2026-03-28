package main;

public class Game implements Runnable{

    private GameWindow gameWindow;
    private GamePanel gamePanel;

    private Thread gameThread;
    public static final int FPS_SET = 120;
    private int frames = 0;

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
        long lastFrame = System.nanoTime();
        long lastCheck = System.currentTimeMillis();

        while (true) {
            if (System.nanoTime() - lastFrame >= timePerFrame) {
                gamePanel.repaint();
                lastFrame = System.nanoTime();
                frames++;
            }


            // FPS Counter
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
    }
}

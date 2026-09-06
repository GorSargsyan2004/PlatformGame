package main;

import gamestates.Gamestate;
import gamestates.Login;
import gamestates.Menu;
import gamestates.Playing;
import music.MusicPlayer;

/**
 * The Game class is the heart of the application. It manages the game loop,
 * initializes main components, and handles state transitions.
 * It implements Runnable to run the game loop in a separate thread.
 */
public class Game implements Runnable{

    // CLASSES
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;

    private Playing playing;
    private Menu menu;
    private Login login;

    private Gamestate lastState = Gamestate.state;

    // CONSTANTS
    /** Target Frames Per Second. */
    public static final int FPS_SET = 120;
    /** Target Updates Per Second for game logic. */
    public static final int UPS_SET = 200;

    /** Default size of a tile in pixels. */
    public final static int TILES_DEFAULT_SIZE = 24;
    /** Number of tiles across the width of the screen. */
    public final static int TILES_IN_WIDTH = 48;
    /** Number of tiles across the height of the screen. */
    public final static int TILES_IN_HEIGHT = 24;

    /** Scale factor for the game display. */
    public static final float SCALE;
    /** Actual size of a tile after scaling. */
    public static final int TILES_SIZE;
    /** Total width of the game window in pixels. */
    public static final int GAME_WIDTH;
    /** Total height of the game window in pixels. */
    public static final int GAME_HEIGHT;

    static {
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        float scaleX = (float) screenSize.width / (TILES_IN_WIDTH * TILES_DEFAULT_SIZE);
        float scaleY = (float) screenSize.height / (TILES_IN_HEIGHT * TILES_DEFAULT_SIZE);
        SCALE = Math.min(scaleX, scaleY);
        TILES_SIZE = (int)(TILES_DEFAULT_SIZE * SCALE);
        GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
        GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;
    }


    /**
     * Initializes the game, classes, and starts the game loop.
     */
    Game() {
        initClasses();

        gamePanel.requestFocus();
        handleMusicChange();
        startGameLoop();
    }

    /**
     * Initializes the main game states and UI components.
     */
    private void initClasses() {
        login = new Login(this);
        menu = new Menu(this);
        playing = new Playing(this);
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
    }

    /**
     * Updates the current game state based on Gamestate.state.
     */
    private void update() {
        if (lastState != Gamestate.state) {
            handleMusicChange();
            if (Gamestate.state == Gamestate.MENU) {
                menu.updateLeaderboard();
            }
            lastState = Gamestate.state;
        }

        switch (Gamestate.state) {
            case LOGIN -> {
                login.update();
            }
            case PLAYING -> {
                playing.update();
            }
            case MENU -> {
                menu.update();
            }
            case QUIT -> {
                playing.getPlayer().saveData();
                System.exit(0);
            }
        }
    }

    /**
     * Handles music transitions based on the current game state.
     */
    private void handleMusicChange() {
        if (Gamestate.state == Gamestate.MENU || Gamestate.state == Gamestate.LOGIN) {
            MusicPlayer.pauseLevelMusic();
            MusicPlayer.playMenuMusic();
        } else if (Gamestate.state == Gamestate.PLAYING) {
            MusicPlayer.pauseMenuMusic();
            MusicPlayer.startLevelMusic();
        }
    }

    /**
     * Restarts the playing state and sets the game state to PLAYING.
     */
    public void restartGame() {
        login.getUserManager().resetCurrScore();
        playing = new Playing(this);
        Gamestate.state = Gamestate.PLAYING;
        music.MusicPlayer.startLevelMusic();
    }

    /** @return The Login state object. */
    public Login getLogin() {
        return login;
    }
    /** @return The Menu state object. */
    public Menu getMenu() {
        return menu;
    }
    /** @return The Playing state object. */
    public Playing getPlaying() {
        return playing;
    }
    
    /** @return The GamePanel object. */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Starts the game loop in a new thread.
     */
    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * The main game loop that controls updates and rendering to maintain a consistent FPS/UPS.
     */
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

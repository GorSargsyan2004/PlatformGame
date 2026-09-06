package gamestates;

import entities.AllayManager;
import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import main.GameAlgorithm;
import music.MusicPlayer;

import ui.MenuButton;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import static main.Game.GAME_HEIGHT;
import static main.Game.TILES_SIZE;

/**
 * The Playing class represents the active gameplay state of the game.
 * It manages the level, player, enemies, and game logic.
 */
public class Playing extends State implements Statemethods{
    private LevelManager levelManager;
    private GameAlgorithm gameAlgorithm;
    private AllayManager allayManager;
    private EnemyManager enemyManager;
    private Player player;
    private MenuButton restartButton, quitButton;

    /**
     * Constructs a new Playing state.
     * @param game The main game object.
     */
    public Playing(Game game) {
        super(game);
        initClasses();
    }

    /**
     * Initializes the managers and entities required for the playing state.
     */
    private void initClasses() {
        // Loading the level
        levelManager = new LevelManager(game);

        // Allays
        allayManager = new AllayManager(this);

        // Enemies
        enemyManager = new EnemyManager(this);

        // Player
        player = new Player(100, 20, new Point2D.Double(400.0, GAME_HEIGHT - 12*TILES_SIZE),
                            Game.SCALE, getLevelData(), enemyManager, game.getLogin().getUserManager());

        // Game Algorithm
        gameAlgorithm = new GameAlgorithm(this, GameAlgorithm.Difficulty.MODERATE);

        int btnY = (int) (150 * Game.SCALE);
        restartButton = new MenuButton(Game.GAME_WIDTH / 2, btnY, 1, Gamestate.PLAYING);
        quitButton = new MenuButton(Game.GAME_WIDTH / 2, btnY + (int)(100 * Game.SCALE), 2, Gamestate.LOGIN);
    }

    /**
     * Updates the level and game algorithm.
     */
    public void update() {
        if (player.isDead()) {
            music.MusicPlayer.stopLevelMusic();
            restartButton.update();
            quitButton.update();
        } else {
            levelManager.update();
            gameAlgorithm.update();
        }
    }

    /**
     * Draws the level and game algorithm.
     * @param g The graphics object used for drawing.
     */
    @Override
    public void draw(Graphics g) {
        levelManager.draw(g);
        gameAlgorithm.draw(g);
        if (player.isDead()) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, (int) (50 * Game.SCALE)));
            String text = "GAME OVER";
            int textWidth = g.getFontMetrics().stringWidth(text);
            g.drawString(text, Game.GAME_WIDTH / 2 - textWidth / 2, (int) (100 * Game.SCALE));
            restartButton.draw(g);
            quitButton.draw(g);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (player.isDead()) {
            if (isIn(e, restartButton)) restartButton.setMousePressed(true);
            else if (isIn(e, quitButton)) quitButton.setMousePressed(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (player.isDead()) {
            if (isIn(e, restartButton) && restartButton.isMousePressed()) {
                game.restartGame();
            } else if (isIn(e, quitButton) && quitButton.isMousePressed()) {
                game.getLogin().clearUsername();
                quitButton.applyGamestate();
            }
            restartButton.resetBools();
            quitButton.resetBools();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (player.isDead()) {
            restartButton.setMouseOver(false);
            quitButton.setMouseOver(false);
            if (isIn(e, restartButton)) restartButton.setMouseOver(true);
            else if (isIn(e, quitButton)) quitButton.setMouseOver(true);
        }
    }



    /**
     * Handles key press events for player movement and actions.
     * @param e The key event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setLeft(true);
                break;
            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                player.setAttack(true);
                break;
            case KeyEvent.VK_W:
                player.setJump(true);
                break;
            case KeyEvent.VK_TAB:
                player.setDashAttack(true);
                break;
            case KeyEvent.VK_S:
                player.setSlide(true);
                break;
            case KeyEvent.VK_E:
                player.setDash(true);
                break;
            case KeyEvent.VK_ESCAPE:
                Gamestate.state = Gamestate.MENU;
                break;
        }
    }

    /**
     * Handles key release events to stop player movement.
     * @param e The key event.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
        }
    }

    /**
     * Returns the level data for the current level.
     * @return The level data.
     */
    public int[][] getLevelData() {
        return levelManager.getCurrentLevel().getLevelData();
    }

    /**
     * Returns the player object.
     * @return The player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the enemy manager.
     * @return The enemy manager.
     */
    public EnemyManager getEnemyManager() { return enemyManager; }

    /**
     * Returns the allay manager.
     * @return The allay manager.
     */
    public AllayManager getAllayManager() { return allayManager; }
}

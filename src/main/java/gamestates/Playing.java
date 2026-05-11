package gamestates;

import entities.AllayManager;
import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import main.GameAlgorithm;
import music.MusicPlayer;

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

    }

    /**
     * Updates the level and game algorithm.
     */
    @Override
    public void update() {
        levelManager.update();
        gameAlgorithm.update();
    }

    /**
     * Draws the level and game algorithm.
     * @param g The graphics object used for drawing.
     */
    @Override
    public void draw(Graphics g) {
        levelManager.draw(g);
        gameAlgorithm.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

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

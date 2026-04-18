package gamestates;

import animations.Direction;
import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import static main.Game.GAME_HEIGHT;
import static main.Game.TILES_SIZE;

public class Playing extends State implements Statemethods{
    private LevelManager levelManager;
    private EnemyManager em;

    // ENTITIES
    private Player player;

    public Playing(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        // Loading the level
        levelManager = new LevelManager(game);

        // TEMPORARY Enemies
        em = new EnemyManager(this);
        em.summonMushroom(Direction.LEFT);
        em.summonGoblin(Direction.RIGHT);
        em.summonSkeleton(Direction.RIGHT);
        em.summonFlyingEye(Direction.LEFT);

        // Player
        player = new Player(100, 20, new Point2D.Double(400.0, GAME_HEIGHT - 12*TILES_SIZE), Game.SCALE, getLevelData(), em);
    }

    @Override
    public void update() {
        levelManager.update();
        if (!player.isDead()) {
            player.update();
        }
        em.update();
    }

    @Override
    public void draw(Graphics g) {
        levelManager.draw(g);
        player.draw(g);
        em.draw(g);
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
            case KeyEvent.VK_ESCAPE:
                Gamestate.state = Gamestate.MENU;
                break;
        }
    }

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

    public int[][] getLevelData() {
        return levelManager.getCurrentLevel().getLevelData();
    }

    public Player getPlayer() {
        return player;
    }
}

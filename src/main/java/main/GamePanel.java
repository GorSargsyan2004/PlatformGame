package main;

import entities.Player;
import gamestates.Gamestate;
import gamestates.Login;
import inputs.KeyboardInputs;
import inputs.MouseInputs;

import javax.swing.*;
import java.awt.*;

import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;

/**
 * The GamePanel class is the primary drawing surface for the game.
 * It handles mouse and keyboard input and manages the main rendering loop via paintComponent.
 */
public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private Game game;

    /**
     * Constructs a GamePanel and initializes input listeners.
     *
     * @param game The main Game object.
     */
    GamePanel(Game game) {
        this.game = game;

        mouseInputs = new MouseInputs(this);

        setPanelSize();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
        setFocusTraversalKeysEnabled(false);
    }

    /**
     * Sets the preferred size of the panel based on game dimensions.
     */
    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    /**
     * Overridden from JPanel to draw the game components based on the current state.
     *
     * @param g The Graphics object used for drawing.
     */
    // < Paint Component >
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (Gamestate.state) {
            case LOGIN -> {
                game.getLogin().draw(g);
            }
            case PLAYING -> {
                game.getPlaying().draw(g);
            }
            case MENU -> {
                game.getMenu().draw(g);
            }
        }
    }

    /** @return The main Game object. */
    public Game getGame() {
        return game;
    }
}

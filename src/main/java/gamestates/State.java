package gamestates;

import main.Game;
import ui.MenuButton;

import java.awt.event.MouseEvent;

/**
 * The State class is the base class for all game states.
 * It provides a reference to the main Game object and utility methods.
 */
public class State {
    /** The main game object. */
    protected Game game;

    /**
     * Constructs a new State.
     * @param game The main game object.
     */
    public State(Game game) {
        this.game = game;
    }

    /**
     * Checks if a mouse event occurred within the bounds of a menu button.
     * @param e The mouse event.
     * @param mb The menu button.
     * @return true if the mouse event is within the button's bounds, false otherwise.
     */
    public boolean isIn(MouseEvent e, MenuButton mb) {
        return mb.getBounds().contains(e.getX(), e.getY());
    }

    /**
     * Returns the main game object.
     * @return The game object.
     */
    public Game getGame() {
        return game;
    }
}

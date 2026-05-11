package gamestates;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * The Statemethods interface defines the standard methods that every game state
 * must implement. It ensures that all game states can be updated, drawn, and
 * respond to mouse and keyboard inputs.
 */
public interface Statemethods {
    /**
     * Updates the logic of the game state.
     */
    public void update();

    /**
     * Draws the components of the game state on the screen.
     *
     * @param g The Graphics object used for drawing.
     */
    public void draw(Graphics g);

    /**
     * Handles the mouse clicked event.
     *
     * @param e The MouseEvent object containing details about the click.
     */
    public void mouseClicked(MouseEvent e);

    /**
     * Handles the mouse pressed event.
     *
     * @param e The MouseEvent object containing details about the press.
     */
    public void mousePressed(MouseEvent e);

    /**
     * Handles the mouse released event.
     *
     * @param e The MouseEvent object containing details about the release.
     */
    public void mouseReleased(MouseEvent e);

    /**
     * Handles the mouse moved event.
     *
     * @param e The MouseEvent object containing details about the movement.
     */
    public void mouseMoved(MouseEvent e);

    /**
     * Handles the key pressed event.
     *
     * @param e The KeyEvent object containing details about the key press.
     */
    public void keyPressed(KeyEvent e);

    /**
     * Handles the key released event.
     *
     * @param e The KeyEvent object containing details about the key release.
     */
    public void keyReleased(KeyEvent e);
}

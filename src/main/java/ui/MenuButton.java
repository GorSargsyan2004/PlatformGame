package ui;

import animations.Animation;
import utils.Direction;
import gamestates.Gamestate;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.Buttons.*;
import static utils.LoadSave.MENU_BUTTONS;

/**
 * The MenuButton class represents a button in the game's user interface.
 * It handles its own animations, bounds, and mouse interaction states.
 */
public class MenuButton {
    // Buttons Indexes
    private static int PLAY = 0;
    private static int RESTART = 1;
    private static int BACK = 2;

    private int xPos, yPos, index;
    private int xOffsetCenter = B_WIDTH / 2;
    private Gamestate state;
    private Animation animation;
    private boolean mouseOver, mousePressed;
    private Rectangle bounds;

    /**
     * Constructs a new MenuButton with specified position, type, and target state.
     *
     * @param xPos  The x-coordinate of the button's center.
     * @param yPos  The y-coordinate of the button.
     * @param index The type of button (PLAY, RESTART, or BACK).
     * @param state The Gamestate to transition to when clicked.
     */
    public MenuButton(int xPos, int yPos, int index, Gamestate state) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.index = index;
        this.state = state;
        
        loadAnimation();
        initBounds();
    }

    /**
     * Initializes the collision bounds for the button.
     */
    private void initBounds() {
        bounds = new Rectangle(xPos-xOffsetCenter, yPos, B_WIDTH, B_HEIGHT);
    }

    /**
     * Loads the appropriate animation for the button type.
     */
    private void loadAnimation() {
        String pattern = "";
        if (index == PLAY)
            pattern = "play";
        else if (index == RESTART)
            pattern = "restart";
        else if (index == BACK)
            pattern = "back";


        animation = new Animation(MENU_BUTTONS + pattern, pattern + "0");
    }

    /**
     * Draws the button's current animation frame.
     *
     * @param g The Graphics object used for drawing.
     */
    public void draw(Graphics g) {
        BufferedImage imageToDraw = animation.getAnimationImageAt(index, Direction.RIGHT);
        g.drawImage(imageToDraw, xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT, null);
    }

    /**
     * Updates the button's animation index based on mouse interaction.
     */
    public void update() {
        index = 0;
        if (mouseOver)
            index = 1;
        if (mousePressed)
            index = 2;
    }

    /** @return true if the mouse is currently over the button. */
    public boolean isMouseOver() {
        return mouseOver;
    }

    /** @return true if the button is currently being pressed. */
    public boolean isMousePressed() {
        return mousePressed;
    }

    /** @param mouseOver Sets whether the mouse is over the button. */
    public void setMouseOver(boolean mouseOver) {
        if (mouseOver && !this.mouseOver) {
            utils.LoadSave.playSound(utils.LoadSave.SOUND_HOVER);
        }
        this.mouseOver = mouseOver;
    }

    /** @param mousePressed Sets whether the button is being pressed. */
    public void setMousePressed(boolean mousePressed) {
        if (mousePressed && !this.mousePressed) {
            utils.LoadSave.playSound(utils.LoadSave.SOUND_CLICK);
        }
        this.mousePressed = mousePressed;
    }

    /** @return The collision bounds of the button. */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Changes the current game state to the state associated with this button.
     */
    public void applyGamestate() {
        Gamestate.state = state;
    }

    /**
     * Resets the mouse interaction booleans to false.
     */
    public void resetBools() {
        mouseOver = false;
        mousePressed = false;
    }
}

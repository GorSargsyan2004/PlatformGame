package ui;

import animations.Animation;
import animations.Direction;
import gamestates.Gamestate;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.Buttons.*;
import static utils.LoadSave.MENU_BUTTONS;

public class MenuButton {
    // Buttons Indexes
    private static int PLAY = 0;
    private static int BACK = 1;

    private int xPos, yPos, index;
    private int xOffsetCenter = B_WIDTH / 2;
    private Gamestate state;
    private Animation animation;
    private boolean mouseOver, mousePressed;
    private Rectangle bounds;

    public MenuButton(int xPos, int yPos, int index, Gamestate state) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.index = index;
        this.state = state;
        
        loadAnimation();
        initBounds();
    }

    private void initBounds() {
        bounds = new Rectangle(xPos-xOffsetCenter, yPos, B_WIDTH, B_HEIGHT);
    }

    private void loadAnimation() {
        String pattern = "";
        if (index == PLAY)
            pattern = "play";
        else if (index == BACK)
            pattern = "back";


        animation = new Animation(MENU_BUTTONS + pattern, pattern + "0");
    }

    public void draw(Graphics g) {
        BufferedImage imageToDraw = animation.getAnimationImageAt(index, Direction.RIGHT);
        g.drawImage(imageToDraw, xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT, null);
    }

    public void update() {
        index = 0;
        if (mouseOver)
            index = 1;
        if (mousePressed)
            index = 2;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void applyGamestate() {
        Gamestate.state = state;
    }

    public void resetBools() {
        mouseOver = false;
        mousePressed = false;
    }
}

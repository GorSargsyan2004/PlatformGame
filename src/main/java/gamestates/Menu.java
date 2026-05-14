package gamestates;

import main.Game;
import ui.MenuButton;
import utils.LoadSave;

import long_term_memory.Leaderboard;
import long_term_memory.Player;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * The Menu class represents the main menu state of the game.
 * It handles the display and interaction of menu buttons.
 */
public class Menu extends State implements Statemethods {
    private MenuButton[] buttons = new MenuButton[3];
    private static final int Y_POS_OF_BUTTONS = 150;
    private static final int Y_POS_OF_BACKGROUND = 80;
    private BufferedImage backgroundImg, backgroundImgMenu;
    private int menuX, menuY, menuWidth, menuHeight;
    /** The leaderboard object used to track and display top scores. */
    private Leaderboard leaderboard;

    /**
     * Constructs a new Menu state.
     * @param game The main game object.
     */
    public Menu(Game game) {
        super(game);

        loadButtons();
        loadBackground();
        leaderboard = new Leaderboard("data/userInfo.txt");
    }

    /**
     * Loads the background images and calculates their positions and dimensions.
     */
    private void loadBackground() {
        backgroundImg = LoadSave.getSave(LoadSave.MENU_BACKGROUND);
        backgroundImgMenu = LoadSave.getSave(LoadSave.BACKGROUND_MENU);
        menuWidth = (int) (backgroundImg.getWidth() / 1.5 * Game.SCALE);
        menuHeight = (int) (backgroundImg.getHeight() / 1.5 * Game.SCALE);

        menuX = Game.GAME_WIDTH / 2 - menuWidth / 2;
        menuY = (int) (Y_POS_OF_BACKGROUND * Game.SCALE);
    }

    /**
     * Initializes the menu buttons with their positions and associated game states.
     */
    private void loadButtons() {
        buttons[0] = new MenuButton(Game.GAME_WIDTH / 2, (int)(Y_POS_OF_BUTTONS*Game.SCALE), 0, Gamestate.PLAYING);
        buttons[1] = new MenuButton(Game.GAME_WIDTH / 2, (int)((Y_POS_OF_BUTTONS+100)*Game.SCALE), 1, Gamestate.PLAYING);
        buttons[2] = new MenuButton(Game.GAME_WIDTH / 2, (int)((Y_POS_OF_BUTTONS+200)*Game.SCALE), 2, Gamestate.QUIT);
    }

    /**
     * Updates the state of all menu buttons.
     */
    @Override
    public void update() {
        for (MenuButton mb : buttons)
            mb.update();
    }

    /**
     * Draws the menu background and buttons.
     * @param g The graphics object used for drawing.
     */
    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImgMenu, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        Graphics2D g2d = (Graphics2D) g;
        float alpha = 0.8f; // 20% transparent
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        Composite oldComposite = g2d.getComposite();

        g2d.setComposite(ac);
        g2d.drawImage(backgroundImg, menuX, menuY, menuWidth, menuHeight, null);
        g2d.setComposite(oldComposite);

        for (MenuButton mb : buttons)
            mb.draw(g);

        drawLeaderboard(g);
    }

    /**
     * Draws the leaderboard on the menu screen.
     * @param g The graphics object used for drawing.
     */
    private void drawLeaderboard(Graphics g) {
        ArrayList<Player> players = leaderboard.getPlayersSorted();
        int lbWidth = (int) (200 * Game.SCALE);
        int lbHeight = (int) (320 * Game.SCALE);
        int lbX = menuX + menuWidth + (int) (20 * Game.SCALE);
        int lbY = menuY;

        // Background for leaderboard
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(lbX, lbY, lbWidth, lbHeight, 20, 20);
        g.setColor(Color.WHITE);
        g.drawRoundRect(lbX, lbY, lbWidth, lbHeight, 20, 20);

        g.setFont(new Font("Arial", Font.BOLD, (int) (20 * Game.SCALE)));
        g.drawString("Leaderboard", lbX + (int) (35 * Game.SCALE), lbY + (int) (30 * Game.SCALE));

        g.setFont(new Font("Arial", Font.PLAIN, (int) (14 * Game.SCALE)));
        for (int i = 0; i < Math.min(players.size(), 10); i++) {
            Player p = players.get(i);
            String text = (i + 1) + ". " + p.getName() + ": " + p.getScore();
            g.drawString(text, lbX + (int) (20 * Game.SCALE), lbY + (int) (60 * Game.SCALE) + i * (int) (25 * Game.SCALE));
        }
    }

    /**
     * Refreshes the leaderboard data from the storage file.
     */
    public void updateLeaderboard() {
        leaderboard.updateLeaderboard();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Handles mouse press events on menu buttons.
     * @param e The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        for (MenuButton mb : buttons) {
            if (isIn(e, mb)) {
                mb.setMousePressed(true);
                break;
            }
        }
    }

    /**
     * Handles mouse release events on menu buttons, triggering actions if appropriate.
     * @param e The mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        for (MenuButton mb : buttons) {
            if (isIn(e, mb) && mb.isMousePressed()) {
                if (mb == buttons[1]) {
                    game.restartGame();
                } else {
                    mb.applyGamestate();
                }
                break;
            }
        }
        resetButtons();
    }

    /**
     * Resets the mouse state for all buttons.
     */
    private void resetButtons() {
        for (MenuButton mb : buttons)
            mb.resetBools();
    }

    /**
     * Handles mouse move events to update the hover state of menu buttons.
     * @param e The mouse event.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        for (MenuButton mb : buttons)
            mb.setMouseOver(false);

        for (MenuButton mb : buttons) {
            if (isIn(e, mb)) {
                mb.setMouseOver(true);
                break;
            }
        }
    }

    /**
     * Handles key press events, allowing the user to return to the playing state.
     * @param e The key event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            Gamestate.state = Gamestate.PLAYING;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

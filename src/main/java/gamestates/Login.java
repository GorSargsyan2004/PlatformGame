package gamestates;

import main.Game;
import long_term_memory.UserManager;
import long_term_memory.UserManagerExceptions.UserManagerException;
import long_term_memory.Leaderboard;
import long_term_memory.Player;
import utils.LoadSave;

import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * The Login class handles the login and registration process for the game.
 * It provides fields for username entry and a single Play button.
 */
public class Login extends State implements Statemethods {
    private final UserManager userManager;
    private final StringBuilder username = new StringBuilder();
    
    // TODO: To recover password feature, uncomment the lines below and in other sections of this file
    // private final StringBuilder password = new StringBuilder();
    
    private boolean usernameActive = true;
    private Rectangle2D.Float usernameField, playBtn, quitBtn;
    
    private boolean playBtnOver = false;
    private boolean quitBtnOver = false;
    
    // private Rectangle2D.Float passwordField, loginBtn, registerBtn;

    private String message = "";
    private Color messageColor = Color.WHITE;
    private BufferedImage backgroundLogin;
    private Leaderboard leaderboard;

    // Controls Table components
    private Image runLeftGif, runRightGif, jumpGif, slideGif, dashGif, attackGif, dashAttackGif;

    /**
     * Constructs a new Login state.
     * @param game The main game object.
     */
    public Login(Game game) {
        super(game);
        userManager = new UserManager("data/userInfo.txt");
        leaderboard = new Leaderboard("data/userInfo.txt");
        backgroundLogin = LoadSave.getSave(LoadSave.BACKGROUND_LOGIN);
        initBounds();
        loadGifs();
    }
    
    private void loadGifs() {
        runLeftGif = new ImageIcon("src/main/resources/Demo/run-left.gif").getImage();
        runRightGif = new ImageIcon("src/main/resources/Demo/run-right.gif").getImage();
        jumpGif = new ImageIcon("src/main/resources/Demo/jump.gif").getImage();
        slideGif = new ImageIcon("src/main/resources/Demo/slide.gif").getImage();
        dashGif = new ImageIcon("src/main/resources/Demo/dash.gif").getImage();
        attackGif = new ImageIcon("src/main/resources/Demo/attack.gif").getImage();
        dashAttackGif = new ImageIcon("src/main/resources/Demo/dash-attack.gif").getImage();
    }

    /**
     * Initializes the bounds for the input fields and buttons.
     */
    private void initBounds() {
        int fieldWidth = (int)(200 * Game.SCALE);
        int fieldHeight = (int)(30 * Game.SCALE);
        int xCenter = Game.GAME_WIDTH / 2 - fieldWidth / 2;

        usernameField = new Rectangle2D.Float(xCenter, Game.GAME_HEIGHT / 2 - fieldHeight, fieldWidth, fieldHeight);
        playBtn = new Rectangle2D.Float(xCenter + fieldWidth / 4, Game.GAME_HEIGHT / 2 + fieldHeight, fieldWidth / 2, fieldHeight);
        quitBtn = new Rectangle2D.Float(xCenter + fieldWidth / 4, Game.GAME_HEIGHT / 2 + fieldHeight + (int)(40 * Game.SCALE), fieldWidth / 2, fieldHeight);

        // TODO: Uncomment for password feature
        /*
        passwordField = new Rectangle2D.Float(xCenter, Game.GAME_HEIGHT / 2 + 10 * Game.SCALE, fieldWidth, fieldHeight);
        loginBtn = new Rectangle2D.Float(xCenter, Game.GAME_HEIGHT / 2 + 60 * Game.SCALE, 90 * Game.SCALE, 30 * Game.SCALE);
        registerBtn = new Rectangle2D.Float(xCenter + 110 * Game.SCALE, Game.GAME_HEIGHT / 2 + 60 * Game.SCALE, 90 * Game.SCALE, 30 * Game.SCALE);
        */
    }

    @Override
    public void update() {
        leaderboard.updateLeaderboard();
    }

    /**
     * Draws the login screen, including input fields, labels, buttons, and messages.
     * @param g The graphics object used for drawing.
     */
    public void clearUsername() {
        username.setLength(0);
    }

    @Override
    public void draw(Graphics g) {
        if (backgroundLogin != null) {
            g.drawImage(backgroundLogin, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int)(16 * Game.SCALE)));
        g.drawString("Username:", (int) usernameField.x, (int) usernameField.y - 5);
        
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect((int) usernameField.x, (int) usernameField.y, (int) usernameField.width, (int) usernameField.height);
        g.setColor(Color.WHITE);
        g.drawRect((int) usernameField.x, (int) usernameField.y, (int) usernameField.width, (int) usernameField.height);
        g.drawString(username.toString() + (usernameActive ? "|" : ""), (int) usernameField.x + 5, (int) usernameField.y + (int)(20 * Game.SCALE));

        // TODO: Uncomment for password feature
        /*
        g.drawString("Password:", (int) passwordField.x, (int) passwordField.y - 5);
        g.drawRect((int) passwordField.x, (int) passwordField.y, (int) passwordField.width, (int) passwordField.height);
        String dots = "*".repeat(password.length());
        g.drawString(dots + (!usernameActive ? "|" : ""), (int) passwordField.x + 5, (int) passwordField.y + 20);
        */

        // Button
        g.setColor(playBtnOver ? new Color(100, 100, 100, 180) : new Color(0, 0, 0, 180));
        g.fillRect((int) playBtn.x, (int) playBtn.y, (int) playBtn.width, (int) playBtn.height);
        
        g.setColor(quitBtnOver ? new Color(100, 100, 100, 180) : new Color(0, 0, 0, 180));
        g.fillRect((int) quitBtn.x, (int) quitBtn.y, (int) quitBtn.width, (int) quitBtn.height);
        
        g.setColor(Color.WHITE);
        g.drawRect((int) playBtn.x, (int) playBtn.y, (int) playBtn.width, (int) playBtn.height);
        g.drawString("Play", (int) playBtn.x + (int)(32 * Game.SCALE), (int) playBtn.y + (int)(20 * Game.SCALE));

        g.drawRect((int) quitBtn.x, (int) quitBtn.y, (int) quitBtn.width, (int) quitBtn.height);
        g.drawString("Quit", (int) quitBtn.x + (int)(32 * Game.SCALE), (int) quitBtn.y + (int)(20 * Game.SCALE));

        if (!message.isEmpty()) {
            g.setColor(messageColor);
            g.drawString(message, (int) usernameField.x - (int)(20 * Game.SCALE), (int) quitBtn.y + (int)(60 * Game.SCALE));
        }
        
        drawLeaderboard(g);
        drawControlsTable(g);
    }
    
    private void drawLeaderboard(Graphics g) {
        ArrayList<Player> players = leaderboard.getPlayersSorted();
        int lbWidth = (int) (200 * Game.SCALE);
        int lbHeight = (int) (320 * Game.SCALE);
        int lbX = Game.GAME_WIDTH - lbWidth - (int) (50 * Game.SCALE);
        int lbY = Game.GAME_HEIGHT / 2 - lbHeight / 2;

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
    
    private void drawControlsTable(Graphics g) {
        int ctWidth = (int) (350 * Game.SCALE);
        int ctHeight = (int) (500 * Game.SCALE);
        int ctX = (int) (50 * Game.SCALE);
        int ctY = Game.GAME_HEIGHT / 2 - ctHeight / 2;
        
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(ctX, ctY, ctWidth, ctHeight, 20, 20);
        g.setColor(Color.WHITE);
        g.drawRoundRect(ctX, ctY, ctWidth, ctHeight, 20, 20);
        
        g.setFont(new Font("Arial", Font.BOLD, (int) (20 * Game.SCALE)));
        g.drawString("Controls", ctX + (int) (125 * Game.SCALE), ctY + (int) (30 * Game.SCALE));
        
        g.setFont(new Font("Arial", Font.PLAIN, (int) (14 * Game.SCALE)));
        
        String[] actions = {"Move Left", "Move Right", "Jump", "Slide", "Dash", "Attack", "Dash Attack"};
        String[] keys = {"'A'", "'D'", "'W'", "'S'", "'E'", "'Space'", "'Tab'"};
        Image[] gifs = {runLeftGif, runRightGif, jumpGif, slideGif, dashGif, attackGif, dashAttackGif};
        
        int startY = ctY + (int) (60 * Game.SCALE);
        int rowHeight = (int) (60 * Game.SCALE);
        Component observer = game.getGamePanel();
        
        for (int i = 0; i < actions.length; i++) {
            g.drawString(actions[i] + " (" + keys[i] + ")", ctX + (int) (20 * Game.SCALE), startY + i * rowHeight + (int)(30 * Game.SCALE));
            if (gifs[i] != null) {
                // scale the gif down if it's too big, drawing it at fixed size
                g.drawImage(gifs[i], ctX + (int) (220 * Game.SCALE), startY + i * rowHeight, (int)(80 * Game.SCALE), (int)(50 * Game.SCALE), observer);
            }
        }
    }

    /**
     * Handles mouse press events to switch active fields or trigger button actions.
     * @param e The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (usernameField.contains(e.getPoint())) {
            usernameActive = true;
        } else if (playBtn.contains(e.getPoint())) {
            utils.LoadSave.playSound(utils.LoadSave.SOUND_CLICK);
            play();
        } else if (quitBtn.contains(e.getPoint())) {
            utils.LoadSave.playSound(utils.LoadSave.SOUND_CLICK);
            System.exit(0);
        }
        
        // TODO: Uncomment for password feature
        /*
        else if (passwordField.contains(e.getPoint())) {
            usernameActive = false;
        } else if (loginBtn.contains(e.getPoint())) {
            login();
        } else if (registerBtn.contains(e.getPoint())) {
            register();
        }
        */
    }

    private void play() {
        if (username.length() == 0) {
            message = "Username cannot be empty!";
            messageColor = Color.RED;
            return;
        }
        try {
            if (userManager.login(username.toString(), "")) {
                Gamestate.state = Gamestate.MENU;
            }
        } catch (long_term_memory.UserManagerExceptions.UserNameDoesnotExistException e) {
            try {
                if (userManager.register(username.toString(), "")) {
                    userManager.login(username.toString(), "");
                    Gamestate.state = Gamestate.MENU;
                }
            } catch (UserManagerException ex) {
                message = ex.getMessage();
                messageColor = Color.RED;
            }
        } catch (UserManagerException e) {
            message = e.getMessage();
            messageColor = Color.RED;
        }
    }

    // TODO: Uncomment for password feature
    /*
    private void login() {
        try {
            if (userManager.login(username.toString(), password.toString())) {
                Gamestate.state = Gamestate.MENU;
            }
        } catch (UserManagerException e) {
            message = e.getMessage();
            messageColor = Color.RED;
        }
    }

    private void register() {
        try {
            if (userManager.register(username.toString(), password.toString())) {
                message = "Registration successful! You can now login.";
                messageColor = Color.GREEN;
            }
        } catch (UserManagerException e) {
            message = e.getMessage();
            messageColor = Color.RED;
        }
    }
    */

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        boolean wasPlayOver = playBtnOver;
        boolean wasQuitOver = quitBtnOver;
        
        playBtnOver = playBtn.contains(e.getPoint());
        quitBtnOver = quitBtn.contains(e.getPoint());
        
        if (playBtnOver && !wasPlayOver) {
            utils.LoadSave.playSound(utils.LoadSave.SOUND_HOVER);
        }
        if (quitBtnOver && !wasQuitOver) {
            utils.LoadSave.playSound(utils.LoadSave.SOUND_HOVER);
        }
    }

    /**
     * Handles key press events for text entry, field switching, and triggering login.
     * @param e The key event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            // TODO: Uncomment for password feature
            // usernameActive = !usernameActive;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            play();
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (usernameActive && username.length() > 0) {
                username.deleteCharAt(username.length() - 1);
            } 
            // TODO: Uncomment for password feature
            /*
            else if (!usernameActive && password.length() > 0) {
                password.deleteCharAt(password.length() - 1);
            }
            */
        } else {
            char c = e.getKeyChar();
            if (Character.isLetterOrDigit(c) || "!@#$%^&*()_+-=".indexOf(c) != -1) {
                if (usernameActive) {
                    username.append(c);
                } 
                // TODO: Uncomment for password feature
                /*
                else {
                    password.append(c);
                }
                */
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Returns the user manager.
     * @return The user manager.
     */
    public UserManager getUserManager() {return userManager;}
}

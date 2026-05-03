package gamestates;

import main.Game;
import long_term_memory.UserManager;
import long_term_memory.UserManagerExceptions.UserManagerException;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class Login extends State implements Statemethods {
    private final UserManager userManager;
    private final StringBuilder username = new StringBuilder();
    private final StringBuilder password = new StringBuilder();
    private boolean usernameActive = true;
    private Rectangle2D.Float usernameField, passwordField, loginBtn, registerBtn;
    private String message = "";
    private Color messageColor = Color.WHITE;

    public Login(Game game) {
        super(game);
        userManager = new UserManager("data/userInfo.txt");
        initBounds();
    }

    private void initBounds() {
        int fieldWidth = 200;
        int fieldHeight = 30;
        int xCenter = Game.GAME_WIDTH / 2 - fieldWidth / 2;

        usernameField = new Rectangle2D.Float(xCenter, 150, fieldWidth, fieldHeight);
        passwordField = new Rectangle2D.Float(xCenter, 200, fieldWidth, fieldHeight);

        loginBtn = new Rectangle2D.Float(xCenter, 250, 90, 30);
        registerBtn = new Rectangle2D.Float(xCenter + 110, 250, 90, 30);
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        g.setColor(Color.WHITE);
        g.drawString("Username:", (int) usernameField.x, (int) usernameField.y - 5);
        g.drawRect((int) usernameField.x, (int) usernameField.y, (int) usernameField.width, (int) usernameField.height);
        g.drawString(username.toString() + (usernameActive ? "|" : ""), (int) usernameField.x + 5, (int) usernameField.y + 20);

        g.drawString("Password:", (int) passwordField.x, (int) passwordField.y - 5);
        g.drawRect((int) passwordField.x, (int) passwordField.y, (int) passwordField.width, (int) passwordField.height);
        String dots = "*".repeat(password.length());
        g.drawString(dots + (!usernameActive ? "|" : ""), (int) passwordField.x + 5, (int) passwordField.y + 20);

        // Buttons
        g.drawRect((int) loginBtn.x, (int) loginBtn.y, (int) loginBtn.width, (int) loginBtn.height);
        g.drawString("Login", (int) loginBtn.x + 25, (int) loginBtn.y + 20);

        g.drawRect((int) registerBtn.x, (int) registerBtn.y, (int) registerBtn.width, (int) registerBtn.height);
        g.drawString("Register", (int) registerBtn.x + 20, (int) registerBtn.y + 20);

        if (!message.isEmpty()) {
            g.setColor(messageColor);
            g.drawString(message, (int) usernameField.x, (int) registerBtn.y + 50);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (usernameField.contains(e.getPoint())) {
            usernameActive = true;
        } else if (passwordField.contains(e.getPoint())) {
            usernameActive = false;
        } else if (loginBtn.contains(e.getPoint())) {
            login();
        } else if (registerBtn.contains(e.getPoint())) {
            register();
        }
    }

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

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            usernameActive = !usernameActive;
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            login();
        } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (usernameActive && username.length() > 0) {
                username.deleteCharAt(username.length() - 1);
            } else if (!usernameActive && password.length() > 0) {
                password.deleteCharAt(password.length() - 1);
            }
        } else {
            char c = e.getKeyChar();
            if (Character.isLetterOrDigit(c) || "!@#$%^&*()_+-=".indexOf(c) != -1) {
                if (usernameActive) {
                    username.append(c);
                } else {
                    password.append(c);
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public UserManager getUserManager() {return userManager;}
}

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

public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private Game game;

    GamePanel(Game game) {
        this.game = game;

        mouseInputs = new MouseInputs(this);

        setPanelSize();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
        setFocusTraversalKeysEnabled(false);
    }

    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

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

    public Game getGame() {
        return game;
    }
}

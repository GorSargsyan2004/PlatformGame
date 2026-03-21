package main;

public class Game {

    private GameWindow gameWindow;
    private GamePanel gamePanel;

    Game() {
        gamePanel = new GamePanel();
        gameWindow = new GameWindow(gamePanel);

        gamePanel.requestFocus();

    }
}

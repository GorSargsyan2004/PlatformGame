package main;

import javax.swing.*;

public class GameWindow {
    private JFrame jframe;

    GameWindow(GamePanel gamePanel) {
        this.jframe = new JFrame();
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.add(gamePanel);
        jframe.setLocationRelativeTo(null);
        jframe.setResizable(false);
        jframe.pack();
        jframe.setVisible(true);

    }
}

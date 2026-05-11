package main;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * The GameWindow class creates and manages the main application window (JFrame).
 * It hosts the GamePanel and handles window focus events.
 */
public class GameWindow {
    private JFrame jframe;

    /**
     * Constructs the GameWindow and configures the JFrame.
     *
     * @param gamePanel The GamePanel to be added to the window.
     */
    GameWindow(GamePanel gamePanel) {
        this.jframe = new JFrame("Oakheart Chronicles");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.add(gamePanel);
        jframe.setLocationRelativeTo(null);
        jframe.setResizable(false);
        jframe.pack();
        jframe.setVisible(true);
        jframe.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                gamePanel.requestFocusInWindow();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        });

    }
}
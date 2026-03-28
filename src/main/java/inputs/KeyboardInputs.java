package inputs;

import animations.Direction;
import main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInputs implements KeyListener {

    private GamePanel gamePanel;

    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                gamePanel.getPlayer().setLeft(true);
                break;
            case KeyEvent.VK_D:
                gamePanel.getPlayer().setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                gamePanel.getPlayer().setAttack(true);
                break;
            case KeyEvent.VK_W:
                gamePanel.getPlayer().setJump(true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                gamePanel.getPlayer().setLeft(false);
                break;
            case KeyEvent.VK_D:
                gamePanel.getPlayer().setRight(false);
                break;
        }
    }
}
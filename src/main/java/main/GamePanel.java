package main;

import inputs.KeyboardInputs;
import inputs.MouseInputs;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private float xDelta = 100, yDelta = 100;
    private float xDir = 0.01f, yDir = 0.01f;
    private int frames = 0;
    private long lastCheck = 0;
    private Color color = new Color(10, 20, 90);
    private Random random;

    GamePanel() {
        mouseInputs = new MouseInputs(this);

        random = new Random();

        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }

    public void changeXDelta(int value) {
        this.xDelta += value;
    }

    public void changeYDelta(int value) {
        this.yDelta += value;
    }

    public void setRectPos(int x, int y) {
        this.yDelta = y;
        this.xDelta = x;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        updateRectangle();
        g.setColor(color);
        g.fillRect((int)xDelta, (int)yDelta, 200, 50);

        frames++;
        if (System.currentTimeMillis() - lastCheck >= 1000) {
            lastCheck = System.currentTimeMillis();
            System.out.println("FPS: " + frames);
            frames = 0;
        }

        repaint();
    }

    private void updateRectangle() {
        xDelta += xDir;
        if(xDelta > 400 || xDelta < 0) {
            xDir*=-1;
            color = getRndColor();
        }

        yDelta += yDir;
        if(yDelta > 400 || yDelta < 0) {
            yDir*=-1;
            color = getRndColor();

        }
    }

    private Color getRndColor() {
        int r = random.nextInt(255),
                b = random.nextInt(255),
                g = random.nextInt(255);

        return new Color(r,g,b);
    }
}

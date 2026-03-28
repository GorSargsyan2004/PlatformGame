package main;

import animations.Animation;
import animations.Direction;
import entities.Player;
import inputs.KeyboardInputs;
import inputs.MouseInputs;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private Player player;

    private Animation anim;

    GamePanel() {
        player = new Player(100, 20, new Point2D.Double(500.0, 500.0));

        mouseInputs = new MouseInputs(this);

        setPanelSize();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }



    private void setPanelSize() {
        Dimension size = new Dimension(1280, 800);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    // < Paint Component >
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        player.update();
        player.drawPlayer(g);
    }

    public Player getPlayer() {
        return player;
    }
}

package utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static main.Game.TILES_DEFAULT_SIZE;
import static levels.LevelManager.TILESET_WIDTH;
import static levels.LevelManager.TILESET_HEIGHT;

public class LevelEditor extends JFrame {

    // --- Adjust these to match your game's grid size ---
    private static final int TILES_IN_WIDTH = 48;
    private static final int TILES_IN_HEIGHT = 24;
    private static final int TILE_SIZE = 32; // Visual size on screen

    private int[][] mapData = new int[TILES_IN_HEIGHT][TILES_IN_WIDTH];
    private int currentSelectedId = 0;

    private BufferedImage[] tiles;
    private BufferedImage tilesetImg;
    private JPanel tilesetPanel;
    private JSpinner idSpinner;

    public LevelEditor() {
        loadTiles();
        initMapData();

        setTitle("Level Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Setup the Drawing Canvas
        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
            }
        };
        canvas.setPreferredSize(new Dimension(TILES_IN_WIDTH * TILE_SIZE, TILES_IN_HEIGHT * TILE_SIZE));
        canvas.setBackground(Color.DARK_GRAY);

        // 2. Add Mouse Controls for Painting
        MouseAdapter painter = new MouseAdapter() {
            private void paintTile(MouseEvent e) {
                int col = e.getX() / TILE_SIZE;
                int row = e.getY() / TILE_SIZE;

                if (col >= 0 && col < TILES_IN_WIDTH && row >= 0 && row < TILES_IN_HEIGHT) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        mapData[row][col] = 4; // Erase to blank tile (ID 4)
                    } else {
                        mapData[row][col] = currentSelectedId;
                    }
                    canvas.repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) { paintTile(e); }
            @Override
            public void mouseDragged(MouseEvent e) { paintTile(e); }
        };
        canvas.addMouseListener(painter);
        canvas.addMouseMotionListener(painter);

        // 3. Setup the Tileset Selection Panel
        tilesetPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (tilesetImg != null) {
                    g.drawImage(tilesetImg, 0, 0, null);
                    // Highlight selected
                    int x = (currentSelectedId % TILESET_WIDTH) * TILES_DEFAULT_SIZE;
                    int y = (currentSelectedId / TILESET_WIDTH) * TILES_DEFAULT_SIZE;
                    g.setColor(Color.RED);
                    ((Graphics2D) g).setStroke(new BasicStroke(2));
                    g.drawRect(x, y, TILES_DEFAULT_SIZE, TILES_DEFAULT_SIZE);
                }
            }
        };
        tilesetPanel.setPreferredSize(new Dimension(TILESET_WIDTH * TILES_DEFAULT_SIZE, TILESET_HEIGHT * TILES_DEFAULT_SIZE));
        tilesetPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / TILES_DEFAULT_SIZE;
                int row = e.getY() / TILES_DEFAULT_SIZE;
                if (col >= 0 && col < TILESET_WIDTH && row >= 0 && row < TILESET_HEIGHT) {
                    currentSelectedId = row * TILESET_WIDTH + col;
                    idSpinner.setValue(currentSelectedId);
                    tilesetPanel.repaint();
                }
            }
        });

        // 4. Setup the Control Panel
        JPanel controlPanel = new JPanel();
        JLabel idLabel = new JLabel("Selected ID:");
        idSpinner = new JSpinner(new SpinnerNumberModel(0, 0, TILESET_WIDTH * TILESET_HEIGHT - 1, 1));
        idSpinner.addChangeListener(e -> {
            currentSelectedId = (int) idSpinner.getValue();
            tilesetPanel.repaint();
        });

        JButton saveButton = new JButton("Save map.png");
        saveButton.addActionListener(e -> saveMap());

        JButton loadButton = new JButton("Load map.png");
        loadButton.addActionListener(e -> {
            loadMap();
            canvas.repaint();
        });

        JButton clearButton = new JButton("Clear (Set all to 4)");
        clearButton.addActionListener(e -> {
            initMapData();
            canvas.repaint();
        });

        controlPanel.add(idLabel);
        controlPanel.add(idSpinner);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(clearButton);
        controlPanel.add(loadButton);
        controlPanel.add(saveButton);

        // 5. Layout assembly
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(canvas), BorderLayout.CENTER);

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.setBorder(BorderFactory.createTitledBorder("Tileset"));
        eastPanel.add(new JScrollPane(tilesetPanel), BorderLayout.CENTER);

        add(controlPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(eastPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadTiles() {
        tilesetImg = LoadSave.getSave(LoadSave.LEVEL_ATLAS);
        if (tilesetImg == null) {
            System.err.println("Could not load tileset image!");
            return;
        }
        tiles = new BufferedImage[TILESET_WIDTH * TILESET_HEIGHT];
        for (int j = 0; j < TILESET_HEIGHT; j++) {
            for (int i = 0; i < TILESET_WIDTH; i++) {
                int index = j * TILESET_WIDTH + i;
                tiles[index] = tilesetImg.getSubimage(i * TILES_DEFAULT_SIZE, j * TILES_DEFAULT_SIZE, TILES_DEFAULT_SIZE, TILES_DEFAULT_SIZE);
            }
        }
    }

    private void initMapData() {
        for (int row = 0; row < TILES_IN_HEIGHT; row++) {
            for (int col = 0; col < TILES_IN_WIDTH; col++) {
                mapData[row][col] = 4; // Blank tile ID
            }
        }
    }

    private void drawGrid(Graphics g) {
        for (int row = 0; row < TILES_IN_HEIGHT; row++) {
            for (int col = 0; col < TILES_IN_WIDTH; col++) {
                int id = mapData[row][col];
                int x = col * TILE_SIZE;
                int y = row * TILE_SIZE;

                if (tiles != null && id >= 0 && id < tiles.length) {
                    g.drawImage(tiles[id], x, y, TILE_SIZE, TILE_SIZE, null);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                }

                g.setColor(new Color(255, 255, 255, 30));
                g.drawRect(x, y, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    private void loadMap() {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setDialogTitle("Select map.png to load");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(file);
                if (img.getWidth() != TILES_IN_WIDTH || img.getHeight() != TILES_IN_HEIGHT) {
                    JOptionPane.showMessageDialog(this, "Map dimensions must be " + TILES_IN_WIDTH + "x" + TILES_IN_HEIGHT + " pixels.", "Dimension Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                for (int y = 0; y < TILES_IN_HEIGHT; y++) {
                    for (int x = 0; x < TILES_IN_WIDTH; x++) {
                        Color color = new Color(img.getRGB(x, y));
                        mapData[y][x] = color.getRed();
                    }
                }
                JOptionPane.showMessageDialog(this, "Map loaded successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to load map: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveMap() {
        BufferedImage img = new BufferedImage(TILES_IN_WIDTH, TILES_IN_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < TILES_IN_HEIGHT; row++) {
            for (int col = 0; col < TILES_IN_WIDTH; col++) {
                int id = mapData[row][col];
                Color color = new Color(id, 0, 0);
                img.setRGB(col, row, color.getRGB());
            }
        }

        try {
            File file = new File("map.png");
            ImageIO.write(img, "png", file);
            JOptionPane.showMessageDialog(this, "Saved successfully to: " + file.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LevelEditor());
    }
}
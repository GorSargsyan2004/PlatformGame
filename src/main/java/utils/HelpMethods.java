package utils;

import main.Game;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class HelpMethods {
    public static boolean CanMoveHere(Point2D.Double pos, float width, float height, int[][] lvlData) {
        if (!IsSolid(pos.x, pos.y, lvlData))
            if (!IsSolid(pos.x + width - 1, pos.y + height - 1, lvlData))
                if (!IsSolid(pos.x + width - 1, pos.y, lvlData))
                    if (!IsSolid(pos.x, pos.y + height - 1, lvlData))
                        return true;

        return false;
    }

    public static boolean CanMoveHereOnSlope(Point2D.Double pos, float width, float height, int[][] lvlData) {
        // Only check top and middle corners for walls. Skip bottom feet to allow walking over slopes and ground under them.
        if (!IsSolid(pos.x, pos.y, lvlData)) // Top Left
            if (!IsSolid(pos.x + width - 1, pos.y, lvlData)) // Top Right
                if (!IsSolid(pos.x, pos.y + height / 2, lvlData)) // Middle Left
                    if (!IsSolid(pos.x + width - 1, pos.y + height / 2, lvlData)) // Middle Right
                        return true;
        return false;
    }

    private static boolean IsSolid(double x, double y, int[][] lvlData) {
        if (x < 0 || x >= Game.GAME_WIDTH)
            return true;
        if (y < 0 || y >= Game.GAME_HEIGHT)
            return true;

        double xIndex = x / Game.TILES_SIZE;
        double yIndex = y / Game.TILES_SIZE;

        int value = lvlData[(int) yIndex][(int) xIndex];

        if (value == LoadSave.BLANK_TILE_ID || IsSlope(value))
            return false;

        if (value < 0 || value >= Game.TILES_IN_WIDTH * Game.TILES_IN_HEIGHT)
            return true;

        return true;
    }

    public static boolean IsSlope(int tileValue) {
        int[] slopes = {105, 106, 107, 108, 147, 148, 149, 150};
        for (int s : slopes)
            if (s == tileValue) return true;
        return false;
    }

    public static float GetSlopeY(float x, int tileValue) {
        float xInTile = x % Game.TILES_SIZE;

        switch (tileValue) {
            case 105: case 147:
                // Upwards Part 1: Bottom-Left to Middle-Right (Height: 1.0 to 0.5)
                return Game.TILES_SIZE - (xInTile * 0.5f);
            case 106: case 148:
                // Upwards Part 2: Middle-Left to Top-Right (Height: 0.5 to 0.0)
                return (Game.TILES_SIZE * 0.5f) - (xInTile * 0.5f);
            case 107: case 149:
                // Downwards Part 1: Top-Left to Middle-Right (Height: 0.0 to 0.5)
                return xInTile * 0.5f;
            case 108: case 150:
                // Downwards Part 2: Middle-Left to Bottom-Right (Height: 0.5 to 1.0)
                return (Game.TILES_SIZE * 0.5f) + (xInTile * 0.5f);
        }
        return 0;
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitBox, float xSpeed) {
        if (xSpeed > 0) {
            // Moving Right
            int currentTile = (int) ((hitBox.x + hitBox.width) / Game.TILES_SIZE);
            return currentTile * Game.TILES_SIZE - hitBox.width;
        } else {
            // Moving Left
            int currentTile = (int) (hitBox.x / Game.TILES_SIZE);
            return (currentTile + 1) * Game.TILES_SIZE;
        }
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitBox, float ySpeed) {
        if (ySpeed > 0) {
            // Falling
            int currentTile = (int) ((hitBox.y + hitBox.height) / Game.TILES_SIZE);
            return currentTile * Game.TILES_SIZE - hitBox.height;
        } else {
            // Jumping/Hitting Roof
            int currentTile = (int) (hitBox.y / Game.TILES_SIZE);
            return (currentTile + 1) * Game.TILES_SIZE;
        }
    }
}

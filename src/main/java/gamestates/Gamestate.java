package gamestates;

/**
 * The Gamestate enum represents the different states the game can be in.
 */
public enum Gamestate {
    /** The game is in the playing state. */
    PLAYING,
    /** The game is in the menu state. */
    MENU,
    /** The game is in the quit state. */
    QUIT,
    /** The game is in the login state. */
    LOGIN;

    /** The current state of the game. */
    public static Gamestate state = LOGIN;
}

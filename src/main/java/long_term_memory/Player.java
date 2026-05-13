package long_term_memory;

/**
 * This class simply creates player object with name and score;
 */
public class Player implements Comparable<Player>{
    private String name;
    private int score;

    /**
     * Creates a player with the given name and score;
     * @param name the name of the player
     * @param score the score of the player
     */
    public Player(String name, int score){
        this.name = name;
        this.score = score;
    }

    /**
     * returns the name of the player
     * @return the name of the player
     */
    public String getName(){
        return name;
    }

    /**
     * returns the score of the player
     * @return the score of the player
     */
    public int getScore(){
        return score;
    }

    /**
     * Compares players with their scores. The player with a higher score comes first.
     * @param other other player.
     * @return a negative integer if this player has a higher score than the other, zero if scores are equal, or a positive integer if this player has a lower score than the other.
     */
    public int compareTo(Player other){
        return Integer.compare(other.score, this.score);
    }
}

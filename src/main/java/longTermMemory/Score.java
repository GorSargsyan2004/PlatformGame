package longTermMemory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This class is for interacting with a text file that keeps the score record of the player.
 */
public class Score {
    private int score;
    //Constructors

    /**
     * This is the only constructor, just call.
     */
    public Score() {
        //input creation.
        Scanner fileIn = null;
        try{
           fileIn = new Scanner(new FileInputStream("data/bestScore.txt"));
        }catch(FileNotFoundException e){
            System.err.println("Problem opening the file");
            System.exit(0);
        }
        score = fileIn.nextInt();
        fileIn.close();
    }

    /**
     * Returns the best recorded score.
     * @return the best recorded score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the given score as the best recorded score, no matter if it is larger than the previous best recorded score.
     * @param score The score to be set as the best recorded score.
     */
    public void setScore(int score) {
        this.score = score;
        createNewPrintWriter(this.score);
    }

    /**
     * Compares the given score with the best recorded score, and if the given score is larger than the best recorded score, it updates the best recorded score.
     * @param score the score to be compared, and if larger than the best recorded score, to be set.
     */
    public void tryUpdateScore(int score) {
        if(score > this.score){
            setScore(this.score);
        }
    }
    private void createNewPrintWriter(int score){
        PrintWriter inp = null;
        try{
            inp = new PrintWriter(new FileOutputStream("data/bestScore.txt"));

        }catch(FileNotFoundException e){
            System.err.println("Problem opening the file");
            System.exit(0);
        }
        inp.println(score);
        inp.close();
    }
//    public static void main(String[] args){
//        Score score = new Score();
//        int score1 = 10;
//        score.tryUpdateScore(score1);
//        System.out.println(score.getScore());
//
//        score1 = 90;
//        score.tryUpdateScore(score1);
//        System.out.println(score.getScore());
//
//        score1 = 100;
//        score.tryUpdateScore(score1);
//        System.out.println(score.getScore());
//
//        score1 = 999;
//        score.tryUpdateScore(score1);
//        System.out.println(score.getScore());
//    }
}

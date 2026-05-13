package long_term_memory;

import long_term_memory.UserManagerExceptions.IncorrectFormatException;
import long_term_memory.UserManagerExceptions.PasswordMismatchException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Leaderboard {
    private ArrayList<Player> topPlayers;
    private final String fileLocation;

    /**
     * Given the fileLocation of a text file with the format "name:password:score", creates a sorted ArrayList of Player object, where the players with higher scores come fist.
     * @param fileLocation the location of the file.
     */
    public Leaderboard(String fileLocation) {
        topPlayers = new ArrayList<>(10);
        this.fileLocation = fileLocation;
        addPlayersToTheList();
    }

    private void addPlayersToTheList(){
        Scanner sc = null;
        try{
            sc = new Scanner(new FileInputStream(fileLocation));
        }
        catch(
                FileNotFoundException e){
            System.out.println("File not found");
            System.exit(0);
        }
        String userLineInStorage;
        String usernameInStorage;
        int userScoreInStorage;
        String[] userLineComponents;
        while(sc.hasNextLine()){
            userLineInStorage = sc.nextLine();
            userLineComponents = userLineInStorage.split(":");
            usernameInStorage = userLineComponents[0];
            userScoreInStorage = Integer.parseInt(userLineComponents[2]);
            topPlayers.add(new Player(usernameInStorage, userScoreInStorage));
        }
        sc.close();
        Collections.sort(topPlayers);
    }

    /**
     * return the sorted arraylist of Player objects.
     * @return the sorted arraylist of Player objects.
     */
    public ArrayList<Player> getPlayersSorted() {
        return topPlayers;
    }
}

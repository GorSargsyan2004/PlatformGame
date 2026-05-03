package long_term_memory;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

/**
 * This class manages logging in, registering, and best score.
 */
public class UserManager {
    private String username;
    private String password;
    private int score;
    private int currScore = 0;
    private final String fileLocation;
    private boolean loggedIn;

    //constructors

    /**
     *
     * @param fileLocation the location of the file where user info should be stored in this format line by line: username:password:score. Do not care about the format and do not change anything in the file, this class does it automatically.
     */
    public UserManager(String fileLocation){
        this.fileLocation = fileLocation;
        loggedIn = false;
    }
    //methods

    /**
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return Ture if the registration was successful. Otherwise, returns false.
     */
    public boolean register(String username, String password){
        if(doesUsernameExist(username)) return false;
        this.username=username;
        this.password=password;
        this.score=0;
        PrintWriter pw = null;
        try{
            pw = new PrintWriter(new FileOutputStream(fileLocation,true), true);
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
            System.exit(0);
        }
        pw.println(this.username+":"+this.password+":"+this.score);
        pw.close();
        loggedIn=true;
        return true;
    }

    /**
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return Ture if the login was successful. Otherwise, returns false.
     */
    public boolean login(String username, String password){
        if(!doesUsernameExist(username)) return false;
        Scanner sc = null;
        try{
            sc = new Scanner(new FileInputStream(fileLocation));
        }
        catch(FileNotFoundException e){
            System.out.println("File not found");
            System.exit(0);
        }
        String userLineInStorage;
        String usernameInStorage;
        String passwordInStorage;
        String[] userLineComponents;
        while(sc.hasNextLine()){
            userLineInStorage = sc.nextLine();
            userLineComponents = userLineInStorage.split(":");
            usernameInStorage = userLineComponents[0];
            passwordInStorage = userLineComponents[1];
            if(usernameInStorage.equals(username)){
                if(passwordInStorage.equals(password)){
                    this.username=username;
                    this.password=password;
                    this.score=Integer.parseInt(userLineComponents[2]);
                    sc.close();
                    loggedIn=true;
                    return true;
                }
                sc.close();
                return false;
            }
        }
        sc.close();
        return false;
    }

    public int getCurrentScore() {
        if(!loggedIn){
            System.out.println("Before getting the score please log in or register.");
            System.exit(0);
        }
        return score;
    }

    /**
     * precondition: before using this method, the user should be already registered/logged in.
     * Adds scores to the current score to store the current score of the user.
     */
    public void addToCurrScore(int adder) {
        if(!loggedIn){
            System.out.println("Before getting the score please log in or register.");
            System.exit(0);
        }
        currScore += adder;
    }

    /**
     * precondition: before using this method, the user should be already registered/logged in.
     * Returns current score of the user.
     */
    public int getCurrScore() {
        if(!loggedIn){
            System.out.println("Before getting the score please log in or register.");
            System.exit(0);
        }
        return currScore;
    }

    /**
     * precondition: before using this method, the user should be already registered/logged in.
     * Returns weather the user passed the best score and will have now new best score.
     */
    public boolean isPassedBestScore() { return currScore > score; }

    /**
     * precondition: before using this method, the user should be already registered/logged in. This will return the user's own best score.
     * Returns the best recorded score of the user.
     * @return the best recorded score of the user.
     */
    public int getScore() {
        if(!loggedIn){
            System.out.println("Before getting the score please log in or register.");
            System.exit(0);
        }
        return score;
    }

    /**
     * precondition: before using this method, the user should be already registered/logged in. This will modify the user's own best score.
     * Sets the given score as the best recorded score, no matter if it is larger than the previous best recorded score.
     * @param score The score to be set as the best recorded score.
     */
    public void setScore(int score) {
        if(!loggedIn){
            System.out.println("Before setting the score please log in or register.");
            return;
        }

        Scanner sc = null;
        try{
            sc = new Scanner(new FileInputStream(fileLocation));
        }
        catch(FileNotFoundException e){
            System.out.println("File not found");
            System.exit(0);
        }

        StringBuilder wholeUserInfo = new StringBuilder();
        while(sc.hasNextLine()){
            String currentLine = sc.nextLine();
            String currentUsername = currentLine.split(":")[0];
            if(currentUsername.equals(username)){
                this.score=score;
                wholeUserInfo.append(username).append(":").append(password).append(":").append(this.score).append("\n");
            } else {
                wholeUserInfo.append(currentLine).append("\n");
            }
        }
        sc.close();

        PrintWriter pw = null;
        try{
            pw = new PrintWriter(new FileOutputStream(fileLocation), true);
        }catch (FileNotFoundException e){
            System.out.println("File not found");
            System.exit(0);
        }
        pw.print(wholeUserInfo.toString());
        pw.close();
    }

    public void setRecord() {
        if (isPassedBestScore()) setScore(currScore);
    }

    /**
     * precondition: before using this method, the user should be already registered/logged in. This will modify the user's own best score.
     * Compares the given score with the best recorded score, and if the given score is larger than the best recorded score, it updates the best recorded score.
     * @param score the score to be compared, and if larger than the best recorded score, to be set.
     */
    public void tryUpdateScore(int score) {
        if(score > this.score){
            setScore(score);
        }
    }

    //checks to see if the userName exists in the storage.
    private boolean doesUsernameExist(String username){

        Scanner sc = null;
        try{
            sc = new Scanner(new FileInputStream(fileLocation));
        }
        catch(FileNotFoundException e){
            System.out.println("File not found");
            System.exit(0);
        }
        while(sc.hasNextLine()){
            String userNameInStorage = sc.nextLine().split(":")[0];
            if(userNameInStorage.equals(username)){
                sc.close();
                return true;
            }
        }
        sc.close();
        return false;
    }

    /**
     * precondition: before using this method, the user should be registered/logged in.
     * Returns the username of the user.
     * @return the username of the user.
     */
    public String getUsername(){
        if(!loggedIn){
            System.out.println("Before getting the username please log in or register.");
            System.exit(0);
        }
        return this.username;
    }

    //test
//    public static void main(String[] args){
//
//    }
}

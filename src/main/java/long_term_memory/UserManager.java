package long_term_memory;
import long_term_memory.UserManagerExceptions.*;
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
    public boolean register(String username, String password) throws UserNameAlreadyExistsException {
        if(doesUsernameExist(username)) throw new UserNameAlreadyExistsException();
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
    public boolean login(String username, String password) throws UserNameDoesnotExistException, PasswordMismatchException, IncorrectFormatException {
        if(!doesUsernameExist(username)) throw new UserNameDoesnotExistException();
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
            if (userLineComponents.length < 3) {
                sc.close();
                throw new IncorrectFormatException("Corrupted data in storage.");
            }
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
                throw new PasswordMismatchException();
            }
        }
        sc.close();
        return false;
    }

    public int getCurrentScore() throws NotRegisteredOrLoggedInException {
        if(!loggedIn){
            throw new NotRegisteredOrLoggedInException("Before getting the score please log in or register.");
        }
        return score;
    }

    /**
     * precondition: before using this method, the user should be already registered/logged in.
     * Adds scores to the current score to store the current score of the user.
     */
    public void addToCurrScore(int adder) throws NotRegisteredOrLoggedInException {
        if(!loggedIn){
            throw new NotRegisteredOrLoggedInException("Before getting the score please log in or register.");
        }
        currScore += adder;
    }

    /**
     * precondition: before using this method, the user should be already registered/logged in.
     * Returns current score of the user.
     */
    public int getCurrScore() throws NotRegisteredOrLoggedInException {
        if(!loggedIn){
            throw new NotRegisteredOrLoggedInException("Before getting the score please log in or register.");
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
    public int getScore() throws NotRegisteredOrLoggedInException {
        if(!loggedIn){
            throw new NotRegisteredOrLoggedInException("Before getting the score please log in or register.");
        }
        return score;
    }

    /**
     * precondition: before using this method, the user should be already registered/logged in. This will modify the user's own best score.
     * Sets the given score as the best recorded score, no matter if it is larger than the previous best recorded score.
     * @param score The score to be set as the best recorded score.
     */
    public void setScore(int score) throws NotRegisteredOrLoggedInException {
        if(!loggedIn){
            throw new NotRegisteredOrLoggedInException("Before setting the score please log in or register.");
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

    public void setRecord() throws NotRegisteredOrLoggedInException {
        if (isPassedBestScore()) setScore(currScore);
    }

    /**
     * precondition: before using this method, the user should be already registered/logged in. This will modify the user's own best score.
     * Compares the given score with the best recorded score, and if the given score is larger than the best recorded score, it updates the best recorded score.
     * @param score the score to be compared, and if larger than the best recorded score, to be set.
     */
    public void tryUpdateScore(int score) throws NotRegisteredOrLoggedInException {
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
    public String getUsername() throws NotRegisteredOrLoggedInException {
        if(!loggedIn){
            throw new NotRegisteredOrLoggedInException("Before getting the username please log in or register.");
        }
        return this.username;
    }

    //test
//    public static void main(String[] args){
//
//    }
}

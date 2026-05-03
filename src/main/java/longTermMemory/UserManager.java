package longTermMemory;
import longTermMemory.UserManagerExceptions.*;

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
    private boolean formatCheck(String str){
        return str.length() >= 4 && !str.contains(":");
    }

    /**
     * @param username The username of the user.
     * @param password The password of the user.
     * @throws UserNameAlreadyExistsException If the given username already exists.
     * @throws IncorrectFormatException If the username or password do not follow the formatting rules.
     */
    public void register(String username, String password) throws UserNameAlreadyExistsException, IncorrectFormatException{
        if(!formatCheck(username)) throw new IncorrectFormatException("Username must contain at least 4 characters. ':' not allowed");
        if(!formatCheck(password)) throw new IncorrectFormatException("Password must contain at least 4 characters. ':' not allowed");
        if(doesUsernameExist(username)) throw new UserNameAlreadyExistsException("That username already exists.");
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
    }

    /**
     * @param username The username of the user.
     * @param password The password of the user.
     * @throws UserNameDoesnotExistException if the given username does not exist.
     * @throws PasswordMismatchException if the password does not match.
     */
    public void login(String username, String password) throws UserNameDoesnotExistException, PasswordMismatchException{
        if(!doesUsernameExist(username)) throw new UserNameDoesnotExistException("That username does not exist.");
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
        String[] userLineComponents = new String[3];
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
                }else{
                    throw new PasswordMismatchException("Incorrect Password");
                }
                sc.close();
            }
        }
        sc.close();
    }


    /**
     * precondition: before using this method, the user should be already registered/logged in. This will return the user's own best score.
     * Returns the best recorded score of the user.
     * @return the best recorded score of the user.
     * @throws NotRegisteredOrLoggedInException if the user is not registered or logged in.
     */
    public int getScore() throws NotRegisteredOrLoggedInException{
        if(!loggedIn) throw new NotRegisteredOrLoggedInException("The user is not registered or logged in.");
        return score;
    }

    /**
     * precondition: before using this method, the user should be already registered/logged in. This will modify the user's own best score.
     * Sets the given score as the best recorded score, no matter if it is larger than the previous best recorded score.
     * @param score The score to be set as the best recorded score.
     * @throws NotRegisteredOrLoggedInException if the user is not registered or logged in.
     */
    public void setScore(int score) throws NotRegisteredOrLoggedInException{
        if(!loggedIn) throw new NotRegisteredOrLoggedInException("The user is not registered or logged in.");
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
                wholeUserInfo.append(username+":"+password+":"+this.score+"\n");
            }else{
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
    /**
     * precondition: before using this method, the user should be already registered/logged in. This will modify the user's own best score.
     * Compares the given score with the best recorded score, and if the given score is larger than the best recorded score, it updates the best recorded score.
     * @param score the score to be compared, and if larger than the best recorded score, to be set.
     */
    public void tryUpdateScore(int score) throws NotRegisteredOrLoggedInException {
        if(!loggedIn) throw new NotRegisteredOrLoggedInException("The user is not registered or logged in.");
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
     * @throws NotRegisteredOrLoggedInException if the user is not registered or logged in.
     */
    public String getUsername() throws NotRegisteredOrLoggedInException{
        if(!loggedIn) throw new NotRegisteredOrLoggedInException("The user is not registered or logged in.");
        return this.username;
    }

    //test
//    public static void main(String[] args){
//
//    }
}

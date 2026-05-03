package longTermMemory.UserManagerExceptions;

public class UserNameDoesnotExistException extends UserManagerException {
    public UserNameDoesnotExistException(){
        super("Logging failed.");
    }
    public UserNameDoesnotExistException(String message){
        super(message);
    }
}

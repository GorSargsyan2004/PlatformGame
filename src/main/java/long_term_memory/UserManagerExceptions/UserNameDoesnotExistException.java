package long_term_memory.UserManagerExceptions;

public class UserNameDoesnotExistException extends UserManagerException {
    public UserNameDoesnotExistException(){
        super("Logging failed.");
    }
    public UserNameDoesnotExistException(String message){
        super(message);
    }
}

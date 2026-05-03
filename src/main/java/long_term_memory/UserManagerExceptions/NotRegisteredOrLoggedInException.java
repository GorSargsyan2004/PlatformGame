package long_term_memory.UserManagerExceptions;

public class NotRegisteredOrLoggedInException extends UserManagerException {
    public NotRegisteredOrLoggedInException(){
        super("User is not logged in or registered.");
    }
    public NotRegisteredOrLoggedInException(String message){
        super(message);
    }
}

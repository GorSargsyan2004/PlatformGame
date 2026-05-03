package long_term_memory.UserManagerExceptions;

public class UserManagerException extends Exception{
    public UserManagerException(){
        super("user manager exception.");
    }
    public UserManagerException(String message){
        super(message);
    }
}

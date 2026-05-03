package long_term_memory.UserManagerExceptions;

public class PasswordMismatchException extends UserManagerException {
    public PasswordMismatchException(){
        super("Password did not match");
    }
    public PasswordMismatchException(String message){
        super(message);
    }
}

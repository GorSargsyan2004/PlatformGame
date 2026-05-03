package long_term_memory.UserManagerExceptions;

public class IncorrectFormatException extends UserManagerException {
    public IncorrectFormatException(){
        super("Incorrect format.");
    }
    public IncorrectFormatException(String message){
        super(message);
    }
}

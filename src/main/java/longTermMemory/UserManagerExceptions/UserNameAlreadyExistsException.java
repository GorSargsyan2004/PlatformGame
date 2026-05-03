package longTermMemory.UserManagerExceptions;

public class UserNameAlreadyExistsException extends UserManagerException {
    public UserNameAlreadyExistsException(){
        super("Registration failed.");
    }
    public UserNameAlreadyExistsException(String message){
        super(message);
    }
}

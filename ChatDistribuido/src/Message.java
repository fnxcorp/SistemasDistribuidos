
import java.io.Serializable;

public class Message implements Serializable{

    private final String user;
    private final String message;

    public Message(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s", user, message);
    }
    
}

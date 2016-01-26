import java.util.LinkedList;
import java.util.PriorityQueue;

public class User {
    private int userID;
    private String IP;

    private LinkedList<String> messageQueue;

    public User(int userID, String IP){
        messageQueue = new LinkedList<>();
        this.IP = IP;
        this.userID = userID;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    public LinkedList<String> getMessageQueue() {
        return messageQueue;
    }
}

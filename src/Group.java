import java.util.ArrayList;

public class Group {
    private int groupID;
    private ArrayList<User> users;


    public Group(int groupID){
        users = new ArrayList<>();
        this.groupID = groupID;
    }

    public void addUser(User user){
        users.add(user);
    }

    public ArrayList<User> getUsers(){
        return users;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }
}


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Main {

    private static ServerSocket serverSocket;
    private static Socket clientSocket;



    public static void main(String[] args) {


        ConnectionHandler.groups.add(new Group(1));
        try {
            serverSocket = new ServerSocket(4444); // Server socket

        } catch (IOException e) {
            System.out.println("Could not listen on port: 4444");
        }

        System.out.println("Server started. Listening to the port 4444");

        try {
            System.out.println(serverSocket.getInetAddress().getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        while (true) {
            try {

                clientSocket = serverSocket.accept(); // accept the client connection
                Runnable connectionHandler = new ConnectionHandler(clientSocket);
                new Thread(connectionHandler).start();

            } catch (IOException ex) {
                System.out.println("Problem in message reading");
            }
        }

    }

}

class ConnectionHandler implements Runnable{

    private Socket clientSocket;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private String message;

    static ArrayList<Group> groups = new ArrayList<>();
    private static ArrayList<User> users = new ArrayList<>();

    public ConnectionHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader); // get the client message
            message = bufferedReader.readLine();

            System.out.println(message);
            processMessage(message);
            inputStreamReader.close();
            clientSocket.close();
        } catch (IOException ex) {
            System.out.println("Problem in message reading");
        }

    }


    private void processMessage(String message){
        if(message!=null) {

            String[] lines = message.split(":");
            String command = lines[0];


            int userID = Integer.parseInt(lines[1]);
            if (command.equals(Commands._newUser)) {
                if (getUser(userID) == null)
                    users.add(new User(userID, clientSocket.getRemoteSocketAddress().toString()));
            } else {
                int groupID = Integer.parseInt(lines[2]);
                if (command.equals(Commands._newGroup)) {
                    Group group = new Group(groupID);
                    group.addUser(getUser(userID));
                    groups.add(group);
                } else if (command.equals(Commands._connectToGroup)) {
                    Group group = getGroup(groupID);
                    group.addUser(getUser(userID));
                } else {
                    if (!command.equals(Commands._sync)) {
                        String data = lines[3];
                        sendMessage(groupID, userID, message);
                    } else {
                        sync(userID);
                    }
                }
            }
        }
    }


    private Group getGroup(int ID){
        for (Group group :
                groups) {
            if(group.getGroupID()==ID){
                return group;
            }
        }

        return null;
    }

    private User getUser(int ID){
        for (User user :
                users) {
            if (user.getUserID() == ID) {
                return user;
            }
        }
        return null;
    }

    private void sendMessage(int groupID, int userID, String message){
        Group group = getGroup(groupID);

        ArrayList<User> users = group.getUsers();

        for(User user:
                users){
            if(user!=null && user.getUserID()!=userID){
                System.out.println("adding "+ userID + "message-"+message);
                user.getMessageQueue().add(message);
            }
        }
    }

    private void sync(int userID){
        try {
            User user = getUser(userID);
            if (user != null) {
                PrintWriter printwriter = new PrintWriter(clientSocket.getOutputStream(), true);

                LinkedList<String> messageQueue = user.getMessageQueue();
                while (messageQueue != null && messageQueue.peek()!=null) {
                    System.out.println("sending " + userID + "message-" + messageQueue.peek());
                    printwriter.println(messageQueue.poll());
                    printwriter.flush();
                }
                printwriter.print("e");

                printwriter.flush();
                printwriter.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
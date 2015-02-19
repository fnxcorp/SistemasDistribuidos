
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;

public class ChatDBImpl extends UnicastRemoteObject implements ChatDB {

    ArrayList<Message> messages;
    HashSet<String> users;
    String registry = "localhost";
    int port = 1099;
    String serverId;
    final String RMI_URL = "rmi://%s:%s/%s"; // rmi://registry:port/service
    ServerManagement serverManagementRef;

    public ChatDBImpl(String serverId, String registry, int port, ServerManagement serverManagementRef)
            throws RemoteException {
        messages = new ArrayList<>();
        users = new HashSet<>();
        this.serverId = serverId;
        this.registry = registry;
        this.port = port;
        this.serverManagementRef = serverManagementRef;
    }

    public ChatDBImpl(String serverId, String registry, int port, ServerManagement serverManagementRef,
            ArrayList<Message> messages, HashSet<String> users) throws RemoteException {
        this.serverId = serverId;
        this.registry = registry;
        this.port = port;
        this.messages = messages;
        this.users = users;
        this.serverManagementRef = serverManagementRef;
    }

    private void replicateMessage(Message msg) {
        try {
            //get reference to ServerManagement service and send message via replicateMessage method
            serverManagementRef.replicateMessage(msg, serverId);
        } catch (RemoteException ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void addMessage(String user, String message) throws RemoteException {
        Message msg = new Message(user, message);
        messages.add(msg);
        replicateMessage(msg);
    }

    @Override
    public void addReplicaMessage(String user, String message) throws RemoteException {
        Message msg = new Message(user, message);
        messages.add(msg);
    }

    @Override
    public ArrayList<Message> getMessages() throws RemoteException {
        return messages;
    }

    private void replicateUsers(String userName, boolean remove) {
        try {
            //get reference to ServerManagement service and send message via replicateUser method
            serverManagementRef.replicateUser(userName, remove, serverId);
        } catch (RemoteException ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public boolean addUser(String userName) throws RemoteException {
        replicateUsers(userName, false);
        return users.add(userName);
    }

    @Override
    public boolean addReplicaUser(String userName) throws RemoteException {
        return users.add(userName);
    }

    @Override
    public boolean removeUser(String userName) throws RemoteException {
        replicateUsers(userName, false);
        return users.remove(userName);
    }

    @Override
    public boolean removeReplicaUser(String userName) throws RemoteException {
        return users.remove(userName);
    }

    @Override
    public HashSet<String> getUsers() throws RemoteException {
        return users;
    }

}

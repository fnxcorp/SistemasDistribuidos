
import java.util.ArrayList;
import java.util.HashSet;
import java.rmi.Remote;

public interface ChatDB extends Remote {

    public void addMessage(String user, String message) throws java.rmi.RemoteException;

    public void addReplicaMessage(String user, String message) throws java.rmi.RemoteException;

    public ArrayList<Message> getMessages() throws java.rmi.RemoteException;

    public boolean addUser(String userName) throws java.rmi.RemoteException;

    public boolean addReplicaUser(String userName) throws java.rmi.RemoteException;

    public boolean removeUser(String username) throws java.rmi.RemoteException;

    public boolean removeReplicaUser(String userName) throws java.rmi.RemoteException;

    public HashSet<String> getUsers() throws java.rmi.RemoteException;
}

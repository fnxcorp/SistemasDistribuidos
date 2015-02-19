
import java.rmi.Remote;

public interface ServerManagement extends Remote {

    public void replicateMessage(Message message, String remittent) throws java.rmi.RemoteException;

    public void replicateUser(String userName, boolean remove, String remittent) throws java.rmi.RemoteException;

    public boolean addBackupServer(String rmiUrl) throws java.rmi.RemoteException;

    public boolean removeBackupServer(String rmiUrl) throws java.rmi.RemoteException;

    public String getNextAvailableServer() throws java.rmi.RemoteException;
}

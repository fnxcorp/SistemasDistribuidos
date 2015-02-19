
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedHashMap;

public class ServerManagementImpl extends UnicastRemoteObject implements ServerManagement {

    //change to LinkedHashMap key will be the rmiUrl and the value will be the Remote Reference
    LinkedHashMap<String, ChatDB> availableBackupServers;
    String registry = "localhost";
    int port = 1099;

    public ServerManagementImpl(String registry, int port) throws RemoteException {
        this.registry = registry;
        this.port = port;
        availableBackupServers = new LinkedHashMap<>();
    }

    @Override
    public void replicateMessage(Message message, String remittent) throws RemoteException {
        //send message to all backup servers except the remitent
        for (String next : availableBackupServers.keySet()) {
            if (!next.contains(remittent)) {
                System.out.printf("replica from %s to %s%n", remittent, next.substring(next.lastIndexOf("/") + 1));
                ChatDB reference = availableBackupServers.get(next);
                reference.addReplicaMessage(message.getUser(), message.getMessage());
            }
        }
    }

    @Override
    public void replicateUser(String userName, boolean remove, String remittent) throws RemoteException {
        //send user update to all backup servers except the remitent
        for (String next : availableBackupServers.keySet()) {
            if (!next.contains(remittent)) {
                ChatDB reference = availableBackupServers.get(next);
                if (!remove) {
                    reference.addReplicaUser(userName);
                } else {
                    reference.removeReplicaUser(userName);
                }
            }
        }
    }

    @Override
    public boolean addBackupServer(String rmiUrl) throws RemoteException {
        try {
            //connect to rmiUrl and get te remoteRef, put remoteRef (ChatDB) as value
            System.out.println("Chat Server RMI URL: " + rmiUrl);
            Remote remoteService = Naming.lookup(rmiUrl);
            ChatDB service = (ChatDB) remoteService;
            return availableBackupServers.put(rmiUrl, service) != null;
        } catch (NotBoundException | MalformedURLException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }

    @Override
    public boolean removeBackupServer(String rmiUrl) throws RemoteException {
        try {
            //disconnect to remote service
            Naming.unbind(rmiUrl);
        } catch (NotBoundException | MalformedURLException ex) {
//            ex.printStackTrace(System.err);
            System.out.printf("Already Unbind (%s) - %s : %s%n", rmiUrl, ex.getClass(), ex.getMessage());
        }
        return availableBackupServers.remove(rmiUrl) == null;
    }

    @Override
    public String getNextAvailableServer() throws RemoteException {
        //Send the first server in the set, as is a linked set, all clients should get the same server :)
        for (String next : availableBackupServers.keySet()) {
            return next;
        }
        return null;
    }

}

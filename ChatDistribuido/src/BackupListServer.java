
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class BackupListServer {

    static String registry = "localhost";
    static int port = 1099;
    static final String RMI_URL = "rmi://%s:%s/%s"; // rmi://registry:port/service

    public static void main(String args[]) {
        System.out.println("Loading RMI Backup Service");
        try {
            if (args.length >= 1) {
                registry = args[0];
                if (args.length >= 2) {
                    port = Integer.parseInt(args[1]);
                }
            }
            String serverListReg = String.format(RMI_URL, registry, port, "ServerList");
            //Create the ServerManagement shared object
            ServerManagementImpl service = new ServerManagementImpl(registry, port);
            Naming.rebind(serverListReg, service);
        } catch (RemoteException | MalformedURLException ex) {
            ex.printStackTrace(System.err);
        }
    }

}

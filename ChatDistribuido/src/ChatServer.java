
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer {

    static String registry = "localhost";
    static int port = 1099;
    //main BackuseSverList ip
    static String remoteRegistry = "localhost";
    static int remotePort = 1099;
    static final String RMI_URL = "rmi://%s:%s/%s"; // rmi://registry:port/service

    static int getChatServerNumber() {
        int n = 0;
        try {
            final Registry re = LocateRegistry.getRegistry(remoteRegistry, remotePort);
            final String[] boundNames = re.list();
            for (final String name : boundNames) {
                if (name.contains("Chat")) {
                    System.out.println(name);
                    int prev = Integer.parseInt(name.substring(4));
                    n = Math.max(n, prev + 1);
                    System.out.println("Chat" + n);
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return n;
    }

    public static void main(String args[]) {
        System.out.println("Loading RMI Chat Service");
        try {
            if (args.length >= 1) {
                registry = args[0];
                if (args.length >= 2) {
                    port = Integer.parseInt(args[1]);
                    if (args.length >= 3) {
                        remoteRegistry = args[2];
                        if (args.length >= 4) {
                            remotePort = Integer.parseInt(args[3]);
                        }
                    }
                }
            }
            int n = getChatServerNumber();
            String chatRegistration = String.format(RMI_URL, registry, port, "Chat" + n);
            String serverListReg = String.format(RMI_URL, remoteRegistry, remotePort, "ServerList");

            //connect to BackupListServer to add itself as a backup server
            ServerManagement serverListService = (ServerManagement) Naming.lookup(serverListReg);

            String mainServer = serverListService.getNextAvailableServer();
            System.out.println("Main Server: " + mainServer);
            //Create the ChatDB shared object
            //connect to serverListReg.nextAvailableServer and initialize with its messages and users;
            ChatDBImpl service;
            if (mainServer == null) {
                service = new ChatDBImpl("Chat" + n, registry, port, serverListService);
            } else {
                //if exists connect to main server to get old messages and start this backup server with that data
                Remote remoteService = Naming.lookup(mainServer);
                ChatDB mainService = (ChatDB) remoteService;
                ArrayList<Message> msgs = mainService.getMessages();
                HashSet<String> users = mainService.getUsers();
                System.out.printf("Starting with %d users and %d messages%n", users.size(), msgs.size());
                service = new ChatDBImpl("Chat" + n, registry, port, serverListService, msgs, users);
                //Naming.unbind(mainServer);
            }
            Naming.rebind(chatRegistration, service);
            serverListService.addBackupServer(chatRegistration);
        } catch (RemoteException | MalformedURLException | NotBoundException re) {
            re.printStackTrace(System.err);
        }
    }

}

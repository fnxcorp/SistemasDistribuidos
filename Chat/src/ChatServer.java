
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteRef;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Usuario
 */
public class ChatServer {

    public static void main(String args[]) {
        System.out.println("Loading RMI Service");
        int cont = 0;
        try {

            // System.out.println(location.remoteToString());
            String registry = "localhost";
            if (args.length >= 1) {
                registry = args[0];
            }
            final Registry re = LocateRegistry.getRegistry(registry);
            final String[] boundNames = re.list();
            boolean unique = true;
            int n = 0;
            for (final String name : boundNames) {
                if (name.contains("Chat")) {
                    unique = false;
                    n = Integer.parseInt(name.substring(4));
                    if (cont <= n) {
                        cont = n + 1;
                    }
                    System.out.println(n);
                }

            }
            ChatImpl service = null;
            if (!unique) {
                String registration = "rmi://" + registry + "/Chat" + n;
                Remote remoteService;

                try {
                    remoteService = Naming.lookup(registration);
                    Chat tmp = (Chat) remoteService;
                    service = new ChatImpl("Chat" + cont, tmp.getMessagesList());
                } catch (NotBoundException ex) {
                    Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                service = new ChatImpl("Chat" + cont);
            }
            //RemoteRef location = service.getRef();
            String registration = "rmi://" + registry + "/Chat" + cont;
            Naming.rebind(registration, service);
        } catch (RemoteException re) {
            System.err.println(
                    "Remote Error -" + re
            );
        } catch (MalformedURLException e) {
            System.err.println("Error - " + e);
        }
    }
}


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteRef;

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
            ChatImpl service = new ChatImpl();
            RemoteRef location = service.getRef();
            System.out.println(location.remoteToString());
            String registry = "localhost";
            if (args.length >= 1) {
                registry = args[0];
            }
            final Registry re = LocateRegistry.getRegistry(registry);
            final String[] boundNames = re.list();
            for (final String name : boundNames) {
                if (name.contains("Chat")) {
                    cont++;
                }

            }
            String registration = "rmi://" + registry + "/Chat"+cont;
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


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
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
        try {
            ChatImpl service = new ChatImpl();
            RemoteRef location = service.getRef();
            System.out.println(location.remoteToString());
            String registry = "localhost";
            if (args.length >= 1) {
                registry = args[0];
            }
            String registration = "rmi://" + registry + "/Chat";
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

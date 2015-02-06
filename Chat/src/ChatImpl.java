
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Usuario
 */
public class ChatImpl extends java.rmi.server.UnicastRemoteObject implements Chat {

    ArrayList<Message> messages;
    private ArrayList<Chat> service;

//    private void initReplication() {
//
//        System.out.println("Looking for chat service");
//        try {
//            String registry = "localhost";
//            //test
//            final Registry re = LocateRegistry.getRegistry(registry);
//            final String[] boundNames = re.list();
//
//            for (final String name : boundNames) {
//                if (name.contains("Chat")) {
//                    String registration = "rmi://" + registry + "/" + name;
//                    Remote remoteService = Naming.lookup(registration);
//                    service.add((Chat) remoteService);
//                }
//            }
//
//        } catch (NotBoundException nbe) {
//            System.out.println("No  service available in registry!");
//        } catch (RemoteException re) {
//            System.out.println("RMI Error - " + re);
//        } catch (MalformedURLException e) {
//            System.out.println("Error - " + e);
//        }
//    }

    public ChatImpl() throws java.rmi.RemoteException {
        messages = new ArrayList<>();
    }

    @Override
    public void sendMessage(String user, String message) throws RemoteException {
        messages.add(new Message(user, message));
    }

    @Override
    public String getMessages() throws RemoteException {
        //return message
        StringBuilder sb = new StringBuilder();
        for (Message m : messages) {
            sb.append(messages.size()).append(" ").append(m.getUser()).append(": ").append(m.getMessage()).append("\n");
        }
        return sb.toString();
    }

}

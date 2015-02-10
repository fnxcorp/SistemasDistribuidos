
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
public class ChatImpl extends java.rmi.server.UnicastRemoteObject implements Chat {

    ArrayList<Message> messages;
    private ArrayList<Chat> service;
    String serverId;

    public ChatImpl(String serverId) throws java.rmi.RemoteException {
        messages = new ArrayList<>();
        this.serverId = serverId;

        // System.out.println("new ref "+ this.getRef().toString()); 
    }

    public ChatImpl(String serverId, ArrayList<Message> msgList) throws java.rmi.RemoteException {
        messages = msgList;
        this.serverId = serverId;

        // System.out.println("new ref "+ this.getRef().toString()); 
    }

    private void replicateMessage(Message message, RemoteRef ref) {
        if (this.ref.remoteEquals(ref)) {
            System.out.println("Lo hice");
            //test
            String registry = "localhost";
            final Registry re;
            try {
                re = LocateRegistry.getRegistry(registry);
                final String[] boundNames = re.list();

                for (final String name : boundNames) {
                    if (name.contains("Chat")) {
                        String registration = "rmi://" + registry + "/" + name;
                        if (!name.equals(this.serverId)) {
                            Remote remoteService = Naming.lookup(registration);
                            Chat tmp = (Chat) remoteService;
                            tmp.saveMessage(message);

                        }

                    }
                }
            } catch (RemoteException ex) {
                Logger.getLogger(ChatImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotBoundException ex) {
                Logger.getLogger(ChatImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(ChatImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void saveMessage(Message msg) throws RemoteException {

        messages.add(msg);
    }

    @Override
    public void sendMessage(String user, String message) throws RemoteException {
        Message msg = new Message(user, message);
        saveMessage(msg);
        replicateMessage(msg, this.ref);
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

    public ArrayList<Message> getMessagesList() {
        return messages;
    }
    
   

}

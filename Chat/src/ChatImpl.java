
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteRef;
import java.util.ArrayList;
import java.util.HashSet;
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
    HashSet<String> users;
    private ArrayList<Chat> service;
    String serverId;

    public ChatImpl(String serverId) throws java.rmi.RemoteException {
        messages = new ArrayList<>();
        this.serverId = serverId;
        users = new HashSet<>();

        // System.out.println("new ref "+ this.getRef().toString()); 
    }

    public ChatImpl(String serverId, ArrayList<Message> msgList, HashSet<String> users) throws java.rmi.RemoteException {
        messages = msgList;
        this.serverId = serverId;
        this.users = users;

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

    private void replicateUser(String user, RemoteRef ref, boolean addRemove) {
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

                            if (addRemove) {
                                tmp.addUser(user);
                            } else {
                                tmp.disconnectUser(user);
                            }

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
            sb.append("").append(" ").append(m.getUser()).append(": ").append(m.getMessage()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public ArrayList<Message> getMessagesList() {
        return messages;
    }

    @Override
    public HashSet<String> getUserList() {
        return users;
    }

    @Override
    public void disconnectUser(String username) {
        if (users.contains(username)) {
            users.remove(username);
            replicateUser(username, ref, false);
        }
    }

    @Override
    public boolean addUser(String userName) {
        if (users.contains(userName)) {
            return false;
        }
        users.add(userName);
        replicateUser(userName, ref, true);
        return true;
    }

}

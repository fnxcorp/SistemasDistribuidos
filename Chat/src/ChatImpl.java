
import java.rmi.RemoteException;
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
    
    public ChatImpl() throws java.rmi.RemoteException{
        messages= new ArrayList<>();
    }
    
    @Override
    public void sendMessage(String user, String message) throws RemoteException {
        messages.add(new Message(user,message));
    }

    @Override
    public String getMessages() throws RemoteException {
       //return message
        StringBuilder sb= new StringBuilder();
        for(Message m: messages){
            sb.append(messages.size()).append(" ").append(m.getUser()).append(": ").append(m.getMessage()).append("\n");
        }
        return sb.toString();
    }
    
   
}

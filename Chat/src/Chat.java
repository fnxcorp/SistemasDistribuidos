
import java.util.ArrayList;
import java.util.HashSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Usuario
 */
public interface Chat extends java.rmi.Remote {

    public void sendMessage(String user, String message) throws java.rmi.RemoteException;

    public void saveMessage(Message msg) throws java.rmi.RemoteException;

    public String getMessages() throws java.rmi.RemoteException;

    public ArrayList<Message> getMessagesList() throws java.rmi.RemoteException;

    public HashSet<String> getUserList() throws java.rmi.RemoteException;

    public void disconnectUser(String username) throws java.rmi.RemoteException;

    public boolean addUser(String userName) throws java.rmi.RemoteException;

}

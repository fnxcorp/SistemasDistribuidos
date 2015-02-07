/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
public interface Chat extends java.rmi.Remote{
    
    public void sendMessage(String user, String message) throws java.rmi.RemoteException;
    public String getMessages() throws java.rmi.RemoteException;
    
}

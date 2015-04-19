/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hp.scheduler;

import com.hp.scheduler.process.EventLogImpl;
import com.hp.scheduler.process.RegistryManager;
import com.hp.scheduler.socket.LogicalClockImpl;
import com.hp.scheduler.socket.SocketService;
import com.hp.scheduler.socket.event.EventManager;
import com.hp.scheduler.socket.event.EventType;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the main app, that will be in charge of managing licenses, clients and so on.
 *
 * @author orozco
 */
public class Manager extends javax.swing.JFrame {

    SocketService service = null;
    EventManager eventManager = null;
    String thisIP = "";
    LogicalClockImpl logicalClock = new LogicalClockImpl();
    RegistryManager registryManager = null;
    EventLogImpl eventLog = null;

    /**
     * Creates new form Manager
     */
    public Manager() {
        initComponents();
        InetAddress iAddress;
        try {
            iAddress = InetAddress.getLocalHost();
            thisIP = iAddress.getHostAddress();
            TxtIP.setText(thisIP);

        } catch (UnknownHostException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        TxtPort = new javax.swing.JTextField();
        BtnConnect = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        TxtLog = new javax.swing.JTextArea();
        BtnClient = new javax.swing.JButton();
        TxtIP = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Optimization Manager");

        jLabel1.setText("Select conection port:");

        TxtPort.setText("8081");

        BtnConnect.setText("Connect");
        BtnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnConnectActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Client ID", "Status"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jTabbedPane1.addTab("Connected Clients", jScrollPane2);

        TxtLog.setColumns(20);
        TxtLog.setRows(5);
        jScrollPane3.setViewportView(TxtLog);

        jTabbedPane1.addTab("Event Log", jScrollPane3);

        BtnClient.setText("Use as Client");
        BtnClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnClientActionPerformed(evt);
            }
        });

        TxtIP.setText("127.0.0.1");

        jLabel2.setText("Current Connection IP:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(29, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(TxtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(BtnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(BtnClient, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(TxtIP, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 100, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(TxtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnClient, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TxtIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(19, 19, 19)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Connection");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * This method is called once the manager click on the Connect button, this initializes everything, the event
     * manager, and starts the 2 services the log service and the registry manager which is in charge of manage clients.
     *
     * @param evt the click event
     */
    private void BtnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnConnectActionPerformed
        logicalClock = new LogicalClockImpl();
        eventLog = new EventLogImpl(TxtLog, logicalClock);
        eventManager = new EventManager();
        eventManager.addEventListener(eventLog);

        logicalClock.tick();

        eventLog.logReceiveEvent("Manager", "Manager", logicalClock.getValue(), EventType.INITIALIZE, "Manager Initialization", Integer.parseInt(TxtPort.getText()), "localhost");

        service = new SocketService(TxtIP.getText(), Integer.parseInt(TxtPort.getText()), eventManager);
        service.start();

        registryManager = new RegistryManager(service, logicalClock);
        eventManager.addEventListener(registryManager);

        BtnConnect.setEnabled(false);
    }//GEN-LAST:event_BtnConnectActionPerformed

    private void BtnClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnClientActionPerformed

    }//GEN-LAST:event_BtnClientActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Manager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Manager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Manager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Manager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Manager().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnClient;
    private javax.swing.JButton BtnConnect;
    private javax.swing.JTextField TxtIP;
    private javax.swing.JTextArea TxtLog;
    private javax.swing.JTextField TxtPort;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}

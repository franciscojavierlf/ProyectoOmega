/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbomessageapp.ui;

import java.util.ArrayList;
import java.util.HashMap;
import turbomessageapp.functionality.Bloc;
import turbomessageapp.functionality.Manager;
import turbomessageapp.logic.Message;
import turbomessageapp.logic.MessageListener;
import turbomessageapp.logic.MessageRequest;
import turbomessageapp.logic.User;

/**
 *
 * @author franciscojavierlf
 */
public class Dashboard extends javax.swing.JFrame {
  
  private final Manager manager;
  private final User user;
  
  private HashMap<String, MessageRequest> directory;

  /**
   * Creates new form Dashboard
   */
  public Dashboard(User user) {
    initComponents();
    this.user = user;
    // Pone el nombre
    nameLabel.setText("Hola, " + user.name);
    // Comienza a escuchar para nuevos mensajes
    manager = new Manager(user);
    manager.addMessageListener(new DashboardMessageListener(this));
    
    // Carga todo
    loadDirectory();
    loadMessages();
  }
  
  /**
   * Agreaga un mensaje al display.
   * @param message 
   */
  public void addMessage(Message message) {
    String text = messagesArea.getText();
    text += "De " + message.sender.name + ": " + message.body + "\n";
    messagesArea.setText(text);
  }
  
  /**
   * Descarga el directorio.
   */
  private void loadDirectory() {
    directory = manager.bloc.getUserDirectory(user);
    String text = "";
    MessageRequest request;
    for (String id : directory.keySet()) {
      request = directory.get(id);
      text += request.getOpposite(user).name + " [" + (request.state == MessageRequest.State.PENDING ? "Pendiente"
              : request.state == MessageRequest.State.ACCEPTED ? "Aceptada" : "Rechazada") + "]\n";
      
    }
    requestsArea.setText(text);
  }
  
  /**
   * Descarga todos los mensajes.
   */
  private void loadMessages() {
    // Descarga los mensajes de las conversaciones
    ArrayList<Message> messages = manager.bloc.getMessages(user);
    // E imprime
    String text = "";
    for (Message m : messages) {
      if (m.type == Message.Type.STATE_UPDATE) continue;
      text += (m.type == Message.Type.TEXT ? "Mensaje de " :
              m.sender.equals(user) ? "Solicitud para " : "Solicitud de ")
            + m.sender.name + ": " + m.body + " ["
        + (m.state == Message.State.DELIVERED ? "D" : m.state == Message.State.READ ? "R" : "S") + "]\n";
    }
    messagesArea.setText(text);
    
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    nameLabel = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    messagesArea = new javax.swing.JTextArea();
    jLabel1 = new javax.swing.JLabel();
    jScrollPane2 = new javax.swing.JScrollPane();
    messageField = new javax.swing.JTextArea();
    userMessageField = new javax.swing.JTextField();
    jButton1 = new javax.swing.JButton();
    jScrollPane3 = new javax.swing.JScrollPane();
    requestsArea = new javax.swing.JTextArea();
    jLabel2 = new javax.swing.JLabel();
    jButton2 = new javax.swing.JButton();
    jButton3 = new javax.swing.JButton();
    jButton4 = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    nameLabel.setFont(new java.awt.Font("Open Sans", 0, 48)); // NOI18N
    nameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    nameLabel.setText("jLabel1");

    messagesArea.setEditable(false);
    messagesArea.setColumns(20);
    messagesArea.setRows(5);
    jScrollPane1.setViewportView(messagesArea);

    jLabel1.setText("Mensajes");

    messageField.setColumns(20);
    messageField.setRows(5);
    jScrollPane2.setViewportView(messageField);

    jButton1.setText("Enviar");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    requestsArea.setEditable(false);
    requestsArea.setColumns(20);
    requestsArea.setRows(5);
    jScrollPane3.setViewportView(requestsArea);

    jLabel2.setText("Solicitudes");

    jButton2.setText("Pedir");
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton2ActionPerformed(evt);
      }
    });

    jButton3.setText("Aceptar");
    jButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton3ActionPerformed(evt);
      }
    });

    jButton4.setText("Cancelar");
    jButton4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton4ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(31, 31, 31)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane2)
          .addGroup(layout.createSequentialGroup()
            .addComponent(userMessageField, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton2)
            .addGap(18, 18, 18)
            .addComponent(jButton3)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jButton4)
            .addGap(0, 25, Short.MAX_VALUE))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(30, 30, 30)))
        .addContainerGap())
      .addGroup(layout.createSequentialGroup()
        .addGap(234, 234, 234)
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(jLabel2)
        .addGap(267, 267, 267))
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(401, 401, 401))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGap(31, 31, 31)
        .addComponent(nameLabel)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(jLabel2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addGroup(layout.createSequentialGroup()
            .addComponent(jScrollPane3)
            .addGap(50, 50, 50)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(userMessageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jButton1)
              .addComponent(jButton2)
              .addComponent(jButton3)
              .addComponent(jButton4))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(33, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    // Manda mensaje
    manager.sendTextMessage(user, userMessageField.getText(), messageField.getText());
  }//GEN-LAST:event_jButton1ActionPerformed

  private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    // Pide solicitud
    manager.sendRequest(user, userMessageField.getText());
  }//GEN-LAST:event_jButton2ActionPerformed

  private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    // Acepta una solicitud
    manager.acceptRequest(userMessageField.getText(), user.id);
  }//GEN-LAST:event_jButton3ActionPerformed

  private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    // Rechaza solicitud
    manager.denyRequest(userMessageField.getText(), user.id);
  }//GEN-LAST:event_jButton4ActionPerformed


  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButton1;
  private javax.swing.JButton jButton2;
  private javax.swing.JButton jButton3;
  private javax.swing.JButton jButton4;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JScrollPane jScrollPane3;
  private javax.swing.JTextArea messageField;
  private javax.swing.JTextArea messagesArea;
  private javax.swing.JLabel nameLabel;
  private javax.swing.JTextArea requestsArea;
  private javax.swing.JTextField userMessageField;
  // End of variables declaration//GEN-END:variables

  /**
   * Acciones al detectar nuevos mensajes.
   */
  private class DashboardMessageListener implements MessageListener {

    private final Dashboard dashboard;
    
    public DashboardMessageListener(Dashboard dashboard) {
      this.dashboard = dashboard;
    }
    
    @Override
    public void onTextReceived(Message message) {
      dashboard.addMessage(message);
    }

    @Override
    public void onRequestReceived(Message message) {
    }

    @Override
    public void onRequestAccepted(Message message) {
    }

    @Override
    public void onRequestDenied(Message message) {
    }

    @Override
    public void onStateUpdated(Message message) {
    }
    
  }
}

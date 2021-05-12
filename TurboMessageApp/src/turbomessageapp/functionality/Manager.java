/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package turbomessageapp.functionality;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import turbomessageapp.logic.Message;
import turbomessageapp.logic.MessageListener;
import turbomessageapp.logic.MessageRequest;
import turbomessageapp.logic.User;

/**
 * Administrador de mensajes.
 * @author franciscojavierlf
 */
public final class Manager {
  
  public final Bloc bloc;
  public final Transmitter transmitter;
  
  public Manager(User user) {
    bloc = new Bloc();
    transmitter = new Transmitter(user);
    
    // Agrega un listener para reaccionar a nuevos mensajes en la base de datos
    transmitter.addListener(new ManagerMessagesListener(this));
    // Y comienza a escuchar
    transmitter.start();
    // Y se conecta a la bd
    bloc.connect();
  }
  
  public void addMessageListener(MessageListener listener) {
    transmitter.addListener(listener);
  }
  
  /**
   * Manda una solicitud a otro usuario.
   * @param senderId
   * @param receiverId
   * @param body
   * @return 
   */
  public Message sendRequest(User sender, String receiverId) {
    // Crea un request y lo agrega a la bd
    Message request = bloc.addRequest(sender, receiverId);
    // Y se transmite el mensaje para el usuario
    transmitter.sendMessage(request);
    return request;
  }
  
  /**
   * Acepta una solicitud.
   * @param sender
   * @param receiverId
   * @return 
   */
  public Message acceptRequest(String senderId, String receiverId) {
    if (bloc.requestExists(senderId, receiverId)) {
      Message message = bloc.addAcceptedRequest(senderId, receiverId);
      transmitter.sendMessage(message);
      return message;
    }
    return null;
  }
  
  /**
   * Rechaza una solicitud.
   * @param sender
   * @param receiverId
   * @return 
   */
  public Message denyRequest(String senderId, String receiverId) {
    if (bloc.requestExists(senderId, receiverId)) {
      Message message = bloc.addDeniedRequest(senderId, receiverId);
      transmitter.sendMessage(message);
      return message;
    }
    return null;
  }
  
  /**
   * Manda un mensaje de texto.
   * @param sender
   * @param receiverId
   * @param body
   * @return 
   */
  public Message sendTextMessage(User sender, String receiverId, String body) {
    Message message = bloc.addTextMessage(sender, receiverId, body);
    transmitter.sendMessage(message);
    return message;
  }
  
   /**
    * Actualiza el estado de un mensaje.
    * @param message
    * @param newState
    * @return 
    */
  public Message updateMessageState(Message message, Message.State newState) {
    // Lo actualiza en la base de datos y luego avisa al usuario
    Message update = bloc.updateMessageState(message, newState);
    transmitter.sendMessage(update);
    return update;
  }
  
  /**
   * Clase para movimientos en la base de datos al recibir mensajes.
   */
  private class ManagerMessagesListener implements MessageListener {
    
    private final Manager manager;
    
    public ManagerMessagesListener(Manager manager) {
      this.manager = manager;
    }

    @Override
    public void onTextReceived(Message message) {
      manager.updateMessageState(message, Message.State.DELIVERED);
    }

    @Override
    public void onRequestReceived(Message message) {
      manager.updateMessageState(message, Message.State.DELIVERED);
    }

    @Override
    public void onRequestAccepted(Message message) {
      manager.updateMessageState(message, Message.State.DELIVERED);
    }

    @Override
    public void onRequestDenied(Message message) {
      manager.updateMessageState(message, Message.State.DELIVERED);
    }

    @Override
    public void onStateUpdated(Message message) {
    }
    
  }
}
